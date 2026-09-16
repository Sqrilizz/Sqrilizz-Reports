package dev.sqrilizz.reports.server

import dev.sqrilizz.reports.core.Report
import dev.sqrilizz.reports.core.ReportStatus
import dev.sqrilizz.reports.core.ReportType
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.time.Duration
import java.time.Instant

private enum class QueueScreen {
    LIST,
    DETAILS,
    PUNISH
}

private class QueueHolder(
    val screen: QueueScreen,
    val page: Int = 0,
    val reportId: Long? = null,
    val slotMap: Map<Int, Any> = emptyMap()
) : InventoryHolder {
    override fun getInventory(): Inventory = throw UnsupportedOperationException()
}

class QueueGui(private val plugin: ReportsPlugin) : Listener {

    private val reportsPerPage = 28
    private val reportSlots = listOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    )

    fun openList(player: Player, page: Int = 0) {
        plugin.runAsync {
            val reports = plugin.reports.openPaged(page, reportsPerPage)
            val stats = plugin.reports.stats()
            val totalOpen = stats.open + stats.inProgress
            val hasNext = ((page + 1) * reportsPerPage) < totalOpen

            plugin.runOnPlayer(player) {
                val slotMap = mutableMapOf<Int, Any>()
                val holder = QueueHolder(QueueScreen.LIST, page = page, slotMap = slotMap)
                val title = LanguageManager.get("gui-reports-title", "PAGE" to (page + 1).toString())
                val inventory = Bukkit.createInventory(holder, 54, ColorManager.component(title))

                fillBorder(inventory)

                reports.forEachIndexed { index, report ->
                    if (index < reportSlots.size) {
                        val slot = reportSlots[index]
                        slotMap[slot] = report.id
                        inventory.setItem(slot, createReportCard(report))
                    }
                }

                if (page > 0) {
                    val prevItem = item(Material.ARROW, LanguageManager.get("gui-prev-page"), listOf("&7Go to page $page"))
                    inventory.setItem(45, prevItem)
                    slotMap[45] = "PREV"
                }

                val infoItem = item(
                    Material.BOOK,
                    "&e" + LanguageManager.get("gui-open-reports"),
                    listOf(
                        "&7Active: &f$totalOpen",
                        "&7Resolved: &a${stats.resolved}",
                        "&7Page: &f${page + 1}"
                    )
                )
                inventory.setItem(49, infoItem)

                if (hasNext) {
                    val nextItem = item(Material.ARROW, LanguageManager.get("gui-next-page"), listOf("&7Go to page ${page + 2}"))
                    inventory.setItem(53, nextItem)
                    slotMap[53] = "NEXT"
                }

                player.openInventory(inventory)
            }
        }
    }

    fun openDetails(player: Player, reportId: Long) {
        plugin.runAsync {
            val report = plugin.reports.get(reportId)
            if (report == null) {
                plugin.runOnPlayer(player) {
                    player.sendMessage(LanguageManager.getComponent("report-not-found"))
                }
                return@runAsync
            }

            plugin.runOnPlayer(player) {
                val slotMap = mutableMapOf<Int, Any>()
                val holder = QueueHolder(QueueScreen.DETAILS, reportId = reportId, slotMap = slotMap)
                val title = LanguageManager.get("gui-report-actions", "ID" to report.id.toString())
                val inventory = Bukkit.createInventory(holder, 27, ColorManager.component(title))

                fill(inventory)

                // Slot 4: Report Details / Player Skull
                val headItem = if (report.type == ReportType.BUG) {
                    item(Material.WRITABLE_BOOK, "&eBug: &f${report.targetName}", reportLore(report))
                } else {
                    skullItem(report.targetName, "&cReport: &f${report.targetName}", reportLore(report))
                }
                inventory.setItem(4, headItem)

                // Slot 10: Resolve
                inventory.setItem(10, item(Material.LIME_WOOL, LanguageManager.get("gui-resolve-report"), listOf(LanguageManager.get("gui-resolve-description"))))
                slotMap[10] = "RESOLVE"

                // Slot 12: Dismiss
                inventory.setItem(12, item(Material.RED_WOOL, LanguageManager.get("gui-not-violation"), listOf(LanguageManager.get("gui-not-violation-description"))))
                slotMap[12] = "DISMISS"

                // Slot 14: Teleport to target
                if (report.type == ReportType.PLAYER) {
                    inventory.setItem(14, item(Material.COMPASS, LanguageManager.get("gui-teleport-to-player"), listOf(LanguageManager.get("gui-teleport-description", "PLAYER" to report.targetName))))
                    slotMap[14] = "TELEPORT"
                }

                // Slot 16: Punish player
                if (report.type == ReportType.PLAYER && player.hasPermission("sqrilizzreports.punish")) {
                    inventory.setItem(16, item(Material.IRON_SWORD, LanguageManager.get("gui-punish-player"), listOf(LanguageManager.get("gui-punish-description"))))
                    slotMap[16] = "PUNISH"
                }

                // Slot 22: Back to list
                inventory.setItem(22, item(Material.ARROW, LanguageManager.get("gui-back-to-list"), listOf("&7Return to queue")))
                slotMap[22] = "BACK"

                // Slot 26: Cross-server transfer
                val transfer = report.sourceServer != plugin.serverId() && plugin.networkEnabled()
                if (transfer) {
                    inventory.setItem(26, item(Material.ENDER_PEARL, "&bGo to ${report.sourceServer}", listOf("&7Transfer through proxy")))
                    slotMap[26] = "TRANSFER"
                }

                player.openInventory(inventory)
            }
        }
    }

    fun openPunishMenu(player: Player, reportId: Long) {
        plugin.runAsync {
            val report = plugin.reports.get(reportId) ?: return@runAsync
            plugin.runOnPlayer(player) {
                val slotMap = mutableMapOf<Int, Any>()
                val holder = QueueHolder(QueueScreen.PUNISH, reportId = reportId, slotMap = slotMap)
                val title = LanguageManager.get("gui-punishment-menu", "PLAYER" to report.targetName, "ID" to report.id.toString())
                val inventory = Bukkit.createInventory(holder, 27, ColorManager.component(title))

                fill(inventory)

                inventory.setItem(10, item(Material.YELLOW_DYE, LanguageManager.get("gui-punishment-warn"), listOf("&7Issue a warning to &f${report.targetName}")))
                slotMap[10] = "warn"

                inventory.setItem(12, item(Material.ORANGE_DYE, LanguageManager.get("gui-punishment-kick"), listOf("&7Kick &f${report.targetName} &7from the server")))
                slotMap[12] = "kick"

                inventory.setItem(14, item(Material.RED_DYE, LanguageManager.get("gui-punishment-mute-1h"), listOf("&7Mute &f${report.targetName} &7for 1 hour")))
                slotMap[14] = "mute"

                inventory.setItem(16, item(Material.NETHERITE_SWORD, LanguageManager.get("gui-punishment-ban-1d"), listOf("&7Ban &f${report.targetName} &7for 1 day")))
                slotMap[16] = "ban"

                inventory.setItem(22, item(Material.ARROW, LanguageManager.get("gui-back"), listOf("&7Return to report details")))
                slotMap[22] = "BACK_TO_REPORT"

                player.openInventory(inventory)
            }
        }
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val holder = event.view.topInventory.holder as? QueueHolder ?: return
        if (event.rawSlot !in 0 until event.view.topInventory.size) return
        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        val action = holder.slotMap[event.rawSlot] ?: return

        when (holder.screen) {
            QueueScreen.LIST -> {
                when (action) {
                    "PREV" -> openList(player, maxOf(0, holder.page - 1))
                    "NEXT" -> openList(player, holder.page + 1)
                    is Long -> openDetails(player, action)
                }
            }
            QueueScreen.DETAILS -> {
                val reportId = holder.reportId ?: return
                when (action) {
                    "BACK" -> openList(player)
                    "RESOLVE" -> {
                        plugin.runAsync {
                            val updated = plugin.reports.resolve(reportId, player.name)
                            if (updated != null) {
                                plugin.notifications.reportStatusChanged(updated, player.name)
                                plugin.runOnPlayer(player) {
                                    player.sendMessage(LanguageManager.getComponent("report-resolved", "ID" to reportId.toString()))
                                    openList(player)
                                }
                            }
                        }
                    }
                    "DISMISS" -> {
                        plugin.runAsync {
                            val updated = plugin.reports.dismiss(reportId, player.name)
                            if (updated != null) {
                                plugin.notifications.reportStatusChanged(updated, player.name)
                                plugin.runOnPlayer(player) {
                                    player.sendMessage(LanguageManager.getComponent("report-closed", "ID" to reportId.toString()))
                                    openList(player)
                                }
                            }
                        }
                    }
                    "TELEPORT" -> {
                        plugin.runAsync {
                            val report = plugin.reports.get(reportId) ?: return@runAsync
                            plugin.runOnPlayer(player) {
                                val target = Bukkit.getPlayerExact(report.targetName)
                                if (target != null && target.isOnline) {
                                    player.teleport(target.location)
                                    player.sendMessage(LanguageManager.getComponent("teleport-success", "PLAYER" to target.name))
                                } else {
                                    player.sendMessage(LanguageManager.getComponent("teleport-offline"))
                                }
                            }
                        }
                    }
                    "PUNISH" -> openPunishMenu(player, reportId)
                    "TRANSFER" -> {
                        plugin.runAsync {
                            val report = plugin.reports.get(reportId) ?: return@runAsync
                            plugin.runOnPlayer(player) {
                                if (!player.hasPermission("sqrilizzreports.transfer")) {
                                    player.sendMessage(LanguageManager.getComponent("no-permission"))
                                    return@runOnPlayer
                                }
                                plugin.requestTransfer(player, report.sourceServer, report.id)
                                player.sendMessage(ColorManager.component("&bConnecting to ${report.sourceServer}…"))
                            }
                        }
                    }
                }
            }
            QueueScreen.PUNISH -> {
                val reportId = holder.reportId ?: return
                if (action == "BACK_TO_REPORT") {
                    openDetails(player, reportId)
                    return
                }
                val punishmentType = action.toString()
                plugin.runAsync {
                    val report = plugin.reports.get(reportId) ?: return@runAsync
                    val commandTemplate = plugin.config.getString("punishments.commands.$punishmentType", "")!!.trim()
                    plugin.runOnPlayer(player) {
                        if (commandTemplate.isNotBlank()) {
                            val cmd = commandTemplate.replace("{player}", report.targetName).replace("{reason}", report.reason)
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
                            player.sendMessage(LanguageManager.getComponent("admin-punished", "PLAYER" to report.targetName, "TYPE" to punishmentType.uppercase()))
                        }
                        openDetails(player, reportId)
                    }
                }
            }
        }
    }

    private fun fill(inventory: Inventory) {
        val bg = item(Material.GRAY_STAINED_GLASS_PANE, " ", emptyList())
        for (i in 0 until inventory.size) {
            inventory.setItem(i, bg)
        }
    }

    private fun fillBorder(inventory: Inventory) {
        val bg = item(Material.GRAY_STAINED_GLASS_PANE, " ", emptyList())
        for (i in 0 until 9) inventory.setItem(i, bg)
        for (i in 45 until 54) inventory.setItem(i, bg)
        for (i in 9 until 45 step 9) {
            inventory.setItem(i, bg)
            inventory.setItem(i + 8, bg)
        }
    }

    private fun reportLore(report: Report): List<String> {
        val duration = Duration.between(report.createdAt, Instant.now())
        val ago = when {
            duration.toDays() > 0 -> "${duration.toDays()} ${LanguageManager.get("time-days")}"
            duration.toHours() > 0 -> "${duration.toHours()} ${LanguageManager.get("time-hours")}"
            duration.toMinutes() > 0 -> "${duration.toMinutes()} ${LanguageManager.get("time-minutes")}"
            else -> "${duration.seconds} ${LanguageManager.get("time-seconds")}"
        }
        return listOf(
            LanguageManager.get("gui-report-id", "ID" to report.id.toString()),
            LanguageManager.get("gui-report-from", "REPORTER" to report.reporterName),
            LanguageManager.get("gui-report-reason", "REASON" to report.reason),
            "&7Server: &b${report.sourceServer}",
            LanguageManager.get("gui-report-ago", "AGO" to ago),
            LanguageManager.get("gui-report-status", "STATUS" to report.status.name)
        )
    }

    private fun createReportCard(report: Report): ItemStack {
        val material = if (report.type == ReportType.BUG) Material.WRITABLE_BOOK else Material.PAPER
        val typeBadge = if (report.type == ReportType.BUG) "&6[BUG]" else "&c[PLAYER]"
        val name = "$typeBadge &f${report.targetName} &7#${report.id}"
        val lore = listOf(
            "&7Reason: &f${report.reason.take(35)}",
            "&7From: &e${report.reporterName} &7• &b${report.sourceServer}",
            "",
            "&e▶ Click to review actions"
        )
        return item(material, name, lore)
    }

    private fun item(material: Material, name: String, lore: List<String>): ItemStack = ItemStack(material).apply {
        editMeta { meta ->
            meta.displayName(ColorManager.component(name))
            meta.lore(lore.map { ColorManager.component(it) })
        }
    }

    private fun skullItem(playerName: String, name: String, lore: List<String>): ItemStack = ItemStack(Material.PLAYER_HEAD).apply {
        editMeta(SkullMeta::class.java) { meta ->
            meta.displayName(ColorManager.component(name))
            meta.lore(lore.map { ColorManager.component(it) })
            @Suppress("DEPRECATION")
            meta.owningPlayer = Bukkit.getOfflinePlayer(playerName)
        }
    }
}
