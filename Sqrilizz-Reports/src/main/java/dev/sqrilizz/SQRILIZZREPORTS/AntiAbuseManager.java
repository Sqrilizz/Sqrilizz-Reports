package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AntiAbuseManager {
    private static FileConfiguration abuseConfig;
    private static File abuseFile;
    
    // Хранение данных о жалобах игроков
    private static Map<String, List<Long>> playerReportTimes = new ConcurrentHashMap<>();
    private static Map<String, Map<String, Integer>> playerTargetReports = new ConcurrentHashMap<>();
    private static Map<String, Integer> falseReportCounts = new ConcurrentHashMap<>();
    private static Map<String, Long> tempMutedPlayers = new ConcurrentHashMap<>();
    
    public static void initialize() {
        abuseFile = new File(Main.getInstance().getDataFolder(), "abuse_data.yml");
        if (!abuseFile.exists()) {
            try {
                abuseFile.createNewFile();
            } catch (IOException e) {
                Main.getInstance().getLogger().severe("Could not create abuse_data.yml: " + e.getMessage());
            }
        }
        abuseConfig = YamlConfiguration.loadConfiguration(abuseFile);
        loadAbuseData();
    }
    
    /**
     * Проверяет, может ли игрок отправить жалобу
     */
    public static boolean canReport(Player reporter, String targetName) {
        String reporterName = VersionUtils.getPlayerCleanName(reporter);
        
        // Проверяем временный мут
        if (isTempMuted(reporterName)) {
            long remainingTime = (tempMutedPlayers.get(reporterName) - System.currentTimeMillis()) / 1000;
            VersionUtils.sendMessage(reporter, LanguageManager.getMessage("abuse-temp-mute")
                .replace("[DURATION]", String.valueOf(remainingTime)));
            return false;
        }
        
        // Проверяем лимит жалоб на конкретного игрока
        if (!checkPlayerReportLimit(reporterName, targetName)) {
            int limit = Main.getInstance().getConfig().getInt("report-limits.per-player", 3);
            int current = getPlayerTargetReportCount(reporterName, targetName);
            VersionUtils.sendMessage(reporter, LanguageManager.getMessage("report-limit-reached")
                .replace("[LIMIT]", String.valueOf(current))
                .replace("[MAX]", String.valueOf(limit)));
            return false;
        }
        
        // Проверяем часовой лимит
        if (!checkHourlyLimit(reporterName)) {
            int limit = Main.getInstance().getConfig().getInt("report-limits.per-hour", 10);
            int current = getHourlyReportCount(reporterName);
            VersionUtils.sendMessage(reporter, LanguageManager.getMessage("hourly-limit-reached")
                .replace("[LIMIT]", String.valueOf(current))
                .replace("[MAX]", String.valueOf(limit)));
            return false;
        }
        
        return true;
    }
    
    /**
     * Регистрирует новую жалобу
     */
    public static void recordReport(Player reporter, String targetName) {
        String reporterName = VersionUtils.getPlayerCleanName(reporter);
        long currentTime = System.currentTimeMillis();
        
        // Записываем время жалобы
        playerReportTimes.computeIfAbsent(reporterName, k -> new ArrayList<>()).add(currentTime);
        
        // Записываем жалобу на конкретного игрока
        playerTargetReports.computeIfAbsent(reporterName, k -> new HashMap<>())
            .merge(targetName, 1, Integer::sum);
        
        // Проверяем на злоупотребления
        checkForAbuse(reporter);
        
        saveAbuseData();
    }
    
    /**
     * Проверяет лимит жалоб на конкретного игрока
     */
    private static boolean checkPlayerReportLimit(String reporterName, String targetName) {
        int limit = Main.getInstance().getConfig().getInt("report-limits.per-player", 3);
        return getPlayerTargetReportCount(reporterName, targetName) < limit;
    }
    
    /**
     * Проверяет часовой лимит жалоб
     */
    private static boolean checkHourlyLimit(String reporterName) {
        int limit = Main.getInstance().getConfig().getInt("report-limits.per-hour", 10);
        return getHourlyReportCount(reporterName) < limit;
    }
    
    /**
     * Получает количество жалоб на конкретного игрока
     */
    private static int getPlayerTargetReportCount(String reporterName, String targetName) {
        return playerTargetReports.getOrDefault(reporterName, new HashMap<>())
            .getOrDefault(targetName, 0);
    }
    
    /**
     * Получает количество жалоб за последний час
     */
    private static int getHourlyReportCount(String reporterName) {
        List<Long> reportTimes = playerReportTimes.get(reporterName);
        if (reportTimes == null) return 0;
        
        long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000);
        return (int) reportTimes.stream().filter(time -> time > oneHourAgo).count();
    }
    
    /**
     * Проверяет на злоупотребления и применяет меры
     */
    private static void checkForAbuse(Player reporter) {
        String reporterName = VersionUtils.getPlayerCleanName(reporter);
        int hourlyCount = getHourlyReportCount(reporterName);
        
        int warningThreshold = Main.getInstance().getConfig().getInt("anti-abuse.warning-threshold", 5);
        int muteThreshold = Main.getInstance().getConfig().getInt("anti-abuse.temp-mute-threshold", 8);
        
        if (hourlyCount >= muteThreshold) {
            // Временный мут
            int muteDuration = Main.getInstance().getConfig().getInt("anti-abuse.temp-mute-duration", 300);
            tempMutedPlayers.put(reporterName, System.currentTimeMillis() + (muteDuration * 1000L));
            VersionUtils.sendMessage(reporter, LanguageManager.getMessage("abuse-temp-mute")
                .replace("[DURATION]", String.valueOf(muteDuration)));
        } else if (hourlyCount >= warningThreshold) {
            // Предупреждение
            VersionUtils.sendMessage(reporter, LanguageManager.getMessage("abuse-warning"));
        }
    }
    
    /**
     * Проверяет, находится ли игрок в временном муте
     */
    private static boolean isTempMuted(String playerName) {
        Long muteTime = tempMutedPlayers.get(playerName);
        if (muteTime == null) return false;
        
        if (System.currentTimeMillis() > muteTime) {
            tempMutedPlayers.remove(playerName);
            return false;
        }
        
        return true;
    }
    
    /**
     * Отмечает жалобу как ложную
     */
    public static void markFalseReport(String reporterName) {
        falseReportCounts.merge(reporterName, 1, Integer::sum);
        saveAbuseData();
    }
    
    /**
     * Проверяет, имеет ли игрок пониженный приоритет
     */
    public static boolean hasLowPriority(String reporterName) {
        int threshold = Main.getInstance().getConfig().getInt("report-limits.false-report-threshold", 3);
        return falseReportCounts.getOrDefault(reporterName, 0) >= threshold;
    }
    
    /**
     * Очищает старые данные (старше 24 часов)
     */
    public static void cleanupOldData() {
        long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        
        for (List<Long> reportTimes : playerReportTimes.values()) {
            reportTimes.removeIf(time -> time < twentyFourHoursAgo);
        }
        
        // Очищаем истекшие муты
        tempMutedPlayers.entrySet().removeIf(entry -> entry.getValue() < System.currentTimeMillis());
        
        saveAbuseData();
    }
    
    /**
     * Сохраняет данные о злоупотреблениях
     */
    private static void saveAbuseData() {
        try {
            // Сохраняем время жалоб
            for (Map.Entry<String, List<Long>> entry : playerReportTimes.entrySet()) {
                abuseConfig.set("report-times." + entry.getKey(), entry.getValue());
            }
            
            // Сохраняем жалобы на игроков
            for (Map.Entry<String, Map<String, Integer>> entry : playerTargetReports.entrySet()) {
                for (Map.Entry<String, Integer> targetEntry : entry.getValue().entrySet()) {
                    abuseConfig.set("target-reports." + entry.getKey() + "." + targetEntry.getKey(), 
                        targetEntry.getValue());
                }
            }
            
            // Сохраняем количество ложных жалоб
            for (Map.Entry<String, Integer> entry : falseReportCounts.entrySet()) {
                abuseConfig.set("false-reports." + entry.getKey(), entry.getValue());
            }
            
            // Сохраняем временные муты
            for (Map.Entry<String, Long> entry : tempMutedPlayers.entrySet()) {
                abuseConfig.set("temp-mutes." + entry.getKey(), entry.getValue());
            }
            
            abuseConfig.save(abuseFile);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe("Could not save abuse data: " + e.getMessage());
        }
    }
    
    /**
     * Загружает данные о злоупотреблениях
     */
    private static void loadAbuseData() {
        // Загружаем время жалоб
        if (abuseConfig.contains("report-times")) {
            for (String playerName : abuseConfig.getConfigurationSection("report-times").getKeys(false)) {
                List<Long> times = abuseConfig.getLongList("report-times." + playerName);
                playerReportTimes.put(playerName, new ArrayList<>(times));
            }
        }
        
        // Загружаем жалобы на игроков
        if (abuseConfig.contains("target-reports")) {
            for (String reporterName : abuseConfig.getConfigurationSection("target-reports").getKeys(false)) {
                Map<String, Integer> targets = new HashMap<>();
                for (String targetName : abuseConfig.getConfigurationSection("target-reports." + reporterName).getKeys(false)) {
                    targets.put(targetName, abuseConfig.getInt("target-reports." + reporterName + "." + targetName));
                }
                playerTargetReports.put(reporterName, targets);
            }
        }
        
        // Загружаем количество ложных жалоб
        if (abuseConfig.contains("false-reports")) {
            for (String playerName : abuseConfig.getConfigurationSection("false-reports").getKeys(false)) {
                falseReportCounts.put(playerName, abuseConfig.getInt("false-reports." + playerName));
            }
        }
        
        // Загружаем временные муты
        if (abuseConfig.contains("temp-mutes")) {
            for (String playerName : abuseConfig.getConfigurationSection("temp-mutes").getKeys(false)) {
                tempMutedPlayers.put(playerName, abuseConfig.getLong("temp-mutes." + playerName));
            }
        }
    }
}
