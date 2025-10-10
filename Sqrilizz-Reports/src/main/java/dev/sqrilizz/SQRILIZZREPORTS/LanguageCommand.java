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

        if (!VersionUtils.hasPermission(player, "reports.language")) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length != 1) {
            VersionUtils.sendMessage(player, ColorManager.colorize("{error}❌ Использование: {secondary}/report-language <ru|en|ar>"));
            return true;
        }

        String language = args[0].toLowerCase();
        if (!language.equals("ru") && !language.equals("en") && !language.equals("ar")) {
            VersionUtils.sendMessage(player, ColorManager.colorize("{error}❌ Поддерживаемые языки: {accent}ru{secondary}, {accent}en{secondary}, {accent}ar"));
            return true;
        }

        LanguageManager.setLanguage(language);
        String languageName = getLanguageName(language);
        VersionUtils.sendMessage(player, ColorManager.colorize("{success}🌍 Язык сервера изменен на {accent}" + languageName));
        return true;
    }
    
    /**
     * Получает полное название языка по коду
     */
    private String getLanguageName(String code) {
        switch (code) {
            case "ru": return "Русский (Russian)";
            case "en": return "English";
            case "ar": return "العربية (Arabic)";
            default: return code.toUpperCase();
        }
    }
} 