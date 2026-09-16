package dev.sqrilizz.reports.server

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import java.util.regex.Pattern

object ColorManager {
    private val HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})")
    private val legacySerializer = LegacyComponentSerializer.legacySection()

    private var useHexColors = true
    private var primaryColor = "#FF6B6B"
    private var secondaryColor = "#4ECDC4"
    private var successColor = "#45B7D1"
    private var warningColor = "#FFA726"
    private var errorColor = "#EF5350"
    private var infoColor = "#66BB6A"
    private var accentColor = "#AB47BC"

    fun init(plugin: ReportsPlugin) {
        val config = plugin.config
        useHexColors = config.getBoolean("design.use-hex-colors", true)
        primaryColor = config.getString("design.colors.primary", primaryColor) ?: primaryColor
        secondaryColor = config.getString("design.colors.secondary", secondaryColor) ?: secondaryColor
        successColor = config.getString("design.colors.success", successColor) ?: successColor
        warningColor = config.getString("design.colors.warning", warningColor) ?: warningColor
        errorColor = config.getString("design.colors.error", errorColor) ?: errorColor
        infoColor = config.getString("design.colors.info", infoColor) ?: infoColor
        accentColor = config.getString("design.colors.accent", accentColor) ?: accentColor
    }

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

        if (useHexColors) {
            result = translateHexColorCodes(result)
        }
        @Suppress("DEPRECATION")
        return ChatColor.translateAlternateColorCodes('&', result)
    }

    fun component(text: String?): Component {
        val colored = colorize(text)
        return legacySerializer.deserialize(colored)
    }

    fun getColor(colorName: String): String {
        val hex = getHexColor(colorName)
        return if (useHexColors) {
            translateHexColorCodes("&#${hex.removePrefix("#")}")
        } else {
            getLegacyColor(colorName)
        }
    }

    private fun getHexColor(name: String): String = when (name.lowercase()) {
        "primary" -> primaryColor
        "secondary" -> secondaryColor
        "success" -> successColor
        "warning" -> warningColor
        "error" -> errorColor
        "info" -> infoColor
        "accent" -> accentColor
        else -> "#FFFFFF"
    }

    private fun getLegacyColor(name: String): String = when (name.lowercase()) {
        "primary", "error" -> "§c"
        "secondary", "info" -> "§a"
        "success" -> "§9"
        "warning" -> "§6"
        "accent" -> "§d"
        else -> "§f"
    }

    private fun translateHexColorCodes(message: String): String {
        val matcher = HEX_PATTERN.matcher(message)
        val buffer = StringBuffer(message.length + 32)
        while (matcher.find()) {
            val hex = matcher.group(1)
            val builder = StringBuilder("§x")
            for (ch in hex) {
                builder.append('§').append(ch)
            }
            matcher.appendReplacement(buffer, builder.toString())
        }
        matcher.appendTail(buffer)
        return buffer.toString()
    }
}
