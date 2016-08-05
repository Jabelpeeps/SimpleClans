package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;

/**
 *
 * @author phaed
 */
public class DemoteCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.demote")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isLeader(player)) {
                    if (arg.length == 1) {
                        String demotedName = arg[0];
                        boolean allOtherLeadersOnline;
                        
                        if (demotedName == null) {
                            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.player.matched"));
                            return;
                        }
                        UUID PlayerUniqueId = Helper.getCachedPlayerUUID(demotedName);
                        if (PlayerUniqueId == null) {
                            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.player.matched"));
                            return;
                        }
                        
                        allOtherLeadersOnline = clan.allOtherLeadersOnline(PlayerUniqueId);
                        
                        if (allOtherLeadersOnline) {
                            if (clan.isLeader(PlayerUniqueId)) {
                                if (clan.getLeaders().size() == 1|| !plugin.getSettingsManager().isConfirmationForDemote()) {
                                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(
                                            lang.get("demoted.back.to.member"), Helper.capitalize(demotedName)));
                                    clan.demote(PlayerUniqueId);
                                }
                                else {
                                    plugin.getRequestManager().addDemoteRequest(cp, demotedName, clan);
                                    ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("demotion.vote.has.been.requested.from.all.leaders"));
                                }
                            }
                            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("player.is.not.a.leader.of.your.clan"));                           
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED , lang.get("leaders.must.be.online.to.vote.on.demotion"));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.demote.leader"), plugin.getSettingsManager().getCommandClan()));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
    }
}
