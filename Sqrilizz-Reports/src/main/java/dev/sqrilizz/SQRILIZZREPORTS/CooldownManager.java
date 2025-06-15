package dev.sqrilizz.SQRILIZZREPORTS;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {
    private static final Map<String, Long> cooldowns = new HashMap<>();

    public static boolean hasCooldown(String playerName) {
        if (!cooldowns.containsKey(playerName)) {
            return false;
        }
        return System.currentTimeMillis() < cooldowns.get(playerName);
    }

    public static void setCooldown(String playerName) {
        int cooldownTime = Main.getInstance().getConfig().getInt("cooldown", 230);
        cooldowns.put(playerName, System.currentTimeMillis() + (cooldownTime * 1000L));
    }

    public static long getRemainingTime(String playerName) {
        if (!cooldowns.containsKey(playerName)) {
            return 0;
        }
        long remaining = cooldowns.get(playerName) - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }
} 