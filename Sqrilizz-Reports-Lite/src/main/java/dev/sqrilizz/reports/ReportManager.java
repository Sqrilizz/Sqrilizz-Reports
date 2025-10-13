package dev.sqrilizz.reports;

import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;

/**
 * Simple and efficient report management
 */
public class ReportManager {
    
    private final Main plugin;
    private final DatabaseManager database;
    private final CacheManager cache;
    
    public ReportManager(Main plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabaseManager();
        this.cache = plugin.getCache();
    }
    
    /**
     * Create a new report
     */
    public boolean createReport(Player reporter, String targetName, String reason) {
        try {
            // Create report object
            Report report = new Report(
                System.currentTimeMillis(),
                reporter.getName(),
                reporter.getUniqueId(),
                targetName,
                reason,
                reporter.getLocation()
            );
            
            // Save to database
            long reportId = database.saveReport(report);
            if (reportId <= 0) return false;
            
            report.setId(reportId);
            
            // Update cache
            cache.addReport(targetName, report);
            
            // Send notifications asynchronously
            plugin.runAsync(() -> sendNotifications(report));
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to create report: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get reports for a player
     */
    public List<Report> getReports(String playerName) {
        // Try cache first
        List<Report> cached = cache.getReports(playerName);
        if (cached != null) return cached;
        
        // Load from database
        List<Report> reports = database.getReports(playerName);
        cache.cacheReports(playerName, reports);
        
        return reports;
    }
    
    /**
     * Resolve a report
     */
    public boolean resolveReport(long reportId, String resolver) {
        try {
            // Update database
            if (!database.resolveReport(reportId, resolver)) return false;
            
            // Clear cache
            cache.invalidateAll();
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to resolve report: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a report
     */
    public boolean deleteReport(long reportId) {
        try {
            // Delete from database
            if (!database.deleteReport(reportId)) return false;
            
            // Clear cache
            cache.invalidateAll();
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to delete report: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Send notifications for new report
     */
    private void sendNotifications(Report report) {
        try {
            // Telegram notification
            if (plugin.getTelegram() != null) {
                plugin.getTelegram().sendReport(report);
            }
            
            // Webhook notification
            if (plugin.getWebhook() != null) {
                plugin.getWebhook().sendReport(report);
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send notifications: " + e.getMessage());
        }
    }
    
    /**
     * Simple Report class
     */
    public static class Report {
        private long id;
        private final long timestamp;
        private final String reporterName;
        private final UUID reporterUuid;
        private final String targetName;
        private final String reason;
        private final String location;
        private boolean resolved;
        private String resolver;
        
        public Report(long timestamp, String reporterName, UUID reporterUuid, 
                     String targetName, String reason, org.bukkit.Location loc) {
            this.timestamp = timestamp;
            this.reporterName = reporterName;
            this.reporterUuid = reporterUuid;
            this.targetName = targetName;
            this.reason = reason;
            this.location = String.format("%.1f, %.1f, %.1f", loc.getX(), loc.getY(), loc.getZ());
            this.resolved = false;
        }
        
        // Getters and setters
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public long getTimestamp() { return timestamp; }
        public String getReporterName() { return reporterName; }
        public UUID getReporterUuid() { return reporterUuid; }
        public String getTargetName() { return targetName; }
        public String getReason() { return reason; }
        public String getLocation() { return location; }
        public boolean isResolved() { return resolved; }
        public void setResolved(boolean resolved) { this.resolved = resolved; }
        public String getResolver() { return resolver; }
        public void setResolver(String resolver) { this.resolver = resolver; }
    }
}
