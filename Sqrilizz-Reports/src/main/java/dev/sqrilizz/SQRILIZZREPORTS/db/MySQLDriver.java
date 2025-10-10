package dev.sqrilizz.SQRILIZZREPORTS.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.sqrilizz.SQRILIZZREPORTS.Main;
import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;
import java.sql.*;
import java.util.*;

class MySQLDriver implements DatabaseManager.Driver {
    private HikariDataSource ds;

    @Override
    public void init() {
        try {
            var cfg = Main.getInstance().getConfig();
            String host = cfg.getString("database.mysql.host", "localhost");
            int port = cfg.getInt("database.mysql.port", 3306);
            String db = cfg.getString("database.mysql.database", "sqrilizz_reports");
            String user = cfg.getString("database.mysql.user", "root");
            String pass = cfg.getString("database.mysql.password", "");
            String params = cfg.getString("database.mysql.params", "useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&serverTimezone=UTC");
            String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?%s", host, port, db, params);

            HikariConfig hc = new HikariConfig();
            hc.setJdbcUrl(jdbcUrl);
            hc.setUsername(user);
            hc.setPassword(pass);
            hc.setMaximumPoolSize(Math.max(5, cfg.getInt("database.mysql.pool-size", 10)));
            hc.setPoolName("SQRILIZZ-MySQL");

            ds = new HikariDataSource(hc);
            createSchema();
        } catch (Exception e) {
            Main.getInstance().getLogger().severe("MySQL init failed: " + e.getMessage());
        }
    }

    private void createSchema() throws SQLException {
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS reports (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "reporter VARCHAR(64), target VARCHAR(64), reason TEXT, ts BIGINT, status VARCHAR(16), " +
                    "reporter_loc VARCHAR(128), target_loc VARCHAR(128), is_anon TINYINT(1), resolver VARCHAR(64), resolved_at BIGINT) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS replies (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, report_id BIGINT, author VARCHAR(64), message TEXT, ts BIGINT, INDEX(report_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
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
            Main.getInstance().getLogger().warning("MySQL saveReport failed: " + e.getMessage());
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
            Main.getInstance().getLogger().warning("MySQL resolveReport failed: " + e.getMessage());
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
            Main.getInstance().getLogger().warning("MySQL addReply failed: " + e.getMessage());
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
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM replies ORDER BY id ASC"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long reportId = rs.getLong("report_id");
                    ReportManager.Report rpt = findById(map, reportId);
                    if (rpt != null) {
                        rpt.replies.add(new ReportManager.Reply(rs.getLong("id"), reportId, rs.getString("author"), rs.getString("message"), rs.getLong("ts")));
                    }
                }
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("MySQL loadReports failed: " + e.getMessage());
        }
        return map;
    }

    private ReportManager.Report findById(Map<String, List<ReportManager.Report>> map, long id) {
        for (var entry : map.entrySet()) {
            for (var r : entry.getValue()) if (r.id == id) return r;
        }
        return null;
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
                    for (var r : list) byId.put(r.id, r);
                    while (rs.next()) {
                        long rid = rs.getLong("report_id");
                        ReportManager.Report r = byId.get(rid);
                        if (r != null) r.replies.add(new ReportManager.Reply(rs.getLong("id"), rid, rs.getString("author"), rs.getString("message"), rs.getLong("ts")));
                    }
                }
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("MySQL getReportsByPlayer failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void replaceAllReports(Map<String, List<ReportManager.Report>> reports) {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            try (Statement st = c.createStatement()) {
                st.executeUpdate("DELETE FROM replies");
                st.executeUpdate("DELETE FROM reports");
            }
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
            Main.getInstance().getLogger().warning("MySQL replaceAllReports failed: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (ds != null) {
            try {
                ds.close();
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("MySQL close failed: " + e.getMessage());
            }
        }
    }
}
