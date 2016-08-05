package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;

/**
 *
 * @author phaed
 */
public class UnbanCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();

        if (plugin.getPermissionsManager().has((Player) player, "simpleclans.mod.ban")) {
            if (arg.length == 1) {
                Player banned = Bukkit.getPlayer( arg[0] );

                if (plugin.getBansManager().isBanned(banned.getUniqueId())) {

                    ChatBlock.sendMessage(banned, ChatColor.AQUA, lang.get("you.have.been.unbanned.from.clan.commands"));

                    plugin.getBansManager().removeBanned(banned.getUniqueId());
                    ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("player.removed.from.the.banned.list"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("this.player.is.not.banned"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.ban.unban"), plugin.getSettingsManager().getCommandClan()));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
    }
}
