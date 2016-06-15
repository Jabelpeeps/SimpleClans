package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;

/**
 * @author phaed
 */
public class RivalriesCommand implements ClanCommand  {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (arg.length == 0)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.rivalries"))
            {
                List<Clan> clans = plugin.getClanManager().getClans();
                plugin.getClanManager().sortClansByKDR(clans);

                ChatBlock chatBlock = new ChatBlock();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang("rivalries") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, headColor + plugin.getLang("legend") +  ChatColor.DARK_RED + " [" + plugin.getLang("war") + "]");
                ChatBlock.sendBlank(player);

                chatBlock.setAlignment("l", "l");
                chatBlock.addRow(plugin.getLang("clan"), plugin.getLang("rivals"));

                for (Clan clan : clans)
                {
                    if (!clan.isVerified())
                    {
                        continue;
                    }

                    chatBlock.addRow("  " + ChatColor.AQUA + clan.getName(), clan.getRivalString(ChatColor.DARK_GRAY + ", "));
                }

                boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                if (more)
                {
                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
                }

                ChatBlock.sendBlank(player);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.rivalries"), plugin.getSettingsManager().getCommandClan()));
        }
    }
}
