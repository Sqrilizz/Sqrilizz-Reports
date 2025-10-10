package dev.sqrilizz.SQRILIZZREPORTS.api;

import org.bukkit.entity.Player;

/**
 * Событие создания жалобы
 */
public class ReportEvent {
    private final Player reporter;
    private final Player target;
    private final String reason;
    private final long timestamp;
    private final String systemName;
    private final boolean isSystemReport;
    
    /**
     * Конструктор для обычных жалоб от игроков
     */
    public ReportEvent(Player reporter, Player target, String reason, long timestamp) {
        this.reporter = reporter;
        this.target = target;
        this.reason = reason;
        this.timestamp = timestamp;
        this.systemName = null;
        this.isSystemReport = false;
    }
    
    /**
     * Конструктор для системных жалоб
     */
    public ReportEvent(Player reporter, Player target, String reason, long timestamp, String systemName) {
        this.reporter = reporter;
        this.target = target;
        this.reason = reason;
        this.timestamp = timestamp;
        this.systemName = systemName;
        this.isSystemReport = true;
    }
    
    /**
     * Получает игрока, отправившего жалобу
     * Может быть null для системных жалоб
     */
    public Player getReporter() {
        return reporter;
    }
    
    /**
     * Получает игрока, на которого пожаловались
     */
    public Player getTarget() {
        return target;
    }
    
    /**
     * Получает причину жалобы
     */
    public String getReason() {
        return reason;
    }
    
    /**
     * Получает время создания жалобы в миллисекундах
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Получает название системы для системных жалоб
     * Возвращает null для обычных жалоб от игроков
     */
    public String getSystemName() {
        return systemName;
    }
    
    /**
     * Проверяет, является ли жалоба системной
     */
    public boolean isSystemReport() {
        return isSystemReport;
    }
    
    /**
     * Получает имя репортера (игрока или системы)
     */
    public String getReporterName() {
        if (isSystemReport && systemName != null) {
            return "SYSTEM_" + systemName;
        }
        
        if (reporter != null) {
            return reporter.getName();
        }
        
        return "Unknown";
    }
    
    /**
     * Получает имя цели
     */
    public String getTargetName() {
        return target != null ? target.getName() : "Unknown";
    }
}
