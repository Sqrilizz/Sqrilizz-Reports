package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhookManager {
    private static String webhookUrl;

    public static void initialize() {
        FileConfiguration config = Main.getInstance().getConfig();
        webhookUrl = config.getString("webhook-url", "");
        Main.getInstance().getLogger().info("DiscordWebhookManager initialized with URL: " + (webhookUrl.isEmpty() ? "not set" : webhookUrl));
    }

    public static void setWebhookUrl(String url) {
        Main.getInstance().getLogger().info("Setting Discord webhook URL: " + url);
        webhookUrl = url;
        Main.getInstance().getConfig().set("webhook-url", url);
        Main.getInstance().saveConfig();
    }

    public static void removeWebhookUrl() {
        Main.getInstance().getLogger().info("Removing Discord webhook URL");
        webhookUrl = "";
        Main.getInstance().getConfig().set("webhook-url", "");
        Main.getInstance().saveConfig();
    }

    public static String getWebhookUrl() {
        return webhookUrl;
    }

    public static void sendReport(String reporter, String target, String reason) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            Main.getInstance().getLogger().info("Discord webhook URL is not set. Skipping Discord notification.");
            return;
        }

        String content = String.format("%s\n%s\n%s\n%s",
                LanguageManager.getMessage("discord-report-title"),
                String.format(LanguageManager.getMessage("discord-report-from"), reporter),
                String.format(LanguageManager.getMessage("discord-report-target"), target),
                String.format(LanguageManager.getMessage("discord-report-reason"), reason));

        Main.getInstance().getLogger().info("Attempting to send Discord webhook message: " + content);

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                URL url = new URL(webhookUrl);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Правильный формат JSON для Discord webhook
                String json = String.format("{\"content\": \"%s\"}", 
                    content.replace("\"", "\\\"")
                          .replace("\n", "\\n")
                          .replace("\r", "\\r"));

                Main.getInstance().getLogger().info("Sending JSON to Discord: " + json);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                Main.getInstance().getLogger().info("Discord webhook response code: " + responseCode);

                // Читаем ответ от сервера
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Main.getInstance().getLogger().info("Discord webhook response: " + response.toString());
                }

                if (responseCode != 204) {
                    Main.getInstance().getLogger().warning("Discord webhook returned unexpected response code: " + responseCode);
                }
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("Failed to send report to Discord webhook: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
} 