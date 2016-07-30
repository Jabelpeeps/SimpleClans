package net.sacredlabyrinth.phaed.simpleclans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.sacredlabyrinth.phaed.simpleclans.Clan;

/**
 *
 * @author NeT32
 */
public class RivalClanAddEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Clan clanFirst;
    private final Clan clanSecond;

    public RivalClanAddEvent(Clan _clanFirst, Clan _clanSecond) {
        clanFirst = _clanFirst;
        clanSecond = _clanSecond;
    }

    public Clan getClanFirst() {
        return clanFirst;
    }

    public Clan getClanSecond() {
        return clanSecond;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
