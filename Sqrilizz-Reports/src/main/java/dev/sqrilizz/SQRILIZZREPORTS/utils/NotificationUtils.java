package dev.sqrilizz.SQRILIZZREPORTS.utils;

import dev.sqrilizz.SQRILIZZREPORTS.*;
import dev.sqrilizz.SQRILIZZREPORTS.api.WebhookManager;
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportEvent;
import dev.sqrilizz.SQRILIZZREPORTS.errors.ErrorManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Оптимизированный утилитарный класс для уведомлений - убирает дублирование кода
 * Повышает производительность за счет асинхронной обработки
 */
public class NotificationUtils {
    
    /**
     * Асинхронная отправка всех типов уведомлений для максимальной производительности
     */
    public static void sendReportNotificationsAsync(ReportManager.Report report, Player reporter, 
                                                   Player target, String reason, String reporterName, 
                                                   String targetName, boolean isAnonymous) {
        // Используем CompletableFuture для асинхронной обработки
        CompletableFuture.runAsync(() -> {
            try {
                sendTelegramNotification(report);
                sendDiscordWebhookNotification(report);
                sendDiscordBotNotification(report, reporterName, targetName, reason, isAnonymous);
                sendCustomWebhook(reporter, target, reason);
            } catch (Exception e) {
                ErrorManager.logError("NOTIFICATION_ASYNC", e);
            }
        });
    }
    
    /**
     * Оптимизированная отправка webhook для различных событий
     */
    public static void sendEventWebhook(String eventType, Map<String, Object> data) {
        CompletableFuture.runAsync(() -> {
            try {
                WebhookManager.sendCustomWebhook(eventType, data);
            } catch (Exception e) {
                ErrorManager.logError("WEBHOOK_" + eventType.toUpperCase(), e);
            }
        });
    }
    
    /**
     * Создание стандартного payload для webhook событий
     */
    public static Map<String, Object> createEventPayload(String type, long reportId, String actor) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("report_id", reportId);
        payload.put("actor", actor);
        payload.put("timestamp", System.currentTimeMillis());
        payload.put("server", Main.getInstance().getServer().getName());
        return payload;
    }
    
    private static void sendTelegramNotification(ReportManager.Report report) {
        if (TelegramManager.isEnabled()) {
            TelegramManager.sendReport(report);
        }
    }
    
    private static void sendDiscordWebhookNotification(ReportManager.Report report) {
        if (DiscordWebhookManager.isEnabled()) {
            DiscordWebhookManager.sendReport(report);
        }
    }
    
    private static void sendDiscordBotNotification(ReportManager.Report report, String reporterName, 
                                                 String targetName, String reason, boolean isAnonymous) {
        if (DiscordBot.isEnabled()) {
            DiscordBot.sendReportNotification(
                reporterName, 
                targetName, 
                reason, 
                report.timestamp, 
                report.reporterLocation, 
                report.targetLocation, 
                isAnonymous
            );
        }
    }
    
    private static void sendCustomWebhook(Player reporter, Player target, String reason) {
        ReportEvent event = new ReportEvent(reporter, target, reason, System.currentTimeMillis());
        WebhookManager.sendReportWebhook(event);
    }
}
