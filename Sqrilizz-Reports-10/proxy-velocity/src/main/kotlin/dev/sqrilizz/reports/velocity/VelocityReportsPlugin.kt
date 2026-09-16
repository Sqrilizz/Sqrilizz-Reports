package dev.sqrilizz.reports.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.sqrilizz.reports.core.NetworkProtocol
import org.slf4j.Logger

@Plugin(id = "sqrilizzreports", name = "Sqrilizz-Reports", version = "10.0.0", authors = ["Sqrilizz"])
class VelocityReportsPlugin @Inject constructor(
    private val proxy: ProxyServer,
    private val logger: Logger
) {
    private val channel = MinecraftChannelIdentifier.from(NetworkProtocol.CHANNEL)

    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        if (event.identifier != channel) return
        val request = NetworkProtocol.decode(event.data) as? NetworkProtocol.TransferRequest ?: return
        val player = proxy.getPlayer(request.moderatorId).orElse(null) ?: return
        val destination = proxy.getServer(request.serverId).orElse(null) ?: run {
            logger.warn("Sqrilizz-Reports could not find target server {}", request.serverId)
            return
        }
        event.result = PluginMessageEvent.ForwardResult.handled()
        player.createConnectionRequest(destination).connect().thenAccept { result ->
            if (!result.isSuccessful) {
                logger.warn("Sqrilizz-Reports could not transfer {} to {}", player.username, request.serverId)
                return@thenAccept
            }
            destination.sendPluginMessage(channel, NetworkProtocol.encodeOpen(NetworkProtocol.OpenReport(request.moderatorId, request.reportId)))
        }
    }
}
