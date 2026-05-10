package dev.sqrilizz.reports

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    private var database: DatabaseManager? = null
    private var cache: CacheManager? = null
    private var telegram: TelegramManager? = null
    private var webhook: WebhookManager? = null
    var majorVersion: Int = 0
        private set
    var serverVersion: String = "unknown"
        private set

    override fun onEnable() {
        instance = this

        saveDefaultConfig()
        detectServerVersion()

        initializeDatabase()
        initializeCache()
        initializeNotifications()
        registerCommands()

        logger.info("Sqrilizz-Reports Lite v${description.version} enabled (MC $serverVersion)")
        logger.info("Simple. Fast. Reliable.")
    }

    override fun onDisable() {
        database?.close()
        cache?.cleanup()
        logger.info("Sqrilizz-Reports Lite disabled.")
    }

    private fun detectServerVersion() {
        try {
            val version = server.version
            val m = Regex("""MC:\s*(\d+)\.(\d+)""").find(version)
            if (m != null) {
                val first = m.groupValues[1].toInt()
                val second = m.groupValues[2].toInt()
                if (first > 1) {
                    majorVersion = first
                    serverVersion = "$first.$second"
                } else {
                    majorVersion = second
                    serverVersion = "$first.$second"
                }
            }
        } catch (e: Exception) {
            logger.warning("Failed to detect server version: ${e.message}")
        }
        logger.info("Detected MC version: $serverVersion (major=$majorVersion)")
    }

    private fun initializeDatabase() {
        try {
            database = DatabaseManager(this).also { it.initialize() }
        } catch (e: Exception) {
            logger.severe("Failed to initialize database: ${e.message}")
            server.pluginManager.disablePlugin(this)
        }
    }

    private fun initializeCache() {
        cache = CacheManager().also { it.initialize() }
    }

    private fun initializeNotifications() {
        if (config.getBoolean("telegram.enabled", false)) {
            telegram = TelegramManager(this).also { it.initialize() }
        }
        if (config.getBoolean("webhook.enabled", false)) {
            webhook = WebhookManager(this)
        }
    }

    private fun registerCommands() {
        val reportCmd = ReportCommand(this)
        getCommand("report")?.setExecutor(reportCmd)
        getCommand("report")?.tabCompleter = reportCmd

        val reportsCmd = ReportsCommand(this)
        getCommand("reports")?.setExecutor(reportsCmd)
        getCommand("reports")?.tabCompleter = reportsCmd
    }

    fun getDatabaseManager(): DatabaseManager? = database
    fun getCache(): CacheManager? = cache
    fun getTelegram(): TelegramManager? = telegram
    fun getWebhook(): WebhookManager? = webhook

    fun runAsync(task: Runnable) {
        server.scheduler.runTaskAsynchronously(this, task)
    }

    companion object {
        @JvmStatic
        var instance: Main? = null
            private set
    }
}
