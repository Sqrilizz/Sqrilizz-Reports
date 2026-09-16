package dev.sqrilizz.reports.server.events

import dev.sqrilizz.reports.core.Report
import dev.sqrilizz.reports.core.ReportStatus
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ReportCreateEvent(val report: Report) : Event(), Cancellable {
    private var isCancelled = false

    override fun isCancelled(): Boolean = isCancelled

    override fun setCancelled(cancel: Boolean) {
        this.isCancelled = cancel
    }

    override fun getHandlers(): HandlerList = HANDLERS

    companion object {
        @JvmStatic
        val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}

class ReportStatusChangeEvent(
    val report: Report,
    val oldStatus: ReportStatus,
    val newStatus: ReportStatus,
    val moderator: String
) : Event() {
    override fun getHandlers(): HandlerList = HANDLERS

    companion object {
        @JvmStatic
        val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}
