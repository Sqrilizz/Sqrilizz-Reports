package dev.sqrilizz.SQRILIZZREPORTS;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ReportsGUI {

    private static final int REPORTS_PER_PAGE = 36; // 4 rows of 9

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
        Map<String, List<ReportManager.Report>> reports =
            ReportManager.getReports();

        if (reports.isEmpty()) {
            VersionUtils.sendMessage(
                player,
                LanguageManager.getMessage("no-reports")
            );
            return;
        }

        // Sort players by active report count (most reports first)
        List<String> reportedPlayers = new ArrayList<>(reports.keySet());
        reportedPlayers.sort((a, b) -> {
            long aCount = reports
                .getOrDefault(a, new ArrayList<>())
                .stream()
                .filter(r -> !r.isResolved())
                .count();
            long bCount = reports
                .getOrDefault(b, new ArrayList<>())
                .stream()
                .filter(r -> !r.isResolved())
                .count();
            return Long.compare(bCount, aCount);
        });

        int totalPages = (int) Math.ceil(
            (double) reportedPlayers.size() / REPORTS_PER_PAGE
        );

        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        Inventory gui = Bukkit.createInventory(
            null,
            54,
            LanguageManager.getMessage("gui-reports-title").replace(
                "[PAGE]",
                String.valueOf(page + 1)
            )
        );

        // Fill top border (row 0, slots 0-8)
        fillBorder(gui, 0, 8);

        int startIndex = page * REPORTS_PER_PAGE;
        int endIndex = Math.min(
            startIndex + REPORTS_PER_PAGE,
            reportedPlayers.size()
        );

        // Place player heads in rows 1-4 (slots 9-44)
        for (int i = startIndex; i < endIndex; i++) {
            String targetName = reportedPlayers.get(i);
            List<ReportManager.Report> playerReports = reports.get(targetName);
            int totalCount = playerReports.size();
            int activeCount = (int) playerReports
                .stream()
                .filter(r -> !r.isResolved())
                .count();

            ItemStack item = createPlayerHead(targetName);
            ItemMeta meta = item.getItemMeta();

            // Color name by active report severity
            String color =
                activeCount >= 5 ? "&c&l" : activeCount >= 3 ? "&6" : "&e";
            meta.setDisplayName(
                ColorManager.colorize(
                    color + NameUtils.cleanPlayerName(targetName)
                )
            );

            List<String> lore = new ArrayList<>();
            lore.add(ColorManager.colorize("&8-----------------"));
            lore.add(
                ColorManager.colorize(
                    "&7" +
                        LanguageManager.getMessage("gui-reports-count").replace(
                            "[COUNT]",
                            "&c" + activeCount + "&7/&f" + totalCount
                        )
                )
            );

            // Show latest report reason preview
            ReportManager.Report latest = playerReports.get(
                playerReports.size() - 1
            );
            String reasonPreview =
                latest.reason.length() > 30
                    ? latest.reason.substring(0, 30) + "..."
                    : latest.reason;
            lore.add(
                ColorManager.colorize(
                    "&7" +
                        LanguageManager.getRawMessage(
                            "gui-report-reason"
                        ).replace("[REASON]", reasonPreview)
                )
            );
            lore.add(ColorManager.colorize("&7" + latest.getTimeAgo()));
            lore.add(ColorManager.colorize("&8-----------------"));
            lore.add(
                LanguageManager.getMessage("gui-click-left").replace(
                    "[ACTION]",
                    LanguageManager.getMessage("gui-open-reports")
                )
            );
            lore.add(
                LanguageManager.getMessage("gui-click-right").replace(
                    "[ACTION]",
                    LanguageManager.getMessage("gui-clear-all")
                )
            );

            meta.setLore(lore);
            item.setItemMeta(meta);

            gui.setItem(9 + (i - startIndex), item);
        }

        // Fill bottom border (row 5, slots 45-53)
        fillBorder(gui, 45, 53);

        // Stats item (slot 45)
        int totalReports = reports.values().stream().mapToInt(List::size).sum();
        int activeReports = (int) reports
            .values()
            .stream()
            .flatMap(List::stream)
            .filter(r -> !r.isResolved())
            .count();
        int totalPlayers = reports.size();
        int activePlayers = (int) reports
            .values()
            .stream()
            .filter(list -> list.stream().anyMatch(r -> !r.isResolved()))
            .count();
        ItemStack stats = new ItemStack(Material.BOOK);
        ItemMeta statsMeta = stats.getItemMeta();
        statsMeta.setDisplayName(
            ColorManager.colorize(
                "&b" +
                    LanguageManager.getRawMessage("gui-reports-title").replace(
                        "[PAGE]",
                        ""
                    )
            )
        );
        List<String> statsLore = new ArrayList<>();
        statsLore.add(ColorManager.colorize("&8-----------------"));
        statsLore.add(
            ColorManager.colorize(
                "&7Active: &c" + activeReports + "&7/" + totalReports
            )
        );
        statsLore.add(
            ColorManager.colorize(
                "&7Players: &f" + activePlayers + "&7/" + totalPlayers
            )
        );
        statsLore.add(
            ColorManager.colorize("&7Page: &f" + (page + 1) + "/" + totalPages)
        );
        statsLore.add(ColorManager.colorize("&8-----------------"));
        statsMeta.setLore(statsLore);
        stats.setItemMeta(statsMeta);
        gui.setItem(45, stats);

        // Navigation buttons
        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(
                LanguageManager.getMessage("gui-prev-page")
            );
            prevPage.setItemMeta(prevMeta);
            gui.setItem(48, prevPage);
        }

        if (page < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(
                LanguageManager.getMessage("gui-next-page")
            );
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
    public static void openPlayerReportsGUI(
        Player admin,
        String targetName,
        int page
    ) {
        String cleanTargetName = NameUtils.cleanPlayerName(targetName);
        List<ReportManager.Report> playerReports =
            ReportManager.getPlayerReports(cleanTargetName);

        if (playerReports.isEmpty()) {
            VersionUtils.sendMessage(
                admin,
                LanguageManager.getMessage("no-reports-for-player").replace(
                    "[PLAYER]",
                    cleanTargetName
                )
            );
            return;
        }

        int totalPages = (int) Math.ceil(
            (double) playerReports.size() / REPORTS_PER_PAGE
        );

        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        Inventory gui = Bukkit.createInventory(
            null,
            54,
            LanguageManager.getMessage("gui-reports-on-player").replace(
                "[PLAYER]",
                cleanTargetName
            )
        );

        int startIndex = page * REPORTS_PER_PAGE;
        int endIndex = Math.min(
            startIndex + REPORTS_PER_PAGE,
            playerReports.size()
        );

        for (int i = startIndex; i < endIndex; i++) {
            ReportManager.Report report = playerReports.get(i);
            boolean isResolved = report.isResolved();

            ItemStack item = new ItemStack(
                isResolved ? Material.MAP : Material.PAPER
            );
            ItemMeta meta = item.getItemMeta();
            String idPrefix = isResolved ? "&7&m" : "&e";
            meta.setDisplayName(
                ColorManager.colorize(
                    idPrefix +
                        LanguageManager.getRawMessage("gui-report-id").replace(
                            "[ID]",
                            String.valueOf(report.id)
                        ) +
                        (isResolved ? " &8[&a✓&8]" : "")
                )
            );

            List<String> lore = new ArrayList<>();
            lore.add(
                LanguageManager.getMessage("gui-report-from").replace(
                    "[REPORTER]",
                    NameUtils.cleanPlayerName(report.reporter)
                )
            );
            lore.add(
                LanguageManager.getMessage("gui-report-reason").replace(
                    "[REASON]",
                    report.reason
                )
            );
            lore.add(
                LanguageManager.getMessage("gui-report-time").replace(
                    "[TIME]",
                    report.getFormattedTime()
                )
            );
            lore.add(
                LanguageManager.getMessage("gui-report-ago").replace(
                    "[AGO]",
                    report.getTimeAgo()
                )
            );
            if (isResolved) {
                lore.add(
                    ColorManager.colorize(
                        "&a" +
                            LanguageManager.getMessage(
                                "gui-report-status"
                            ).replace(
                                "[STATUS]",
                                LanguageManager.getMessage("status-resolved")
                            )
                    )
                );
                if (!report.getResolvedBy().isEmpty()) {
                    lore.add(
                        ColorManager.colorize(
                            "&7" +
                                LanguageManager.getMessage(
                                    "gui-report-resolved-by"
                                ).replace("[PLAYER]", report.getResolvedBy())
                        )
                    );
                    lore.add(
                        ColorManager.colorize(
                            "&7" +
                                LanguageManager.getMessage(
                                    "gui-report-resolved-at"
                                ).replace(
                                    "[TIME]",
                                    report.getFormattedResolvedTime()
                                )
                        )
                    );
                }
            } else {
                lore.add("");
                lore.add(LanguageManager.getMessage("gui-open-actions"));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            gui.setItem(i - startIndex, item);
        }

        // Quick action buttons
        ItemStack teleport = new ItemStack(Material.ENDER_PEARL);
        ItemMeta tpMeta = teleport.getItemMeta();
        tpMeta.setDisplayName(
            LanguageManager.getMessage("gui-teleport-to-player")
        );
        List<String> tpLore = new ArrayList<>();
        tpLore.add(
            LanguageManager.getMessage("gui-teleport-description").replace(
                "[PLAYER]",
                cleanTargetName
            )
        );
        tpMeta.setLore(tpLore);
        teleport.setItemMeta(tpMeta);
        gui.setItem(45, teleport);

        ItemStack clearAll = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta clearMeta = clearAll.getItemMeta();
        clearMeta.setDisplayName(
            LanguageManager.getMessage("gui-clear-all-reports")
        );
        List<String> clearLore = new ArrayList<>();
        clearLore.add(
            LanguageManager.getMessage("gui-clear-description").replace(
                "[PLAYER]",
                cleanTargetName
            )
        );
        clearMeta.setLore(clearLore);
        clearAll.setItemMeta(clearMeta);
        gui.setItem(46, clearAll);

        // Navigation
        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(
                LanguageManager.getMessage("gui-prev-page")
            );
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
            nextMeta.setDisplayName(
                LanguageManager.getMessage("gui-next-page")
            );
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
    public static void openReportActionsGUI(
        Player admin,
        long reportId,
        String targetName
    ) {
        ReportManager.Report report = findReportById(reportId);

        if (report == null) {
            VersionUtils.sendMessage(
                admin,
                LanguageManager.getMessage("report-not-found")
            );
            return;
        }

        Inventory gui = Bukkit.createInventory(
            null,
            45,
            LanguageManager.getMessage("gui-report-actions").replace(
                "[ID]",
                String.valueOf(reportId)
            )
        );

        // Fill borders
        fillBorder(gui, 0, 8);
        fillBorder(gui, 36, 44);

        // Report info card (slot 4, top center)
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(
            ColorManager.colorize(
                "&b#" +
                    report.id +
                    " - " +
                    NameUtils.cleanPlayerName(report.target)
            )
        );
        List<String> infoLore = new ArrayList<>();
        infoLore.add(ColorManager.colorize("&8-----------------"));
        infoLore.add(
            LanguageManager.getMessage("gui-report-from").replace(
                "[REPORTER]",
                report.isAnonymous
                    ? ColorManager.colorize("&7&oAnonymous")
                    : NameUtils.cleanPlayerName(report.reporter)
            )
        );
        infoLore.add(
            LanguageManager.getMessage("gui-report-reason").replace(
                "[REASON]",
                report.reason
            )
        );
        infoLore.add(
            LanguageManager.getMessage("gui-report-time").replace(
                "[TIME]",
                report.getFormattedTime()
            )
        );
        infoLore.add(
            LanguageManager.getMessage("gui-report-ago").replace(
                "[AGO]",
                report.getTimeAgo()
            )
        );
        infoLore.add(ColorManager.colorize("&8-----------------"));
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        gui.setItem(4, info);

        // ===== NEW ACTION BUTTONS (row 2, slots 14-16) =====
        boolean isBugReport =
            "BUG_REPORT".equals(report.target) || report.target == null;

        // Notify player (slot 14)
        ItemStack notify = VersionUtils.createItem(
            "NAME_TAG",
            "NAME_TAG",
            (short) 0
        );
        ItemMeta notifyMeta = notify.getItemMeta();
        notifyMeta.setDisplayName(
            LanguageManager.getMessage(
                isBugReport ? "gui-notify-player-bug" : "gui-notify-player"
            )
        );
        List<String> notifyLore = new ArrayList<>();
        notifyLore.add(ColorManager.colorize("&8-----------------"));
        notifyLore.add(LanguageManager.getMessage("gui-notify-description"));
        notifyLore.add(
            LanguageManager.getMessage("gui-notify-description2").replace(
                "[PLAYER]",
                report.reporter
            )
        );
        notifyLore.add(ColorManager.colorize("&8-----------------"));
        notifyMeta.setLore(notifyLore);
        notify.setItemMeta(notifyMeta);
        gui.setItem(14, notify);

        // Mark resolved (slot 15)
        ItemStack resolved = new ItemStack(Material.EMERALD);
        ItemMeta resolvedMeta = resolved.getItemMeta();
        resolvedMeta.setDisplayName(
            LanguageManager.getMessage(
                isBugReport ? "gui-resolved-bug" : "gui-resolved"
            )
        );
        List<String> resolvedLore = new ArrayList<>();
        resolvedLore.add(ColorManager.colorize("&8-----------------"));
        resolvedLore.add(
            LanguageManager.getMessage("gui-resolved-description")
        );
        resolvedLore.add(
            LanguageManager.getMessage("gui-resolved-description2")
        );
        resolvedLore.add(ColorManager.colorize("&8-----------------"));
        resolvedMeta.setLore(resolvedLore);
        resolved.setItemMeta(resolvedMeta);
        gui.setItem(15, resolved);

        // Not a bug / Not a violation (slot 16)
        ItemStack notBug = new ItemStack(Material.BARRIER);
        ItemMeta notBugMeta = notBug.getItemMeta();
        notBugMeta.setDisplayName(
            LanguageManager.getMessage(
                isBugReport ? "gui-not-bug" : "gui-not-violation"
            )
        );
        List<String> notBugLore = new ArrayList<>();
        notBugLore.add(ColorManager.colorize("&8-----------------"));
        notBugLore.add(
            LanguageManager.getMessage(
                isBugReport
                    ? "gui-not-bug-description"
                    : "gui-not-violation-description"
            )
        );
        notBugLore.add(LanguageManager.getMessage("gui-not-bug-description2"));
        notBugLore.add(ColorManager.colorize("&8-----------------"));
        notBugMeta.setLore(notBugLore);
        notBug.setItemMeta(notBugMeta);
        gui.setItem(16, notBug);

        // Teleport to reporter (row 3)
        ItemStack tpReporter = new ItemStack(Material.ENDER_PEARL);
        ItemMeta tpReporterMeta = tpReporter.getItemMeta();
        tpReporterMeta.setDisplayName(
            LanguageManager.getMessage("gui-teleport-to-reporter")
        );
        List<String> tpReporterLore = new ArrayList<>();
        tpReporterLore.add(ColorManager.colorize("&8-----------------"));
        tpReporterLore.add(
            LanguageManager.getMessage("gui-teleport-description").replace(
                "[PLAYER]",
                report.reporter
            )
        );
        tpReporterLore.add(
            LanguageManager.getMessage("gui-teleport-location").replace(
                "[LOCATION]",
                report.reporterLocation
            )
        );
        tpReporterLore.add(ColorManager.colorize("&8-----------------"));
        tpReporterMeta.setLore(tpReporterLore);
        tpReporter.setItemMeta(tpReporterMeta);
        gui.setItem(19, tpReporter);

        // Teleport to target
        ItemStack tpTarget = VersionUtils.createItem(
            "ENDER_EYE",
            "EYE_OF_ENDER",
            (short) 0
        );
        ItemMeta tpTargetMeta = tpTarget.getItemMeta();
        tpTargetMeta.setDisplayName(
            LanguageManager.getMessage("gui-teleport-to-target")
        );
        List<String> tpTargetLore = new ArrayList<>();
        tpTargetLore.add(ColorManager.colorize("&8-----------------"));
        tpTargetLore.add(
            LanguageManager.getMessage("gui-teleport-description").replace(
                "[PLAYER]",
                report.target
            )
        );
        tpTargetLore.add(
            LanguageManager.getMessage("gui-teleport-location").replace(
                "[LOCATION]",
                report.targetLocation
            )
        );
        tpTargetLore.add(ColorManager.colorize("&8-----------------"));
        tpTargetMeta.setLore(tpTargetLore);
        tpTarget.setItemMeta(tpTargetMeta);
        gui.setItem(21, tpTarget);

        // Punish target (center)
        ItemStack punish = new ItemStack(Material.IRON_SWORD);
        ItemMeta punishMeta = punish.getItemMeta();
        punishMeta.setDisplayName(
            LanguageManager.getMessage("gui-punish-player")
        );
        punishMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        List<String> punishLore = new ArrayList<>();
        punishLore.add(ColorManager.colorize("&8-----------------"));
        punishLore.add(LanguageManager.getMessage("gui-punish-description"));
        punishLore.add(
            LanguageManager.getMessage("gui-punish-for").replace(
                "[PLAYER]",
                report.target
            )
        );
        punishLore.add(ColorManager.colorize("&8-----------------"));
        punishMeta.setLore(punishLore);
        punish.setItemMeta(punishMeta);
        gui.setItem(22, punish);

        // Resolve report
        ItemStack resolve = VersionUtils.createItem(
            "LIME_DYE",
            "INK_SACK",
            (short) 10
        );
        ItemMeta resolveMeta = resolve.getItemMeta();
        resolveMeta.setDisplayName(
            LanguageManager.getMessage("gui-resolve-report")
        );
        List<String> resolveLore = new ArrayList<>();
        resolveLore.add(ColorManager.colorize("&8-----------------"));
        resolveLore.add(LanguageManager.getMessage("gui-resolve-description"));
        resolveLore.add(LanguageManager.getMessage("gui-resolve-description2"));
        resolveLore.add(ColorManager.colorize("&8-----------------"));
        resolveMeta.setLore(resolveLore);
        resolve.setItemMeta(resolveMeta);
        gui.setItem(23, resolve);

        // Delete report
        ItemStack delete = VersionUtils.createItem(
            "RED_DYE",
            "INK_SACK",
            (short) 1
        );
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(
            LanguageManager.getMessage("gui-delete-report")
        );
        List<String> deleteLore = new ArrayList<>();
        deleteLore.add(ColorManager.colorize("&8-----------------"));
        deleteLore.add(LanguageManager.getMessage("gui-delete-description"));
        deleteLore.add(LanguageManager.getMessage("gui-delete-description2"));
        deleteLore.add(ColorManager.colorize("&8-----------------"));
        deleteMeta.setLore(deleteLore);
        delete.setItemMeta(deleteMeta);
        gui.setItem(25, delete);

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(LanguageManager.getMessage("gui-back"));
        back.setItemMeta(backMeta);
        gui.setItem(40, back);

        admin.openInventory(gui);
    }

    /**
     * Opens punishment menu for a player
     */
    public static void openPunishmentGUI(
        Player admin,
        String targetName,
        long reportId
    ) {
        Inventory gui = Bukkit.createInventory(
            null,
            45,
            LanguageManager.getMessage("gui-punishment-menu")
                .replace("[PLAYER]", targetName)
                .replace("[ID]", String.valueOf(reportId))
        );

        // Fill borders
        fillBorder(gui, 0, 8);
        fillBorder(gui, 36, 44);

        // Target info (top center)
        ItemStack targetInfo = createPlayerHead(targetName);
        ItemMeta targetMeta = targetInfo.getItemMeta();
        targetMeta.setDisplayName(
            ColorManager.colorize("&c" + NameUtils.cleanPlayerName(targetName))
        );
        List<String> targetLore = new ArrayList<>();
        targetLore.add(ColorManager.colorize("&8-----------------"));
        targetLore.add(ColorManager.colorize("&7Report ID: &f#" + reportId));
        targetLore.add(ColorManager.colorize("&8-----------------"));
        targetMeta.setLore(targetLore);
        targetInfo.setItemMeta(targetMeta);
        gui.setItem(4, targetInfo);

        // Warn
        ItemStack warn = VersionUtils.createItem(
            "YELLOW_DYE",
            "INK_SACK",
            (short) 11
        );
        ItemMeta warnMeta = warn.getItemMeta();
        warnMeta.setDisplayName(
            LanguageManager.getMessage("gui-punishment-warn")
        );
        List<String> warnLore = new ArrayList<>();
        warnLore.add(ColorManager.colorize("&8-----------------"));
        warnLore.add(ColorManager.colorize("&7Severity: &eLow"));
        warnLore.add(ColorManager.colorize("&8-----------------"));
        warnMeta.setLore(warnLore);
        warn.setItemMeta(warnMeta);
        gui.setItem(19, warn);

        // Kick
        ItemStack kick = new ItemStack(Material.IRON_DOOR);
        ItemMeta kickMeta = kick.getItemMeta();
        kickMeta.setDisplayName(
            LanguageManager.getMessage("gui-punishment-kick")
        );
        List<String> kickLore = new ArrayList<>();
        kickLore.add(ColorManager.colorize("&8-----------------"));
        kickLore.add(ColorManager.colorize("&7Severity: &6Medium"));
        kickLore.add(ColorManager.colorize("&8-----------------"));
        kickMeta.setLore(kickLore);
        kick.setItemMeta(kickMeta);
        gui.setItem(20, kick);

        // Mute 1h
        ItemStack mute1h = new ItemStack(Material.PAPER);
        ItemMeta mute1hMeta = mute1h.getItemMeta();
        mute1hMeta.setDisplayName(
            LanguageManager.getMessage("gui-punishment-mute-1h")
        );
        List<String> mute1hLore = new ArrayList<>();
        mute1hLore.add(ColorManager.colorize("&8-----------------"));
        mute1hLore.add(ColorManager.colorize("&7Duration: &f1 hour"));
        mute1hLore.add(ColorManager.colorize("&8-----------------"));
        mute1hMeta.setLore(mute1hLore);
        mute1h.setItemMeta(mute1hMeta);
        gui.setItem(21, mute1h);

        // Mute 1d
        ItemStack mute1d = new ItemStack(Material.BOOK);
        ItemMeta mute1dMeta = mute1d.getItemMeta();
        mute1dMeta.setDisplayName(
            LanguageManager.getMessage("gui-punishment-mute-1d")
        );
        List<String> mute1dLore = new ArrayList<>();
        mute1dLore.add(ColorManager.colorize("&8-----------------"));
        mute1dLore.add(ColorManager.colorize("&7Duration: &f1 day"));
        mute1dLore.add(ColorManager.colorize("&8-----------------"));
        mute1dMeta.setLore(mute1dLore);
        mute1d.setItemMeta(mute1dMeta);
        gui.setItem(22, mute1d);

        // Ban 1d
        ItemStack ban1d = VersionUtils.createItem(
            "IRON_AXE",
            "IRON_AXE",
            (short) 0
        );
        ItemMeta ban1dMeta = ban1d.getItemMeta();
        ban1dMeta.setDisplayName(
            LanguageManager.getMessage("gui-punishment-ban-1d")
        );
        ban1dMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        List<String> ban1dLore = new ArrayList<>();
        ban1dLore.add(ColorManager.colorize("&8-----------------"));
        ban1dLore.add(ColorManager.colorize("&7Duration: &f1 day"));
        ban1dLore.add(ColorManager.colorize("&7Severity: &cHigh"));
        ban1dLore.add(ColorManager.colorize("&8-----------------"));
        ban1dMeta.setLore(ban1dLore);
        ban1d.setItemMeta(ban1dMeta);
        gui.setItem(23, ban1d);

        // Ban 7d
        ItemStack ban7d = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta ban7dMeta = ban7d.getItemMeta();
        ban7dMeta.setDisplayName(
            LanguageManager.getMessage("gui-punishment-ban-7d")
        );
        ban7dMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        List<String> ban7dLore = new ArrayList<>();
        ban7dLore.add(ColorManager.colorize("&8-----------------"));
        ban7dLore.add(ColorManager.colorize("&7Duration: &f7 days"));
        ban7dLore.add(ColorManager.colorize("&7Severity: &cHigh"));
        ban7dLore.add(ColorManager.colorize("&8-----------------"));
        ban7dMeta.setLore(ban7dLore);
        ban7d.setItemMeta(ban7dMeta);
        gui.setItem(24, ban7d);

        // Permanent ban
        ItemStack banPerm = VersionUtils.createItem(
            "NETHERITE_AXE",
            "DIAMOND_AXE",
            (short) 0
        );
        ItemMeta banPermMeta = banPerm.getItemMeta();
        banPermMeta.setDisplayName(
            LanguageManager.getMessage("gui-punishment-ban-perm")
        );
        banPermMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        List<String> banPermLore = new ArrayList<>();
        banPermLore.add(ColorManager.colorize("&8-----------------"));
        banPermLore.add(ColorManager.colorize("&7Duration: &4PERMANENT"));
        banPermLore.add(ColorManager.colorize("&7Severity: &4&lCritical"));
        banPermLore.add(ColorManager.colorize("&8-----------------"));
        banPermMeta.setLore(banPermLore);
        banPerm.setItemMeta(banPermMeta);
        gui.setItem(25, banPerm);

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(LanguageManager.getMessage("gui-back"));
        back.setItemMeta(backMeta);
        gui.setItem(40, back);

        admin.openInventory(gui);
    }

    /**
     * Opens confirmation menu after punishment
     */
    public static void openPunishmentConfirmGUI(
        Player admin,
        String targetName,
        long reportId,
        String punishmentType
    ) {
        Inventory gui = Bukkit.createInventory(
            null,
            27,
            LanguageManager.getMessage("gui-punishment-confirm")
        );

        // Info item
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(
            LanguageManager.getMessage("gui-punishment-applied")
        );
        List<String> infoLore = new ArrayList<>();
        infoLore.add(
            LanguageManager.getMessage("gui-punishment-player").replace(
                "[PLAYER]",
                targetName
            )
        );
        infoLore.add(
            LanguageManager.getMessage("gui-punishment-type").replace(
                "[TYPE]",
                punishmentType
            )
        );
        infoLore.add("");
        infoLore.add(LanguageManager.getMessage("gui-punishment-question"));
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        gui.setItem(13, info);

        // Resolve and close report
        ItemStack resolve = VersionUtils.createItem(
            "LIME_DYE",
            "INK_SACK",
            (short) 10
        );
        ItemMeta resolveMeta = resolve.getItemMeta();
        resolveMeta.setDisplayName(
            LanguageManager.getMessage("gui-close-report")
        );
        List<String> resolveLore = new ArrayList<>();
        resolveLore.add(LanguageManager.getMessage("gui-close-report-desc"));
        resolveLore.add(LanguageManager.getMessage("gui-close-report-desc2"));
        resolveMeta.setLore(resolveLore);
        resolve.setItemMeta(resolveMeta);
        gui.setItem(11, resolve);

        // Keep report open
        ItemStack keep = VersionUtils.createItem(
            "YELLOW_DYE",
            "INK_SACK",
            (short) 11
        );
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
     * Fills border slots with gray glass panes
     */
    private static void fillBorder(Inventory gui, int from, int to) {
        ItemStack pane = VersionUtils.createItem(
            "GRAY_STAINED_GLASS_PANE",
            "STAINED_GLASS_PANE",
            (short) 7
        );
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);
        for (int i = from; i <= to; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, pane);
            }
        }
    }

    /**
     * Helper method to create player head
     */
    private static ItemStack createPlayerHead(String playerName) {
        ItemStack head = VersionUtils.createItem(
            "PLAYER_HEAD",
            "SKULL_ITEM",
            (short) 3
        );
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        VersionUtils.setSkullOwner(meta, playerName);

        head.setItemMeta(meta);
        return head;
    }

    /**
     * Helper method to find report by ID - O(1) lookup
     */
    private static ReportManager.Report findReportById(long id) {
        return ReportManager.getReportById(id);
    }

    /**
     * Opens confirmation GUI for Resolved / Not a Bug actions
     */
    public static void openConfirmActionGUI(
        Player admin,
        long reportId,
        String targetName,
        String reporterName,
        String actionType,
        boolean isBugReport
    ) {
        String titleKey = "RESOLVED".equals(actionType)
            ? "gui-confirm-resolved"
            : "gui-confirm-notbug";
        Inventory gui = Bukkit.createInventory(
            null,
            27,
            LanguageManager.getMessage(titleKey).replace(
                "[ID]",
                String.valueOf(reportId)
            )
        );

        // Info item (center)
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(
            ColorManager.colorize("&6#" + reportId + " &7- " + reporterName)
        );
        List<String> infoLore = new ArrayList<>();
        infoLore.add(ColorManager.colorize("&8-----------------"));
        infoLore.add(LanguageManager.getMessage("gui-confirm-question"));
        infoLore.add(ColorManager.colorize("&8-----------------"));
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        gui.setItem(13, info);

        // Confirm button
        ItemStack confirm = VersionUtils.createItem(
            "LIME_DYE",
            "INK_SACK",
            (short) 10
        );
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(
            LanguageManager.getMessage("gui-confirm-yes")
        );
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(LanguageManager.getMessage("gui-confirm-yes-lore"));
        confirmMeta.setLore(confirmLore);
        confirm.setItemMeta(confirmMeta);
        gui.setItem(11, confirm);

        // Cancel button
        ItemStack cancel = VersionUtils.createItem(
            "RED_DYE",
            "INK_SACK",
            (short) 1
        );
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(LanguageManager.getMessage("gui-confirm-no"));
        List<String> cancelLore = new ArrayList<>();
        cancelLore.add(LanguageManager.getMessage("gui-confirm-no-lore"));
        cancelMeta.setLore(cancelLore);
        cancel.setItemMeta(cancelMeta);
        gui.setItem(15, cancel);

        admin.openInventory(gui);
    }
}
