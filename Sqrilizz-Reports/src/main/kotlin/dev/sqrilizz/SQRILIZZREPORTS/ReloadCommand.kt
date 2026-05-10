package dev.sqrilizz.SQRILIZZREPORTS

import dev.sqrilizz.SQRILIZZREPORTS.api.AuthManager
import dev.sqrilizz.SQRILIZZREPORTS.api.RESTServer
import dev.sqrilizz.SQRILIZZREPORTS.db.DatabaseManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ReloadCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(LanguageManager.getMessage("player-only"))
            return true
        }

        if (!VersionUtils.hasPermission(sender, "reports.reload")) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("no-permission"))
            return true
        }

        try {
            Main.getInstance().reloadConfig()

            ColorManager.initialize()
            LanguageManager.initialize()
            TelegramManager.initialize()
            DiscordWebhookManager.initialize()
            AntiAbuseManager.initialize()

            AuthManager.initialize()
            RESTServer.shutdown()
            RESTServer.initialize()

            DatabaseManager.close()
            DatabaseManager.initialize()
            DatabaseManager.replaceAllReports(ReportManager.getReports())

            VersionUtils.sendMessage(sender, LanguageManager.getMessage("config-reloaded"))
            VersionUtils.sendMessage(sender, ColorManager.colorize(
                "{info}\uD83D\uDD27 Настройки очистки имен: ${NameUtils.getCleaningStats()}"))
            VersionUtils.sendMessage(sender, ColorManager.colorize(
                "{success}\uD83C\uDFA8 Дизайн: ${if (ColorManager.isHexSupported()) "Hex цвета" else "Legacy цвета"}"))
        } catch (e: Exception) {
            VersionUtils.sendMessage(sender, ColorManager.colorize(
                "{error}\u274c Ошибка при перезагрузке конфигурации: ${e.message}"))
            Main.getInstance().logger.severe("Failed to reload configuration: ${e.message}")
            e.printStackTrace()
        }

        return true
    }
}
