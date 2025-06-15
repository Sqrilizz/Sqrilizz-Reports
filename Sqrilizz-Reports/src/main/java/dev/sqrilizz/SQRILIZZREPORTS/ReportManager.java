package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportManager {
    private static File reportsFile;
    private static FileConfiguration reportsConfig;
    private static final Map<String, List<Report>> reports = new HashMap<>();
    private static final int AUTO_BAN_THRESHOLD = Main.getInstance().getConfig().getInt("auto-ban-threshold", 8);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static void initialize() {
        reportsFile = new File(Main.getInstance().getDataFolder(), "reports.yml");
        if (!reportsFile.exists()) {
            try {
                reportsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
        loadReports();
    }

    private static void loadReports() {
        reports.clear();
        if (reportsConfig.contains("reports")) {
            for (String targetName : reportsConfig.getConfigurationSection("reports").getKeys(false)) {
                List<Report> targetReports = new ArrayList<>();
                for (String key : reportsConfig.getConfigurationSection("reports." + targetName).getKeys(false)) {
                    String reporter = reportsConfig.getString("reports." + targetName + "." + key + ".reporter");
                    String reason = reportsConfig.getString("reports." + targetName + "." + key + ".reason");
                    long timestamp = reportsConfig.getLong("reports." + targetName + "." + key + ".timestamp");
                    targetReports.add(new Report(reporter, reason, timestamp));
                }
                reports.put(targetName, targetReports);
            }
        }
    }

    public static void addReport(String reporter, String target, String reason) {
        List<Report> targetReports = reports.computeIfAbsent(target, k -> new ArrayList<>());
        Report report = new Report(reporter, reason, System.currentTimeMillis());
        targetReports.add(report);
        saveReports();

        // Send notification to Telegram
        TelegramManager.sendReport(reporter, target, reason);
        // Send notification to Discord
        DiscordWebhookManager.sendReport(reporter, target, reason);

        // Check for auto-ban
        if (targetReports.size() >= Main.getInstance().getConfig().getInt("auto-ban-threshold", 8)) {
            Player targetPlayer = Bukkit.getPlayer(target);
            if (targetPlayer != null) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
                        "ban " + target + " " + LanguageManager.getMessage("auto-ban-message"));
                });
            }
        }
    }

    public static void clearReports(String target) {
        reports.remove(target);
        saveReports();
    }

    public static void clearAllReports() {
        reports.clear();
        saveReports();
    }

    public static int getReportCount(String target) {
        return reports.getOrDefault(target, new ArrayList<>()).size();
    }

    public static List<Report> getReports(String target) {
        return reports.getOrDefault(target, new ArrayList<>());
    }

    public static Map<String, List<Report>> getReports() {
        return reports;
    }

    public static void saveReports() {
        reportsConfig.set("reports", null);
        for (Map.Entry<String, List<Report>> entry : reports.entrySet()) {
            String target = entry.getKey();
            List<Report> targetReports = entry.getValue();
            for (int i = 0; i < targetReports.size(); i++) {
                Report report = targetReports.get(i);
                String path = "reports." + target + "." + i;
                reportsConfig.set(path + ".reporter", report.reporter);
                reportsConfig.set(path + ".reason", report.reason);
                reportsConfig.set(path + ".timestamp", report.timestamp);
            }
        }
        try {
            reportsConfig.save(reportsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Report {
        public final String reporter;
        public final String reason;
        public final long timestamp;

        public Report(String reporter, String reason, long timestamp) {
            this.reporter = reporter;
            this.reason = reason;
            this.timestamp = timestamp;
        }

        public String getTimeAgo() {
            long now = System.currentTimeMillis();
            long diff = now - timestamp;
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