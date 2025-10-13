package dev.sqrilizz.SQRILIZZREPORTS.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.sqrilizz.SQRILIZZREPORTS.Main;
import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;

import javax.sql.DataSource;
import java.io.File;
import java.sql.*;
import java.util.*;

class SQLiteDriver implements DatabaseManager.Driver {
    private HikariDataSource ds;

    @Override
    public void init() {
        try {
            File dataFolder = Main.getInstance().getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdirs();
            String dbPath = new File(dataFolder, "reports.db").getAbsolutePath();
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl("jdbc:sqlite:" + dbPath);
            cfg.setMaximumPoolSize(5);
            cfg.setPoolName("SQRILIZZ-SQLite");
            ds = new HikariDataSource(cfg);
            createSchema();
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("SQLite init failed: " + e.getMessage());
        }
    }

    private void createSchema() throws SQLException {
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS reports (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "reporter TEXT, target TEXT, reason TEXT, ts BIGINT, status TEXT, " +
                    "reporter_loc TEXT, target_loc TEXT, is_anon INTEGER, resolver TEXT, resolved_at BIGINT)"
            );
            st.executeUpdate("CREATE TABLE IF NOT EXISTS replies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, report_id INTEGER, author TEXT, message TEXT, ts BIGINT)"
            );
        }
    }

    @Override
    public long saveReport(ReportManager.Report r) {
        String sql = "INSERT INTO reports(reporter,target,reason,ts,status,reporter_loc,target_loc,is_anon) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.reporter);
            ps.setString(2, r.target);
            ps.setString(3, r.reason);
            ps.setLong(4, r.timestamp);
            ps.setString(5, r.status);
            ps.setString(6, r.reporterLocation);
            ps.setString(7, r.targetLocation);
            ps.setInt(8, r.isAnonymous ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("SQLite saveReport failed: " + e.getMessage());
        }
        return 0L;
    }

    @Override
    public boolean resolveReport(long id, String resolver) {
        String sql = "UPDATE reports SET status='resolved', resolver=?, resolved_at=? WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, resolver);
            ps.setLong(2, System.currentTimeMillis());
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("SQLite resolveReport failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addReply(long reportId, String author, String message, long ts) {
        String sql = "INSERT INTO replies(report_id,author,message,ts) VALUES(?,?,?,?)";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, reportId);
            ps.setString(2, author);
            ps.setString(3, message);
            ps.setLong(4, ts);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("SQLite addReply failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, List<ReportManager.Report>> loadReports() {
        Map<String, List<ReportManager.Report>> map = new HashMap<>();
        try (Connection c = ds.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM reports ORDER BY id DESC"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportManager.Report r = new ReportManager.Report(
                            rs.getLong("id"),
                            rs.getString("reporter"),
                            rs.getString("target"),
                            rs.getString("reason"),
                            rs.getLong("ts"),
                            rs.getString("reporter_loc"),
                            rs.getString("target_loc"),
                            rs.getInt("is_anon") == 1,
                            rs.getString("status")
                    );
                    map.computeIfAbsent(r.target, k -> new ArrayList<>()).add(r);
                }
            }
            // load replies
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM replies ORDER BY id ASC"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long reportId = rs.getLong("report_id");
                    ReportManager.Report report = findById(map, reportId);
                    if (report != null) {
                        report.replies.add(new ReportManager.Reply(rs.getLong("id"), reportId, rs.getString("author"), rs.getString("message"), rs.getLong("ts")));
                    }
                }
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("SQLite loadReports failed: " + e.getMessage());
        }
        return map;
    }

    @Override
    public List<ReportManager.Report> getReportsByPlayer(String player) {
        List<ReportManager.Report> list = new ArrayList<>();
        try (Connection c = ds.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM reports WHERE target=? ORDER BY id DESC")) {
                ps.setString(1, player);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new ReportManager.Report(
                                rs.getLong("id"),
                                rs.getString("reporter"),
                                rs.getString("target"),
                                rs.getString("reason"),
                                rs.getLong("ts"),
                                rs.getString("reporter_loc"),
                                rs.getString("target_loc"),
                                rs.getInt("is_anon") == 1,
                                rs.getString("status")
                        ));
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM replies WHERE report_id IN (SELECT id FROM reports WHERE target=?) ORDER BY id ASC")) {
                ps.setString(1, player);
                try (ResultSet rs = ps.executeQuery()) {
                    Map<Long, ReportManager.Report> byId = new HashMap<>();
                    for (ReportManager.Report r : list) byId.put(r.id, r);
                    while (rs.next()) {
                        long rid = rs.getLong("report_id");
                        ReportManager.Report r = byId.get(rid);
                        if (r != null) r.replies.add(new ReportManager.Reply(rs.getLong("id"), rid, rs.getString("author"), rs.getString("message"), rs.getLong("ts")));
                    }
                }
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("SQLite getReportsByPlayer failed: " + e.getMessage());
        }
        return list;
    }

    private ReportManager.Report findById(Map<String, List<ReportManager.Report>> map, long id) {
        for (var entry : map.entrySet()) {
            for (var r : entry.getValue()) if (r.id == id) return r;
        }
        return null;
    }

    @Override
    public void replaceAllReports(Map<String, List<ReportManager.Report>> reports) {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            try (Statement st = c.createStatement()) {
                st.executeUpdate("DELETE FROM replies");
                st.executeUpdate("DELETE FROM reports");
            }
            // Insert reports
            String insReport = "INSERT INTO reports(id,reporter,target,reason,ts,status,reporter_loc,target_loc,is_anon) VALUES(?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(insReport)) {
                for (var entry : reports.entrySet()) {
                    for (var r : entry.getValue()) {
                        ps.setLong(1, r.id);
                        ps.setString(2, r.reporter);
                        ps.setString(3, r.target);
                        ps.setString(4, r.reason);
                        ps.setLong(5, r.timestamp);
                        ps.setString(6, r.status);
                        ps.setString(7, r.reporterLocation);
                        ps.setString(8, r.targetLocation);
                        ps.setInt(9, r.isAnonymous ? 1 : 0);
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }
            // Insert replies
            String insReply = "INSERT INTO replies(id,report_id,author,message,ts) VALUES(?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(insReply)) {
                for (var entry : reports.entrySet()) {
                    for (var r : entry.getValue()) {
                        for (var rep : r.replies) {
                            ps.setLong(1, rep.id);
                            ps.setLong(2, rep.reportId);
                            ps.setString(3, rep.author);
                            ps.setString(4, rep.message);
                            ps.setLong(5, rep.timestamp);
                            ps.addBatch();
                        }
                    }
                }
                ps.executeBatch();
            }
            c.commit();
            c.setAutoCommit(true);
        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("SQLite replaceAllReports failed: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (ds != null) {
            try {
                ds.close();
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("SQLite close failed: " + e.getMessage());
            }
        }
    }
}
