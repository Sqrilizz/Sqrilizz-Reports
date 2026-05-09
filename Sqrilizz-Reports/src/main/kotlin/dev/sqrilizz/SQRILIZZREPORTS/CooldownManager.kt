package dev.sqrilizz.SQRILIZZREPORTS

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Менеджер кулдаунов для предотвращения спама репортов
 */
object CooldownManager {
    
    private val cooldowns = ConcurrentHashMap<UUID, Long>()
    private var cooldownTime: Long = 60 // секунды
    
    /**
     * Инициализация менеджера кулдаунов
     */
    @JvmStatic
    fun initialize() {
        val config = Main.getInstance().config
        cooldownTime = config.getLong("cooldown-time", 60)
        
        Main.getInstance().logger.info("CooldownManager initialized with cooldown: ${cooldownTime}s")
    }
    
    /**
     * Проверяет, есть ли у игрока активный кулдаун
     */
    @JvmStatic
    fun hasCooldown(uuid: UUID): Boolean {
        val cooldownEnd = cooldowns[uuid] ?: return false
        val currentTime = System.currentTimeMillis()
        
        if (currentTime >= cooldownEnd) {
            cooldowns.remove(uuid)
            return false
        }
        
        return true
    }
    
    /**
     * Получает оставшееся время кулдаuna в секундах
     */
    @JvmStatic
    fun getRemainingTime(uuid: UUID): Long {
        val cooldownEnd = cooldowns[uuid] ?: return 0
        val currentTime = System.currentTimeMillis()
        val remaining = (cooldownEnd - currentTime) / 1000
        
        return if (remaining > 0) remaining else 0
    }
    
    /**
     * Устанавливает кулдаун для игрока
     */
    @JvmStatic
    fun setCooldown(uuid: UUID) {
        val cooldownEnd = System.currentTimeMillis() + (cooldownTime * 1000)
        cooldowns[uuid] = cooldownEnd
    }
    
    /**
     * Удаляет кулдаун игрока (для админских команд)
     */
    @JvmStatic
    fun removeCooldown(uuid: UUID) {
        cooldowns.remove(uuid)
    }
    
    /**
     * Очищает все кулдауны
     */
    @JvmStatic
    fun clearAll() {
        cooldowns.clear()
    }
    
    /**
     * Получает количество активных кулдаунов
     */
    @JvmStatic
    fun getActiveCooldownsCount(): Int {
        // Очищаем истекшие кулдауны
        val currentTime = System.currentTimeMillis()
        cooldowns.entries.removeIf { it.value <= currentTime }
        
        return cooldowns.size
    }
    
    /**
     * Устанавливает время кулдауна (для динамической настройки)
     */
    @JvmStatic
    fun setCooldownTime(seconds: Long) {
        cooldownTime = seconds
    }
    
    /**
     * Получает текущее время кулдауна
     */
    @JvmStatic
    fun getCooldownTime(): Long = cooldownTime
}
