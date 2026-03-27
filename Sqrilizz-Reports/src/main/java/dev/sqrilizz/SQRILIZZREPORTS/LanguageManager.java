package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static String currentLanguage = "en";
    private static final Map<String, FileConfiguration> languageFiles = new HashMap<>();
    
    public static void initialize() {
        FileConfiguration config = Main.getInstance().getConfig();
        currentLanguage = config.getString("language", "en");
        
        // Load language files
        loadLanguageFile("en");
        loadLanguageFile("ru");
        loadLanguageFile("ar");
        
        Main.getInstance().getLogger().info("Language set to: " + currentLanguage);
    }
    
    private static void loadLanguageFile(String lang) {
        try {
            File langFile = new File(Main.getInstance().getDataFolder(), "messages_" + lang + ".yml");
            
            // Save default language file if it doesn't exist
            if (!langFile.exists()) {
                Main.getInstance().saveResource("messages_" + lang + ".yml", false);
            }
            
            // Load from file
            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
            
            // Load defaults from resources
            InputStream defStream = Main.getInstance().getResource("messages_" + lang + ".yml");
            if (defStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defStream, StandardCharsets.UTF_8)
                );
                langConfig.setDefaults(defConfig);
            }
            
            languageFiles.put(lang, langConfig);
            Main.getInstance().getLogger().info("Loaded language file: messages_" + lang + ".yml");
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("Failed to load language file for: " + lang);
            e.printStackTrace();
        }
    }
    
    public static String getMessage(String key) {
        FileConfiguration langConfig = languageFiles.get(currentLanguage);
        
        if (langConfig == null) {
            langConfig = languageFiles.get("en");
        }
        
        String message = langConfig.getString(key);
        
        if (message == null) {
            // Fallback to English if message not found
            FileConfiguration enConfig = languageFiles.get("en");
            if (enConfig != null) {
                message = enConfig.getString(key, "Message not found: " + key);
            } else {
                message = "Message not found: " + key;
            }
        }
        
        // Apply color formatting through ColorManager
        return ColorManager.colorize(message);
    }
    
    public static String getRawMessage(String key) {
        FileConfiguration langConfig = languageFiles.get(currentLanguage);
        
        if (langConfig == null) {
            langConfig = languageFiles.get("en");
        }
        
        String message = langConfig.getString(key);
        
        if (message == null) {
            // Fallback to English if message not found
            FileConfiguration enConfig = languageFiles.get("en");
            if (enConfig != null) {
                message = enConfig.getString(key, "Message not found: " + key);
            } else {
                message = "Message not found: " + key;
            }
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
    
    public static void reload() {
        languageFiles.clear();
        initialize();
    }
}
