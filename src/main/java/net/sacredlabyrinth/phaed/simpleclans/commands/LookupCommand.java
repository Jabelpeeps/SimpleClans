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
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class LookupCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();
        
        Player player = (Player) sender;
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        String playerName = null;

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.lookup"))
                playerName = player.getName();
            else
                ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        }
        else if (arg.length == 1) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.lookup"))
                playerName = arg[0];
            else
                ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                                        lang.get("usage.lookup.tag"), settings.getCommandClan()));

        if (playerName != null) {
            ClanPlayer targetCp = plugin.getClanManager().getAnyClanPlayer(Bukkit.getPlayer( playerName).getUniqueId());
            ClanPlayer myCp = plugin.getClanManager().getClanPlayer(player);
            Clan myClan = myCp == null ? null : myCp.getClan();

            if (targetCp != null) {
                Clan targetClan = targetCp.getClan();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, MessageFormat.format( lang.get("s.player.info"), 
                        String.join( "", settings.getPageClanNameColor(), targetCp.getName(), subColor), " ", 
                                            headColor, Helper.generatePageSeparator( settings.getPageSep() ) ) );
                ChatBlock.sendBlank(player);

                String clanName = ChatColor.WHITE + lang.get("none");

                if (targetClan != null) {
                    clanName = String.join( "", settings.getClanChatBracketColor(),
                                                settings.getClanChatTagBracketLeft(),
                                                settings.getTagDefaultColor(),
                                                targetClan.getColorTag(),
                                                settings.getClanChatBracketColor(),
                                                settings.getClanChatTagBracketRight(),
                                                " ", settings.getPageClanNameColor(),
                                                targetClan.getName() );
                }

                String status = targetClan == null ? ChatColor.WHITE + lang.get("free.agent") 
                                                   : (targetCp.isLeader() ? settings.getPageLeaderColor() + lang.get("leader") 
                                                   : (targetCp.isTrusted() ? settings.getPageTrustedColor() + lang.get("trusted") 
                                                   : settings.getPageUnTrustedColor() + lang.get("untrusted")));
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

                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("clan.0"), clanName));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("rank.0"), rank));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("status.0"), status));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("kdr.0"), kdr));
                ChatBlock.sendMessage(player, "  ", subColor, lang.get("kill.totals"), 
                                                " ", headColor, "[", lang.get("rival"), ":", rival,
                                                " ", headColor, "", lang.get("neutral"), ":", neutral, 
                                                " ", headColor, "", lang.get("civilian"), ":", civilian, headColor, "]" );
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("deaths.0"), deaths));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("join.date.0"), joinDate));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("last.seen.0"), lastSeen));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("past.clans.0"), pastClans));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("inactive.0"), inactive));

                if (arg.length == 1 && !targetCp.equals(myCp)) {
                	String killType = ChatColor.GRAY + lang.get("neutral");

                    if (targetClan == null) {
                        killType = ChatColor.DARK_GRAY + lang.get("civilian");
                    }
                    else if (myClan != null && myClan.isRival(targetClan)) {
                        killType = ChatColor.WHITE + lang.get("rival");
                    }

                    ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("kill.type.0"), killType));
                }
                ChatBlock.sendBlank(player);
            }
            else {
                ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.player.data.found"));

                if (arg.length == 1 && myClan != null) {
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, MessageFormat.format(lang.get("kill.type.civilian"), ChatColor.DARK_GRAY));
                }
            }
        }
    }
}
