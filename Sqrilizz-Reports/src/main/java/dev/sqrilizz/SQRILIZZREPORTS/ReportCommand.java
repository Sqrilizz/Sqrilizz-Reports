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
            VersionUtils.sendMessage(player, LanguageManager.getMessage("report-usage"));
            return true;
        }

        String targetName = args[0];
        String reason = String.join(" ", args).substring(targetName.length()).trim();

        if (reason.isEmpty()) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("report-usage"));
            return true;
        }

        // Проверяем кулдаун
        if (CooldownManager.hasCooldown(VersionUtils.getPlayerUUID(player))) {
            long remainingTime = CooldownManager.getRemainingTime(VersionUtils.getPlayerUUID(player));
            VersionUtils.sendMessage(player, LanguageManager.getMessage("cooldown-message")
                .replace("[COOLDOWN]", String.valueOf(remainingTime)));
            return true;
        }

        // Проверяем систему защиты от злоупотреблений
        if (!AntiAbuseManager.canReport(player, targetName)) {
            return true;
        }

        // Проверяем, не пытается ли игрок пожаловаться на себя
        if (targetName.equalsIgnoreCase(player.getName())) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("cannot-report-self"));
            return true;
        }

        // Ищем цель
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            // Проверяем оффлайн игроков
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);
            if (!offlinePlayer.hasPlayedBefore()) {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("player-not-found")
                    .replace("[PLAYER]", targetName));
                return true;
            }
            // Для оффлайн игроков создаем временный Player объект или используем другой подход
            VersionUtils.sendMessage(player, LanguageManager.getMessage("report-offline")
                .replace("[PLAYER]", targetName));
            return true;
        }

        // Отправляем жалобу
        ReportManager.addReport(player, targetPlayer, reason);
        
        // Регистрируем жалобу в системе защиты от злоупотреблений
        AntiAbuseManager.recordReport(player, VersionUtils.getPlayerCleanName(targetPlayer));
        
        // Устанавливаем кулдаун
        CooldownManager.setCooldown(VersionUtils.getPlayerUUID(player));
        
        // Уведомляем игрока об успешной отправке
        VersionUtils.sendMessage(player, LanguageManager.getMessage("report-success")
            .replace("[PLAYER]", VersionUtils.getPlayerDisplayName(targetPlayer))
            .replace("[REASON]", reason));
        
        // Уведомляем цель о получении жалобы
        VersionUtils.sendMessage(targetPlayer, LanguageManager.getMessage("report-received")
            .replace("[PLAYER]", VersionUtils.getPlayerDisplayName(player)));
        
        return true;
    }
} 