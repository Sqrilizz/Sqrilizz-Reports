package dev.sqrilizz.SQRILIZZREPORTS;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Discord Bot для модерации сервера через Discord
 * Поддерживает команды: /ban, /kick, /mute, /warn, /reports
 */
public class DiscordBot extends ListenerAdapter {
    
    private static JDA jda;
    private static boolean enabled = false;
    private static String guildId;
    private static String channelId;
    private static List<String> modRoles;
    private static boolean moderationEnabled = false;
    
    /**
     * Инициализация Discord бота
     */
    public static void initialize() {
        FileConfiguration config = Main.getInstance().getConfig();
        
        enabled = config.getBoolean("discord-bot.enabled", false);
        if (!enabled) {
            Main.getInstance().getLogger().info("Discord Bot disabled in config");
            return;
        }
        
        String token = config.getString("discord-bot.token", "");
        if (token.isEmpty()) {
            Main.getInstance().getLogger().warning("Discord Bot token not set!");
            return;
        }
        
        guildId = config.getString("discord-bot.guild-id", "");
        channelId = config.getString("discord-bot.channel-id", "");
        modRoles = config.getStringList("discord-bot.mod-roles");
        moderationEnabled = config.getBoolean("discord-bot.moderation.enabled", false);
        
        try {
            jda = JDABuilder.createDefault(token)
                    .addEventListeners(new DiscordBot())
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .build();
                    
            // Устанавливаем статус бота
            String status = config.getString("discord-bot.status", "Watching reports 👀");
            jda.awaitReady();
            jda.getPresence().setActivity(Activity.watching(status));
            
            Main.getInstance().getLogger().info("Discord Bot successfully initialized!");
            
        } catch (Exception e) {
            Main.getInstance().getLogger().severe("Failed to initialize Discord Bot: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Отключение бота
     */
    public static void shutdown() {
        if (jda != null) {
            jda.shutdown();
            try {
                if (!jda.awaitShutdown(10, TimeUnit.SECONDS)) {
                    jda.shutdownNow();
                }
            } catch (InterruptedException e) {
                jda.shutdownNow();
            }
        }
    }
    
    /**
     * Проверяет, включен ли бот
     */
    public static boolean isEnabled() {
        return enabled && jda != null;
    }
    
    /**
     * Отправляет уведомление о новой жалобе в Discord
     */
    public static void sendReportNotification(String reporter, String target, String reason, long timestamp, String reporterLoc, String targetLoc, boolean isAnonymous) {
        if (!isEnabled() || channelId.isEmpty()) return;
        
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) return;
        
        // Создаем красивый embed
        var embed = new net.dv8tion.jda.api.EmbedBuilder()
                .setTitle("🚨 Новая жалоба")
                .setColor(Color.RED)
                .addField("👤 Жалобщик", isAnonymous ? "*Анонимно*" : reporter, true)
                .addField("🎯 Цель", target, true)
                .addField("📝 Причина", reason, false)
                .addField("📍 Локация жалобщика", reporterLoc, true)
                .addField("📍 Локация цели", targetLoc, true)
                .setTimestamp(Instant.ofEpochMilli(timestamp))
                .setFooter("Sqrilizz-Reports", null)
                .build();
                
        channel.sendMessageEmbeds(embed).queue();
    }
    
    @Override
    public void onReady(ReadyEvent event) {
        Main.getInstance().getLogger().info("Discord Bot is ready! Logged in as: " + event.getJDA().getSelfUser().getAsTag());
        
        // Регистрируем slash команды если модерация включена
        if (moderationEnabled && !guildId.isEmpty()) {
            Guild guild = jda.getGuildById(guildId);
            if (guild != null) {
                registerSlashCommands(guild);
            }
        }
    }
    
    /**
     * Регистрирует slash команды
     */
    private void registerSlashCommands(Guild guild) {
        FileConfiguration config = Main.getInstance().getConfig();
        
        var commands = guild.updateCommands();
        
        if (config.getBoolean("discord-bot.moderation.commands.ban", true)) {
            commands.addCommands(Commands.slash("ban", "Забанить игрока")
                    .addOption(OptionType.STRING, "player", "Имя игрока", true)
                    .addOption(OptionType.STRING, "reason", "Причина бана", false));
        }
        
        if (config.getBoolean("discord-bot.moderation.commands.kick", true)) {
            commands.addCommands(Commands.slash("kick", "Кикнуть игрока")
                    .addOption(OptionType.STRING, "player", "Имя игрока", true)
                    .addOption(OptionType.STRING, "reason", "Причина кика", false));
        }
        
        if (config.getBoolean("discord-bot.moderation.commands.mute", true)) {
            commands.addCommands(Commands.slash("mute", "Замутить игрока")
                    .addOption(OptionType.STRING, "player", "Имя игрока", true)
                    .addOption(OptionType.INTEGER, "duration", "Длительность в минутах", false)
                    .addOption(OptionType.STRING, "reason", "Причина мута", false));
        }
        
        if (config.getBoolean("discord-bot.moderation.commands.warn", true)) {
            commands.addCommands(Commands.slash("warn", "Предупредить игрока")
                    .addOption(OptionType.STRING, "player", "Имя игрока", true)
                    .addOption(OptionType.STRING, "reason", "Причина предупреждения", false));
        }
        
        // Команда для просмотра жалоб
        commands.addCommands(Commands.slash("reports", "Просмотр жалоб")
                .addOption(OptionType.STRING, "player", "Имя игрока (необязательно)", false));
        
        commands.queue(success -> {
            Main.getInstance().getLogger().info("Discord slash commands registered successfully!");
        }, error -> {
            Main.getInstance().getLogger().severe("Failed to register Discord slash commands: " + error.getMessage());
        });
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!moderationEnabled) {
            event.reply("❌ Модерация через Discord отключена").setEphemeral(true).queue();
            return;
        }
        
        // Проверяем права доступа
        if (!hasModeratorRole(event.getMember())) {
            event.reply("❌ У вас нет прав для использования этой команды").setEphemeral(true).queue();
            return;
        }
        
        String command = event.getName();
        String playerName = event.getOption("player") != null ? event.getOption("player").getAsString() : null;
        String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : "Не указана";
        
        switch (command) {
            case "ban":
                handleBanCommand(event, playerName, reason);
                break;
            case "kick":
                handleKickCommand(event, playerName, reason);
                break;
            case "mute":
                int duration = event.getOption("duration") != null ? event.getOption("duration").getAsInt() : 60;
                handleMuteCommand(event, playerName, duration, reason);
                break;
            case "warn":
                handleWarnCommand(event, playerName, reason);
                break;
            case "reports":
                handleReportsCommand(event, playerName);
                break;
        }
    }
    
    /**
     * Проверяет, есть ли у пользователя роль модератора
     */
    private boolean hasModeratorRole(Member member) {
        if (member == null) return false;
        if (member.hasPermission(Permission.ADMINISTRATOR)) return true;
        
        return member.getRoles().stream()
                .anyMatch(role -> modRoles.contains(role.getId()));
    }
    
    /**
     * Обработка команды бана
     */
    private void handleBanCommand(SlashCommandInteractionEvent event, String playerName, String reason) {
        if (playerName == null) {
            event.reply("❌ Укажите имя игрока").setEphemeral(true).queue();
            return;
        }
        
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            if (player == null) {
                event.reply("❌ Игрок не найден").setEphemeral(true).queue();
                return;
            }
            
            // Выполняем бан через консоль
            String banCommand = String.format("ban %s %s", playerName, reason);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand);
            
            event.reply(String.format("✅ Игрок **%s** забанен\n📝 Причина: %s", playerName, reason)).queue();
            
            Main.getInstance().getLogger().info(String.format("Player %s banned via Discord by %s. Reason: %s", 
                    playerName, event.getUser().getAsTag(), reason));
        });
    }
    
    /**
     * Обработка команды кика
     */
    private void handleKickCommand(SlashCommandInteractionEvent event, String playerName, String reason) {
        if (playerName == null) {
            event.reply("❌ Укажите имя игрока").setEphemeral(true).queue();
            return;
        }
        
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            var player = Bukkit.getPlayer(playerName);
            if (player == null || !player.isOnline()) {
                event.reply("❌ Игрок не в сети").setEphemeral(true).queue();
                return;
            }
            
            player.kickPlayer(ColorManager.colorize("{error}Вы были кикнуты с сервера\n{secondary}Причина: " + reason));
            
            event.reply(String.format("✅ Игрок **%s** кикнут\n📝 Причина: %s", playerName, reason)).queue();
            
            Main.getInstance().getLogger().info(String.format("Player %s kicked via Discord by %s. Reason: %s", 
                    playerName, event.getUser().getAsTag(), reason));
        });
    }
    
    /**
     * Обработка команды мута
     */
    private void handleMuteCommand(SlashCommandInteractionEvent event, String playerName, int duration, String reason) {
        if (playerName == null) {
            event.reply("❌ Укажите имя игрока").setEphemeral(true).queue();
            return;
        }
        
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            // Выполняем мут через консоль (если есть плагин для мутов)
            String muteCommand = String.format("mute %s %dm %s", playerName, duration, reason);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), muteCommand);
            
            event.reply(String.format("✅ Игрок **%s** замучен на %d минут\n📝 Причина: %s", playerName, duration, reason)).queue();
            
            Main.getInstance().getLogger().info(String.format("Player %s muted via Discord by %s for %d minutes. Reason: %s", 
                    playerName, event.getUser().getAsTag(), duration, reason));
        });
    }
    
    /**
     * Обработка команды предупреждения
     */
    private void handleWarnCommand(SlashCommandInteractionEvent event, String playerName, String reason) {
        if (playerName == null) {
            event.reply("❌ Укажите имя игрока").setEphemeral(true).queue();
            return;
        }
        
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            var player = Bukkit.getPlayer(playerName);
            if (player != null && player.isOnline()) {
                VersionUtils.sendMessage(player, ColorManager.colorize("{warning}⚠️ {bold}ПРЕДУПРЕЖДЕНИЕ{reset}\n{secondary}Причина: {info}" + reason));
            }
            
            event.reply(String.format("✅ Игрок **%s** предупрежден\n📝 Причина: %s", playerName, reason)).queue();
            
            Main.getInstance().getLogger().info(String.format("Player %s warned via Discord by %s. Reason: %s", 
                    playerName, event.getUser().getAsTag(), reason));
        });
    }
    
    /**
     * Обработка команды просмотра жалоб
     */
    private void handleReportsCommand(SlashCommandInteractionEvent event, String playerName) {
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            if (playerName == null) {
                // Показываем общую статистику
                var reports = ReportManager.getReports();
                int totalReports = reports.values().stream().mapToInt(List::size).sum();
                
                var embed = new net.dv8tion.jda.api.EmbedBuilder()
                        .setTitle("📊 Статистика жалоб")
                        .setColor(Color.BLUE)
                        .addField("📋 Всего жалоб", String.valueOf(totalReports), true)
                        .addField("👥 Игроков с жалобами", String.valueOf(reports.size()), true)
                        .setTimestamp(Instant.now())
                        .build();
                        
                event.replyEmbeds(embed).queue();
            } else {
                // Показываем жалобы на конкретного игрока
                var playerReports = ReportManager.getReports().get(playerName);
                if (playerReports == null || playerReports.isEmpty()) {
                    event.reply(String.format("📋 На игрока **%s** нет жалоб", playerName)).queue();
                    return;
                }
                
                var embed = new net.dv8tion.jda.api.EmbedBuilder()
                        .setTitle(String.format("📋 Жалобы на %s", playerName))
                        .setColor(Color.ORANGE)
                        .addField("📊 Количество", String.valueOf(playerReports.size()), true);
                
                // Показываем последние 5 жалоб
                int count = Math.min(5, playerReports.size());
                for (int i = 0; i < count; i++) {
                    var report = playerReports.get(i);
                    embed.addField(
                            String.format("Жалоба #%d", i + 1),
                            String.format("**От:** %s\n**Причина:** %s", report.reporter, report.reason),
                            false
                    );
                }
                
                if (playerReports.size() > 5) {
                    embed.setFooter(String.format("И еще %d жалоб...", playerReports.size() - 5));
                }
                
                event.replyEmbeds(embed.build()).queue();
            }
        });
    }
}
