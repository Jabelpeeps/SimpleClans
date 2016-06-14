package net.sacredlabyrinth.phaed.simpleclans.executors;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class DenyCommandExecutor implements CommandExecutor {
    SimpleClans plugin = SimpleClans.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (plugin.getSettingsManager().isBanned(player.getUniqueId())) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("banned"));
            return false;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null) {
            Clan clan = cp.getClan();

            if (clan.isLeader(player)) {
                if (plugin.getRequestManager().hasRequest(clan.getTag())) {
                    if (cp.getVote() == null) {
                        plugin.getRequestManager().deny(cp);
                        clan.leaderAnnounce(ChatColor.RED + MessageFormat.format(plugin.getLang("has.voted.to.deny"), Helper.capitalize(player.getName())));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.have.already.voted"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("nothing.to.deny"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
        }
        else if (plugin.getRequestManager().hasRequest(player.getName().toLowerCase())) {
         
            cp = plugin.getClanManager().getCreateClanPlayer(player.getUniqueId());
            plugin.getRequestManager().deny(cp);
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("nothing.to.deny"));

        return false;
    }
}
