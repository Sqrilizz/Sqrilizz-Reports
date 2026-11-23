package dev.sqrilizz.reports.server

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import dev.sqrilizz.reports.core.Report
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

class RestApi(private val plugin: ReportsPlugin) : AutoCloseable {
    private var server: HttpServer? = null

    fun start() {
        if (!plugin.config.getBoolean("rest.enabled", false)) return
        val token = plugin.config.getString("rest.token", "")!!.trim()
        require(token.length >= 24) { "rest.token must have at least 24 characters when REST is enabled" }
        server = HttpServer.create(InetSocketAddress(plugin.config.getInt("rest.port", 8971)), 0).apply {
            executor = Executors.newFixedThreadPool(2) { task -> Thread(task, "Sqrilizz-Reports-REST").apply { isDaemon = true } }
            createContext("/v1/reports") { exchange ->
                if (!authorized(exchange, token)) return@createContext
                when {
                    exchange.requestMethod == "GET" -> exchange.respond(200, "[${plugin.reports.open(45).joinToString(",") { it.json() }}]")
                    else -> exchange.respond(405, "{\"error\":\"method_not_allowed\"}")
                }
            }
            createContext("/v1/stats") { exchange ->
                if (!authorized(exchange, token)) return@createContext
                if (exchange.requestMethod != "GET") return@createContext exchange.respond(405, "{\"error\":\"method_not_allowed\"}")
                exchange.respond(200, "{\"open\":${plugin.reports.open(45).size},\"server\":\"${plugin.serverId().json()}\"}")
            }
            createContext("/v1/reports/resolve") { exchange ->
                if (!authorized(exchange, token)) return@createContext
                if (exchange.requestMethod != "POST") return@createContext exchange.respond(405, "{\"error\":\"method_not_allowed\"}")
                val id = exchange.requestURI.query?.substringAfter("id=", "")?.toLongOrNull()
                if (id == null) return@createContext exchange.respond(400, "{\"error\":\"invalid_id\"}")
                val report = plugin.reports.resolve(id, "REST")
                exchange.respond(if (report == null) 404 else 200, if (report == null) "{\"error\":\"not_found\"}" else report.json())
            }
            start()
        }
        plugin.logger.info("Sqrilizz-Reports REST API listening on ${plugin.config.getInt("rest.port", 8971)}")
    }

    override fun close() {
        server?.stop(1)
    }

    private fun authorized(exchange: HttpExchange, token: String): Boolean {
        if (exchange.requestHeaders.getFirst("Authorization") == "Bearer $token") return true
        exchange.respond(401, "{\"error\":\"unauthorized\"}")
        return false
    }

    private fun HttpExchange.respond(status: Int, payload: String) {
        val bytes = payload.toByteArray(StandardCharsets.UTF_8)
        responseHeaders.set("Content-Type", "application/json; charset=utf-8")
        sendResponseHeaders(status, bytes.size.toLong())
        responseBody.use { it.write(bytes) }
    }

    private fun Report.json(): String = "{\"id\":$id,\"target\":\"${targetName.json()}\",\"reporter\":\"${reporterName.json()}\",\"reason\":\"${reason.json()}\",\"server\":\"${sourceServer.json()}\",\"status\":\"${status.name}\"}"
    private fun String.json(): String = replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
}
