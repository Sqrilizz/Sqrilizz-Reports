package dev.sqrilizz.SQRILIZZREPORTS.api

import dev.sqrilizz.SQRILIZZREPORTS.AntiAbuseManager
import dev.sqrilizz.SQRILIZZREPORTS.ReportManager
import dev.sqrilizz.SQRILIZZREPORTS.VersionUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.function.Consumer

object ReportAPI {

    private val reportListeners = mutableListOf<Consumer<ReportEvent>>()
    private val resolveListeners = mutableListOf<Consumer<ReportManager.Report>>()
    private val deleteListeners = mutableListOf<Consumer<ReportManager.Report>>()
    private val replyListeners = mutableListOf<Consumer<ReportManager.Reply>>()

    @JvmStatic
    fun createReport(reporter: Player?, target: Player?, reason: String?): Boolean {
        if (reporter == null || target == null || reason.isNullOrBlank()) return false

        val targetName = VersionUtils.getPlayerCleanName(target)
        if (!AntiAbuseManager.canReport(reporter, targetName)) return false

        ReportManager.addReport(reporter, target, reason)
        AntiAbuseManager.recordReport(reporter, targetName)

        val event = ReportEvent(reporter, target, reason, System.currentTimeMillis())
        reportListeners.forEach { listener ->
            try { listener.accept(event) } catch (_: Exception) { }
        }

        return true
    }

    @JvmStatic
    fun createSystemReport(systemName: String?, target: Player?, reason: String?): Boolean {
        if (systemName == null || target == null || reason.isNullOrBlank()) return false

        val systemReporter = Bukkit.getPlayer("SYSTEM_$systemName")
        if (systemReporter == null) {
            val targetName = VersionUtils.getPlayerCleanName(target)
            val report = ReportManager.Report(
                "SYSTEM_$systemName", targetName, reason,
                System.currentTimeMillis(), "System",
                getPlayerLocation(target), false
            )

            ReportManager.addSystemReport(report)

            val event = ReportEvent(null, target, reason, System.currentTimeMillis(), systemName)
            reportListeners.forEach { listener ->
                try { listener.accept(event) } catch (_: Exception) { }
            }

            return true
        }

        return createReport(systemReporter, target, reason)
    }

    @JvmStatic
    fun getReports(target: Player?): List<ReportManager.Report> {
        if (target == null) return emptyList()
        return ReportManager.getPlayerReports(VersionUtils.getPlayerCleanName(target))
    }

    @JvmStatic
    fun getReports(targetName: String?): List<ReportManager.Report> {
        if (targetName.isNullOrBlank()) return emptyList()
        return ReportManager.getPlayerReports(targetName)
    }

    @JvmStatic
    fun clearReports(target: Player?) {
        if (target == null) return
        ReportManager.clearReports(VersionUtils.getPlayerCleanName(target))
    }

    @JvmStatic
    fun clearReports(targetName: String?) {
        if (targetName.isNullOrBlank()) return
        ReportManager.clearReports(targetName)
    }

    @JvmStatic
    fun getReportCount(target: Player?): Int {
        if (target == null) return 0
        return ReportManager.getReportCount(VersionUtils.getPlayerCleanName(target))
    }

    @JvmStatic
    fun getReportCount(targetName: String?): Int {
        if (targetName.isNullOrBlank()) return 0
        return ReportManager.getReportCount(targetName)
    }

    @JvmStatic
    fun onReportCreate(listener: Consumer<ReportEvent>?) {
        if (listener != null) reportListeners.add(listener)
    }

    @JvmStatic
    fun onReportResolve(listener: Consumer<ReportManager.Report>?) {
        if (listener != null) resolveListeners.add(listener)
    }

    @JvmStatic
    fun onReportReply(listener: Consumer<ReportManager.Reply>?) {
        if (listener != null) replyListeners.add(listener)
    }

    @JvmStatic
    fun onReportDelete(listener: Consumer<ReportManager.Report>?) {
        if (listener != null) deleteListeners.add(listener)
    }

    @JvmStatic
    fun notifyResolved(report: ReportManager.Report) {
        resolveListeners.forEach { c ->
            try { c.accept(report) } catch (_: Exception) { }
        }
    }

    @JvmStatic
    fun notifyReplied(reply: ReportManager.Reply) {
        replyListeners.forEach { c ->
            try { c.accept(reply) } catch (_: Exception) { }
        }
    }

    @JvmStatic
    fun notifyDeleted(report: ReportManager.Report) {
        deleteListeners.forEach { c ->
            try { c.accept(report) } catch (_: Exception) { }
        }
    }

    @JvmStatic
    fun markFalseReport(reporterName: String?) {
        if (reporterName.isNullOrBlank()) return
        AntiAbuseManager.markFalseReport(reporterName)
    }

    @JvmStatic
    fun canReport(reporter: Player?, targetName: String?): Boolean {
        if (reporter == null || targetName.isNullOrBlank()) return false
        return AntiAbuseManager.canReport(reporter, targetName)
    }

    @JvmStatic
    fun hasLowPriority(reporterName: String?): Boolean {
        if (reporterName.isNullOrBlank()) return false
        return AntiAbuseManager.hasLowPriority(reporterName)
    }

    private fun getPlayerLocation(player: Player?): String {
        if (player == null || !player.isOnline) return "Unknown"
        val loc = player.location
        return "${loc.world?.name}: ${loc.blockX}, ${loc.blockY}, ${loc.blockZ}"
    }
}
