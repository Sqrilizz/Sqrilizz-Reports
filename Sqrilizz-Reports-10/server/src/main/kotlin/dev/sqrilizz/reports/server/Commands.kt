package dev.sqrilizz.reports.server

import dev.sqrilizz.reports.core.CreateReport
import dev.sqrilizz.reports.core.ReportException
import dev.sqrilizz.reports.core.ReportType
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

private val COMMON_REASONS = listOf(
    "Cheating", "Fly", "Speed", "KillAura", "AutoClicker",
    "X-Ray", "Griefing", "Spam", "Insult", "Toxic", "BugAbuse"
)

class ReportCommand(private val plugin: ReportsPlugin) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: run {
            sender.sendMessage(LanguageManager.getComponent("player-only"))
            return true
        }

        if (args.size < 2) {
            player.sendMessage(LanguageManager.getComponent("report-usage"))
            return true
        }

        val targetName = args[0]
        val target = Bukkit.getPlayerExact(targetName)
        if (target?.uniqueId == player.uniqueId) {
            player.sendMessage(LanguageManager.getComponent("cannot-report-self"))
            return true
        }

        val reason = args.drop(1).joinToString(" ")
        val request = CreateReport(
            reporterId = player.uniqueId,
            reporterName = player.name,
            targetId = target?.uniqueId,
            targetName = target?.name ?: targetName,
            reason = reason,
            sourceServer = plugin.serverId(),
            type = ReportType.PLAYER
        )

        plugin.runAsync {
            val result = plugin.reports.create(request)
            plugin.runOnPlayer(player) {
                result.fold(
                    onSuccess = { report ->
                        plugin.notifications.reportCreated(report)
                        player.sendMessage(LanguageManager.getComponent("report-success", "PLAYER" to report.targetName, "REASON" to report.reason))
                    },
                    onFailure = { error ->
                        val errCode = (error as? ReportException)?.message
                        val msg = when (errCode) {
                            "cooldown" -> LanguageManager.get("cooldown-message", "COOLDOWN" to plugin.config.getLong("reports.cooldown-seconds", 45).toString())
                            "reason" -> LanguageManager.get("report-usage")
                            "hourly-limit" -> LanguageManager.get("hourly-limit-reached", "LIMIT" to plugin.config.getInt("reports.max-per-hour", 8).toString(), "MAX" to plugin.config.getInt("reports.max-per-hour", 8).toString())
                            "target-limit" -> LanguageManager.get("report-limit-reached", "LIMIT" to plugin.config.getInt("reports.max-per-target", 2).toString(), "MAX" to plugin.config.getInt("reports.max-per-target", 2).toString())
                            "duplicate" -> LanguageManager.get("duplicate-report", "ID" to "0", "PLAYER" to targetName)
                            else -> "{error}Failed to create report: " + (error.message ?: "unknown")
                        }
                        player.sendMessage(ColorManager.component(msg))
                    }
                )
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], true) }.sorted()
            2 -> COMMON_REASONS.filter { it.startsWith(args[1], true) }
            else -> emptyList()
        }
    }
}

class BugReportCommand(private val plugin: ReportsPlugin) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: run {
            sender.sendMessage(LanguageManager.getComponent("player-only"))
            return true
        }

        val categories = plugin.config.getStringList("bugreports.categories").ifEmpty {
            listOf("duplication", "crash", "exploit", "performance", "gameplay", "world", "inventory", "commands", "permissions", "economy", "other")
        }

        if (args.size < 2) {
            player.sendMessage(LanguageManager.getComponent("bugreport-usage"))
            player.sendMessage(ColorManager.component("&7Categories: &e${categories.joinToString("&7, &e")}"))
            return true
        }

        val category = args[0].lowercase()
        if (category !in categories) {
            player.sendMessage(LanguageManager.getComponent("bugreport-invalid-category", "CATEGORY" to category))
            player.sendMessage(ColorManager.component("&7Valid categories: &e${categories.joinToString("&7, &e")}"))
            return true
        }

        val description = args.drop(1).joinToString(" ")
        val request = CreateReport(
            reporterId = player.uniqueId,
            reporterName = player.name,
            targetId = null,
            targetName = category,
            reason = description,
            sourceServer = plugin.serverId(),
            type = ReportType.BUG
        )

        plugin.runAsync {
            val result = plugin.reports.create(request)
            plugin.runOnPlayer(player) {
                result.fold(
                    onSuccess = { report ->
                        plugin.notifications.reportCreated(report)
                        player.sendMessage(LanguageManager.getComponent("bugreport-success", "CATEGORY" to report.targetName, "DESCRIPTION" to report.reason))
                    },
                    onFailure = { error ->
                        val errCode = (error as? ReportException)?.message
                        val msg = when (errCode) {
                            "cooldown" -> LanguageManager.get("cooldown-message", "COOLDOWN" to plugin.config.getLong("reports.cooldown-seconds", 45).toString())
                            "hourly-limit" -> LanguageManager.get("hourly-limit-reached", "LIMIT" to plugin.config.getInt("reports.max-per-hour", 8).toString(), "MAX" to plugin.config.getInt("reports.max-per-hour", 8).toString())
                            else -> "{error}Failed to create bug report: " + (error.message ?: "unknown")
                        }
                        player.sendMessage(ColorManager.component(msg))
                    }
                )
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        val categories = plugin.config.getStringList("bugreports.categories").ifEmpty {
            listOf("duplication", "crash", "exploit", "performance", "gameplay", "world", "inventory", "commands", "permissions", "economy", "other")
        }
        return if (args.size == 1) {
            categories.filter { it.startsWith(args[0], true) }
        } else {
            emptyList()
        }
    }
}

class ReportsCommand(private val plugin: ReportsPlugin) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            val player = sender as? Player ?: run {
                printConsoleReports(sender)
                return true
            }
            plugin.queueGui.openList(player)
            return true
        }

        when (args[0].lowercase()) {
            "open" -> {
                val player = sender as? Player ?: run {
                    printConsoleReports(sender)
                    return true
                }
                val page = args.getOrNull(1)?.toIntOrNull()?.let { it - 1 } ?: 0
                plugin.queueGui.openList(player, maxOf(0, page))
            }
            "resolve" -> {
                val id = args.getOrNull(1)?.toLongOrNull() ?: run {
                    sender.sendMessage(ColorManager.component("{error}Usage: /reports resolve <id>"))
                    return true
                }
                plugin.runAsync {
                    val updated = plugin.reports.resolve(id, sender.name)
                    if (updated != null) plugin.notifications.reportStatusChanged(updated, sender.name)
                    sender.sendMessage(
                        if (updated == null) LanguageManager.getComponent("report-not-found")
                        else LanguageManager.getComponent("report-resolved", "ID" to id.toString())
                    )
                }
            }
            "dismiss" -> {
                val id = args.getOrNull(1)?.toLongOrNull() ?: run {
                    sender.sendMessage(ColorManager.component("{error}Usage: /reports dismiss <id>"))
                    return true
                }
                plugin.runAsync {
                    val updated = plugin.reports.dismiss(id, sender.name)
                    if (updated != null) plugin.notifications.reportStatusChanged(updated, sender.name)
                    sender.sendMessage(
                        if (updated == null) LanguageManager.getComponent("report-not-found")
                        else LanguageManager.getComponent("report-closed", "ID" to id.toString())
                    )
                }
            }
            "tp" -> {
                val player = sender as? Player ?: run {
                    sender.sendMessage(LanguageManager.getComponent("player-only"))
                    return true
                }
                val targetName = args.getOrNull(1) ?: run {
                    player.sendMessage(ColorManager.component("{error}Usage: /reports tp <player>"))
                    return true
                }
                val target = Bukkit.getPlayerExact(targetName)
                if (target != null && target.isOnline) {
                    player.teleport(target.location)
                    player.sendMessage(LanguageManager.getComponent("teleport-success", "PLAYER" to target.name))
                } else {
                    player.sendMessage(LanguageManager.getComponent("teleport-offline"))
                }
            }
            "punish" -> {
                if (!sender.hasPermission("sqrilizzreports.punish")) {
                    sender.sendMessage(LanguageManager.getComponent("no-permission"))
                    return true
                }
                val target = args.getOrNull(1) ?: return false
                val action = args.getOrNull(2)?.lowercase() ?: return false
                val reason = args.drop(3).joinToString(" ").ifBlank { "Reported player" }
                val template = plugin.config.getString("punishments.commands.$action", "")!!.trim()
                if (template.isBlank()) {
                    sender.sendMessage(ColorManager.component("{error}No punishment command configured for: $action"))
                    return true
                }
                val commandLine = template.replace("{player}", target).replace("{reason}", reason)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine)
                sender.sendMessage(LanguageManager.getComponent("admin-punished", "PLAYER" to target, "TYPE" to action.uppercase()))
            }
            "stats" -> {
                plugin.runAsync {
                    val stats = plugin.reports.stats()
                    sender.sendMessage(ColorManager.component("&8=== &6&lSqrilizz-Reports Stats &8==="))
                    sender.sendMessage(ColorManager.component("&eOpen: &f${stats.open}"))
                    sender.sendMessage(ColorManager.component("&6In Progress: &f${stats.inProgress}"))
                    sender.sendMessage(ColorManager.component("&aResolved: &f${stats.resolved}"))
                    sender.sendMessage(ColorManager.component("&cDismissed: &f${stats.dismissed}"))
                    sender.sendMessage(ColorManager.component("&7Total Reports: &f${stats.total}"))
                }
            }
            "reload" -> {
                plugin.reloadAll()
                sender.sendMessage(LanguageManager.getComponent("config-reloaded"))
            }
            "language" -> {
                val lang = args.getOrNull(1)?.lowercase() ?: run {
                    sender.sendMessage(ColorManager.component("&7Current language: &e${LanguageManager.getCurrentLanguage()}&7. Available: &fen, ru, ar"))
                    return true
                }
                if (LanguageManager.setLanguage(plugin, lang)) {
                    sender.sendMessage(ColorManager.component("&aLanguage set to: &e$lang"))
                } else {
                    sender.sendMessage(ColorManager.component("&cUnknown language: $lang. Supported: en, ru, ar"))
                }
            }
            "help" -> {
                sender.sendMessage(ColorManager.component("&8=== &6Sqrilizz-Reports Commands &8==="))
                sender.sendMessage(ColorManager.component("&e/reports &7- Open reports GUI"))
                sender.sendMessage(ColorManager.component("&e/reports open [page] &7- Open reports queue"))
                sender.sendMessage(ColorManager.component("&e/reports resolve <id> &7- Mark report as resolved"))
                sender.sendMessage(ColorManager.component("&e/reports dismiss <id> &7- Dismiss report"))
                sender.sendMessage(ColorManager.component("&e/reports tp <player> &7- Teleport to player"))
                sender.sendMessage(ColorManager.component("&e/reports punish <player> <warn|kick|mute|ban> [reason] &7- Punish player"))
                sender.sendMessage(ColorManager.component("&e/reports stats &7- Show queue statistics"))
                sender.sendMessage(ColorManager.component("&e/reports language <en|ru|ar> &7- Change plugin language"))
                sender.sendMessage(ColorManager.component("&e/reports reload &7- Reload configuration and language files"))
            }
            else -> {
                sender.sendMessage(ColorManager.component("{error}Unknown subcommand. Type &e/reports help &cfor help."))
            }
        }
        return true
    }

    private fun printConsoleReports(sender: CommandSender) {
        plugin.runAsync {
            val open = plugin.reports.open(45)
            sender.sendMessage("=== Open Reports (${open.size}) ===")
            open.forEach {
                sender.sendMessage("#${it.id} [${it.type}] ${it.targetName} by ${it.reporterName} on ${it.sourceServer}: ${it.reason}")
            }
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        val subcommands = listOf("open", "resolve", "dismiss", "tp", "punish", "stats", "reload", "language", "help")
        return when (args.size) {
            1 -> subcommands.filter { it.startsWith(args[0], true) }
            2 -> when (args[0].lowercase()) {
                "tp", "punish" -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], true) }
                "language" -> listOf("en", "ru", "ar").filter { it.startsWith(args[1], true) }
                else -> emptyList()
            }
            3 -> when (args[0].lowercase()) {
                "punish" -> listOf("warn", "kick", "mute", "ban").filter { it.startsWith(args[2], true) }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }
}
