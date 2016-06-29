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
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class AllyCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        Player player = (Player) sender;

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.ally")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isVerified())  {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            return;
        }

        if (!clan.isLeader(player)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
            return;
        }

        if (arg.length != 2) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.ally"), settings.getCommandClan()));
            return;
        }

        if (clan.getSize() < settings.getClanMinSizeToAlly()) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("minimum.to.make.alliance"), settings.getClanMinSizeToAlly()));
            return;
        }

        String action = arg[0];
        Clan ally = plugin.getClanManager().getClan(arg[1]);

        if (ally == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
            return;
        }

        if (!ally.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("cannot.ally.with.an.unverified.clan"));
            return;
        }

        if (action.equals(plugin.getLang("add"))) {
            
            if (!clan.isAlly(ally)) {
                Set<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());

                if (!onlineLeaders.isEmpty()) {
                    plugin.getRequestManager().addAllyRequest(cp, ally, clan);
                    ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("leaders.have.been.asked.for.an.alliance"), Helper.capitalize(ally.getName())));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("at.least.one.leader.accept.the.alliance"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.already.allies"));
        }
        else if (action.equals(plugin.getLang("remove"))) {
            
            if (clan.isAlly(ally)) {
                clan.removeAlly(ally);
                ally.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.broken.the.alliance"), Helper.capitalize(clan.getName()), ally.getName()));
                clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.broken.the.alliance"), Helper.capitalize(cp.getName()), Helper.capitalize(ally.getName())));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clans.are.not.allies"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.ally"), settings.getCommandClan()));
    }
}