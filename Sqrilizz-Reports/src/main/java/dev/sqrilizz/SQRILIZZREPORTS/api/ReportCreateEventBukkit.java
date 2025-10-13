package dev.sqrilizz.SQRILIZZREPORTS.api;

import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReportCreateEventBukkit extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final ReportManager.Report report;

    public ReportCreateEventBukkit(ReportManager.Report report) {
        this.report = report;
    }

    public ReportManager.Report getReport() { return report; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
