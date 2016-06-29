package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class LookupCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        
        Player player = (Player) sender;
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        String playerName = null;

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.lookup"))
                playerName = player.getName();
            else
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
        else if (arg.length == 1) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.lookup"))
                playerName = arg[0];
            else
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.lookup.tag"), settings.getCommandClan()));

        if (playerName != null) {
            ClanPlayer targetCp = plugin.getClanManager().getAnyClanPlayer(Bukkit.getPlayer( playerName).getUniqueId());
            ClanPlayer myCp = plugin.getClanManager().getClanPlayer(player);
            Clan myClan = myCp == null ? null : myCp.getClan();

            if (targetCp != null) {
                Clan targetClan = targetCp.getClan();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, MessageFormat.format(plugin.getLang("s.player.info"), settings.getPageClanNameColor() + targetCp.getName() + subColor) + " " + headColor + Helper.generatePageSeparator(settings.getPageSep()));
                ChatBlock.sendBlank(player);

                String clanName = ChatColor.WHITE + plugin.getLang("none");

                if (targetClan != null) {
                    clanName = settings.getClanChatBracketColor() + settings.getClanChatTagBracketLeft() + settings.getTagDefaultColor() + targetClan.getColorTag() + settings.getClanChatBracketColor() + settings.getClanChatTagBracketRight() + " " + settings.getPageClanNameColor() + targetClan.getName();
                }

                String status = targetClan == null ? ChatColor.WHITE + plugin.getLang("free.agent") 
                                                   : (targetCp.isLeader() ? settings.getPageLeaderColor() + plugin.getLang("leader") 
                                                   : (targetCp.isTrusted() ? settings.getPageTrustedColor() + plugin.getLang("trusted") 
                                                   : settings.getPageUnTrustedColor() + plugin.getLang("untrusted")));
                String rank = ChatColor.WHITE + "" + Helper.parseColors(targetCp.getRank());
                String joinDate = ChatColor.WHITE + "" + targetCp.getJoinDateString();
                String lastSeen = ChatColor.WHITE + "" + targetCp.getLastSeenString();
                String inactive = ChatColor.WHITE + "" + targetCp.getInactiveDays() + subColor + "/" + ChatColor.WHITE + settings.getPurgePlayers() + " days";
                String rival = ChatColor.WHITE + "" + targetCp.getRivalKills();
                String neutral = ChatColor.WHITE + "" + targetCp.getNeutralKills();
                String civilian = ChatColor.WHITE + "" + targetCp.getCivilianKills();
                String deaths = ChatColor.WHITE + "" + targetCp.getDeaths();
                String kdr = ChatColor.YELLOW + "" + formatter.format(targetCp.getKDR());
                String pastClans = ChatColor.WHITE + "" + targetCp.getPastClansString(headColor + ", ");

                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(plugin.getLang("clan.0"), clanName));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(plugin.getLang("rank.0"), rank));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(plugin.getLang("status.0"), status));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(plugin.getLang("kdr.0"), kdr));
                ChatBlock.sendMessage(player, "  ", subColor, plugin.getLang("kill.totals")," ",headColor,"[",plugin.getLang("rival"),":",rival," ",headColor,"",plugin.getLang("neutral"),":",neutral," ",headColor,"",plugin.getLang("civilian"),":",civilian,headColor,"]");
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(plugin.getLang("deaths.0"), deaths));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(plugin.getLang("join.date.0"), joinDate));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(plugin.getLang("last.seen.0"), lastSeen));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(plugin.getLang("past.clans.0"), pastClans));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(plugin.getLang("inactive.0"), inactive));

                if (arg.length == 1 && !targetCp.equals(myCp)) {
                	String killType = ChatColor.GRAY + plugin.getLang("neutral");

                    if (targetClan == null) {
                        killType = ChatColor.DARK_GRAY + plugin.getLang("civilian");
                    }
                    else if (myClan != null && myClan.isRival(targetClan)) {
                        killType = ChatColor.WHITE + plugin.getLang("rival");
                    }

                    ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(plugin.getLang("kill.type.0"), killType));
                }
                ChatBlock.sendBlank(player);
            }
            else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.data.found"));

                if (arg.length == 1 && myClan != null) {
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, MessageFormat.format(plugin.getLang("kill.type.civilian"), ChatColor.DARK_GRAY));
                }
            }
        }
    }
}
