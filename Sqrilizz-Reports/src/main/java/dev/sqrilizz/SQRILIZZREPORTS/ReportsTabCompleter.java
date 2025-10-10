package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TabCompleter для всех команд плагина Sqrilizz-Reports
 * Предоставляет автодополнение для команд и их аргументов
 */
public class ReportsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        switch (command.getName().toLowerCase()) {
            case "report":
                return handleReportCommand(sender, args);
            case "reports":
                return handleReportsCommand(sender, args);
            case "report-language":
                return handleLanguageCommand(sender, args);
            case "report-telegram":
                return handleTelegramCommand(sender, args);
            case "report-webhook":
                return handleWebhookCommand(sender, args);
            case "report-discord":
                return handleDiscordCommand(sender, args);
            case "report-stats":
            case "report-reload":
                return completions; // Нет аргументов
        }
        
        return completions;
    }
    
    /**
     * Автодополнение для команды /report
     */
    private List<String> handleReportCommand(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Первый аргумент - имя игрока
            String input = args[0].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input) && !player.equals(sender)) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 2) {
            // Второй аргумент - примеры причин
            String input = args[1].toLowerCase();
            List<String> reasons = Arrays.asList(
                "читерство", "хак", "флай", "спидхак", "килаура", "антинокбек",
                "cheating", "hacking", "fly", "speed", "killaura", "antikb",
                "غش", "هاك", "طيران", "سرعة"
            );
            
            for (String reason : reasons) {
                if (reason.toLowerCase().startsWith(input)) {
                    completions.add(reason);
                }
            }
        }
        
        return completions;
    }
    
    /**
     * Автодополнение для команды /reports
     */
    private List<String> handleReportsCommand(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Первый аргумент - подкоманды
            String input = args[0].toLowerCase();
            List<String> subcommands = Arrays.asList("check", "clear", "clearall", "false");
            
            for (String subcmd : subcommands) {
                if (subcmd.startsWith(input)) {
                    completions.add(subcmd);
                }
            }
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("clearall")) {
            // Второй аргумент - имя игрока (кроме clearall)
            String input = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    completions.add(player.getName());
                }
            }
            
            // Добавляем оффлайн игроков из reports.yml
            try {
                for (String playerName : ReportManager.getReports().keySet()) {
                    if (playerName.toLowerCase().startsWith(input)) {
                        completions.add(playerName);
                    }
                }
            } catch (Exception e) {
                // Игнорируем ошибки
            }
        }
        
        return completions;
    }
    
    /**
     * Автодополнение для команды /report-language
     */
    private List<String> handleLanguageCommand(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> languages = Arrays.asList("en", "ru", "ar");
            
            for (String lang : languages) {
                if (lang.startsWith(input)) {
                    completions.add(lang);
                }
            }
        }
        
        return completions;
    }
    
    /**
     * Автодополнение для команды /report-telegram
     */
    private List<String> handleTelegramCommand(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> options = Arrays.asList("token", "chat");
            
            for (String option : options) {
                if (option.startsWith(input)) {
                    completions.add(option);
                }
            }
        }
        
        return completions;
    }
    
    /**
     * Автодополнение для команды /report-webhook
     */
    private List<String> handleWebhookCommand(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> options = Arrays.asList("set", "remove");
            
            for (String option : options) {
                if (option.startsWith(input)) {
                    completions.add(option);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            // Пример URL для webhook
            completions.add("https://discord.com/api/webhooks/...");
        }
        
        return completions;
    }
    
    /**
     * Автодополнение для команды /report-discord
     */
    private List<String> handleDiscordCommand(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> options = Arrays.asList("token", "guild", "channel", "enable", "disable", "moderation", "status");
            
            for (String option : options) {
                if (option.startsWith(input)) {
                    completions.add(option);
                }
            }
        } else if (args.length == 2) {
            String command = args[0].toLowerCase();
            if (command.equals("moderation")) {
                String input = args[1].toLowerCase();
                List<String> values = Arrays.asList("true", "false", "on", "off");
                
                for (String value : values) {
                    if (value.startsWith(input)) {
                        completions.add(value);
                    }
                }
            } else if (command.equals("enable") || command.equals("disable") || command.equals("status")) {
                // Эти команды не требуют второго аргумента
                return completions;
            }
        }
        
        return completions;
    }
}
