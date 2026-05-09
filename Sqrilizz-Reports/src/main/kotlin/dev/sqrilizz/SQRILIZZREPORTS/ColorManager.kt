package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.ChatColor
import java.util.regex.Pattern

/**
 * Менеджер цветов для красивого дизайна сообщений
 * Поддерживает hex цвета для современных версий и fallback для старых
 */
object ColorManager {
    
    private val HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})")
    private val colorCache = mutableMapOf<String, String>()
    private var useHexColors = true
    
    // Предустановленные цвета из конфига
    private var primaryColor = "#FF6B6B"
    private var secondaryColor = "#4ECDC4"
    private var successColor = "#45B7D1"
    private var warningColor = "#FFA726"
    private var errorColor = "#EF5350"
    private var infoColor = "#66BB6A"
    private var accentColor = "#AB47BC"
    
    /**
     * Инициализация цветов из конфига
     */
    @JvmStatic
    fun initialize() {
        val config = Main.getInstance().config
        
        useHexColors = config.getBoolean("design.use-hex-colors", true) && Main.isVersionAtLeast(16)
        
        if (config.contains("design.colors")) {
            primaryColor = config.getString("design.colors.primary", primaryColor) ?: primaryColor
            secondaryColor = config.getString("design.colors.secondary", secondaryColor) ?: secondaryColor
            successColor = config.getString("design.colors.success", successColor) ?: successColor
            warningColor = config.getString("design.colors.warning", warningColor) ?: warningColor
            errorColor = config.getString("design.colors.error", errorColor) ?: errorColor
            infoColor = config.getString("design.colors.info", infoColor) ?: infoColor
            accentColor = config.getString("design.colors.accent", accentColor) ?: accentColor
        }
        
        Main.getInstance().logger.info("ColorManager initialized with hex support: $useHexColors")
    }
    
    /**
     * Применяет цвета к тексту
     */
    @JvmStatic
    fun colorize(text: String?): String {
        if (text == null) return ""
        
        var result = text
            .replace("{primary}", getColor("primary"))
            .replace("{secondary}", getColor("secondary"))
            .replace("{success}", getColor("success"))
            .replace("{warning}", getColor("warning"))
            .replace("{error}", getColor("error"))
            .replace("{info}", getColor("info"))
            .replace("{accent}", getColor("accent"))
            .replace("{reset}", "§r")
            .replace("{bold}", "§l")
            .replace("{italic}", "§o")
        
        // Обрабатываем hex цвета если поддерживаются
        if (useHexColors) {
            result = translateHexColorCodes(result)
        }
        
        // Обрабатываем обычные цветовые коды
        return ChatColor.translateAlternateColorCodes('&', result)
    }
    
    /**
     * Получает цвет по названию
     */
    @JvmStatic
    fun getColor(colorName: String): String {
        val hexColor = getHexColor(colorName)
        
        return if (useHexColors) {
            translateHexColorCodes("&#${hexColor.substring(1)}")
        } else {
            getLegacyColor(colorName)
        }
    }
    
    /**
     * Проверяет, поддерживаются ли hex цвета
     */
    @JvmStatic
    fun isHexSupported(): Boolean = useHexColors
    
    /**
     * Получает hex цвет по названию
     */
    private fun getHexColor(colorName: String): String = when (colorName.lowercase()) {
        "primary" -> primaryColor
        "secondary" -> secondaryColor
        "success" -> successColor
        "warning" -> warningColor
        "error" -> errorColor
        "info" -> infoColor
        "accent" -> accentColor
        else -> "#FFFFFF"
    }
    
    /**
     * Получает legacy цвет для старых версий
     */
    private fun getLegacyColor(colorName: String): String = when (colorName.lowercase()) {
        "primary", "error" -> "§c"
        "secondary", "info" -> "§a"
        "success" -> "§9"
        "warning" -> "§6"
        "accent" -> "§d"
        else -> "§f"
    }
    
    /**
     * Переводит hex цвета в формат Minecraft
     */
    private fun translateHexColorCodes(message: String): String {
        val matcher = HEX_PATTERN.matcher(message)
        val buffer = StringBuffer(message.length + 4 * 8)
        
        while (matcher.find()) {
            val group = matcher.group(1)
            matcher.appendReplacement(buffer, buildString {
                append("§x")
                for (i in 0..5) {
                    append("§${group[i]}")
                }
            })
        }
        
        return matcher.appendTail(buffer).toString()
    }
    
    /**
     * Создает градиент между двумя цветами
     */
    @JvmStatic
    fun createGradient(text: String, startColor: String, endColor: String): String {
        if (!useHexColors || text.length <= 1) {
            return colorize(startColor) + text
        }
        
        return buildString {
            text.forEachIndexed { i, char ->
                val ratio = i.toFloat() / (text.length - 1)
                val interpolatedColor = interpolateColor(startColor, endColor, ratio)
                append(translateHexColorCodes("&#${interpolatedColor.substring(1)}"))
                append(char)
            }
        }
    }
    
    /**
     * Интерполирует между двумя hex цветами
     */
    private fun interpolateColor(color1: String, color2: String, ratio: Float): String {
        if (ratio <= 0) return color1
        if (ratio >= 1) return color2
        
        val r1 = color1.substring(1, 3).toInt(16)
        val g1 = color1.substring(3, 5).toInt(16)
        val b1 = color1.substring(5, 7).toInt(16)
        
        val r2 = color2.substring(1, 3).toInt(16)
        val g2 = color2.substring(3, 5).toInt(16)
        val b2 = color2.substring(5, 7).toInt(16)
        
        val r = (r1 + (r2 - r1) * ratio).toInt()
        val g = (g1 + (g2 - g1) * ratio).toInt()
        val b = (b1 + (b2 - b1) * ratio).toInt()
        
        return "#%02X%02X%02X".format(r, g, b)
    }
}
