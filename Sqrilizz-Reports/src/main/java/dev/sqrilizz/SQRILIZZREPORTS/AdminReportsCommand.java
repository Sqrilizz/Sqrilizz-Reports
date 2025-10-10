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

        if (!VersionUtils.hasPermission(player, "reports.admin")) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            showReportsList(player);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
            clearReports(player, args[1]);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
            checkPlayerReports(player, args[1]);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("false")) {
            punishFalseReport(player, args[1]);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("clearall")) {
            clearAllReports(player);
            return true;
        }

        VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-usage"));
        return true;
    }

    private void showReportsList(Player player) {
        Map<String, List<ReportManager.Report>> reports = ReportManager.getReports();
        
        if (reports.isEmpty()) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-reports"));
            return;
        }

        VersionUtils.sendMessage(player, LanguageManager.getMessage("reports-list-header"));
        
        for (Map.Entry<String, List<ReportManager.Report>> entry : reports.entrySet()) {
            String targetName = NameUtils.cleanPlayerName(entry.getKey());
            int count = entry.getValue().size();
            VersionUtils.sendMessage(player, LanguageManager.getMessage("reports-list-entry")
                .replace("[PLAYER]", targetName)
                .replace("[COUNT]", String.valueOf(count)));
        }
    }

    private void clearReports(Player player, String targetName) {
        String cleanTargetName = NameUtils.cleanPlayerName(targetName);
        
        if (ReportManager.getReportCount(cleanTargetName) == 0) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-reports-for-player")
                .replace("[PLAYER]", cleanTargetName));
            return;
        }

        ReportManager.clearReports(cleanTargetName);
        VersionUtils.sendMessage(player, LanguageManager.getMessage("reports-cleared")
            .replace("[PLAYER]", cleanTargetName));
    }

    private void checkPlayerReports(Player player, String targetName) {
        String cleanTargetName = NameUtils.cleanPlayerName(targetName);
        List<ReportManager.Report> targetReports = ReportManager.getPlayerReports(cleanTargetName);
        
        if (targetReports.isEmpty()) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-reports-for-player")
                .replace("[PLAYER]", cleanTargetName));
            return;
        }

        VersionUtils.sendMessage(player, LanguageManager.getMessage("player-reports-header")
            .replace("[PLAYER]", cleanTargetName));
        
        for (ReportManager.Report report : targetReports) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("report-details")
                .replace("[REPORTER]", NameUtils.cleanPlayerName(report.reporter))
                .replace("[REASON]", report.reason)
                .replace("[TIME]", report.getFormattedTime()));
            
            // Показываем координаты
            VersionUtils.sendMessage(player, "§7  Координаты жалобщика: §f" + report.reporterLocation);
            VersionUtils.sendMessage(player, "§7  Координаты цели: §f" + report.targetLocation);
            VersionUtils.sendMessage(player, "§7  Время назад: §f" + report.getTimeAgo());
        }
    }

    private void punishFalseReport(Player player, String reporterName) {
        String cleanReporterName = NameUtils.cleanPlayerName(reporterName);
        
        // Отмечаем ложную жалобу в AntiAbuseManager
        AntiAbuseManager.markFalseReport(cleanReporterName);
        
        // Находим все отчеты от этого игрока
        Map<String, List<ReportManager.Report>> allReports = ReportManager.getReports();
        boolean found = false;
        
        for (Map.Entry<String, List<ReportManager.Report>> entry : allReports.entrySet()) {
            List<ReportManager.Report> reports = entry.getValue();
            reports.removeIf(report -> report.reporter.equals(cleanReporterName));
            if (!reports.isEmpty()) {
                found = true;
            }
        }
        
        if (found) {
            // Сохраняем изменения
            ReportManager.saveReports();
            
            // Баним игрока за ложную жалобу
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + cleanReporterName + " 5d Ложная жалоба");
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("false-report-punishment")
                .replace("[PLAYER]", cleanReporterName));
        } else {
            VersionUtils.sendMessage(player, "§cИгрок " + cleanReporterName + " не найден в отчетах");
        }
    }

    private void clearAllReports(Player player) {
        ReportManager.clearAllReports();
        VersionUtils.sendMessage(player, LanguageManager.getMessage("all-reports-cleared"));
    }
} 