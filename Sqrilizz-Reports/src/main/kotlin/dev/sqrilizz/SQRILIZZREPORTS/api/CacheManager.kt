package dev.sqrilizz.SQRILIZZREPORTS.api

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import dev.sqrilizz.SQRILIZZREPORTS.Main
import dev.sqrilizz.SQRILIZZREPORTS.ReportManager
import java.util.concurrent.TimeUnit

object CacheManager {

    private var reportsCache: Cache<String, List<ReportManager.Report>>? = null

    @JvmStatic
    fun initialize() {
        val cfg = Main.getInstance().config
        val expireSeconds = cfg.getInt("performance.cache.expire-after-write", 30)
        val maxSize = cfg.getInt("performance.cache.maximum-size", 10000).toLong()
        val recordStats = cfg.getBoolean("performance.cache.record-stats", false)

        val builder = Caffeine.newBuilder()
            .expireAfterWrite(expireSeconds.toLong(), TimeUnit.SECONDS)
            .maximumSize(maxSize)

        if (recordStats) {
            builder.recordStats()
        }

        reportsCache = builder.build()

        Main.getInstance().logger.info(
            "Cache initialized: expire=${expireSeconds}s, maxSize=$maxSize, stats=$recordStats")
    }

    @JvmStatic
    fun getReportsCached(player: String): List<ReportManager.Report>? =
        reportsCache?.get(player) { ReportManager.getPlayerReports(it) }

    @JvmStatic
    fun invalidate(player: String) {
        reportsCache?.invalidate(player)
    }

    @JvmStatic
    fun invalidateAll() {
        reportsCache?.invalidateAll()
    }

    @JvmStatic
    fun getCacheStats(): String {
        val cache = reportsCache ?: return "Cache not initialized"
        val stats = cache.stats()
        return String.format(
            "Cache Stats: hits=%d, misses=%d, hitRate=%.2f%%, size=%d",
            stats.hitCount(), stats.missCount(), stats.hitRate() * 100, cache.estimatedSize()
        )
    }

    @JvmStatic
    fun cleanUp() {
        reportsCache?.cleanUp()
    }
}
