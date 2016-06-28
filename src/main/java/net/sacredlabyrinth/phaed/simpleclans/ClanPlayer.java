package net.sacredlabyrinth.phaed.simpleclans;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class ClanPlayer implements Serializable, Comparable<ClanPlayer> {
    private static final long serialVersionUID = 1L;
    private UUID uniqueId;
    private String displayName;
    private boolean leader;
    private boolean trusted;
    private String tag;
    private Clan clan;
    private boolean friendlyFire;
    private int neutralKills;
    private int rivalKills;
    private int civilianKills;
    private int deaths;
    private long lastSeen;
    private long joinDate;
    private Set<String> pastClans = new HashSet<>();
    private Map<Request, VoteResult> vote = new HashMap<>();
    private Channel channel;
    private static SimpleClans plugin = SimpleClans.getInstance();

    private boolean useChatShortcut = false;
    private boolean globalChat = true;
    private boolean allyChat = true;
    private boolean clanChat = true;
    private boolean bbEnabled = true;
    private boolean tagEnabled = true;
    private boolean capeEnabled = true;

    private boolean allyChatMute = false;
    private boolean clanChatMute = false;

    private String rank = "";

    public enum Channel { CLAN, ALLY, NONE }
    
    public ClanPlayer() {
        tag = "";
        channel = Channel.NONE;
    }

    public ClanPlayer(UUID playerUniqueId) {
        uniqueId = playerUniqueId;
        lastSeen = System.currentTimeMillis();
        joinDate = System.currentTimeMillis();
        neutralKills = 0;
        rivalKills = 0;
        civilianKills = 0;
        tag = "";
        channel = Channel.NONE;

        Player player = Bukkit.getPlayer(playerUniqueId);
        
        if (player != null) {
            displayName = player.getName();
        } else {
            displayName = Bukkit.getOfflinePlayer(playerUniqueId).getName();
        }
    }

    @Override
    public int hashCode() {
        return displayName.hashCode() >> 13;
    }
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClanPlayer && ((ClanPlayer) obj).displayName.equals(displayName);
    }
    @Override
    public int compareTo(ClanPlayer other) {        
        return uniqueId.compareTo(other.uniqueId);
    }
    @Override
    public String toString() { return displayName; }
    public String getName() { return displayName; }
    public UUID getUniqueId() { return uniqueId; }
    /**
     * Returns the clean name for this player (lowercase)
     *
     * @return the name
     */
    public String getCleanName() { return displayName.toLowerCase(); }
    public void setName(String name) { displayName = name; }
    public void setUniqueId(UUID _uniqueId) { uniqueId = _uniqueId; }
    public boolean isLeader() { return leader; }
    
    /**
     * Sets this player as a leader (does not update clanplayer to db)
     *
     * @param _leader the leader to set
     */
    public void setLeader(boolean _leader) {
        if (_leader) {
            trusted = _leader;
        }
        leader = _leader;
    }

    /**
     * Check whether the player is an ally with another player
     *
     * @param player
     * @return
     */
    public boolean isAlly(Player player) {
        ClanPlayer allycp = plugin.getClanManager().getClanPlayer(player);

        return allycp != null && allycp.clan.isAlly(tag);
    }

    /**
     * Check whether the player is an rival with another player
     *
     * @param player
     * @return
     */
    public boolean isRival(Player player) {
        ClanPlayer allycp = plugin.getClanManager().getClanPlayer(player);

        return allycp != null && allycp.clan.isRival(tag);
    }

    /**
     * Returns the last seen date for this player in milliseconds
     *
     * @return the lastSeen
     */
    public long getLastSeen() { return lastSeen; }
    public void setLastSeen(long _lastSeen) { lastSeen = _lastSeen; }
    /**
     * Updates last seen date to today
     */
    public void updateLastSeen() { lastSeen = System.currentTimeMillis(); }
    
    /**
     * Returns a verbal representation of how many days ago a player was last seen
     *
     * @return
     */
    public String getLastSeenDaysString() {
        double days = getLastSeenDays();

        if (days < 1) 
            return plugin.getLang("today");
        else if (Math.round(days) == 1) 
            return MessageFormat.format(plugin.getLang("1.color.day"), ChatColor.GRAY);
        else 
            return MessageFormat.format(plugin.getLang("many.color.days"), Math.round(days), ChatColor.GRAY);
    }

    public double getLastSeenDays() {
        return Dates.differenceInDays(lastSeen, System.currentTimeMillis());
    }

    public int getRivalKills() { return rivalKills; }
    public void setRivalKills(int _rivalKills) { rivalKills = _rivalKills; }
    /**
     * Adds one rival kill to this player (does not update clanplayer to db)
     */
    public void addRivalKill() { rivalKills += 1; }
    public int getCivilianKills() { return civilianKills; }
    public void setCivilianKills(int _civilianKills) { civilianKills = _civilianKills; }
    /**
     * Adds one civilian kill to this player (does not update clanplayer to db)
     */
    public void addCivilianKill() { civilianKills += 1; }
    public int getNeutralKills() { return neutralKills; }
    public void setNeutralKills(int _neutralKills) { neutralKills = _neutralKills;  }
    /**
     * Adds one civilian kill to this player (does not update clanplayer to db)
     */
    public void addNeutralKill() { neutralKills += 1; }
    public boolean isFriendlyFire() { return friendlyFire; }
    /**
     * Sets whether this player is allowing friendly fire (does not update clanplayer to db)
     *
     * @param _friendlyFire the friendlyFire to set
     */
    public void setFriendlyFire(boolean _friendlyFire) { friendlyFire = _friendlyFire; }
    public VoteResult getVote(Request req) { return vote.get( req ); }
    public void setVote(VoteResult _vote, Request req) { vote.put( req, _vote ); }
    public boolean hasVote(Request req) { return vote.containsKey( req ); }
    public void clearVote(Request req) { vote.remove( req ); }
    public int getDeaths() { return deaths; }
    public void setDeaths(int _deaths) { deaths = _deaths; }
    /**
     * Adds one death to this player  (does not update clanplayer to db)
     */
    public void addDeath() { deaths += 1; }
    
    /**
     * Returns weighted kill score for this player (kills multiplied by the different weights)
     *
     * @return
     */
    public double getWeightedKills()  {
        SettingsManager settings = plugin.getSettingsManager();
        return (rivalKills * settings.getKwRival()) 
                + (neutralKills * settings.getKwNeutral()) 
                + (civilianKills * settings.getKwCivilian());
    }
    /**
     * Returns weighted-kill/death ratio
     *
     * @return
     */
    public float getKDR() {
        int totalDeaths = deaths;

        if (totalDeaths == 0)  {
            totalDeaths = 1;
        }
        return ((float) getWeightedKills()) / (totalDeaths);
    }

    /**
     * Returns the player's join date to his current clan in milliseconds, 0 if not in a clan
     *
     * @return the joinDate
     */
    public long getJoinDate() { return joinDate; }
    public void setJoinDate(long _joinDate) { joinDate = _joinDate; }

    /**
     * Returns a string representation of the join date, blank if not in a clan
     *
     * @return
     */
    public String getJoinDateString() {
        if (joinDate == 0) {
            return "";
        }
        return new SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(joinDate));
    }

    /**
     * Returns a string representation of the last seen date
     *
     * @return
     */
    public String getLastSeenString() {
        return new SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(lastSeen));
    }

    /**
     * Returns the number of days the player has been inactive
     *
     * @return
     */
    public int getInactiveDays() {
        return (int) Math.floor(Dates.differenceInDays(lastSeen, System.currentTimeMillis()));
    }

    public String getPackedPastClans() { return String.join( "|", pastClans ); }
    public void setPackedPastClans(String PackedPastClans) {
        pastClans = Helper.fromArray2(PackedPastClans.split("[|]"));
    }

    /**
     * Adds a past clan to the player (does not update the clanplayer to db)
     *
     * @param tag
     */
    public void addPastClan(String _tag) { pastClans.add(_tag); }

    /**
     * Removes a past clan from the player (does not update the clanplayer to db)
     *
     * @param tag is the clan's colored tag
     */
    public void removePastClan(String _tag) { pastClans.remove(_tag); }

    /**
     * Returns a separator delimited string with the color tags for all past clans this player has been in
     *
     * @param sep
     * @return
     */
    public String getPastClansString(String sep) {
        String out = String.join( sep, pastClans );

        return out.trim().isEmpty() ? plugin.getLang("none") : out;
    }

    public Clan getClan() { return clan; }

    public void setClan(Clan _clan) {
        if (_clan == null) 
            tag = "";
        else 
            tag = _clan.getTag();
        clan = _clan;
    }

    /**
     * Returns this player's clan's tag.  Empty string if he's not in a clan.
     *
     * @return the tag
     */
    public String getTag() { return tag; }
    /**
     * Returns this player's clan's tag label.  Empty string if he's not in a clan.
     *
     * @return the tag
     */
    public String getTagLabel() {
        return clan == null ? "" : clan.getTagLabel(leader);
    }
    /**
     * Returns this player's trusted status
     *
     * @return the trusted
     */
    public boolean isTrusted() { return leader || trusted; }
    /**
     * Sets this player's trusted status (does not update the clanplayer to db)
     *
     * @param _trusted the trusted to set
     */
    public void setTrusted(boolean _trusted) { trusted = _trusted; }

    /**
     * Return the list of flags and their data as a json string
     *
     * @return the flags
     */
    @SuppressWarnings( "unchecked" )
    public String getFlags() {
        JSONObject json = new JSONObject();

        // the player's rank inside his clan
        if (rank != null) {
            json.put("rank", rank);
        }

        // writing the list of flags to json
        json.put("channel", channel.toString());

        // writing the channel state settings flags
        List<Boolean> settings = new LinkedList<>();
        settings.add(globalChat);
        settings.add(allyChat);
        settings.add(clanChat);

        json.put("channel-state", settings);

        // couple of toggles
        json.put("chat-shortcut", useChatShortcut);
        json.put("bb-enabled", bbEnabled);
        json.put("hide-tag", tagEnabled);
        json.put("cape-enabled", capeEnabled);

        return json.toString();
    }

    /**
     * Read the list of flags in from a json string
     *
     * @param flagString the flags to set
     */
    public void setFlags(String flagString) {
        if (flagString != null && !flagString.isEmpty()) {
            Object obj = JSONValue.parse(flagString);
            JSONObject flags = (JSONObject) obj;

            if (flags != null) {
                for (Object flag : flags.keySet()) {
                    try {
                        if (flag.equals("rank")) {
                            if(flags.get(flag) != null) {
                                rank = flags.get(flag).toString();
                            }
                        }
                        else if (flag.equals("channel")) {
                            String chn = flags.get(flag).toString();

                            if (chn != null && !chn.isEmpty()) {
                                if (chn.equalsIgnoreCase("clan")) 
                                    channel = Channel.CLAN;
                                else if (chn.equalsIgnoreCase("ally")) 
                                    channel = Channel.ALLY;
                                else 
                                    channel = Channel.NONE;
                            }
                        }
                        else if (flag.equals("channel-state")) {
                            JSONArray settings = (JSONArray) flags.get(flag);

                            if (settings != null && !settings.isEmpty()) {
                                globalChat = (Boolean) settings.get(0);
                                allyChat = (Boolean) settings.get(1);
                                clanChat = (Boolean) settings.get(2);
                            }
                        }
                        else if (flag.equals("bb-enabled")) 
                            bbEnabled = (Boolean) flags.get(flag);
                        else if (flag.equals("hide-tag")) 
                            tagEnabled = (Boolean) flags.get(flag);
                        else if (flag.equals("cape-enabled")) 
                            capeEnabled = (Boolean) flags.get(flag);
                        else if (flag.equals("chat-shortcut")) 
                            useChatShortcut = (Boolean) flags.get(flag);
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
    
    public void setBbEnabled(boolean _bbEnabled) {
        bbEnabled = _bbEnabled;
        plugin.getStorageManager().updateClanPlayer(this);
    }
    public void setCapeEnabled(boolean _capeEnabled) {
        capeEnabled = _capeEnabled;
        plugin.getStorageManager().updateClanPlayer(this);
    }
    public void setTagEnabled(boolean _tagEnabled) {
        tagEnabled = _tagEnabled;
        plugin.getStorageManager().updateClanPlayer(this);
        plugin.getClanManager().updateDisplayName(this.toPlayer());
    }

    public Channel getChannel() { return channel; }
    public boolean isGlobalChat() { return globalChat; }
    public boolean isAllyChat() { return allyChat; }
    public boolean isClanChat() { return clanChat;  }
    public void setGlobalChat(boolean _globalChat) { globalChat = _globalChat; }
    public void setAllyChat(boolean _allyChat) { allyChat = _allyChat; }
    public void setClanChat(boolean _clanChat) { clanChat = _clanChat; }
    public void setChannel(Channel _channel) { channel = _channel; }
    public boolean isBbEnabled() { return bbEnabled; }
    public boolean isCapeEnabled() { return capeEnabled; }
    public boolean isTagEnabled() { return tagEnabled; }   
    public boolean isUseChatShortcut() { return useChatShortcut; }
    public String getRank() { return rank; }
    public void setRank(String _rank) { rank = _rank; } 
    public Player toPlayer() { return Bukkit.getPlayer(uniqueId); }
    public void setMuted(boolean b) { clanChatMute = b; }
    public void setMutedAlly(boolean b) { allyChatMute = b; }
    public boolean isMuted() { return clanChatMute; }
    public boolean isMutedAlly() { return allyChatMute; }
}
