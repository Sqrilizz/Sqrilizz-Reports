package dev.sqrilizz.SQRILIZZREPORTS.utils

import dev.sqrilizz.SQRILIZZREPORTS.*
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportEvent
import dev.sqrilizz.SQRILIZZREPORTS.api.WebhookManager
import dev.sqrilizz.SQRILIZZREPORTS.errors.ErrorManager
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

object NotificationUtils {

    @JvmStatic
    fun sendReportNotificationsAsync(
        report: ReportManager.Report, reporter: Player,
        target: Player, reason: String, reporterName: String,
        targetName: String, isAnonymous: Boolean
    ) {
        CompletableFuture.runAsync {
            try {
                sendTelegramNotification(report)
                sendDiscordWebhookNotification(report)
                sendCustomWebhook(reporter, target, reason)
            } catch (e: Exception) {
                ErrorManager.logError("NOTIFICATION_ASYNC", e)
            }
        }
    }

    @JvmStatic
    fun sendBugReportNotificationsAsync(
        report: ReportManager.Report, reporter: Player,
        category: String, description: String, reporterName: String
    ) {
        CompletableFuture.runAsync {
            try {
                sendTelegramNotification(report)
                sendDiscordWebhookNotification(report)
                sendCustomBugReportWebhook(reporter, category, description)
            } catch (e: Exception) {
                ErrorManager.logError("BUG_NOTIFICATION_ASYNC", e)
            }
        }
    }

    @JvmStatic
    fun sendEventWebhook(eventType: String, data: Map<String, Any>) {
        CompletableFuture.runAsync {
            try {
                WebhookManager.sendCustomWebhook(eventType, data)
            } catch (e: Exception) {
                ErrorManager.logError("WEBHOOK_${eventType.uppercase()}", e)
            }
        }
    }

    @JvmStatic
    fun createEventPayload(type: String, reportId: Long, actor: String): MutableMap<String, Any> =
        mutableMapOf(
            "type" to type,
            "report_id" to reportId,
            "actor" to actor,
            "timestamp" to System.currentTimeMillis(),
            "server" to Main.getInstance().server.name
        )

    private fun sendTelegramNotification(report: ReportManager.Report) {
        if (TelegramManager.isEnabled()) {
            TelegramManager.sendReport(report)
        }
    }

    private fun sendDiscordWebhookNotification(report: ReportManager.Report) {
        if (DiscordWebhookManager.isEnabled()) {
            DiscordWebhookManager.sendReport(report)
        }
    }

    private fun sendCustomWebhook(reporter: Player, target: Player, reason: String) {
        val event = ReportEvent(reporter, target, reason, System.currentTimeMillis())
        WebhookManager.sendReportWebhook(event)
    }

    private fun sendCustomBugReportWebhook(reporter: Player, category: String, description: String) {
        val fullReason = "[${category.uppercase()}] $description"
        val event = ReportEvent(reporter, null, fullReason, System.currentTimeMillis())
        WebhookManager.sendReportWebhook(event)
    }
}
