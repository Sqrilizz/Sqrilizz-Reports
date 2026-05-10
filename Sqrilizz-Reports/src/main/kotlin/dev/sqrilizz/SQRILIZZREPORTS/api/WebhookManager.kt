package dev.sqrilizz.SQRILIZZREPORTS.api

import dev.sqrilizz.SQRILIZZREPORTS.Main
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

object WebhookManager {

    private val webhookUrls = ConcurrentHashMap<String, String>()

    @JvmStatic
    fun registerWebhook(eventType: String?, webhookUrl: String?) {
        if (eventType != null && !webhookUrl.isNullOrBlank()) {
            webhookUrls[eventType.lowercase()] = webhookUrl
        }
    }

    @JvmStatic
    fun unregisterWebhook(eventType: String?) {
        if (eventType != null) {
            webhookUrls.remove(eventType.lowercase())
        }
    }

    @JvmStatic
    fun sendReportWebhook(event: ReportEvent) {
        val url = webhookUrls["report"] ?: return

        val payload = mutableMapOf<String, Any>(
            "type" to "report",
            "timestamp" to event.timestamp,
            "reporter" to event.reporterName,
            "target" to event.targetName,
            "reason" to event.reason,
            "is_system_report" to event.isSystemReport
        )

        if (event.isSystemReport && event.systemName != null) {
            payload["system_name"] = event.systemName
        }

        sendWebhook(url, payload)
    }

    @JvmStatic
    fun sendFalseReportWebhook(reporterName: String, adminName: String) {
        val url = webhookUrls["false_report"] ?: return

        sendWebhook(url, mapOf(
            "type" to "false_report",
            "timestamp" to System.currentTimeMillis(),
            "reporter" to reporterName,
            "admin" to adminName
        ))
    }

    @JvmStatic
    fun sendAutoBanWebhook(playerName: String, reportCount: Int) {
        val url = webhookUrls["auto_ban"] ?: return

        sendWebhook(url, mapOf(
            "type" to "auto_ban",
            "timestamp" to System.currentTimeMillis(),
            "player" to playerName,
            "report_count" to reportCount
        ))
    }

    @JvmStatic
    fun sendCustomWebhook(eventType: String, data: Map<String, Any>) {
        val url = webhookUrls[eventType.lowercase()] ?: return

        val payload = data.toMutableMap().apply {
            this["type"] = eventType
            this["timestamp"] = System.currentTimeMillis()
        }

        sendWebhook(url, payload)
    }

    @JvmStatic
    fun loadWebhooksFromConfig() {
        val config = Main.getInstance().config

        if (config.contains("webhooks")) {
            config.getConfigurationSection("webhooks")?.getKeys(false)?.forEach { eventType ->
                val url = config.getString("webhooks.$eventType")
                if (!url.isNullOrBlank()) {
                    registerWebhook(eventType, url)
                }
            }
        }
    }

    @JvmStatic
    fun getRegisteredWebhooks(): Map<String, String> = HashMap(webhookUrls)

    private fun sendWebhook(webhookUrl: String, payload: Map<String, Any>) {
        Main.runTaskAsync {
            try {
                val connection = URL(webhookUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("User-Agent", "Sqrilizz-Reports-Plugin")
                connection.doOutput = true

                val jsonPayload = mapToJson(payload)

                connection.outputStream.use { os ->
                    os.write(jsonPayload.toByteArray(StandardCharsets.UTF_8))
                }

                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    Main.getInstance().logger.info("Webhook sent successfully to $webhookUrl")
                } else {
                    Main.getInstance().logger.warning("Webhook failed with response code: $responseCode")
                }
            } catch (e: IOException) {
                Main.getInstance().logger.warning("Failed to send webhook: ${e.message}")
            }
        }
    }

    private fun mapToJson(map: Map<String, Any>): String = buildString {
        append("{")
        map.entries.forEachIndexed { index, (key, value) ->
            if (index > 0) append(",")
            append("\"").append(escapeJson(key)).append("\":")
            when (value) {
                is String -> append("\"").append(escapeJson(value)).append("\"")
                is Number, is Boolean -> append(value)
                else -> append("\"").append(escapeJson(value.toString())).append("\"")
            }
        }
        append("}")
    }

    private fun escapeJson(str: String): String =
        str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\u000C", "\\f")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
}
