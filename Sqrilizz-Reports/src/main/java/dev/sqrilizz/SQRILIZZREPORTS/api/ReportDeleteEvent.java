package dev.sqrilizz.SQRILIZZREPORTS.api;

import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReportDeleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final ReportManager.Report report;
    private final String deleter;

    public ReportDeleteEvent(ReportManager.Report report, String deleter) {
        this.report = report;
        this.deleter = deleter;
    }

    public ReportManager.Report getReport() { return report; }
    public String getDeleter() { return deleter; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
