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
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 *
 * @author phaed
 */
public class ModtagCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.modtag")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {
                        if (arg.length == 1) {
                            String newtag = arg[0];
                            String cleantag = Helper.cleanTag(newtag);

                            if (Helper.stripColors(newtag).length() <= settings.getTagMaxLength()) {
                                if (!settings.hasDisallowedColor(newtag)) {
                                    if (Helper.stripColors(newtag).matches("[0-9a-zA-Z]*")) {
                                        if (cleantag.equals(clan.getTag())) {
                                            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(
                                                    lang.get("tag.changed.to.0"), Helper.parseColors(newtag)));
                                            clan.changeClanTag(newtag);
                                            plugin.getClanManager().updateDisplayName(player.getPlayer());
                                        }
                                        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("you.can.only.modify.the.color.and.case.of.the.tag"));
                                    }
                                    else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("your.clan.tag.can.only.contain.letters.numbers.and.color.codes"));
                                }
                                else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                                        lang.get("your.tag.cannot.contain.the.following.colors"), settings.getDisallowedColorString()));
                            }
                            else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                                    lang.get("your.clan.tag.cannot.be.longer.than.characters"), settings.getTagMaxLength()));
                        }
                        else {
                            ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.modtag.tag"), settings.getCommandClan()));
                            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("example.clan.modtag.4kfo.4l"));
                        }
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
