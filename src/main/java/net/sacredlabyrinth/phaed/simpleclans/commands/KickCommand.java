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

/**
 *
 * @author phaed
 */
public class KickCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.kick")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)  {
                Clan clan = cp.getClan();

                if (clan.isLeader(player)) {
                    if (arg.length == 1) {
                        Player kicked = Bukkit.getPlayer( arg[0] );
                        
                        if (kicked != null) {
                            if (!kicked.getName().equalsIgnoreCase(player.getName())) {
                                if (clan.isMember(kicked)) {
                                    if (!clan.isLeader(kicked)) {
                                        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("has.been.kicked.by"), Helper.capitalize(kicked.getName()), player.getName()));
                                        clan.removePlayerFromClan(kicked.getUniqueId());                                        
                                    }
                                    else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("you.cannot.kick.another.leader"));
                                }
                                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("the.player.is.not.a.member.of.your.clan"));
                            }
                            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("you.cannot.kick.yourself"));
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.player.matched"));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.kick.player"), plugin.getSettingsManager().getCommandClan()));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
    }
}
