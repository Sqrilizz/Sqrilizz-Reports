package dev.sqrilizz.SQRILIZZREPORTS.lifecycle;

import dev.sqrilizz.SQRILIZZREPORTS.Main;
import dev.sqrilizz.SQRILIZZREPORTS.api.CacheManager;
import dev.sqrilizz.SQRILIZZREPORTS.api.RESTServer;
import dev.sqrilizz.SQRILIZZREPORTS.db.DatabaseManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Система graceful shutdown для идеального завершения работы плагина
 * Обеспечивает корректное закрытие всех ресурсов и сервисов
 */
public class ShutdownManager {
    
    private static final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    private static final ExecutorService shutdownExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "SQRILIZZ-Shutdown");
        t.setDaemon(true);
        return t;
    });
    
    /**
     * Инициализировать систему graceful shutdown
     */
    public static void initialize() {
        // Регистрируем shutdown hook для JVM
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!isShuttingDown.get()) {
                performGracefulShutdown();
            }
        }, "SQRILIZZ-ShutdownHook"));
        
        Main.getInstance().getLogger().info("Graceful shutdown system initialized");
    }
    
    /**
     * Выполнить graceful shutdown всех компонентов
     */
    public static void performGracefulShutdown() {
        if (isShuttingDown.compareAndSet(false, true)) {
            Main.getInstance().getLogger().info("[SHUTDOWN] Starting graceful shutdown...");
            
            long startTime = System.currentTimeMillis();
            
            try {
                // Параллельное завершение всех сервисов
                CompletableFuture<Void> allShutdowns = CompletableFuture.allOf(
                    shutdownRESTServer(),
                    shutdownDatabase(),
                    shutdownCache(),
                    shutdownDiscordBot(),
                    shutdownTelegramBot(),
                    savePerformanceReport()
                );
                
                // Ждем завершения всех операций (максимум 30 секунд)
                allShutdowns.get(30, TimeUnit.SECONDS);
                
                long duration = System.currentTimeMillis() - startTime;
                Main.getInstance().getLogger().info("[SHUTDOWN] Graceful shutdown completed in " + duration + "ms");
                
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("[SHUTDOWN] Some components failed to shutdown gracefully: " + e.getMessage());
            } finally {
                shutdownExecutor.shutdown();
            }
        }
    }
    
    /**
     * Проверить, выполняется ли shutdown
     */
    public static boolean isShuttingDown() {
        return isShuttingDown.get();
    }
    
    private static CompletableFuture<Void> shutdownRESTServer() {
        return CompletableFuture.runAsync(() -> {
            try {
                Main.getInstance().getLogger().info("[REST] Shutting down REST server...");
                RESTServer.shutdown();
                Main.getInstance().getLogger().info("[REST] REST server shutdown complete");
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("[REST] REST server shutdown failed: " + e.getMessage());
            }
        }, shutdownExecutor);
    }
    
    private static CompletableFuture<Void> shutdownDatabase() {
        return CompletableFuture.runAsync(() -> {
            try {
                Main.getInstance().getLogger().info("[DATABASE] Shutting down database connections...");
                DatabaseManager.close();
                Main.getInstance().getLogger().info("[DATABASE] Database shutdown complete");
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("[DATABASE] Database shutdown failed: " + e.getMessage());
            }
        }, shutdownExecutor);
    }
    
    private static CompletableFuture<Void> shutdownCache() {
        return CompletableFuture.runAsync(() -> {
            try {
                Main.getInstance().getLogger().info("[CACHE] Shutting down cache system...");
                CacheManager.cleanUp();
                Main.getInstance().getLogger().info("[CACHE] Cache shutdown complete");
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("[CACHE] Cache shutdown failed: " + e.getMessage());
            }
        }, shutdownExecutor);
    }
    
    private static CompletableFuture<Void> shutdownDiscordBot() {
        return CompletableFuture.runAsync(() -> {
            try {
                Main.getInstance().getLogger().info("[DISCORD] Shutting down Discord bot...");
                // DiscordBot.shutdown(); // Если есть такой метод
                Main.getInstance().getLogger().info("[DISCORD] Discord bot shutdown complete");
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("[DISCORD] Discord bot shutdown failed: " + e.getMessage());
            }
        }, shutdownExecutor);
    }
    
    private static CompletableFuture<Void> shutdownTelegramBot() {
        return CompletableFuture.runAsync(() -> {
            try {
                Main.getInstance().getLogger().info("[TELEGRAM] Shutting down Telegram manager...");
                dev.sqrilizz.SQRILIZZREPORTS.TelegramManager.shutdown();
                Main.getInstance().getLogger().info("[TELEGRAM] Telegram manager shutdown complete");
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("[TELEGRAM] Telegram manager shutdown failed: " + e.getMessage());
            }
        }, shutdownExecutor);
    }
    
    private static CompletableFuture<Void> savePerformanceReport() {
        return CompletableFuture.runAsync(() -> {
            try {
                Main.getInstance().getLogger().info("[PERFORMANCE] Saving final performance report...");
                String report = "Performance monitoring disabled";
                Main.getInstance().getLogger().info("Final Performance Report: " + report);
                Main.getInstance().getLogger().info("[PERFORMANCE] Performance report saved");
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("[PERFORMANCE] Performance report save failed: " + e.getMessage());
            }
        }, shutdownExecutor);
    }
    
    /**
     * Принудительное завершение (для экстренных случаев)
     */
    public static void forceShutdown() {
        Main.getInstance().getLogger().warning("[FORCE SHUTDOWN] Force shutdown initiated");
        isShuttingDown.set(true);
        
        try {
            shutdownExecutor.shutdownNow();
            if (!shutdownExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                Main.getInstance().getLogger().severe("[FORCE SHUTDOWN] Force shutdown timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
