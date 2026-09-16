package dev.sqrilizz.reports.server

import dev.sqrilizz.reports.core.ReportRepository
import java.io.File

object ReportRepositoryFactory {
    fun create(plugin: ReportsPlugin): ReportRepository = when (plugin.config.getString("storage.type", "sqlite")!!.lowercase()) {
        "sqlite" -> SqliteReportRepository(File(plugin.dataFolder, plugin.config.getString("storage.sqlite-file", "reports.db")!!))
        "mysql", "mariadb" -> MySqlReportRepository(
            host = plugin.config.getString("storage.mysql.host", "127.0.0.1")!!,
            port = plugin.config.getInt("storage.mysql.port", 3306),
            database = plugin.config.getString("storage.mysql.database", "sqrilizz_reports")!!,
            username = plugin.config.getString("storage.mysql.username", "root")!!,
            password = plugin.config.getString("storage.mysql.password", "")!!,
            parameters = plugin.config.getString("storage.mysql.parameters", "")!!,
            maxPoolSize = plugin.config.getInt("storage.hikari.maximum-pool-size", 10),
            minIdle = plugin.config.getInt("storage.hikari.minimum-idle", 2)
        )
        else -> error("Unsupported storage.type. Use sqlite, mysql, or mariadb.")
    }
}
