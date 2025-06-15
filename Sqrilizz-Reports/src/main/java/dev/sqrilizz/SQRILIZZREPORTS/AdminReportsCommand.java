package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class AdminReportsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("sqrilizzreports.admin")) {
            player.sendMessage(LanguageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            showReportsList(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "clear":
                if (args.length < 2) {
                    player.sendMessage(LanguageManager.getMessage("clear-usage"));
                    return true;
                }
                clearReports(player, args[1]);
                break;
            case "check":
                if (args.length < 2) {
                    player.sendMessage(LanguageManager.getMessage("check-usage"));
                    return true;
                }
                checkPlayerReports(player, args[1]);
                break;
            case "false":
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /griefreports false <игрок>");
                    return true;
                }
                punishFalseReport(player, args[1]);
                break;
            case "clearall":
                clearAllReports(player);
                break;
            default:
                player.sendMessage(LanguageManager.getMessage("admin-usage"));
                break;
        }

        return true;
    }

    private void showReportsList(Player player) {
        Map<String, List<ReportManager.Report>> reports = ReportManager.getReports();
        if (reports.isEmpty()) {
            player.sendMessage(LanguageManager.getMessage("no-reports"));
            return;
        }

        player.sendMessage(LanguageManager.getMessage("reports-list-header"));
        for (Map.Entry<String, List<ReportManager.Report>> entry : reports.entrySet()) {
            String targetName = entry.getKey();
            int reportCount = entry.getValue().size();
            player.sendMessage(LanguageManager.getMessage("reports-list-entry")
                    .replace("[PLAYER]", targetName)
                    .replace("[COUNT]", String.valueOf(reportCount)));
        }
    }

    private void clearReports(Player player, String targetName) {
        if (ReportManager.getReportCount(targetName) == 0) {
            player.sendMessage(LanguageManager.getMessage("no-reports-for-player")
                    .replace("[PLAYER]", targetName));
            return;
        }

        ReportManager.clearReports(targetName);
        player.sendMessage(LanguageManager.getMessage("reports-cleared")
                .replace("[PLAYER]", targetName));
    }

    private void checkPlayerReports(Player player, String targetName) {
        List<ReportManager.Report> reports = ReportManager.getReports(targetName);
        if (reports.isEmpty()) {
            player.sendMessage(LanguageManager.getMessage("no-reports-for-player")
                    .replace("[PLAYER]", targetName));
            return;
        }

        player.sendMessage(LanguageManager.getMessage("player-reports-header")
                .replace("[PLAYER]", targetName));

        for (ReportManager.Report report : reports) {
            player.sendMessage(LanguageManager.getMessage("report-details")
                    .replace("[REPORTER]", report.reporter)
                    .replace("[REASON]", report.reason)
                    .replace("[TIME]", report.getTimeAgo()));
        }
    }

    private void punishFalseReport(Player player, String reporterName) {
        // Получаем последний репорт от этого игрока
        List<ReportManager.Report> allReports = ReportManager.getReports().values().stream()
                .flatMap(List::stream)
                .filter(report -> report.reporter.equalsIgnoreCase(reporterName))
                .toList();

        if (allReports.isEmpty()) {
            player.sendMessage("§cНе найдено жалоб от игрока " + reporterName);
            return;
        }

        // Баним игрока на 5 дней
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + reporterName + " 5d Ложная жалоба");
        
        // Очищаем все жалобы от этого игрока
        for (Map.Entry<String, List<ReportManager.Report>> entry : ReportManager.getReports().entrySet()) {
            entry.getValue().removeIf(report -> report.reporter.equalsIgnoreCase(reporterName));
        }
        ReportManager.saveReports();

        // Уведомляем всех админов
        String message = LanguageManager.getMessage("false-report-punishment")
                .replace("[PLAYER]", reporterName);
        for (Player admin : Bukkit.getOnlinePlayers()) {
            if (admin.hasPermission("sqrilizzreports.admin")) {
                admin.sendMessage(message);
            }
        }
    }

    private void clearAllReports(Player player) {
        ReportManager.clearAllReports();
        player.sendMessage(LanguageManager.getMessage("all-reports-cleared"));
        
        // Уведомляем всех админов
        String message = LanguageManager.getMessage("all-reports-cleared");
        for (Player admin : Bukkit.getOnlinePlayers()) {
            if (admin.hasPermission("sqrilizzreports.admin")) {
                admin.sendMessage(message);
            }
        }
    }
} 