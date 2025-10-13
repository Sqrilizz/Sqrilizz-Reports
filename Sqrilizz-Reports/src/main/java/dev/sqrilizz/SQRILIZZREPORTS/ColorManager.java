package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Менеджер цветов для красивого дизайна сообщений
 * Поддерживает hex цвета для современных версий и fallback для старых
 */
public class ColorManager {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Map<String, String> colorCache = new HashMap<>();
    private static boolean useHexColors = true;
    
    // Предустановленные цвета из конфига
    private static String PRIMARY_COLOR = "#FF6B6B";
    private static String SECONDARY_COLOR = "#4ECDC4";
    private static String SUCCESS_COLOR = "#45B7D1";
    private static String WARNING_COLOR = "#FFA726";
    private static String ERROR_COLOR = "#EF5350";
    private static String INFO_COLOR = "#66BB6A";
    private static String ACCENT_COLOR = "#AB47BC";
    
    /**
     * Инициализация цветов из конфига
     */
    public static void initialize() {
        FileConfiguration config = Main.getInstance().getConfig();
        
        useHexColors = config.getBoolean("design.use-hex-colors", true) && Main.isVersionAtLeast(16);
        
        if (config.contains("design.colors")) {
            PRIMARY_COLOR = config.getString("design.colors.primary", PRIMARY_COLOR);
            SECONDARY_COLOR = config.getString("design.colors.secondary", SECONDARY_COLOR);
            SUCCESS_COLOR = config.getString("design.colors.success", SUCCESS_COLOR);
            WARNING_COLOR = config.getString("design.colors.warning", WARNING_COLOR);
            ERROR_COLOR = config.getString("design.colors.error", ERROR_COLOR);
            INFO_COLOR = config.getString("design.colors.info", INFO_COLOR);
            ACCENT_COLOR = config.getString("design.colors.accent", ACCENT_COLOR);
        }
        
        Main.getInstance().getLogger().info("ColorManager initialized with hex support: " + useHexColors);
    }
    
    /**
     * Применяет цвета к тексту
     */
    public static String colorize(String text) {
        if (text == null) return "";
        
        // Заменяем цветовые теги
        text = text.replace("{primary}", getColor("primary"))
                  .replace("{secondary}", getColor("secondary"))
                  .replace("{success}", getColor("success"))
                  .replace("{warning}", getColor("warning"))
                  .replace("{error}", getColor("error"))
                  .replace("{info}", getColor("info"))
                  .replace("{accent}", getColor("accent"))
                  .replace("{reset}", "§r")
                  .replace("{bold}", "§l")
                  .replace("{italic}", "§o");
        
        // Обрабатываем hex цвета если поддерживаются
        if (useHexColors) {
            text = translateHexColorCodes(text);
        }
        
        // Обрабатываем обычные цветовые коды
        text = ChatColor.translateAlternateColorCodes('&', text);
        
        return text;
    }
    
    /**
     * Получает цвет по названию
     */
    public static String getColor(String colorName) {
        String hexColor = getHexColor(colorName);
        
        if (useHexColors) {
            return translateHexColorCodes("&#" + hexColor.substring(1));
        } else {
            return getLegacyColor(colorName);
        }
    }
    
    /**
     * Проверяет, поддерживаются ли hex цвета
     */
    public static boolean isHexSupported() {
        return useHexColors;
    }
    
    /**
     * Получает hex цвет по названию
     */
    private static String getHexColor(String colorName) {
        switch (colorName.toLowerCase()) {
            case "primary":
                return PRIMARY_COLOR;
            case "secondary":
                return SECONDARY_COLOR;
            case "success":
                return SUCCESS_COLOR;
            case "warning":
                return WARNING_COLOR;
            case "error":
                return ERROR_COLOR;
            case "info":
                return INFO_COLOR;
            case "accent":
                return ACCENT_COLOR;
            default:
                return "#FFFFFF";
        }
    }
    
    /**
     * Получает legacy цвет для старых версий
     */
    private static String getLegacyColor(String colorName) {
        switch (colorName.toLowerCase()) {
            case "primary":
            case "error":
                return "§c";
            case "secondary":
            case "info":
                return "§a";
            case "success":
                return "§9";
            case "warning":
                return "§6";
            case "accent":
                return "§d";
            default:
                return "§f";
        }
    }
    
    /**
     * Переводит hex цвета в формат Minecraft
     */
    private static String translateHexColorCodes(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, "§x"
                + "§" + group.charAt(0) + "§" + group.charAt(1)
                + "§" + group.charAt(2) + "§" + group.charAt(3)
                + "§" + group.charAt(4) + "§" + group.charAt(5));
        }
        
        return matcher.appendTail(buffer).toString();
    }
    
    /**
     * Создает градиент между двумя цветами
     */
    public static String createGradient(String text, String startColor, String endColor) {
        if (!useHexColors || text.length() <= 1) {
            return colorize(startColor) + text;
        }
        
        StringBuilder result = new StringBuilder();
        int length = text.length();
        
        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            String interpolatedColor = interpolateColor(startColor, endColor, ratio);
            result.append(translateHexColorCodes("&#" + interpolatedColor.substring(1)))
                  .append(text.charAt(i));
        }
        
        return result.toString();
    }
    
    /**
     * Интерполирует между двумя hex цветами
     */
    private static String interpolateColor(String color1, String color2, float ratio) {
        if (ratio <= 0) return color1;
        if (ratio >= 1) return color2;
        
        int r1 = Integer.parseInt(color1.substring(1, 3), 16);
        int g1 = Integer.parseInt(color1.substring(3, 5), 16);
        int b1 = Integer.parseInt(color1.substring(5, 7), 16);
        
        int r2 = Integer.parseInt(color2.substring(1, 3), 16);
        int g2 = Integer.parseInt(color2.substring(3, 5), 16);
        int b2 = Integer.parseInt(color2.substring(5, 7), 16);
        
        int r = Math.round(r1 + (r2 - r1) * ratio);
        int g = Math.round(g1 + (g2 - g1) * ratio);
        int b = Math.round(b1 + (b2 - b1) * ratio);
        
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
