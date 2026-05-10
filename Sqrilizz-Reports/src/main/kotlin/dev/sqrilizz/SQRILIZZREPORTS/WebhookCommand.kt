package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WebhookCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(LanguageManager.getMessage("player-only"))
            return true
        }

        if (!VersionUtils.hasPermission(sender, "reports.admin")) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("no-permission"))
            return true
        }

        if (args.isEmpty()) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("webhook-usage"))
            return true
        }

        when (args[0].lowercase()) {
            "set" -> {
                if (args.size < 2) {
                    VersionUtils.sendMessage(sender, LanguageManager.getMessage("webhook-usage"))
                    return true
                }
                DiscordWebhookManager.setWebhookUrl(args[1])
                VersionUtils.sendMessage(sender, LanguageManager.getMessage("webhook-set"))
            }
            "remove" -> {
                DiscordWebhookManager.setWebhookUrl("")
                VersionUtils.sendMessage(sender, LanguageManager.getMessage("webhook-removed"))
            }
            else -> {
                VersionUtils.sendMessage(sender, LanguageManager.getMessage("webhook-usage"))
            }
        }

        return true
    }
}
