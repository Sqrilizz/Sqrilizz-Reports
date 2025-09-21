package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {
    private static Main instance;
    private static boolean isFolia = false;
    private static String serverVersion;
    private static int majorVersion;

    @Override
    public void onEnable() {
        try {
            instance = this;
            getLogger().info("Starting initialization...");
            
            // Detect server type and version
            detectServerType();
            getLogger().info("Detected server: " + (isFolia ? "Folia" : "Paper/Spigot") + " v" + serverVersion);
            
            // Save default config
            saveDefaultConfig();
            getLogger().info("Config saved");
            
            // Initialize managers
            getLogger().info("Initializing ColorManager...");
            ColorManager.initialize();
            getLogger().info("ColorManager initialized");
            
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
            
            getLogger().info("Initializing DiscordBot...");
            DiscordBot.initialize();
            getLogger().info("DiscordBot initialized");

            getLogger().info("Initializing AntiAbuseManager...");
            AntiAbuseManager.initialize();
            getLogger().info("AntiAbuseManager initialized");

            getLogger().info("Loading webhook configurations...");
            dev.sqrilizz.SQRILIZZREPORTS.api.WebhookManager.loadWebhooksFromConfig();
            getLogger().info("Webhook configurations loaded");
            
            // Register commands
            getLogger().info("Registering commands...");
            getCommand("report").setExecutor(new ReportCommand());
            getCommand("reports").setExecutor(new AdminReportsCommand());
            getCommand("report-telegram").setExecutor(new TelegramCommand());
            getCommand("report-language").setExecutor(new LanguageCommand());
            getCommand("report-webhook").setExecutor(new WebhookCommand());
            getCommand("report-stats").setExecutor(new StatsCommand());
            getCommand("report-reload").setExecutor(new ReloadCommand());
            getCommand("report-discord").setExecutor(new DiscordBotCommand());
            
            // Register tab completers
            getLogger().info("Registering tab completers...");
            ReportsTabCompleter tabCompleter = new ReportsTabCompleter();
            getCommand("report").setTabCompleter(tabCompleter);
            getCommand("reports").setTabCompleter(tabCompleter);
            getCommand("report-language").setTabCompleter(tabCompleter);
            getCommand("report-telegram").setTabCompleter(tabCompleter);
            getCommand("report-webhook").setTabCompleter(tabCompleter);
            getCommand("report-stats").setTabCompleter(tabCompleter);
            getCommand("report-reload").setTabCompleter(tabCompleter);
            getCommand("report-discord").setTabCompleter(tabCompleter);
            getLogger().info("Commands and tab completers registered");
            
            // Log name cleaning stats
            getLogger().info("Name cleaning configuration: " + NameUtils.getCleaningStats());
            
            // Запускаем задачу очистки старых данных каждые 6 часов
            getScheduler().runTaskTimerAsynchronously(this, () -> {
                AntiAbuseManager.cleanupOldData();
                getLogger().info("Cleaned up old anti-abuse data");
            }, 20L * 60 * 60 * 6, 20L * 60 * 60 * 6); // 6 часов в тиках
            
            getLogger().info("SQRILIZZREPORTS has been enabled successfully!");
        } catch (Exception e) {
            getLogger().severe("Failed to enable SQRILIZZREPORTS: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down Discord Bot...");
        DiscordBot.shutdown();
        getLogger().info("SQRILIZZREPORTS has been disabled!");
    }

    private void detectServerType() {
        try {
            // Check if Folia is available
            Class.forName("io.papermc.folia.FoliaPlugin");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
        
        // Get server version
        String version = Bukkit.getVersion();
        if (version.contains("1.8")) {
            serverVersion = "1.8.x";
            majorVersion = 8;
        } else if (version.contains("1.9") || version.contains("1.10") || version.contains("1.11")) {
            serverVersion = "1.9-1.11";
            majorVersion = 9;
        } else if (version.contains("1.12") || version.contains("1.13") || version.contains("1.14") || version.contains("1.15")) {
            serverVersion = "1.12-1.15";
            majorVersion = 12;
        } else if (version.contains("1.16")) {
            serverVersion = "1.16";
            majorVersion = 16;
        } else if (version.contains("1.17") || version.contains("1.18")) {
            serverVersion = "1.17-1.18";
            majorVersion = 17;
        } else if (version.contains("1.19")) {
            serverVersion = "1.19";
            majorVersion = 19;
        } else if (version.contains("1.20")) {
            serverVersion = "1.20";
            majorVersion = 20;
        } else if (version.contains("1.21")) {
            serverVersion = "1.21";
            majorVersion = 21;
        } else {
            serverVersion = "unknown";
            majorVersion = 0;
        }
    }

    public static Main getInstance() {
        return instance;
    }
    
    public static boolean isFolia() {
        return isFolia;
    }
    
    public static String getServerVersion() {
        return serverVersion;
    }
    
    public static int getMajorVersion() {
        return majorVersion;
    }
    
    public static BukkitScheduler getScheduler() {
        if (isFolia) {
            try {
                // Use Folia's scheduler if available
                Class<?> foliaSchedulerClass = Class.forName("io.papermc.folia.scheduler.FoliaScheduler");
                return (BukkitScheduler) foliaSchedulerClass.getMethod("getInstance").invoke(null);
            } catch (Exception e) {
                // Fallback to regular scheduler
                return Bukkit.getScheduler();
            }
        }
        return Bukkit.getScheduler();
    }
    
    public static boolean isVersionAtLeast(int version) {
        return majorVersion >= version;
    }
    
    public static boolean isVersionBetween(int minVersion, int maxVersion) {
        return majorVersion >= minVersion && majorVersion <= maxVersion;
    }
} 