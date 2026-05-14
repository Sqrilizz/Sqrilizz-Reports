package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!VersionUtils.hasPermission(player, "reports.admin")) {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("no-permission"));
                return true;
            }
        }

        DebugManager.toggle();
        boolean enabled = DebugManager.isEnabled();
        String status = enabled ? "&aON" : "&cOFF";
        String msg = ColorManager.colorize("{info}Debug mode: " + status);

        if (sender instanceof Player) {
            VersionUtils.sendMessage((Player) sender, msg);
        } else {
            sender.sendMessage("Debug mode: " + (enabled ? "ON" : "OFF"));
        }

        return true;
    }
}
