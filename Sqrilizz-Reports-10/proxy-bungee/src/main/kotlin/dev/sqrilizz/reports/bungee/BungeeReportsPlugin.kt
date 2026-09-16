package dev.sqrilizz.reports.bungee

import dev.sqrilizz.reports.core.NetworkProtocol
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler

class BungeeReportsPlugin : Plugin(), Listener {
    override fun onEnable() {
        proxy.pluginManager.registerListener(this, this)
        proxy.registerChannel(NetworkProtocol.CHANNEL)
    }

    override fun onDisable() {
        proxy.unregisterChannel(NetworkProtocol.CHANNEL)
    }

    @EventHandler
    fun onPluginMessage(event: PluginMessageEvent) {
        if (event.tag != NetworkProtocol.CHANNEL) return
        val request = NetworkProtocol.decode(event.data) as? NetworkProtocol.TransferRequest ?: return
        val player = ProxyServer.getInstance().getPlayer(request.moderatorId) ?: return
        val destination = ProxyServer.getInstance().getServerInfo(request.serverId) ?: run {
            logger.warning("Sqrilizz-Reports could not find target server ${request.serverId}")
            return
        }
        event.isCancelled = true
        player.connect(destination) { success, _ ->
            if (!success) {
                logger.warning("Sqrilizz-Reports could not transfer ${player.name} to ${request.serverId}")
                return@connect
            }
            destination.sendData(NetworkProtocol.CHANNEL, NetworkProtocol.encodeOpen(NetworkProtocol.OpenReport(request.moderatorId, request.reportId)), false)
        }
    }
}
