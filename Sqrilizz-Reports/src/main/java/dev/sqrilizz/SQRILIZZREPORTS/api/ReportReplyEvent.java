package dev.sqrilizz.SQRILIZZREPORTS.api;

import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReportReplyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final ReportManager.Reply reply;

    public ReportReplyEvent(ReportManager.Reply reply) {
        this.reply = reply;
    }

    public ReportManager.Reply getReply() { return reply; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
