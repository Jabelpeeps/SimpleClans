package net.sacredlabyrinth.phaed.simpleclans.executors;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class MoreCommandExecutor implements CommandExecutor {
    SimpleClans plugin = SimpleClans.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (plugin.getSettingsManager().isBanned(player.getUniqueId())) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("banned"));
            return false;
        }

        ChatBlock chatBlock = plugin.getStorageManager().getChatBlock(player);

        if (chatBlock != null && chatBlock.size() > 0) {
            chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

            if (chatBlock.size() > 0) {
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, plugin.getSettingsManager().getPageHeadingsColor() + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
            }
            ChatBlock.sendBlank(player);
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("nothing.more.to.see"));

        return false;
    }
}
