package dev.sqrilizz.reports;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Simple SQLite database manager
 */
public class DatabaseManager {
    
    private final Main plugin;
    private Connection connection;
    
    public DatabaseManager(Main plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() throws SQLException {
        // Create database file
        File dbFile = new File(plugin.getDataFolder(), "reports.db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        
        // Connect
        connection = DriverManager.getConnection(url);
        
        // Create table
        createTable();
        
        plugin.getLogger().info("Database initialized successfully");
    }
    
    private void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS reports (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp BIGINT NOT NULL,
                reporter_name TEXT NOT NULL,
                reporter_uuid TEXT NOT NULL,
                target_name TEXT NOT NULL,
                reason TEXT NOT NULL,
                location TEXT NOT NULL,
                resolved BOOLEAN DEFAULT FALSE,
                resolver TEXT,
                resolved_at BIGINT
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    /**
     * Save a new report
     */
    public long saveReport(ReportManager.Report report) {
        String sql = """
            INSERT INTO reports (timestamp, reporter_name, reporter_uuid, target_name, reason, location)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, report.getTimestamp());
            stmt.setString(2, report.getReporterName());
            stmt.setString(3, report.getReporterUuid().toString());
            stmt.setString(4, report.getTargetName());
            stmt.setString(5, report.getReason());
            stmt.setString(6, report.getLocation());
            
            stmt.executeUpdate();
            
            // Get generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save report: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Get reports for a player
     */
    public List<ReportManager.Report> getReports(String targetName) {
        List<ReportManager.Report> reports = new ArrayList<>();
        
        String sql = "SELECT * FROM reports WHERE target_name = ? ORDER BY timestamp DESC LIMIT 50";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, targetName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportManager.Report report = createReportFromResultSet(rs);
                    reports.add(report);
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get reports: " + e.getMessage());
        }
        
        return reports;
    }
    
    /**
     * Resolve a report
     */
    public boolean resolveReport(long reportId, String resolver) {
        String sql = "UPDATE reports SET resolved = TRUE, resolver = ?, resolved_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, resolver);
            stmt.setLong(2, System.currentTimeMillis());
            stmt.setLong(3, reportId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to resolve report: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a report
     */
    public boolean deleteReport(long reportId) {
        String sql = "DELETE FROM reports WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reportId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to delete report: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Create Report object from ResultSet
     */
    private ReportManager.Report createReportFromResultSet(ResultSet rs) throws SQLException {
        // Create a dummy location for the constructor
        org.bukkit.Location dummyLoc = new org.bukkit.Location(null, 0, 0, 0);
        
        ReportManager.Report report = new ReportManager.Report(
            rs.getLong("timestamp"),
            rs.getString("reporter_name"),
            UUID.fromString(rs.getString("reporter_uuid")),
            rs.getString("target_name"),
            rs.getString("reason"),
            dummyLoc
        );
        
        report.setId(rs.getLong("id"));
        report.setResolved(rs.getBoolean("resolved"));
        report.setResolver(rs.getString("resolver"));
        
        return report;
    }
    
    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to close database: " + e.getMessage());
        }
    }
}
