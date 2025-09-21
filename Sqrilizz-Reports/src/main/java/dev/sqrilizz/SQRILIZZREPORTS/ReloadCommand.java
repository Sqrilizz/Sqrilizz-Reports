package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!VersionUtils.hasPermission(player, "reports.reload")) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-permission"));
            return true;
        }

        try {
            // Перезагружаем конфигурацию
            Main.getInstance().reloadConfig();
            
            // Перезагружаем менеджеры в правильном порядке
            ColorManager.initialize();
            LanguageManager.initialize();
            TelegramManager.initialize();
            DiscordWebhookManager.initialize();
            DiscordBot.initialize();
            AntiAbuseManager.initialize();
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("config-reloaded"));
            VersionUtils.sendMessage(player, ColorManager.colorize("{info}🔧 Настройки очистки имен: " + NameUtils.getCleaningStats()));
            VersionUtils.sendMessage(player, ColorManager.colorize("{success}🎨 Дизайн: " + (ColorManager.isHexSupported() ? "Hex цвета" : "Legacy цвета")));
            
        } catch (Exception e) {
            VersionUtils.sendMessage(player, ColorManager.colorize("{error}❌ Ошибка при перезагрузке конфигурации: " + e.getMessage()));
            Main.getInstance().getLogger().severe("Failed to reload configuration: " + e.getMessage());
            e.printStackTrace();
        }
        
        return true;
    }
}
