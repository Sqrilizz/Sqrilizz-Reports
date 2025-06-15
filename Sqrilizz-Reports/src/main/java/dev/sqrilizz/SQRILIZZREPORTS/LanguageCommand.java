package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LanguageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("reports.language")) {
            player.sendMessage(LanguageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cИспользование: /report-language <ru|en>");
            return true;
        }

        String language = args[0].toLowerCase();
        if (!language.equals("ru") && !language.equals("en")) {
            player.sendMessage("§cПоддерживаемые языки: ru, en");
            return true;
        }

        LanguageManager.setLanguage(language);
        player.sendMessage("§aЯзык сервера изменен на " + language.toUpperCase());
        return true;
    }
} 