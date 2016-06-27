package net.sacredlabyrinth.phaed.simpleclans;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDFetcher;

/**
 * @author phaed
 */
public class Helper {
	
    private static SimpleClans plugin = SimpleClans.getInstance();
    
	private Helper() {}

    /**
     * Get a players full color name if he is online
     *
     * @param playerName
     * @return
     */
    public static String getColorName(String playerName) {
        List<Player> players = Bukkit.matchPlayer(playerName);

        if (players.size() == 1) {
            return plugin.getPermissionsManager().getPrefix(players.get(0)) 
                    + players.get(0).getName() 
                    + plugin.getPermissionsManager().getSuffix(players.get(0));
        }
        return playerName;
    }

    /**
     * Capitalize first word of sentence
     *
     * @param content
     * @return
     */
    public static String capitalize(String content) {
        if (content.length() < 2) {
            return content;
        }
        String first = content.substring(0, 1).toUpperCase();
        return first + content.substring(1);
    }

    /**
     * Hex value to ChatColor
     *
     * @param hexValue
     * @return
     */
    public static String toColor(String hexValue) {
        if (hexValue == null) {
            return "";
        }
        return ChatColor.getByChar(hexValue).toString();
    }

    /**
     * Converts string array to ArrayList<String>, remove empty strings
     *
     * @param values
     * @return
     */
    public static List<String> fromArray(String... values) {
        List<String> results = new ArrayList<>();
        Collections.addAll(results, values);
        results.remove("");
        return results;
    }

    /**
     * Converts string array to HashSet<String>, remove empty strings
     *
     * @param values
     * @return
     */
    public static Set<String> fromArray2(String... values) {
        HashSet<String> results = new HashSet<>();
        Collections.addAll(results, values);
        results.remove("");
        return results;
    }

    /**
     * Converts ArrayList<String> to string array
     */
    public static String[] toArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    /**
     * Removes first item from a string array
     */
    public static String[] removeFirst(String[] args) {
        List<String> out = fromArray(args);

        if (!out.isEmpty()) {
            out.remove(0);
        }
        return toArray(out);
    }

    /**
     * Converts a string array to a space separated string
     */
    public static String toMessage(String[] args) {
        return String.join( " ", args );
    }

    /**
     * Converts a string array to a string with custom separators
     *
     * @param args
     * @param sep
     * @return
     */
    public static String toMessage(String[] args, String sep) {
        return String.join( sep, args );
    }

    /**
     * Converts a string array to a string with custom separators
     *
     * @param args
     * @param sep
     * @return
     */
    public static String toMessage(List<String> args, String sep) {
        return String.join( sep, args );
    }
    
    /**
     * Converts a string array to a string with custom separators
     *
     * @param args
     * @param sep
     * @return
     */
    public static String toMessage(Set<String> args, String sep) {
        return String.join( sep, args );
    }
    
    /**
     * Convert color hex values with ampersand to special character
     *
     * @param msg
     * @return
     */
    public static String parseColors(String msg) {
        return msg.replace("&", "\u00a7");
    }

    /**
     * Removes color codes from strings
     *
     * @param msg
     * @return
     */
    public static String stripColors(String msg) {
        String out = msg.replaceAll("[&][0-9a-f]", "");
        out = out.replaceAll(String.valueOf((char) 194), "");
        return out.replaceAll("[\u00a7][0-9a-f]", "");
    }

    /**
     * Retrieves the last color code @param msg @return
     */
    public static String getLastColorCode(String msg) {
        msg = msg.replaceAll(String.valueOf((char) 194), "").trim();

        if (msg.length() < 2) {
            return "";
        }

        String one = msg.substring(msg.length() - 2, msg.length() - 1);
        String two = msg.substring(msg.length() - 1);

        if (one.equals("\u00a7")) {
            return one + two;
        }

        if (one.equals("&")) {
            return Helper.toColor(two);
        }
        return "";
    }

    /**
     * Cleans up the tag from color codes and makes it lowercase
     */
    public static String cleanTag(String tag) {
        return stripColors(tag).toLowerCase();
    }

    /**
     * Removes trailing separators
     */
    public static String stripTrailing(String msg, String sep) {
        if (msg.length() < sep.length()) {
            return msg;
        }

        String out = msg;
        String first = msg.substring(0, sep.length());
        String last = msg.substring(msg.length() - sep.length(), msg.length());

        if (first.equals(sep)) {
            out = msg.substring(sep.length());
        }

        if (last.equals(sep)) {
            out = msg.substring(0, msg.length() - sep.length());
        }
        return out;
    }

    /**
     * Generates page separator line
     */
    public static String generatePageSeparator(String sep) {
        String out = "";

        for (int i = 0; i < 320; i++) {
            out += sep;
        }
        return out;
    }

    /**
     * Check whether a player is online
     */
    public static boolean isOnline(UUID playerUniqueId) { 
        return Bukkit.getPlayer( playerUniqueId ) != null;
    }

    /**
     * Remove offline players from a ClanPlayer array
     *
     * @param in
     * @return
     */
    public static Set<ClanPlayer> stripOffLinePlayers(List<ClanPlayer> in) {
        return in.parallelStream().filter( cp -> { return cp.toPlayer() != null; } ).collect(Collectors.toSet());
    }

    /**
     * Test if a url is valid
     *
     * @param strUrl
     * @return
     */
    public static boolean testURL(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
        }
        catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Escapes single quotes
     *
     * @param str
     * @return
     */
    public static String escapeQuotes(String str) {
        if (str == null)  {
            return "";
        }
        return str.replace("'", "''");
    }

    /**
     * Returns a prettier coordinate, does not include world
     *
     * @param loc
     * @return
     */
    public static String toLocationString(Location loc) {
        return loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " " + loc.getWorld().getName();
    }

    /**
     * Whether the two locations refer to the same block
     *
     * @param loc
     * @param loc2
     * @return
     */
    public static boolean isSameBlock(Location loc, Location loc2) {
        return loc.getBlockX() == loc2.getBlockX() && loc.getBlockY() == loc2.getBlockY() && loc.getBlockZ() == loc2.getBlockZ();
    }

    /**
     * Sort hashmap by value

     */
    public static Map sortByValue(Map map) {
        
        List list = new LinkedList(map.entrySet());
        
        Collections.sort(list, (o1, o2) -> {
            return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());     
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); )  {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static boolean isVanished(Player player) {
        if (player != null && player.hasMetadata("vanished") && !player.getMetadata("vanished").isEmpty()) {
        	return player.getMetadata("vanished").get(0).asBoolean();
        }
        return false;
    }

    public static Player getPlayer(String playerName) {
        return Bukkit.getPlayer( getCachedPlayerUUID(playerName));
    }

    public static UUID getCachedPlayerUUID(String playerName) {
        Player OnlinePlayer = Bukkit.getPlayerExact(playerName);
    
        if (OnlinePlayer != null) {
            return OnlinePlayer.getUniqueId();
        }
        
        for ( OfflinePlayer each : Bukkit.getOfflinePlayers() ) {
            if ( each.getName().equalsIgnoreCase( playerName ) )
                return each.getUniqueId();
        }
        
        try {
            return UUIDFetcher.getUUIDOf(playerName);
            
        } catch (Exception ex) {

            OfflinePlayer OfflinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (OfflinePlayer != null) {
                return OfflinePlayer.getUniqueId();
            }
            return null;
        }
    }
}
