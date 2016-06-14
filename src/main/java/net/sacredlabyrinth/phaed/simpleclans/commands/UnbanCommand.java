package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 *
 * @author phaed
 */
public class UnbanCommand {

    /**
     * Execute the command
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.ban")) {
            if (arg.length == 1) {
                Player banned = Bukkit.getPlayer( arg[0] );

                if (plugin.getSettingsManager().isBanned(banned.getUniqueId())) {

                    ChatBlock.sendMessage(banned, ChatColor.AQUA + plugin.getLang("you.have.been.unbanned.from.clan.commands"));

                    plugin.getSettingsManager().removeBanned(banned.getUniqueId());
                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.removed.from.the.banned.list"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.not.banned"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.ban.unban"), plugin.getSettingsManager().getCommandClan()));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
    }
}
