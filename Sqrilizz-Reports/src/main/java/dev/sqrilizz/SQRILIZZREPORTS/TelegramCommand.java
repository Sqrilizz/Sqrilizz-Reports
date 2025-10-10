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

        if (!VersionUtils.hasPermission(player, "reports.telegram")) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length != 2) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("telegram-usage"));
            return true;
        }

        String type = args[0].toLowerCase();
        String value = args[1];

        if (type.equals("token")) {
            // Сохраняем токен в конфигурацию
            Main.getInstance().getConfig().set("telegram.token", value);
            Main.getInstance().saveConfig();
            
            // Перезагружаем TelegramManager
            TelegramManager.initialize();
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("telegram-token-set"));
        } else if (type.equals("chat")) {
            // Сохраняем chat ID в конфигурацию
            Main.getInstance().getConfig().set("telegram.chat_id", value);
            Main.getInstance().saveConfig();
            
            // Перезагружаем TelegramManager
            TelegramManager.initialize();
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("telegram-chat-set"));
        } else {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("telegram-usage"));
        }

        return true;
    }
} 