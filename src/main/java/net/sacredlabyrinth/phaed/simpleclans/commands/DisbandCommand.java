package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;

/**
 * @author phaed
 */
public class DisbandCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();
        ClanManager clanMan = plugin.getClanManager();

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.disband")) {
                ClanPlayer cp = clanMan.getClanPlayer(player);

                if (cp != null) {
                    Clan clan = cp.getClan();

                    if (clan.isLeader(player)) {
                        if (clan.getLeaders().size() == 1) {
                            clan.clanAnnounce(player.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("clan.has.been.disbanded"), clan.getName()));
                            clan.disband();
                        }
                        else {
                            plugin.getRequestManager().addDisbandRequest(cp, clan);
                            ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("clan.disband.vote.has.been.requested.from.all.leaders"));
                        }
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        }
        else if (arg.length == 1) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.mod.disband")) {
                Clan clan = clanMan.getClan(arg[0]);

                if (clan != null) {
                    clanMan.serverAnnounce(ChatColor.AQUA + MessageFormat.format(lang.get("clan.has.been.disbanded"), clan.getName()));
                    clan.disband();
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.clan.matched"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED,  MessageFormat.format(lang.get("usage.0.disband"), plugin.getSettingsManager().getCommandClan()));
    }
}
