package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WebhookCommand implements CommandExecutor {
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
            VersionUtils.sendMessage(player, LanguageManager.getMessage("webhook-usage"));
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("set")) {
            if (args.length < 2) {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("webhook-usage"));
                return true;
            }

            String url = args[1];
            DiscordWebhookManager.setWebhookUrl(url);
            VersionUtils.sendMessage(player, LanguageManager.getMessage("webhook-set"));
        } else if (action.equals("remove")) {
            DiscordWebhookManager.setWebhookUrl("");
            VersionUtils.sendMessage(player, LanguageManager.getMessage("webhook-removed"));
        } else {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("webhook-usage"));
        }

        return true;
    }
} 