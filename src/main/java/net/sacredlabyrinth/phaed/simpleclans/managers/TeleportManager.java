package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.TeleportState;

public final class TeleportManager {
    private SimpleClans plugin;
    private Set<TeleportState> waitingPlayers = new HashSet<>();

    public TeleportManager() {
        plugin = SimpleClans.getInstance();
        startCounter();
    }

    /**
     * Add player to teleport waiting queue
     *
     * @param player
     * @param dest
     * @param clanName
     */
    public void addPlayer(Player player, Location dest, String clanName) {
        int secs = plugin.getSettingsManager().getWaitSecs();

        waitingPlayers.add( new TeleportState(player, dest, clanName) );
        
        if (secs > 0) {
            ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("waiting.for.teleport.stand.still.for.0.seconds"), secs));
        }
    }

    private void dropItems(Player player) {
        
        if (plugin.getSettingsManager().isDropOnHome()) {
            
            PlayerInventory inv = player.getInventory();
            ItemStack[] contents = inv.getContents();

            List<Integer> itemsList = plugin.getSettingsManager().getItemsList();
            
            for (ItemStack item : contents) {
                if (item == null) {
                    continue;
                }

                if (itemsList.contains(item.getTypeId())) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    inv.remove(item);
                }
            }
        }
        else if (plugin.getSettingsManager().isKeepOnHome()) {

            PlayerInventory inv = player.getInventory();
            ItemStack[] contents = inv.getContents().clone();

            List<Integer> itemsList = plugin.getSettingsManager().getItemsList();
            
            for (ItemStack item : contents) {
                if (item == null) {
                    continue;
                }

                if (!itemsList.contains(item.getTypeId())) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    inv.remove(item);
                }
            }
        }
    }

    private void startCounter() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
    
            for (Iterator<TeleportState> iter = waitingPlayers.iterator(); iter.hasNext(); ) {
                TeleportState state = iter.next();

                if (state.isProcessing()) {
                    continue;
                }
                state.setProcessing(true);

                Player player = state.getPlayer();

                if (player != null) {
                    if (state.isTeleportTime()) {
                        if (Helper.isSameBlock(player.getLocation(), state.getLocation()))  {
                            Location loc = state.getDestination();

                            int x = loc.getBlockX();
                            int z = loc.getBlockZ();

                            if (plugin.getSettingsManager().isTeleportBlocks()) {
                                player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY() - 1, z + 1), Material.GLASS, (byte) 0);
                                player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY() - 1, z - 1), Material.GLASS, (byte) 0);
                                player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY() - 1, z - 1), Material.GLASS, (byte) 0);
                                player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY() - 1, z + 1), Material.GLASS, (byte) 0);
                            }
                            
                            if (!plugin.getPermissionsManager().has(player, "simpleclans.mod.keep-items")) {
                                dropItems(player);
                            }

                            SimpleClans.debug("teleporting");

                            player.teleport(new Location(loc.getWorld(), loc.getBlockX() + .5, loc.getBlockY(), loc.getBlockZ() + .5));

                            ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("now.at.homebase"), state.getClanName()));
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.moved.teleport.cancelled"));

                        iter.remove();
                    }
                    else if (!Helper.isSameBlock(player.getLocation(), state.getLocation())) {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.moved.teleport.cancelled"));
                            iter.remove();
                    }
                    else ChatBlock.sendMessage(player, ChatColor.AQUA + "" + state.getCounter());
                }
                else iter.remove();

                state.setProcessing(false);
            }           
        }, 0, 20L);
    }
}