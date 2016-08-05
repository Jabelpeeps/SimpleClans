package net.sacredlabyrinth.phaed.simpleclans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;

/**
 * @author phaed
 */
public class ReloadCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        PermissionsManager perms = plugin.getPermissionsManager();
        LanguageManager lang = plugin.getLanguageManager();

        if    ( sender instanceof Player 
                && !perms.has( (Player)sender, "simpleclans.admin.reload") ) {
        	ChatBlock.sendMessage(sender, ChatColor.RED, "Does not match a clan command");
        	return;
        }

        plugin.getSettingsManager().load();
        lang.load();
        plugin.getStorageManager().importFromDatabase();
        perms.loadPermissions();

        for (Clan clan : plugin.getClanManager().getClans()) {
            perms.updateClanPermissions(clan);
        }
        ChatBlock.sendMessage(sender, ChatColor.AQUA, lang.get("configuration.reloaded"));
    }
}
