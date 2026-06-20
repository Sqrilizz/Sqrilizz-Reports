package dev.sqrilizz.SQRILIZZREPORTS;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                    Method setDisplayName = meta
                        .getClass()
                        .getMethod("setDisplayName", String.class);
                    setDisplayName.invoke(meta, name);
                } catch (Exception e) {
                    // Fallback to name
                    try {
                        Method setName = meta
                            .getClass()
                            .getMethod("setName", String.class);
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
     * Resolve renamed materials across modern and legacy Bukkit versions.
     */
    public static Material getMaterial(String modernName, String legacyName) {
        Material material = null;
        if (modernName != null) {
            material = Material.matchMaterial(modernName);
        }
        if (material == null && legacyName != null) {
            material = Material.matchMaterial(legacyName);
        }
        if (material == null) {
            material = Material.STONE;
        }
        return material;
    }

    /**
     * Create an item using modern material names with a legacy fallback + data value.
     */
    public static ItemStack createItem(
        String modernName,
        String legacyName,
        short legacyData
    ) {
        Material material = getMaterial(modernName, legacyName);
        if (
            legacyName != null &&
            material.name().equals(legacyName) &&
            legacyData > 0
        ) {
            return new ItemStack(material, 1, legacyData);
        }
        return new ItemStack(material);
    }

    /**
     * Set skull owner on both modern (setOwningPlayer) and legacy (setOwner) APIs.
     */
    public static void setSkullOwner(ItemMeta meta, String playerName) {
        if (meta == null || playerName == null || playerName.isEmpty()) return;

        try {
            Method setOwningPlayer = meta
                .getClass()
                .getMethod("setOwningPlayer", OfflinePlayer.class);
            setOwningPlayer.invoke(meta, Bukkit.getOfflinePlayer(playerName));
            return;
        } catch (Exception ignored) {
            // Legacy fallback below.
        }

        try {
            Method setOwner = meta
                .getClass()
                .getMethod("setOwner", String.class);
            setOwner.invoke(meta, playerName);
        } catch (Exception ignored) {
            // Skull owners are cosmetic; ignore unsupported APIs.
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
                return new double[] { 20.0, 20.0, 20.0 };
            }
        } catch (Exception e) {
            return new double[] { 20.0, 20.0, 20.0 };
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
    public static boolean hasPermission(
        org.bukkit.command.CommandSender sender,
        String permission
    ) {
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
                Method getOnlinePlayers = Bukkit.class.getMethod(
                    "getOnlinePlayers"
                );
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
                Method getOnlinePlayers = Bukkit.class.getMethod(
                    "getOnlinePlayers"
                );
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
