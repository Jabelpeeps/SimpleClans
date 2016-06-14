package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 *
 * @author phaed
 */
public class BanCommand {
    
    public BanCommand() {}

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
                
                UUID PlayerUniqueId = banned.getUniqueId();
                if (!plugin.getSettingsManager().isBanned(PlayerUniqueId)) {
                    Player pl = SimpleClans.getInstance().getServer().getPlayer(PlayerUniqueId);

                    if (pl != null) {
                        ChatBlock.sendMessage(pl, ChatColor.AQUA + plugin.getLang("you.banned"));
                    }

                    plugin.getClanManager().ban(banned);
                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.added.to.banned.list"));
                }
                else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.already.banned"));
                }               
            } 
            else {
                ChatBlock.sendMessage(player, MessageFormat.format(plugin.getLang("usage.ban.unban"), ChatColor.RED, plugin.getSettingsManager().getCommandClan()));
            }
        }
        else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
