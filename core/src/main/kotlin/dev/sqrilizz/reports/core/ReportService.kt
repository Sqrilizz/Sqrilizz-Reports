package dev.sqrilizz.reports.core

import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class ReportService(
    private val repository: ReportRepository,
    private val cooldown: Duration,
    private val duplicateWindow: Duration,
    private val maxReportsPerHour: Int,
    private val maxReportsPerTarget: Int
) {
    private val lastCreatedAt = ConcurrentHashMap<UUID, Instant>()
    private val recentReports = ConcurrentHashMap<UUID, ArrayDeque<RecentReport>>()

    fun create(request: CreateReport): Result<Report> {
        val now = Instant.now()
        val last = lastCreatedAt[request.reporterId]
        if (last != null && now.isBefore(last.plus(cooldown))) {
            return Result.failure(ReportException("cooldown"))
        }
        if (request.reason.length !in 3..240) {
            return Result.failure(ReportException("reason"))
        }
        val history = recentReports.computeIfAbsent(request.reporterId) { ArrayDeque() }
        synchronized(history) {
            while (history.firstOrNull()?.createdAt?.isBefore(now.minus(Duration.ofHours(1))) == true) history.removeFirst()
            if (history.size >= maxReportsPerHour) return Result.failure(ReportException("hourly-limit"))
            val sameTarget = history.count { it.targetName.equals(request.targetName, true) }
            if (sameTarget >= maxReportsPerTarget) return Result.failure(ReportException("target-limit"))
            if (history.any { it.targetName.equals(request.targetName, true) && it.createdAt.isAfter(now.minus(duplicateWindow)) }) {
                return Result.failure(ReportException("duplicate"))
            }
            history.addLast(RecentReport(request.targetName, now))
        }
        lastCreatedAt[request.reporterId] = now
        return runCatching { repository.create(request) }.onFailure {
            synchronized(history) { history.removeLastOrNull() }
            lastCreatedAt.remove(request.reporterId, now)
        }
    }

    fun get(id: Long): Report? = repository.find(id)

    fun open(limit: Int = 45): List<Report> = repository.listOpen(limit.coerceIn(1, 200))

    fun openPaged(page: Int, pageSize: Int = 28): List<Report> {
        val safePage = maxOf(0, page)
        val safeSize = pageSize.coerceIn(1, 45)
        return repository.listOpenPaged(safePage * safeSize, safeSize)
    }

    fun resolve(id: Long, moderator: String): Report? = repository.updateStatus(id, ReportStatus.RESOLVED, moderator)

    fun dismiss(id: Long, moderator: String): Report? = repository.updateStatus(id, ReportStatus.DISMISSED, moderator)

    fun reopen(id: Long, moderator: String): Report? = repository.updateStatus(id, ReportStatus.OPEN, moderator)

    fun delete(id: Long): Boolean = repository.delete(id)

    fun stats(): ReportStats = repository.stats()

    fun clearCooldowns(uuid: UUID? = null) {
        if (uuid == null) {
            lastCreatedAt.clear()
            recentReports.clear()
        } else {
            lastCreatedAt.remove(uuid)
            recentReports.remove(uuid)
        }
    }
}

private data class RecentReport(val targetName: String, val createdAt: Instant)

class ReportException(message: String) : RuntimeException(message)
