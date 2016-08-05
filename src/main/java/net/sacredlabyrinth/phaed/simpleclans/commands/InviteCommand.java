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
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;

/**
 *
 * @author phaed
 */
public class InviteCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        PermissionsManager perms = plugin.getPermissionsManager();
        LanguageManager lang = plugin.getLanguageManager();

        if (perms.has(player, "simpleclans.leader.invite"))  {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isLeader(player)) {
                    if (arg.length == 1) {
                        Player invited = Bukkit.getPlayer(arg[0]);

                        if (invited != null) {
                            if (perms.has(invited, "simpleclans.member.can-join")) {
                                if (!invited.getName().equals(player.getName())) {
                                    if (!plugin.getBansManager().isBanned(invited.getUniqueId())) {
                                        ClanPlayer cpInv = plugin.getClanManager().getClanPlayer(invited);

                                        if (cpInv == null) {
                                            if (plugin.getClanManager().purchaseInvite(player)) {
                                                if(clan.getSize() < plugin.getSettingsManager().getMaxMembers()) {
                                                    plugin.getRequestManager().addInviteRequest(cp, invited.getUniqueId(), clan);
                                                    ChatBlock.sendMessage(player, ChatColor.AQUA, MessageFormat.format(lang.get("has.been.asked.to.join"), Helper.capitalize(invited.getName()), clan.getName()));
                                                } 
                                                else  ChatBlock.sendMessage(player, ChatColor.RED, lang.get("the.clan.members.reached.limit"));
                                            }
                                        }
                                        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("the.player.is.already.member.of.another.clan"));
                                    }
                                    else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("this.player.is.banned.from.using.clan.commands"));
                                }
                                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("you.cannot.invite.yourself"));
                            }
                            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("the.player.doesn.t.not.have.the.permissions.to.join.clans"));
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.player.matched"));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.invite.player"), plugin.getSettingsManager().getCommandClan()));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
    }
}
