package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.Set;

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
public class WarCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        Player player = (Player) sender;

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.war")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {
                        if (arg.length == 2) {
                            String action = arg[0];
                            Clan war = plugin.getClanManager().getClan(arg[1]);

                            if (war != null) {
                                if (clan.isRival(war)) {
                                    if (action.equals(plugin.getLang("start"))) {
                                        if (!clan.isWarring(war)) {
                                            Set<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());

                                            if (!onlineLeaders.isEmpty()) {
                                                plugin.getRequestManager().addWarStartRequest(cp, war, clan);
                                                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("leaders.have.been.asked.to.accept.the.war.request"), Helper.capitalize(war.getName())));
                                            }
                                            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("at.least.one.leader.accept.the.alliance"));
                                        }
                                        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clans.already.at.war"));
                                    }
                                    else if (action.equals(plugin.getLang("end"))) {
                                        if (clan.isWarring(war)) {
                                            plugin.getRequestManager().addWarEndRequest(cp, war, clan);
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("leaders.asked.to.end.rivalry"), Helper.capitalize(war.getName())));
                                        }
                                        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clans.not.at.war"));
                                    }
                                    else ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.war"), plugin.getSettingsManager().getCommandClan()));
                                }
                                else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.can.only.start.war.with.rivals"));
                            }
                            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.war"), plugin.getSettingsManager().getCommandClan()));
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
