package dev.sqrilizz.SQRILIZZREPORTS.api

import org.bukkit.entity.Player

data class ReportEvent @JvmOverloads constructor(
    val reporter: Player?,
    val target: Player?,
    val reason: String,
    val timestamp: Long,
    val systemName: String? = null
) {
    val isSystemReport: Boolean = systemName != null

    val reporterName: String
        get() = when {
            isSystemReport && systemName != null -> "SYSTEM_$systemName"
            reporter != null -> reporter.name
            else -> "Unknown"
        }

    val targetName: String
        get() = target?.name ?: "Unknown"
}
