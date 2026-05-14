package dev.sqrilizz.SQRILIZZREPORTS.punishment;

import dev.sqrilizz.SQRILIZZREPORTS.DebugManager;
import dev.sqrilizz.SQRILIZZREPORTS.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Универсальный менеджер наказаний
 * Поддерживает: LiteBans, AdvancedBan, BanManager, Bukkit
 */
public class PunishmentManager {
    
    private static PunishmentSystem activePunishmentSystem = PunishmentSystem.BUKKIT;
    
    public enum PunishmentSystem {
        LITEBANS,
        ADVANCEDBAN,
        BANMANAGER,
        BUKKIT
    }
    
    public enum PunishmentType {
        WARN,
        KICK,
        MUTE,
        BAN,
        TEMPBAN,
        TEMPMUTE
    }
    
    /**
     * Инициализация системы наказаний
     */
    public static void initialize() {
        String forceSystem = Main.getInstance().getConfig().getString("punishment.force-system", "none");
        boolean autoDetect = Main.getInstance().getConfig().getBoolean("punishment.auto-detect", true);
        
        // Если указана принудительная система — используем её
        if (forceSystem != null && !forceSystem.equalsIgnoreCase("none")) {
            switch (forceSystem.toLowerCase()) {
                case "litebans":
                    activePunishmentSystem = PunishmentSystem.LITEBANS;
                    Main.getInstance().getLogger().info("[PUNISHMENT] Forced to use LiteBans");
                    return;
                case "advancedban":
                    activePunishmentSystem = PunishmentSystem.ADVANCEDBAN;
                    Main.getInstance().getLogger().info("[PUNISHMENT] Forced to use AdvancedBan");
                    return;
                case "banmanager":
                    activePunishmentSystem = PunishmentSystem.BANMANAGER;
                    Main.getInstance().getLogger().info("[PUNISHMENT] Forced to use BanManager");
                    return;
                case "bukkit":
                    activePunishmentSystem = PunishmentSystem.BUKKIT;
                    Main.getInstance().getLogger().info("[PUNISHMENT] Forced to use Bukkit");
                    return;
                default:
                    Main.getInstance().getLogger().warning("[PUNISHMENT] Unknown force-system: " + forceSystem + ", falling back to auto-detect");
                    break;
            }
        }
        
        // Авто-определение доступной системы наказаний
        if (autoDetect) {
            if (Bukkit.getPluginManager().getPlugin("LiteBans") != null) {
                activePunishmentSystem = PunishmentSystem.LITEBANS;
                Main.getInstance().getLogger().info("[PUNISHMENT] Auto-detected LiteBans");
            } else if (Bukkit.getPluginManager().getPlugin("AdvancedBan") != null) {
                activePunishmentSystem = PunishmentSystem.ADVANCEDBAN;
                Main.getInstance().getLogger().info("[PUNISHMENT] Auto-detected AdvancedBan");
            } else if (Bukkit.getPluginManager().getPlugin("BanManager") != null) {
                activePunishmentSystem = PunishmentSystem.BANMANAGER;
                Main.getInstance().getLogger().info("[PUNISHMENT] Auto-detected BanManager");
            } else {
                activePunishmentSystem = PunishmentSystem.BUKKIT;
                Main.getInstance().getLogger().info("[PUNISHMENT] No punishment plugin found, using Bukkit");
            }
        } else {
            activePunishmentSystem = PunishmentSystem.BUKKIT;
            Main.getInstance().getLogger().info("[PUNISHMENT] Auto-detect disabled, using Bukkit");
        }
    }
    
    /**
     * Получить активную систему наказаний
     */
    public static PunishmentSystem getActivePunishmentSystem() {
        return activePunishmentSystem;
    }
    
    /**
     * Выдать предупреждение
     */
    public static boolean warn(String playerName, String reason, String admin) {
        DebugManager.log("PUNISHMENT", "warn: player=" + playerName + " admin=" + admin + " system=" + activePunishmentSystem);
        switch (activePunishmentSystem) {
            case LITEBANS:
                return executeLiteBansCommand("warn", playerName, reason, admin, 0);
            case ADVANCEDBAN:
                return executeAdvancedBanCommand("warn", playerName, reason, admin, 0);
            case BANMANAGER:
                return executeBanManagerCommand("warn", playerName, reason, admin, 0);
            case BUKKIT:
            default:
                return executeBukkitWarn(playerName, reason, admin);
        }
    }
    
    /**
     * Кикнуть игрока
     */
    public static boolean kick(String playerName, String reason, String admin) {
        DebugManager.log("PUNISHMENT", "kick: player=" + playerName + " admin=" + admin + " system=" + activePunishmentSystem);
        switch (activePunishmentSystem) {
            case LITEBANS:
                return executeLiteBansCommand("kick", playerName, reason, admin, 0);
            case ADVANCEDBAN:
                return executeAdvancedBanCommand("kick", playerName, reason, admin, 0);
            case BANMANAGER:
                return executeBanManagerCommand("kick", playerName, reason, admin, 0);
            case BUKKIT:
            default:
                return executeBukkitKick(playerName, reason);
        }
    }
    
    /**
     * Замутить игрока
     */
    public static boolean mute(String playerName, String reason, String admin, long duration, TimeUnit unit) {
        DebugManager.log("PUNISHMENT", "mute: player=" + playerName + " admin=" + admin + " duration=" + duration + " unit=" + unit + " system=" + activePunishmentSystem);
        long seconds = unit.toSeconds(duration);
        switch (activePunishmentSystem) {
            case LITEBANS:
                return executeLiteBansCommand("mute", playerName, reason, admin, seconds);
            case ADVANCEDBAN:
                return executeAdvancedBanCommand("mute", playerName, reason, admin, seconds);
            case BANMANAGER:
                return executeBanManagerCommand("mute", playerName, reason, admin, seconds);
            case BUKKIT:
            default:
                Main.getInstance().getLogger().warning("[PUNISHMENT] Bukkit doesn't support mute. Use a punishment plugin.");
                return false;
        }
    }
    
    /**
     * Забанить игрока
     */
    public static boolean ban(String playerName, String reason, String admin, long duration, TimeUnit unit) {
        DebugManager.log("PUNISHMENT", "ban: player=" + playerName + " admin=" + admin + " duration=" + duration + " unit=" + unit + " system=" + activePunishmentSystem);
        long seconds = unit.toSeconds(duration);
        switch (activePunishmentSystem) {
            case LITEBANS:
                return executeLiteBansCommand("ban", playerName, reason, admin, seconds);
            case ADVANCEDBAN:
                return executeAdvancedBanCommand("ban", playerName, reason, admin, seconds);
            case BANMANAGER:
                return executeBanManagerCommand("ban", playerName, reason, admin, seconds);
            case BUKKIT:
            default:
                return executeBukkitBan(playerName, reason, seconds);
        }
    }
    
    /**
     * Перманентный бан
     */
    public static boolean banPermanent(String playerName, String reason, String admin) {
        switch (activePunishmentSystem) {
            case LITEBANS:
                return executeLiteBansCommand("ban", playerName, reason, admin, -1);
            case ADVANCEDBAN:
                return executeAdvancedBanCommand("ban", playerName, reason, admin, -1);
            case BANMANAGER:
                return executeBanManagerCommand("ban", playerName, reason, admin, -1);
            case BUKKIT:
            default:
                return executeBukkitBan(playerName, reason, -1);
        }
    }
    
    // ==================== LiteBans ====================
    
    private static boolean executeLiteBansCommand(String type, String playerName, String reason, String admin, long seconds) {
        try {
            String command;
            if (seconds > 0) {
                String duration = formatDuration(seconds);
                command = String.format("%s %s %s %s -s", type, playerName, duration, reason);
            } else if (seconds == -1) {
                command = String.format("%s %s %s -s", type, playerName, reason);
            } else {
                command = String.format("%s %s %s -s", type, playerName, reason);
            }
            
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            return true;
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("[PUNISHMENT] LiteBans command failed: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== AdvancedBan ====================
    
    private static boolean executeAdvancedBanCommand(String type, String playerName, String reason, String admin, long seconds) {
        try {
            String command;
            if (seconds > 0) {
                String duration = formatDuration(seconds);
                command = String.format("%s %s %s %s", type, playerName, duration, reason);
            } else {
                command = String.format("%s %s %s", type, playerName, reason);
            }
            
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            return true;
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("[PUNISHMENT] AdvancedBan command failed: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== BanManager ====================
    
    private static boolean executeBanManagerCommand(String type, String playerName, String reason, String admin, long seconds) {
        try {
            String command;
            if (seconds > 0) {
                String duration = formatDuration(seconds);
                command = String.format("bm%s %s %s %s", type, playerName, duration, reason);
            } else {
                command = String.format("bm%s %s %s", type, playerName, reason);
            }
            
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            return true;
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("[PUNISHMENT] BanManager command failed: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== Bukkit ====================
    
    private static boolean executeBukkitWarn(String playerName, String reason, String admin) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null && player.isOnline()) {
            player.sendMessage("§c§lWARNING");
            player.sendMessage("§7Reason: §f" + reason);
            player.sendMessage("§7Moderator: §f" + admin);
            return true;
        }
        return false;
    }
    
    private static boolean executeBukkitKick(String playerName, String reason) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null && player.isOnline()) {
            player.kickPlayer("§c§lKICKED\n§7Reason: §f" + reason);
            return true;
        }
        return false;
    }
    
    private static boolean executeBukkitBan(String playerName, String reason, long seconds) {
        try {
            Player player = Bukkit.getPlayer(playerName);
            if (seconds > 0) {
                // Временный бан
                java.util.Date expiry = new java.util.Date(System.currentTimeMillis() + (seconds * 1000));
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(playerName, reason, expiry, "Sqrilizz-Reports");
            } else {
                // Перманентный бан
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(playerName, reason, null, "Sqrilizz-Reports");
            }
            
            if (player != null && player.isOnline()) {
                player.kickPlayer("§c§lBANNED\n§7Reason: §f" + reason);
            }
            return true;
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("[PUNISHMENT] Bukkit ban failed: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== Утилиты ====================
    
    /**
     * Форматирование длительности для команд
     * Примеры: 3600s -> 1h, 86400s -> 1d
     */
    private static String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "h";
        } else if (seconds < 2592000) {
            return (seconds / 86400) + "d";
        } else if (seconds < 31536000) {
            return (seconds / 2592000) + "mo";
        } else {
            return (seconds / 31536000) + "y";
        }
    }
    
    /**
     * Получить читаемое название длительности
     */
    public static String getDurationString(long duration, TimeUnit unit) {
        long seconds = unit.toSeconds(duration);
        
        if (seconds < 60) {
            return seconds + " seconds";
        } else if (seconds < 3600) {
            return (seconds / 60) + " minutes";
        } else if (seconds < 86400) {
            return (seconds / 3600) + " hours";
        } else if (seconds < 2592000) {
            return (seconds / 86400) + " days";
        } else if (seconds < 31536000) {
            return (seconds / 2592000) + " months";
        } else {
            return (seconds / 31536000) + " years";
        }
    }
}
