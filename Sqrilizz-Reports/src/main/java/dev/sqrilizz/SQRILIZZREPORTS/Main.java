package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import dev.sqrilizz.SQRILIZZREPORTS.api.AuthManager;
import dev.sqrilizz.SQRILIZZREPORTS.api.RESTServer;
import dev.sqrilizz.SQRILIZZREPORTS.lifecycle.ShutdownManager;

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

            // Initialize security/auth and REST API configuration
            getLogger().info("Initializing AuthManager...");
            AuthManager.initialize();
            getLogger().info("AuthManager initialized");
            
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
            
            getLogger().info("Initializing optimized TelegramManager...");
            try {
                TelegramManager.initialize();
                getLogger().info("Optimized TelegramManager initialized");
            } catch (Exception e) {
                getLogger().warning("TelegramManager initialization failed: " + e.getMessage());
                getLogger().warning("Telegram features will be disabled.");
            }
            
            // Initialize MySQL optional support
            getLogger().info("Setting up optional MySQL support...");
            MySQLOptionalManager.setupMySQLInstructions();
            getLogger().info("MySQL support status: " + MySQLOptionalManager.getMySQLDriverInfo());
            
            getLogger().info("Initializing DiscordWebhookManager...");
            DiscordWebhookManager.initialize();
            getLogger().info("DiscordWebhookManager initialized");

            getLogger().info("Initializing AntiAbuseManager...");
            AntiAbuseManager.initialize();
            getLogger().info("AntiAbuseManager initialized");

            getLogger().info("Loading webhook configurations...");
            dev.sqrilizz.SQRILIZZREPORTS.api.WebhookManager.loadWebhooksFromConfig();
            getLogger().info("Webhook configurations loaded");

            // Initialize monitoring and lifecycle management
            getLogger().info("Initializing performance monitoring...");
            // PerformanceMonitor is static, no initialization needed
            getLogger().info("Performance monitoring initialized");
            
            getLogger().info("Initializing graceful shutdown system...");
            ShutdownManager.initialize();
            getLogger().info("Graceful shutdown system initialized");
            
            // Start REST Server last
            getLogger().info("Starting REST Server...");
            RESTServer.initialize();
            getLogger().info("REST Server initialization complete");
            
            // Register commands
            getLogger().info("Registering commands...");
            getCommand("report").setExecutor(new ReportCommand());
            getCommand("reports").setExecutor(new AdminReportsCommand());
            getCommand("report-telegram").setExecutor(new TelegramCommand());
            getCommand("report-language").setExecutor(new LanguageCommand());
            getCommand("report-webhook").setExecutor(new WebhookCommand());
            getCommand("report-reload").setExecutor(new ReloadCommand());
            
            // Register tab completers
            getLogger().info("Registering tab completers...");
            ReportsTabCompleter tabCompleter = new ReportsTabCompleter();
            getCommand("report").setTabCompleter(tabCompleter);
            getCommand("reports").setTabCompleter(tabCompleter);
            getCommand("report-language").setTabCompleter(tabCompleter);
            getCommand("report-telegram").setTabCompleter(tabCompleter);
            getCommand("report-webhook").setTabCompleter(tabCompleter);
            getCommand("report-reload").setTabCompleter(tabCompleter);
            getLogger().info("Commands and tab completers registered");
            
            // Log name cleaning stats
            getLogger().info("Name cleaning configuration: " + NameUtils.getCleaningStats());
            
            // Запускаем задачу очистки старых данных каждые 6 часов
            runTaskTimerAsync(() -> {
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
        ShutdownManager.performGracefulShutdown();
        getLogger().info("Shutting down optimized Telegram Manager...");
        TelegramManager.shutdown();
        getLogger().info("Shutting down REST Server...");
        RESTServer.shutdown();
        getLogger().info("SQRILIZZREPORTS has been disabled gracefully.");
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
        return Bukkit.getScheduler();
    }
    
    /**
     * Runs a task asynchronously, compatible with both Bukkit and Folia
     */
    public static void runTaskAsync(Runnable task) {
        if (isFolia) {
            try {
                // Use Folia's global region scheduler for async tasks
                Class<?> foliaClass = Class.forName("io.papermc.folia.Folia");
                Object globalRegionScheduler = foliaClass.getMethod("getGlobalRegionScheduler").invoke(null);
                globalRegionScheduler.getClass().getMethod("run", org.bukkit.plugin.Plugin.class, java.util.function.Consumer.class)
                    .invoke(globalRegionScheduler, instance, (java.util.function.Consumer<Object>) (o) -> task.run());
            } catch (Exception e) {
                // Fallback to regular async scheduler
                fallbackSchedulerAsync(task);
            }
        } else {
            fallbackSchedulerAsync(task);
        }
    }
    
    /**
     * Fallback async scheduler method for older versions
     */
    private static void fallbackSchedulerAsync(Runnable task) {
        try {
            Bukkit.getScheduler().runTaskAsynchronously(instance, task);
        } catch (UnsupportedOperationException e) {
            // Fallback to sync scheduler for very old versions
            try {
                instance.getLogger().warning("Async task not supported, falling back to sync scheduler");
                Bukkit.getScheduler().runTask(instance, task);
            } catch (UnsupportedOperationException e2) {
                // If even sync scheduler fails, run directly in a new thread
                instance.getLogger().warning("Bukkit scheduler not supported on this version, running task in new thread");
                Thread taskThread = new Thread(task);
                taskThread.setName("SQRILIZZREPORTS-AsyncTask");
                taskThread.setDaemon(true);
                taskThread.start();
            }
        }
    }
    
    /**
     * Runs a repeating task asynchronously, compatible with both Bukkit and Folia
     */
    public static void runTaskTimerAsync(Runnable task, long delay, long period) {
        if (isFolia) {
            try {
                // Use Folia's global region scheduler for async repeating tasks
                Class<?> foliaClass = Class.forName("io.papermc.folia.Folia");
                Object globalRegionScheduler = foliaClass.getMethod("getGlobalRegionScheduler").invoke(null);
                globalRegionScheduler.getClass().getMethod("runAtFixedRate", org.bukkit.plugin.Plugin.class, java.util.function.Consumer.class, long.class, long.class)
                    .invoke(globalRegionScheduler, instance, (java.util.function.Consumer<Object>) (o) -> task.run(), delay, period);
            } catch (Exception e) {
                // Fallback to regular scheduler
                fallbackSchedulerTimerAsync(task, delay, period);
            }
        } else {
            fallbackSchedulerTimerAsync(task, delay, period);
        }
    }
    
    /**
     * Fallback scheduler method for older versions that don't support lambda expressions
     */
    private static void fallbackSchedulerTimerAsync(Runnable task, long delay, long period) {
        try {
            // For very old versions (1.8.x), we need to be more careful with async tasks
            if (majorVersion <= 8) {
                // Try sync scheduler first for very old versions
                try {
                    Bukkit.getScheduler().runTaskTimer(instance, task, delay, period);
                } catch (UnsupportedOperationException e2) {
                    // If even sync scheduler fails, create a simple timer thread
                    instance.getLogger().warning("Bukkit scheduler not supported on this version, using simple timer");
                    createSimpleTimer(task, delay, period);
                }
            } else {
                // Use async scheduler for newer versions
                Bukkit.getScheduler().runTaskTimerAsynchronously(instance, task, delay, period);
            }
        } catch (UnsupportedOperationException e) {
            // Final fallback - try sync scheduler
            try {
                instance.getLogger().warning("Async timer not supported, falling back to sync scheduler");
                Bukkit.getScheduler().runTaskTimer(instance, task, delay, period);
            } catch (UnsupportedOperationException e2) {
                // If even sync scheduler fails, create a simple timer thread
                instance.getLogger().warning("Bukkit scheduler not supported on this version, using simple timer");
                createSimpleTimer(task, delay, period);
            }
        }
    }
    
    /**
     * Creates a simple timer thread for very old Minecraft versions that don't support Bukkit scheduler
     */
    private static void createSimpleTimer(Runnable task, long delay, long period) {
        Thread timerThread = new Thread(() -> {
            try {
                // Initial delay (convert ticks to milliseconds: 1 tick = 50ms)
                if (delay > 0) {
                    Thread.sleep(delay * 50);
                }
                
                // Run the task periodically
                while (!instance.isEnabled()) {
                    break;
                }
                
                while (instance.isEnabled()) {
                    try {
                        task.run();
                    } catch (Exception e) {
                        instance.getLogger().warning("Error in timer task: " + e.getMessage());
                    }
                    
                    // Wait for the next period (convert ticks to milliseconds)
                    Thread.sleep(period * 50);
                }
            } catch (InterruptedException e) {
                instance.getLogger().info("Timer thread interrupted");
                Thread.currentThread().interrupt();
            }
        });
        
        timerThread.setName("SQRILIZZREPORTS-Timer");
        timerThread.setDaemon(true);
        timerThread.start();
    }
    
    /**
     * Runs a sync task, compatible with both Bukkit and Folia
     */
    public static void runTask(Runnable task) {
        if (isFolia) {
            try {
                // Use Folia's global region scheduler for sync tasks
                Class<?> foliaClass = Class.forName("io.papermc.folia.Folia");
                Object globalRegionScheduler = foliaClass.getMethod("getGlobalRegionScheduler").invoke(null);
                globalRegionScheduler.getClass().getMethod("run", org.bukkit.plugin.Plugin.class, java.util.function.Consumer.class)
                    .invoke(globalRegionScheduler, instance, (java.util.function.Consumer<Object>) (o) -> task.run());
            } catch (Exception e) {
                // Fallback to regular scheduler
                try {
                    Bukkit.getScheduler().runTask(instance, task);
                } catch (UnsupportedOperationException e2) {
                    // If scheduler fails, run directly
                    instance.getLogger().warning("Sync task scheduler not supported, running task directly");
                    task.run();
                }
            }
        } else {
            try {
                Bukkit.getScheduler().runTask(instance, task);
            } catch (UnsupportedOperationException e) {
                // If scheduler fails, run directly
                instance.getLogger().warning("Sync task scheduler not supported, running task directly");
                task.run();
            }
        }
    }
    
    public static boolean isVersionAtLeast(int version) {
        return majorVersion >= version;
    }
    
    public static boolean isVersionBetween(int minVersion, int maxVersion) {
        return majorVersion >= minVersion && majorVersion <= maxVersion;
    }
} 