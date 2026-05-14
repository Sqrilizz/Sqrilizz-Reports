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
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static void initialize() {
        // Read from nested config path: discord.webhook.url
        // Also support legacy flat path: discord.webhook_url for backward compatibility
        String url = Main.getInstance().getConfig().getString("discord.webhook.url", "");
        if (url.isEmpty() || url.equals("YOUR_WEBHOOK_URL")) {
            url = Main.getInstance().getConfig().getString("discord.webhook_url", "");
        }
        webhookUrl = url;
        
        // Check enabled flag from config
        boolean configEnabled = Main.getInstance().getConfig().getBoolean("discord.webhook.enabled", true);
        enabled = configEnabled && !webhookUrl.isEmpty() && !webhookUrl.equals("YOUR_WEBHOOK_URL");
        
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
        enabled = !url.isEmpty() && !url.equals("YOUR_WEBHOOK_URL");
        // Save to both paths for compatibility
        Main.getInstance().getConfig().set("discord.webhook.url", url);
        Main.getInstance().getConfig().set("discord.webhook.enabled", enabled);
        Main.getInstance().getConfig().set("discord.webhook_url", url);
        Main.getInstance().saveConfig();
    }

    public static void sendReport(ReportManager.Report report) {
        if (!isEnabled()) return;

        Main.runTaskAsync(() -> {
            try {
                JsonObject embed = new JsonObject();
                embed.addProperty("title", "Новая жалоба");
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
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(webhook.toString()))
                    .build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                
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

    public static void sendBugReport(ReportManager.Report report, String category) {
        if (!isEnabled()) return;

        Main.runTaskAsync(() -> {
            try {
                JsonObject embed = new JsonObject();
                embed.addProperty("title", "Новый баг-репорт");
                embed.addProperty("color", 0xFFA500); // Оранжевый цвет для багов
                embed.addProperty("timestamp", Instant.now().toString());

                // Добавляем поля
                JsonObject reporterField = new JsonObject();
                reporterField.addProperty("name", "Отправитель");
                reporterField.addProperty("value", report.reporter);
                reporterField.addProperty("inline", true);

                JsonObject categoryField = new JsonObject();
                categoryField.addProperty("name", "Категория");
                categoryField.addProperty("value", category.toUpperCase());
                categoryField.addProperty("inline", true);

                JsonObject reasonField = new JsonObject();
                reasonField.addProperty("name", "Описание");
                reasonField.addProperty("value", report.reason);
                reasonField.addProperty("inline", false);

                JsonObject timeField = new JsonObject();
                timeField.addProperty("name", "Время");
                timeField.addProperty("value", report.getFormattedTime());
                timeField.addProperty("inline", true);

                JsonObject reporterLocField = new JsonObject();
                reporterLocField.addProperty("name", "Координаты отправителя");
                reporterLocField.addProperty("value", report.reporterLocation);
                reporterLocField.addProperty("inline", true);

                // Создаем массив полей
                JsonObject[] fields = {reporterField, categoryField, reasonField, timeField, reporterLocField};
                embed.add("fields", gson.toJsonTree(fields));

                // Создаем основной объект webhook
                JsonObject webhook = new JsonObject();
                webhook.add("embeds", gson.toJsonTree(new JsonObject[]{embed}));

                // Отправляем webhook
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(webhook.toString()))
                    .build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 204) {
                    Main.getInstance().getLogger().info("Discord bug report webhook sent successfully");
                } else {
                    Main.getInstance().getLogger().warning("Discord bug report webhook failed with status: " + response.statusCode());
                }

            } catch (IOException | InterruptedException e) {
                Main.getInstance().getLogger().severe("Failed to send Discord bug report webhook: " + e.getMessage());
            }
        });
    }

    public static void sendResolvedReport(ReportManager.Report report, String resolver) {
        if (!isEnabled()) return;

        Main.runTaskAsync(() -> {
            try {
                JsonObject embed = new JsonObject();
                embed.addProperty("title", "Репорт решён");
                embed.addProperty("color", 0x00FF00); // Зелёный цвет
                embed.addProperty("timestamp", Instant.now().toString());

                JsonObject resolverField = new JsonObject();
                resolverField.addProperty("name", "Решил");
                resolverField.addProperty("value", resolver);
                resolverField.addProperty("inline", true);

                JsonObject idField = new JsonObject();
                idField.addProperty("name", "ID репорта");
                idField.addProperty("value", String.valueOf(report.id));
                idField.addProperty("inline", true);

                JsonObject targetField = new JsonObject();
                targetField.addProperty("name", "На кого");
                targetField.addProperty("value", report.target);
                targetField.addProperty("inline", true);

                JsonObject reporterField = new JsonObject();
                reporterField.addProperty("name", "От кого");
                reporterField.addProperty("value", report.isAnonymous ? "Аноним" : report.reporter);
                reporterField.addProperty("inline", true);

                JsonObject reasonField = new JsonObject();
                reasonField.addProperty("name", "Причина");
                reasonField.addProperty("value", report.reason);
                reasonField.addProperty("inline", false);

                JsonObject timeField = new JsonObject();
                timeField.addProperty("name", "Время решения");
                timeField.addProperty("value", TIME_FORMATTER.format(Instant.now()));
                timeField.addProperty("inline", true);

                JsonObject[] fields = {resolverField, idField, targetField, reporterField, reasonField, timeField};
                embed.add("fields", gson.toJsonTree(fields));

                JsonObject webhook = new JsonObject();
                webhook.add("embeds", gson.toJsonTree(new JsonObject[]{embed}));

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(webhook.toString()))
                    .build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 204) {
                    Main.getInstance().getLogger().info("Discord resolved webhook sent successfully");
                } else {
                    Main.getInstance().getLogger().warning("Discord resolved webhook failed with status: " + response.statusCode());
                }

            } catch (IOException | InterruptedException e) {
                Main.getInstance().getLogger().severe("Failed to send Discord resolved webhook: " + e.getMessage());
            }
        });
    }
} 