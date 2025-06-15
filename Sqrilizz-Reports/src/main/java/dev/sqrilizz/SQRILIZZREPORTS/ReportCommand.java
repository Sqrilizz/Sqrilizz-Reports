package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(LanguageManager.getMessage("report-usage"));
            return true;
        }

        if (CooldownManager.hasCooldown(player.getName())) {
            player.sendMessage(LanguageManager.getMessage("cooldown-message")
                    .replace("[COOLDOWN]", String.valueOf(CooldownManager.getRemainingTime(player.getName()))));
            return true;
        }

        String targetName = args[0];
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();

        // Проверяем сначала онлайн игроков
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            // Если игрок не онлайн, проверяем оффлайн игроков
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
            if (!offlineTarget.hasPlayedBefore()) {
                player.sendMessage(LanguageManager.getMessage("player-not-found")
                        .replace("[PLAYER]", targetName));
                return true;
            }
            targetName = offlineTarget.getName(); // Используем правильное имя игрока
        } else {
            targetName = target.getName();
        }

        if (targetName.equalsIgnoreCase(player.getName())) {
            player.sendMessage(LanguageManager.getMessage("cannot-report-self"));
            return true;
        }

        ReportManager.addReport(player.getName(), targetName, reason);
        player.sendMessage(LanguageManager.getMessage("report-success")
                .replace("[PLAYER]", targetName)
                .replace("[REASON]", reason));

        // Notify admins
        for (Player admin : Bukkit.getOnlinePlayers()) {
            if (admin.hasPermission("sqrilizzreports.admin")) {
                admin.sendMessage(LanguageManager.getMessage("admin-report-notification")
                        .replace("[REPORTER]", player.getName())
                        .replace("[TARGET]", targetName)
                        .replace("[REASON]", reason));
            }
        }

        // Set cooldown
        CooldownManager.setCooldown(player.getName());

        return true;
    }
} 