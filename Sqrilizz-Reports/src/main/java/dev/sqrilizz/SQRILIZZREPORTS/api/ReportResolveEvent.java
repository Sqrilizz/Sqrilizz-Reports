package dev.sqrilizz.SQRILIZZREPORTS.api;

import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReportResolveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final ReportManager.Report report;
    private final String resolver;

    public ReportResolveEvent(ReportManager.Report report, String resolver) {
        this.report = report;
        this.resolver = resolver;
    }

    public ReportManager.Report getReport() { return report; }
    public String getResolver() { return resolver; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
