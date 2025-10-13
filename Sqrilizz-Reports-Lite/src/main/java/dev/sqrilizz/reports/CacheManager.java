package dev.sqrilizz.reports;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple in-memory cache for reports
 */
public class CacheManager {
    
    private final ConcurrentHashMap<String, CachedReports> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    public void initialize() {
        // Clean expired entries every 5 minutes
        scheduler.scheduleAtFixedRate(this::cleanExpired, 5, 5, TimeUnit.MINUTES);
    }
    
    /**
     * Get cached reports for a player
     */
    public List<ReportManager.Report> getReports(String playerName) {
        CachedReports cached = cache.get(playerName.toLowerCase());
        
        if (cached != null && !cached.isExpired()) {
            return cached.reports;
        }
        
        // Remove expired entry
        if (cached != null) {
            cache.remove(playerName.toLowerCase());
        }
        
        return null;
    }
    
    /**
     * Cache reports for a player
     */
    public void cacheReports(String playerName, List<ReportManager.Report> reports) {
        cache.put(playerName.toLowerCase(), new CachedReports(reports));
    }
    
    /**
     * Add a new report to cache
     */
    public void addReport(String targetName, ReportManager.Report report) {
        CachedReports cached = cache.get(targetName.toLowerCase());
        if (cached != null && !cached.isExpired()) {
            cached.reports.add(0, report); // Add to beginning
        }
    }
    
    /**
     * Invalidate all cache entries
     */
    public void invalidateAll() {
        cache.clear();
    }
    
    /**
     * Clean expired entries
     */
    private void cleanExpired() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Cleanup on shutdown
     */
    public void cleanup() {
        scheduler.shutdown();
        cache.clear();
    }
    
    /**
     * Cached reports with expiration
     */
    private static class CachedReports {
        final List<ReportManager.Report> reports;
        final long expireTime;
        
        CachedReports(List<ReportManager.Report> reports) {
            this.reports = reports;
            this.expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10); // 10 min cache
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
}
