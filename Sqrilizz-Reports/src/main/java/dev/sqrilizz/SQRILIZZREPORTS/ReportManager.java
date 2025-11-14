package dev.sqrilizz.SQRILIZZREPORTS;

import dev.sqrilizz.SQRILIZZREPORTS.api.CacheManager;
import dev.sqrilizz.SQRILIZZREPORTS.db.DatabaseManager;
import dev.sqrilizz.SQRILIZZREPORTS.errors.ErrorManager;
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportAPI;
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportDeleteEvent;
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportCreateEventBukkit;
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportReplyEvent;
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportResolveEvent;
import dev.sqrilizz.SQRILIZZREPORTS.utils.NotificationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReportManager {
    private static FileConfiguration reportsConfig;
    private static File reportsFile;
    // Оптимизированная синхронизация: ReadWriteLock вместо широкой блокировки
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static Map<String, List<Report>> reports = new ConcurrentHashMap<>();
    // O(1) поиск по ID для максимальной производительности
    private static Map<Long, Report> reportsById = new ConcurrentHashMap<>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static void initialize() {
        // YAML backup file
        reportsFile = new File(Main.getInstance().getDataFolder(), "reports.yml");
        if (!reportsFile.exists()) {
            try {
                reportsFile.createNewFile();
            } catch (IOException e) {
                Main.getInstance().getLogger().severe("Could not create reports.yml: " + e.getMessage());
            }
        }
        reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);

        // Initialize cache and database
        CacheManager.initialize();
        DatabaseManager.initialize();

        // Prefer DB as source of truth, fallback to YAML backup
        try {
            Map<String, List<Report>> fromDb = DatabaseManager.loadReports();
            if (fromDb != null && !fromDb.isEmpty()) {
                reports = new ConcurrentHashMap<>(fromDb);
            } else {
                loadReportsFromYaml();
            }
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("Failed to load reports from DB, using YAML backup: " + e.getMessage());
            loadReportsFromYaml();
        }
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

        // Persist to DB first to obtain ID
        long id = 0L;
        try {
            id = DatabaseManager.saveReport(report);
        } catch (Exception e) {
            ErrorManager.logError("DB_SAVE_REPORT", e);
            // Backup to JSON for recovery
            ErrorManager.writeBackup("report_create", targetName, report);
        }
        if (id > 0) report.id = id;

        // Оптимизированная запись с WriteLock
        LOCK.writeLock().lock();
        try {
            reports.computeIfAbsent(targetName, k -> new ArrayList<>()).add(0, report);
            // Добавляем в индекс для O(1) поиска
            if (report.id > 0) {
                reportsById.put(report.id, report);
            }
        } finally {
            LOCK.writeLock().unlock();
        }
        // Backup YAML
        saveReports();

        // Уведомляем админов (анонимно или нет)
        notifyAdmins(report);

        // Записываем метрику создания репорта
        
        // Асинхронная отправка уведомлений для максимальной производительности
        NotificationUtils.sendReportNotificationsAsync(report, reporter, target, reason, reporterName, targetName, isAnonymous);
        // Bukkit event
        Bukkit.getPluginManager().callEvent(new ReportCreateEventBukkit(report));
        // API listeners
        ReportAPI.onReportCreate(l -> {}); // no-op to ensure class load
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
        LOCK.writeLock().lock();
        try {
            List<Report> removed = reports.remove(targetName);
            // Удаляем из индекса
            if (removed != null) {
                removed.forEach(r -> reportsById.remove(r.id));
            }
        } finally {
            LOCK.writeLock().unlock();
        }
        saveReports();
    }

    public static void clearAllReports() {
        LOCK.writeLock().lock();
        try {
            reports.clear();
            reportsById.clear();
        } finally {
            LOCK.writeLock().unlock();
        }
        saveReports();
    }

    public static List<Report> getPlayerReports(String targetName) {
        // Оптимизированное чтение с ReadLock
        LOCK.readLock().lock();
        try {
            return new ArrayList<>(reports.getOrDefault(targetName, new ArrayList<>()));
        } finally {
            LOCK.readLock().unlock();
        }
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
                    
                    reportsConfig.set(path + ".id", report.id);
                    reportsConfig.set(path + ".reporter", report.reporter);
                    reportsConfig.set(path + ".target", report.target);
                    reportsConfig.set(path + ".reason", report.reason);
                    reportsConfig.set(path + ".timestamp", report.timestamp);
                    reportsConfig.set(path + ".reporterLocation", report.reporterLocation);
                    reportsConfig.set(path + ".targetLocation", report.targetLocation);
                    reportsConfig.set(path + ".isAnonymous", report.isAnonymous);
                    reportsConfig.set(path + ".status", report.status);
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

    private static void loadReportsFromYaml() {
        reports.clear();
        reportsById.clear(); // Очищаем индекс
        
        if (reportsConfig.contains("reports")) {
            for (String targetName : reportsConfig.getConfigurationSection("reports").getKeys(false)) {
                List<Report> targetReports = new ArrayList<>();
                
                for (String index : reportsConfig.getConfigurationSection("reports." + targetName).getKeys(false)) {
                    String path = "reports." + targetName + "." + index;
                    
                    long id = reportsConfig.getLong(path + ".id", 0L);
                    String reporter = reportsConfig.getString(path + ".reporter", "Unknown");
                    String target = reportsConfig.getString(path + ".target", targetName);
                    String reason = reportsConfig.getString(path + ".reason", "No reason");
                    long timestamp = reportsConfig.getLong(path + ".timestamp", System.currentTimeMillis());
                    String reporterLocation = reportsConfig.getString(path + ".reporterLocation", "Неизвестно");
                    String targetLocation = reportsConfig.getString(path + ".targetLocation", "Неизвестно");
                    boolean isAnonymous = reportsConfig.getBoolean(path + ".isAnonymous", false);
                    String status = reportsConfig.getString(path + ".status", "open");
                    
                    Report r = new Report(id, reporter, target, reason, timestamp, reporterLocation, targetLocation, isAnonymous, status);
                    targetReports.add(r);
                    // Добавляем в индекс для O(1) поиска
                    if (id > 0) {
                        reportsById.put(id, r);
                    }
                }
                
                reports.put(targetName, targetReports);
            }
        }
    }

    public static boolean resolveReport(long id, String resolver) {
        Report r = findById(id);
        if (r == null) return false;
        r.status = "resolved";
        boolean ok = false;
        try {
            ok = DatabaseManager.resolveReport(id, resolver);
        } catch (Exception e) {
            ErrorManager.logError("DB_RESOLVE", e);
        }
        saveReports();
        CacheManager.invalidate(r.target);
        // Записываем метрику разрешения репорта
        
        // Fire events
        Bukkit.getPluginManager().callEvent(new ReportResolveEvent(r, resolver));
        ReportAPI.notifyResolved(r);
        // Оптимизированный webhook
        NotificationUtils.sendEventWebhook("report_resolved", 
            NotificationUtils.createEventPayload("report_resolved", id, resolver));
        return ok;
    }

    public static boolean addReply(long id, String author, String message) {
        Report r = findById(id);
        if (r == null) return false;
        Reply reply = new Reply(0L, id, author, message, System.currentTimeMillis());
        boolean ok = false;
        try {
            ok = DatabaseManager.addReply(id, author, message, reply.timestamp);
        } catch (Exception e) {
            ErrorManager.logError("DB_REPLY", e);
        }
        r.replies.add(reply);
        saveReports();
        CacheManager.invalidate(r.target);
        // Записываем метрику добавления ответа
        
        // Fire events
        Bukkit.getPluginManager().callEvent(new ReportReplyEvent(reply));
        ReportAPI.notifyReplied(reply);
        // Оптимизированный webhook для ответов
        var payload = NotificationUtils.createEventPayload("report_reply", id, author);
        payload.put("message", message);
        NotificationUtils.sendEventWebhook("report_reply", payload);
        return ok;
    }

    public static boolean deleteReport(long id, String deleter) {
        Report r = findById(id);
        if (r == null) return false;
        LOCK.writeLock().lock();
        try {
            List<Report> lst = reports.getOrDefault(r.target, new ArrayList<>());
            lst.removeIf(rep -> rep.id == id);
            if (lst.isEmpty()) reports.remove(r.target); else reports.put(r.target, lst);
            // Удаляем из индекса
            reportsById.remove(id);
        } finally {
            LOCK.writeLock().unlock();
        }
        saveReports();
        CacheManager.invalidate(r.target);
        // Записываем метрику удаления репорта
        
        // Fire events
        Bukkit.getPluginManager().callEvent(new ReportDeleteEvent(r, deleter));
        ReportAPI.notifyDeleted(r);
        // Оптимизированный webhook для удаления
        NotificationUtils.sendEventWebhook("report_deleted", 
            NotificationUtils.createEventPayload("report_deleted", id, deleter));
        return true;
    }


    // O(1) поиск вместо O(n²) - критическая оптимизация!
    private static Report findById(long id) {
        return reportsById.get(id);
    }

    public static class Report {
        public long id;
        public final String reporter;
        public final String target;
        public final String reason;
        public final long timestamp;
        public final String reporterLocation;
        public final String targetLocation;
        public final boolean isAnonymous;
        public String status;
        public final List<Reply> replies = new ArrayList<>();

        public Report(String reporter, String target, String reason, long timestamp, String reporterLocation, String targetLocation) {
            this(0L, reporter, target, reason, timestamp, reporterLocation, targetLocation, false, "open");
        }

        public Report(String reporter, String target, String reason, long timestamp, String reporterLocation, String targetLocation, boolean isAnonymous) {
            this(0L, reporter, target, reason, timestamp, reporterLocation, targetLocation, isAnonymous, "open");
        }

        public Report(long id, String reporter, String target, String reason, long timestamp, String reporterLocation, String targetLocation, boolean isAnonymous, String status) {
            this.id = id;
            this.reporter = reporter;
            this.target = target;
            this.reason = reason;
            this.timestamp = timestamp;
            this.reporterLocation = reporterLocation;
            this.targetLocation = targetLocation;
            this.isAnonymous = isAnonymous;
            this.status = status == null ? "open" : status;
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

    public static class Reply {
        public final long id;
        public final long reportId;
        public final String author;
        public final String message;
        public final long timestamp;

        public Reply(long id, long reportId, String author, String message, long timestamp) {
            this.id = id;
            this.reportId = reportId;
            this.author = author;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
 