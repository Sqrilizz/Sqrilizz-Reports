package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AdminReportsCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(LanguageManager.getMessage("player-only"))
            return true
        }

        if (!VersionUtils.hasPermission(sender, "reports.admin")) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("no-permission"))
            return true
        }

        when {
            args.isEmpty() || (args.size == 1 && args[0].equals("gui", ignoreCase = true)) -> {
                ReportsGUI.openReportsListGUI(sender)
            }
            args.size == 1 && args[0].equals("list", ignoreCase = true) -> {
                showReportsList(sender)
            }
            args.size == 2 && args[0].equals("clear", ignoreCase = true) -> {
                clearReports(sender, args[1])
            }
            args.size == 2 && args[0].equals("check", ignoreCase = true) -> {
                checkPlayerReports(sender, args[1])
            }
            args.size == 2 && args[0].equals("false", ignoreCase = true) -> {
                punishFalseReport(sender, args[1])
            }
            args.size == 1 && args[0].equals("clearall", ignoreCase = true) -> {
                clearAllReports(sender)
            }
            else -> {
                VersionUtils.sendMessage(sender, LanguageManager.getMessage("admin-usage"))
            }
        }

        return true
    }

    private fun showReportsList(player: Player) {
        val reports = ReportManager.getReports()

        if (reports.isEmpty()) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-reports"))
            return
        }

        VersionUtils.sendMessage(player, LanguageManager.getMessage("reports-list-header"))

        for ((targetName, targetReports) in reports) {
            val cleanName = NameUtils.cleanPlayerName(targetName)
            VersionUtils.sendMessage(player, LanguageManager.getMessage("reports-list-entry")
                .replace("[PLAYER]", cleanName)
                .replace("[COUNT]", targetReports.size.toString()))
        }
    }

    private fun clearReports(player: Player, targetName: String) {
        val cleanTargetName = NameUtils.cleanPlayerName(targetName)

        if (ReportManager.getReportCount(cleanTargetName) == 0) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-reports-for-player")
                .replace("[PLAYER]", cleanTargetName))
            return
        }

        ReportManager.clearReports(cleanTargetName)
        VersionUtils.sendMessage(player, LanguageManager.getMessage("reports-cleared")
            .replace("[PLAYER]", cleanTargetName))
    }

    private fun checkPlayerReports(player: Player, targetName: String) {
        val cleanTargetName = NameUtils.cleanPlayerName(targetName)
        val targetReports = ReportManager.getPlayerReports(cleanTargetName)

        if (targetReports.isEmpty()) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-reports-for-player")
                .replace("[PLAYER]", cleanTargetName))
            return
        }

        VersionUtils.sendMessage(player, LanguageManager.getMessage("player-reports-header")
            .replace("[PLAYER]", cleanTargetName))

        for (report in targetReports) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("report-details")
                .replace("[REPORTER]", NameUtils.cleanPlayerName(report.reporter))
                .replace("[REASON]", report.reason)
                .replace("[TIME]", report.formattedTime))

            VersionUtils.sendMessage(player, "\u00a77  Координаты жалобщика: \u00a7f${report.reporterLocation}")
            VersionUtils.sendMessage(player, "\u00a77  Координаты цели: \u00a7f${report.targetLocation}")
            VersionUtils.sendMessage(player, "\u00a77  Время назад: \u00a7f${report.timeAgo}")
        }
    }

    private fun punishFalseReport(player: Player, reporterName: String) {
        val cleanReporterName = NameUtils.cleanPlayerName(reporterName)

        AntiAbuseManager.markFalseReport(cleanReporterName)

        VersionUtils.sendMessage(player, LanguageManager.getMessage("false-report-punishment")
            .replace("[PLAYER]", cleanReporterName))

        dev.sqrilizz.SQRILIZZREPORTS.api.WebhookManager.sendFalseReportWebhook(
            cleanReporterName, VersionUtils.getPlayerCleanName(player))
    }

    private fun clearAllReports(player: Player) {
        ReportManager.clearAllReports()
        VersionUtils.sendMessage(player, LanguageManager.getMessage("all-reports-cleared"))
    }
}
