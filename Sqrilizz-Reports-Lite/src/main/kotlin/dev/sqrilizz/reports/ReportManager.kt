package dev.sqrilizz.reports

import org.bukkit.entity.Player
import java.util.UUID

class ReportManager(private val plugin: Main) {

    private val database: DatabaseManager? = plugin.getDatabaseManager()
    private val cache: CacheManager? = plugin.getCache()

    fun createReport(reporter: Player, targetName: String, reason: String): Boolean {
        return try {
            val report = Report(
                timestamp = System.currentTimeMillis(),
                reporterName = reporter.name,
                reporterUuid = reporter.uniqueId,
                targetName = targetName,
                reason = reason,
                location = "%.1f, %.1f, %.1f".format(
                    reporter.location.x, reporter.location.y, reporter.location.z
                )
            )

            val reportId = database?.saveReport(report) ?: -1
            if (reportId <= 0) return false

            report.id = reportId
            cache?.addReport(targetName, report)

            plugin.runAsync(Runnable { sendNotifications(report) })
            true
        } catch (e: Exception) {
            plugin.logger.severe("Failed to create report: ${e.message}")
            false
        }
    }

    fun getReports(playerName: String): List<Report> {
        val cached = cache?.getReports(playerName)
        if (cached != null) return cached

        val reports = database?.getReports(playerName) ?: emptyList()
        cache?.cacheReports(playerName, reports)
        return reports
    }

    fun resolveReport(reportId: Long, resolver: String): Boolean {
        return try {
            if (database?.resolveReport(reportId, resolver) != true) return false
            cache?.invalidateAll()
            true
        } catch (e: Exception) {
            plugin.logger.severe("Failed to resolve report: ${e.message}")
            false
        }
    }

    fun deleteReport(reportId: Long): Boolean {
        return try {
            if (database?.deleteReport(reportId) != true) return false
            cache?.invalidateAll()
            true
        } catch (e: Exception) {
            plugin.logger.severe("Failed to delete report: ${e.message}")
            false
        }
    }

    private fun sendNotifications(report: Report) {
        try {
            plugin.getTelegram()?.sendReport(report)
            plugin.getWebhook()?.sendReport(report)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to send notifications: ${e.message}")
        }
    }

    class Report(
        val timestamp: Long,
        val reporterName: String,
        val reporterUuid: UUID,
        val targetName: String,
        val reason: String,
        val location: String
    ) {
        var id: Long = 0
        var isResolved: Boolean = false
        var resolver: String? = null
    }
}
