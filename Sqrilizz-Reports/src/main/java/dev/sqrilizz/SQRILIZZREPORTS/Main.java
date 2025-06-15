package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onEnable() {
        try {
            instance = this;
            getLogger().info("Starting initialization...");
            
            // Save default config
            saveDefaultConfig();
            getLogger().info("Config saved");
            
            // Initialize managers
            getLogger().info("Initializing LanguageManager...");
            LanguageManager.initialize();
            getLogger().info("LanguageManager initialized");
            
            getLogger().info("Initializing ReportManager...");
            ReportManager.initialize();
            getLogger().info("ReportManager initialized");
            
            getLogger().info("Initializing TelegramManager...");
            TelegramManager.initialize();
            getLogger().info("TelegramManager initialized");

            getLogger().info("Initializing DiscordWebhookManager...");
            DiscordWebhookManager.initialize();
            getLogger().info("DiscordWebhookManager initialized");
            
            // Register commands
            getLogger().info("Registering commands...");
            getCommand("report").setExecutor(new ReportCommand());
            getCommand("reports").setExecutor(new AdminReportsCommand());
            getCommand("report-telegram").setExecutor(new TelegramCommand());
            getCommand("report-language").setExecutor(new LanguageCommand());
            getCommand("report-webhook").setExecutor(new WebhookCommand());
            getLogger().info("Commands registered");
            
            getLogger().info("SQRILIZZREPORTS has been enabled successfully!");
        } catch (Exception e) {
            getLogger().severe("Failed to enable SQRILIZZREPORTS: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("SQRILIZZREPORTS has been disabled!");
    }

    public static Main getInstance() {
        return instance;
    }
} 