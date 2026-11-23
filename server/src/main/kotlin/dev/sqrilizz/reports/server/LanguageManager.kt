package dev.sqrilizz.reports.server

import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object LanguageManager {
    private var currentLanguage = "en"
    private val languageFiles = mutableMapOf<String, FileConfiguration>()

    fun init(plugin: ReportsPlugin) {
        currentLanguage = plugin.config.getString("language", "en")?.lowercase() ?: "en"
        listOf("en", "ru", "ar").forEach { lang ->
            loadLanguageFile(plugin, lang)
        }
        plugin.logger.info("LanguageManager initialized. Active language: $currentLanguage")
    }

    private fun loadLanguageFile(plugin: ReportsPlugin, lang: String) {
        try {
            val file = File(plugin.dataFolder, "messages_$lang.yml")
            if (!file.exists()) {
                plugin.saveResource("messages_$lang.yml", false)
            }
            val config = YamlConfiguration.loadConfiguration(file)
            plugin.getResource("messages_$lang.yml")?.use { stream ->
                val def = YamlConfiguration.loadConfiguration(InputStreamReader(stream, StandardCharsets.UTF_8))
                config.setDefaults(def)
            }
            languageFiles[lang] = config
        } catch (e: Exception) {
            plugin.logger.warning("Failed to load language file messages_$lang.yml: ${e.message}")
        }
    }

    fun get(key: String, vararg replacements: Pair<String, Any>): String {
        val langConfig = languageFiles[currentLanguage] ?: languageFiles["en"]
        var msg = langConfig?.getString(key) ?: languageFiles["en"]?.getString(key) ?: key
        replacements.forEach { (placeholder, value) ->
            msg = msg.replace("[$placeholder]", value.toString()).replace("{$placeholder}", value.toString())
        }
        return ColorManager.colorize(msg)
    }

    fun getComponent(key: String, vararg replacements: Pair<String, Any>): Component {
        return ColorManager.component(get(key, *replacements))
    }

    fun setLanguage(plugin: ReportsPlugin, lang: String): Boolean {
        if (lang !in languageFiles) return false
        currentLanguage = lang
        plugin.config.set("language", lang)
        plugin.saveConfig()
        return true
    }

    fun getCurrentLanguage(): String = currentLanguage

    fun reload(plugin: ReportsPlugin) {
        languageFiles.clear()
        init(plugin)
    }
}
