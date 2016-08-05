package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

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
public class ProfileCommand implements ClanCommand  {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        Clan clan = null;

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.profile")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
                }
                else {
                    if (cp.getClan().isVerified())
                        clan = cp.getClan();
                    else
                        ChatBlock.sendMessage(player, ChatColor.RED, lang.get("clan.is.not.verified"));
                }
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        }
        else if (arg.length == 1) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.profile")) {
                clan = plugin.getClanManager().getClan(arg[0]);

                if (clan == null)
                    ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.clan.matched"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                            lang.get("usage.0.profile.tag"), settings.getCommandClan()));

        if (clan != null) {
            if (clan.isVerified()) {
                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, settings.getPageClanNameColor(),
                                            Helper.capitalize(clan.getName()),
                                            subColor, " ", lang.get("profile"), " ",
                                            headColor, Helper.generatePageSeparator( settings.getPageSep() ) );
                ChatBlock.sendBlank(player);

                String name = String.join( "", settings.getClanChatBracketColor(),
                                               settings.getClanChatTagBracketLeft(),
                                               settings.getTagDefaultColor(),
                                               clan.getColorTag(),
                                               settings.getClanChatBracketColor(),
                                               settings.getClanChatTagBracketRight(), " ",
                                               settings.getPageClanNameColor(),
                                               clan.getName() );
                String leaders = clan.getLeadersString(settings.getPageLeaderColor(), subColor + ", ");
                String onlineCount = ChatColor.WHITE + "" + Helper.stripOffLinePlayers(clan.getMembers()).size();
                String membersOnline = onlineCount + subColor + "/" + ChatColor.WHITE + clan.getSize();
                String inactive = ChatColor.WHITE + "" + clan.getInactiveDays() + subColor + "/" + ChatColor.WHITE + 
                                                        (clan.isVerified() ? settings.getPurgeClan() 
                                                                           : settings.getPurgeUnverified() ) 
                                                        + " " + lang.get("days");
                String founded = ChatColor.WHITE + "" + clan.getFoundedString();
                String allies = ChatColor.WHITE + "" + clan.getAllyString(subColor + ", ");
                String rivals = ChatColor.WHITE + "" + clan.getRivalString(subColor + ", ");
                String kdr = ChatColor.YELLOW + "" + formatter.format(clan.getTotalKDR());
                String deaths = ChatColor.WHITE + "" + clan.getTotalDeaths();
                String rival = ChatColor.WHITE + "" + clan.getTotalRival();
                String neutral = ChatColor.WHITE + "" + clan.getTotalNeutral();
                String civ = ChatColor.WHITE + "" + clan.getTotalCivilian();
                String status = ChatColor.WHITE + "" + (clan.isVerified() ? settings.getPageTrustedColor() + lang.get("verified") 
                                                                          : settings.getPageUnTrustedColor() + lang.get("unverified"));

                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("name.0"), name));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("status.0"), status));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("leaders.0"), leaders));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("members.online.0"), membersOnline));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("kdr.0"), kdr));
                ChatBlock.sendMessage(player, "  ", subColor, lang.get("kill.totals"), " ", 
                                                    headColor, "[", lang.get("rival"), ":", rival, " ",
                                                    headColor, "", lang.get("neutral"), ":", neutral, " ",
                                                    headColor, "", lang.get("civilian"), ":", civ, headColor, "]");
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("deaths.0"), deaths));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("allies.0"), allies));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("rivals.0"), rivals));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("founded.0"), founded));
                ChatBlock.sendMessage(player, "  ", subColor, MessageFormat.format(lang.get("inactive.0"), inactive));

                ChatBlock.sendBlank(player);
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("clan.is.not.verified"));
        }
    }
}
