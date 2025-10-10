package dev.sqrilizz.SQRILIZZREPORTS;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    public static boolean hasCooldown(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return false;
        }
        return System.currentTimeMillis() < cooldowns.get(playerUUID);
    }

    public static void setCooldown(UUID playerUUID) {
        int cooldownTime = Main.getInstance().getConfig().getInt("cooldown", 230);
        cooldowns.put(playerUUID, System.currentTimeMillis() + (cooldownTime * 1000L));
    }

    public static long getRemainingTime(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return 0;
        }
        long remaining = cooldowns.get(playerUUID) - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }
    
    // Legacy methods for backward compatibility
    public static boolean hasCooldown(String playerName) {
        // For very old versions without UUID support
        return hasCooldown(UUID.nameUUIDFromBytes(playerName.getBytes()));
    }

    public static void setCooldown(String playerName) {
        // For very old versions without UUID support
        setCooldown(UUID.nameUUIDFromBytes(playerName.getBytes()));
    }

    public static long getRemainingTime(String playerName) {
        // For very old versions without UUID support
        return getRemainingTime(UUID.nameUUIDFromBytes(playerName.getBytes()));
    }
} 