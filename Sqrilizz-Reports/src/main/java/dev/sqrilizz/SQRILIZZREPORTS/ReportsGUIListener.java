package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsGUIListener implements Listener {
    
    // Temporary storage for punishment context
    private static final Map<String, PunishmentContext> punishmentContexts = new HashMap<>();
    
    private static class PunishmentContext {
        String targetName;
        long reportId;
        String punishmentType;
        
        PunishmentContext(String targetName, long reportId, String punishmentType) {
            this.targetName = targetName;
            this.reportId = reportId;
            this.punishmentType = punishmentType;
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        
        // Strip color codes from title for checking
        String strippedTitle = title.replaceAll("§[0-9a-fk-or]", "");
        
        
        // Check if it's our GUI by checking for key words in any language
        boolean isOurGUI = false;
        
        // Check for "Reports" / "Репорты" / "البلاغات"
        if (strippedTitle.contains("Reports") || strippedTitle.contains("Репорты") || strippedTitle.contains("البلаغات")) {
            isOurGUI = true;
        }
        // Check for "Actions" / "Действия" / "الإجراءات"
        if (strippedTitle.contains("Actions") || strippedTitle.contains("Действия") || strippedTitle.contains("الإجراءات")) {
            isOurGUI = true;
        }
        // Check for "Punishment" / "Наказание" / "العقوبة"
        if (strippedTitle.contains("Punishment") || strippedTitle.contains("Наказание") || strippedTitle.contains("العقوبة")) {
            isOurGUI = true;
        }
        // Check for "Applied" / "выдано" / "تم تطبيق"
        if (strippedTitle.contains("Applied") || strippedTitle.contains("выдано") || strippedTitle.contains("تم تطبيق")) {
            isOurGUI = true;
        }
        
        
        if (!isOurGUI) {
            return;
        }
        
        // Cancel ALL clicks in our GUI to prevent item dragging
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        
        ItemMeta meta = clicked.getItemMeta();
        String displayName = meta.getDisplayName();
        
        // Determine menu type by checking for keywords
        // Main reports list GUI (contains "Page" / "Страница" / "صفحة")
        if (strippedTitle.contains("Page") || strippedTitle.contains("Страница") || strippedTitle.contains("صفحة")) {
            handleReportsListClick(player, clicked, displayName, event.isLeftClick());
        }
        // Report actions GUI (contains "Report #" / "Репорт #" / "بلاغ #")
        else if ((strippedTitle.contains("Actions") || strippedTitle.contains("Действия") || strippedTitle.contains("الإجراءات")) 
                 && strippedTitle.contains("#")) {
            // Extract report ID from title
            String[] parts = strippedTitle.split("#");
            if (parts.length >= 2) {
                try {
                    long reportId = Long.parseLong(parts[1].trim().split(" ")[0]);
                    handleReportActionsClick(player, displayName, reportId);
                } catch (NumberFormatException e) {
                    // Invalid report ID
                }
            }
        }
        // Punishment menu (contains "Punishment" / "Наказание" / "العقوبة" and "#")
        else if ((strippedTitle.contains("Punishment") || strippedTitle.contains("Наказание") || strippedTitle.contains("العقوبة"))
                 && strippedTitle.contains("#")) {
            // Extract targetName and reportId from title
            String[] parts = strippedTitle.split("#");
            if (parts.length >= 2) {
                try {
                    long reportId = Long.parseLong(parts[1].trim());
                    // Extract target name (everything before the #)
                    String beforeHash = parts[0].trim();
                    String[] nameParts = beforeHash.split("-");
                    if (nameParts.length >= 2) {
                        String targetName = nameParts[nameParts.length - 1].trim();
                        handlePunishmentClick(player, displayName, targetName, reportId);
                    }
                } catch (NumberFormatException e) {
                    // Invalid report ID
                }
            }
        }
        // Punishment confirmation GUI (contains "Applied" / "выдано" / "تم تطبيق")
        else if (strippedTitle.contains("Applied") || strippedTitle.contains("выдано") || strippedTitle.contains("تم تطبيق")) {
            handlePunishmentConfirmClick(player, displayName);
        }
        // Player reports GUI (contains player name, check by elimination)
        else if (strippedTitle.contains("Reports") || strippedTitle.contains("Репорты") || strippedTitle.contains("البلاغات")) {
            // Extract target name from title - it's after "on" / "на" / "ضد"
            String targetName = "";
            if (strippedTitle.contains("on ")) {
                targetName = strippedTitle.split("on ")[1].trim();
            } else if (strippedTitle.contains("на ")) {
                targetName = strippedTitle.split("на ")[1].trim();
            } else if (strippedTitle.contains("ضد ")) {
                targetName = strippedTitle.split("ضد ")[1].trim();
            }
            if (!targetName.isEmpty()) {
                handlePlayerReportsClick(player, clicked, displayName, targetName);
            }
        }
    }
    
    /**
     * Helper method to check if button name matches any language variant
     */
    private boolean isButton(String displayName, String... keywords) {
        String stripped = displayName.replaceAll("§[0-9a-fk-or]", "");
        for (String keyword : keywords) {
            if (stripped.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private void handleReportsListClick(Player player, ItemStack clicked, String displayName, boolean isLeftClick) {
        // Close button
        if (isButton(displayName, "Close", "Закрыть", "إغلاق")) {
            player.closeInventory();
            return;
        }
        
        // Navigation
        if (isButton(displayName, "Next", "Следующая", "التالية")) {
            // TODO: Implement pagination
            return;
        }
        
        if (isButton(displayName, "Previous", "Предыдущая", "السابقة")) {
            // TODO: Implement pagination
            return;
        }
        
        // Player head clicked
        if (clicked.getType().toString().contains("PLAYER_HEAD") || clicked.getType().toString().contains("SKULL")) {
            SkullMeta skullMeta = (SkullMeta) clicked.getItemMeta();
            
            // Extract player name from display name (now it's just the colored name)
            String targetName = displayName.replaceAll("§[0-9a-fk-or]", "").trim();
            
            if (isLeftClick) {
                // Open player reports with smooth transition
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    ReportsGUI.openPlayerReportsGUI(player, targetName);
                }, 2L);
            } else {
                // Clear all reports
                ReportManager.clearReports(targetName);
                VersionUtils.sendMessage(player, LanguageManager.getMessage("reports-cleared")
                    .replace("[PLAYER]", targetName));
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    ReportsGUI.openReportsListGUI(player);
                }, 2L);
            }
        }
    }
    
    private void handlePlayerReportsClick(Player player, ItemStack clicked, String displayName, String targetName) {
        // Close button
        if (isButton(displayName, "Close", "Закрыть", "إغلاق")) {
            player.closeInventory();
            return;
        }
        
        // Back button
        if (isButton(displayName, "Back", "Назад", "رجوع")) {
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                ReportsGUI.openReportsListGUI(player);
            }, 2L);
            return;
        }
        
        // Teleport to player
        if (isButton(displayName, "Teleport", "Телепортироваться", "الانتقال")) {
            Player target = Bukkit.getPlayer(targetName);
            if (target != null && target.isOnline()) {
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    player.teleport(target.getLocation());
                    VersionUtils.sendMessage(player, LanguageManager.getMessage("teleport-success").replace("[PLAYER]", targetName));
                }, 2L);
            } else {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("teleport-offline"));
            }
            return;
        }
        
        // Clear all reports
        if (isButton(displayName, "Clear", "Очистить", "مسح")) {
            ReportManager.clearReports(targetName);
            VersionUtils.sendMessage(player, LanguageManager.getMessage("reports-cleared")
                .replace("[PLAYER]", targetName));
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                ReportsGUI.openReportsListGUI(player);
            }, 2L);
            return;
        }
        
        // Report clicked
        if (isButton(displayName, "Report #", "Репорт #", "بلاغ #")) {
            String reportIdStr = displayName.replaceAll("[^0-9]", "");
            try {
                long reportId = Long.parseLong(reportIdStr);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    ReportsGUI.openReportActionsGUI(player, reportId, targetName);
                }, 2L);
            } catch (NumberFormatException e) {
                // Invalid report ID
            }
        }
    }

    private void handleReportActionsClick(Player player, String displayName, long reportId) {
        ReportManager.Report report = findReportById(reportId);
        
        if (report == null) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("report-not-found"));
            player.closeInventory();
            return;
        }
        
        // Back button
        if (isButton(displayName, "Back", "Назад", "رجوع")) {
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                ReportsGUI.openPlayerReportsGUI(player, report.target);
            }, 2L);
            return;
        }
        
        // Teleport to reporter
        if (isButton(displayName, "reporter", "жалобщику", "المبلغ")) {
            Player reporter = Bukkit.getPlayer(report.reporter);
            if (reporter != null && reporter.isOnline()) {
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    player.teleport(reporter.getLocation());
                    VersionUtils.sendMessage(player, LanguageManager.getMessage("teleport-success").replace("[PLAYER]", report.reporter));
                }, 2L);
            } else {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("teleport-offline"));
            }
            return;
        }
        
        // Teleport to target
        if (isButton(displayName, "target", "нарушителю", "المخالف")) {
            Player target = Bukkit.getPlayer(report.target);
            if (target != null && target.isOnline()) {
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    player.teleport(target.getLocation());
                    VersionUtils.sendMessage(player, LanguageManager.getMessage("teleport-success").replace("[PLAYER]", report.target));
                }, 2L);
            } else {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("teleport-offline"));
            }
            return;
        }
        
        // Punish player
        if (isButton(displayName, "Punish", "Наказать", "معاقبة")) {
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                ReportsGUI.openPunishmentGUI(player, report.target, reportId);
            }, 2L);
            return;
        }
        
        // Resolve report
        if (isButton(displayName, "Resolve", "Решить", "حل")) {
            ReportManager.resolveReport(reportId, player.getName());
            VersionUtils.sendMessage(player, LanguageManager.getMessage("report-resolved").replace("[ID]", String.valueOf(reportId)));
            player.closeInventory();
            
            // Check if there are more reports for this player
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                List<ReportManager.Report> remainingReports = ReportManager.getPlayerReports(report.target);
                if (remainingReports.isEmpty()) {
                    ReportsGUI.openReportsListGUI(player);
                } else {
                    ReportsGUI.openPlayerReportsGUI(player, report.target);
                }
            }, 2L);
            return;
        }
        
        // Delete report
        if (isButton(displayName, "Delete", "Удалить", "حذف")) {
            ReportManager.deleteReport(reportId, player.getName());
            VersionUtils.sendMessage(player, LanguageManager.getMessage("report-deleted").replace("[ID]", String.valueOf(reportId)));
            player.closeInventory();
            
            // Check if there are more reports for this player
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                List<ReportManager.Report> remainingReports = ReportManager.getPlayerReports(report.target);
                if (remainingReports.isEmpty()) {
                    ReportsGUI.openReportsListGUI(player);
                } else {
                    ReportsGUI.openPlayerReportsGUI(player, report.target);
                }
            }, 2L);
        }
    }
    
    private void handlePunishmentClick(Player player, String displayName, String targetName, long reportId) {
        // Back button
        if (displayName.contains("Назад")) {
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                ReportsGUI.openPlayerReportsGUI(player, targetName);
            }, 2L);
            return;
        }
        
        String cleanTargetName = NameUtils.cleanPlayerName(targetName);
        String reason = LanguageManager.getMessage("punishment-reason-default").replace("[REASON]", "Нарушение правил (репорт)");
        Player target = Bukkit.getPlayer(cleanTargetName);
        final String[] punishmentType = {""};  // Use array to make it effectively final
        
        // Warn
        if (isButton(displayName, "Warning", "Предупреждение", "تحذير")) {
            punishmentType[0] = "Предупреждение";
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warn " + cleanTargetName + " " + reason);
            
            // Notify target player
            if (target != null && target.isOnline()) {
                String warnMsg = LanguageManager.getMessage("punishment-warn")
                    .replace("[REASON]", reason)
                    .replace("[ADMIN]", player.getName());
                VersionUtils.sendMessage(target, warnMsg);
            }
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                .replace("[PLAYER]", cleanTargetName)
                .replace("[TYPE]", punishmentType[0]));
        }
        // Kick
        else if (isButton(displayName, "Kick", "Кик", "طرد")) {
            punishmentType[0] = "Кик";
            // Notify before kick
            if (target != null && target.isOnline()) {
                String kickMsg = LanguageManager.getMessage("punishment-kick")
                    .replace("[REASON]", reason)
                    .replace("[ADMIN]", player.getName());
                VersionUtils.sendMessage(target, kickMsg);
            }
            
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + cleanTargetName + " " + reason);
            }, 20L);
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                .replace("[PLAYER]", cleanTargetName)
                .replace("[TYPE]", punishmentType[0]));
        }
        // Mute 1h
        else if (isButton(displayName, "1 hour", "1 час", "ساعة")) {
            punishmentType[0] = "Мут 1 час";
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempmute " + cleanTargetName + " 1h " + reason);
            
            // Notify target player
            if (target != null && target.isOnline()) {
                String muteMsg = LanguageManager.getMessage("punishment-mute")
                    .replace("[REASON]", reason)
                    .replace("[DURATION]", "1 час")
                    .replace("[ADMIN]", player.getName());
                VersionUtils.sendMessage(target, muteMsg);
            }
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                .replace("[PLAYER]", cleanTargetName)
                .replace("[TYPE]", punishmentType[0]));
        }
        // Mute 1d
        else if (isButton(displayName, "1 day", "1 день", "يوم واحد") && isButton(displayName, "Mute", "Мут", "كتم")) {
            punishmentType[0] = "Мут 1 день";
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempmute " + cleanTargetName + " 1d " + reason);
            
            // Notify target player
            if (target != null && target.isOnline()) {
                String muteMsg = LanguageManager.getMessage("punishment-mute")
                    .replace("[REASON]", reason)
                    .replace("[DURATION]", "1 день")
                    .replace("[ADMIN]", player.getName());
                VersionUtils.sendMessage(target, muteMsg);
            }
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                .replace("[PLAYER]", cleanTargetName)
                .replace("[TYPE]", punishmentType[0]));
        }
        // Ban 1d
        else if (isButton(displayName, "1 day", "1 день", "يوم واحد") && isButton(displayName, "Ban", "Бан", "حظر")) {
            punishmentType[0] = "Бан 1 день";
            // Notify before ban
            if (target != null && target.isOnline()) {
                String banMsg = LanguageManager.getMessage("punishment-ban")
                    .replace("[REASON]", reason)
                    .replace("[DURATION]", "1 день")
                    .replace("[ADMIN]", player.getName());
                VersionUtils.sendMessage(target, banMsg);
            }
            
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + cleanTargetName + " 1d " + reason);
            }, 20L);
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                .replace("[PLAYER]", cleanTargetName)
                .replace("[TYPE]", punishmentType[0]));
        }
        // Ban 7d
        else if (isButton(displayName, "7 days", "7 дней", "7 أيام")) {
            punishmentType[0] = "Бан 7 дней";
            // Notify before ban
            if (target != null && target.isOnline()) {
                String banMsg = LanguageManager.getMessage("punishment-ban")
                    .replace("[REASON]", reason)
                    .replace("[DURATION]", "7 дней")
                    .replace("[ADMIN]", player.getName());
                VersionUtils.sendMessage(target, banMsg);
            }
            
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + cleanTargetName + " 7d " + reason);
            }, 20L);
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                .replace("[PLAYER]", cleanTargetName)
                .replace("[TYPE]", punishmentType[0]));
        }
        // Permanent ban
        else if (isButton(displayName, "Permanent", "Перманентный", "دائم")) {
            punishmentType[0] = "Перманентный бан";
            // Notify before ban
            if (target != null && target.isOnline()) {
                String banMsg = LanguageManager.getMessage("punishment-ban")
                    .replace("[REASON]", reason)
                    .replace("[DURATION]", "Навсегда")
                    .replace("[ADMIN]", player.getName());
                VersionUtils.sendMessage(target, banMsg);
            }
            
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + cleanTargetName + " " + reason);
            }, 20L);
            
            VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                .replace("[PLAYER]", cleanTargetName)
                .replace("[TYPE]", punishmentType[0]));
        }
        
        // If punishment was applied, open confirmation menu
        if (!punishmentType[0].isEmpty()) {
            // Store context
            punishmentContexts.put(player.getName(), new PunishmentContext(cleanTargetName, reportId, punishmentType[0]));
            
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                ReportsGUI.openPunishmentConfirmGUI(player, cleanTargetName, reportId, punishmentType[0]);
            }, 2L);
        }
    }
    
    private void handlePunishmentConfirmClick(Player player, String displayName) {
        PunishmentContext context = punishmentContexts.get(player.getName());
        
        if (context == null) {
            player.closeInventory();
            return;
        }
        
        // Close report
        if (isButton(displayName, "Close report", "Закрыть репорт", "إغلاق البلاغ")) {
            ReportManager.resolveReport(context.reportId, player.getName());
            VersionUtils.sendMessage(player, LanguageManager.getMessage("report-closed").replace("[ID]", String.valueOf(context.reportId)));
            
            punishmentContexts.remove(player.getName());
            player.closeInventory();
            
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                List<ReportManager.Report> remainingReports = ReportManager.getPlayerReports(context.targetName);
                if (remainingReports.isEmpty()) {
                    ReportsGUI.openReportsListGUI(player);
                } else {
                    ReportsGUI.openPlayerReportsGUI(player, context.targetName);
                }
            }, 2L);
            return;
        }
        
        // Keep report open
        if (isButton(displayName, "Keep", "Оставить", "الاحتفاظ")) {
            punishmentContexts.remove(player.getName());
            player.closeInventory();
            
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                ReportsGUI.openPlayerReportsGUI(player, context.targetName);
            }, 2L);
            return;
        }
        
        // Close menu
        if (isButton(displayName, "Close menu", "Закрыть меню", "إغلاق القائمة")) {
            punishmentContexts.remove(player.getName());
            player.closeInventory();
        }
    }
    
    /**
     * Helper method to find report by ID
     */
    private ReportManager.Report findReportById(long id) {
        Map<String, List<ReportManager.Report>> allReports = ReportManager.getReports();
        
        for (List<ReportManager.Report> reports : allReports.values()) {
            for (ReportManager.Report report : reports) {
                if (report.id == id) {
                    return report;
                }
            }
        }
        
        return null;
    }
}
