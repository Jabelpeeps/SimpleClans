package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerHomeSetEvent;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class HomeCommand  implements ClanCommand {

    @SuppressWarnings( "deprecation" )
    @Override
    public void execute(CommandSender sender, String[] arg) {
        
        SimpleClans plugin = SimpleClans.getInstance();
        PermissionsManager perms = plugin.getPermissionsManager();
        ClanManager clanMan = plugin.getClanManager();
        
        Player player = (Player) sender;
        
        if (arg.length == 2 && arg[0].equalsIgnoreCase("set") && perms.has(player, "simpleclans.mod.home")) {
            
            if (!clanMan.purchaseHomeTeleportSet(player)) {
                return;
            }
            Location loc = player.getLocation();
            Clan clan = clanMan.getClan(arg[1]);
            if (clan != null) {
                clan.setHomeLocation(loc);
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("hombase.mod.set"), clan.getName()) + " " + ChatColor.YELLOW + Helper.toLocationString(loc));
            }
        }
        if (arg.length == 2 && arg[0].equalsIgnoreCase("tp") && perms.has(player, "simpleclans.mod.hometp")) {
            
            Clan clan = clanMan.getClan(arg[1]);
            
            if (clan != null) {
                Location loc = clan.getHomeLocation();
                if (loc == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("hombase.not.set"));
                    return;
                }
                player.teleport(loc);
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("now.at.homebase"), clan.getName()));
                return;
            }
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.clan.does.not.exist"));
            return;
        }
        ClanPlayer cp = clanMan.getClanPlayer(player);
        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }
        Clan clan = cp.getClan();
        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            return;
        }
        if (!cp.isTrusted()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.vitals"));
            return;
        }
        if (arg.length == 0) {
            if (!perms.has(player, "simpleclans.member.home")) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                return;
            }
                Location loc = clan.getHomeLocation();
                if (loc == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("hombase.not.set"));
                    return;
                }
            if (clanMan.purchaseHomeTeleport(player)) {
                plugin.getTeleportManager().addPlayer(player, clan.getHomeLocation(), clan.getName());
            }
        }
        else {
            SettingsManager settings = plugin.getSettingsManager();
            String ttag = arg[0];
            
            if (ttag.equalsIgnoreCase("set")) {
                if (!cp.isLeader()) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    return;
                }
                if (!perms.has(player, "simpleclans.leader.home-set")) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    return;
                }
                if (    settings.isHomebaseSetOnce() 
                        && clan.getHomeLocation() != null 
                        && !perms.has(player, "simpleclans.mod.home")) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("home.base.only.once"));
                    return;
                }
                PlayerHomeSetEvent homeSetEvent = new PlayerHomeSetEvent(clan, cp, player.getLocation());
                Bukkit.getPluginManager().callEvent(homeSetEvent);
                
                if (homeSetEvent.isCancelled() || !clanMan.purchaseHomeTeleportSet(player)) {
                    return;
                }
                clan.setHomeLocation(player.getLocation());
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("hombase.set"), ChatColor.YELLOW + Helper.toLocationString(player.getLocation())));
            }
            else if (ttag.equalsIgnoreCase("clear")) {
                if (!cp.isLeader()) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    return;
                }
                if (!perms.has(player, "simpleclans.leader.home-set")) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    return;
                }
                if (    settings.isHomebaseSetOnce() 
                        && clan.getHomeLocation() != null 
                        && !perms.has(player, "simpleclans.mod.home")) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("home.base.only.once"));
                    return;
                }
                clan.setHomeLocation(null);
                ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("hombase.cleared"));
            }
            else if (ttag.equalsIgnoreCase("regroup")) {
                if (settings.getAllowReGroupCommand()) {
                    Location loc = player.getLocation();
                    if (!cp.isLeader()) {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                        return;
                    }
                    if (!perms.has(player, "simpleclans.leader.regroup")) {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                        return;
                    }
                    List<ClanPlayer> members = clan.getAllMembers();
                    for (ClanPlayer ccp : members) {
                        Player pl = ccp.toPlayer();
                        if (pl == null || pl.equals(player)) {
                            continue;
                        }
                        int x = loc.getBlockX();
                        int z = loc.getBlockZ();
                        player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY(), z + 1), Material.GLASS, (byte) 0);
                        player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY(), z - 1), Material.GLASS, (byte) 0);
                        player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY(), z - 1), Material.GLASS, (byte) 0);
                        player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY(), z + 1), Material.GLASS, (byte) 0);
                        Random r = new Random();
                        int xx = r.nextInt(2) - 1;
                        int zz = r.nextInt(2) - 1;
                        if (xx == 0 && zz == 0) {
                            xx = 1;
                        }
                        x = x + xx;
                        z = z + zz;
                        pl.teleport(new Location(loc.getWorld(), x + .5, loc.getBlockY(), z + .5));
                    }
                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("hombase.set") + ChatColor.YELLOW + Helper.toLocationString(loc));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
