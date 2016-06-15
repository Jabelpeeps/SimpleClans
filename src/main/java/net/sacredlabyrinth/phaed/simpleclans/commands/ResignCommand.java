package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;

/**
 * @author phaed
 */
public class ResignCommand  implements ClanCommand {
 
    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.resign")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (!clan.isLeader(player) || clan.getLeaders().size() > 1) {
                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("0.has.resigned"), Helper.capitalize(player.getName())));
                    clan.removePlayerFromClan(player.getUniqueId());
                }
                else if (clan.isLeader(player) && clan.getLeaders().size() == 1) {
                    plugin.getClanManager().serverAnnounce(ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.has.been.disbanded"), clan.getName()));
                    clan.disband();
                }
                else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("last.leader.cannot.resign.you.must.appoint.another.leader.or.disband.the.clan"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
    }
}
