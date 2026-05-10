package dev.sqrilizz.reports

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class CooldownManager {

    private val cooldowns = ConcurrentHashMap<UUID, Long>()

    fun setCooldown(uuid: UUID) {
        cooldowns[uuid] = System.currentTimeMillis()
    }

    fun removeCooldown(uuid: UUID) {
        cooldowns.remove(uuid)
    }

    fun getRemainingSeconds(uuid: UUID, cooldownSeconds: Int): Int {
        val lastUsed = cooldowns[uuid] ?: return 0
        val elapsed = (System.currentTimeMillis() - lastUsed) / 1000
        val remaining = cooldownSeconds - elapsed
        return if (remaining > 0) remaining.toInt() else 0
    }
}
