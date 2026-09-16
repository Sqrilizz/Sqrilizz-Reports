package dev.sqrilizz.reports.server

import dev.sqrilizz.reports.core.NetworkProtocol
import dev.sqrilizz.reports.core.ReportRepository
import dev.sqrilizz.reports.core.ReportService
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.util.logging.Level

class ReportsPlugin : JavaPlugin() {
    lateinit var reports: ReportService
        private set
    lateinit var repository: ReportRepository
        private set
    lateinit var queueGui: QueueGui
        private set
    lateinit var notifications: NotificationService
        private set
    private var restApi: RestApi? = null

    override fun onEnable() {
        saveDefaultConfig()
        SchedulerHelper.init()
        ColorManager.init(this)
        LanguageManager.init(this)

        repository = ReportRepositoryFactory.create(this)
        reports = ReportService(
            repository,
            Duration.ofSeconds(config.getLong("reports.cooldown-seconds", 45)),
            Duration.ofSeconds(config.getLong("reports.duplicate-window-seconds", 900)),
            config.getInt("reports.max-per-hour", 8),
            config.getInt("reports.max-per-target", 2)
        )

        val migrated = V9Migrator.migrate(this)
        queueGui = QueueGui(this)
        notifications = NotificationService(this)
        restApi = RestApi(this).also { it.start() }

        val reportCommand = ReportCommand(this)
        getCommand("report")?.apply {
            setExecutor(reportCommand)
            tabCompleter = reportCommand
        }

        val reportsCommand = ReportsCommand(this)
        getCommand("reports")?.apply {
            setExecutor(reportsCommand)
            tabCompleter = reportsCommand
        }

        val bugReportCommand = BugReportCommand(this)
        getCommand("bugreport")?.apply {
            setExecutor(bugReportCommand)
            tabCompleter = bugReportCommand
        }

        server.pluginManager.registerEvents(queueGui, this)
        server.messenger.registerOutgoingPluginChannel(this, NetworkProtocol.CHANNEL)
        server.messenger.registerIncomingPluginChannel(this, NetworkProtocol.CHANNEL) { _, player, bytes ->
            val message = NetworkProtocol.decode(bytes)
            if (message is NetworkProtocol.OpenReport && message.moderatorId == player.uniqueId) {
                runOnPlayer(player) { queueGui.openDetails(player, message.reportId) }
            }
        }
        logger.info("Sqrilizz-Reports v10 enabled on ${platformName()} [${SchedulerHelper.getPlatform()}] as ${serverId()} using ${config.getString("storage.type", "sqlite")}; migrated=$migrated")
    }

    override fun onDisable() {
        restApi?.close()
        if (::repository.isInitialized) repository.close()
    }

    fun reloadAll() {
        reloadConfig()
        ColorManager.init(this)
        LanguageManager.reload(this)
        reports.clearCooldowns()
    }

    fun serverId(): String = config.getString("server-id", "local")!!.trim().ifBlank { "local" }

    fun networkEnabled(): Boolean = config.getBoolean("network.enabled", false)

    fun requestTransfer(player: Player, serverId: String, reportId: Long) {
        if (!networkEnabled()) return
        player.sendPluginMessage(this, NetworkProtocol.CHANNEL, NetworkProtocol.encodeTransfer(NetworkProtocol.TransferRequest(player.uniqueId, serverId, reportId)))
    }

    fun runAsync(task: () -> Unit) {
        SchedulerHelper.runAsync(this, task)
    }

    fun runOnPlayer(player: Player, task: () -> Unit) {
        SchedulerHelper.runOnPlayer(this, player, task)
    }

    private fun platformName(): String = server.name.ifBlank { "Paper" }

    fun logFailure(message: String, error: Throwable) {
        logger.log(Level.WARNING, message, error)
    }
}
