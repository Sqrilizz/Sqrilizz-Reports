package dev.sqrilizz.reports

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ReportCommand(private val plugin: Main) : CommandExecutor, TabCompleter {

    private val reportManager = ReportManager(plugin)
    private val cooldowns = CooldownManager()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}Only players can create reports.")
            return true
        }

        if (!sender.hasPermission("reports.use")) {
            sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
            return true
        }

        if (args.size < 2) {
            sender.sendMessage("${ChatColor.RED}Usage: /report <player> <reason>")
            return true
        }

        val targetName = args[0]
        val reason = args.drop(1).joinToString(" ")

        if (reason.length > 200) {
            sender.sendMessage("${ChatColor.RED}Reason is too long! Maximum 200 characters.")
            return true
        }

        if (targetName.equals(sender.name, ignoreCase = true)) {
            sender.sendMessage("${ChatColor.RED}You cannot report yourself!")
            return true
        }

        val cooldownSeconds = plugin.config.getInt("cooldown", 30)
        val remaining = cooldowns.getRemainingSeconds(sender.uniqueId, cooldownSeconds)
        if (remaining > 0) {
            sender.sendMessage("${ChatColor.RED}Please wait ${remaining}s before creating another report.")
            return true
        }

        cooldowns.setCooldown(sender.uniqueId)

        plugin.runAsync {
            val success = reportManager.createReport(sender, targetName, reason)

            plugin.server.scheduler.runTask(plugin, Runnable {
                if (success) {
                    sender.sendMessage("${ChatColor.GREEN}Report submitted successfully!")
                    sender.sendMessage("${ChatColor.GRAY}Target: ${ChatColor.WHITE}$targetName")
                    sender.sendMessage("${ChatColor.GRAY}Reason: ${ChatColor.WHITE}$reason")
                    notifyAdmins(targetName, reason, sender.name)
                } else {
                    sender.sendMessage("${ChatColor.RED}Failed to submit report. Please try again.")
                    cooldowns.removeCooldown(sender.uniqueId)
                }
            })
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, label: String, args: Array<out String>
    ): List<String> {
        if (args.size == 1) {
            val prefix = args[0].lowercase()
            return plugin.server.onlinePlayers
                .map { it.name }
                .filter { it.lowercase().startsWith(prefix) }
                .filter { !it.equals(sender.name, ignoreCase = true) }
        }
        return emptyList()
    }

    private fun notifyAdmins(target: String, reason: String, reporter: String) {
        val message = "${ChatColor.YELLOW}[REPORT] ${ChatColor.WHITE}$reporter" +
            "${ChatColor.GRAY} reported ${ChatColor.WHITE}$target" +
            "${ChatColor.GRAY} for: ${ChatColor.WHITE}$reason"

        plugin.server.onlinePlayers
            .filter { it.hasPermission("reports.admin") }
            .forEach { it.sendMessage(message) }
    }
}
