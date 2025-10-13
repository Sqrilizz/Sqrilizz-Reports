package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Команда для настройки Discord Bot
 */
public class DiscordBotCommand implements CommandExecutor {

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

        if (args.length < 2) {
            VersionUtils.sendMessage(player, ColorManager.colorize("{error}❌ Использование: {secondary}/report-discord <token|guild|channel> <значение>"));
            return true;
        }

        String type = args[0].toLowerCase();
        String value = args[1];

        switch (type) {
            case "token":
                // Сохраняем токен в конфигурацию
                Main.getInstance().getConfig().set("discord-bot.token", value);
                Main.getInstance().saveConfig();
                
                // Перезагружаем Discord Bot
                DiscordBot.shutdown();
                DiscordBot.initialize();
                
                VersionUtils.sendMessage(player, ColorManager.colorize("{success}🤖 Discord Bot токен установлен и бот перезапущен"));
                break;
                
            case "guild":
                // Сохраняем guild ID
                Main.getInstance().getConfig().set("discord-bot.guild-id", value);
                Main.getInstance().saveConfig();
                
                VersionUtils.sendMessage(player, ColorManager.colorize("{success}🏰 Discord Guild ID установлен: {accent}" + value));
                break;
                
            case "channel":
                // Сохраняем channel ID
                Main.getInstance().getConfig().set("discord-bot.channel-id", value);
                Main.getInstance().saveConfig();
                
                VersionUtils.sendMessage(player, ColorManager.colorize("{success}📢 Discord Channel ID установлен: {accent}" + value));
                break;
                
            case "enable":
                // Включаем бота
                Main.getInstance().getConfig().set("discord-bot.enabled", true);
                Main.getInstance().saveConfig();
                
                DiscordBot.initialize();
                VersionUtils.sendMessage(player, ColorManager.colorize("{success}✅ Discord Bot включен"));
                break;
                
            case "disable":
                // Выключаем бота
                Main.getInstance().getConfig().set("discord-bot.enabled", false);
                Main.getInstance().saveConfig();
                
                DiscordBot.shutdown();
                VersionUtils.sendMessage(player, ColorManager.colorize("{warning}⚠️ Discord Bot выключен"));
                break;
                
            case "moderation":
                boolean enableMod = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on");
                Main.getInstance().getConfig().set("discord-bot.moderation.enabled", enableMod);
                Main.getInstance().saveConfig();
                
                String status = enableMod ? "{success}✅ включена" : "{warning}❌ выключена";
                VersionUtils.sendMessage(player, ColorManager.colorize("{info}🛡️ Модерация через Discord " + status));
                break;
                
            case "status":
                // Показываем статус бота
                showBotStatus(player);
                break;
                
            default:
                VersionUtils.sendMessage(player, ColorManager.colorize("{error}❌ Неизвестный параметр: {accent}" + type));
                VersionUtils.sendMessage(player, ColorManager.colorize("{info}💡 Доступные параметры: {secondary}token, guild, channel, enable, disable, moderation, status"));
                break;
        }

        return true;
    }
    
    /**
     * Показывает статус Discord Bot
     */
    private void showBotStatus(Player player) {
        VersionUtils.sendMessage(player, ColorManager.colorize("{accent}▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        VersionUtils.sendMessage(player, ColorManager.colorize("{primary}{bold}🤖 DISCORD BOT STATUS{reset}"));
        VersionUtils.sendMessage(player, ColorManager.colorize("{accent}▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        
        boolean enabled = Main.getInstance().getConfig().getBoolean("discord-bot.enabled", false);
        String enabledStatus = enabled ? "{success}✅ Включен" : "{error}❌ Выключен";
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}📊 Статус: " + enabledStatus));
        
        boolean connected = DiscordBot.isEnabled();
        String connectionStatus = connected ? "{success}✅ Подключен" : "{error}❌ Не подключен";
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}🔗 Подключение: " + connectionStatus));
        
        String token = Main.getInstance().getConfig().getString("discord-bot.token", "");
        String tokenStatus = token.isEmpty() ? "{error}❌ Не установлен" : "{success}✅ Установлен";
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}🔑 Токен: " + tokenStatus));
        
        String guildId = Main.getInstance().getConfig().getString("discord-bot.guild-id", "");
        String guildStatus = guildId.isEmpty() ? "{warning}⚠️ Не установлен" : "{accent}" + guildId;
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}🏰 Guild ID: " + guildStatus));
        
        String channelId = Main.getInstance().getConfig().getString("discord-bot.channel-id", "");
        String channelStatus = channelId.isEmpty() ? "{warning}⚠️ Не установлен" : "{accent}" + channelId;
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}📢 Channel ID: " + channelStatus));
        
        boolean moderationEnabled = Main.getInstance().getConfig().getBoolean("discord-bot.moderation.enabled", false);
        String modStatus = moderationEnabled ? "{success}✅ Включена" : "{error}❌ Выключена";
        VersionUtils.sendMessage(player, ColorManager.colorize("{secondary}🛡️ Модерация: " + modStatus));
        
        VersionUtils.sendMessage(player, ColorManager.colorize("{accent}▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
    }
}
