package dev.sqrilizz.reports.server

import dev.sqrilizz.reports.core.Report
import dev.sqrilizz.reports.core.ReportType
import org.bukkit.Bukkit
import org.bukkit.Sound
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.format.DateTimeFormatter

class NotificationService(private val plugin: ReportsPlugin) {
    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()

    fun reportCreated(report: Report) {
        notifyStaff(report)
        sendDiscord(report)
        sendTelegram(report)
    }

    fun reportStatusChanged(report: Report, moderator: String) {
        sendDiscordStatusUpdate(report, moderator)
    }

    private fun notifyStaff(report: Report) {
        val message = if (report.type == ReportType.BUG) {
            LanguageManager.get(
                "admin-bugreport-notification",
                "REPORTER" to report.reporterName,
                "CATEGORY" to report.targetName,
                "REASON" to report.reason,
                "TIME" to report.createdAt.toString(),
                "REPORTER_LOC" to report.sourceServer
            )
        } else {
            LanguageManager.get(
                "admin-report-notification",
                "REPORTER" to report.reporterName,
                "TARGET" to report.targetName,
                "REASON" to report.reason,
                "TIME" to report.createdAt.toString(),
                "REPORTER_LOC" to report.sourceServer,
                "TARGET_LOC" to report.sourceServer
            )
        }

        val soundName = plugin.config.getString("sounds.report-created", "ENTITY_EXPERIENCE_ORB_PICKUP") ?: ""
        val soundEnabled = plugin.config.getBoolean("sounds.enabled", true)
        @Suppress("DEPRECATION")
        val sound = runCatching { Sound.valueOf(soundName) }.getOrNull()

        Bukkit.getOnlinePlayers().filter { it.hasPermission("sqrilizzreports.admin") }.forEach { admin ->
            admin.sendMessage(ColorManager.component(message))
            if (soundEnabled && sound != null) {
                admin.playSound(admin.location, sound, 1.0f, 1.0f)
            }
        }
    }

    private fun sendDiscord(report: Report) {
        val url = plugin.config.getString("notifications.discord-webhook.url", "")!!.trim()
        if (!plugin.config.getBoolean("notifications.discord-webhook.enabled", false) || url.isBlank()) return

        val isEmbed = plugin.config.getBoolean("notifications.discord-webhook.embed", true)
        val username = plugin.config.getString("notifications.discord-webhook.username", "Sqrilizz-Reports") ?: "Sqrilizz-Reports"
        val avatarUrl = plugin.config.getString("notifications.discord-webhook.avatar-url", "") ?: ""

        val json = if (isEmbed) {
            val title = if (report.type == ReportType.BUG) "Bug Report #${report.id}" else "Player Report #${report.id}"
            val targetFieldTitle = if (report.type == ReportType.BUG) "Category" else "Target"
            val hex = plugin.config.getString("notifications.discord-webhook.color", "#FF6B6B")!!.removePrefix("#")
            val colorInt = hex.toIntOrNull(16) ?: 0xFF6B6B

            buildString {
                append("{")
                append("\"username\":\"${escape(username)}\",")
                if (avatarUrl.isNotBlank()) append("\"avatar_url\":\"${escape(avatarUrl)}\",")
                append("\"embeds\":[{")
                append("\"title\":\"${escape(title)}\",")
                append("\"color\":$colorInt,")
                append("\"fields\":[")
                append("{\"name\":\"$targetFieldTitle\",\"value\":\"${escape(report.targetName)}\",\"inline\":true},")
                append("{\"name\":\"Reporter\",\"value\":\"${escape(report.reporterName)}\",\"inline\":true},")
                append("{\"name\":\"Server\",\"value\":\"${escape(report.sourceServer)}\",\"inline\":true},")
                append("{\"name\":\"Reason\",\"value\":\"${escape(report.reason)}\",\"inline\":false}")
                append("],")
                append("\"timestamp\":\"${DateTimeFormatter.ISO_INSTANT.format(report.createdAt)}\",")
                append("\"footer\":{\"text\":\"Sqrilizz-Reports v11\"}")
                append("}]}")
            }
        } else {
            val content = "New report #${report.id} | ${report.targetName} | ${report.sourceServer} | ${report.reason}"
            "{\"username\":\"${escape(username)}\",\"content\":\"${escape(content)}\"}"
        }

        request(url, json)
    }

    private fun sendDiscordStatusUpdate(report: Report, moderator: String) {
        val url = plugin.config.getString("notifications.discord-webhook.url", "")!!.trim()
        if (!plugin.config.getBoolean("notifications.discord-webhook.enabled", false) || url.isBlank()) return
        val username = plugin.config.getString("notifications.discord-webhook.username", "Sqrilizz-Reports") ?: "Sqrilizz-Reports"
        val content = "Report #${report.id} (${report.targetName}) updated to **${report.status}** by $moderator"
        val json = "{\"username\":\"${escape(username)}\",\"content\":\"${escape(content)}\"}"
        request(url, json)
    }

    private fun sendTelegram(report: Report) {
        val token = plugin.config.getString("notifications.telegram.token", "")!!.trim()
        val chatId = plugin.config.getString("notifications.telegram.chat-id", "")!!.trim()
        if (!plugin.config.getBoolean("notifications.telegram.enabled", false) || token.isBlank() || chatId.isBlank()) return

        val text = buildString {
            append("<b>[Sqrilizz-Reports]</b> New ${report.type} #${report.id}\n")
            append("<b>Target:</b> ${escapeHtml(report.targetName)}\n")
            append("<b>From:</b> ${escapeHtml(report.reporterName)} (${escapeHtml(report.sourceServer)})\n")
            append("<b>Reason:</b> ${escapeHtml(report.reason)}")
        }

        val body = "chat_id=" + URLEncoder.encode(chatId, StandardCharsets.UTF_8) +
                "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                "&parse_mode=HTML"

        request("https://api.telegram.org/bot$token/sendMessage", body, "application/x-www-form-urlencoded")
    }

    private fun request(url: String, body: String, contentType: String = "application/json") {
        plugin.runAsync {
            runCatching {
                val req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", contentType)
                    .POST(BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(6))
                    .build()
                client.send(req, java.net.http.HttpResponse.BodyHandlers.discarding())
            }
        }
    }

    private fun escape(text: String): String = text
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "")

    private fun escapeHtml(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}
