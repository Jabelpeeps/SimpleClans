package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;

/**
 * @author phaed
 */
public final class ClanManager {

    private SimpleClans plugin = SimpleClans.getInstance();
    private HashMap<String, Clan> clans = new HashMap<>();
    private HashMap<UUID, ClanPlayer> clanPlayers = new HashMap<>();

    /**
     * Deletes all clans and clan players in memory
     */
    public void cleanData() {
        clans.clear();
        clanPlayers.clear();
    }

    /**
     * Import a clan into the in-memory store
     *
     * @param clan
     */
    public void importClan(Clan clan) {
        clans.put(clan.getTag(), clan);
    }

    /**
     * Import a clan player into the in-memory store
     *
     * @param cp
     */
    public void importClanPlayer(ClanPlayer cp) {      
        if (cp.getUniqueId() != null) {
            clanPlayers.put(cp.getUniqueId(), cp);
        }
    }

    /**
     * Create a new clan
     *
     * @param player
     * @param colorTag
     * @param name
     */
    public void createClan(Player player, String colorTag, String name) {
        ClanPlayer cp = getCreateClanPlayer(player.getUniqueId());

        boolean verified = !plugin.getSettingsManager().isRequireVerification() || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

        Clan clan = new Clan(colorTag, name, verified);
        clan.addPlayerToClan(cp);
        cp.setLeader(true);

        plugin.getStorageManager().insertClan(clan);
        importClan(clan);
        plugin.getStorageManager().updateClanPlayer(cp);

        plugin.getPermissionsManager().updateClanPermissions(clan);

        Bukkit.getPluginManager().callEvent(new CreateClanEvent(clan));
    }

    /**
     * Delete a players data file
     *
     * @param cp
     */
    public void deleteClanPlayer(ClanPlayer cp) {
        clanPlayers.remove(cp.getUniqueId());
        plugin.getStorageManager().deleteClanPlayer(cp);
    }

    /**
     * Delete a player data from memory
     *
     * @param playerUniqueId
     */
    public void deleteClanPlayerFromMemory(UUID playerUniqueId) {
        clanPlayers.remove(playerUniqueId);
    }

    /**
     * Remove a clan from memory
     *
     * @param tag
     */
    public void removeClan(String tag) {
        clans.remove(tag);
    }

    /**
     * Whether the tag belongs to a clan
     *
     * @param tag
     * @return
     */
    public boolean isClan(String tag) {
        return clans.containsKey(Helper.cleanTag(tag));

    }

    /**
     * Returns the clan the tag belongs to
     *
     * @param tag
     * @return
     */
    public Clan getClan(String tag) {
        return clans.get(Helper.cleanTag(tag));
    }

    /**
     * Get a player's clan
     *
     * @param playerUniqueId
     * @return null if not in a clan
     */
    public Clan getClanByPlayerUniqueId(UUID playerUniqueId) {
        ClanPlayer cp = getClanPlayer(playerUniqueId);

        if (cp != null) {
            return cp.getClan();
        }
        return null;
    }

    /**
     * @return the clans
     */
    public List<Clan> getClans() {
        return new ArrayList<>(clans.values());
    }

    /**
     * Returns the collection of all clan players, including the disabled ones
     *
     * @return
     */
    public List<ClanPlayer> getAllClanPlayers() {
        return new ArrayList<>(clanPlayers.values());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     * Used for BungeeCord Reload ClanPlayer and your Clan
     *
     * @param player
     * @return
     */
    public ClanPlayer getClanPlayerJoinEvent(Player player) {
        plugin.getStorageManager().importFromDatabaseOnePlayer(player);
        return getClanPlayer(player.getUniqueId());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     *
     * @param player
     * @return
     */
    public ClanPlayer getClanPlayer(Player player) {
        return getClanPlayer(player.getUniqueId());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer getClanPlayer(UUID playerUniqueId) {
        ClanPlayer cp = clanPlayers.get(playerUniqueId);

        if ( cp == null || cp.getClan() == null ) {
            return null;
        }
        return cp;
    }

    /**
     * Gets the ClanPlayer data object for the player, will retrieve disabled
     * clan players as well, these are players who used to be in a clan but are
     * not currently in one, their data file persists and can be accessed. their
     * clan will be null though.
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer getAnyClanPlayer(UUID playerUniqueId) {
        return clanPlayers.get(playerUniqueId);
    }

    /**
     * Gets the ClanPlayer object for the player, creates one if not found
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer getCreateClanPlayer(UUID playerUniqueId) {
        if (clanPlayers.containsKey(playerUniqueId)) {
            return clanPlayers.get(playerUniqueId);
        }
        ClanPlayer cp = new ClanPlayer(playerUniqueId);

        plugin.getStorageManager().insertClanPlayer(cp);
        importClanPlayer(cp);

        return cp;
    }

    /**
     * Announce message to the server
     *
     * @param msg
     */
    public void serverAnnounce(String msg) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (Player player : players) {
            ChatBlock.sendMessage(player, ChatColor.DARK_GRAY + "* " + ChatColor.AQUA + msg);
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[" + plugin.getLang("server.announce") + "] " + ChatColor.WHITE + msg);
    }

    /**
     * Update the players display name with his clan's tag
     *
     * @param player
     */
    public void updateDisplayName(Player player) {
        // do not update displayname if in compat mode

        if (player == null || plugin.getSettingsManager().isCompatMode()) {
            return;
        }
        
        if (plugin.getSettingsManager().isChatTags()) {
            String prefix = plugin.getPermissionsManager().getPrefix(player);
//            String suffix = plugin.getPermissionsManager().getSuffix(player);
            String lastColor = plugin.getSettingsManager().isUseColorCodeFromPrefix() ? Helper.getLastColorCode(prefix) : ChatColor.WHITE + "";
            String fullName = player.getName();

            ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());

            if (cp == null) {
                return;
            }
            if (cp.isTagEnabled()) {
                Clan clan = cp.getClan();

                if (clan != null) {
                    fullName = clan.getTagLabel(cp.isLeader()) + lastColor + fullName + ChatColor.WHITE;
                }
                player.setDisplayName(fullName);
            }
            else player.setDisplayName(lastColor + fullName + ChatColor.WHITE);
        }
    }

    /**
     * Process a player and his clan's last seen date
     *
     * @param player
     */
    public void updateLastSeen(Player player) {
        ClanPlayer cp = getAnyClanPlayer(player.getUniqueId());
        StorageManager stor = plugin.getStorageManager();
        
        if (cp != null) {
            cp.updateLastSeen();
            stor.updateClanPlayerAsync(cp);

            Clan clan = cp.getClan();

            if (clan != null) {
                clan.updateLastUsed();
                stor.updateClanAsync(clan);
            }
        }
    }

    public void ban(Player player) {
        ClanPlayer cp = getClanPlayer(player);
        if ( cp == null ) return;
        Clan clan = cp.getClan();

        if (clan != null) {
            if (clan.getSize() == 1) {
                clan.disband();
            }
            else {
                cp.setClan(null);
                cp.addPastClan(clan.getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
                cp.setLeader(false);
                cp.setJoinDate(0);
                clan.removeMember(player.getUniqueId());

                plugin.getStorageManager().updateClanPlayer(cp);
                plugin.getStorageManager().updateClan(clan);
            }
        }
        plugin.getSettingsManager().addBanned(player.getUniqueId());
    }

    public int getRivableClanCount() {
        SettingsManager settings = plugin.getSettingsManager();
        return (int) clans.values()
                          .parallelStream()
                          .filter( c -> !settings.isUnrivable( c.getTag() ) )
                          .count();
    }

    /**
     * Returns a formatted string detailing the players armor
     *
     * @param inv
     * @return
     */
    public String getArmorString(PlayerInventory inv) {
        String out = "";

        ItemStack h = inv.getHelmet();

        if (h != null) {
            String helmet = plugin.getLang("armor.h");
            switch (h.getType()) {
                case CHAINMAIL_HELMET:
                    out += ChatColor.WHITE + helmet; break;
                case DIAMOND_HELMET:
                    out += ChatColor.AQUA + helmet; break;
                case GOLD_HELMET:
                    out += ChatColor.YELLOW + helmet; break;
                case IRON_HELMET:
                    out += ChatColor.GRAY + helmet; break;
                case LEATHER_HELMET:
                    out += ChatColor.GOLD + helmet; break;
                default:
                    out += ChatColor.BLACK + helmet; break;                
            }
        }
        ItemStack c = inv.getChestplate();

        if (c != null) {
            String chestplate = plugin.getLang("armor.c");
            switch ( c.getType()) {
                case CHAINMAIL_CHESTPLATE:
                    out += ChatColor.WHITE + chestplate; break;
                case DIAMOND_CHESTPLATE:
                    out += ChatColor.AQUA + chestplate; break;
                case GOLD_CHESTPLATE:
                    out += ChatColor.YELLOW + chestplate; break;
                case IRON_CHESTPLATE:
                    out += ChatColor.GRAY + chestplate; break;
                case LEATHER_CHESTPLATE:
                    out += ChatColor.GOLD + chestplate; break;
                default:
                    out += ChatColor.BLACK + chestplate; break;
            }
        }
        ItemStack l = inv.getLeggings();

        if (l != null) {
            String leggings = plugin.getLang("armor.l");
            switch (l.getType()) {
                case CHAINMAIL_LEGGINGS:
                    out += ChatColor.WHITE + leggings; break;
                case DIAMOND_LEGGINGS:
                    out += ChatColor.AQUA + leggings; break;
                case GOLD_LEGGINGS:
                    out += ChatColor.YELLOW + leggings; break;
                case IRON_LEGGINGS:
                    out += ChatColor.WHITE + leggings; break;
                case LEATHER_LEGGINGS:
                    out += ChatColor.GOLD + leggings; break;
                default:
                    out += ChatColor.BLACK + leggings; break;
            }
        }
        ItemStack b = inv.getBoots();

        if (b != null) {
            String boots = plugin.getLang("armor.B");
            switch (b.getType()) {
                case CHAINMAIL_BOOTS:
                    out += ChatColor.WHITE + boots; break;
                case DIAMOND_BOOTS:
                    out += ChatColor.AQUA + boots; break;
                case GOLD_BOOTS:
                    out += ChatColor.YELLOW + boots; break;
                case IRON_BOOTS:
                    out += ChatColor.WHITE + boots; break;
                case LEATHER_BOOTS:
                    out += ChatColor.GOLD + boots; break;
                default: 
                    out += ChatColor.BLACK + boots; break;          
            }
        }
        if (out.length() == 0) {
            out = ChatColor.BLACK + "None";
        }
        return out;
    }

    /**
     * Returns a formatted string detailing the players weapons
     *
     * @param inv
     * @return
     */
    public String getWeaponString(PlayerInventory inv) {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();

        StringJoiner joiner = new StringJoiner( "," ); 

        int count = getItemCount(inv.all(Material.DIAMOND_SWORD));

        if (count > 0) {
            joiner.add( ChatColor.AQUA + plugin.getLang("weapon.S") + headColor + (count > 1 ? count + "" : "" ));
        }

        count = getItemCount(inv.all(Material.GOLD_SWORD));

        if (count > 0) {
            joiner.add( ChatColor.YELLOW + plugin.getLang("weapon.S") + headColor + (count > 1 ? count + "" : "" ));
        }

        count = getItemCount(inv.all(Material.IRON_SWORD));

        if (count > 0) {
            joiner.add( ChatColor.WHITE + plugin.getLang("weapon.S") + headColor + (count > 1 ? count + "" : "" ));
        }

        count = getItemCount(inv.all(Material.STONE_SWORD));

        if (count > 0) {
            joiner.add( ChatColor.GRAY + plugin.getLang("weapon.S") + headColor + (count > 1 ? count + "" : "" ));
        }

        count = getItemCount(inv.all(Material.WOOD_SWORD));

        if (count > 0) {
            joiner.add( ChatColor.GOLD + plugin.getLang("weapon.S") + headColor + (count > 1 ? count + "" : "" ));
        }

        count = getItemCount(inv.all(Material.BOW));

        if (count > 0) {
            joiner.add( ChatColor.GOLD + plugin.getLang("weapon.B") + headColor + (count > 1 ? count + "" : "" ));
        }

        count = getItemCount(inv.all(Material.ARROW));

        if (count > 0) {
            joiner.add( ChatColor.GOLD + plugin.getLang("weapon.A") + headColor + count );
        }

        if ( joiner.length() < 2 ) {
            joiner.add( ChatColor.BLACK + "None" );
        }

        return joiner.toString();
    }

    private int getItemCount(HashMap<Integer, ? extends ItemStack> all) {
        return all.values().parallelStream().mapToInt( ItemStack::getAmount ).sum();
    }

    /**
     * Returns a formatted string detailing the players food
     *
     * @param inv
     * @return
     */
    public String getFoodString(PlayerInventory inv) {
        double out = 0;

        int count = getItemCount(inv.all(Material.GRILLED_PORK)); 

        if (count > 0) {
            out += count * 4;
        }

        count = getItemCount(inv.all(Material.COOKED_FISH));

        if (count > 0) {
            out += count * 3;
        }

        count = getItemCount(inv.all(Material.COOKIE));

        if (count > 0) {
            out += count * 1;
        }

        count = getItemCount(inv.all(Material.CAKE));

        if (count > 0) {
            out += count * 6;
        }

        count = getItemCount(inv.all(Material.CAKE_BLOCK));

        if (count > 0) {
            out += count * 9;
        }

        count = getItemCount(inv.all(Material.MUSHROOM_SOUP));

        if (count > 0) {
            out += count * 4;
        }

        count = getItemCount(inv.all(Material.BREAD));

        if (count > 0) {
            out += count * 3;
        }

        count = getItemCount(inv.all(Material.APPLE));

        if (count > 0) {
            out += count * 2;
        }

        count = getItemCount(inv.all(Material.GOLDEN_APPLE));

        if (count > 0) {
            out += count * 5;
        }

        count = getItemCount(inv.all(Material.RAW_BEEF));

        if (count > 0) {
            out += count * 2;
        }

        count = getItemCount(inv.all(Material.COOKED_BEEF)); 

        if (count > 0) {
            out += count * 4;
        }

        count = getItemCount(inv.all(Material.PORK)); 

        if (count > 0) {
            out += count * 2;
        }

        count = getItemCount(inv.all(Material.RAW_CHICKEN));

        if (count > 0) {
            out += count * 1;
        }

        count = getItemCount(inv.all(Material.COOKED_CHICKEN));

        if (count > 0) {
            out += count * 3;
        }

        count = getItemCount(inv.all(Material.ROTTEN_FLESH));

        if (count > 0) {
            out += count * 2;
        }

        count = getItemCount(inv.all(Material.MELON)); 

        if (count > 0) {
            out += count * 2;
        }

        if (out == 0) {
            return ChatColor.BLACK + plugin.getLang("none");
        }
        return new DecimalFormat("#.#").format(out) + "" + ChatColor.GOLD + "h";
    }

    /**
     * Returns a formatted string detailing the players health
     *
     * @param health
     * @return
     */
    public String getHealthString(double health) {
        String out = "";

        if (health >= 16) 
            out += ChatColor.GREEN;
        else if (health >= 8) 
            out += ChatColor.GOLD;
        else 
            out += ChatColor.RED;

        for (int i = 0; i < health; i++) {
            out += '|';
        }
        return out;
    }

    /**
     * Returns a formatted string detailing the players hunger
     *
     * @param health
     * @return
     */
    public String getHungerString(int health) {
        String out = "";

        if (health >= 16) 
            out += ChatColor.GREEN;
        else if (health >= 8) 
            out += ChatColor.GOLD;
        else 
            out += ChatColor.RED;

        for (int i = 0; i < health; i++) {
            out += '|';
        }
        return out;
    }

    /**
     * Sort clans by KDR
     *
     * @param clans
     * @return
     */
    public void sortClansByKDR(List<Clan> _clans) {
        Collections.sort(_clans, (c1, c2) -> {
                Float o1 = c1.getTotalKDR();
                Float o2 = c2.getTotalKDR();

                return o2.compareTo(o1);
        });
    }

    /**
     * Sort clans by KDR
     *
     * @param clans
     * @return
     */
    public void sortClansBySize(List<Clan> _clans) {
        Collections.sort(_clans, (c1, c2) -> {
                Integer o1 = c1.getAllMembers().size();
                Integer o2 = c2.getAllMembers().size();

                return o2.compareTo(o1);
        });
    }

    /**
     * Sort clan players by KDR
     *
     * @param cps
     * @return
     */
    public void sortClanPlayersByKDR(List<ClanPlayer> cps) {
        Collections.sort(cps, (c1, c2) -> {
                Float o1 = c1.getKDR();
                Float o2 = c2.getKDR();

                return o2.compareTo(o1);
        });
    }

    /**
     * Sort clan players by last seen days
     *
     * @param cps
     * @return
     */
    public void sortClanPlayersByLastSeen(List<ClanPlayer> cps) {
        Collections.sort(cps, (c1, c2) -> {
                Double o1 = c1.getLastSeenDays();
                Double o2 = c2.getLastSeenDays();

                return o1.compareTo(o2);
        });
    }

    /**
     * Purchase clan creation
     *
     * @param player
     * @return
     */
    public boolean purchaseCreation(Player player) {
        SettingsManager settings = plugin.getSettingsManager();
        if (!settings.isePurchaseCreation()) {
            return true;
        }
        double price = settings.getCreationPrice();

        PermissionsManager perms = plugin.getPermissionsManager();
        if (perms.hasEconomy()) {
            if (perms.playerHasMoney(player, price)) {
                perms.playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            }
            else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }
        return true;
    }

    /**
     * Purchase invite
     *
     * @param player
     * @return
     */
    public boolean purchaseInvite(Player player) {
        SettingsManager settings = plugin.getSettingsManager();
        if (!settings.isePurchaseInvite()) {
            return true;
        }
        double price = settings.getInvitePrice();

        PermissionsManager perms = plugin.getPermissionsManager();
        if (perms.hasEconomy()) {
            if (perms.playerHasMoney(player, price)) {
                perms.playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            }
            else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }
        return true;
    }

    /**
     * Purchase Home Teleport
     *
     * @param player
     * @return
     */
    public boolean purchaseHomeTeleport(Player player) {
        SettingsManager settings = plugin.getSettingsManager();
        if (!settings.isePurchaseHomeTeleport()) {
            return true;
        }
        double price = settings.getHomeTeleportPrice();

        PermissionsManager perms = plugin.getPermissionsManager();
        if (perms.hasEconomy()) {
            if (perms.playerHasMoney(player, price)) {
                perms.playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            }
            else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }
        return true;
    }

    /**
     * Purchase Home Teleport Set
     *
     * @param player
     * @return
     */
    public boolean purchaseHomeTeleportSet(Player player) {
        SettingsManager settings = plugin.getSettingsManager();
        if (!settings.isePurchaseHomeTeleportSet()) {
            return true;
        }
        double price = settings.getHomeTeleportPriceSet();

        PermissionsManager perms = plugin.getPermissionsManager();
        if (perms.hasEconomy()) {
            if (perms.playerHasMoney(player, price)) {
                perms.playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            }
            else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }
        return true;
    }

    /**
     * Purchase clan verification
     *
     * @param player
     * @return
     */
    public boolean purchaseVerification(Player player) {
        SettingsManager settings = plugin.getSettingsManager();
        if (!settings.isePurchaseVerification()) {
            return true;
        }
        double price = settings.getVerificationPrice();

        PermissionsManager perms = plugin.getPermissionsManager();
        if (perms.hasEconomy()) {
            if (perms.playerHasMoney(player, price)) {
                perms.playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            }
            else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }
        return true;
    }

    /**
     * Processes a clan chat command
     *
     * @param player
     * @param msg
     */
    public void processClanChat(Player player, String tag, String msg) {
        Clan clan = plugin.getClanManager().getClan(tag);

        if (clan == null || !clan.isMember(player)) {
            return;
        }
        processClanChat(player, msg);
    }

    /**
     * Processes a clan chat command
     *
     * @param player
     * @param msg
     */
    public void processClanChat(Player player, String msg) {
        SettingsManager settings = plugin.getSettingsManager();
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player.getUniqueId());

        if (cp == null) return;

        String[] split = msg.split(" ");

        if (split.length == 0)  return; 

        String command = split[0];

        if (command.equals(plugin.getLang("on"))) {
            cp.setClanChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled clan chat");
        }
        else if (command.equals(plugin.getLang("off"))) {
            cp.setClanChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled clan chat");
        }
        else if (command.equals(plugin.getLang("join"))) {
            cp.setChannel(ClanPlayer.Channel.CLAN);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have joined clan chat");
        }
        else if (command.equals(plugin.getLang("leave"))) {
            cp.setChannel(ClanPlayer.Channel.NONE);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have left clan chat");
        }
        else if (command.equals(plugin.getLang("mute"))) {
            if (cp.isMuted()) {
                cp.setMuted(true);
                ChatBlock.sendMessage(player, ChatColor.AQUA + "You have muted clan chat");
            }
            else {
                cp.setMuted(false);
                ChatBlock.sendMessage(player, ChatColor.AQUA + "You have unmuted clan chat");
            }
        }
        else {
            String code = "" + ChatColor.RED + ChatColor.WHITE + ChatColor.RED + ChatColor.BLACK;
            String tag;

            if (cp.getRank() != null && !cp.getRank().isEmpty())  {
                tag = settings.getClanChatBracketColor() + settings.getClanChatTagBracketLeft() + settings.getClanChatRankColor() + cp.getRank() + settings.getClanChatBracketColor() + settings.getClanChatTagBracketRight() + " ";
            }
            else {
                tag = settings.getClanChatBracketColor() + settings.getClanChatTagBracketLeft() + settings.getTagDefaultColor() + cp.getClan().getColorTag() + settings.getClanChatBracketColor() + settings.getClanChatTagBracketRight() + " ";
            }

            String message = code + Helper.parseColors(tag) + settings.getClanChatNameColor() + settings.getClanChatPlayerBracketLeft() + player.getName() + settings.getClanChatPlayerBracketRight() + " " + settings.getClanChatMessageColor() + msg;
            String eyeMessage = code + settings.getClanChatBracketColor() + settings.getClanChatTagBracketLeft() + settings.getTagDefaultColor() + cp.getClan().getColorTag() + settings.getClanChatBracketColor() + settings.getClanChatTagBracketRight() + " " + settings.getClanChatNameColor() + settings.getClanChatPlayerBracketLeft() + player.getName() + settings.getClanChatPlayerBracketRight() + " " + settings.getClanChatMessageColor() + msg;

            Bukkit.getConsoleSender().sendMessage(eyeMessage);

            List<ClanPlayer> cps = cp.getClan().getMembers();

            for (ClanPlayer cpp : cps) {
                Player member = cpp.toPlayer();
                if (cpp.isMuted()) {
                    continue;
                }
                ChatBlock.sendMessage(member, message);
            }
            sendToAllSeeing(eyeMessage, cps);
        }
    }

    public void sendToAllSeeing(String msg, List<ClanPlayer> cps) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (Player player : players) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.admin.all-seeing-eye")) {
                boolean alreadySent = false;

                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null && cp.isMuted()) {
                    continue;
                }

                for (ClanPlayer cpp : cps) {
                    if (cpp.getUniqueId().equals(player.getUniqueId())) {
                        alreadySent = true;
                    }
                }

                if (!alreadySent) {
                    ChatBlock.sendMessage(player, ChatColor.DARK_GRAY + Helper.stripColors(msg));
                }
            }
        }
    }

    /**
     * Processes a ally chat command
     *
     * @param player
     * @param msg
     */
    public void processAllyChat(Player player, String msg) {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) return;

        String[] split = msg.split(" ");

        if (split.length == 0) return;

        String command = split[0];

        if (command.equals(plugin.getLang("on"))) {
            cp.setAllyChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled ally chat");
        }
        else if (command.equals(plugin.getLang("off"))) {
            cp.setAllyChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled ally chat");
        }
        else if (command.equals(plugin.getLang("join"))) {
            cp.setChannel(ClanPlayer.Channel.ALLY);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have joined ally chat");
        }
        else if (command.equals(plugin.getLang("leave"))) {
            cp.setChannel(ClanPlayer.Channel.NONE);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have left ally chat");
        }
        else if (command.equals(plugin.getLang("mute"))) {
            if (!cp.isMutedAlly()) {
                cp.setMutedAlly(true);
                ChatBlock.sendMessage(player, ChatColor.AQUA + "You have muted ally chat");
            }
            else {
                cp.setMutedAlly(false);
                ChatBlock.sendMessage(player, ChatColor.AQUA + "You have unmuted ally chat");
            }
        }
        else {
            SettingsManager settings = plugin.getSettingsManager();
            String code = "" + ChatColor.AQUA + ChatColor.WHITE + ChatColor.AQUA + ChatColor.BLACK;
            String message = code + settings.getAllyChatBracketColor() + settings.getAllyChatTagBracketLeft() + settings.getAllyChatTagColor() + settings.getCommandAlly() + settings.getAllyChatBracketColor() + settings.getAllyChatTagBracketRight() + " " + settings.getAllyChatNameColor() + settings.getAllyChatPlayerBracketLeft() + player.getName() + settings.getAllyChatPlayerBracketRight() + " " + settings.getAllyChatMessageColor() + msg;
            SimpleClans.log(message);

            Player self = cp.toPlayer();
            ChatBlock.sendMessage(self, message);

            Set<ClanPlayer> allies = cp.getClan().getAllAllyMembers();
            allies.addAll(cp.getClan().getMembers());

            for (ClanPlayer ally : allies) {
                if (ally.isMutedAlly()) {
                    continue;
                }
                Player member = ally.toPlayer();
                
                if (player.getUniqueId().equals(ally.getUniqueId())) {
                    continue;
                }
                ChatBlock.sendMessage(member, message);
            }
        }
    }

    /**
     * Processes a global chat command
     *
     * @param player
     * @param msg
     * @return boolean
     */
    public boolean processGlobalChat(Player player, String msg) {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player.getUniqueId());
        
        if (cp == null) return false;

        String[] split = msg.split(" ");

        if (split.length == 0) return false;

        String command = split[0];

        if (command.equals(plugin.getLang("on"))) {
            cp.setGlobalChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled global chat");
        }
        else if (command.equals(plugin.getLang("off"))) {
            cp.setGlobalChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled global chat");
        }
        else return true;

        return false;
    }
}