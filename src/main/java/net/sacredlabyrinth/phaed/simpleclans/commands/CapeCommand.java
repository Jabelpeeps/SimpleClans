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
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;

/**
 * @author phaed
 */
public class CapeCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.cape")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {
                        if (arg.length == 1) {
                            String url = arg[0];

                            if (url.contains(".png")) {
                                if (Helper.testURL(url)) {
                                    clan.addBb( player.getName(), ChatColor.AQUA + MessageFormat.format(
                                            lang.get("changed.the.clan.cape"), Helper.capitalize(player.getName())));
                                    clan.setClanCape(url);
                                }
                                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("url.error"));
                            }
                            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("cape.must.be.png"));
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                                lang.get("usage.cape.url"), plugin.getSettingsManager().getCommandClan()));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("clan.is.not.verified"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
    }
}
