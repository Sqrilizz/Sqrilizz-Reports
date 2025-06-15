package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TelegramCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("sqrilizzreports.telegram")) {
            player.sendMessage(LanguageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(LanguageManager.getMessage("telegram-usage"));
            return true;
        }

        String type = args[0].toLowerCase();
        String value = args[1];

        switch (type) {
            case "token":
                TelegramManager.setBotToken(value);
                player.sendMessage(LanguageManager.getMessage("telegram-token-set"));
                break;
            case "chat":
                TelegramManager.setChatId(value);
                player.sendMessage(LanguageManager.getMessage("telegram-chat-set"));
                break;
            default:
                player.sendMessage(LanguageManager.getMessage("telegram-usage"));
                break;
        }

        return true;
    }
} 