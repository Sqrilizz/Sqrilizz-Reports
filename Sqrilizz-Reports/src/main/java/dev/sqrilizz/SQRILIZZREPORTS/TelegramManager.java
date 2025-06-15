package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramManager {
    private static String botToken;
    private static String chatId;
    private static TelegramBot bot;
    private static boolean isEnabled = false;

    public static void initialize() {
        botToken = Main.getInstance().getConfig().getString("telegram.bot-token", "");
        chatId = Main.getInstance().getConfig().getString("telegram.chat-id", "");
        
        Main.getInstance().getLogger().info("Initializing Telegram bot...");
        Main.getInstance().getLogger().info("Bot token: " + (botToken.isEmpty() ? "not set" : "set"));
        Main.getInstance().getLogger().info("Chat ID: " + (chatId.isEmpty() ? "not set" : chatId));
        
        if (!botToken.isEmpty() && !chatId.isEmpty()) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                try {
                    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                    bot = new TelegramBot();
                    botsApi.registerBot(bot);
                    isEnabled = true;
                    Main.getInstance().getLogger().info("Telegram bot has been initialized successfully!");
                } catch (TelegramApiException e) {
                    Main.getInstance().getLogger().warning("Failed to initialize Telegram bot: " + e.getMessage());
                    e.printStackTrace();
                    isEnabled = false;
                }
            });
        } else {
            Main.getInstance().getLogger().warning("Telegram bot is not configured. Please set bot token and chat ID.");
            isEnabled = false;
        }
    }

    public static void sendReport(String reporter, String target, String reason) {
        if (!isEnabled) {
            Main.getInstance().getLogger().warning("Cannot send report: Telegram bot is not enabled");
            return;
        }

        String message = String.format("%s\n%s\n%s\n%s",
            LanguageManager.getMessage("telegram-report-title"),
            String.format(LanguageManager.getMessage("telegram-report-from"), reporter),
            String.format(LanguageManager.getMessage("telegram-report-target"), target),
            String.format(LanguageManager.getMessage("telegram-report-reason"), reason));

        Main.getInstance().getLogger().info("Sending Telegram message: " + message);

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);
                sendMessage.enableMarkdown(true);
                bot.execute(sendMessage);
                Main.getInstance().getLogger().info("Telegram message sent successfully!");
            } catch (TelegramApiException e) {
                Main.getInstance().getLogger().warning("Failed to send report to Telegram: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static void setBotToken(String token) {
        Main.getInstance().getLogger().info("Setting new bot token...");
        botToken = token;
        Main.getInstance().getConfig().set("telegram.bot-token", token);
        Main.getInstance().saveConfig();
        initialize();
    }

    public static void setChatId(String id) {
        Main.getInstance().getLogger().info("Setting new chat ID: " + id);
        chatId = id;
        Main.getInstance().getConfig().set("telegram.chat-id", id);
        Main.getInstance().saveConfig();
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    private static class TelegramBot extends TelegramLongPollingBot {
        @Override
        public String getBotUsername() {
            return "SqrilizzReportsBot";
        }

        @Override
        public String getBotToken() {
            return botToken;
        }

        @Override
        public void onUpdateReceived(Update update) {
            // We don't need to handle incoming messages for now
        }
    }
} 