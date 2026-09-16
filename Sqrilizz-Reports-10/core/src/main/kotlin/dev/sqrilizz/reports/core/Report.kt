package dev.sqrilizz.reports.core

import java.time.Instant
import java.util.UUID

enum class ReportStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    DISMISSED
}

enum class ReportType {
    PLAYER,
    BUG
}

data class Report(
    val id: Long,
    val reporterId: UUID,
    val reporterName: String,
    val targetId: UUID?,
    val targetName: String,
    val reason: String,
    val sourceServer: String,
    val createdAt: Instant,
    val status: ReportStatus,
    val handledBy: String? = null,
    val handledAt: Instant? = null,
    val type: ReportType = ReportType.PLAYER
)

data class CreateReport(
    val reporterId: UUID,
    val reporterName: String,
    val targetId: UUID?,
    val targetName: String,
    val reason: String,
    val sourceServer: String,
    val type: ReportType = ReportType.PLAYER
)

data class ReportStats(
    val open: Long,
    val inProgress: Long,
    val resolved: Long,
    val dismissed: Long,
    val total: Long
)

interface ReportRepository : AutoCloseable {
    fun create(report: CreateReport): Report
    fun find(id: Long): Report?
    fun listOpen(limit: Int): List<Report>
    fun listOpenPaged(offset: Int, limit: Int): List<Report> {
        val all = listOpen(offset + limit)
        return if (offset >= all.size) emptyList() else all.subList(offset, minOf(offset + limit, all.size))
    }
    fun updateStatus(id: Long, status: ReportStatus, handledBy: String): Report?
    fun stats(): ReportStats = ReportStats(0, 0, 0, 0, 0)
    fun delete(id: Long): Boolean = false
    override fun close()
}

interface NetworkGateway {
    fun requestModeratorTransfer(moderatorId: UUID, destinationServer: String, reportId: Long)
}
