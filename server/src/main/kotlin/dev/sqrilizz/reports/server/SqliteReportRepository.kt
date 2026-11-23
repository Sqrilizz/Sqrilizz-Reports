package dev.sqrilizz.reports.server

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.sqrilizz.reports.core.CreateReport
import dev.sqrilizz.reports.core.Report
import dev.sqrilizz.reports.core.ReportRepository
import dev.sqrilizz.reports.core.ReportStats
import dev.sqrilizz.reports.core.ReportStatus
import dev.sqrilizz.reports.core.ReportType
import java.io.File
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID

class SqliteReportRepository(file: File) : ReportRepository {
    private val dataSource: HikariDataSource

    init {
        file.parentFile?.mkdirs()
        val config = HikariConfig().apply {
            poolName = "Sqrilizz-Reports-SQLite"
            jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
            maximumPoolSize = 1
            isAutoCommit = true
            connectionTestQuery = "SELECT 1"
            addDataSourceProperty("journal_mode", "WAL")
            addDataSourceProperty("synchronous", "NORMAL")
        }
        dataSource = HikariDataSource(config)

        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS reports (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        reporter_id TEXT NOT NULL,
                        reporter_name TEXT NOT NULL,
                        target_id TEXT,
                        target_name TEXT NOT NULL,
                        reason TEXT NOT NULL,
                        source_server TEXT NOT NULL,
                        created_at INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        handled_by TEXT,
                        handled_at INTEGER,
                        report_type TEXT NOT NULL DEFAULT 'PLAYER'
                    )
                    """.trimIndent()
                )
                statement.executeUpdate("CREATE INDEX IF NOT EXISTS reports_open_index ON reports(status, created_at DESC)")
            }
        }
    }

    override fun create(report: CreateReport): Report {
        val now = Instant.now()
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                "INSERT INTO reports(reporter_id, reporter_name, target_id, target_name, reason, source_server, created_at, status, report_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            ).use { statement ->
                statement.setString(1, report.reporterId.toString())
                statement.setString(2, report.reporterName)
                statement.setString(3, report.targetId?.toString())
                statement.setString(4, report.targetName)
                statement.setString(5, report.reason)
                statement.setString(6, report.sourceServer)
                statement.setLong(7, now.toEpochMilli())
                statement.setString(8, ReportStatus.OPEN.name)
                statement.setString(9, report.type.name)
                statement.executeUpdate()
            }
            val id = connection.createStatement().use { statement ->
                statement.executeQuery("SELECT last_insert_rowid()").use { result -> result.getLong(1) }
            }
            return Report(id, report.reporterId, report.reporterName, report.targetId, report.targetName, report.reason, report.sourceServer, now, ReportStatus.OPEN, type = report.type)
        }
    }

    override fun find(id: Long): Report? = dataSource.connection.use { connection ->
        connection.prepareStatement("SELECT * FROM reports WHERE id = ?").use { statement ->
            statement.setLong(1, id)
            statement.executeQuery().use { result -> if (result.next()) result.toReport() else null }
        }
    }

    override fun listOpen(limit: Int): List<Report> = dataSource.connection.use { connection ->
        connection.prepareStatement(
            "SELECT * FROM reports WHERE status IN (?, ?) ORDER BY created_at DESC LIMIT ?"
        ).use { statement ->
            statement.setString(1, ReportStatus.OPEN.name)
            statement.setString(2, ReportStatus.IN_PROGRESS.name)
            statement.setInt(3, limit)
            statement.executeQuery().use { result -> buildList { while (result.next()) add(result.toReport()) } }
        }
    }

    override fun listOpenPaged(offset: Int, limit: Int): List<Report> = dataSource.connection.use { connection ->
        connection.prepareStatement(
            "SELECT * FROM reports WHERE status IN (?, ?) ORDER BY created_at DESC LIMIT ? OFFSET ?"
        ).use { statement ->
            statement.setString(1, ReportStatus.OPEN.name)
            statement.setString(2, ReportStatus.IN_PROGRESS.name)
            statement.setInt(3, limit)
            statement.setInt(4, maxOf(0, offset))
            statement.executeQuery().use { result -> buildList { while (result.next()) add(result.toReport()) } }
        }
    }

    override fun updateStatus(id: Long, status: ReportStatus, handledBy: String): Report? {
        val now = Instant.now()
        dataSource.connection.use { connection ->
            connection.prepareStatement("UPDATE reports SET status = ?, handled_by = ?, handled_at = ? WHERE id = ? AND status IN (?, ?)").use { statement ->
                statement.setString(1, status.name)
                statement.setString(2, handledBy)
                statement.setLong(3, now.toEpochMilli())
                statement.setLong(4, id)
                statement.setString(5, ReportStatus.OPEN.name)
                statement.setString(6, ReportStatus.IN_PROGRESS.name)
                if (statement.executeUpdate() == 0) return null
            }
        }
        return find(id)
    }

    override fun delete(id: Long): Boolean = dataSource.connection.use { connection ->
        connection.prepareStatement("DELETE FROM reports WHERE id = ?").use { statement ->
            statement.setLong(1, id)
            statement.executeUpdate() > 0
        }
    }

    override fun stats(): ReportStats = dataSource.connection.use { connection ->
        connection.createStatement().use { statement ->
            statement.executeQuery(
                """
                SELECT
                    SUM(CASE WHEN status = 'OPEN' THEN 1 ELSE 0 END) as open_cnt,
                    SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress_cnt,
                    SUM(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END) as resolved_cnt,
                    SUM(CASE WHEN status = 'DISMISSED' THEN 1 ELSE 0 END) as dismissed_cnt,
                    COUNT(*) as total_cnt
                FROM reports
                """.trimIndent()
            ).use { rs ->
                if (rs.next()) {
                    ReportStats(
                        open = rs.getLong("open_cnt"),
                        inProgress = rs.getLong("in_progress_cnt"),
                        resolved = rs.getLong("resolved_cnt"),
                        dismissed = rs.getLong("dismissed_cnt"),
                        total = rs.getLong("total_cnt")
                    )
                } else {
                    ReportStats(0, 0, 0, 0, 0)
                }
            }
        }
    }

    override fun close() {
        if (!dataSource.isClosed) {
            dataSource.close()
        }
    }

    private fun ResultSet.toReport(): Report {
        val typeStr = runCatching { getString("report_type") }.getOrNull()
        val reportType = if (typeStr != null) {
            runCatching { ReportType.valueOf(typeStr) }.getOrDefault(ReportType.PLAYER)
        } else {
            ReportType.PLAYER
        }
        return Report(
            id = getLong("id"),
            reporterId = UUID.fromString(getString("reporter_id")),
            reporterName = getString("reporter_name"),
            targetId = getString("target_id")?.let(UUID::fromString),
            targetName = getString("target_name"),
            reason = getString("reason"),
            sourceServer = getString("source_server"),
            createdAt = Instant.ofEpochMilli(getLong("created_at")),
            status = ReportStatus.valueOf(getString("status")),
            handledBy = getString("handled_by"),
            handledAt = getLong("handled_at").takeIf { !wasNull() }?.let(Instant::ofEpochMilli),
            type = reportType
        )
    }
}
