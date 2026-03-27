package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportsGUI {
    
    private static final int REPORTS_PER_PAGE = 45;
    
    /**
     * Opens the main reports list GUI
     */
    public static void openReportsListGUI(Player player) {
        openReportsListGUI(player, 0);
    }
    
    /**
     * Opens the main reports list GUI with pagination
     */
    public static void openReportsListGUI(Player player, int page) {
        Map<String, List<ReportManager.Report>> reports = ReportManager.getReports();
        
        if (reports.isEmpty()) {
            VersionUtils.sendMessage(player, LanguageManager.getMessage("no-reports"));
            return;
        }
        
        List<String> reportedPlayers = new ArrayList<>(reports.keySet());
        int totalPages = (int) Math.ceil((double) reportedPlayers.size() / REPORTS_PER_PAGE);
        
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;
        
        Inventory gui = Bukkit.createInventory(null, 54, LanguageManager.getMessage("gui-reports-title").replace("[PAGE]", String.valueOf(page + 1)));
        
        int startIndex = page * REPORTS_PER_PAGE;
        int endIndex = Math.min(startIndex + REPORTS_PER_PAGE, reportedPlayers.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            String targetName = reportedPlayers.get(i);
            List<ReportManager.Report> playerReports = reports.get(targetName);
            int reportCount = playerReports.size();
            
            ItemStack item = createPlayerHead(targetName);
            ItemMeta meta = item.getItemMeta();
            // Set display name to just the player name with color
            meta.setDisplayName(ColorManager.colorize("&e" + NameUtils.cleanPlayerName(targetName)));
            
            List<String> lore = new ArrayList<>();
            lore.add(LanguageManager.getMessage("gui-reports-count").replace("[COUNT]", String.valueOf(reportCount)));
            lore.add("");
            lore.add(LanguageManager.getMessage("gui-click-left").replace("[ACTION]", LanguageManager.getMessage("gui-open-reports")));
            lore.add(LanguageManager.getMessage("gui-click-right").replace("[ACTION]", LanguageManager.getMessage("gui-clear-all")));
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            
            gui.setItem(i - startIndex, item);
        }
        
        // Navigation buttons
        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(LanguageManager.getMessage("gui-prev-page"));
            prevPage.setItemMeta(prevMeta);
            gui.setItem(48, prevPage);
        }
        
        if (page < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(LanguageManager.getMessage("gui-next-page"));
            nextPage.setItemMeta(nextMeta);
            gui.setItem(50, nextPage);
        }
        
        // Close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(LanguageManager.getMessage("gui-close"));
        close.setItemMeta(closeMeta);
        gui.setItem(49, close);
        
        player.openInventory(gui);
    }

    /**
     * Opens detailed view of reports for a specific player
     */
    public static void openPlayerReportsGUI(Player admin, String targetName) {
        openPlayerReportsGUI(admin, targetName, 0);
    }
    
    /**
     * Opens detailed view of reports for a specific player with pagination
     */
    public static void openPlayerReportsGUI(Player admin, String targetName, int page) {
        String cleanTargetName = NameUtils.cleanPlayerName(targetName);
        List<ReportManager.Report> playerReports = ReportManager.getPlayerReports(cleanTargetName);
        
        if (playerReports.isEmpty()) {
            VersionUtils.sendMessage(admin, LanguageManager.getMessage("no-reports-for-player")
                .replace("[PLAYER]", cleanTargetName));
            return;
        }
        
        int totalPages = (int) Math.ceil((double) playerReports.size() / REPORTS_PER_PAGE);
        
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;
        
        Inventory gui = Bukkit.createInventory(null, 54, LanguageManager.getMessage("gui-reports-on-player").replace("[PLAYER]", cleanTargetName));
        
        int startIndex = page * REPORTS_PER_PAGE;
        int endIndex = Math.min(startIndex + REPORTS_PER_PAGE, playerReports.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            ReportManager.Report report = playerReports.get(i);
            
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(LanguageManager.getMessage("gui-report-id").replace("[ID]", String.valueOf(report.id)));
            
            List<String> lore = new ArrayList<>();
            lore.add(LanguageManager.getMessage("gui-report-from").replace("[REPORTER]", NameUtils.cleanPlayerName(report.reporter)));
            lore.add(LanguageManager.getMessage("gui-report-reason").replace("[REASON]", report.reason));
            lore.add(LanguageManager.getMessage("gui-report-time").replace("[TIME]", report.getFormattedTime()));
            lore.add(LanguageManager.getMessage("gui-report-ago").replace("[AGO]", report.getTimeAgo()));
            lore.add("");
            lore.add(LanguageManager.getMessage("gui-open-actions"));
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            
            gui.setItem(i - startIndex, item);
        }
        
        // Quick action buttons
        ItemStack teleport = new ItemStack(Material.ENDER_PEARL);
        ItemMeta tpMeta = teleport.getItemMeta();
        tpMeta.setDisplayName(LanguageManager.getMessage("gui-teleport-to-player"));
        List<String> tpLore = new ArrayList<>();
        tpLore.add(LanguageManager.getMessage("gui-teleport-description").replace("[PLAYER]", cleanTargetName));
        tpMeta.setLore(tpLore);
        teleport.setItemMeta(tpMeta);
        gui.setItem(45, teleport);
        
        ItemStack clearAll = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta clearMeta = clearAll.getItemMeta();
        clearMeta.setDisplayName(LanguageManager.getMessage("gui-clear-all-reports"));
        List<String> clearLore = new ArrayList<>();
        clearLore.add(LanguageManager.getMessage("gui-clear-description").replace("[PLAYER]", cleanTargetName));
        clearMeta.setLore(clearLore);
        clearAll.setItemMeta(clearMeta);
        gui.setItem(46, clearAll);
        
        // Navigation
        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(LanguageManager.getMessage("gui-prev-page"));
            prevPage.setItemMeta(prevMeta);
            gui.setItem(48, prevPage);
        }
        
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(LanguageManager.getMessage("gui-back-to-list"));
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        if (page < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(LanguageManager.getMessage("gui-next-page"));
            nextPage.setItemMeta(nextMeta);
            gui.setItem(50, nextPage);
        }
        
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(LanguageManager.getMessage("gui-close"));
        close.setItemMeta(closeMeta);
        gui.setItem(53, close);
        
        admin.openInventory(gui);
    }

    /**
     * Opens action menu for a specific report
     */
    public static void openReportActionsGUI(Player admin, long reportId, String targetName) {
        ReportManager.Report report = findReportById(reportId);
        
        if (report == null) {
            VersionUtils.sendMessage(admin, LanguageManager.getMessage("no-reports"));
            return;
        }
        
        Inventory gui = Bukkit.createInventory(null, 27, LanguageManager.getMessage("gui-report-actions").replace("[ID]", String.valueOf(reportId)));
        
        // Teleport to reporter
        ItemStack tpReporter = new ItemStack(Material.ENDER_PEARL);
        ItemMeta tpReporterMeta = tpReporter.getItemMeta();
        tpReporterMeta.setDisplayName(LanguageManager.getMessage("gui-teleport-to-reporter"));
        List<String> tpReporterLore = new ArrayList<>();
        tpReporterLore.add(LanguageManager.getMessage("gui-teleport-description").replace("[PLAYER]", report.reporter));
        tpReporterLore.add(LanguageManager.getMessage("gui-teleport-location").replace("[LOCATION]", report.reporterLocation));
        tpReporterMeta.setLore(tpReporterLore);
        tpReporter.setItemMeta(tpReporterMeta);
        gui.setItem(10, tpReporter);
        
        // Teleport to target
        ItemStack tpTarget = new ItemStack(Material.ENDER_EYE);
        ItemMeta tpTargetMeta = tpTarget.getItemMeta();
        tpTargetMeta.setDisplayName(LanguageManager.getMessage("gui-teleport-to-target"));
        List<String> tpTargetLore = new ArrayList<>();
        tpTargetLore.add(LanguageManager.getMessage("gui-teleport-description").replace("[PLAYER]", report.target));
        tpTargetLore.add(LanguageManager.getMessage("gui-teleport-location").replace("[LOCATION]", report.targetLocation));
        tpTargetMeta.setLore(tpTargetLore);
        tpTarget.setItemMeta(tpTargetMeta);
        gui.setItem(11, tpTarget);
        
        // Punish target
        ItemStack punish = new ItemStack(Material.IRON_SWORD);
        ItemMeta punishMeta = punish.getItemMeta();
        punishMeta.setDisplayName(LanguageManager.getMessage("gui-punish-player"));
        List<String> punishLore = new ArrayList<>();
        punishLore.add(LanguageManager.getMessage("gui-punish-description"));
        punishLore.add(LanguageManager.getMessage("gui-punish-for").replace("[PLAYER]", report.target));
        punishMeta.setLore(punishLore);
        punish.setItemMeta(punishMeta);
        gui.setItem(13, punish);
        
        // Resolve report
        ItemStack resolve = new ItemStack(Material.LIME_DYE);
        ItemMeta resolveMeta = resolve.getItemMeta();
        resolveMeta.setDisplayName(LanguageManager.getMessage("gui-resolve-report"));
        List<String> resolveLore = new ArrayList<>();
        resolveLore.add(LanguageManager.getMessage("gui-resolve-description"));
        resolveLore.add(LanguageManager.getMessage("gui-resolve-description2"));
        resolveMeta.setLore(resolveLore);
        resolve.setItemMeta(resolveMeta);
        gui.setItem(15, resolve);
        
        // Delete report
        ItemStack delete = new ItemStack(Material.RED_DYE);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(LanguageManager.getMessage("gui-delete-report"));
        List<String> deleteLore = new ArrayList<>();
        deleteLore.add(LanguageManager.getMessage("gui-delete-description"));
        deleteLore.add(LanguageManager.getMessage("gui-delete-description2"));
        deleteMeta.setLore(deleteLore);
        delete.setItemMeta(deleteMeta);
        gui.setItem(16, delete);
        
        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(LanguageManager.getMessage("gui-back"));
        back.setItemMeta(backMeta);
        gui.setItem(22, back);
        
        admin.openInventory(gui);
    }
    
    /**
     * Opens punishment menu for a player
     */
    public static void openPunishmentGUI(Player admin, String targetName, long reportId) {
        Inventory gui = Bukkit.createInventory(null, 27, LanguageManager.getMessage("gui-punishment-menu").replace("[PLAYER]", targetName).replace("[ID]", String.valueOf(reportId)));
        
        // Warn
        ItemStack warn = new ItemStack(Material.YELLOW_DYE);
        ItemMeta warnMeta = warn.getItemMeta();
        warnMeta.setDisplayName(LanguageManager.getMessage("gui-punishment-warn"));
        warn.setItemMeta(warnMeta);
        gui.setItem(10, warn);
        
        // Kick
        ItemStack kick = new ItemStack(Material.IRON_DOOR);
        ItemMeta kickMeta = kick.getItemMeta();
        kickMeta.setDisplayName(LanguageManager.getMessage("gui-punishment-kick"));
        kick.setItemMeta(kickMeta);
        gui.setItem(11, kick);
        
        // Mute 1h
        ItemStack mute1h = new ItemStack(Material.PAPER);
        ItemMeta mute1hMeta = mute1h.getItemMeta();
        mute1hMeta.setDisplayName(LanguageManager.getMessage("gui-punishment-mute-1h"));
        mute1h.setItemMeta(mute1hMeta);
        gui.setItem(12, mute1h);
        
        // Mute 1d
        ItemStack mute1d = new ItemStack(Material.BOOK);
        ItemMeta mute1dMeta = mute1d.getItemMeta();
        mute1dMeta.setDisplayName(LanguageManager.getMessage("gui-punishment-mute-1d"));
        mute1d.setItemMeta(mute1dMeta);
        gui.setItem(13, mute1d);
        
        // Ban 1d
        ItemStack ban1d = new ItemStack(Material.IRON_AXE);
        ItemMeta ban1dMeta = ban1d.getItemMeta();
        ban1dMeta.setDisplayName(LanguageManager.getMessage("gui-punishment-ban-1d"));
        ban1d.setItemMeta(ban1dMeta);
        gui.setItem(14, ban1d);
        
        // Ban 7d
        ItemStack ban7d = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta ban7dMeta = ban7d.getItemMeta();
        ban7dMeta.setDisplayName(LanguageManager.getMessage("gui-punishment-ban-7d"));
        ban7d.setItemMeta(ban7dMeta);
        gui.setItem(15, ban7d);
        
        // Permanent ban
        ItemStack banPerm = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta banPermMeta = banPerm.getItemMeta();
        banPermMeta.setDisplayName(LanguageManager.getMessage("gui-punishment-ban-perm"));
        banPerm.setItemMeta(banPermMeta);
        gui.setItem(16, banPerm);
        
        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(LanguageManager.getMessage("gui-back"));
        back.setItemMeta(backMeta);
        gui.setItem(22, back);
        
        admin.openInventory(gui);
    }
    
    /**
     * Opens confirmation menu after punishment
     */
    public static void openPunishmentConfirmGUI(Player admin, String targetName, long reportId, String punishmentType) {
        Inventory gui = Bukkit.createInventory(null, 27, LanguageManager.getMessage("gui-punishment-confirm"));
        
        // Info item
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(LanguageManager.getMessage("gui-punishment-applied"));
        List<String> infoLore = new ArrayList<>();
        infoLore.add(LanguageManager.getMessage("gui-punishment-player").replace("[PLAYER]", targetName));
        infoLore.add(LanguageManager.getMessage("gui-punishment-type").replace("[TYPE]", punishmentType));
        infoLore.add("");
        infoLore.add(LanguageManager.getMessage("gui-punishment-question"));
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        gui.setItem(13, info);
        
        // Resolve and close report
        ItemStack resolve = new ItemStack(Material.LIME_DYE);
        ItemMeta resolveMeta = resolve.getItemMeta();
        resolveMeta.setDisplayName(LanguageManager.getMessage("gui-close-report"));
        List<String> resolveLore = new ArrayList<>();
        resolveLore.add(LanguageManager.getMessage("gui-close-report-desc"));
        resolveLore.add(LanguageManager.getMessage("gui-close-report-desc2"));
        resolveMeta.setLore(resolveLore);
        resolve.setItemMeta(resolveMeta);
        gui.setItem(11, resolve);
        
        // Keep report open
        ItemStack keep = new ItemStack(Material.YELLOW_DYE);
        ItemMeta keepMeta = keep.getItemMeta();
        keepMeta.setDisplayName(LanguageManager.getMessage("gui-keep-report"));
        List<String> keepLore = new ArrayList<>();
        keepLore.add(LanguageManager.getMessage("gui-keep-report-desc"));
        keepLore.add(LanguageManager.getMessage("gui-keep-report-desc2"));
        keepMeta.setLore(keepLore);
        keep.setItemMeta(keepMeta);
        gui.setItem(15, keep);
        
        // Close menu
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(LanguageManager.getMessage("gui-close-menu"));
        close.setItemMeta(closeMeta);
        gui.setItem(22, close);
        
        admin.openInventory(gui);
    }
    
    /**
     * Helper method to create player head
     */
    private static ItemStack createPlayerHead(String playerName) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            meta.setOwningPlayer(player);
        }
        
        head.setItemMeta(meta);
        return head;
    }
    
    /**
     * Helper method to find report by ID
     */
    private static ReportManager.Report findReportById(long id) {
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
