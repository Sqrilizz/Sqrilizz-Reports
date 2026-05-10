package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TelegramCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(LanguageManager.getMessage("player-only"))
            return true
        }

        if (!VersionUtils.hasPermission(sender, "reports.telegram")) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("no-permission"))
            return true
        }

        if (args.size != 2) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("telegram-usage"))
            return true
        }

        when (args[0].lowercase()) {
            "token" -> {
                Main.getInstance().config.set("telegram.token", args[1])
                Main.getInstance().saveConfig()
                TelegramManager.initialize()
                VersionUtils.sendMessage(sender, LanguageManager.getMessage("telegram-token-set"))
            }
            "chat" -> {
                Main.getInstance().config.set("telegram.chat_id", args[1])
                Main.getInstance().saveConfig()
                TelegramManager.initialize()
                VersionUtils.sendMessage(sender, LanguageManager.getMessage("telegram-chat-set"))
            }
            else -> {
                VersionUtils.sendMessage(sender, LanguageManager.getMessage("telegram-usage"))
            }
        }

        return true
    }
}
