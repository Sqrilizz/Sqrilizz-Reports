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
            targetName = NameUtils.cleanPlayerName(offlinePlayer.getName());
        } else {
            targetName = VersionUtils.getPlayerCleanName(targetPlayer);
        }

        ReportManager.Report duplicate = findDuplicate(player, targetName);
        if (duplicate != null) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("duplicate-report")
                .replace("[ID]", String.valueOf(duplicate.id))
                .replace("[PLAYER]", targetName));
            return true;
        }

        if (CooldownManager.hasCooldown(VersionUtils.getPlayerUUID(player))) {
            long remainingTime = CooldownManager.getRemainingTime(VersionUtils.getPlayerUUID(player));
            VersionUtils.sendMessage(player, LanguageManager.getMessage("cooldown-message")
                .replace("[COOLDOWN]", String.valueOf(remainingTime)));
            return true;
        }

        if (!AntiAbuseManager.canReport(player, targetName)) {
            return true;
        }

        if (targetPlayer == null) {
            ReportManager.addOfflineReport(player, targetName, reason);
        } else {
            ReportManager.addReport(player, targetPlayer, reason);
        }

        AntiAbuseManager.recordReport(player, targetName);
        CooldownManager.setCooldown(VersionUtils.getPlayerUUID(player));

        VersionUtils.sendMessage(player, LanguageManager.getMessage("report-success")
            .replace("[PLAYER]", targetName)
            .replace("[REASON]", reason));
        return true;
    }

    private ReportManager.Report findDuplicate(Player reporter, String targetName) {
        if (!Main.getInstance().getConfig().getBoolean("reports.duplicate-protection.enabled", true)) {
            return null;
        }
        long windowMillis = Main.getInstance().getConfig()
            .getLong("reports.duplicate-protection.window-seconds", 900L) * 1000L;
        return ReportManager.findRecentOpenReport(
            VersionUtils.getPlayerCleanName(reporter),
            targetName,
            windowMillis
        );
    }
}
