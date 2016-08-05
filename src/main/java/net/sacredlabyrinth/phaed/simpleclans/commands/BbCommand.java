package net.sacredlabyrinth.phaed.simpleclans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;

/**
 * @author phaed
 */
public class BbCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();
        PermissionsManager perms = plugin.getPermissionsManager();

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null) {
            Clan clan = cp.getClan();

            if (clan.isVerified()) {
                if (arg.length == 0) {
                    
                    if (perms.has(player, "simpleclans.member.bb")) 
                        clan.displayBb(player);
                    else 
                        ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions") );
                } 
                else if (arg.length == 1 && arg[0].equalsIgnoreCase("clear")) {
                    
                    if (perms.has(player, "simpleclans.leader.bb-clear")) {
                        if (cp.isTrusted() && cp.isLeader()) {
                            cp.getClan().clearBb();
                            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("cleared.bb"));
                        } 
                        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));    
                    } 
                    else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));    
                } 
                else if (perms.has(player, "simpleclans.member.bb-add")) {
                    
                    if (cp.isTrusted()) {
                        String msg = String.join( " ", arg);
                        clan.addBb(player.getName(), ChatColor.AQUA + player.getName() + ": " + ChatColor.WHITE + msg);
                        plugin.getStorageManager().updateClan(clan);
                    } 
                    else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));
                } 
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
            } 
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("clan.is.not.verified"));
        } 
        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
    }
}