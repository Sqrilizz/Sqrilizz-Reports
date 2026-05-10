package dev.sqrilizz.SQRILIZZREPORTS

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DiscordWebhookManager {

    private var webhookUrl = ""
    private var enabled = false
    private val gson = Gson()
    private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    @JvmStatic
    fun initialize() {
        webhookUrl = Main.getInstance().config.getString("discord.webhook_url", "") ?: ""
        enabled = webhookUrl.isNotEmpty()

        if (enabled) {
            Main.getInstance().logger.info("Discord webhook initialized successfully")
        } else {
            Main.getInstance().logger.info("Discord webhook not configured")
        }
    }

    @JvmStatic
    fun isEnabled(): Boolean = enabled && webhookUrl.isNotEmpty()

    @JvmStatic
    fun getWebhookUrl(): String = webhookUrl

    @JvmStatic
    fun setWebhookUrl(url: String) {
        webhookUrl = url
        enabled = url.isNotEmpty()
        Main.getInstance().config.set("discord.webhook_url", url)
        Main.getInstance().saveConfig()
    }

    @JvmStatic
    fun sendReport(report: ReportManager.Report) {
        if (!isEnabled()) return

        Main.runTaskAsync {
            try {
                val embed = JsonObject().apply {
                    addProperty("title", "\uD83D\uDEA8 Новая жалоба")
                    addProperty("color", 0xFF0000)
                    addProperty("timestamp", Instant.now().toString())
                }

                val fields = arrayOf(
                    createField("От кого", report.reporter, true),
                    createField("На кого", report.target, true),
                    createField("Причина", report.reason, false),
                    createField("Время", report.formattedTime, true),
                    createField("Координаты жалобщика", report.reporterLocation, true),
                    createField("Координаты цели", report.targetLocation, true)
                )
                embed.add("fields", gson.toJsonTree(fields))

                val webhook = JsonObject().apply {
                    add("embeds", gson.toJsonTree(arrayOf(embed)))
                }

                val client = HttpClient.newHttpClient()
                val request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(webhook.toString()))
                    .build()

                val response = client.send(request, HttpResponse.BodyHandlers.ofString())

                if (response.statusCode() == 204) {
                    Main.getInstance().logger.info("Discord webhook sent successfully")
                } else {
                    Main.getInstance().logger.warning("Discord webhook failed with status: ${response.statusCode()}")
                }
            } catch (e: Exception) {
                Main.getInstance().logger.severe("Failed to send Discord webhook: ${e.message}")
            }
        }
    }

    private fun createField(name: String, value: String, inline: Boolean): JsonObject =
        JsonObject().apply {
            addProperty("name", name)
            addProperty("value", value)
            addProperty("inline", inline)
        }
}
