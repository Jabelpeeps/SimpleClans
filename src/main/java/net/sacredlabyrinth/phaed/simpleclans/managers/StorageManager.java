package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private HashMap<String, ChatBlock> chatBlocks = new HashMap<>();
    
    PreparedStatement mostKilled;
    PreparedStatement getPlayers;
    PreparedStatement getClans;

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
                                    + "`uuid` VARCHAR( 255 ) DEFAULT NULL, "
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
                                    + "`attacker_uuid` VARCHAR( 255 ) DEFAULT NULL, "
                                    + "`victim` varchar(16) NOT NULL, "
                                    + "`victim_tag` varchar(16) NOT NULL, "
                                    + "`victim_uuid` VARCHAR( 255 ) DEFAULT NULL, "
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
                                    + "`victim` varchar(16) NOT NULL, "
                                    + "`victim_tag` varchar(16) NOT NULL, "
                                    + "`kill_type` varchar(1) NOT NULL, "
                                    + "PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }
            }
            else {
                Bukkit.getConsoleSender().sendMessage("[SimpleClans] " + ChatColor.RED + plugin.getLang("sqlite.connection.failed"));
            }
        }
        try (Connection con = core.getConnection()){
            mostKilled = con.prepareStatement( "SELECT attacker, victim, count(victim) AS kills FROM `sc_kills` GROUP BY attacker, victim ORDER BY 3 DESC;" );
            getPlayers = con.prepareCall( "SELECT * FROM  `sc_players`;" );
            getClans = con.prepareStatement( "SELECT * FROM  `sc_clans`;" );
        } catch ( SQLException e ) {
            e.printStackTrace();
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
        
        try (ResultSet res = core.getResultSet(getClans)) {
            if (res != null) {
                while (res.next()) {                    
                    out.add( setClanFields(res, new Clan()));                   
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
        Clan clan = new Clan();
        String query = "SELECT * FROM  `sc_clans` WHERE `tag` = '" + tagClan + "';";

        try ( ResultSet res = core.select(query) ) {
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

        if (founded == 0) founded = (new Date()).getTime();

        if (last_used == 0) last_used = (new Date()).getTime();

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

        try ( ResultSet res = core.getResultSet( getPlayers ) ) {
            if (res != null) {
                while (res.next()) {
                    
                    String uuid = res.getString("uuid");
                    String tag = res.getString("tag");
                    long last_seen = res.getLong("last_seen");
                    long join_date = res.getLong("join_date");

                    if (last_seen == 0) {
                        last_seen = (new Date()).getTime();
                    }

                    if (join_date == 0) {
                        join_date = (new Date()).getTime();
                    }

                    ClanPlayer cp = new ClanPlayer();
                    if (uuid != null) {
                        cp.setUniqueId(UUID.fromString(uuid));
                    }
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

                    if (!tag.isEmpty()) {
                        Clan clan = plugin.getClanManager().getClan(tag);

                        if (clan != null) {
                            cp.setClan(clan);
                        }
                    }
                    out.add(cp);              
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
     * Retrieves one clan player from the database
     * Used for BungeeCord Reload ClanPlayer and your Clan
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer retrieveOneClanPlayer(UUID playerUniqueId) {
        ClanPlayer out = null;
        String query = "SELECT * FROM `sc_players` WHERE `uuid` = '" + playerUniqueId.toString() + "';";

        try ( ResultSet res = core.select(query) ) {
            if (res != null) {
                while (res.next()) {
                    String uuid = res.getString("uuid");
                    String name = res.getString("name");
                    String tag = res.getString("tag");
                    boolean leader = res.getBoolean("leader");
                    boolean friendly_fire = res.getBoolean("friendly_fire");
                    boolean trusted = res.getBoolean("trusted");
                    int neutral_kills = res.getInt("neutral_kills");
                    int rival_kills = res.getInt("rival_kills");
                    int civilian_kills = res.getInt("civilian_kills");
                    int deaths = res.getInt("deaths");
                    long last_seen = res.getLong("last_seen");
                    long join_date = res.getLong("join_date");
                    String flags = res.getString("flags");
                    String packed_past_clans = Helper.parseColors(res.getString("packed_past_clans"));

                    if (last_seen == 0) {
                        last_seen = (new Date()).getTime();
                    }

                    if (join_date == 0) {
                        join_date = (new Date()).getTime();
                    }

                    ClanPlayer cp = new ClanPlayer();
                    if (uuid != null) {
                        cp.setUniqueId(UUID.fromString(uuid));
                    }
                    cp.setFlags(flags);
                    cp.setName(name);
                    cp.setLeader(leader);
                    cp.setFriendlyFire(friendly_fire);
                    cp.setNeutralKills(neutral_kills);
                    cp.setRivalKills(rival_kills);
                    cp.setCivilianKills(civilian_kills);
                    cp.setDeaths(deaths);
                    cp.setLastSeen(last_seen);
                    cp.setJoinDate(join_date);
                    cp.setPackedPastClans(packed_past_clans);
                    cp.setTrusted(leader || trusted);

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
                    out = cp;                    
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
     * Insert a clan into the database
     *
     * @param clan
     */
    public void insertClan(Clan clan) {
        String query = "INSERT INTO `sc_clans` (  `verified`, `tag`, `color_tag`, `name`, `friendly_fire`, `founded`, `last_used`, `packed_allies`, `packed_rivals`, `packed_bb`, `cape_url`, `flags`, `balance`) ";
        String values = "VALUES ( " + (clan.isVerified() ? 1 : 0) + ",'" + Helper.escapeQuotes(clan.getTag()) + "','" + Helper.escapeQuotes(clan.getColorTag()) + "','" + Helper.escapeQuotes(clan.getName()) + "'," + (clan.isFriendlyFire() ? 1 : 0) + ",'" + clan.getFounded() + "','" + clan.getLastUsed() + "','" + Helper.escapeQuotes(clan.getPackedAllies()) + "','" + Helper.escapeQuotes(clan.getPackedRivals()) + "','" + Helper.escapeQuotes(clan.getPackedBb()) + "','" + Helper.escapeQuotes(clan.getCapeUrl()) + "','" + Helper.escapeQuotes(clan.getFlags()) + "','" + Helper.escapeQuotes(String.valueOf(clan.getBalance())) + "');";
        core.insert(query + values);
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
        String query = "UPDATE `sc_players` SET `name` = '"+p.getName()+"' WHERE uuid = '"+p.getUniqueId().toString()+"';";
        core.update(query);
    }
    
    /**
     * Update a clan to the database
     *
     * @param clan
     */
    public void updateClan(Clan clan) {
        clan.updateLastUsed();
        String query = "UPDATE `sc_clans` SET verified = " + (clan.isVerified() ? 1 : 0) + ", tag = '" + Helper.escapeQuotes(clan.getTag()) + "', color_tag = '" + Helper.escapeQuotes(clan.getColorTag()) + "', name = '" + Helper.escapeQuotes(clan.getName()) + "', friendly_fire = " + (clan.isFriendlyFire() ? 1 : 0) + ", founded = '" + clan.getFounded() + "', last_used = '" + clan.getLastUsed() + "', packed_allies = '" + Helper.escapeQuotes(clan.getPackedAllies()) + "', packed_rivals = '" + Helper.escapeQuotes(clan.getPackedRivals()) + "', packed_bb = '" + Helper.escapeQuotes(clan.getPackedBb()) + "', cape_url = '" + Helper.escapeQuotes(clan.getCapeUrl()) + "', cape_url = '" + Helper.escapeQuotes(String.valueOf(clan.getCapeUrl())) + "', balance = '" + clan.getBalance() + "', flags = '" + Helper.escapeQuotes(clan.getFlags()) + "' WHERE tag = '" + Helper.escapeQuotes(clan.getTag()) + "';";
        core.update(query);
    }

    /**
     * Delete a clan from the database
     *
     * @param clan
     */
    public void deleteClan(Clan clan) {
        String query = "DELETE FROM `sc_clans` WHERE tag = '" + clan.getTag() + "';";
        core.delete(query);
    }

    /**
     * Insert a clan player into the database
     *
     * @param cp
     */
    public void insertClanPlayer(ClanPlayer cp) {        
        String query = "INSERT INTO `sc_players` ( `uuid`, `name`, `leader`, `tag`, `friendly_fire`, `neutral_kills`, `rival_kills`, `civilian_kills`, `deaths`, `last_seen`, `join_date`, `packed_past_clans`, `flags`) ";
        String values = "VALUES ( '" + cp.getUniqueId().toString() + "', '" + cp.getName() + "'," + (cp.isLeader() ? 1 : 0) + ",'" + Helper.escapeQuotes(cp.getTag()) + "'," + (cp.isFriendlyFire() ? 1 : 0) + "," + cp.getNeutralKills() + "," + cp.getRivalKills() + "," + cp.getCivilianKills() + "," + cp.getDeaths() + ",'" + cp.getLastSeen() + "',' " + cp.getJoinDate() + "','" + Helper.escapeQuotes(cp.getPackedPastClans()) + "','" + Helper.escapeQuotes(cp.getFlags()) + "');";
        core.insert(query + values);  
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
        
        String query = "UPDATE `sc_players` SET leader = " + (cp.isLeader() ? 1 : 0) + ", tag = '" + Helper.escapeQuotes(cp.getTag()) + "' , friendly_fire = " + (cp.isFriendlyFire() ? 1 : 0) + ", neutral_kills = " + cp.getNeutralKills() + ", rival_kills = " + cp.getRivalKills() + ", civilian_kills = " + cp.getCivilianKills() + ", deaths = " + cp.getDeaths() + ", last_seen = '" + cp.getLastSeen() + "', packed_past_clans = '" + Helper.escapeQuotes(cp.getPackedPastClans()) + "', trusted = " + (cp.isTrusted() ? 1 : 0) + ", flags = '" + Helper.escapeQuotes(cp.getFlags()) + "', name = '" + cp.getName() + "' WHERE `uuid` = '" + cp.getUniqueId().toString() + "';";
        core.update(query);
    }

    /**
     * Delete a clan player from the database
     *
     * @param cp
     */
    public void deleteClanPlayer(ClanPlayer cp) {
        String query = "DELETE FROM `sc_players` WHERE uuid = '" + cp.getUniqueId() + "';";
        core.delete(query);
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
        String query = "INSERT INTO `sc_kills` (  `attacker_uuid`, `attacker`, `attacker_tag`, `victim_uuid`, `victim`, `victim_tag`, `kill_type`) ";
        String values = "VALUES ( '" + attacker.getUniqueId() + "','" + attacker.getName() + "','" + attackerTag + "','" + victim.getUniqueId() + "','" + victim.getName() + "','" + victimTag + "','" + type + "');";
        core.insert(query + values);       
    }

    /**
     * Delete a player's kill record form the database
     *
     * @param playerUniqueId
     */
    public void deleteKills(UUID playerUniqueId) {
        String query = "DELETE FROM `sc_kills` WHERE `attacker_uuid` = '" + playerUniqueId + "'";
        core.delete(query);
    }

    /**
     * Returns a map of victim->count of all kills that specific player did
     *
     * @param playerName
     * @return
     */
    public Map<String, Integer> getKillsPerPlayer(String playerName) {
        HashMap<String, Integer> out = new HashMap<>();

        String query = "SELECT victim, count(victim) AS kills FROM `sc_kills` WHERE attacker = '" + playerName + "' GROUP BY victim ORDER BY count(victim) DESC;";

        try ( ResultSet res = core.select(query) ) {
            if (res != null) {
                while (res.next()) {                   
                    String victim = res.getString("victim");
                    int kills = res.getInt("kills");
                    out.put(victim, kills);
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

       try ( ResultSet res = core.getResultSet( mostKilled ) ) {
            if (res != null) {
                while (res.next()) {
                    String attacker = res.getString("attacker");
                    String victim = res.getString("victim");
                    int kills = res.getInt("kills");
                    out.put(attacker + " " + victim, kills);
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
