package dev.sqrilizz.reports.server

import com.google.gson.JsonParser
import dev.sqrilizz.reports.core.CreateReport
import dev.sqrilizz.reports.core.ReportStatus
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.UUID

object V9Migrator {
    fun migrate(plugin: ReportsPlugin): Int {
        if (plugin.config.getBoolean("migration.v9-completed", false)) return 0
        val path = plugin.config.getString("migration.v9-json-file", "")!!.trim()
        if (path.isBlank()) return 0
        val file = File(path)
        require(file.isFile) { "migration.v9-json-file does not point to a file" }
        val root = file.reader(StandardCharsets.UTF_8).use(JsonParser::parseReader).asJsonObject
        var migrated = 0
        root.entrySet().forEach { (_, reports) ->
            reports.asJsonArray.forEach { entry ->
                val report = entry.asJsonObject
                val reporter = report.get("reporter")?.asString?.ifBlank { "Unknown" } ?: "Unknown"
                val target = report.get("target")?.asString?.ifBlank { "Unknown" } ?: "Unknown"
                val created = plugin.repository.create(
                    CreateReport(
                        UUID.nameUUIDFromBytes("legacy:$reporter".toByteArray(StandardCharsets.UTF_8)),
                        reporter,
                        null,
                        target,
                        report.get("reason")?.asString ?: "Migrated report",
                        "legacy"
                    )
                )
                val status = report.get("status")?.asString?.uppercase()?.let { runCatching { ReportStatus.valueOf(it) }.getOrNull() }
                if (status != null && status != ReportStatus.OPEN && status != ReportStatus.IN_PROGRESS) {
                    plugin.repository.updateStatus(created.id, status, report.get("resolvedBy")?.asString ?: "Legacy")
                }
                migrated++
            }
        }
        plugin.config.set("migration.v9-completed", true)
        plugin.saveConfig()
        return migrated
    }
}
