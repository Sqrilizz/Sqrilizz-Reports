package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DiscordWebhookManager {
    private static String webhookUrl = "";
    private static boolean enabled = false;
    private static final Gson gson = new Gson();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        .withZone(ZoneId.systemDefault());

    public static void initialize() {
        webhookUrl = Main.getInstance().getConfig().getString("discord.webhook_url", "");
        enabled = !webhookUrl.isEmpty();
        
        if (enabled) {
            Main.getInstance().getLogger().info("Discord webhook initialized successfully");
        } else {
            Main.getInstance().getLogger().info("Discord webhook not configured");
        }
    }

    public static boolean isEnabled() {
        return enabled && !webhookUrl.isEmpty();
    }

    public static String getWebhookUrl() {
        return webhookUrl;
    }

    public static void setWebhookUrl(String url) {
        webhookUrl = url;
        enabled = !url.isEmpty();
        Main.getInstance().getConfig().set("discord.webhook_url", url);
        Main.getInstance().saveConfig();
    }

    public static void sendReport(ReportManager.Report report) {
        if (!isEnabled()) return;

        Main.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                JsonObject embed = new JsonObject();
                embed.addProperty("title", "🚨 Новая жалоба");
                embed.addProperty("color", 0xFF0000); // Красный цвет
                embed.addProperty("timestamp", Instant.now().toString());

                // Добавляем поля
                JsonObject reporterField = new JsonObject();
                reporterField.addProperty("name", "От кого");
                reporterField.addProperty("value", report.reporter);
                reporterField.addProperty("inline", true);

                JsonObject targetField = new JsonObject();
                targetField.addProperty("name", "На кого");
                targetField.addProperty("value", report.target);
                targetField.addProperty("inline", true);

                JsonObject reasonField = new JsonObject();
                reasonField.addProperty("name", "Причина");
                reasonField.addProperty("value", report.reason);
                reasonField.addProperty("inline", false);

                JsonObject timeField = new JsonObject();
                timeField.addProperty("name", "Время");
                timeField.addProperty("value", report.getFormattedTime());
                timeField.addProperty("inline", true);

                JsonObject reporterLocField = new JsonObject();
                reporterLocField.addProperty("name", "Координаты жалобщика");
                reporterLocField.addProperty("value", report.reporterLocation);
                reporterLocField.addProperty("inline", true);

                JsonObject targetLocField = new JsonObject();
                targetLocField.addProperty("name", "Координаты цели");
                targetLocField.addProperty("value", report.targetLocation);
                targetLocField.addProperty("inline", true);

                // Создаем массив полей
                JsonObject[] fields = {reporterField, targetField, reasonField, timeField, reporterLocField, targetLocField};
                embed.add("fields", gson.toJsonTree(fields));

                // Создаем основной объект webhook
                JsonObject webhook = new JsonObject();
                webhook.add("embeds", gson.toJsonTree(new JsonObject[]{embed}));

                // Отправляем webhook
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(webhook.toString()))
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 204) {
                    Main.getInstance().getLogger().info("Discord webhook sent successfully");
                } else {
                    Main.getInstance().getLogger().warning("Discord webhook failed with status: " + response.statusCode());
                }

            } catch (IOException | InterruptedException e) {
                Main.getInstance().getLogger().severe("Failed to send Discord webhook: " + e.getMessage());
            }
        });
    }
} 