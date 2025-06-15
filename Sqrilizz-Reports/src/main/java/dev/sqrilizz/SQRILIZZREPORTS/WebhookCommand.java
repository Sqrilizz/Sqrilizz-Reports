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

        if (!player.hasPermission("reports.admin")) {
            player.sendMessage(LanguageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cИспользование: /report-webhook <set|remove> [url]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set":
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /report-webhook set <url>");
                    return true;
                }
                String webhookUrl = args[1];
                if (!webhookUrl.startsWith("https://discord.com/api/webhooks/")) {
                    player.sendMessage("§cНеверный формат вебхука! URL должен начинаться с https://discord.com/api/webhooks/");
                    return true;
                }
                DiscordWebhookManager.setWebhookUrl(webhookUrl);
                player.sendMessage("§aВебхук успешно установлен!");
                break;
            case "remove":
                DiscordWebhookManager.removeWebhookUrl();
                player.sendMessage("§aВебхук успешно удален!");
                break;
            default:
                player.sendMessage("§cИспользование: /report-webhook <set|remove> [url]");
                break;
        }

        return true;
    }
} 