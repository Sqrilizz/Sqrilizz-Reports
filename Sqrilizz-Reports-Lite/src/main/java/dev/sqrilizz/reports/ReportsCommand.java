package dev.sqrilizz.reports;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Simple /reports command for viewing and managing reports
 */
public class ReportsCommand implements CommandExecutor {
    
    private final Main plugin;
    private final ReportManager reportManager;
    
    public ReportsCommand(Main plugin) {
        this.plugin = plugin;
        this.reportManager = new ReportManager(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("reports.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "view", "list" -> handleView(sender, args);
            case "close", "resolve" -> handleResolve(sender, args);
            case "delete", "remove" -> handleDelete(sender, args);
            case "reload" -> handleReload(sender);
            default -> showHelp(sender);
        }
        
        return true;
    }
    
    /**
     * Handle viewing reports
     */
    private void handleView(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /reports view <player>");
            return;
        }
        
        String playerName = args[1];
        
        // Get reports asynchronously
        plugin.runAsync(() -> {
            List<ReportManager.Report> reports = reportManager.getReports(playerName);
            
            // Send response on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (reports.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "No reports found for " + playerName);
                    return;
                }
                
                sender.sendMessage(ChatColor.GREEN + "=== Reports for " + playerName + " ===");
                
                for (ReportManager.Report report : reports) {
                    String time = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                        .withZone(ZoneId.systemDefault())
                        .format(Instant.ofEpochMilli(report.getTimestamp()));
                    
                    String status = report.isResolved() ? 
                        ChatColor.GREEN + "[RESOLVED]" : 
                        ChatColor.RED + "[OPEN]";
                    
                    sender.sendMessage(String.format("%s #%d %s%s %s%s",
                        status,
                        report.getId(),
                        ChatColor.GRAY,
                        time,
                        ChatColor.WHITE,
                        report.getReason()
                    ));
                    
                    sender.sendMessage(ChatColor.GRAY + "  Reporter: " + ChatColor.WHITE + report.getReporterName());
                    
                    if (report.isResolved()) {
                        sender.sendMessage(ChatColor.GRAY + "  Resolved by: " + ChatColor.WHITE + report.getResolver());
                    }
                }
                
                sender.sendMessage(ChatColor.GREEN + "=== End of reports ===");
            });
        });
    }
    
    /**
     * Handle resolving reports
     */
    private void handleResolve(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /reports close <id>");
            return;
        }
        
        long reportId;
        try {
            reportId = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid report ID!");
            return;
        }
        
        // Resolve report asynchronously
        plugin.runAsync(() -> {
            boolean success = reportManager.resolveReport(reportId, sender.getName());
            
            // Send response on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (success) {
                    sender.sendMessage(ChatColor.GREEN + "Report #" + reportId + " has been resolved.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to resolve report #" + reportId + ". Report not found.");
                }
            });
        });
    }
    
    /**
     * Handle deleting reports
     */
    private void handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /reports delete <id>");
            return;
        }
        
        long reportId;
        try {
            reportId = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid report ID!");
            return;
        }
        
        // Delete report asynchronously
        plugin.runAsync(() -> {
            boolean success = reportManager.deleteReport(reportId);
            
            // Send response on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (success) {
                    sender.sendMessage(ChatColor.GREEN + "Report #" + reportId + " has been deleted.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to delete report #" + reportId + ". Report not found.");
                }
            });
        });
    }
    
    /**
     * Handle config reload
     */
    private void handleReload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully!");
    }
    
    /**
     * Show command help
     */
    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "=== Sqrilizz-Reports Lite v1.0 ===");
        sender.sendMessage(ChatColor.YELLOW + "/reports view <player>" + ChatColor.GRAY + " - View reports for a player");
        sender.sendMessage(ChatColor.YELLOW + "/reports close <id>" + ChatColor.GRAY + " - Close a report");
        sender.sendMessage(ChatColor.YELLOW + "/reports delete <id>" + ChatColor.GRAY + " - Delete a report");
        sender.sendMessage(ChatColor.YELLOW + "/reports reload" + ChatColor.GRAY + " - Reload configuration");
    }
}
