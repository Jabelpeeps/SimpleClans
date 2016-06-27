package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.storage.DBCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.MySQLCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.SQLiteCore;

/**
 * @author phaed
 */
public final class StorageManager {

    private SimpleClans plugin;
    private DBCore core;
    private Connection connection;
    private HashMap<String, ChatBlock> chatBlocks = new HashMap<>();
    
    PreparedStatement mostKilled;
    PreparedStatement getPlayers;
    PreparedStatement getClans;
    PreparedStatement getKills;
    PreparedStatement deleteKills;
    PreparedStatement deleteCP;
    PreparedStatement insertKill;
    PreparedStatement updateCP;
    PreparedStatement insertCP;
    PreparedStatement deleteClan;
    PreparedStatement updateClan;
    PreparedStatement updatePlayerName;
    PreparedStatement insertClan;
    PreparedStatement getOneCP;
    PreparedStatement getOneClan;

    public StorageManager() {
        plugin = SimpleClans.getInstance();
        initiateDB();
        importFromDatabase();
    }

    /**
     * Retrieve a player's pending chat lines
     *
     * @param player
     * @return
     */
    public ChatBlock getChatBlock(Player player) {       
        return chatBlocks.get(player.getUniqueId().toString());        
    }

    /**
     * Store pending chat lines for a player
     *
     * @param player
     * @param cb
     */
    public void addChatBlock(CommandSender player, ChatBlock cb) {
        
        UUID uuid = Helper.getCachedPlayerUUID(player.getName());

        if (uuid == null) return;
        
        chatBlocks.put(uuid.toString(), cb);
    }

    public void initiateDB() {
        SettingsManager settings = plugin.getSettingsManager();
        if (settings.isUseMysql()) {
            core = new MySQLCore(settings.getHost(), settings.getDatabase(), settings.getPort(), settings.getUsername(), settings.getPassword());

            if (core.checkConnection()) {
                connection = core.getConnection();
                
                SimpleClans.log("[SimpleClans] " + plugin.getLang("mysql.connection.successful"));

                if (!core.existsTable("sc_clans")) {
                    SimpleClans.log("Creating table: sc_clans");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_clans` ( "
                                    + "`id` bigint(20) NOT NULL auto_increment, "
                                    + "`verified` tinyint(1) default '0', "
                                    + "`tag` varchar(25) NOT NULL, "
                                    + "`color_tag` varchar(25) NOT NULL, "
                                    + "`name` varchar(100) NOT NULL, "
                                    + "`friendly_fire` tinyint(1) default '0', "
                                    + "`founded` bigint NOT NULL, "
                                    + "`last_used` bigint NOT NULL, "
                                    + "`packed_allies` text NOT NULL, "
                                    + "`packed_rivals` text NOT NULL, "
                                    + "`packed_bb` mediumtext NOT NULL, "
                                    + "`cape_url` varchar(255) NOT NULL, "
                                    + "`flags` text NOT NULL, "
                                    + "`balance` double(64,2), "
                                    + "PRIMARY KEY  (`id`), "
                                    + "UNIQUE KEY `uq_simpleclans_1` (`tag`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_players")) {
                    SimpleClans.log("Creating table: sc_players");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_players` ( "
                                    + "`id` bigint(20) NOT NULL auto_increment, "
                                    + "`name` varchar(16) NOT NULL, "
                                    + "`uuid` CHAR(36) NOT NULL, "
                                    + "`leader` tinyint(1) default '0', "
                                    + "`tag` varchar(25) NOT NULL, "
                                    + "`friendly_fire` tinyint(1) default '0', "
                                    + "`neutral_kills` int(11) default NULL, "
                                    + "`rival_kills` int(11) default NULL, "
                                    + "`civilian_kills` int(11) default NULL, "
                                    + "`deaths` int(11) default NULL, "
                                    + "`last_seen` bigint NOT NULL, "
                                    + "`join_date` bigint NOT NULL, "
                                    + "`trusted` tinyint(1) default '0', "
                                    + "`flags` text NOT NULL, "
                                    + "`packed_past_clans` text, "
                                    + "PRIMARY KEY  (`id`), "
                                    + "UNIQUE INDEX `uq_player_uuid` (`uuid`)"
                                    + ");";
                    core.execute(query);
                }

                if (!core.existsTable("sc_kills")) {
                    SimpleClans.log("Creating table: sc_kills");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_kills` ( "
                                    + "`kill_id` bigint(20) NOT NULL auto_increment, "
                                    + "`attacker` varchar(16) NOT NULL, "
                                    + "`attacker_tag` varchar(16) NOT NULL, "
                                    + "`attacker_uuid` CHAR(36) NOT NULL, "
                                    + "`victim` varchar(16) NOT NULL, "
                                    + "`victim_tag` varchar(16) NOT NULL, "
                                    + "`victim_uuid` CHAR(36) NOT NULL, "
                                    + "`kill_type` varchar(1) NOT NULL, "
                                    + "PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }
            }
            else Bukkit.getConsoleSender().sendMessage("[SimpleClans] " + ChatColor.RED + plugin.getLang("mysql.connection.failed"));
        }
        else {
            core = new SQLiteCore(plugin.getDataFolder().getPath());

            if (core.checkConnection()) {
                connection = core.getConnection();
                SimpleClans.log("[SimpleClans] " + plugin.getLang("sqlite.connection.successful"));

                if (!core.existsTable("sc_clans")) {
                    SimpleClans.log("Creating table: sc_clans");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_clans` ( "
                                    + "`id` bigint(20), "
                                    + "`verified` tinyint(1) default '0', "
                                    + "`tag` varchar(25) NOT NULL, "
                                    + "`color_tag` varchar(25) NOT NULL, "
                                    + "`name` varchar(100) NOT NULL, "
                                    + "`friendly_fire` tinyint(1) default '0', "
                                    + "`founded` bigint NOT NULL, "
                                    + "`last_used` bigint NOT NULL, "
                                    + "`packed_allies` text NOT NULL, "
                                    + "`packed_rivals` text NOT NULL, "
                                    + "`packed_bb` mediumtext NOT NULL, "
                                    + "`cape_url` varchar(255) NOT NULL, "
                                    + "`flags` text NOT NULL, "
                                    + "`balance` double(64,2) default 0.0,  "
                                    + "PRIMARY KEY  (`id`), UNIQUE (`tag`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_players")) {
                    SimpleClans.log("Creating table: sc_players");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_players` ( "
                                    + "`id` bigint(20), "
                                    + "`name` varchar(16) NOT NULL, "
                                    + "`uuid` CHAR(36) NOT NULL, "
                                    + "`leader` tinyint(1) default '0', "
                                    + "`tag` varchar(25) NOT NULL, "
                                    + "`friendly_fire` tinyint(1) default '0', "
                                    + "`neutral_kills` int(11) default NULL, "
                                    + "`rival_kills` int(11) default NULL, "
                                    + "`civilian_kills` int(11) default NULL, "
                                    + "`deaths` int(11) default NULL, "
                                    + "`last_seen` bigint NOT NULL, "
                                    + "`join_date` bigint NOT NULL, "
                                    + "`trusted` tinyint(1) default '0', "
                                    + "`flags` text NOT NULL, "
                                    + "`packed_past_clans` text, "
                                    + "PRIMARY KEY  (`id`), UNIQUE (`name`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_kills")) {
                    SimpleClans.log("Creating table: sc_kills");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_kills` ( "
                                    + "`kill_id` bigint(20), "
                                    + "`attacker` varchar(16) NOT NULL, "
                                    + "`attacker_tag` varchar(16) NOT NULL, "
                                    + "`attacker_uuid` CHAR(36) NOT NULL, "
                                    + "`victim` varchar(16) NOT NULL, "
                                    + "`victim_tag` varchar(16) NOT NULL, "
                                    + "`victim_uuid` CHAR(36) NOT NULL, "
                                    + "`kill_type` varchar(1) NOT NULL, "
                                    + "PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }
            }
            else {
                Bukkit.getConsoleSender().sendMessage("[SimpleClans] " + ChatColor.RED + plugin.getLang("sqlite.connection.failed"));
            }
        }
    }

    private boolean checkStatement(PreparedStatement query) {
        try {
            return query.getConnection().equals( connection );
        } catch ( SQLException e ) {
            return false;
        }
    }
    
    private PreparedStatement prepareStatement( String query ) {
        try {
            return connection.prepareStatement( query );
        } catch ( SQLException e ) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void closeConnection() {
        core.close();
    }

    /**
     * Import all data from database to memory
     */
    public void importFromDatabase() {
        ClanManager clanManager = plugin.getClanManager();
        clanManager.cleanData();

        List<Clan> clans = retrieveClans();
        purgeClans(clans);

        for (Clan clan : clans) clanManager.importClan(clan);

        for (Clan clan : clans) clan.validateWarring();

        if (!clans.isEmpty()) {
            SimpleClans.log(MessageFormat.format("[SimpleClans] " + plugin.getLang("clans"), clans.size()));
        }

        List<ClanPlayer> cps = retrieveClanPlayers();
        purgeClanPlayers(cps);

        for (ClanPlayer cp : cps) {
            Clan tm = cp.getClan();

            if (tm != null) {
                tm.importMember(cp);
            }
            clanManager.importClanPlayer(cp);
        }

        if (!cps.isEmpty()) {
            SimpleClans.log(MessageFormat.format("[SimpleClans] " + plugin.getLang("clan.players"), cps.size()));
        }
    }

    /**
     * Import one ClanPlayer data from database to memory
     * Used for BungeeCord Reload ClanPlayer and your Clan
     *
     * @param player
     */
    public void importFromDatabaseOnePlayer(Player player) {
        plugin.getClanManager().deleteClanPlayerFromMemory(player.getUniqueId());

        ClanPlayer cp = retrieveOneClanPlayer(player.getUniqueId());

        if (cp != null) {
            Clan tm = cp.getClan();

            if (tm != null) {
                tm.importMember(cp);
            }
            plugin.getClanManager().importClanPlayer(cp);

            SimpleClans.log("[SimpleClans] ClanPlayer Reloaded: " + player.getName() + ", UUID: " + player.getUniqueId().toString());
        }
    }

    private void purgeClans(List<Clan> clans) {
        List<Clan> purge = new ArrayList<>();

        for (Clan clan : clans) {
            if (clan.isVerified()) {
                if (clan.getInactiveDays() > plugin.getSettingsManager().getPurgeClan()) {
                    purge.add(clan);
                }
            }
            else if (clan.getInactiveDays() > plugin.getSettingsManager().getPurgeUnverified()) {
                purge.add(clan);              
            }
        }
        for (Clan clan : purge) {
            SimpleClans.log("[SimpleClans] " + MessageFormat.format(plugin.getLang("purging.clan"), clan.getName()));
            deleteClan(clan);
            clans.remove(clan);
        }
    }

    private void purgeClanPlayers(List<ClanPlayer> cps) {
        List<ClanPlayer> purge = new ArrayList<>();

        for (ClanPlayer cp : cps) {
            if (cp.getInactiveDays() > plugin.getSettingsManager().getPurgePlayers() && !cp.isLeader()) {
            	purge.add(cp);
            }
        }
        for (ClanPlayer cp : purge) {
            SimpleClans.log("[SimpleClans] " + MessageFormat.format(plugin.getLang("purging.player.data"), cp.getName()));
            deleteClanPlayer(cp);
            cps.remove(cp);
        }
    }

    /**
     * Retrieves all simple clans from the database
     *
     * @return
     */
    public List<Clan> retrieveClans() {
        List<Clan> out = new ArrayList<>();
        
        if ( getClans == null || !checkStatement(getClans) ) {
            getClans = prepareStatement( "SELECT * FROM  `sc_clans`;" );
        }
        try ( ResultSet res = core.getResultSet(getClans) ) {           
            if (res != null) {
                while (res.next()) {                    
                    out.add( setClanFields( res, new Clan() ) );                   
                }
            }
        }
        catch (SQLException ex) {
            SimpleClans.getLog().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
            SimpleClans.getLog().log(Level.SEVERE, null, ex);         
        }
        return out;
    }

    /**
     * Retrieves one Clan from the database
     * Used for BungeeCord Reload ClanPlayer and your Clan
     *
     * @param tagClan
     * @return
     */
    public Clan retrieveOneClan(String tagClan) {
        if (getOneClan == null || !checkStatement(getOneClan)) {
            getOneClan = prepareStatement("SELECT * FROM  `sc_clans` WHERE `tag` = '?';");
        }
        Clan clan = new Clan();

        try ( ResultSet res = core.getResultSet( getOneClan, tagClan ) ) {
            if (res != null) {
                while (res.next()) {    
                    clan = setClanFields( res, clan );
                }
            }
        }
        catch (SQLException ex) {
            SimpleClans.getLog().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
            SimpleClans.getLog().log(Level.SEVERE, null, ex);
        }
        return clan;
    }

    private Clan setClanFields(ResultSet res, Clan clan) throws SQLException {
        long founded = res.getLong("founded");
        long last_used = res.getLong("last_used");

        if (founded == 0) founded = System.currentTimeMillis();
        if (last_used == 0) last_used = System.currentTimeMillis();

        clan.setFlags(res.getString("flags"));
        clan.setVerified(res.getBoolean("verified"));
        clan.setFriendlyFire(res.getBoolean("friendly_fire"));
        clan.setTag(res.getString("tag"));
        clan.setColorTag(Helper.parseColors(res.getString("color_tag")));
        clan.setName(res.getString("name"));
        clan.setPackedAllies(res.getString("packed_allies"));
        clan.setPackedRivals(res.getString("packed_rivals"));
        clan.setPackedBb(res.getString("packed_bb"));
        clan.setCapeUrl(res.getString("cape_url"));
        clan.setFounded(founded);
        clan.setLastUsed(last_used);
        clan.setBalance(res.getDouble("balance"));
        
        return clan;
    }
    
    /**
     * Retrieves all clan players from the database
     *
     * @return
     */
    public List<ClanPlayer> retrieveClanPlayers() {
        List<ClanPlayer> out = new ArrayList<>();
        
        if ( getPlayers == null || !checkStatement(getPlayers) ) {
            getPlayers = prepareStatement( "SELECT * FROM  `sc_players`;" );
        }
        try ( ResultSet res = core.getResultSet( getPlayers ) ) {
            if (res != null) {
                while (res.next()) { 
                    ClanPlayer cp = new ClanPlayer();
                    String tag = res.getString("tag");  
                    
                    if (!tag.isEmpty()) {
                        Clan clan = plugin.getClanManager().getClan(tag);

                        if (clan != null) {
                            cp.setClan(clan);
                        }
                    }
                    out.add( setFields(res, cp));              
                }
            }
        }
        catch (SQLException ex) {
            SimpleClans.getLog().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
            SimpleClans.getLog().log(Level.SEVERE, null, ex);
        }
        return out;
    }

    private ClanPlayer setFields(ResultSet res, ClanPlayer cp ) throws SQLException {
        long last_seen = res.getLong("last_seen");
        long join_date = res.getLong("join_date");

        if (last_seen == 0) last_seen = System.currentTimeMillis();
        if (join_date == 0) join_date = System.currentTimeMillis();

        cp.setUniqueId(UUID.fromString(res.getString("uuid")));
        cp.setFlags(res.getString("flags"));
        cp.setName(res.getString("name"));
        cp.setLeader(res.getBoolean("leader"));
        cp.setFriendlyFire(res.getBoolean("friendly_fire"));
        cp.setNeutralKills(res.getInt("neutral_kills"));
        cp.setRivalKills(res.getInt("rival_kills"));
        cp.setCivilianKills(res.getInt("civilian_kills"));
        cp.setDeaths(res.getInt("deaths"));
        cp.setLastSeen(last_seen);
        cp.setJoinDate(join_date);
        cp.setPackedPastClans(Helper.parseColors(res.getString("packed_past_clans")));
        cp.setTrusted(res.getBoolean("leader") || res.getBoolean("trusted"));

        return cp;
    }
    
    /**
     * Retrieves one clan player from the database
     * Used for BungeeCord Reload ClanPlayer and your Clan
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer retrieveOneClanPlayer(UUID playerUniqueId) {
        if (getOneCP == null || checkStatement(getOneCP)) {
            getOneCP = prepareStatement("SELECT * FROM `sc_players` WHERE `uuid` = '?';" );
        }

        try ( ResultSet res = core.getResultSet( getOneCP, playerUniqueId.toString() ) ) {
            if (res != null) {
                while (res.next()) {
                    String tag = res.getString("tag");
                    ClanPlayer cp = new ClanPlayer();

                    if (!tag.isEmpty()) {
                        Clan clanDB = retrieveOneClan(tag);
                        Clan clan = plugin.getClanManager().getClan(tag);

                        if (clan != null) {
                            Clan clanReSync = plugin.getClanManager().getClan(tag);
                            clanReSync.setFlags(clanDB.getFlags());
                            clanReSync.setVerified(clanDB.isVerified());
                            clanReSync.setFriendlyFire(clanDB.isFriendlyFire());
                            clanReSync.setTag(clanDB.getTag());
                            clanReSync.setColorTag(clanDB.getColorTag());
                            clanReSync.setName(clanDB.getName());
                            clanReSync.setPackedAllies(clanDB.getPackedAllies());
                            clanReSync.setPackedRivals(clanDB.getPackedRivals());
                            clanReSync.setPackedBb(clanDB.getPackedBb());
                            clanReSync.setCapeUrl(clanDB.getCapeUrl());
                            clanReSync.setFounded(clanDB.getFounded());
                            clanReSync.setLastUsed(clanDB.getLastUsed());
                            clanReSync.setBalance(clanDB.getBalance());
                            cp.setClan(clanReSync);
                        }
                        else {
                            plugin.getClanManager().importClan(clanDB);
                            clanDB.validateWarring();
                            Clan newclan = plugin.getClanManager().getClan(clanDB.getTag());
                            cp.setClan(newclan);
                        }
                    } 
                    return setFields(res, cp);                   
                }
            }
        }
        catch (SQLException ex) {
            SimpleClans.getLog().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
            SimpleClans.getLog().log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Insert a clan into the database
     *
     * @param clan
     */
    public void insertClan(Clan clan) {
        if ( insertClan == null || !checkStatement(insertClan)) {
            insertClan = prepareStatement(
                    "INSERT INTO `sc_clans` (  `verified`, `tag`, `color_tag`, `name`, `friendly_fire`, `founded`, "
                    + "`last_used`, `packed_allies`, `packed_rivals`, `packed_bb`, `cape_url`, `flags`, `balance`) "
                    + "VALUES ( '?','?','?','?','?','?','?','?','?','?','?','?','?');" );
        }
        core.executeUpdate( insertClan, String.valueOf( clan.isVerified() ? 1 : 0 ),
                                        Helper.escapeQuotes(clan.getTag()),
                                        Helper.escapeQuotes(clan.getColorTag()),
                                        Helper.escapeQuotes(clan.getName()),
                                        String.valueOf( clan.isFriendlyFire() ? 1 : 0 ),
                                        String.valueOf( clan.getFounded() ),
                                        String.valueOf( clan.getLastUsed() ),
                                        Helper.escapeQuotes(clan.getPackedAllies()),
                                        Helper.escapeQuotes(clan.getPackedRivals()),
                                        Helper.escapeQuotes(clan.getPackedBb()),
                                        Helper.escapeQuotes(clan.getCapeUrl()),
                                        Helper.escapeQuotes(clan.getFlags()),
                                        Helper.escapeQuotes(String.valueOf(clan.getBalance())));
    }

    /**
     * Update a clan to the database asynchronously
     *
     * @param clan
     */
	public void updateClanAsync(final Clan clan) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateClan(clan);           
            }           
        }.runTaskAsynchronously( plugin );
    }

    /**
     * Change the name of a player in the database asynchronously
     * 
     * @param Player to update
     */
	public void updatePlayerNameAsync(final Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePlayerName(p);             
            }           
        }.runTaskAsynchronously( plugin );
    }
    
    /**
     * Change the name of a player in the database asynchronously
     * 
     * @param Player to update
     */
    public void updatePlayerName(final Player p) {
        if ( updatePlayerName == null || checkStatement(updatePlayerName)) {
            updatePlayerName = prepareStatement("UPDATE `sc_players` SET `name` = '?' WHERE uuid = '?';");
        }
        core.executeUpdate( updatePlayerName, p.getName(), p.getUniqueId().toString());
    }
    
    /**
     * Update a clan to the database
     *
     * @param clan
     */
    public void updateClan(Clan clan) {
        if ( updateClan == null || !checkStatement(updateClan)) {
            updateClan = prepareStatement( 
                    "UPDATE `sc_clans` SET verified = '?', tag = '?', color_tag = '?', name = '?', friendly_fire = '?', "
                    + "founded = '?', last_used = '?', packed_allies = '?', packed_rivals = '?', packed_bb = '?', "
                    + "cape_url = '?', balance = '?', flags = '?' WHERE tag = '?';" );
        }
        clan.updateLastUsed();
        core.executeUpdate( updateClan, String.valueOf(clan.isVerified() ? 1 : 0),
                                        Helper.escapeQuotes(clan.getTag()),
                                        Helper.escapeQuotes(clan.getColorTag()),
                                        Helper.escapeQuotes(clan.getName()),
                                        String.valueOf(clan.isFriendlyFire() ? 1 : 0),
                                        String.valueOf(clan.getFounded()),
                                        String.valueOf( clan.getLastUsed() ),
                                        Helper.escapeQuotes(clan.getPackedAllies()),
                                        Helper.escapeQuotes(clan.getPackedRivals()),
                                        Helper.escapeQuotes(clan.getPackedBb()),
                                        Helper.escapeQuotes(clan.getCapeUrl()),
                                        String.valueOf( clan.getBalance() ),
                                        Helper.escapeQuotes(clan.getFlags()),
                                        Helper.escapeQuotes(clan.getTag()));
    }

    /**
     * Delete a clan from the database
     *
     * @param clan
     */
    public void deleteClan(Clan clan) {
        if ( deleteClan == null || !checkStatement(deleteClan)) {
            deleteClan =  prepareStatement( "DELETE FROM `sc_clans` WHERE tag = '?';" );
        }
        core.executeUpdate( deleteClan, clan.getTag() );
    }

    /**
     * Insert a clan player into the database
     *
     * @param cp
     */
    public void insertClanPlayer(ClanPlayer cp) { 
        if ( insertCP == null || checkStatement(insertCP)) {
            insertCP = prepareStatement(
                    "INSERT INTO `sc_players` ( `uuid`, `name`, `leader`, `tag`, `friendly_fire`, `neutral_kills`, "
                    + "`rival_kills`, `civilian_kills`, `deaths`, `last_seen`, `join_date`, `packed_past_clans`, "
                    + "`flags`) VALUES ('?','?','?','?','?','?','?','?','?','?','?','?','?');" );
        }
        core.executeUpdate( insertCP, cp.getUniqueId().toString(),
                                      cp.getName(),
                                      String.valueOf(cp.isLeader() ? 1 : 0),
                                      Helper.escapeQuotes(cp.getTag()),
                                      String.valueOf(cp.isFriendlyFire() ? 1 : 0),
                                      String.valueOf( cp.getNeutralKills() ),
                                      String.valueOf( cp.getRivalKills() ),
                                      String.valueOf( cp.getCivilianKills() ),
                                      String.valueOf( cp.getDeaths() ),
                                      String.valueOf( cp.getLastSeen() ),
                                      String.valueOf( cp.getJoinDate() ),
                                      Helper.escapeQuotes(cp.getPackedPastClans()),
                                      Helper.escapeQuotes(cp.getFlags()));  
    }

    /**
     * Update a clan player to the database asynchronously
     *
     * @param cp
     */
	public void updateClanPlayerAsync(final ClanPlayer cp) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateClanPlayer(cp);               
            }
        }.runTaskAsynchronously( plugin );
    }

    /**
     * Update a clan player to the database
     *
     * @param cp
     */
    public void updateClanPlayer(ClanPlayer cp) {
        cp.updateLastSeen();
        if ( updateCP == null || !checkStatement(updateCP)) {
            updateCP = prepareStatement(
                    "UPDATE `sc_players` SET leader = '?', tag = '?' , friendly_fire = '?', neutral_kills = '?', "
                    + "rival_kills = '?', civilian_kills = '?', deaths = '?', last_seen = '?', packed_past_clans = '?', "
                    + "trusted = '?', flags = '?', name = '?' WHERE `uuid` = '?';");
        }
        core.executeUpdate( updateCP, String.valueOf( cp.isLeader() ? 1 : 0), 
                                      Helper.escapeQuotes( cp.getTag() ),
                                      String.valueOf( cp.isFriendlyFire() ? 1 : 0), 
                                      String.valueOf( cp.getNeutralKills() ), 
                                      String.valueOf( cp.getRivalKills() ), 
                                      String.valueOf( cp.getCivilianKills() ), 
                                      String.valueOf( cp.getDeaths() ), 
                                      String.valueOf( cp.getLastSeen() ), 
                                      Helper.escapeQuotes( cp.getPackedPastClans() ),
                                      String.valueOf( cp.isTrusted() ? 1 : 0 ), 
                                      Helper.escapeQuotes( cp.getFlags() ), 
                                      cp.getName(),
                                      cp.getUniqueId().toString() );
    }

    /**
     * Delete a clan player from the database
     *
     * @param cp
     */
    public void deleteClanPlayer(ClanPlayer cp) {
        if ( deleteCP == null || !checkStatement(deleteCP)) {
            deleteCP = prepareStatement("DELETE FROM `sc_players` WHERE uuid = '?';");
        }
        core.executeUpdate( deleteCP, cp.getUniqueId().toString() );;
        deleteKills(cp.getUniqueId());
    }

    /**
     * Insert a kill into the database
     *
     * @param attacker
     * @param attackerTag
     * @param victim
     * @param victimTag
     * @param type
     */
    public void insertKill(Player attacker, String attackerTag, Player victim, String victimTag, String type) {        
        if ( insertKill == null || !checkStatement(insertKill)) {
            insertKill = prepareStatement(
                    "INSERT INTO `sc_kills` (  `attacker_uuid`, `attacker`, `attacker_tag`, `victim_uuid`,"
                    + " `victim`, `victim_tag`, `kill_type`) VALUES ( '?','?','?','?','?','?','?');" );
        }
        core.executeUpdate( insertKill, attacker.getUniqueId().toString(), attacker.getName(), attackerTag,
                                        victim.getUniqueId().toString(), victim.getName(), victimTag, type ); 
    }

    /**
     * Delete a player's kill record form the database
     *
     * @param playerUniqueId
     */
    public void deleteKills(UUID playerUniqueId) {
        if ( deleteKills == null || !checkStatement(deleteKills) ) {
            deleteKills = prepareStatement("DELETE FROM `sc_kills` WHERE `attacker_uuid` = '?';");
        }
        core.executeUpdate( deleteKills, playerUniqueId.toString() );
    }

    /**
     * Returns a map of victim->count of all kills that specific player did
     *
     * @param playerName
     * @return
     */
    public Map<String, Integer> getKillsPerPlayer(UUID player) {
        HashMap<String, Integer> out = new HashMap<>();

        if ( getKills == null || !checkStatement(getKills) ) {
            getKills = prepareStatement( "SELECT victim, count(victim) AS kills FROM `sc_kills` WHERE attacker_uuid = '?' GROUP BY victim ORDER BY count(victim) DESC;" );
        }

        try ( ResultSet res = core.getResultSet( getKills, player.toString() ) ) {
            if (res != null) {
                while (res.next()) { 
                    out.put(res.getString("victim"), res.getInt("kills"));
                }
            }
        }
        catch (SQLException ex) {
            SimpleClans.getLog().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
            SimpleClans.getLog().log(Level.SEVERE, null, ex);
        }
        return out;
    }

    /**
     * Returns a map of tag->count of all kills
     *
     * @return
     */
    public Map<String, Integer> getMostKilled() {
        HashMap<String, Integer> out = new HashMap<>();
        
        if ( mostKilled == null || !checkStatement(mostKilled) ) {
            mostKilled = prepareStatement( "SELECT attacker, victim, count(victim) AS kills FROM `sc_kills` GROUP BY attacker, victim ORDER BY 3 DESC;" );
        }
        try ( ResultSet res = core.getResultSet( mostKilled ) ) {
            if (res != null) {
                while ( res.next() ) {
                    out.put(res.getString("attacker") + " " + res.getString("victim"), res.getInt("kills"));
                }
            }
        }
        catch (SQLException ex) {
            SimpleClans.getLog().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
            SimpleClans.getLog().log(Level.SEVERE, null, ex);
        }
        return out;
    }
}
