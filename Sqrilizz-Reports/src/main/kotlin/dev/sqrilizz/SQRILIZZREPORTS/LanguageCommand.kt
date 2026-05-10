package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LanguageCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(LanguageManager.getMessage("player-only"))
            return true
        }

        if (!VersionUtils.hasPermission(sender, "reports.language")) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("no-permission"))
            return true
        }

        if (args.size != 1) {
            VersionUtils.sendMessage(sender, ColorManager.colorize("{error}\u274c Использование: {secondary}/report-language <ru|en|ar>"))
            return true
        }

        val language = args[0].lowercase()
        if (language !in listOf("ru", "en", "ar")) {
            VersionUtils.sendMessage(sender, ColorManager.colorize("{error}\u274c Поддерживаемые языки: {accent}ru{secondary}, {accent}en{secondary}, {accent}ar"))
            return true
        }

        LanguageManager.setLanguage(language)
        val languageName = when (language) {
            "ru" -> "Русский (Russian)"
            "en" -> "English"
            "ar" -> "العربية (Arabic)"
            else -> language.uppercase()
        }
        VersionUtils.sendMessage(sender, ColorManager.colorize("{success}\uD83C\uDF0D Язык сервера изменен на {accent}$languageName"))
        return true
    }
}
