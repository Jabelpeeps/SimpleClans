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
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class AlliancesCommand implements ClanCommand  {

    @Override
    public void execute(CommandSender player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();
        SettingsManager settings = plugin.getSettingsManager();
        
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();

        if (arg.length == 0)  {
            if (plugin.getPermissionsManager().has((Player) player, "simpleclans.anyone.alliances")) {
                List<Clan> clans = plugin.getClanManager().getClans();
                plugin.getClanManager().sortClansByKDR(clans);

                ChatBlock chatBlock = new ChatBlock();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, settings.getServerName(), subColor, " " , lang.get("alliances"), " ", 
                                            headColor, Helper.generatePageSeparator( settings.getPageSep() ) );
                ChatBlock.sendBlank(player);

                chatBlock.setAlignment("l", "l");
                chatBlock.addRow("  ", headColor, lang.get("clan"), lang.get("allies"));

                for (Clan clan : clans) {
                    if (!clan.isVerified()) {
                        continue;
                    }

                    chatBlock.addRow("  ", ChatColor.AQUA.toString(), clan.getName(), clan.getAllyString( ChatColor.DARK_GRAY + ", "));
                }

                boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                if (more) {
                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + MessageFormat.format(lang.get("view.next.page"), settings.getCommandMore()));
                }

                ChatBlock.sendBlank(player);
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.clan.alliances"), settings.getCommandClan()));
    }
}
