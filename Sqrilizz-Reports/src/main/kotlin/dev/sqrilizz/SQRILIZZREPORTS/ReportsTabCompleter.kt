package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ReportsTabCompleter : TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> =
        when (command.name.lowercase()) {
            "report" -> handleReportCommand(sender, args)
            "bugreport" -> handleBugReportCommand(sender, args)
            "reports" -> handleReportsCommand(sender, args)
            "report-language" -> handleLanguageCommand(args)
            "report-telegram" -> handleTelegramCommand(args)
            "report-webhook" -> handleWebhookCommand(args)
            "report-discord" -> handleDiscordCommand(args)
            else -> emptyList()
        }

    private fun handleReportCommand(sender: CommandSender, args: Array<out String>): List<String> {
        if (args.size == 1) {
            val input = args[0].lowercase()
            return Bukkit.getOnlinePlayers()
                .filter { it.name.lowercase().startsWith(input) && it != sender }
                .map { it.name }
        }
        if (args.size == 2) {
            val input = args[1].lowercase()
            val reasons = listOf(
                "читерство", "хак", "флай", "спидхак", "килаура", "антинокбек",
                "cheating", "hacking", "fly", "speed", "killaura", "antikb",
                "غش", "هاك", "طيران", "سرعة"
            )
            return reasons.filter { it.lowercase().startsWith(input) }
        }
        return emptyList()
    }

    private fun handleBugReportCommand(sender: CommandSender, args: Array<out String>): List<String> {
        if (args.size == 1) {
            val input = args[0].lowercase()
            val categories = listOf(
                "duplication", "dupe", "crash", "server-crash",
                "exploit", "glitch", "performance", "lag",
                "gameplay", "mechanic", "world", "generation",
                "inventory", "items", "commands", "cmd",
                "permissions", "perms", "economy", "money",
                "other", "misc"
            )
            return categories.filter { it.startsWith(input) }
        }
        if (args.size == 2) {
            val category = args[0].lowercase()
            val input = args[1].lowercase()
            val examples = when (category) {
                "duplication", "dupe" -> listOf("Items duplicating in chest", "Дюп предметов в сундуке", "مضاعفة العناصر")
                "crash", "server-crash" -> listOf("Server crashes when...", "Сервер крашится когда...", "تعطل السيرفر عند...")
                "exploit", "glitch" -> listOf("Can bypass protection", "Можно обойти защиту", "يمكن تجاوز الحماية")
                "performance", "lag" -> listOf("Severe lag in...", "Сильные лаги в...", "تأخير شديد في...")
                "gameplay", "mechanic" -> listOf("Broken mechanic...", "Сломанная механика...", "ميكانيكا معطلة...")
                "world", "generation" -> listOf("World generation bug", "Баг генерации мира", "خلل في توليد العالم")
                "inventory", "items" -> listOf("Items disappearing", "Предметы пропадают", "اختفاء العناصر")
                "commands", "cmd" -> listOf("Command not working", "Команда не работает", "الأمر لا يعمل")
                "permissions", "perms" -> listOf("Permission not applied", "Права не применяются", "الصلاحيات لا تعمل")
                "economy", "money" -> listOf("Money duplication", "Дюп денег", "مضاعفة الأموال")
                "other", "misc" -> listOf("Description of the issue", "Описание проблемы", "وصف المشكلة")
                else -> emptyList()
            }
            return examples.filter { it.lowercase().startsWith(input) }
        }
        return emptyList()
    }

    private fun handleReportsCommand(sender: CommandSender, args: Array<out String>): List<String> {
        if (args.size == 1) {
            val input = args[0].lowercase()
            return listOf("gui", "list", "clear", "check", "false", "clearall")
                .filter { it.startsWith(input) }
        }
        if (args.size == 2) {
            val subCommand = args[0].lowercase()
            val input = args[1].lowercase()
            if (subCommand in listOf("clear", "check", "false")) {
                return Bukkit.getOnlinePlayers()
                    .filter { it.name.lowercase().startsWith(input) }
                    .map { it.name }
            }
        }
        return emptyList()
    }

    private fun handleLanguageCommand(args: Array<out String>): List<String> {
        if (args.size == 1) {
            val input = args[0].lowercase()
            return listOf("en", "ru", "ar").filter { it.startsWith(input) }
        }
        return emptyList()
    }

    private fun handleTelegramCommand(args: Array<out String>): List<String> {
        if (args.size == 1) {
            val input = args[0].lowercase()
            return listOf("token", "chat").filter { it.startsWith(input) }
        }
        return emptyList()
    }

    private fun handleWebhookCommand(args: Array<out String>): List<String> {
        if (args.size == 1) {
            val input = args[0].lowercase()
            return listOf("set", "remove").filter { it.startsWith(input) }
        }
        return emptyList()
    }

    private fun handleDiscordCommand(args: Array<out String>): List<String> {
        if (args.size == 1) {
            val input = args[0].lowercase()
            return listOf("set", "remove", "test").filter { it.startsWith(input) }
        }
        return emptyList()
    }
}
