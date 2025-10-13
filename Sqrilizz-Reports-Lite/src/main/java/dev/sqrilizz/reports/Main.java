package dev.sqrilizz.reports;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Sqrilizz-Reports Lite v8.0 - Simplified but powerful report system
 * Only essential features for maximum performance and simplicity
 */
public class Main extends JavaPlugin {
    
    private static Main instance;
    private DatabaseManager database;
    private CacheManager cache;
    private TelegramManager telegram;
    private WebhookManager webhook;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize core systems
        initializeDatabase();
        initializeCache();
        initializeNotifications();
        
        // Register commands
        registerCommands();
        
        getLogger().info("Sqrilizz-Reports Lite v1.0 enabled successfully!");
        getLogger().info("Simple. Fast. Reliable.");
    }
    
    @Override
    public void onDisable() {
        // Clean shutdown
        if (database != null) database.close();
        if (cache != null) cache.cleanup();
        
        getLogger().info("Sqrilizz-Reports Lite disabled.");
    }
    
    private void initializeDatabase() {
        try {
            database = new DatabaseManager(this);
            database.initialize();
        } catch (Exception e) {
            getLogger().severe("Failed to initialize database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    private void initializeCache() {
        cache = new CacheManager();
        cache.initialize();
    }
    
    private void initializeNotifications() {
        // Initialize Telegram if enabled
        if (getConfig().getBoolean("telegram.enabled", false)) {
            telegram = new TelegramManager(this);
            telegram.initialize();
        }
        
        // Initialize Webhooks if enabled
        if (getConfig().getBoolean("webhook.enabled", false)) {
            webhook = new WebhookManager(this);
        }
    }
    
    private void registerCommands() {
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("reports").setExecutor(new ReportsCommand(this));
    }
    
    // Getters
    public static Main getInstance() { return instance; }
    public DatabaseManager getDatabaseManager() { return database; }
    public CacheManager getCache() { return cache; }
    public TelegramManager getTelegram() { return telegram; }
    public WebhookManager getWebhook() { return webhook; }
    
    // Utility method for async tasks
    public void runAsync(Runnable task) {
        getServer().getScheduler().runTaskAsynchronously(this, task);
    }
}
