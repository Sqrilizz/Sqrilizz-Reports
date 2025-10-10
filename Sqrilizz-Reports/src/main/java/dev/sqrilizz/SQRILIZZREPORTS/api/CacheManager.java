package dev.sqrilizz.SQRILIZZREPORTS.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.sqrilizz.SQRILIZZREPORTS.Main;
import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheManager {
    private static Cache<String, List<ReportManager.Report>> reportsCache;

    /**
     * Оптимизированная инициализация с конфигурируемыми параметрами
     */
    public static void initialize() {
        var cfg = Main.getInstance().getConfig();
        int expireSeconds = cfg.getInt("performance.cache.expire-after-write", 30);
        int maxSize = cfg.getInt("performance.cache.maximum-size", 10000);
        boolean recordStats = cfg.getBoolean("performance.cache.record-stats", false);
        
        var builder = Caffeine.newBuilder()
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize);
                
        if (recordStats) {
            builder.recordStats();
        }
        
        reportsCache = builder.build();
        
        Main.getInstance().getLogger().info("Cache initialized: expire=" + expireSeconds + "s, maxSize=" + maxSize + ", stats=" + recordStats);
    }

    public static List<ReportManager.Report> getReportsCached(String player) {
        return reportsCache.get(player, k -> ReportManager.getPlayerReports(k));
    }

    public static void invalidate(String player) {
        if (reportsCache != null) reportsCache.invalidate(player);
    }

    public static void invalidateAll() {
        if (reportsCache != null) reportsCache.invalidateAll();
    }
    
    /**
     * Получить статистику кеша для мониторинга производительности
     */
    public static String getCacheStats() {
        if (reportsCache == null) return "Cache not initialized";
        var stats = reportsCache.stats();
        return String.format("Cache Stats: hits=%d, misses=%d, hitRate=%.2f%%, size=%d", 
                stats.hitCount(), stats.missCount(), stats.hitRate() * 100, reportsCache.estimatedSize());
    }
    
    /**
     * Принудительная очистка устаревших записей
     */
    public static void cleanUp() {
        if (reportsCache != null) reportsCache.cleanUp();
    }
}
