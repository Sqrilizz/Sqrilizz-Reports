package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BugReportCommand : CommandExecutor {

    companion object {
        private val BUG_CATEGORIES = listOf(
            "duplication", "dupe",
            "crash", "server-crash",
            "exploit", "glitch",
            "performance", "lag",
            "gameplay", "mechanic",
            "world", "generation",
            "inventory", "items",
            "commands", "cmd",
            "permissions", "perms",
            "economy", "money",
            "other", "misc"
        )

        private val CATEGORY_ALIASES = mapOf(
            "dupe" to "duplication",
            "server-crash" to "crash",
            "glitch" to "exploit",
            "lag" to "performance",
            "mechanic" to "gameplay",
            "generation" to "world",
            "items" to "inventory",
            "cmd" to "commands",
            "perms" to "permissions",
            "money" to "economy",
            "misc" to "other"
        )
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(LanguageManager.getMessage("player-only"))
            return true
        }

        if (args.size < 2) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("bugreport-usage"))
            sendCategoriesList(sender)
            return true
        }

        val category = args[0].lowercase()
        val description = args.drop(1).joinToString(" ")

        if (description.isEmpty()) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("bugreport-usage"))
            return true
        }

        if (category !in BUG_CATEGORIES) {
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("bugreport-invalid-category")
                .replace("[CATEGORY]", category))
            sendCategoriesList(sender)
            return true
        }

        val normalizedCategory = CATEGORY_ALIASES[category] ?: category

        if (CooldownManager.hasCooldown(VersionUtils.getPlayerUUID(sender))) {
            val remainingTime = CooldownManager.getRemainingTime(VersionUtils.getPlayerUUID(sender))
            VersionUtils.sendMessage(sender, LanguageManager.getMessage("cooldown-message")
                .replace("[COOLDOWN]", remainingTime.toString()))
            return true
        }

        if (!AntiAbuseManager.canReport(sender, "BUG_REPORT")) {
            return true
        }

        ReportManager.addBugReport(sender, normalizedCategory, description)
        AntiAbuseManager.recordReport(sender, "BUG_REPORT")
        CooldownManager.setCooldown(VersionUtils.getPlayerUUID(sender))

        VersionUtils.sendMessage(sender, LanguageManager.getMessage("bugreport-success")
            .replace("[CATEGORY]", LanguageManager.getMessage("bugreport-category-$normalizedCategory"))
            .replace("[DESCRIPTION]", description))

        return true
    }

    private fun sendCategoriesList(player: Player) {
        VersionUtils.sendMessage(player, LanguageManager.getMessage("bugreport-categories-header"))

        val mainCategories = arrayOf(
            "duplication", "crash", "exploit", "performance", "gameplay",
            "world", "inventory", "commands", "permissions", "economy", "other"
        )

        for (cat in mainCategories) {
            val aliases = CATEGORY_ALIASES.entries
                .filter { it.value == cat }
                .joinToString(", ") { it.key }
            val aliasText = if (aliases.isNotEmpty()) " ($aliases)" else ""
            VersionUtils.sendMessage(player, LanguageManager.getMessage("bugreport-category-entry")
                .replace("[CATEGORY]", cat)
                .replace("[ALIASES]", aliasText)
                .replace("[DESCRIPTION]", LanguageManager.getMessage("bugreport-category-$cat")))
        }
    }
}
