package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object LanguageManager {
    private var currentLanguage = "en"
    private val languageFiles = mutableMapOf<String, FileConfiguration>()
    
    @JvmStatic
    fun initialize() {
        val config = Main.getInstance().config
        currentLanguage = config.getString("language", "en") ?: "en"
        
        // Load language files
        loadLanguageFile("en")
        loadLanguageFile("ru")
        loadLanguageFile("ar")
        
        Main.getInstance().logger.info("Language set to: $currentLanguage")
    }
    
    private fun loadLanguageFile(lang: String) {
        try {
            val langFile = File(Main.getInstance().dataFolder, "messages_$lang.yml")
            
            // Save default language file if it doesn't exist
            if (!langFile.exists()) {
                Main.getInstance().saveResource("messages_$lang.yml", false)
            }
            
            // Load from file
            val langConfig = YamlConfiguration.loadConfiguration(langFile)
            
            // Load defaults from resources
            Main.getInstance().getResource("messages_$lang.yml")?.use { stream ->
                val defConfig = YamlConfiguration.loadConfiguration(
                    InputStreamReader(stream, StandardCharsets.UTF_8)
                )
                langConfig.setDefaults(defConfig)
            }
            
            languageFiles[lang] = langConfig
            Main.getInstance().logger.info("Loaded language file: messages_$lang.yml")
        } catch (e: Exception) {
            Main.getInstance().logger.warning("Failed to load language file for: $lang")
            e.printStackTrace()
        }
    }
    
    @JvmStatic
    fun getMessage(key: String): String {
        val langConfig = languageFiles[currentLanguage] ?: languageFiles["en"]
        
        val message = langConfig?.getString(key) 
            ?: languageFiles["en"]?.getString(key, "Message not found: $key")
            ?: "Message not found: $key"
        
        // Apply color formatting through ColorManager
        return ColorManager.colorize(message)
    }
    
    @JvmStatic
    fun getRawMessage(key: String): String {
        val langConfig = languageFiles[currentLanguage] ?: languageFiles["en"]
        
        return langConfig?.getString(key)
            ?: languageFiles["en"]?.getString(key, "Message not found: $key")
            ?: "Message not found: $key"
    }
    
    @JvmStatic
    fun setLanguage(language: String) {
        currentLanguage = language
        Main.getInstance().config.set("language", language)
        Main.getInstance().saveConfig()
    }
    
    @JvmStatic
    fun getCurrentLanguage(): String = currentLanguage
    
    @JvmStatic
    fun reload() {
        languageFiles.clear()
        initialize()
    }
}
