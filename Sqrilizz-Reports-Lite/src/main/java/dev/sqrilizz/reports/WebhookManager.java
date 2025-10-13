package dev.sqrilizz.reports;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Simple webhook notifications
 */
public class WebhookManager {
    
    private final Main plugin;
    private final HttpClient httpClient;
    private final String webhookUrl;
    private final boolean enabled;
    
    public WebhookManager(Main plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        this.webhookUrl = plugin.getConfig().getString("webhook.url", "");
        this.enabled = !webhookUrl.isEmpty();
        
        if (enabled) {
            plugin.getLogger().info("Webhook notifications enabled");
        }
    }
    
    /**
     * Send report notification via webhook
     */
    public void sendReport(ReportManager.Report report) {
        if (!enabled) return;
        
        try {
            String json = createReportJson(report);
            sendWebhook(json);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send webhook: " + e.getMessage());
        }
    }
    
    /**
     * Create JSON payload for report
     */
    private String createReportJson(ReportManager.Report report) {
        return String.format("""
            {
                "event": "report_created",
                "report": {
                    "id": %d,
                    "timestamp": %d,
                    "reporter": "%s",
                    "target": "%s",
                    "reason": "%s",
                    "location": "%s",
                    "resolved": false
                },
                "server": "%s"
            }
            """,
            report.getId(),
            report.getTimestamp(),
            escapeJson(report.getReporterName()),
            escapeJson(report.getTargetName()),
            escapeJson(report.getReason()),
            escapeJson(report.getLocation()),
            escapeJson(plugin.getServer().getName())
        );
    }
    
    /**
     * Send webhook HTTP request
     */
    private void sendWebhook(String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(webhookUrl))
            .header("Content-Type", "application/json")
            .header("User-Agent", "Sqrilizz-Reports-Lite/8.0")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .timeout(Duration.ofSeconds(30))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 400) {
            throw new IOException("Webhook returned status: " + response.statusCode());
        }
    }
    
    /**
     * Escape JSON special characters
     */
    private String escapeJson(String text) {
        if (text == null) return "";
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
