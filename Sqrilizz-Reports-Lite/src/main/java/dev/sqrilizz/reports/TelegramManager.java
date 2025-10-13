package dev.sqrilizz.reports;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Simple Telegram notifications without heavy dependencies
 */
public class TelegramManager {
    
    private final Main plugin;
    private final HttpClient httpClient;
    private final String botToken;
    private final String chatId;
    private final boolean enabled;
    
    public TelegramManager(Main plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        this.botToken = plugin.getConfig().getString("telegram.bot-token", "");
        this.chatId = plugin.getConfig().getString("telegram.chat-id", "");
        this.enabled = !botToken.isEmpty() && !chatId.isEmpty();
    }
    
    public void initialize() {
        if (enabled) {
            plugin.getLogger().info("Telegram notifications enabled");
        } else {
            plugin.getLogger().warning("Telegram not configured properly");
        }
    }
    
    /**
     * Send report notification to Telegram
     */
    public void sendReport(ReportManager.Report report) {
        if (!enabled) return;
        
        try {
            String message = formatReportMessage(report);
            sendMessage(message);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send Telegram notification: " + e.getMessage());
        }
    }
    
    /**
     * Format report as Telegram message
     */
    private String formatReportMessage(ReportManager.Report report) {
        String time = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(report.getTimestamp()));
        
        return String.format("""
            🚨 **New Report #%d**
            
            **Target:** %s
            **Reporter:** %s
            **Reason:** %s
            **Location:** %s
            **Time:** %s
            
            Use `/reports close %d` to close this report.
            """, 
            report.getId(),
            report.getTargetName(),
            report.getReporterName(),
            report.getReason(),
            report.getLocation(),
            time,
            report.getId()
        );
    }
    
    /**
     * Send message to Telegram
     */
    private void sendMessage(String text) throws IOException, InterruptedException {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
        
        String json = String.format("""
            {
                "chat_id": "%s",
                "text": "%s",
                "parse_mode": "Markdown",
                "disable_web_page_preview": true
            }
            """, chatId, escapeJson(text));
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .timeout(Duration.ofSeconds(30))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Telegram API returned status: " + response.statusCode());
        }
    }
    
    /**
     * Escape JSON special characters
     */
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
