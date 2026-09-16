package dev.sqrilizz.reports.server

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object SchedulerHelper {
    private var platform: Platform = Platform.SPIGOT
    private val fallbackExecutor: Executor = Executors.newFixedThreadPool(2) { task ->
        Thread(task, "Sqrilizz-Reports-Async").apply { isDaemon = true }
    }

    enum class Platform {
        PAPER,
        FOLIA,
        SPIGOT
    }

    fun init() {
        platform = detectPlatform()
    }

    fun getPlatform(): Platform = platform

    fun runAsync(plugin: Plugin, task: () -> Unit) {
        when (platform) {
            Platform.PAPER, Platform.FOLIA -> {
                try {
                    Bukkit.getAsyncScheduler().runNow(plugin) { task() }
                } catch (_: Exception) {
                    fallbackExecutor.execute(task)
                }
            }
            Platform.SPIGOT -> {
                try {
                    @Suppress("DEPRECATION")
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
                } catch (_: Exception) {
                    fallbackExecutor.execute(task)
                }
            }
        }
    }

    fun runOnPlayer(plugin: Plugin, player: Player, task: () -> Unit) {
        when (platform) {
            Platform.FOLIA -> {
                try {
                    val scheduler = player.javaClass.getMethod("getScheduler").invoke(player)
                    scheduler.javaClass.getMethod(
                        "execute",
                        Plugin::class.java,
                        Runnable::class.java,
                        Runnable::class.java,
                        Long::class.javaPrimitiveType
                    ).invoke(scheduler, plugin, Runnable(task), null, 1L)
                } catch (_: Exception) {
                    runOnMain(plugin, task)
                }
            }
            Platform.PAPER, Platform.SPIGOT -> {
                runOnMain(plugin, task)
            }
        }
    }

    fun runOnMain(plugin: Plugin, task: () -> Unit) {
        if (Bukkit.isPrimaryThread()) {
            task()
        } else {
            @Suppress("DEPRECATION")
            Bukkit.getScheduler().runTask(plugin, Runnable(task))
        }
    }

    private fun detectPlatform(): Platform {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
            return Platform.FOLIA
        } catch (_: ClassNotFoundException) {}

        try {
            Class.forName("io.papermc.paper.plugin.loader.PaperClasspathBuilder")
            return Platform.PAPER
        } catch (_: ClassNotFoundException) {}

        try {
            Bukkit::class.java.getMethod("getAsyncScheduler")
            return Platform.PAPER
        } catch (_: NoSuchMethodException) {}

        return Platform.SPIGOT
    }
}
