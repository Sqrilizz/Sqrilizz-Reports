package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ReportCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(LanguageManager.getMessage("player-only"))
            return true
        }

        if (args.size < 2) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("report-usage"))
            return true
        }

        val targetName = args[0]
        val reason = args.drop(1).joinToString(" ")

        if (reason.isEmpty()) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("report-usage"))
            return true
        }

        if (CooldownManager.hasCooldown(VersionUtils.getPlayerUUID(sender))) {
            val remainingTime = CooldownManager.getRemainingTime(VersionUtils.getPlayerUUID(sender))
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("cooldown-message")
                .replace("[COOLDOWN]", remainingTime.toString()))
            return true
        }

        if (!AntiAbuseManager.canReport(sender, targetName)) {
            return true
        }

        if (targetName.equals(sender.name, ignoreCase = true)) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("cannot-report-self"))
            return true
        }

        val targetPlayer = Bukkit.getPlayer(targetName)
        if (targetPlayer == null) {
            val offlinePlayer = Bukkit.getOfflinePlayer(targetName)
            if (!offlinePlayer.hasPlayedBefore()) {
                VersionUtils.sendMessage(sender, LanguageManager.getMessage("player-not-found")
                    .replace("[PLAYER]", targetName))
                return true
            }
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("report-offline")
                .replace("[PLAYER]", targetName))
            return true
        }

        ReportManager.addReport(sender, targetPlayer, reason)
        AntiAbuseManager.recordReport(sender, VersionUtils.getPlayerCleanName(targetPlayer))
        CooldownManager.setCooldown(VersionUtils.getPlayerUUID(sender))

        VersionUtils.sendMessage(sender, LanguageManager.getMessage("report-success")
            .replace("[PLAYER]", VersionUtils.getPlayerDisplayName(targetPlayer))
            .replace("[REASON]", reason))

        VersionUtils.sendMessage(targetPlayer, LanguageManager.getMessage("report-received")
            .replace("[PLAYER]", VersionUtils.getPlayerDisplayName(sender)))

        return true
    }
}
