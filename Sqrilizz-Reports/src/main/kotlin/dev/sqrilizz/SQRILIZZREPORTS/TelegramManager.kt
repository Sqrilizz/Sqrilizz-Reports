package dev.sqrilizz.SQRILIZZREPORTS

import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object TelegramManager {

    private var botToken: String = ""
    private var chatId: String = ""
    private var enabled = false
    private var httpClient: OkHttpClient? = null

    private const val TELEGRAM_API_URL = "https://api.telegram.org/bot"
    private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()

    @JvmStatic
    fun initialize() {
        try {
            if (Main.getInstance().config.getBoolean("telegram.enabled", false)) {
                botToken = Main.getInstance().config.getString("telegram.token", "") ?: ""
                chatId = Main.getInstance().config.getString("telegram.chat_id", "") ?: ""

                if (botToken.isNotEmpty() && chatId.isNotEmpty()) {
                    httpClient = OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build()

                    enabled = true
                    Main.getInstance().logger.info("Optimized Telegram manager initialized successfully")
                } else {
                    Main.getInstance().logger.warning("Telegram bot not configured properly")
                }
            }
        } catch (e: Exception) {
            Main.getInstance().logger.severe("Failed to initialize optimized Telegram manager: ${e.message}")
        }
    }

    @JvmStatic
    fun isEnabled(): Boolean = enabled && httpClient != null

    @JvmStatic
    fun sendReport(report: ReportManager.Report) {
        if (!isEnabled()) return

        Main.runTaskAsync {
            try {
                val message = buildString {
                    appendLine("\uD83D\uDEA8 *Новая жалоба*")
                    appendLine()
                    appendLine("*От:* ${report.reporter}")
                    appendLine("*На:* ${report.target}")
                    appendLine("*Причина:* ${report.reason}")
                    appendLine("*Время:* ${report.formattedTime}")
                    appendLine("*Координаты жалобщика:* ${report.reporterLocation}")
                    append("*Координаты цели:* ${report.targetLocation}")
                }
                sendMessage(message)
            } catch (e: Exception) {
                Main.getInstance().logger.warning("Failed to send Telegram report: ${e.message}")
            }
        }
    }

    @JvmStatic
    fun sendMessage(text: String) {
        if (!isEnabled()) return

        try {
            val payload = JsonObject().apply {
                addProperty("chat_id", chatId)
                addProperty("text", text)
                addProperty("parse_mode", "Markdown")
            }

            val body = payload.toString().toRequestBody(JSON_MEDIA)
            val request = Request.Builder()
                .url("$TELEGRAM_API_URL$botToken/sendMessage")
                .post(body)
                .build()

            httpClient?.newCall(request)?.execute()?.use { response ->
                if (!response.isSuccessful) {
                    Main.getInstance().logger.warning("Failed to send Telegram message: ${response.code}")
                }
            }
        } catch (e: IOException) {
            Main.getInstance().logger.warning("Error sending Telegram message: ${e.message}")
        }
    }

    @JvmStatic
    fun shutdown() {
        httpClient?.let {
            it.dispatcher.executorService.shutdown()
            it.connectionPool.evictAll()
        }
        enabled = false
    }

    @JvmStatic
    fun testConnection(): Boolean {
        if (!isEnabled()) return false

        return try {
            val request = Request.Builder()
                .url("$TELEGRAM_API_URL$botToken/getMe")
                .build()
            httpClient?.newCall(request)?.execute()?.use { it.isSuccessful } ?: false
        } catch (_: IOException) {
            false
        }
    }
}
