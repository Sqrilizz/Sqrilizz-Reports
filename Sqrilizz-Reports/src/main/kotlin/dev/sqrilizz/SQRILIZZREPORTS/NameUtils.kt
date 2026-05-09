package dev.sqrilizz.SQRILIZZREPORTS

import java.util.regex.Pattern

object NameUtils {
    
    // Patterns для удаления цветовых кодов Minecraft
    private val COLOR_CODE_PATTERN = Pattern.compile("§[0-9a-fk-or]")
    private val HEX_COLOR_PATTERN = Pattern.compile("§x(§[0-9a-f]){6}")
    private val FORMATTING_PATTERN = Pattern.compile("§[lmnok]")
    private val RESET_PATTERN = Pattern.compile("§r")
    
    /**
     * Очищает ник игрока от всех цветовых кодов и форматирования
     */
    @JvmStatic
    fun cleanPlayerName(name: String?): String {
        if (name.isNullOrEmpty()) return name ?: ""
        
        val config = Main.getInstance().config
        
        // Проверяем, включена ли очистка имен
        if (!config.getBoolean("name-cleaning.enabled", true)) {
            return name
        }
        
        var cleaned = name
        
        // Удаляем hex цвета
        if (config.getBoolean("name-cleaning.remove-hex-colors", true)) {
            cleaned = HEX_COLOR_PATTERN.matcher(cleaned).replaceAll("")
        }
        
        // Удаляем обычные цветовые коды
        if (config.getBoolean("name-cleaning.remove-color-codes", true)) {
            cleaned = COLOR_CODE_PATTERN.matcher(cleaned).replaceAll("")
        }
        
        // Удаляем форматирование
        if (config.getBoolean("name-cleaning.remove-formatting", true)) {
            cleaned = FORMATTING_PATTERN.matcher(cleaned).replaceAll("")
        }
        
        // Удаляем сброс форматирования
        if (config.getBoolean("name-cleaning.remove-reset-codes", true)) {
            cleaned = RESET_PATTERN.matcher(cleaned).replaceAll("")
        }
        
        // Убираем лишние пробелы
        cleaned = cleaned.trim()
        
        // Если после очистки имя пустое, возвращаем оригинал
        if (cleaned.isEmpty()) return name
        
        // Логируем очистку если включено
        if (config.getBoolean("name-cleaning.log-cleaning", false) && containsColorCodes(name)) {
            Main.getInstance().logger.info("Cleaned player name: '$name' -> '$cleaned'")
        }
        
        return cleaned
    }
    
    /**
     * Очищает ник игрока и обрезает до разумной длины
     */
    @JvmStatic
    fun cleanPlayerName(name: String?, maxLength: Int): String {
        val cleaned = cleanPlayerName(name)
        return if (cleaned.length > maxLength) {
            cleaned.substring(0, maxLength)
        } else {
            cleaned
        }
    }
    
    /**
     * Очищает ник игрока с настройками из конфигурации
     */
    @JvmStatic
    fun cleanPlayerNameWithConfig(name: String?): String {
        val maxLength = Main.getInstance().config.getInt("name-cleaning.max-name-length", 16)
        return cleanPlayerName(name, maxLength)
    }
    
    /**
     * Проверяет, содержит ли ник цветовые коды
     */
    @JvmStatic
    fun containsColorCodes(name: String?): Boolean = name?.contains("§") == true
    
    /**
     * Получает чистый ник для отображения в отчетах
     */
    @JvmStatic
    fun getDisplayName(name: String?): String = cleanPlayerNameWithConfig(name)
    
    /**
     * Получает чистый ник для логирования
     */
    @JvmStatic
    fun getLogName(name: String?): String = cleanPlayerName(name, 32)
    
    /**
     * Получает статистику очистки имен
     */
    @JvmStatic
    fun getCleaningStats(): String {
        val config = Main.getInstance().config
        return buildString {
            append("Name cleaning: ${config.getBoolean("name-cleaning.enabled", true)}")
            append(" (hex: ${config.getBoolean("name-cleaning.remove-hex-colors", true)}")
            append(", colors: ${config.getBoolean("name-cleaning.remove-color-codes", true)}")
            append(", formatting: ${config.getBoolean("name-cleaning.remove-formatting", true)}")
            append(", reset: ${config.getBoolean("name-cleaning.remove-reset-codes", true)}")
            append(", maxLength: ${config.getInt("name-cleaning.max-name-length", 16)})")
        }
    }
}
