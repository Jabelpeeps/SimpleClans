package net.sacredlabyrinth.phaed.simpleclans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.sacredlabyrinth.phaed.simpleclans.Clan;

/**
 *
 * @author NeT32
 */
public class DisbandClanEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Clan clan;

    public DisbandClanEvent(Clan _clan) {
        clan = _clan;
    }

    public Clan getClan() {
        return clan;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
