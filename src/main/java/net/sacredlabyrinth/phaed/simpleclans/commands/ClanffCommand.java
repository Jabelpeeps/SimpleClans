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
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;

/**
 *
 * @author phaed
 */
public class ClanffCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.ff")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isLeader(player)) {
                    if (arg.length == 1) {
                        String action = arg[0];

                        if (action.equalsIgnoreCase(lang.get("allow"))) {
                            clan.addBb(player.getName(), ChatColor.AQUA + lang.get("clan.wide.friendly.fire.is.allowed"));
                            clan.setFriendlyFire(true);
                            plugin.getStorageManager().updateClan(clan);
                        }
                        else if (action.equalsIgnoreCase(lang.get("block"))) {
                            clan.addBb(player.getName(), ChatColor.AQUA + lang.get("clan.wide.friendly.fire.blocked"));
                            clan.setFriendlyFire(false);
                            plugin.getStorageManager().updateClan(clan);
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                                lang.get("usage.clanff"), plugin.getSettingsManager().getCommandClan()));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                            lang.get("usage.clanff"), plugin.getSettingsManager().getCommandClan()));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
    }
}
