package dev.sqrilizz.reports

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ReportsCommand(private val plugin: Main) : CommandExecutor, TabCompleter {

    private val reportManager = ReportManager(plugin)

    companion object {
        private val DATE_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())

        private val SUB_COMMANDS = listOf("view", "list", "close", "resolve", "delete", "remove", "reload")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("reports.admin")) {
            sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
            return true
        }

        if (args.isEmpty()) {
            showHelp(sender)
            return true
        }

        when (args[0].lowercase()) {
            "view", "list" -> handleView(sender, args)
            "close", "resolve" -> handleResolve(sender, args)
            "delete", "remove" -> handleDelete(sender, args)
            "reload" -> handleReload(sender)
            else -> showHelp(sender)
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, label: String, args: Array<out String>
    ): List<String> {
        if (!sender.hasPermission("reports.admin")) return emptyList()

        return when (args.size) {
            1 -> SUB_COMMANDS.filter { it.startsWith(args[0].lowercase()) }
            2 -> when (args[0].lowercase()) {
                "view", "list" -> plugin.server.onlinePlayers
                    .map { it.name }
                    .filter { it.lowercase().startsWith(args[1].lowercase()) }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }

    private fun handleView(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("${ChatColor.RED}Usage: /reports view <player>")
            return
        }

        val playerName = args[1]

        plugin.runAsync {
            val reports = reportManager.getReports(playerName)

            plugin.server.scheduler.runTask(plugin, Runnable {
                if (reports.isEmpty()) {
                    sender.sendMessage("${ChatColor.YELLOW}No reports found for $playerName")
                    return@Runnable
                }

                sender.sendMessage("${ChatColor.GREEN}=== Reports for $playerName (${reports.size}) ===")

                for (report in reports) {
                    val time = DATE_FORMAT.format(Instant.ofEpochMilli(report.timestamp))
                    val status = if (report.isResolved)
                        "${ChatColor.GREEN}[RESOLVED]"
                    else
                        "${ChatColor.RED}[OPEN]"

                    sender.sendMessage("$status #${report.id} ${ChatColor.GRAY}$time ${ChatColor.WHITE}${report.reason}")
                    sender.sendMessage("${ChatColor.GRAY}  Reporter: ${ChatColor.WHITE}${report.reporterName}")

                    if (report.isResolved) {
                        sender.sendMessage("${ChatColor.GRAY}  Resolved by: ${ChatColor.WHITE}${report.resolver}")
                    }
                }

                sender.sendMessage("${ChatColor.GREEN}=== End of reports ===")
            })
        }
    }

    private fun handleResolve(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("${ChatColor.RED}Usage: /reports close <id>")
            return
        }

        val reportId = args[1].toLongOrNull()
        if (reportId == null) {
            sender.sendMessage("${ChatColor.RED}Invalid report ID!")
            return
        }

        plugin.runAsync {
            val success = reportManager.resolveReport(reportId, sender.name)

            plugin.server.scheduler.runTask(plugin, Runnable {
                if (success) {
                    sender.sendMessage("${ChatColor.GREEN}Report #$reportId has been resolved.")
                } else {
                    sender.sendMessage("${ChatColor.RED}Failed to resolve report #$reportId. Report not found.")
                }
            })
        }
    }

    private fun handleDelete(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("${ChatColor.RED}Usage: /reports delete <id>")
            return
        }

        val reportId = args[1].toLongOrNull()
        if (reportId == null) {
            sender.sendMessage("${ChatColor.RED}Invalid report ID!")
            return
        }

        plugin.runAsync {
            val success = reportManager.deleteReport(reportId)

            plugin.server.scheduler.runTask(plugin, Runnable {
                if (success) {
                    sender.sendMessage("${ChatColor.GREEN}Report #$reportId has been deleted.")
                } else {
                    sender.sendMessage("${ChatColor.RED}Failed to delete report #$reportId. Report not found.")
                }
            })
        }
    }

    private fun handleReload(sender: CommandSender) {
        plugin.reloadConfig()
        sender.sendMessage("${ChatColor.GREEN}Configuration reloaded successfully!")
    }

    private fun showHelp(sender: CommandSender) {
        sender.sendMessage("${ChatColor.GREEN}=== Sqrilizz-Reports Lite v${plugin.description.version} ===")
        sender.sendMessage("${ChatColor.YELLOW}/reports view <player>${ChatColor.GRAY} - View reports for a player")
        sender.sendMessage("${ChatColor.YELLOW}/reports close <id>${ChatColor.GRAY} - Close a report")
        sender.sendMessage("${ChatColor.YELLOW}/reports delete <id>${ChatColor.GRAY} - Delete a report")
        sender.sendMessage("${ChatColor.YELLOW}/reports reload${ChatColor.GRAY} - Reload configuration")
    }
}
