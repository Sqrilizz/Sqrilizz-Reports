package dev.sqrilizz.reports.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class InMemoryReportRepository : ReportRepository {
    private val reports = ConcurrentHashMap<Long, Report>()
    private val counter = AtomicLong(1)

    override fun create(report: CreateReport): Report {
        val id = counter.getAndIncrement()
        val created = Report(
            id = id,
            reporterId = report.reporterId,
            reporterName = report.reporterName,
            targetId = report.targetId,
            targetName = report.targetName,
            reason = report.reason,
            sourceServer = report.sourceServer,
            createdAt = Instant.now(),
            status = ReportStatus.OPEN,
            type = report.type
        )
        reports[id] = created
        return created
    }

    override fun find(id: Long): Report? = reports[id]

    override fun listOpen(limit: Int): List<Report> = reports.values
        .filter { it.status == ReportStatus.OPEN || it.status == ReportStatus.IN_PROGRESS }
        .sortedByDescending { it.createdAt }
        .take(limit)

    override fun updateStatus(id: Long, status: ReportStatus, handledBy: String): Report? {
        val current = reports[id] ?: return null
        val updated = current.copy(status = status, handledBy = handledBy, handledAt = Instant.now())
        reports[id] = updated
        return updated
    }

    override fun delete(id: Long): Boolean = reports.remove(id) != null

    override fun stats(): ReportStats {
        var open = 0L
        var inProgress = 0L
        var resolved = 0L
        var dismissed = 0L
        for (r in reports.values) {
            when (r.status) {
                ReportStatus.OPEN -> open++
                ReportStatus.IN_PROGRESS -> inProgress++
                ReportStatus.RESOLVED -> resolved++
                ReportStatus.DISMISSED -> dismissed++
            }
        }
        return ReportStats(open, inProgress, resolved, dismissed, reports.size.toLong())
    }

    override fun close() {
        reports.clear()
    }
}

class ReportServiceTest {
    private lateinit var repository: InMemoryReportRepository
    private lateinit var service: ReportService

    @BeforeEach
    fun setUp() {
        repository = InMemoryReportRepository()
        service = ReportService(
            repository = repository,
            cooldown = Duration.ofSeconds(10),
            duplicateWindow = Duration.ofMinutes(5),
            maxReportsPerHour = 5,
            maxReportsPerTarget = 2
        )
    }

    @Test
    fun `test creating a report succeeds`() {
        val reporterId = UUID.randomUUID()
        val req = CreateReport(reporterId, "Alice", UUID.randomUUID(), "Bob", "Fly hacking in arena", "lobby")
        val result = service.create(req)
        assertTrue(result.isSuccess)
        val report = result.getOrThrow()
        assertEquals("Bob", report.targetName)
        assertEquals(ReportStatus.OPEN, report.status)
        assertEquals(1L, report.id)
    }

    @Test
    fun `test cooldown prevents immediate subsequent report`() {
        val reporterId = UUID.randomUUID()
        val req1 = CreateReport(reporterId, "Alice", UUID.randomUUID(), "Bob", "Speed hacking", "lobby")
        val req2 = CreateReport(reporterId, "Alice", UUID.randomUUID(), "Charlie", "Killaura", "lobby")
        assertTrue(service.create(req1).isSuccess)
        val result2 = service.create(req2)
        assertTrue(result2.isFailure)
        assertEquals("cooldown", (result2.exceptionOrNull() as? ReportException)?.message)
    }

    @Test
    fun `test invalid reason length is rejected`() {
        val reporterId = UUID.randomUUID()
        val tooShort = CreateReport(reporterId, "Alice", null, "Bob", "no", "lobby")
        val result = service.create(tooShort)
        assertTrue(result.isFailure)
        assertEquals("reason", (result.exceptionOrNull() as? ReportException)?.message)
    }

    @Test
    fun `test resolve and dismiss status updates`() {
        val reporterId = UUID.randomUUID()
        val req = CreateReport(reporterId, "Alice", null, "Bob", "Griefing builds", "survival")
        val report = service.create(req).getOrThrow()

        val resolved = service.resolve(report.id, "AdminUser")
        assertNotNull(resolved)
        assertEquals(ReportStatus.RESOLVED, resolved?.status)
        assertEquals("AdminUser", resolved?.handledBy)

        val stats = service.stats()
        assertEquals(1L, stats.resolved)
        assertEquals(0L, stats.open)
    }
}
