package net.sacredlabyrinth.phaed.simpleclans.listeners;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.AcceptCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.AllyCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.DenyCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.GlobalCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.MoreCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class SCPlayerListener implements Listener {
    private SimpleClans plugin = SimpleClans.getInstance();

    public SCPlayerListener() {}

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)  {

        Player player = event.getPlayer();

        if (player == null) return;
        
        SettingsManager settings = plugin.getSettingsManager();
        
        if (settings.isBlacklistedWorld(player.getLocation().getWorld().getName())) 
            return;

        if (event.getMessage().length() == 0) return;

        String[] split = event.getMessage().substring(1).split(" ");

        if (split.length == 0) return;

        String command = split[0];

        if (settings.isTagBasedClanChat() && plugin.getClanManager().isClan(command)) {
            
            if (!settings.getClanChatEnable()) return;

            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp == null) return;

            if (cp.getTag().equalsIgnoreCase(command)) {
                
                event.setCancelled(true);

                if (split.length > 1)
                    plugin.getClanManager()
                          .processClanChat(player, cp.getTag(), Helper.toMessage(Helper.removeFirst(split)));
            }
        }
        if (command.equals(".")) {
            
            if (!settings.getClanChatEnable()) return;

            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp == null) return;

            event.setCancelled(true);

            if (split.length > 1)
                plugin.getClanManager()
                      .processClanChat(player, cp.getTag(), Helper.toMessage(Helper.removeFirst(split)));
        }

        if (settings.isForceCommandPriority()) {
            
            // what the H*** is this C***?
            if (command.equalsIgnoreCase(settings.getCommandAlly())) {
                if (!Bukkit.getPluginCommand(
                        settings.getCommandAlly() ).equals(
                                plugin.getCommand(settings.getCommandAlly()))) {
                    new AllyCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            }
            else if (command.equalsIgnoreCase(settings.getCommandGlobal())) {
                if (!Bukkit.getPluginCommand(
                        settings.getCommandGlobal() ).equals(
                                plugin.getCommand(settings.getCommandGlobal()))) {
                    new GlobalCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            }
            else if (command.equalsIgnoreCase(settings.getCommandClan())) {
                if (!Bukkit.getPluginCommand(
                        settings.getCommandClan()).equals(
                                plugin.getCommand(settings.getCommandClan()))) {
                    new ClanCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            }
            else if (command.equalsIgnoreCase(settings.getCommandAccept())) {
                if (!Bukkit.getPluginCommand(
                        settings.getCommandAccept()).equals(
                                plugin.getCommand(settings.getCommandAccept()))) {
                    new AcceptCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            }
            else if (command.equalsIgnoreCase(settings.getCommandDeny())) {
                if (!Bukkit.getPluginCommand(
                        settings.getCommandDeny()).equals(
                                plugin.getCommand(settings.getCommandDeny()))) {
                    new DenyCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            }
            else if (command.equalsIgnoreCase(settings.getCommandMore())) {
                if (!Bukkit.getPluginCommand(
                        settings.getCommandMore()).equals(
                                plugin.getCommand(settings.getCommandMore()))) {
                    new MoreCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (inBlackListedWorld(event) ) return;

        if (event.getPlayer() == null) return;

        String message = event.getMessage();
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(event.getPlayer());

        if (cp != null) {
            if (cp.getChannel().equals(ClanPlayer.Channel.CLAN)) {
                plugin.getClanManager().processClanChat(event.getPlayer(), message);
                event.setCancelled(true);
            }
            else if (cp.getChannel().equals(ClanPlayer.Channel.ALLY)) {
                plugin.getClanManager().processAllyChat(event.getPlayer(), message);
                event.setCancelled(true);
            }
        }

        if (!plugin.getPermissionsManager().has(event.getPlayer(), "simpleclans.mod.nohide")) {
            boolean isClanChat = event.getMessage().contains("" + ChatColor.RED + ChatColor.WHITE + ChatColor.RED + ChatColor.BLACK);
            boolean isAllyChat = event.getMessage().contains("" + ChatColor.AQUA + ChatColor.WHITE + ChatColor.AQUA + ChatColor.BLACK);

            for (Iterator<?> iter = event.getRecipients().iterator(); iter.hasNext(); ) {
                Player player = (Player) iter.next();

                ClanPlayer rcp = plugin.getClanManager().getClanPlayer(player);

                if (rcp != null) {
                    if (!rcp.isClanChat() && isClanChat) {
                    	iter.remove();
                        continue;
                    }
                    if (!rcp.isAllyChat() && isAllyChat) {
                    	iter.remove();
                        continue;
                    }
                    if (!rcp.isGlobalChat() && !isAllyChat && !isClanChat) {
                    	iter.remove();
                    }
                }
            }
        }

        if (plugin.getSettingsManager().isCompatMode()) {
            if (plugin.getSettingsManager().isChatTags()) {
                if (cp != null && cp.isTagEnabled()) {
                    String tagLabel = cp.getClan().getTagLabel(cp.isLeader());

                    Player player = event.getPlayer();

                    if (player.getDisplayName().contains("{clan}"))
                        player.setDisplayName(player.getDisplayName().replace("{clan}", tagLabel));
                    else if (event.getFormat().contains("{clan}"))
                        event.setFormat(event.getFormat().replace("{clan}", tagLabel));
                    else {
                        String format = event.getFormat();
                        event.setFormat(tagLabel + format);
                    }
                }
                else {
                    event.setFormat(event.getFormat().replace("{clan}", ""));
                    event.setFormat(event.getFormat().replace("tagLabel", ""));
                }
            }
        }
        else {
            plugin.getClanManager().updateDisplayName(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if ( inBlackListedWorld(event) ) return;
        
        final Player player = event.getPlayer();

        ClanPlayer cp;
        if (plugin.getSettingsManager().getUseBungeeCord()) 
            cp = plugin.getClanManager().getClanPlayerJoinEvent(player);
        else 
            cp = plugin.getClanManager().getClanPlayer(player);
        
        plugin.getStorageManager().updatePlayerNameAsync(player);
        plugin.getClanManager().updateLastSeen(player);
        plugin.getClanManager().updateDisplayName(player);
        
        if (cp == null) return;
  
        cp.setName(player.getName());

        plugin.getPermissionsManager().addPlayerPermissions(cp);

        if (plugin.getSettingsManager().isBbShowOnLogin() && cp.isBbEnabled()) {
        	cp.getClan().displayBb(player);
        }
        plugin.getPermissionsManager().addClanPermissions(cp);

        if (event.getPlayer().isOp()) {
            for (String message : plugin.getMessages()) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + message);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (inBlackListedWorld(event) ) return;

        if (plugin.getSettingsManager().isTeleportOnSpawn()) {
            Player player = event.getPlayer();

            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Location loc = cp.getClan().getHomeLocation();

                if (loc != null) {
                    event.setRespawnLocation(loc);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (inBlackListedWorld(event) ) return;

        Player player = event.getPlayer();
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        plugin.getPermissionsManager().removeClanPlayerPermissions(cp);
        plugin.getClanManager().updateLastSeen(player);
        plugin.getRequestManager().endPendingRequest(player.getName());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (inBlackListedWorld(event) ) return;
            
        plugin.getClanManager().updateLastSeen(event.getPlayer());
    }
    
    private boolean inBlackListedWorld( PlayerEvent event) {        
        return plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld().getName());       
    }
}