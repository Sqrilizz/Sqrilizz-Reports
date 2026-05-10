package dev.sqrilizz.reports

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TelegramManager(private val plugin: Main) {

    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    private val botToken: String = plugin.config.getString("telegram.bot-token", "")!!
    private val chatId: String = plugin.config.getString("telegram.chat-id", "")!!
    val isEnabled: Boolean = botToken.isNotEmpty() && chatId.isNotEmpty()

    companion object {
        private val DATE_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy").withZone(ZoneId.systemDefault())
    }

    fun initialize() {
        if (isEnabled) {
            plugin.logger.info("Telegram notifications enabled")
        } else {
            plugin.logger.warning("Telegram not configured properly")
        }
    }

    fun sendReport(report: ReportManager.Report) {
        if (!isEnabled) return

        try {
            val message = formatReportMessage(report)
            sendMessage(message)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to send Telegram notification: ${e.message}")
        }
    }

    private fun formatReportMessage(report: ReportManager.Report): String {
        val time = DATE_FORMAT.format(Instant.ofEpochMilli(report.timestamp))
        return """
            🚨 *New Report #${report.id}*
            
            *Target:* ${report.targetName}
            *Reporter:* ${report.reporterName}
            *Reason:* ${report.reason}
            *Location:* ${report.location}
            *Time:* $time
            
            Use `/reports close ${report.id}` to close this report.
        """.trimIndent()
    }

    private fun sendMessage(text: String) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        val json = """{"chat_id":"$chatId","text":"${escapeJson(text)}","parse_mode":"Markdown","disable_web_page_preview":true}"""

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .timeout(Duration.ofSeconds(30))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            throw java.io.IOException("Telegram API returned status: ${response.statusCode()}")
        }
    }

    private fun escapeJson(text: String): String =
        text.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
}
