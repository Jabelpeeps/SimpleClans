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
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class RivalCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.rival")) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
            return;
        }
        ClanManager clanMan = plugin.getClanManager();
        ClanPlayer cp = clanMan.getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));  
            return;
        }
        Clan clan = cp.getClan();

        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("clan.is.not.verified"));
            return;
        }
        if (clan.isUnrivable()) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("your.clan.cannot.create.rivals"));
            return;
        }
        if (!clan.isLeader(player)) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));
            return;
        }
        if (arg.length != 2) {
            ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                                    lang.get("usage.rival"), settings.getCommandClan()));
            return;
        }
        if (clan.getSize() < settings.getClanMinSizeToRival()) {
            ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                                    lang.get("min.players.rivalries"), settings.getClanMinSizeToRival()));
            return;
        }
        String action = arg[0];
        Clan rival = clanMan.getClan(arg[1]);

        if (rival == null) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.clan.matched"));
            return;
        }
        if (settings.isUnrivable(rival.getTag())) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("the.clan.cannot.be.rivaled"));
            return;
        }
        if (!rival.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("cannot.rival.an.unverified.clan"));
            return;
        }
        if (action.equals(lang.get("add"))) {
            
            if (clan.reachedRivalLimit()) {
                ChatBlock.sendMessage(player, ChatColor.RED, lang.get("rival.limit.reached"));
                return;
            }
            if (clan.isRival(rival)) {
                ChatBlock.sendMessage(player, ChatColor.RED, lang.get("your.clans.are.already.rivals"));
                return;
            }
            clan.addRival(rival);
            rival.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(
                    lang.get("has.initiated.a.rivalry"), Helper.capitalize(clan.getName()), rival.getName()));
            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(
                    lang.get("has.initiated.a.rivalry"), Helper.capitalize(player.getName()), Helper.capitalize(rival.getName())));    
        }
        else if (action.equals(lang.get("remove"))) {
            
            if (!clan.isRival(rival)) {
                ChatBlock.sendMessage(player, ChatColor.RED, lang.get("your.clans.are.not.rivals"));
                return;
            }
            plugin.getRequestManager().addRivalryBreakRequest(cp, rival, clan);
            ChatBlock.sendMessage(player, ChatColor.AQUA, MessageFormat.format(
                                lang.get("leaders.asked.to.end.rivalry"), Helper.capitalize(rival.getName())));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                                lang.get("usage.rival"), settings.getCommandClan()));
    }
}
