package dev.sqrilizz.SQRILIZZREPORTS;

import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Optimized Telegram Manager using lightweight HTTP client instead of heavy Telegram Bot API
 * This reduces JAR size by ~15-20MB
 */
public class TelegramManager {
    private static String botToken;
    private static String chatId;
    private static boolean enabled = false;
    private static OkHttpClient httpClient;
    
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void initialize() {
        try {
            if (Main.getInstance().getConfig().getBoolean("telegram.enabled", false)) {
                botToken = Main.getInstance().getConfig().getString("telegram.token", "");
                chatId = Main.getInstance().getConfig().getString("telegram.chat_id", "");
                
                if (!botToken.isEmpty() && !chatId.isEmpty()) {
                    // Create lightweight HTTP client
                    httpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                    
                    enabled = true;
                    Main.getInstance().getLogger().info("Optimized Telegram manager initialized successfully");
                } else {
                    Main.getInstance().getLogger().warning("Telegram bot not configured properly");
                }
            }
        } catch (Exception e) {
            Main.getInstance().getLogger().severe("Failed to initialize optimized Telegram manager: " + e.getMessage());
        }
    }

    public static boolean isEnabled() {
        return enabled && httpClient != null;
    }

    public static void sendReport(ReportManager.Report report) {
        if (!isEnabled()) return;

        Main.runTaskAsync(() -> {
            try {
                String message = String.format(
                    "🚨 *Новая жалоба*\n\n" +
                    "*От:* %s\n" +
                    "*На:* %s\n" +
                    "*Причина:* %s\n" +
                    "*Время:* %s\n" +
                    "*Координаты жалобщика:* %s\n" +
                    "*Координаты цели:* %s",
                    report.reporter,
                    report.target,
                    report.reason,
                    report.getFormattedTime(),
                    report.reporterLocation,
                    report.targetLocation
                );

                sendMessage(message);
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("Failed to send Telegram report: " + e.getMessage());
            }
        });
    }

    public static void sendMessage(String text) {
        if (!isEnabled()) return;

        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("chat_id", chatId);
            payload.addProperty("text", text);
            payload.addProperty("parse_mode", "Markdown");

            RequestBody body = RequestBody.create(payload.toString(), JSON);
            Request request = new Request.Builder()
                .url(TELEGRAM_API_URL + botToken + "/sendMessage")
                .post(body)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Main.getInstance().getLogger().warning("Failed to send Telegram message: " + response.code());
                }
            }
        } catch (IOException e) {
            Main.getInstance().getLogger().warning("Error sending Telegram message: " + e.getMessage());
        }
    }

    public static void shutdown() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
        enabled = false;
    }

    // Utility method to test bot connection
    public static boolean testConnection() {
        if (!isEnabled()) return false;

        try {
            Request request = new Request.Builder()
                .url(TELEGRAM_API_URL + botToken + "/getMe")
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (IOException e) {
            return false;
        }
    }
}
