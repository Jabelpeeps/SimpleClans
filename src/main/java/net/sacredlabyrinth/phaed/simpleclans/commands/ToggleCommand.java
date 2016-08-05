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
public class ToggleCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();
        PermissionsManager perms = plugin.getPermissionsManager();

        if (arg.length == 0) {
            return;
        }

        String cmd = arg[0];
        
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        
        if ( cp == null )  {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
            return;
        }
        Clan clan = cp.getClan();
        
        if ( clan == null || !clan.isVerified() )  {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("clan.is.not.verified"));
            return;
        }
        
        if (cmd.equalsIgnoreCase("cape") && perms.has(player, "simpleclans.member.cape-toggle")) {

            if (cp.isCapeEnabled()) {
                ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("capeoff"));
                cp.setCapeEnabled(false);
            } 
            else {
                ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("capeon"));
                cp.setCapeEnabled(true);
            }
            return;
        }
        
        if (cmd.equalsIgnoreCase("bb") && perms.has(player, "simpleclans.member.bb-toggle")) {

            if (cp.isBbEnabled()) {
                ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("bboff"));
                cp.setBbEnabled(false);
            } 
            else {
                ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("bbon"));
                cp.setBbEnabled(true);
            }
            plugin.getStorageManager().updateClanPlayer(cp);
            return;
        }
        
        if (cmd.equalsIgnoreCase("tag") && perms.has(player, "simpleclans.member.tag-toggle")) {

            if (cp.isTagEnabled()) {
                ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("tagoff"));
                cp.setTagEnabled(false);
            } 
            else {
                ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("tagon"));
                cp.setTagEnabled(true);
            }
            plugin.getStorageManager().updateClanPlayer(cp);
            return;
        }

        if (!clan.isLeader(player)) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));
            return;
        }
        
        if (cmd.equalsIgnoreCase("deposit") && perms.has(player, "simpleclans.leader.deposit-toggle")) {        	
            clan.setAllowDeposit(!clan.isAllowDeposit());
            return;
        }
        
        if (cmd.equalsIgnoreCase("withdraw") && perms.has(player, "simpleclans.leader.withdraw-toggle")) { 
            clan.setAllowWithdraw(!clan.isAllowWithdraw());
            return;
        }
        ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
    }
}
