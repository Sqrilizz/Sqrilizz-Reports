package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class StatsCommand implements CommandExecutor {
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

        // Показываем статистику плагина
        showPluginStats(player);
        
        return true;
    }
    
    private void showPluginStats(Player player) {
        VersionUtils.sendMessage(player, LanguageManager.getMessage("stats-header"));
        
        // Статистика отчетов
        Map<String, List<ReportManager.Report>> reports = ReportManager.getReports();
        int totalReports = reports.values().stream().mapToInt(List::size).sum();
        int uniquePlayers = reports.size();
        
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}📊 Всего отчетов: {accent}" + totalReports));
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}👥 Уникальных игроков: {accent}" + uniquePlayers));
        
        // Статистика очистки имен
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}🔧 Настройки очистки имен:"));
        VersionUtils.sendMessage(player, ColorManager.colorize("{info}  " + NameUtils.getCleaningStats()));
        
        // Статистика сервера
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}🖥️ Версия сервера: {accent}" + VersionUtils.getServerVersion()));
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}⚙️ Платформа: {accent}" + (VersionUtils.isFoliaServer() ? "Folia" : "Paper/Spigot")));
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}🌐 Онлайн игроков: {accent}" + VersionUtils.getOnlinePlayersCount()));
        
        // Статистика интеграций
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}🔗 Интеграции:"));
        String telegramStatus = TelegramManager.isEnabled() ? "{success}✅ Включен" : "{error}❌ Выключен";
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}  📱 Telegram: " + telegramStatus));
        
        String discordWebhookStatus = DiscordWebhookManager.getWebhookUrl().isEmpty() ? "{error}❌ Не настроен" : "{success}✅ Настроен";
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}  🔗 Discord Webhook: " + discordWebhookStatus));
        
        String discordBotStatus = DiscordBot.isEnabled() ? "{success}✅ Активен" : "{error}❌ Неактивен";
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}  🤖 Discord Bot: " + discordBotStatus));
        
        // Статистика дизайна
        String hexStatus = ColorManager.isHexSupported() ? "{success}✅ Поддерживаются" : "{warning}⚠️ Legacy режим";
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}🎨 Hex цвета: " + hexStatus));
        
        // Статистика языка
        String currentLang = getLanguageDisplayName(LanguageManager.getCurrentLanguage());
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}🌍 Язык: {accent}" + currentLang));
        
        VersionUtils.sendMessage(player, LanguageManager.getMessage("stats-footer"));
    }
    
    /**
     * Получает красивое отображение названия языка
     */
    private String getLanguageDisplayName(String code) {
        switch (code.toLowerCase()) {
            case "ru": return "🇷🇺 Русский";
            case "en": return "🇺🇸 English";
            case "ar": return "🇸🇦 العربية";
            default: return code.toUpperCase();
        }
    }
}
