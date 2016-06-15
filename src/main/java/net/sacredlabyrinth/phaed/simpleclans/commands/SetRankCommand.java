package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;

public class SetRankCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        Player player = (Player) sender;
        
        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.setrank")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {
                        if (arg.length >= 1) {
                            Player ranker = Bukkit.getPlayer( arg[0] );
                            String rank = Helper.toMessage(Helper.removeFirst(arg));

                            if (clan.isMember(ranker) || clan.isLeader(ranker)) {
                             
                                ClanPlayer cpm = plugin.getClanManager().getClanPlayer(ranker);
                                cpm.setRank(rank);
                                plugin.getStorageManager().updateClanPlayer(cpm);

                                ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.rank.changed"));
                            }
                            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.setrank"), plugin.getSettingsManager().getCommandClan()));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
    }
}
