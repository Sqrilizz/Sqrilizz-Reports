package dev.sqrilizz.SQRILIZZREPORTS;

import java.util.regex.Pattern;

public class NameUtils {
    
    // Pattern для удаления всех цветовых кодов Minecraft
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§[0-9a-fk-or]");
    
    // Pattern для удаления hex цветов (формат §x§f§f§2§5§7§5§2§5§7)
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("§x(§[0-9a-f]){6}");
    
    // Pattern для удаления форматирования (жирный, курсив, подчеркнутый и т.д.)
    private static final Pattern FORMATTING_PATTERN = Pattern.compile("§[lmnok]");
    
    // Pattern для удаления сброса форматирования
    private static final Pattern RESET_PATTERN = Pattern.compile("§r");
    
    /**
     * Очищает ник игрока от всех цветовых кодов и форматирования
     * @param name Исходный ник (может содержать цветовые коды)
     * @return Очищенный ник без цветовых кодов
     */
    public static String cleanPlayerName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        
        // Проверяем, включена ли очистка имен
        if (!Main.getInstance().getConfig().getBoolean("name-cleaning.enabled", true)) {
            return name;
        }
        
        // Удаляем все типы цветовых кодов и форматирования
        String cleaned = name;
        
        // Удаляем hex цвета (§x§f§f§2§5§7§5§2§5§7)
        if (Main.getInstance().getConfig().getBoolean("name-cleaning.remove-hex-colors", true)) {
            cleaned = HEX_COLOR_PATTERN.matcher(cleaned).replaceAll("");
        }
        
        // Удаляем обычные цветовые коды (§a, §b, §c и т.д.)
        if (Main.getInstance().getConfig().getBoolean("name-cleaning.remove-color-codes", true)) {
            cleaned = COLOR_CODE_PATTERN.matcher(cleaned).replaceAll("");
        }
        
        // Удаляем форматирование (§l, §m, §n, §o, §k)
        if (Main.getInstance().getConfig().getBoolean("name-cleaning.remove-formatting", true)) {
            cleaned = FORMATTING_PATTERN.matcher(cleaned).replaceAll("");
        }
        
        // Удаляем сброс форматирования (§r)
        if (Main.getInstance().getConfig().getBoolean("name-cleaning.remove-reset-codes", true)) {
            cleaned = RESET_PATTERN.matcher(cleaned).replaceAll("");
        }
        
        // Убираем лишние пробелы в начале и конце
        cleaned = cleaned.trim();
        
        // Если после очистки имя пустое, возвращаем оригинал
        if (cleaned.isEmpty()) {
            return name;
        }
        
        // Логируем очистку если включено
        if (Main.getInstance().getConfig().getBoolean("name-cleaning.log-cleaning", false)) {
            if (containsColorCodes(name)) {
                Main.getInstance().getLogger().info("Cleaned player name: '" + name + "' -> '" + cleaned + "'");
            }
        }
        
        return cleaned;
    }
    
    /**
     * Очищает ник игрока и обрезает до разумной длины
     * @param name Исходный ник
     * @param maxLength Максимальная длина (по умолчанию из конфигурации)
     * @return Очищенный и обрезанный ник
     */
    public static String cleanPlayerName(String name, int maxLength) {
        String cleaned = cleanPlayerName(name);
        
        if (cleaned.length() > maxLength) {
            cleaned = cleaned.substring(0, maxLength);
        }
        
        return cleaned;
    }
    
    /**
     * Очищает ник игрока с настройками из конфигурации
     * @param name Исходный ник
     * @return Очищенный ник
     */
    public static String cleanPlayerNameWithConfig(String name) {
        int maxLength = Main.getInstance().getConfig().getInt("name-cleaning.max-name-length", 16);
        return cleanPlayerName(name, maxLength);
    }
    
    /**
     * Проверяет, содержит ли ник цветовые коды
     * @param name Ник для проверки
     * @return true если содержит цветовые коды
     */
    public static boolean containsColorCodes(String name) {
        if (name == null) return false;
        return name.contains("§");
    }
    
    /**
     * Получает чистый ник для отображения в отчетах
     * @param name Исходный ник
     * @return Очищенный ник для отображения
     */
    public static String getDisplayName(String name) {
        return cleanPlayerNameWithConfig(name);
    }
    
    /**
     * Получает чистый ник для логирования
     * @param name Исходный ник
     * @return Очищенный ник для логирования
     */
    public static String getLogName(String name) {
        return cleanPlayerName(name, 32);
    }
    
    /**
     * Получает статистику очистки имен
     * @return Строка с информацией о настройках очистки
     */
    public static String getCleaningStats() {
        return String.format("Name cleaning: %s (hex: %s, colors: %s, formatting: %s, reset: %s, maxLength: %d)",
            Main.getInstance().getConfig().getBoolean("name-cleaning.enabled", true),
            Main.getInstance().getConfig().getBoolean("name-cleaning.remove-hex-colors", true),
            Main.getInstance().getConfig().getBoolean("name-cleaning.remove-color-codes", true),
            Main.getInstance().getConfig().getBoolean("name-cleaning.remove-formatting", true),
            Main.getInstance().getConfig().getBoolean("name-cleaning.remove-reset-codes", true),
            Main.getInstance().getConfig().getInt("name-cleaning.max-name-length", 16)
        );
    }
}
