package net.sacredlabyrinth.phaed.simpleclans.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;

/**
 *
 * @author NeT32
 */
public class PlayerHomeSetEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Clan clan;
    private final ClanPlayer cp;
    private final Location loc;

    public PlayerHomeSetEvent(Clan _clan, ClanPlayer _cp, Location _loc) {
        clan = _clan;
        cp = _cp;
        loc = _loc;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean _cancelled) {
        cancelled = _cancelled;
    }

    public Clan getClan() {
        return this.clan;
    }

    public ClanPlayer getClanPlayer() {
        return cp;
    }

    public Location getLocation() {
        return loc;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
