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
public class RivalriesCommand implements ClanCommand  {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.rivalries")) {
                List<Clan> clans = plugin.getClanManager().getClans();
                plugin.getClanManager().sortClansByKDR(clans);

                ChatBlock chatBlock = new ChatBlock();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, settings.getServerName(), subColor, " ", lang.get("rivalries"), " ", 
                                                    headColor, Helper.generatePageSeparator(settings.getPageSep()));
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, headColor, lang.get("legend"), ChatColor.DARK_RED + " [", lang.get("war"), "]" );
                ChatBlock.sendBlank(player);

                chatBlock.setAlignment("l", "l");
                chatBlock.addRow(lang.get("clan"), lang.get("rivals"));

                for (Clan clan : clans) {
                    if (!clan.isVerified()) continue;

                    chatBlock.addRow("  " + ChatColor.AQUA + clan.getName(), clan.getRivalString(ChatColor.DARK_GRAY + ", "));
                }

                boolean more = chatBlock.sendBlock(player, settings.getPageSize());

                if (more) {
                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + MessageFormat.format(
                                                lang.get("view.next.page"), settings.getCommandMore()));
                }
                ChatBlock.sendBlank(player);
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(
                                                lang.get("usage.0.rivalries"), settings.getCommandClan()));
    }
}
