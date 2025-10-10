package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramManager {
    private static TelegramBot bot;
    private static boolean enabled = false;

    public static void initialize() {
        try {
            if (Main.getInstance().getConfig().getBoolean("telegram.enabled", false)) {
                String token = Main.getInstance().getConfig().getString("telegram.token", "");
                String chatId = Main.getInstance().getConfig().getString("telegram.chat_id", "");
                
                if (!token.isEmpty() && !chatId.isEmpty()) {
                    bot = new TelegramBot(token);
                    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                    botsApi.registerBot(bot);
                    enabled = true;
                    Main.getInstance().getLogger().info("Telegram bot initialized successfully");
                } else {
                    Main.getInstance().getLogger().warning("Telegram bot not configured properly");
                }
            }
        } catch (Exception e) {
            Main.getInstance().getLogger().severe("Failed to initialize Telegram bot: " + e.getMessage());
        }
    }

    public static boolean isEnabled() {
        return enabled && bot != null;
    }

    public static void sendReport(ReportManager.Report report) {
        if (!isEnabled()) return;

        String message = String.format(
            "🚨 *Новая жалоба*\n\n" +
            "*От:* %s\n" +
            "*На:* %s\n" +
            "*Причина:* %s\n" +
            "*Время:* %s\n" +
            "*Координаты жалобщика:* %s\n" +
            "*Координаты цели:* %s",
            report.reporter,
            report.target,
            report.reason,
            report.getFormattedTime(),
            report.reporterLocation,
            report.targetLocation
        );

        Main.runTaskAsync(() -> {
            try {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(Main.getInstance().getConfig().getString("telegram.chat_id"));
                sendMessage.setText(message);
                sendMessage.enableMarkdown(true);
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                Main.getInstance().getLogger().severe("Failed to send Telegram message: " + e.getMessage());
            }
        });
    }

    private static class TelegramBot extends TelegramLongPollingBot {
        private final String botToken;

        public TelegramBot(String token) {
            super(token);
            this.botToken = token;
        }

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
            // Обработка входящих сообщений (если нужно)
            if (update.hasMessage() && update.getMessage().hasText()) {
                String text = update.getMessage().getText();
                if (text.equals("/start")) {
                    try {
                        SendMessage response = new SendMessage();
                        response.setChatId(update.getMessage().getChatId().toString());
                        response.setText("Привет! Я бот для уведомлений о жалобах на сервере.");
                        execute(response);
                    } catch (TelegramApiException e) {
                        Main.getInstance().getLogger().warning("Failed to send Telegram response: " + e.getMessage());
                    }
                }
            }
        }
    }
} 