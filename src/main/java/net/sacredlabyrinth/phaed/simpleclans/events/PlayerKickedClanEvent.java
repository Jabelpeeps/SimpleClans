package net.sacredlabyrinth.phaed.simpleclans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;

/**
 *
 * @author NeT32
 */
public class PlayerKickedClanEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Clan clan;
    private final ClanPlayer target;

    public PlayerKickedClanEvent(Clan _clan, ClanPlayer _target) {
        clan = _clan;
        target = _target;
    }

    public Clan getClan() {
        return clan;
    }

    public ClanPlayer getClanPlayer() {
        return target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
