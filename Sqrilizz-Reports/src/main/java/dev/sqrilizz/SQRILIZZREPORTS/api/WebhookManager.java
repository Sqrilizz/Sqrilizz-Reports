package dev.sqrilizz.SQRILIZZREPORTS.api;

import dev.sqrilizz.SQRILIZZREPORTS.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер для отправки webhook уведомлений
 * Поддерживает JSON выгрузку для интеграции с внешними системами
 */
public class WebhookManager {
    
    private static final Map<String, String> webhookUrls = new HashMap<>();
    
    /**
     * Регистрирует webhook URL для определенного типа событий
     * 
     * @param eventType Тип события (например, "report", "false_report", "ban")
     * @param webhookUrl URL webhook'а
     */
    public static void registerWebhook(String eventType, String webhookUrl) {
        if (eventType != null && webhookUrl != null && !webhookUrl.trim().isEmpty()) {
            webhookUrls.put(eventType.toLowerCase(), webhookUrl);
        }
    }
    
    /**
     * Удаляет webhook для определенного типа событий
     * 
     * @param eventType Тип события
     */
    public static void unregisterWebhook(String eventType) {
        if (eventType != null) {
            webhookUrls.remove(eventType.toLowerCase());
        }
    }
    
    /**
     * Отправляет уведомление о новой жалобе
     * 
     * @param event Событие жалобы
     */
    public static void sendReportWebhook(ReportEvent event) {
        String webhookUrl = webhookUrls.get("report");
        if (webhookUrl == null) {
            return;
        }
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "report");
        payload.put("timestamp", event.getTimestamp());
        payload.put("reporter", event.getReporterName());
        payload.put("target", event.getTargetName());
        payload.put("reason", event.getReason());
        payload.put("is_system_report", event.isSystemReport());
        
        if (event.isSystemReport()) {
            payload.put("system_name", event.getSystemName());
        }
        
        sendWebhook(webhookUrl, payload);
    }
    
    /**
     * Отправляет уведомление о ложной жалобе
     * 
     * @param reporterName Имя игрока, отправившего ложную жалобу
     * @param adminName Имя администратора, отметившего жалобу как ложную
     */
    public static void sendFalseReportWebhook(String reporterName, String adminName) {
        String webhookUrl = webhookUrls.get("false_report");
        if (webhookUrl == null) {
            return;
        }
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "false_report");
        payload.put("timestamp", System.currentTimeMillis());
        payload.put("reporter", reporterName);
        payload.put("admin", adminName);
        
        sendWebhook(webhookUrl, payload);
    }
    
    /**
     * Отправляет уведомление о бане за превышение лимита жалоб
     * 
     * @param playerName Имя забаненного игрока
     * @param reportCount Количество жалоб
     */
    public static void sendAutoBanWebhook(String playerName, int reportCount) {
        String webhookUrl = webhookUrls.get("auto_ban");
        if (webhookUrl == null) {
            return;
        }
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "auto_ban");
        payload.put("timestamp", System.currentTimeMillis());
        payload.put("player", playerName);
        payload.put("report_count", reportCount);
        
        sendWebhook(webhookUrl, payload);
    }
    
    /**
     * Отправляет произвольное webhook уведомление
     * 
     * @param eventType Тип события
     * @param data Данные для отправки
     */
    public static void sendCustomWebhook(String eventType, Map<String, Object> data) {
        String webhookUrl = webhookUrls.get(eventType.toLowerCase());
        if (webhookUrl == null) {
            return;
        }
        
        Map<String, Object> payload = new HashMap<>(data);
        payload.put("type", eventType);
        payload.put("timestamp", System.currentTimeMillis());
        
        sendWebhook(webhookUrl, payload);
    }
    
    /**
     * Отправляет HTTP POST запрос с JSON данными
     * 
     * @param webhookUrl URL webhook'а
     * @param payload Данные для отправки
     */
    private static void sendWebhook(String webhookUrl, Map<String, Object> payload) {
        // Выполняем в асинхронном потоке, чтобы не блокировать основной поток
        Main.runTaskAsync(() -> {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Sqrilizz-Reports-Plugin");
                connection.setDoOutput(true);
                
                // Простая JSON сериализация
                String jsonPayload = mapToJson(payload);
                
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    Main.getInstance().getLogger().info("Webhook sent successfully to " + webhookUrl);
                } else {
                    Main.getInstance().getLogger().warning("Webhook failed with response code: " + responseCode);
                }
                
            } catch (IOException e) {
                Main.getInstance().getLogger().warning("Failed to send webhook: " + e.getMessage());
            }
        });
    }
    
    /**
     * Простая JSON сериализация Map'а
     * Не используем внешние библиотеки для совместимости
     */
    private static String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            
            json.append("\"").append(escapeJson(entry.getKey())).append("\":");
            
            Object value = entry.getValue();
            if (value == null) {
                json.append("null");
            } else if (value instanceof String) {
                json.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value.toString());
            } else {
                json.append("\"").append(escapeJson(value.toString())).append("\"");
            }
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Экранирует специальные символы для JSON
     */
    private static String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Загружает webhook URL'ы из конфигурации
     */
    public static void loadWebhooksFromConfig() {
        FileConfiguration config = Main.getInstance().getConfig();
        
        if (config.contains("webhooks")) {
            for (String eventType : config.getConfigurationSection("webhooks").getKeys(false)) {
                String url = config.getString("webhooks." + eventType);
                if (url != null && !url.trim().isEmpty()) {
                    registerWebhook(eventType, url);
                }
            }
        }
    }
    
    /**
     * Получает все зарегистрированные webhook'и
     */
    public static Map<String, String> getRegisteredWebhooks() {
        return new HashMap<>(webhookUrls);
    }
}
