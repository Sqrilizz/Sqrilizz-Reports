package dev.sqrilizz.SQRILIZZREPORTS.api;

import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;
import dev.sqrilizz.SQRILIZZREPORTS.AntiAbuseManager;
import dev.sqrilizz.SQRILIZZREPORTS.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Публичный API для плагина Sqrilizz-Reports
 * Позволяет сторонним плагинам интегрироваться с системой жалоб
 */
public class ReportAPI {
    
    private static final List<Consumer<ReportEvent>> reportListeners = new ArrayList<>();
    private static final List<Consumer<ReportManager.Report>> resolveListeners = new ArrayList<>();
    private static final List<Consumer<ReportManager.Report>> deleteListeners = new ArrayList<>();
    private static final List<Consumer<ReportManager.Reply>> replyListeners = new ArrayList<>();
    
    /**
     * Создает новую жалобу
     * 
     * @param reporter Игрок, отправляющий жалобу
     * @param target Игрок, на которого жалуются
     * @param reason Причина жалобы
     * @return true если жалоба была успешно создана, false если была заблокирована системой защиты
     */
    public static boolean createReport(Player reporter, Player target, String reason) {
        if (reporter == null || target == null || reason == null || reason.trim().isEmpty()) {
            return false;
        }
        
        // Проверяем систему защиты от злоупотреблений
        String targetName = VersionUtils.getPlayerCleanName(target);
        if (!AntiAbuseManager.canReport(reporter, targetName)) {
            return false;
        }
        
        // Создаем жалобу
        ReportManager.addReport(reporter, target, reason);
        
        // Регистрируем в системе защиты от злоупотреблений
        AntiAbuseManager.recordReport(reporter, targetName);
        
        // Уведомляем слушателей
        ReportEvent event = new ReportEvent(reporter, target, reason, System.currentTimeMillis());
        for (Consumer<ReportEvent> listener : reportListeners) {
            try {
                listener.accept(event);
            } catch (Exception e) {
                // Игнорируем ошибки в слушателях
            }
        }
        
        return true;
    }
    
    /**
     * Создает новую жалобу от имени системы (например, от античита)
     * 
     * @param systemName Название системы, отправляющей жалобу
     * @param target Игрок, на которого жалуются
     * @param reason Причина жалобы
     * @return true если жалоба была успешно создана
     */
    public static boolean createSystemReport(String systemName, Player target, String reason) {
        if (systemName == null || target == null || reason == null || reason.trim().isEmpty()) {
            return false;
        }
        
        // Создаем фиктивного игрока-репортера для системных жалоб
        // Системные жалобы не проверяются системой защиты от злоупотреблений
        Player systemReporter = Bukkit.getPlayer("SYSTEM_" + systemName);
        if (systemReporter == null) {
            // Если системного игрока нет, создаем жалобу напрямую через ReportManager
            String targetName = VersionUtils.getPlayerCleanName(target);
            ReportManager.Report report = new ReportManager.Report(
                "SYSTEM_" + systemName,
                targetName,
                reason,
                System.currentTimeMillis(),
                "System",
                getPlayerLocation(target),
                false
            );
            
            ReportManager.getReports().computeIfAbsent(targetName, k -> new ArrayList<>()).add(report);
            ReportManager.saveReports();
            
            // Уведомляем слушателей
            ReportEvent event = new ReportEvent(null, target, reason, System.currentTimeMillis(), systemName);
            for (Consumer<ReportEvent> listener : reportListeners) {
                try {
                    listener.accept(event);
                } catch (Exception e) {
                    // Игнорируем ошибки в слушателях
                }
            }
            
            return true;
        }
        
        return createReport(systemReporter, target, reason);
    }
    
    /**
     * Получает список жалоб на игрока
     * 
     * @param target Игрок, жалобы на которого нужно получить
     * @return Список жалоб
     */
    public static List<ReportManager.Report> getReports(Player target) {
        if (target == null) {
            return new ArrayList<>();
        }
        
        String targetName = VersionUtils.getPlayerCleanName(target);
        return ReportManager.getPlayerReports(targetName);
    }
    
    /**
     * Получает список жалоб на игрока по имени
     * 
     * @param targetName Имя игрока
     * @return Список жалоб
     */
    public static List<ReportManager.Report> getReports(String targetName) {
        if (targetName == null || targetName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return ReportManager.getPlayerReports(targetName);
    }
    
    /**
     * Очищает все жалобы на игрока
     * 
     * @param target Игрок, жалобы на которого нужно очистить
     */
    public static void clearReports(Player target) {
        if (target == null) {
            return;
        }
        
        String targetName = VersionUtils.getPlayerCleanName(target);
        ReportManager.clearReports(targetName);
    }
    
    /**
     * Очищает все жалобы на игрока по имени
     * 
     * @param targetName Имя игрока
     */
    public static void clearReports(String targetName) {
        if (targetName == null || targetName.trim().isEmpty()) {
            return;
        }
        
        ReportManager.clearReports(targetName);
    }
    
    /**
     * Получает количество жалоб на игрока
     * 
     * @param target Игрок
     * @return Количество жалоб
     */
    public static int getReportCount(Player target) {
        if (target == null) {
            return 0;
        }
        
        String targetName = VersionUtils.getPlayerCleanName(target);
        return ReportManager.getReportCount(targetName);
    }
    
    /**
     * Получает количество жалоб на игрока по имени
     * 
     * @param targetName Имя игрока
     * @return Количество жалоб
     */
    public static int getReportCount(String targetName) {
        if (targetName == null || targetName.trim().isEmpty()) {
            return 0;
        }
        
        return ReportManager.getReportCount(targetName);
    }
    
    /**
     * Регистрирует слушатель событий жалоб
     * 
     * @param listener Слушатель
     */
    public static void onReportCreate(Consumer<ReportEvent> listener) {
        if (listener != null) {
            reportListeners.add(listener);
        }
    }

    /**
     * Регистрирует слушатель события закрытия жалобы
     */
    public static void onReportResolve(Consumer<ReportManager.Report> listener) {
        if (listener != null) resolveListeners.add(listener);
    }

    /**
     * Регистрирует слушатель события ответа на жалобу
     */
    public static void onReportReply(Consumer<ReportManager.Reply> listener) {
        if (listener != null) replyListeners.add(listener);
    }

    /**
     * Регистрирует слушатель события удаления жалобы
     */
    public static void onReportDelete(Consumer<ReportManager.Report> listener) {
        if (listener != null) deleteListeners.add(listener);
    }

    // Internal notify methods, used by ReportManager
    public static void notifyResolved(ReportManager.Report report) {
        for (Consumer<ReportManager.Report> c : resolveListeners) {
            try { c.accept(report); } catch (Exception ignored) {}
        }
    }

    public static void notifyReplied(ReportManager.Reply reply) {
        for (Consumer<ReportManager.Reply> c : replyListeners) {
            try { c.accept(reply); } catch (Exception ignored) {}
        }
    }

    public static void notifyDeleted(ReportManager.Report report) {
        for (Consumer<ReportManager.Report> c : deleteListeners) {
            try { c.accept(report); } catch (Exception ignored) {}
        }
    }
    
    /**
     * Отмечает жалобу как ложную
     * 
     * @param reporterName Имя игрока, отправившего ложную жалобу
     */
    public static void markFalseReport(String reporterName) {
        if (reporterName == null || reporterName.trim().isEmpty()) {
            return;
        }
        
        AntiAbuseManager.markFalseReport(reporterName);
    }
    
    /**
     * Проверяет, может ли игрок отправить жалобу
     * 
     * @param reporter Игрок-репортер
     * @param targetName Имя цели
     * @return true если может отправить жалобу
     */
    public static boolean canReport(Player reporter, String targetName) {
        if (reporter == null || targetName == null || targetName.trim().isEmpty()) {
            return false;
        }
        
        return AntiAbuseManager.canReport(reporter, targetName);
    }
    
    /**
     * Проверяет, имеет ли игрок пониженный приоритет из-за ложных жалоб
     * 
     * @param reporterName Имя игрока
     * @return true если имеет пониженный приоритет
     */
    public static boolean hasLowPriority(String reporterName) {
        if (reporterName == null || reporterName.trim().isEmpty()) {
            return false;
        }
        
        return AntiAbuseManager.hasLowPriority(reporterName);
    }
    
    /**
     * Получает координаты игрока в виде строки
     */
    private static String getPlayerLocation(Player player) {
        if (player == null || !player.isOnline()) {
            return "Unknown";
        }
        
        String world = player.getWorld().getName();
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        
        return String.format("%s: %d, %d, %d", world, x, y, z);
    }
}
