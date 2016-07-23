package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;


/**
 * @author phaed
 */
public class ListCommand implements ClanCommand {
    
    @Override
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        if ( sender instanceof ConsoleCommandSender 
                || plugin.getPermissionsManager().has( (Player) sender, "simpleclans.anyone.list") ) {
            
            if (arg.length == 0) {
                List<Clan> clans = plugin.getClanManager().getClans();
                plugin.getClanManager().sortClansByKDR(clans);

                if (!clans.isEmpty()) {
                    ChatBlock chatBlock = new ChatBlock();

                    ChatBlock.sendBlank(sender);
                    ChatBlock.saySingle(sender, settings.getServerName() + subColor + " " + plugin.getLang("clans.lower") + " " + headColor + Helper.generatePageSeparator(settings.getPageSep()));
                    ChatBlock.sendBlank(sender);
                    ChatBlock.sendMessage(sender, headColor + plugin.getLang("total.clans") + " " + subColor + clans.size());
                    ChatBlock.sendBlank(sender);

                    chatBlock.setAlignment("c", "l", "c", "c");
                    chatBlock.setFlexibility(false, true, false, false);

                    chatBlock.addRow("  " + headColor + plugin.getLang("rank"), plugin.getLang("name"), plugin.getLang("kdr"), plugin.getLang("members"));

                    int rank = 1;

                    for (Clan clan : clans) {
                        if (!settings.isShowUnverifiedOnList() && !clan.isVerified()) {
                        	continue;
                        }

                        String tag = settings.getClanChatBracketColor() 
                                    + settings.getClanChatTagBracketLeft() 
                                    + settings.getTagDefaultColor() + clan.getColorTag() 
                                    + settings.getClanChatBracketColor() 
                                    + settings.getClanChatTagBracketRight();
                        
                        String name = (clan.isVerified() ? settings.getPageClanNameColor() 
                                                         : ChatColor.GRAY) + clan.getName();
                        String fullname = tag + " " + name;
                        String size = ChatColor.WHITE + "" + clan.getSize();
                        String kdr = clan.isVerified() ? ChatColor.YELLOW + "" + formatter.format(clan.getTotalKDR()) 
                                                       : "";

                        chatBlock.addRow("  " + rank, fullname, kdr, size);
                        rank++;
                    }

                    boolean more = chatBlock.sendBlock(sender, settings.getPageSize());

                    if (more) {
                        plugin.getStorageManager().addChatBlock(sender, chatBlock);
                        ChatBlock.sendBlank(sender);
                        ChatBlock.sendMessage(sender, headColor + MessageFormat.format(plugin.getLang("view.next.page"), settings.getCommandMore()));
                    }

                    ChatBlock.sendBlank(sender);
                }
                else ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("no.clans.have.been.created"));
            }
            else ChatBlock.sendMessage(sender, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.list"), settings.getCommandClan()));
        }
        else ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("insufficient.permissions"));
    }
}
