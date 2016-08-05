package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.UUID;

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
public class BanCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();
        
        if (plugin.getPermissionsManager().has((Player) player, "simpleclans.mod.ban")) {
            if (arg.length == 1) {
                Player banned = Bukkit.getPlayer( arg[0] );
                
                UUID PlayerUniqueId = banned.getUniqueId();
                if (!plugin.getBansManager().isBanned(PlayerUniqueId)) {
                    Player pl = Bukkit.getPlayer(PlayerUniqueId);

                    if (pl != null) {
                        ChatBlock.sendMessage(pl, ChatColor.AQUA, lang.get("you.banned"));
                    }

                    plugin.getClanManager().ban(banned);
                    ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("player.added.to.banned.list"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED , lang.get("this.player.is.already.banned"));
            } 
            else ChatBlock.sendMessage(player, MessageFormat.format(lang.get("usage.ban.unban"), ChatColor.RED, plugin.getSettingsManager().getCommandClan()));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED , lang.get("insufficient.permissions"));
    }
}
