package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LanguageManager {
    private static FileConfiguration config;
    private static String currentLanguage;

    public static void initialize() {
        File configFile = new File(Main.getInstance().getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            Main.getInstance().saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        currentLanguage = config.getString("language", "ru");
    }

    public static String getMessage(String key) {
        String path = "messages." + currentLanguage + "." + key;
        String message = config.getString(path);
        if (message == null) {
            return "Missing message: " + key;
        }
        return message;
    }

    public static void setLanguage(String language) {
        if (config.contains("messages." + language)) {
            currentLanguage = language;
            config.set("language", language);
            Main.getInstance().saveConfig();
        }
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }
} 