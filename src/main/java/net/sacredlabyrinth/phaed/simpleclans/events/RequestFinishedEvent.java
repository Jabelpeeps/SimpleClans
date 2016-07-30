package net.sacredlabyrinth.phaed.simpleclans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.sacredlabyrinth.phaed.simpleclans.Request;

/**
 *
 * @author NeT32
 */
public class RequestFinishedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Request RequestProcess;

    public RequestFinishedEvent(Request _RequestProcess) {
        RequestProcess = _RequestProcess;
    }

    public Request getRequest() {
        return RequestProcess;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
