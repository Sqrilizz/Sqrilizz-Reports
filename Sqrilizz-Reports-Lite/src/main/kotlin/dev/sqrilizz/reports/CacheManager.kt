package dev.sqrilizz.reports

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class CacheManager {

    private val cache = ConcurrentHashMap<String, CachedReports>()
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "Reports-Cache-Cleanup").apply { isDaemon = true }
    }

    fun initialize() {
        scheduler.scheduleAtFixedRate(::cleanExpired, 5, 5, TimeUnit.MINUTES)
    }

    fun getReports(playerName: String): List<ReportManager.Report>? {
        val cached = cache[playerName.lowercase()]
        if (cached != null && !cached.isExpired) return cached.reports
        if (cached != null) cache.remove(playerName.lowercase())
        return null
    }

    fun cacheReports(playerName: String, reports: List<ReportManager.Report>) {
        cache[playerName.lowercase()] = CachedReports(ArrayList(reports))
    }

    fun addReport(targetName: String, report: ReportManager.Report) {
        val cached = cache[targetName.lowercase()]
        if (cached != null && !cached.isExpired) {
            cached.reports.add(0, report)
        }
    }

    fun invalidateAll() {
        cache.clear()
    }

    private fun cleanExpired() {
        cache.entries.removeIf { it.value.isExpired }
    }

    fun cleanup() {
        scheduler.shutdown()
        cache.clear()
    }

    private class CachedReports(val reports: MutableList<ReportManager.Report>) {
        private val expireTime: Long = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10)
        val isExpired: Boolean get() = System.currentTimeMillis() > expireTime
    }
}
