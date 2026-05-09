package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class BugReportCommand implements CommandExecutor {
    
    // Доступные категории багов
    private static final List<String> BUG_CATEGORIES = Arrays.asList(
        "duplication", "dupe",      // Дюп предметов
        "crash", "server-crash",    // Краш сервера
        "exploit", "glitch",        // Эксплойты
        "performance", "lag",       // Проблемы с производительностью
        "gameplay", "mechanic",     // Игровая механика
        "world", "generation",      // Генерация мира
        "inventory", "items",       // Инвентарь/предметы
        "commands", "cmd",          // Команды
        "permissions", "perms",     // Права доступа
        "economy", "money",         // Экономика
        "other", "misc"             // Прочее
    );
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("bugreport-usage"));
            sendCategoriesList(player);
            return true;
        }

        String category = args[0].toLowerCase();
        String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (description.isEmpty()) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("bugreport-usage"));
            return true;
        }

        // Проверяем валидность категории
        if (!isValidCategory(category)) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("bugreport-invalid-category")
                .replace("[CATEGORY]", category));
            sendCategoriesList(player);
            return true;
        }

        // Нормализуем категорию
        String normalizedCategory = normalizeCategory(category);

        // Проверяем кулдаун
        if (CooldownManager.hasCooldown(VersionUtils.getPlayerUUID(player))) {
            long remainingTime = CooldownManager.getRemainingTime(VersionUtils.getPlayerUUID(player));
            VersionUtils.sendMessage(player, LanguageManager.getMessage("cooldown-message")
                .replace("[COOLDOWN]", String.valueOf(remainingTime)));
            return true;
        }

        // Проверяем систему защиты от злоупотреблений для багрепортов
        if (!AntiAbuseManager.canReport(player, "BUG_REPORT")) {
            return true;
        }

        // Отправляем багрепорт с категорией
        ReportManager.addBugReport(player, normalizedCategory, description);
        
        // Регистрируем багрепорт в системе защиты от злоупотреблений
        AntiAbuseManager.recordReport(player, "BUG_REPORT");
        
        // Устанавливаем кулдаун
        CooldownManager.setCooldown(VersionUtils.getPlayerUUID(player));
        
        // Уведомляем игрока об успешной отправке
        VersionUtils.sendMessage(player, LanguageManager.getMessage("bugreport-success")
            .replace("[CATEGORY]", getCategoryDisplayName(normalizedCategory))
            .replace("[DESCRIPTION]", description));
        
        return true;
    }
    
    private boolean isValidCategory(String category) {
        return BUG_CATEGORIES.contains(category.toLowerCase());
    }
    
    private String normalizeCategory(String category) {
        category = category.toLowerCase();
        // Нормализуем синонимы к основным категориям
        switch (category) {
            case "dupe": return "duplication";
            case "server-crash": return "crash";
            case "glitch": return "exploit";
            case "lag": return "performance";
            case "mechanic": return "gameplay";
            case "generation": return "world";
            case "items": return "inventory";
            case "cmd": return "commands";
            case "perms": return "permissions";
            case "money": return "economy";
            case "misc": return "other";
            default: return category;
        }
    }
    
    private String getCategoryDisplayName(String category) {
        return LanguageManager.getMessage("bugreport-category-" + category);
    }
    
    private void sendCategoriesList(Player player) {
        VersionUtils.sendMessage(player, LanguageManager.getMessage("bugreport-categories-header"));
        
        // Группируем категории для отображения
        String[] mainCategories = {"duplication", "crash", "exploit", "performance", "gameplay", 
                                   "world", "inventory", "commands", "permissions", "economy", "other"};
        
        for (String cat : mainCategories) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("bugreport-category-item")
                .replace("[CATEGORY]", cat)
                .replace("[DESCRIPTION]", getCategoryDisplayName(cat)));
        }
    }
}
