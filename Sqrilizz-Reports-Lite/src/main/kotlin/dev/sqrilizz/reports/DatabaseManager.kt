package dev.sqrilizz.reports

import java.io.File
import java.sql.*
import java.util.UUID

class DatabaseManager(private val plugin: Main) {

    private var connection: Connection? = null
    private lateinit var dbUrl: String

    fun initialize() {
        val dbFile = File(plugin.dataFolder, "reports.db")
        dbUrl = "jdbc:sqlite:${dbFile.absolutePath}"

        connection = DriverManager.getConnection(dbUrl)
        createTable()

        plugin.logger.info("Database initialized successfully")
    }

    private fun getConnection(): Connection {
        val conn = connection
        if (conn == null || conn.isClosed) {
            connection = DriverManager.getConnection(dbUrl)
        }
        return connection!!
    }

    private fun createTable() {
        val sql = """
            CREATE TABLE IF NOT EXISTS reports (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp BIGINT NOT NULL,
                reporter_name TEXT NOT NULL,
                reporter_uuid TEXT NOT NULL,
                target_name TEXT NOT NULL,
                reason TEXT NOT NULL,
                location TEXT NOT NULL,
                resolved BOOLEAN DEFAULT FALSE,
                resolver TEXT,
                resolved_at BIGINT
            )
        """.trimIndent()

        getConnection().createStatement().use { it.execute(sql) }
    }

    fun saveReport(report: ReportManager.Report): Long {
        val sql = """
            INSERT INTO reports (timestamp, reporter_name, reporter_uuid, target_name, reason, location)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        return try {
            getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setLong(1, report.timestamp)
                stmt.setString(2, report.reporterName)
                stmt.setString(3, report.reporterUuid.toString())
                stmt.setString(4, report.targetName)
                stmt.setString(5, report.reason)
                stmt.setString(6, report.location)

                stmt.executeUpdate()

                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getLong(1) else -1
                }
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to save report: ${e.message}")
            -1
        }
    }

    fun getReports(targetName: String): List<ReportManager.Report> {
        val sql = "SELECT * FROM reports WHERE target_name = ? ORDER BY timestamp DESC LIMIT 50"

        return try {
            getConnection().prepareStatement(sql).use { stmt ->
                stmt.setString(1, targetName)
                stmt.executeQuery().use { rs ->
                    val reports = mutableListOf<ReportManager.Report>()
                    while (rs.next()) {
                        reports.add(createReportFromResultSet(rs))
                    }
                    reports
                }
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to get reports: ${e.message}")
            emptyList()
        }
    }

    fun resolveReport(reportId: Long, resolver: String): Boolean {
        val sql = "UPDATE reports SET resolved = TRUE, resolver = ?, resolved_at = ? WHERE id = ?"

        return try {
            getConnection().prepareStatement(sql).use { stmt ->
                stmt.setString(1, resolver)
                stmt.setLong(2, System.currentTimeMillis())
                stmt.setLong(3, reportId)
                stmt.executeUpdate() > 0
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to resolve report: ${e.message}")
            false
        }
    }

    fun deleteReport(reportId: Long): Boolean {
        val sql = "DELETE FROM reports WHERE id = ?"

        return try {
            getConnection().prepareStatement(sql).use { stmt ->
                stmt.setLong(1, reportId)
                stmt.executeUpdate() > 0
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to delete report: ${e.message}")
            false
        }
    }

    private fun createReportFromResultSet(rs: ResultSet): ReportManager.Report {
        val report = ReportManager.Report(
            timestamp = rs.getLong("timestamp"),
            reporterName = rs.getString("reporter_name"),
            reporterUuid = UUID.fromString(rs.getString("reporter_uuid")),
            targetName = rs.getString("target_name"),
            reason = rs.getString("reason"),
            location = rs.getString("location")
        )
        report.id = rs.getLong("id")
        report.isResolved = rs.getBoolean("resolved")
        report.resolver = rs.getString("resolver")
        return report
    }

    fun close() {
        try {
            connection?.takeIf { !it.isClosed }?.close()
        } catch (e: SQLException) {
            plugin.logger.warning("Failed to close database: ${e.message}")
        }
    }
}
