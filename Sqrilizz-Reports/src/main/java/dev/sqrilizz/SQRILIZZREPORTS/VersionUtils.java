package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class VersionUtils {
    
    /**
     * Get player's display name with version compatibility and color code cleaning
     */
    public static String getPlayerDisplayName(Player player) {
        if (Main.isVersionAtLeast(13)) {
            String displayName = player.getDisplayName();
            return NameUtils.cleanPlayerName(displayName);
        } else {
            String name = player.getName();
            return NameUtils.cleanPlayerName(name);
        }
    }
    
    /**
     * Get player's clean name (without color codes)
     */
    public static String getPlayerCleanName(Player player) {
        return NameUtils.cleanPlayerName(player.getName());
    }
    
    /**
     * Get player's UUID with version compatibility
     */
    public static UUID getPlayerUUID(Player player) {
        if (Main.isVersionAtLeast(7)) {
            return player.getUniqueId();
        } else {
            // For very old versions, generate UUID from name
            return UUID.nameUUIDFromBytes(player.getName().getBytes());
        }
    }
    
    /**
     * Set item display name with version compatibility
     */
    public static void setItemDisplayName(ItemStack item, String name) {
        if (item == null) return;
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (Main.isVersionAtLeast(13)) {
                meta.setDisplayName(name);
            } else {
                // For older versions, use reflection
                try {
                    Method setDisplayName = meta.getClass().getMethod("setDisplayName", String.class);
                    setDisplayName.invoke(meta, name);
                } catch (Exception e) {
                    // Fallback to name
                    try {
                        Method setName = meta.getClass().getMethod("setName", String.class);
                        setName.invoke(meta, name);
                    } catch (Exception ex) {
                        // Ignore if not supported
                    }
                }
            }
            item.setItemMeta(meta);
        }
    }
    
    /**
     * Get server TPS with version compatibility
     */
    public static double[] getTPS() {
        try {
            if (Main.isVersionAtLeast(16)) {
                return Bukkit.getTPS();
            } else {
                // For older versions, return default values
                return new double[]{20.0, 20.0, 20.0};
            }
        } catch (Exception e) {
            return new double[]{20.0, 20.0, 20.0};
        }
    }
    
    /**
     * Check if player has permission with version compatibility
     */
    public static boolean hasPermission(Player player, String permission) {
        if (Main.isVersionAtLeast(8)) {
            return player.hasPermission(permission);
        } else {
            // For very old versions, check if player is op
            return player.isOp();
        }
    }
    
    /**
     * Check if CommandSender has permission with version compatibility
     * Supports both Player and ConsoleCommandSender
     */
    public static boolean hasPermission(org.bukkit.command.CommandSender sender, String permission) {
        if (sender instanceof Player) {
            return hasPermission((Player) sender, permission);
        } else {
            // Console always has permission
            return true;
        }
    }
    
    /**
     * Send message to player with version compatibility
     */
    public static void sendMessage(Player player, String message) {
        if (player != null && player.isOnline()) {
            player.sendMessage(message);
        }
    }

    public static Collection<? extends Player> getOnlinePlayers() {
        try {
            if (Main.isVersionAtLeast(8)) {
                return Bukkit.getOnlinePlayers();
            } else {
                // For very old versions, use reflection
                Method getOnlinePlayers = Bukkit.class.getMethod("getOnlinePlayers");
                Object[] players = (Object[]) getOnlinePlayers.invoke(null);
                return Arrays.asList((Player[]) players);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Get online players count with version compatibility
     */
    public static int getOnlinePlayersCount() {
        try {
            if (Main.isVersionAtLeast(8)) {
                return Bukkit.getOnlinePlayers().size();
            } else {
                // For very old versions, use reflection
                Method getOnlinePlayers = Bukkit.class.getMethod("getOnlinePlayers");
                Object[] players = (Object[]) getOnlinePlayers.invoke(null);
                return players.length;
            }
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Check if server is running Folia
     */
    public static boolean isFoliaServer() {
        return Main.isFolia();
    }
    
    /**
     * Get server version string
     */
    public static String getServerVersion() {
        return Main.getServerVersion();
    }
    
    /**
     * Check if version is at least specified version
     */
    public static boolean isVersionAtLeast(int version) {
        return Main.isVersionAtLeast(version);
    }
    
    /**
     * Check if version is between specified versions
     */
    public static boolean isVersionBetween(int minVersion, int maxVersion) {
        return Main.isVersionBetween(minVersion, maxVersion);
    }
    
    /**
     * Get major version number
     */
    public static int getMajorVersion() {
        return Main.getMajorVersion();
    }
}
