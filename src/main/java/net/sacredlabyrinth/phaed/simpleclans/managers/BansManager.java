package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class BansManager {

    private SimpleClans plugin = SimpleClans.getInstance();
    private Set<UUID> bannedPlayers = new HashSet<>();
    private File bansFile;
    private FileConfiguration bans;
    
    public BansManager() {
        bansFile = new File( plugin.getDataFolder(), "bans.yml");
        bans = YamlConfiguration.loadConfiguration( bansFile );
         
        bans.getStringList("bans")
            .parallelStream()
            .map( s -> UUID.fromString( s ) )
            .forEach( u -> bannedPlayers.add( u ) );
    }

    public void saveBans() {

        bans.set( "bans", null );
        
        List<String> newBans = new ArrayList<>();

        bannedPlayers.parallelStream()
                     .map( u -> u.toString() )
                     .forEach( e -> newBans.add( e ) );
        
        bans.set( "bans", newBans );
        
        try {
            bans.save( bansFile );
            
        } catch ( IOException e1 ) {
            plugin.getLogger().log( Level.SEVERE, "Error saving Bans list to bans.yml." );
            e1.printStackTrace();
        }
    }
    /**
     * Check whether a player is banned
     *
     * @param playerUniqueId the player's name
     * @return whether player is banned
     */
    public boolean isBanned(UUID playerUniqueId) {
        return bannedPlayers.contains( playerUniqueId );
    }

    /**
     * Add a player to the banned list
     *
     * @param playerUniqueId the player's name
     */
    public void addBanned(UUID playerUniqueId) {
        bannedPlayers.add(playerUniqueId);
    }

    /**
     * Remove a player from the banned list
     *
     * @param playerUniqueId the player's name
     */
    public void removeBanned(UUID playerUniqueId) {
        bannedPlayers.remove(playerUniqueId);
    }

    public Set<UUID> getBannedPlayers() { return Collections.unmodifiableSet(bannedPlayers); }
}
