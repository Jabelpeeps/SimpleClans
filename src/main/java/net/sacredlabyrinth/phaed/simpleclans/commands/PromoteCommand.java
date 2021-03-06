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
public class PromoteCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.promote")) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isLeader(player)) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.leader.permissions"));
            return;
        }

        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.promote.member"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        Player promoted = Helper.getPlayer(arg[0]);

        if (promoted == null) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("the.member.to.be.promoted.must.be.online"));
            return;
        }

        if (!plugin.getPermissionsManager().has(promoted, "simpleclans.leader.promotable")) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("the.player.does.not.have.the.permissions.to.lead.a.clan"));
            return;
        }

        if (promoted.getName().equals(player.getName())) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("you.cannot.promote.yourself"));
            return;
        }

        if (!clan.isMember(promoted)) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("the.player.is.not.a.member.of.your.clan"));
            return;
        }

        if (clan.isLeader(promoted) && plugin.getSettingsManager().isConfirmationForPromote()) {
            ChatBlock.sendMessage(player, ChatColor.RED , lang.get("the.player.is.already.a.leader"));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("promoted.to.leader"), Helper.capitalize(promoted.getName())));       
        clan.promote(promoted.getUniqueId());
    }
}

