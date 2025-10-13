package dev.sqrilizz.reports;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Simple /report command
 */
public class ReportCommand implements CommandExecutor {
    
    private final Main plugin;
    private final ReportManager reportManager;
    
    public ReportCommand(Main plugin) {
        this.plugin = plugin;
        this.reportManager = new ReportManager(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Only players can create reports
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create reports.");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check arguments
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /report <player> <reason>");
            return true;
        }
        
        String targetName = args[0];
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        
        // Validate reason length
        if (reason.length() > 200) {
            player.sendMessage(ChatColor.RED + "Reason is too long! Maximum 200 characters.");
            return true;
        }
        
        // Can't report yourself
        if (targetName.equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "You cannot report yourself!");
            return true;
        }
        
        // Create report asynchronously
        plugin.runAsync(() -> {
            boolean success = reportManager.createReport(player, targetName, reason);
            
            // Send response on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Report submitted successfully!");
                    player.sendMessage(ChatColor.GRAY + "Target: " + ChatColor.WHITE + targetName);
                    player.sendMessage(ChatColor.GRAY + "Reason: " + ChatColor.WHITE + reason);
                    
                    // Notify online admins
                    notifyAdmins(targetName, reason, player.getName());
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to submit report. Please try again.");
                }
            });
        });
        
        return true;
    }
    
    /**
     * Notify online admins about new report
     */
    private void notifyAdmins(String target, String reason, String reporter) {
        String message = ChatColor.YELLOW + "[REPORT] " + ChatColor.WHITE + reporter + 
                        ChatColor.GRAY + " reported " + ChatColor.WHITE + target + 
                        ChatColor.GRAY + " for: " + ChatColor.WHITE + reason;
        
        plugin.getServer().getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("reports.admin"))
            .forEach(admin -> admin.sendMessage(message));
    }
}
