package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID

object VersionUtils {

    @JvmStatic
    fun getPlayerDisplayName(player: Player): String {
        val displayName = if (Main.isVersionAtLeast(13)) player.displayName else player.name
        return NameUtils.cleanPlayerName(displayName)
    }

    @JvmStatic
    fun getPlayerCleanName(player: Player): String =
        NameUtils.cleanPlayerName(player.name)

    @JvmStatic
    fun getPlayerUUID(player: Player): UUID =
        if (Main.isVersionAtLeast(7)) player.uniqueId
        else UUID.nameUUIDFromBytes(player.name.toByteArray())

    @JvmStatic
    fun setItemDisplayName(item: ItemStack?, name: String) {
        if (item == null) return
        val meta = item.itemMeta ?: return

        if (Main.isVersionAtLeast(13)) {
            meta.setDisplayName(name)
        } else {
            try {
                meta.javaClass.getMethod("setDisplayName", String::class.java).invoke(meta, name)
            } catch (_: Exception) {
                try {
                    meta.javaClass.getMethod("setName", String::class.java).invoke(meta, name)
                } catch (_: Exception) { }
            }
        }
        item.itemMeta = meta
    }

    @JvmStatic
    fun getTPS(): DoubleArray =
        try {
            if (Main.isVersionAtLeast(16)) Bukkit.getTPS()
            else doubleArrayOf(20.0, 20.0, 20.0)
        } catch (_: Exception) {
            doubleArrayOf(20.0, 20.0, 20.0)
        }

    @JvmStatic
    fun hasPermission(player: Player, permission: String): Boolean =
        if (Main.isVersionAtLeast(8)) player.hasPermission(permission)
        else player.isOp

    @JvmStatic
    fun hasPermission(sender: CommandSender, permission: String): Boolean =
        if (sender is Player) hasPermission(sender, permission)
        else true

    @JvmStatic
    fun sendMessage(player: Player?, message: String) {
        if (player != null && player.isOnline) {
            player.sendMessage(message)
        }
    }

    @JvmStatic
    fun getOnlinePlayers(): Collection<Player> =
        try {
            if (Main.isVersionAtLeast(8)) {
                @Suppress("UNCHECKED_CAST")
                Bukkit.getOnlinePlayers() as Collection<Player>
            } else {
                val method = Bukkit::class.java.getMethod("getOnlinePlayers")
                val players = method.invoke(null) as Array<*>
                players.filterIsInstance<Player>()
            }
        } catch (_: Exception) {
            emptyList()
        }

    @JvmStatic
    fun getOnlinePlayersCount(): Int =
        try {
            if (Main.isVersionAtLeast(8)) Bukkit.getOnlinePlayers().size
            else {
                val method = Bukkit::class.java.getMethod("getOnlinePlayers")
                val players = method.invoke(null) as Array<*>
                players.size
            }
        } catch (_: Exception) {
            0
        }
}
