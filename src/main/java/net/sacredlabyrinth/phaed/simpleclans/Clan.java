package net.sacredlabyrinth.phaed.simpleclans;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import net.sacredlabyrinth.phaed.simpleclans.events.AllyClanAddEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.AllyClanRemoveEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.DisbandClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerDemoteEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerJoinedClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerKickedClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerPromoteEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.RivalClanAddEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.RivalClanRemoveEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class Clan implements Serializable, Comparable<Clan> {

    private static final long serialVersionUID = 1L;
    private boolean verified;
    private String tag;
    private String colorTag;
    private String name;
    private double balance;
    private boolean friendlyFire;
    private long founded;
    private long lastUsed;
    private String capeUrl = "";
    private Set<String> allies = new HashSet<>();
    private Set<String> rivals = new HashSet<>();
    private List<String> bb = new ArrayList<>();
    private Set<UUID> members = new HashSet<>();
    private Set<Clan> warringClans = new HashSet<>();
    private int homeX = 0;
    private int homeY = 0;
    private int homeZ = 0;
    private String homeWorld = "";
    private boolean allowWithdraw = false;
    private boolean allowDeposit = true;
    SimpleClans plugin = SimpleClans.getInstance();

    public Clan() {
        tag = "";
    }

    public Clan(String _tag, String _name, boolean _verified) {
        tag = Helper.cleanTag(_tag);
        colorTag = Helper.parseColors(_tag);
        name = _name;
        founded = System.currentTimeMillis();
        lastUsed = System.currentTimeMillis();
        verified = _verified;
        friendlyFire = plugin.getSettingsManager().isClanFFOnByDefault();
    }

    @Override
    public int hashCode() {
        return tag.hashCode() >> 13;
    }
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Clan && ((Clan) obj).tag.equals(tag);
    }
    @Override
    public int compareTo(Clan other) {
        return tag.compareToIgnoreCase(other.tag);
    }
    @Override
    public String toString() {
        return tag;
    }

    /**
     * deposits money to the clan
     *
     * @param amount
     * @param player
     */
    public void deposit(double amount, Player player) {
        PermissionsManager perms = plugin.getPermissionsManager();
        if (perms.playerHasMoney(player, amount)) {
            if (perms.playerChargeMoney(player, amount)) {
                
                player.sendMessage(ChatColor.AQUA + MessageFormat.format(plugin.getLang("player.clan.deposit"), amount));
                addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("bb.clan.deposit"), amount));
                balance += amount;
                plugin.getStorageManager().updateClan(this);
            }
            else player.sendMessage(ChatColor.AQUA + plugin.getLang("not.sufficient.money"));
        }
        else player.sendMessage(ChatColor.AQUA + plugin.getLang("not.sufficient.money"));
    }

    /**
     * withdraws money to the clan
     *
     * @param amount
     * @param player
     */
    public void withdraw(double amount, Player player) {
        if (balance >= amount) {
            if (plugin.getPermissionsManager().playerGrantMoney(player, amount)) {
                
                player.sendMessage(ChatColor.AQUA + MessageFormat.format(plugin.getLang("player.clan.withdraw"), amount));
                addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("bb.clan.withdraw"), amount));
                balance -= amount;
                plugin.getStorageManager().updateClan(this);
            }
        }
        else player.sendMessage(ChatColor.AQUA + plugin.getLang("clan.bank.not.enough.money"));
    }

    public String getName() { return name; }
    public void setName(String _name) { name = _name; }
    public double getBalance() { return balance; }
    public void setBalance(double _balance) { balance = _balance; }
    /**
     * Returns the clan's tag clean (no colors)
     *
     * @return the tag
     */
    public String getTag() { return tag; }
    public void setTag(String _tag) { tag = _tag; }
    public long getLastUsed() { return lastUsed; }
    /**
     * Updates last used date to today (does not update clan on db)
     */
    public void updateLastUsed() { lastUsed = System.currentTimeMillis(); }
    public int getInactiveDays() {
        return (int) Math.floor(Dates.differenceInDays(lastUsed, System.currentTimeMillis()));
    }
    public void setLastUsed(long _lastUsed) { lastUsed = _lastUsed; }
    public boolean isFriendlyFire() { return friendlyFire; }
    /**
     * Sets the friendly fire status of this clan (does not update clan on db)
     *
     * @param friendlyFire the friendlyFire to set
     */
    public void setFriendlyFire(boolean _friendlyFire) { friendlyFire = _friendlyFire; }
    public boolean isMember(Player player) { return members.contains(player.getUniqueId()); }
    public boolean isMember(UUID playerUniqueId) { return members.contains(playerUniqueId); }
    /**
     * Returns a list with the contents of the bulletin board
     *
     * @return the bb
     */
    public List<String> getBb() { return Collections.unmodifiableList(bb); }
    private void addAlly(String _tag) { allies.add(_tag); }
    private boolean removeAlly(String ally) { return allies.remove(ally); }
    /**
     * The founded date in milliseconds
     *
     * @return the founded
     */
    public long getFounded() { return founded; }
    /**
     * The string representation of the founded date
     *
     * @return
     */
    public String getFoundedString() {
        return new SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(founded));
    }
    public void setFounded(long _founded) { founded = _founded; }
    public String getColorTag() { return colorTag; }
    public void setColorTag(String _colorTag) { colorTag = Helper.parseColors(_colorTag); }

    /**
     * Adds a bulletin board message without announcer
     *
     * @param msg
     */
    public void addBb(String msg) {
        while (bb.size() > plugin.getSettingsManager().getBbSize()) {
            bb.remove(0);
        }
        bb.add(msg);
        plugin.getStorageManager().updateClan(this);
    }

    public void clearBb() {
        bb.clear();
        plugin.getStorageManager().updateClan(this);
    }

    public void importMember(ClanPlayer cp) {
        UUID uuid = cp.getUniqueId();
    
        if ( uuid == null ) return;
    
        members.add(uuid);
    }

    public void removeMember(UUID playerUniqueId) { members.remove(playerUniqueId); }
    public int getSize() { return members.size(); }
    /**
     * Returns a list of all rival tags clean (no colors)
     *
     * @return the rivals
     */
    public Set<String> getRivals() { return Collections.unmodifiableSet(rivals); }
    private void addRival(String _tag) { rivals.add(_tag); }
    private boolean removeRival(String rival) { return rivals.remove(rival); }
    public boolean isRival(String _tag) { return rivals.contains(_tag); }
    public boolean isAlly(String _tag) { return allies.contains(_tag); }

    /**
     * Tells you if the clan is verified, always returns true if no verification
     * is required
     *
     * @return
     */
    public boolean isVerified() {
        return verified || !plugin.getSettingsManager().isRequireVerification();
    }

    public void setVerified(boolean _verified) { verified = _verified; }
    public String getCapeUrl() { return capeUrl; }
    public void setCapeUrl(String _capeUrl) { capeUrl = _capeUrl; }
    public String getPackedBb() { return Helper.toMessage(bb, "|"); }
    public void setPackedBb(String packedBb) { bb = Helper.fromArray(packedBb.split("[|]")); }
    public String getPackedAllies() { return Helper.toMessage(allies, "|"); }
    public void setPackedAllies(String packedAllies) { allies = Helper.fromArray2(packedAllies.split("[|]")); }
    public String getPackedRivals() { return Helper.toMessage(rivals, "|"); }
    public void setPackedRivals(String packedRivals) { rivals = Helper.fromArray2(packedRivals.split("[|]")); }

    /**
     * Returns a separator delimited string with all the ally clan's colored
     * tags
     *
     * @param sep
     * @return
     */
    public String getAllyString(String sep) {
        String out = "";

        ClanManager clanMan = plugin.getClanManager();
        for (String allyTag : allies) {
            Clan ally = clanMan.getClan(allyTag);

            if (ally != null) {
                out += ally.getColorTag() + sep;
            }
        }

        out = Helper.stripTrailing(out, sep);

        if (out.trim().isEmpty()) {
            return ChatColor.BLACK + "None";
        }
        return Helper.parseColors(out);
    }

    /**
     * Returns a separator delimited string with all the rival clan's colored
     * tags
     *
     * @param sep
     * @return
     */
    public String getRivalString(String sep) {
        String out = "";

        ClanManager clanMan = plugin.getClanManager();
        for (String rivalTag : rivals) {
            Clan rival = clanMan.getClan(rivalTag);

            if (rival != null) {
                if (isWarring(rival)) {
                    out += ChatColor.DARK_RED + "[" + Helper.stripColors(rival.getColorTag()) + "]" + sep;
                }
                else {
                    out += rival.getColorTag() + sep;
                }

            }
        }
        out = Helper.stripTrailing(out, sep);

        if (out.trim().isEmpty()) {
            return ChatColor.BLACK + "None";
        }
        return Helper.parseColors(out);
    }

    /**
     * Returns a separator delimited string with all the leaders
     *
     * @param prefix
     * @param sep
     * @return the formatted leaders string
     */
    public String getLeadersString(String prefix, String sep) {
        String out = "";

        ClanManager clanMan = plugin.getClanManager();
        for (UUID member : members) {
            ClanPlayer cp = clanMan.getClanPlayer(member);
        
            if (cp == null)  continue;

            if (cp.isLeader()) {
                out += prefix + cp.getName() + sep;
            }
        }
        return Helper.stripTrailing(out, sep);
    }

    public boolean isLeader(Player player) { return isLeader(player.getUniqueId()); }

    /**
     * Check if a player is a leader of a clan
     *
     * @param playerUniqueId
     * @return the leaders
     */
    public boolean isLeader(UUID playerUniqueId) {
        if (isMember(playerUniqueId)) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerUniqueId);

            return cp != null && cp.isLeader();
        }
        return false;
    }

    /**
     * Get all members (leaders, and non-leaders) in the clan
     *
     * @return the members
     */
    public List<ClanPlayer> getMembers() {
        ClanManager clanMan = plugin.getClanManager();

        return members.parallelStream()
                      .map( m -> clanMan.getClanPlayer( m ) )
                      .filter( e -> e != null )
                      .collect( Collectors.toList() );
    }

    /**
     * Get all online members (leaders, and non-leaders) in the clan
     *
     * @return the members
     */
    public List<ClanPlayer> getOnlineMembers() {
        ClanManager clanMan = plugin.getClanManager();

        return members.parallelStream()
                      .map( m -> clanMan.getClanPlayer( m ) )
                      .filter( e -> e != null && e.toPlayer() != null && e.toPlayer().isOnline() )
                      .collect( Collectors.toList() );
    }

    /**
     * Get all leaders in the clan
     *
     * @return the leaders
     */
    public List<ClanPlayer> getLeaders() {
        ClanManager clanMan = plugin.getClanManager();

        return members.parallelStream()
                      .map( m -> clanMan.getClanPlayer( m ) )
                      .filter( e -> e != null && e.isLeader() )
                      .collect( Collectors.toList() );
    }

    /**
     * Get all non-leader players in the clan
     *
     * @return non leaders
     */
    public List<ClanPlayer> getNonLeaders() {
        ClanManager clanMan = plugin.getClanManager();
        List<ClanPlayer> out = new ArrayList<>();

        for (UUID member : members) {
            ClanPlayer cp = clanMan.getClanPlayer(member);
            
            if (cp == null) {
                continue;
            }
            if (!cp.isLeader()) {
                out.add(cp);
            }
        }
        Collections.sort(out);

        return out;
    }

    /**
     * Get all clan's members
     *
     * @return
     */
    public List<ClanPlayer> getAllMembers() {
        List<ClanPlayer> out = new ArrayList<>();

        ClanManager clanMan = plugin.getClanManager();
        for (UUID member : members) {
            ClanPlayer cp = clanMan.getClanPlayer(member);
            
            if (cp == null) continue;

            out.add(cp);
        }
        Collections.sort(out);

        return out;
    }

    /**
     * Get all the ally clan's members
     *
     * @return
     */
    public Set<ClanPlayer> getAllAllyMembers() {
        Set<ClanPlayer> out = new HashSet<>();

        ClanManager clanMan = plugin.getClanManager();
        for (String each : allies) {
            Clan ally = clanMan.getClan(each);

            if (ally != null) {
                out.addAll(ally.getMembers());
            }
        }
        return out;
    }

    /**
     * Gets the clan's total KDR
     *
     * @return
     */
    public float getTotalKDR() {
        if (members.isEmpty()) {
            return 0;
        }

        double totalWeightedKills = 0;
        int totalDeaths = 0;

        ClanManager clanMan = plugin.getClanManager();
        for (UUID member : members) {
            ClanPlayer cp = clanMan.getClanPlayer(member);
            
            if (cp == null) {
                continue;
            }
            totalWeightedKills += cp.getWeightedKills();
            totalDeaths += cp.getDeaths();
        }
        if (totalDeaths == 0) {
            totalDeaths = 1;
        }
        return ((float) totalWeightedKills) / (totalDeaths);
    }

    /**
     * Gets the clan's total KDR
     *
     * @return
     */
    public int getTotalDeaths() {
        int totalDeaths = 0;

        if (members.isEmpty()) {
            return totalDeaths;
        }

        ClanManager clanMan = plugin.getClanManager();
        for (UUID member : members) {
            ClanPlayer cp = clanMan.getClanPlayer(member);
            
            if (cp == null) {
                continue;
            }
            totalDeaths += cp.getDeaths();
        }
        return totalDeaths;
    }

    /**
     * Gets average weighted kills for the clan
     *
     * @return
     */
    public int getAverageWK() {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        ClanManager clanMan = plugin.getClanManager();
        for (UUID member : members) {
            ClanPlayer cp = clanMan.getClanPlayer(member);
            
            if (cp == null) {
                continue;
            }
            total += cp.getWeightedKills();
        }
        return total / getSize();
    }

    /**
     * Gets total rival kills for the clan
     *
     * @return
     */
    public int getTotalRival() {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        ClanManager clanMan = plugin.getClanManager();
        for (UUID member : members) {
            ClanPlayer cp = clanMan.getClanPlayer(member);
            
            if (cp == null) {
                continue;
            }
            total += cp.getRivalKills();
        }
        return total;
    }

    /**
     * Gets total neutral kills for the clan
     *
     * @return
     */
    public int getTotalNeutral() {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        ClanManager clanMan = plugin.getClanManager();
        for (UUID member : members) {
            ClanPlayer cp = clanMan.getClanPlayer(member);
            
            if (cp == null) {
                continue;
            }
            total += cp.getNeutralKills();
        }
        return total;
    }

    /**
     * Gets total civilian kills for the clan
     *
     * @return
     */
    public int getTotalCivilian() {
        ClanManager clanManager = plugin.getClanManager();
        
        return members.parallelStream()
                      .map( m -> clanManager.getClanPlayer( m ) )
                      .filter( cp -> cp != null )
                      .collect( Collectors.summingInt( cp -> cp.getCivilianKills() ) );
    }

    /**
     * Set a clan's cape url
     *
     * @param url
     */
    public void setClanCape(String url) {
        setCapeUrl(url);
        plugin.getStorageManager().updateClan(this);
    }

    /**
     * Check whether the clan has crossed the rival limit
     *
     * @return
     */
    public boolean reachedRivalLimit() {
        int clanCount = plugin.getClanManager().getRivableClanCount() - 1;
        int rivalPercent = plugin.getSettingsManager().getRivalLimitPercent();

        return rivals.size() > clanCount * rivalPercent / 100;
    }

    /**
     * Add a new player to the clan
     *
     * @param cp
     */
    public void addPlayerToClan(ClanPlayer cp) {
        cp.removePastClan(colorTag);
        cp.setClan(this);
        cp.setLeader(false);
        cp.setTrusted(plugin.getSettingsManager().isClanTrustByDefault());

        importMember(cp);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(this);

        // add clan permission
        plugin.getPermissionsManager().addClanPermissions(cp);

        Player player = Bukkit.getPlayer(cp.getUniqueId());
        
        if (player != null) {
            plugin.getClanManager().updateDisplayName(player);
        }
        Bukkit.getPluginManager().callEvent(new PlayerJoinedClanEvent(this, cp));
    }

    /**
     * Remove a player from a clan
     *
     * @param playerUniqueId
     */
    public void removePlayerFromClan(UUID playerUniqueId) {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerUniqueId);

        // remove clan group-permission
        plugin.getPermissionsManager().removeClanPermissions(cp);

        // remove permissions
        plugin.getPermissionsManager().removeClanPlayerPermissions(cp);

        cp.setClan(null);
        cp.addPastClan(getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
        cp.setLeader(false);
        cp.setTrusted(false);
        cp.setJoinDate(0);
        removeMember(playerUniqueId);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(this);

        Player matched = Bukkit.getPlayer(playerUniqueId);

        if (matched != null) {
            plugin.getClanManager().updateDisplayName(matched);
        }
        Bukkit.getPluginManager().callEvent(new PlayerKickedClanEvent(this, cp));
    }

    /**
     * Promote a member to a leader of a clan
     *
     * @param playerUniqueId
     */
    public void promote(UUID playerUniqueId) {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerUniqueId);

        cp.setLeader(true);
        cp.setTrusted(true);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(this);

        // add clan permission
        plugin.getPermissionsManager().addClanPermissions(cp);
        Bukkit.getPluginManager().callEvent(new PlayerPromoteEvent(this, cp));
    }

    /**
     * Demote a leader back to a member of a clan
     *
     * @param playerUniqueId
     */
    public void demote(UUID playerUniqueId) {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerUniqueId);

        cp.setLeader(false);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(this);

        // add clan permission
        plugin.getPermissionsManager().addClanPermissions(cp);
        Bukkit.getPluginManager().callEvent(new PlayerDemoteEvent(this, cp));
    }

    /**
     * Add an ally to a clan, and the clan to the ally
     *
     * @param ally
     */
    public void addAlly(Clan ally) {
        removeRival(ally.tag);
        addAlly(ally.tag);

        ally.removeRival(tag);
        ally.addAlly(tag);

        plugin.getStorageManager().updateClan(this);
        plugin.getStorageManager().updateClan(ally);
        Bukkit.getPluginManager().callEvent(new AllyClanAddEvent(this, ally));
    }

    /**
     * Remove an ally form the clan, and the clan from the ally
     *
     * @param ally
     */
    public void removeAlly(Clan ally) {
        removeAlly(ally.tag);
        ally.removeAlly(tag);

        plugin.getStorageManager().updateClan(this);
        plugin.getStorageManager().updateClan(ally);
        Bukkit.getPluginManager().callEvent(new AllyClanRemoveEvent(this, ally));
    }

    /**
     * Add a rival to the clan, and the clan to the rival
     *
     * @param rival
     */
    public void addRival(Clan rival) {
        removeAlly(rival.tag);
        addRival(rival.tag);

        rival.removeAlly(tag);
        rival.addRival(tag);

        plugin.getStorageManager().updateClan(this);
        plugin.getStorageManager().updateClan(rival);
        Bukkit.getPluginManager().callEvent(new RivalClanAddEvent(this, rival));
    }

    /**
     * Removes a rival from the clan, the clan from the rival
     *
     * @param rival
     */
    public void removeRival(Clan rival) {
        removeRival(rival.tag);
        rival.removeRival(tag);

        plugin.getStorageManager().updateClan(this);
        plugin.getStorageManager().updateClan(rival);
        Bukkit.getPluginManager().callEvent(new RivalClanRemoveEvent(this, rival));
    }

    public void verifyClan() {
        setVerified(true);
        plugin.getStorageManager().updateClan(this);
    }

    public boolean isAnyOnline() {
        return members.parallelStream().anyMatch( m -> Helper.isOnline( m ) );
    }

    /**
     * Check whether all leaders of a clan are online
     *
     * @return
     */
    public boolean allLeadersOnline() {
        return getLeaders().parallelStream().allMatch( l -> Helper.isOnline( l.getUniqueId() ) );
    }

    /**
     * Check whether all leaders, except for the one passed in, are online
     *
     * @param playerUniqueId
     * @return
     */
    public boolean allOtherLeadersOnline(UUID playerUniqueId) {
        return getLeaders().parallelStream().map( l -> l.getUniqueId() )
                                            .filter( u -> u.equals( playerUniqueId ) )
                                            .allMatch( u -> Helper.isOnline( u ) );
    }

    /**
     * Change a clan's tag
     *
     * @param tag
     */
    public void changeClanTag(String _tag) {
        setColorTag(_tag);
        plugin.getStorageManager().updateClan(this);
    }

    /**
     * Announce message to a whole clan
     *
     * @param playerName
     * @param msg
     */
    public void clanAnnounce(String playerName, String msg) {
        String message = plugin.getSettingsManager().getClanChatAnnouncementColor() + msg;

        for (ClanPlayer cp : getMembers()) {
            Player pl = cp.toPlayer();

            if (pl != null) {
                ChatBlock.sendMessage(pl, message);
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[" + plugin.getLang("clan.announce") + ChatColor.AQUA + "] " + ChatColor.AQUA + "[" + Helper.getColorName(playerName) + ChatColor.WHITE + "] " + message);
    }

    /**
     * Announce message to a all the leaders of a clan
     *
     * @param msg
     */
    public void leaderAnnounce(String msg) {
        String message = plugin.getSettingsManager().getClanChatAnnouncementColor() + msg;

        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer cp : leaders) {
            Player pl = cp.toPlayer();

            if (pl != null) {
                ChatBlock.sendMessage(pl, message);
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[" + plugin.getLang("leader.announce") + ChatColor.AQUA + "] " + ChatColor.WHITE + message);
    }

    /**
     * Add a new bb message and announce it to all online members of a clan
     *
     * @param announcerName
     * @param msg
     */
    public void addBb(String announcerName, String msg) {
        if (verified) {
            SettingsManager settings = plugin.getSettingsManager();
            addBb(settings.getBbColor() + msg);
            clanAnnounce(announcerName, settings.getBbAccentColor() + "* " + settings.getBbColor() + Helper.parseColors(msg));
        }
    }

    /**
     * Displays bb to a player
     *
     * @param player
     */
    public void displayBb(Player player) {
        if (verified) {
            SettingsManager settings = plugin.getSettingsManager();
            ChatBlock.sendBlank(player);
            ChatBlock.saySingle(player, MessageFormat.format(plugin.getLang("bulletin.board.header"), settings.getBbAccentColor(), settings.getPageHeadingsColor(), Helper.capitalize(getName())));

            int maxSize = settings.getBbSize();

            while (bb.size() > maxSize) {
                bb.remove(0);
            }

            for (String msg : bb) {
                ChatBlock.sendMessage(player, settings.getBbAccentColor() + "* " + settings.getBbColor() + Helper.parseColors(msg));
            }
            ChatBlock.sendBlank(player);
        }
    }

    /**
     * Disband a clan
     */
    public void disband() {
        Bukkit.getPluginManager().callEvent(new DisbandClanEvent(this));
        Collection<ClanPlayer> clanPlayers = plugin.getClanManager().getAllClanPlayers();
        List<Clan> clans = plugin.getClanManager().getClans();

        for (ClanPlayer cp : clanPlayers) {
            if (cp.getTag().equals(tag)) {
                plugin.getPermissionsManager().removeClanPermissions(this);
                cp.setClan(null);

                if (isVerified()) {
                    cp.addPastClan(getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
                }
                cp.setLeader(false);
                plugin.getStorageManager().updateClanPlayer(cp);
            }
        }
        clans.remove(this);

        for (Clan c : clans) {
            String disbanded = plugin.getLang("clan.disbanded");

            if (c.removeWarringClan(this)) {
                c.addBb(disbanded, ChatColor.AQUA + MessageFormat.format(plugin.getLang("you.are.no.longer.at.war"), Helper.capitalize(c.getName()), getColorTag()));
            }

            if (c.removeRival(getTag())) {
                c.addBb(disbanded, ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.disbanded.rivalry.ended"), Helper.capitalize(getName())));
            }

            if (c.removeAlly(getTag())) {
                c.addBb(disbanded, ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.disbanded.alliance.ended"), Helper.capitalize(getName())));
            }
        }

        final Clan thisOne = this;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                plugin.getClanManager().removeClan(thisOne.getTag());
                plugin.getStorageManager().deleteClan(thisOne);
            }
        }, 1);
    }

    public boolean isUnrivable() {
        return plugin.getSettingsManager().isUnrivable(tag);
    }

    public boolean isWarring(Clan clan) {
        return warringClans.contains(clan);
    }

    public void addWarringClan(Clan clan) {
        warringClans.add( clan );
        plugin.getStorageManager().updateClan(this);
    }

    public boolean removeWarringClan(Clan clan) {
        if (warringClans.remove(clan)) {
            plugin.getStorageManager().updateClan(this);
            return true;
        }
        return false;
    }

    public List<Clan> getWarringClans() {
        return new ArrayList<>(warringClans);
    }

    /**
     * Return the list of flags and their data as a json string
     *
     * @return the flags
     */
    public String getFlags() {
        JSONObject json = new JSONObject();

        // writing the list of flags to json

        JSONArray warring = new JSONArray();
        warring.add(warringClans.parallelStream().map( c -> c.getName() ).toArray());

        json.put("warring", warring);
        json.put("homeX", homeX);
        json.put("homeY", homeY);
        json.put("homeZ", homeZ);
        json.put("homeWorld", homeWorld == null ? "" : homeWorld);

        return json.toString();
    }

    /**
     * Read the list of flags in from a json string
     *
     * @param flagString the flags to set
     */
    public void setFlags(String flagString) {
        if (flagString != null && !flagString.isEmpty()) {
            JSONObject flags = (JSONObject) JSONValue.parse(flagString);

            if (flags != null) {
                for (Object flag : flags.keySet()) {
                    // reading the list of flags from json

                    try {
                        if (flag.equals("warring")) {
                            JSONArray clans = (JSONArray) flags.get(flag);

                            if (clans != null) {
                                for (Object eachTag : clans) {
                                    SimpleClans.debug("warring added: " + eachTag.toString());
                                    warringClans.add(plugin.getClanManager().getClan( (String) eachTag ));
                                }
                            }
                        }
                        if (flag.equals("homeX")) {
                            homeX = ((Long) flags.get(flag)).intValue();
                        }
                        if (flag.equals("homeY"))  {
                            homeY = ((Long) flags.get(flag)).intValue();
                        }
                        if (flag.equals("homeZ")) {
                            homeZ = ((Long) flags.get(flag)).intValue();
                        }
                        if (flag.equals("homeWorld")) {
                            homeWorld = (String) flags.get(flag);
                        }
                    }
                    catch (Exception ex) {
                        for (StackTraceElement el : ex.getStackTrace()) {
                            System.out.print("Failed reading flag: " + flag);
                            System.out.print("Value: " + flags.get(flag));
                            System.out.print(el.toString());
                        }
                    }
                }
            }
        }
    }

    public void validateWarring() {
        warringClans.removeIf( c -> !plugin.getClanManager().getClans().contains( c ) );
    }

    public void setHomeLocation(Location home) {
        if (home == null) {
            homeY = 0;
            homeX = 0;
            homeZ = 0;
            homeWorld = null;
        }
        else {
            home.setY(home.getBlockY() + 1);

            homeX = home.getBlockX();
            homeY = home.getBlockY();
            homeZ = home.getBlockZ();
            homeWorld = home.getWorld().getName();
        }
        plugin.getStorageManager().updateClan(this);
    }

    public Location getHomeLocation() {
        World world = Bukkit.getWorld(homeWorld);

        if (world != null) {
            if (!(world.getBlockAt(homeX, homeY, homeZ).getType().equals(Material.AIR)) || !(world.getBlockAt(homeX, homeY + 1, homeZ).getType().equals(Material.AIR)) || homeY == 0) {
                return new Location(world, homeX, world.getHighestBlockYAt(homeX, homeZ), homeZ);
            }
            return new Location(world, homeX, homeY, homeZ);

        }
        return null;
    }

    public String getTagLabel(boolean isLeader) {
        
        SettingsManager setMan = plugin.getSettingsManager();

        if (isLeader) {
            return setMan.getTagBracketLeaderColor() + setMan.getTagBracketLeft() +
                    setMan.getTagDefaultColor() + colorTag +
                    setMan.getTagBracketLeaderColor() + setMan.getTagBracketRight() +
                    setMan.getTagSeparatorLeaderColor() + setMan.getTagSeparator();
        }
        return setMan.getTagBracketColor() + setMan.getTagBracketLeft() +
                setMan.getTagDefaultColor() + colorTag +
                setMan.getTagBracketColor() + setMan.getTagBracketRight() +
                setMan.getTagSeparatorColor() + setMan.getTagSeparator();
    }

    public boolean isAllowWithdraw() {
        return allowWithdraw;
    }
    public void setAllowWithdraw(boolean _allowWithdraw) {
        allowWithdraw = _allowWithdraw;
    }
    public boolean isAllowDeposit() {
        return allowDeposit;
    }
    public void setAllowDeposit(boolean _allowDeposit) {
        allowDeposit = _allowDeposit;
    }
}
