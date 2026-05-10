package dev.sqrilizz.SQRILIZZREPORTS.lifecycle

import dev.sqrilizz.SQRILIZZREPORTS.Main
import dev.sqrilizz.SQRILIZZREPORTS.TelegramManager
import dev.sqrilizz.SQRILIZZREPORTS.api.CacheManager
import dev.sqrilizz.SQRILIZZREPORTS.api.RESTServer
import dev.sqrilizz.SQRILIZZREPORTS.db.DatabaseManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

object ShutdownManager {

    private val isShuttingDown = AtomicBoolean(false)
    private val shutdownExecutor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "SQRILIZZ-Shutdown").apply { isDaemon = true }
    }

    @JvmStatic
    fun initialize() {
        Runtime.getRuntime().addShutdownHook(Thread({
            if (!isShuttingDown.get()) {
                performGracefulShutdown()
            }
        }, "SQRILIZZ-ShutdownHook"))

        Main.getInstance().logger.info("Graceful shutdown system initialized")
    }

    @JvmStatic
    fun performGracefulShutdown() {
        if (isShuttingDown.compareAndSet(false, true)) {
            Main.getInstance().logger.info("\uD83D\uDD04 Starting graceful shutdown...")

            val startTime = System.currentTimeMillis()

            try {
                val allShutdowns = CompletableFuture.allOf(
                    shutdownComponent("REST server") { RESTServer.shutdown() },
                    shutdownComponent("database connections") { DatabaseManager.close() },
                    shutdownComponent("cache system") { CacheManager.cleanUp() },
                    shutdownComponent("Discord bot") { /* placeholder */ },
                    shutdownComponent("Telegram manager") { TelegramManager.shutdown() },
                    shutdownComponent("performance report") {
                        Main.getInstance().logger.info("Final Performance Report:\nPerformance monitoring disabled")
                    }
                )

                allShutdowns.get(30, TimeUnit.SECONDS)

                val duration = System.currentTimeMillis() - startTime
                Main.getInstance().logger.info("\u2705 Graceful shutdown completed in ${duration}ms")
            } catch (e: Exception) {
                Main.getInstance().logger.warning("\u26a0\ufe0f Some components failed to shutdown gracefully: ${e.message}")
            } finally {
                shutdownExecutor.shutdown()
            }
        }
    }

    @JvmStatic
    fun isShuttingDown(): Boolean = isShuttingDown.get()

    @JvmStatic
    fun forceShutdown() {
        Main.getInstance().logger.warning("\uD83D\uDEA8 FORCE SHUTDOWN INITIATED")
        isShuttingDown.set(true)

        try {
            shutdownExecutor.shutdownNow()
            if (!shutdownExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                Main.getInstance().logger.severe("\u274c Force shutdown timeout")
            }
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    private fun shutdownComponent(name: String, action: () -> Unit): CompletableFuture<Void> =
        CompletableFuture.runAsync({
            try {
                Main.getInstance().logger.info("Shutting down $name...")
                action()
                Main.getInstance().logger.info("$name shutdown complete")
            } catch (e: Exception) {
                Main.getInstance().logger.warning("$name shutdown failed: ${e.message}")
            }
        }, shutdownExecutor)
}
