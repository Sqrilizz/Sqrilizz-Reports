package dev.sqrilizz.SQRILIZZREPORTS.commands;

import dev.sqrilizz.SQRILIZZREPORTS.LanguageManager;
import dev.sqrilizz.SQRILIZZREPORTS.VersionUtils;
import dev.sqrilizz.SQRILIZZREPORTS.api.CacheManager;
import dev.sqrilizz.SQRILIZZREPORTS.monitoring.PerformanceMonitor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Команда для мониторинга здоровья и производительности системы
 * Обеспечивает полную диагностику для достижения 10/10 качества
 */
public class HealthCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!VersionUtils.hasPermission(sender, "reports.admin")) {
            sender.sendMessage(LanguageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            showHealthOverview(sender);
        } else {
            switch (args[0].toLowerCase()) {
                case "detailed":
                case "full":
                    showDetailedStats(sender);
                    break;
                case "cache":
                    showCacheStats(sender);
                    break;
                case "reset":
                    if (args.length > 1 && "confirm".equals(args[1])) {
                        resetStats(sender);
                    } else {
                        sender.sendMessage("§c⚠️ Use '/report-health reset confirm' to reset all statistics");
                    }
                    break;
                default:
                    showUsage(sender);
                    break;
            }
        }

        return true;
    }

    private void showHealthOverview(CommandSender sender) {
        String report = PerformanceMonitor.getHealthReport();
        sender.sendMessage("§6" + report);
    }

    private void showDetailedStats(CommandSender sender) {
        Map<String, Object> stats = PerformanceMonitor.getPerformanceStats();
        
        sender.sendMessage("§6=== DETAILED PERFORMANCE STATISTICS ===");
        sender.sendMessage("");
        
        // Операционные метрики
        sender.sendMessage("§e📊 Operations:");
        sender.sendMessage(String.format("§7  Reports Created: §f%s", stats.get("reports_created")));
        sender.sendMessage(String.format("§7  Reports Resolved: §f%s", stats.get("reports_resolved")));
        sender.sendMessage(String.format("§7  Replies Added: §f%s", stats.get("replies_added")));
        sender.sendMessage(String.format("§7  Reports Deleted: §f%s", stats.get("reports_deleted")));
        sender.sendMessage("");
        
        // Производительность
        sender.sendMessage("§e⚡ Performance:");
        sender.sendMessage(String.format("§7  DB Operations: §f%s", stats.get("db_operations")));
        sender.sendMessage(String.format("§7  Cache Hits: §f%s", stats.get("cache_hits")));
        sender.sendMessage(String.format("§7  Cache Misses: §f%s", stats.get("cache_misses")));
        sender.sendMessage(String.format("§7  Cache Hit Rate: §a%.2f%%", stats.get("cache_hit_rate")));
        sender.sendMessage(String.format("§7  Active Connections: §f%s", stats.get("active_connections")));
        sender.sendMessage("");
        
        // Ошибки
        sender.sendMessage("§e❌ Errors:");
        sender.sendMessage(String.format("§7  DB Errors: §c%s", stats.get("db_errors")));
        sender.sendMessage(String.format("§7  Notification Errors: §c%s", stats.get("notification_errors")));
        sender.sendMessage(String.format("§7  Cache Errors: §c%s", stats.get("cache_errors")));
        sender.sendMessage(String.format("§7  Total Errors: §c%s", stats.get("total_errors")));
        sender.sendMessage("");
        
        // Системные метрики
        sender.sendMessage("§e🖥️ System:");
        sender.sendMessage(String.format("§7  Uptime: §f%s ms", stats.get("uptime_ms")));
        sender.sendMessage(String.format("§7  Memory: §f%s/%s MB (%.1f%%)", 
                stats.get("memory_used_mb"), stats.get("memory_max_mb"), stats.get("memory_usage_percent")));
        sender.sendMessage(String.format("§7  Threads: §f%s (%s daemon)", 
                stats.get("thread_count"), stats.get("daemon_thread_count")));
    }

    private void showCacheStats(CommandSender sender) {
        try {
            String cacheStats = CacheManager.getCacheStats();
            sender.sendMessage("§6=== CACHE STATISTICS ===");
            sender.sendMessage("§f" + cacheStats);
        } catch (Exception e) {
            sender.sendMessage("§c❌ Failed to retrieve cache statistics: " + e.getMessage());
        }
    }

    private void resetStats(CommandSender sender) {
        try {
            PerformanceMonitor.resetCounters();
            sender.sendMessage("§a✅ All performance statistics have been reset");
        } catch (Exception e) {
            sender.sendMessage("§c❌ Failed to reset statistics: " + e.getMessage());
        }
    }

    private void showUsage(CommandSender sender) {
        sender.sendMessage("§6=== HEALTH COMMAND USAGE ===");
        sender.sendMessage("§e/report-health §7- Show health overview");
        sender.sendMessage("§e/report-health detailed §7- Show detailed statistics");
        sender.sendMessage("§e/report-health cache §7- Show cache statistics");
        sender.sendMessage("§e/report-health reset confirm §7- Reset all statistics");
    }
}
