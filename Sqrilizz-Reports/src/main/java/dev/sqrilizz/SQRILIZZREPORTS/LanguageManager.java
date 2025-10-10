package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.configuration.file.FileConfiguration;

public class LanguageManager {
    private static String currentLanguage = "en";
    
    public static void initialize() {
        FileConfiguration config = Main.getInstance().getConfig();
        currentLanguage = config.getString("language", "en");
        Main.getInstance().getLogger().info("Language set to: " + currentLanguage);
    }
    
    public static String getMessage(String key) {
        FileConfiguration config = Main.getInstance().getConfig();
        String message = config.getString("messages." + currentLanguage + "." + key);
        
        if (message == null) {
            // Fallback to English if message not found
            message = config.getString("messages.en." + key, "Message not found: " + key);
        }
        
        // Apply color formatting through ColorManager
        return ColorManager.colorize(message);
    }
    
    public static String getRawMessage(String key) {
        FileConfiguration config = Main.getInstance().getConfig();
        String message = config.getString("messages." + currentLanguage + "." + key);
        
        if (message == null) {
            // Fallback to English if message not found
            message = config.getString("messages.en." + key, "Message not found: " + key);
        }
        
        return message;
    }
    
    public static void setLanguage(String language) {
        currentLanguage = language;
        Main.getInstance().getConfig().set("language", language);
        Main.getInstance().saveConfig();
    }
    
    public static String getCurrentLanguage() {
        return currentLanguage;
    }
}