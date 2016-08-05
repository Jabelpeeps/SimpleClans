package net.sacredlabyrinth.phaed.simpleclans.executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;

public class GlobalCommandExecutor implements CommandExecutor {
    SimpleClans plugin;

    public GlobalCommandExecutor() {
        plugin = SimpleClans.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (strings.length == 0) return false;

        ClanPlayer cp;
        cp = plugin.getClanManager().getClanPlayer(player.getUniqueId());

        if (cp == null) return false;

        String subCommand = strings[0];
        LanguageManager lang = plugin.getLanguageManager();

        if (subCommand.equals(lang.get("on"))) {
            cp.setGlobalChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled global chat");
        }
        else if (subCommand.equals(lang.get("off"))) {
            cp.setGlobalChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled global chat");
        }
        else return true;

        return false;
    }
}
