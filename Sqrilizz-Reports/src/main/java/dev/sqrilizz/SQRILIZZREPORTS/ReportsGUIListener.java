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
        
        DebugManager.log("GUI", "Click: title='" + strippedTitle + "' item=" + clicked.getType() + " name='" + displayName + "' slot=" + event.getSlot());
        
        // Determine menu type by checking for keywords
        // Main reports list GUI (contains "Page" / "Страница" / "صفحة")
        if (strippedTitle.contains("Page") || strippedTitle.contains("Страница") || strippedTitle.contains("صфحة")) {
            DebugManager.log("GUI", "-> Reports list menu detected");
            handleReportsListClick(player, clicked, displayName, title, event.isLeftClick());
        }
        // Report actions GUI (contains "Report #" / "Репорт #" / "بلاغ #")
        else if ((strippedTitle.contains("Actions") || strippedTitle.contains("Действия") || strippedTitle.contains("الإجراءات")) 
                 && strippedTitle.contains("#")) {
            // Extract report ID from title
            String[] parts = strippedTitle.split("#");
            if (parts.length >= 2) {
                try {
                    long reportId = Long.parseLong(parts[1].trim().split(" ")[0]);
                    DebugManager.log("GUI", "-> Report actions menu, reportId=" + reportId);
                    handleReportActionsClick(player, displayName, reportId);
                } catch (NumberFormatException e) {
                    DebugManager.warn("Failed to parse report ID from title: " + strippedTitle);
                }
            }
        }
        // Punishment menu (contains "Punishment" / "Наказание" / "العقوبة" and "#")
        else if ((strippedTitle.contains("Punishment") || strippedTitle.contains("Наказание") || strippedTitle.contains("العقوبة"))
                 && strippedTitle.contains("#")) {
            // Extract targetName and reportId from title
            // Title format: "[Punishment] PlayerName #ID" or "[Наказание] PlayerName #ID"
            String[] parts = strippedTitle.split("#");
            if (parts.length >= 2) {
                try {
                    long reportId = Long.parseLong(parts[1].trim());
                    // Extract target name: everything between "] " and " #"
                    String beforeHash = parts[0].trim();
                    int bracketEnd = beforeHash.lastIndexOf("] ");
                    String targetName;
                    if (bracketEnd >= 0) {
                        targetName = beforeHash.substring(bracketEnd + 2).trim();
                    } else {
                        // Fallback: last word before #
                        String[] words = beforeHash.split("\\s+");
                        targetName = words[words.length - 1].trim();
                    }
                    if (!targetName.isEmpty()) {
                        DebugManager.log("GUI", "-> Punishment menu, target=" + targetName + " reportId=" + reportId);
                        handlePunishmentClick(player, displayName, targetName, reportId);
                    }
                } catch (NumberFormatException e) {
                    DebugManager.warn("Failed to parse punishment report ID from title: " + strippedTitle);
                }
            }
        }
        // Punishment confirmation GUI (contains "Applied" / "выдано" / "تم تطبيق")
        else if (strippedTitle.contains("Applied") || strippedTitle.contains("выдано") || strippedTitle.contains("تم تطبيق")) {
            DebugManager.log("GUI", "-> Punishment confirm menu");
            handlePunishmentConfirmClick(player, displayName);
        }
        // Player reports GUI (contains player name, check by elimination)
        else if (strippedTitle.contains("Reports") || strippedTitle.contains("Репорты") || strippedTitle.contains("البلاغات")) {
            // Extract target name from title - it's after "on" / "на" / "ضد"
            // Title format: "[Reports on PlayerName]" - need to strip trailing ] and brackets
            String targetName = "";
            if (strippedTitle.contains("on ")) {
                targetName = strippedTitle.split("on ")[1].trim();
            } else if (strippedTitle.contains("на ")) {
                targetName = strippedTitle.split("на ")[1].trim();
            } else if (strippedTitle.contains("ضد ")) {
                targetName = strippedTitle.split("ضد ")[1].trim();
            }
            // Strip trailing ] from title format like [Reports on Player]
            if (targetName.endsWith("]")) {
                targetName = targetName.substring(0, targetName.length() - 1).trim();
            }
            if (!targetName.isEmpty()) {
                DebugManager.log("GUI", "-> Player reports menu, target=" + targetName);
                handlePlayerReportsClick(player, clicked, displayName, targetName, title);
            } else {
                DebugManager.warn("Could not extract target name from title: " + strippedTitle);
            }
        }
    }
    
    /**
     * Helper method to check if button name matches any language variant
     */
    private boolean isButton(String displayName, String... keywords) {
        String stripped = displayName.replaceAll("§[0-9a-fk-or]", "").toLowerCase();
        for (String keyword : keywords) {
            if (stripped.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    private void handleReportsListClick(Player player, ItemStack clicked, String displayName, String title, boolean isLeftClick) {
        // Close button
        if (isButton(displayName, "Close", "Закрыть", "إغلاق")) {
            player.closeInventory();
            return;
        }
        
        // Navigation
        if (isButton(displayName, "Next", "Следующая", "التالية")) {
            // Extract current page from title
            int currentPage = extractPageFromTitle(title);
            ReportsGUI.openReportsListGUI(player, currentPage + 1);
            return;
        }
        
        if (isButton(displayName, "Previous", "Предыдущая", "السابقة")) {
            // Extract current page from title
            int currentPage = extractPageFromTitle(title);
            ReportsGUI.openReportsListGUI(player, currentPage - 1);
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
    
    private void handlePlayerReportsClick(Player player, ItemStack clicked, String displayName, String targetName, String title) {
        // Close button
        if (isButton(displayName, "Close", "Закрыть", "إغلاق")) {
            player.closeInventory();
            return;
        }
        
        // Navigation buttons
        if (isButton(displayName, "Next", "Следующая", "التالية")) {
            // Use event title which is already a String
            int currentPage = extractPageFromTitle(title);
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                ReportsGUI.openPlayerReportsGUI(player, targetName, currentPage + 1);
            }, 2L);
            return;
        }
        
        if (isButton(displayName, "Previous", "Предыдущая", "السابقة")) {
            // Use event title which is already a String
            int currentPage = extractPageFromTitle(title);
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                ReportsGUI.openPlayerReportsGUI(player, targetName, currentPage - 1);
            }, 2L);
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
        DebugManager.log("GUI", "handleReportActionsClick: reportId=" + reportId + " button='" + displayName + "'");
        ReportManager.Report report = findReportById(reportId);
        
        if (report == null) {
            DebugManager.warn("Report not found: " + reportId);
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
        if (isButton(displayName, "Back", "Назад", "رجوع")) {
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                ReportsGUI.openPlayerReportsGUI(player, targetName);
            }, 2L);
            return;
        }
        
        String cleanTargetName = NameUtils.cleanPlayerName(targetName);
        String reason = "Report violation";
        final String[] punishmentType = {""};
        
        // Warn
        if (isButton(displayName, "Warning", "Предупреждение", "تحذير")) {
            punishmentType[0] = "Warning";
            boolean success = dev.sqrilizz.SQRILIZZREPORTS.punishment.PunishmentManager.warn(
                cleanTargetName, reason, player.getName()
            );
            
            if (success) {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                    .replace("[PLAYER]", cleanTargetName)
                    .replace("[TYPE]", punishmentType[0]));
            }
        }
        // Kick
        else if (isButton(displayName, "Kick", "Кик", "طرد")) {
            punishmentType[0] = "Kick";
            boolean success = dev.sqrilizz.SQRILIZZREPORTS.punishment.PunishmentManager.kick(
                cleanTargetName, reason, player.getName()
            );
            
            if (success) {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                    .replace("[PLAYER]", cleanTargetName)
                    .replace("[TYPE]", punishmentType[0]));
            }
        }
        // Mute 1h
        else if (isButton(displayName, "1 hour", "1 час", "ساعة")) {
            punishmentType[0] = "Mute 1 hour";
            boolean success = dev.sqrilizz.SQRILIZZREPORTS.punishment.PunishmentManager.mute(
                cleanTargetName, reason, player.getName(), 1, java.util.concurrent.TimeUnit.HOURS
            );
            
            if (success) {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                    .replace("[PLAYER]", cleanTargetName)
                    .replace("[TYPE]", punishmentType[0]));
            }
        }
        // Mute 1d
        else if (isButton(displayName, "1 day", "1 день", "يوم واحد") && isButton(displayName, "Mute", "Мут", "كتم")) {
            punishmentType[0] = "Mute 1 day";
            boolean success = dev.sqrilizz.SQRILIZZREPORTS.punishment.PunishmentManager.mute(
                cleanTargetName, reason, player.getName(), 1, java.util.concurrent.TimeUnit.DAYS
            );
            
            if (success) {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                    .replace("[PLAYER]", cleanTargetName)
                    .replace("[TYPE]", punishmentType[0]));
            }
        }
        // Ban 1d
        else if (isButton(displayName, "1 day", "1 день", "يوم واحد") && isButton(displayName, "Ban", "Бан", "حظر")) {
            punishmentType[0] = "Ban 1 day";
            boolean success = dev.sqrilizz.SQRILIZZREPORTS.punishment.PunishmentManager.ban(
                cleanTargetName, reason, player.getName(), 1, java.util.concurrent.TimeUnit.DAYS
            );
            
            if (success) {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                    .replace("[PLAYER]", cleanTargetName)
                    .replace("[TYPE]", punishmentType[0]));
            }
        }
        // Ban 7d
        else if (isButton(displayName, "7 days", "7 дней", "7 أيام")) {
            punishmentType[0] = "Ban 7 days";
            boolean success = dev.sqrilizz.SQRILIZZREPORTS.punishment.PunishmentManager.ban(
                cleanTargetName, reason, player.getName(), 7, java.util.concurrent.TimeUnit.DAYS
            );
            
            if (success) {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                    .replace("[PLAYER]", cleanTargetName)
                    .replace("[TYPE]", punishmentType[0]));
            }
        }
        // Permanent ban
        else if (isButton(displayName, "Permanent", "Перманентный", "دائم")) {
            punishmentType[0] = "Permanent ban";
            boolean success = dev.sqrilizz.SQRILIZZREPORTS.punishment.PunishmentManager.banPermanent(
                cleanTargetName, reason, player.getName()
            );
            
            if (success) {
                VersionUtils.sendMessage(player, LanguageManager.getMessage("admin-punished")
                    .replace("[PLAYER]", cleanTargetName)
                    .replace("[TYPE]", punishmentType[0]));
            }
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
     * Helper method to find report by ID - O(1) lookup
     */
    private ReportManager.Report findReportById(long id) {
        return ReportManager.getReportById(id);
    }
    
    /**
     * Helper method to extract page number from GUI title
     */
    private int extractPageFromTitle(String title) {
        try {
            // Remove color codes
            String stripped = title.replaceAll("§[0-9a-fk-or]", "");
            
            // Try to find pattern like "Page 1" / "Страница 1" / "صفحة 1"
            String[] parts = stripped.split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].matches("\\d+")) {
                    return Integer.parseInt(parts[i]) - 1; // Convert to 0-based index
                }
            }
        } catch (Exception e) {
            // If parsing fails, return 0
        }
        return 0;
    }
}
