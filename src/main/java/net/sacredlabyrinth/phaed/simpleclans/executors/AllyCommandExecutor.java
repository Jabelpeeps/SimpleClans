package net.sacredlabyrinth.phaed.simpleclans.executors;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

public class AllyCommandExecutor implements CommandExecutor {
    SimpleClans plugin;

    public AllyCommandExecutor() {
        plugin = SimpleClans.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        SettingsManager settings = plugin.getSettingsManager();

        if (!settings.isAllyChatEnable()) return false;

        if (strings.length == 0) return false;

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) return false;

        String subCommand = strings[0];

        if (subCommand.equals(plugin.getLang("on"))) {
            cp.setAllyChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled ally chat");
        }
        else if (subCommand.equals(plugin.getLang("off"))) {
            cp.setAllyChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled ally chat");
        }
        else if (subCommand.equals(plugin.getLang("join"))) {
            cp.setChannel(ClanPlayer.Channel.ALLY);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have joined ally chat");
        }
        else if (subCommand.equals(plugin.getLang("leave"))) {
            cp.setChannel(ClanPlayer.Channel.NONE);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have left ally chat");
        }
        else if (subCommand.equals(plugin.getLang("mute"))) {
            if (!cp.isMutedAlly()) {
                cp.setMutedAlly(true);
                ChatBlock.sendMessage(player, ChatColor.AQUA + "You have muted ally chat");
            }
            else {
                cp.setMutedAlly(false);
                ChatBlock.sendMessage(player, ChatColor.AQUA + "You have unmuted ally chat");
            }
        }
        else {
            String code = "" + ChatColor.AQUA + ChatColor.WHITE + ChatColor.AQUA + ChatColor.BLACK;
            String message = String.join( "", code,
                                  settings.getAllyChatBracketColor(), 
                                  settings.getAllyChatTagBracketLeft(),  
                                  settings.getAllyChatTagColor(), 
                                  settings.getCommandAlly(),
                                  settings.getAllyChatBracketColor(), 
                                  settings.getAllyChatTagBracketRight(), " ",
                                  settings.getAllyChatNameColor(), 
                                  settings.getAllyChatPlayerBracketLeft(), 
                                  player.getName(), 
                                  settings.getAllyChatPlayerBracketRight(), " ", 
                                  settings.getAllyChatMessageColor(),
                                  String.join( "", strings ) );
            SimpleClans.log(message);

            Player self = cp.toPlayer();
            ChatBlock.sendMessage(self, message);

            Set<ClanPlayer> allies = cp.getClan().getAllAllyMembers();
            allies.addAll(cp.getClan().getMembers());

            for (ClanPlayer ally : allies) {
                if (ally.isMutedAlly()) {
                    continue;
                }
                Player member = ally.toPlayer();
                if (player.getUniqueId().equals(ally.getUniqueId())) {
                    continue;
                }
                
                ChatBlock.sendMessage(member, message);
            }
        }
        return false;
    }
}
