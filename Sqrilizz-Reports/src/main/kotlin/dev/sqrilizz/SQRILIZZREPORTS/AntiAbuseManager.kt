package dev.sqrilizz.SQRILIZZREPORTS

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

object AntiAbuseManager {

    private lateinit var abuseConfig: FileConfiguration
    private lateinit var abuseFile: File

    private val playerReportTimes = ConcurrentHashMap<String, MutableList<Long>>()
    private val playerTargetReports = ConcurrentHashMap<String, MutableMap<String, Int>>()
    private val falseReportCounts = ConcurrentHashMap<String, Int>()
    private val tempMutedPlayers = ConcurrentHashMap<String, Long>()

    @JvmStatic
    fun initialize() {
        abuseFile = File(Main.getInstance().dataFolder, "abuse_data.yml")
        if (!abuseFile.exists()) {
            try {
                abuseFile.createNewFile()
            } catch (e: IOException) {
                Main.getInstance().logger.severe("Could not create abuse_data.yml: ${e.message}")
            }
        }
        abuseConfig = YamlConfiguration.loadConfiguration(abuseFile)
        loadAbuseData()
    }

    @JvmStatic
    fun canReport(reporter: Player, targetName: String): Boolean {
        val reporterName = VersionUtils.getPlayerCleanName(reporter)

        if (isTempMuted(reporterName)) {
            val remainingTime = (tempMutedPlayers[reporterName]!! - System.currentTimeMillis()) / 1000
            VersionUtils.sendMessage(reporter, LanguageManager.getMessage("abuse-temp-mute")
                .replace("[DURATION]", remainingTime.toString()))
            return false
        }

        if (!checkPlayerReportLimit(reporterName, targetName)) {
            val limit = Main.getInstance().config.getInt("report-limits.per-player", 3)
            val current = getPlayerTargetReportCount(reporterName, targetName)
            VersionUtils.sendMessage(reporter, LanguageManager.getMessage("report-limit-reached")
                .replace("[LIMIT]", current.toString())
                .replace("[MAX]", limit.toString()))
            return false
        }

        if (!checkHourlyLimit(reporterName)) {
            val limit = Main.getInstance().config.getInt("report-limits.per-hour", 10)
            val current = getHourlyReportCount(reporterName)
            VersionUtils.sendMessage(reporter, LanguageManager.getMessage("hourly-limit-reached")
                .replace("[LIMIT]", current.toString())
                .replace("[MAX]", limit.toString()))
            return false
        }

        return true
    }

    @JvmStatic
    fun recordReport(reporter: Player, targetName: String) {
        val reporterName = VersionUtils.getPlayerCleanName(reporter)
        val currentTime = System.currentTimeMillis()

        playerReportTimes.getOrPut(reporterName) { mutableListOf() }.add(currentTime)
        playerTargetReports.getOrPut(reporterName) { mutableMapOf() }
            .merge(targetName, 1, Int::plus)

        checkForAbuse(reporter)
        saveAbuseData()
    }

    @JvmStatic
    fun markFalseReport(reporterName: String) {
        falseReportCounts.merge(reporterName, 1, Int::plus)
        saveAbuseData()
    }

    @JvmStatic
    fun hasLowPriority(reporterName: String): Boolean {
        val threshold = Main.getInstance().config.getInt("report-limits.false-report-threshold", 3)
        return (falseReportCounts[reporterName] ?: 0) >= threshold
    }

    @JvmStatic
    fun cleanupOldData() {
        val twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)

        for (reportTimes in playerReportTimes.values) {
            reportTimes.removeIf { it < twentyFourHoursAgo }
        }

        tempMutedPlayers.entries.removeIf { it.value < System.currentTimeMillis() }

        saveAbuseData()
    }

    private fun checkPlayerReportLimit(reporterName: String, targetName: String): Boolean {
        val limit = Main.getInstance().config.getInt("report-limits.per-player", 3)
        return getPlayerTargetReportCount(reporterName, targetName) < limit
    }

    private fun checkHourlyLimit(reporterName: String): Boolean {
        val limit = Main.getInstance().config.getInt("report-limits.per-hour", 10)
        return getHourlyReportCount(reporterName) < limit
    }

    private fun getPlayerTargetReportCount(reporterName: String, targetName: String): Int =
        playerTargetReports[reporterName]?.get(targetName) ?: 0

    private fun getHourlyReportCount(reporterName: String): Int {
        val reportTimes = playerReportTimes[reporterName] ?: return 0
        val oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
        return reportTimes.count { it > oneHourAgo }
    }

    private fun checkForAbuse(reporter: Player) {
        val reporterName = VersionUtils.getPlayerCleanName(reporter)
        val hourlyCount = getHourlyReportCount(reporterName)

        val warningThreshold = Main.getInstance().config.getInt("anti-abuse.warning-threshold", 5)
        val muteThreshold = Main.getInstance().config.getInt("anti-abuse.temp-mute-threshold", 8)

        when {
            hourlyCount >= muteThreshold -> {
                val muteDuration = Main.getInstance().config.getInt("anti-abuse.temp-mute-duration", 300)
                tempMutedPlayers[reporterName] = System.currentTimeMillis() + (muteDuration * 1000L)
                VersionUtils.sendMessage(reporter, LanguageManager.getMessage("abuse-temp-mute")
                    .replace("[DURATION]", muteDuration.toString()))
            }
            hourlyCount >= warningThreshold -> {
                VersionUtils.sendMessage(reporter, LanguageManager.getMessage("abuse-warning"))
            }
        }
    }

    private fun isTempMuted(playerName: String): Boolean {
        val muteTime = tempMutedPlayers[playerName] ?: return false
        if (System.currentTimeMillis() > muteTime) {
            tempMutedPlayers.remove(playerName)
            return false
        }
        return true
    }

    private fun saveAbuseData() {
        try {
            for ((playerName, times) in playerReportTimes) {
                abuseConfig.set("report-times.$playerName", times)
            }
            for ((reporterName, targets) in playerTargetReports) {
                for ((targetName, count) in targets) {
                    abuseConfig.set("target-reports.$reporterName.$targetName", count)
                }
            }
            for ((playerName, count) in falseReportCounts) {
                abuseConfig.set("false-reports.$playerName", count)
            }
            for ((playerName, muteTime) in tempMutedPlayers) {
                abuseConfig.set("temp-mutes.$playerName", muteTime)
            }
            abuseConfig.save(abuseFile)
        } catch (e: IOException) {
            Main.getInstance().logger.severe("Could not save abuse data: ${e.message}")
        }
    }

    private fun loadAbuseData() {
        abuseConfig.getConfigurationSection("report-times")?.getKeys(false)?.forEach { playerName ->
            val times = abuseConfig.getLongList("report-times.$playerName")
            playerReportTimes[playerName] = times.toMutableList()
        }

        abuseConfig.getConfigurationSection("target-reports")?.getKeys(false)?.forEach { reporterName ->
            val targets = mutableMapOf<String, Int>()
            abuseConfig.getConfigurationSection("target-reports.$reporterName")?.getKeys(false)?.forEach { targetName ->
                targets[targetName] = abuseConfig.getInt("target-reports.$reporterName.$targetName")
            }
            playerTargetReports[reporterName] = targets
        }

        abuseConfig.getConfigurationSection("false-reports")?.getKeys(false)?.forEach { playerName ->
            falseReportCounts[playerName] = abuseConfig.getInt("false-reports.$playerName")
        }

        abuseConfig.getConfigurationSection("temp-mutes")?.getKeys(false)?.forEach { playerName ->
            tempMutedPlayers[playerName] = abuseConfig.getLong("temp-mutes.$playerName")
        }
    }
}
