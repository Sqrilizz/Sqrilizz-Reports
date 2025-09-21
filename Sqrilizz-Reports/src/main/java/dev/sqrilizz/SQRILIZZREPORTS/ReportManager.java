package dev.sqrilizz.SQRILIZZREPORTS;

import dev.sqrilizz.SQRILIZZREPORTS.api.WebhookManager;
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportManager {
    private static FileConfiguration reportsConfig;
    private static File reportsFile;
    private static Map<String, List<Report>> reports = new HashMap<>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static void initialize() {
        reportsFile = new File(Main.getInstance().getDataFolder(), "reports.yml");
        if (!reportsFile.exists()) {
            try {
                reportsFile.createNewFile();
            } catch (IOException e) {
                Main.getInstance().getLogger().severe("Could not create reports.yml: " + e.getMessage());
            }
        }
        reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
        loadReports();
    }

    public static void addReport(Player reporter, Player target, String reason) {
        String targetName = VersionUtils.getPlayerCleanName(target);
        String reporterName = VersionUtils.getPlayerCleanName(reporter);
        boolean isAnonymous = Main.getInstance().getConfig().getBoolean("anonymous-reports", false);
        
        Report report = new Report(
            reporterName,
            targetName,
            reason,
            System.currentTimeMillis(),
            getPlayerLocation(reporter),
            getPlayerLocation(target),
            isAnonymous
        );

        reports.computeIfAbsent(targetName, k -> new ArrayList<>()).add(report);
        saveReports();

        // Уведомляем админов (анонимно или нет)
        notifyAdmins(report);

        // Отправляем в Telegram
        if (TelegramManager.isEnabled()) {
            TelegramManager.sendReport(report);
        }

        // Отправляем в Discord Webhook
        if (DiscordWebhookManager.isEnabled()) {
            DiscordWebhookManager.sendReport(report);
        }
        
        // Отправляем в Discord Bot
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

        // Отправляем webhook уведомление
        ReportEvent event = new ReportEvent(reporter, target, reason, System.currentTimeMillis());
        WebhookManager.sendReportWebhook(event);
    }

    private static String getPlayerLocation(Player player) {
        if (player == null || !player.isOnline()) {
            return "Неизвестно";
        }
        
        String world = player.getWorld().getName();
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        
        return String.format("%s: %d, %d, %d", world, x, y, z);
    }

    public static void clearReports(String targetName) {
        reports.remove(targetName);
        saveReports();
    }

    public static void clearAllReports() {
        reports.clear();
        saveReports();
    }

    public static List<Report> getPlayerReports(String targetName) {
        return reports.getOrDefault(targetName, new ArrayList<>());
    }

    public static Map<String, List<Report>> getReports() {
        return reports;
    }

    public static int getReportCount(String targetName) {
        return reports.getOrDefault(targetName, new ArrayList<>()).size();
    }

    public static void saveReports() {
        try {
            reportsConfig.set("reports", null); // Очищаем старые данные
            
            for (Map.Entry<String, List<Report>> entry : reports.entrySet()) {
                String targetName = entry.getKey();
                List<Report> targetReports = entry.getValue();
                
                for (int i = 0; i < targetReports.size(); i++) {
                    Report report = targetReports.get(i);
                    String path = "reports." + targetName + "." + i;
                    
                    reportsConfig.set(path + ".reporter", report.reporter);
                    reportsConfig.set(path + ".target", report.target);
                    reportsConfig.set(path + ".reason", report.reason);
                    reportsConfig.set(path + ".timestamp", report.timestamp);
                    reportsConfig.set(path + ".reporterLocation", report.reporterLocation);
                    reportsConfig.set(path + ".targetLocation", report.targetLocation);
                    reportsConfig.set(path + ".isAnonymous", report.isAnonymous);
                }
            }
            
            reportsConfig.save(reportsFile);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe("Could not save reports: " + e.getMessage());
        }
    }

    private static void notifyAdmins(Report report) {
        String message;
        
        if (report.isAnonymous) {
            // Анонимное уведомление
            message = LanguageManager.getMessage("anonymous-report-notification")
                .replace("[REASON]", report.reason)
                .replace("[TIME]", DATE_FORMAT.format(new Date(report.timestamp)))
                .replace("[TARGET_LOC]", report.targetLocation);
        } else {
            // Обычное уведомление
            message = LanguageManager.getMessage("admin-report-notification")
                .replace("[REPORTER]", report.reporter)
                .replace("[TARGET]", report.target)
                .replace("[REASON]", report.reason)
                .replace("[TIME]", DATE_FORMAT.format(new Date(report.timestamp)))
                .replace("[REPORTER_LOC]", report.reporterLocation)
                .replace("[TARGET_LOC]", report.targetLocation);
        }

        for (Player player : VersionUtils.getOnlinePlayers()) {
            if (VersionUtils.hasPermission(player, "reports.admin")) {
                VersionUtils.sendMessage(player, message);
            }
        }
    }

    private static void loadReports() {
        reports.clear();
        
        if (reportsConfig.contains("reports")) {
            for (String targetName : reportsConfig.getConfigurationSection("reports").getKeys(false)) {
                List<Report> targetReports = new ArrayList<>();
                
                for (String index : reportsConfig.getConfigurationSection("reports." + targetName).getKeys(false)) {
                    String path = "reports." + targetName + "." + index;
                    
                    String reporter = reportsConfig.getString(path + ".reporter", "Unknown");
                    String target = reportsConfig.getString(path + ".target", targetName);
                    String reason = reportsConfig.getString(path + ".reason", "No reason");
                    long timestamp = reportsConfig.getLong(path + ".timestamp", System.currentTimeMillis());
                    String reporterLocation = reportsConfig.getString(path + ".reporterLocation", "Неизвестно");
                    String targetLocation = reportsConfig.getString(path + ".targetLocation", "Неизвестно");
                    boolean isAnonymous = reportsConfig.getBoolean(path + ".isAnonymous", false);
                    
                    targetReports.add(new Report(reporter, target, reason, timestamp, reporterLocation, targetLocation, isAnonymous));
                }
                
                reports.put(targetName, targetReports);
            }
        }
    }

    public static class Report {
        public final String reporter;
        public final String target;
        public final String reason;
        public final long timestamp;
        public final String reporterLocation;
        public final String targetLocation;
        public final boolean isAnonymous;

        public Report(String reporter, String target, String reason, long timestamp, String reporterLocation, String targetLocation) {
            this(reporter, target, reason, timestamp, reporterLocation, targetLocation, false);
        }

        public Report(String reporter, String target, String reason, long timestamp, String reporterLocation, String targetLocation, boolean isAnonymous) {
            this.reporter = reporter;
            this.target = target;
            this.reason = reason;
            this.timestamp = timestamp;
            this.reporterLocation = reporterLocation;
            this.targetLocation = targetLocation;
            this.isAnonymous = isAnonymous;
        }

        public String getFormattedTime() {
            return DATE_FORMAT.format(new Date(timestamp));
        }

        public String getTimeAgo() {
            long diff = System.currentTimeMillis() - timestamp;
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                return days + " " + LanguageManager.getMessage("time-days");
            } else if (hours > 0) {
                return hours + " " + LanguageManager.getMessage("time-hours");
            } else if (minutes > 0) {
                return minutes + " " + LanguageManager.getMessage("time-minutes");
            } else {
                return seconds + " " + LanguageManager.getMessage("time-seconds");
            }
        }
    }
} 