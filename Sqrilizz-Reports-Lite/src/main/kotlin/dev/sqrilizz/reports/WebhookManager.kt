package dev.sqrilizz.reports

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class WebhookManager(private val plugin: Main) {

    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    private val webhookUrl: String = plugin.config.getString("webhook.url", "")!!
    val isEnabled: Boolean = webhookUrl.isNotEmpty()

    init {
        if (isEnabled) {
            plugin.logger.info("Webhook notifications enabled")
        }
    }

    fun sendReport(report: ReportManager.Report) {
        if (!isEnabled) return

        try {
            val json = createReportJson(report)
            sendWebhook(json)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to send webhook: ${e.message}")
        }
    }

    private fun createReportJson(report: ReportManager.Report): String = """
        {
            "event": "report_created",
            "report": {
                "id": ${report.id},
                "timestamp": ${report.timestamp},
                "reporter": "${escapeJson(report.reporterName)}",
                "target": "${escapeJson(report.targetName)}",
                "reason": "${escapeJson(report.reason)}",
                "location": "${escapeJson(report.location)}",
                "resolved": false
            },
            "server": "${escapeJson(plugin.server.name)}"
        }
    """.trimIndent()

    private fun sendWebhook(json: String) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(webhookUrl))
            .header("Content-Type", "application/json")
            .header("User-Agent", "Sqrilizz-Reports-Lite/${plugin.description.version}")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .timeout(Duration.ofSeconds(30))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() >= 400) {
            throw java.io.IOException("Webhook returned status: ${response.statusCode()}")
        }
    }

    private fun escapeJson(text: String?): String =
        (text ?: "")
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
}
