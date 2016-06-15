package net.sacredlabyrinth.phaed.simpleclans;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportState {
    private UUID playerUniqueId;
    private Location playerLocation;
    private Location destination;
    private int counter;
    private String clanName;
    private boolean processing;

    public TeleportState(Player player, Location dest, String _clanName) {
        destination = dest;
        playerLocation = player.getLocation();
        clanName = _clanName;
        counter = SimpleClans.getInstance().getSettingsManager().getWaitSecs();
        playerUniqueId = player.getUniqueId();
    }
    
    public Location getLocation()  {
        return playerLocation;
    }
    /**
     * Whether its time for teleport
     * @return
     */
    public boolean isTeleportTime() {
        if (counter > 1) {
            counter--;
            return false;
        }
        return true;
    }
    /**
     * The player that is waiting for teleport
     * @return
     */
    public Player getPlayer() {      
        return Bukkit.getPlayer(playerUniqueId);
    }
    /**
     * Get seconds left before teleport
     * @return
     */
    public int getCounter() {
        return counter;
    }
    public void setCounter(int _counter) {
        counter = _counter;
    }
    public String getClanName() {
        return clanName;
    }
    public Location getDestination() {
        return destination;
    }
    public boolean isProcessing() {
        return processing;
    }
    public void setProcessing(boolean _processing) {
        processing = _processing;
    }
    public UUID getUniqueId() {
        return playerUniqueId;
    }
}
