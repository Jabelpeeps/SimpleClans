package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.List;

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
public class RosterCommand implements ClanCommand  {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();

        Clan clan = null;

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.roster")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp == null)
                    ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
                else
                    clan = cp.getClan();
            } 
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        } 
        else if (arg.length == 1) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.roster")) {
                clan = plugin.getClanManager().getClan(arg[0]);

                if (clan == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED, lang.get("no.clan.matched"));
                }
            } 
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        } 
        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                                            lang.get("usage.0.roster.tag"), settings.getCommandClan()));

        if (clan != null) {
            if (clan.isVerified()) {
                ChatBlock chatBlock = new ChatBlock();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, settings.getPageClanNameColor(), 
                                            Helper.capitalize(clan.getName()), 
                                            subColor, " ", lang.get("roster"), " ", 
                                            headColor, Helper.generatePageSeparator(settings.getPageSep()));
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, headColor, lang.get("legend"), " ", settings.getPageLeaderColor(), 
                                              lang.get("leader"), headColor, ", ", settings.getPageTrustedColor(), 
                                              lang.get("trusted"), headColor, ", ", settings.getPageUnTrustedColor(), 
                                              lang.get("untrusted"));
                ChatBlock.sendBlank(player);

                chatBlock.setFlexibility(false, true, false, true);
                chatBlock.addRow("  ", headColor, lang.get("player"), lang.get("rank"), lang.get("seen"));

                List<ClanPlayer> leaders = clan.getLeaders();
                plugin.getClanManager().sortClanPlayersByLastSeen(leaders);

                List<ClanPlayer> members = clan.getNonLeaders();
                plugin.getClanManager().sortClanPlayersByLastSeen(members);

                for (ClanPlayer cp : leaders) {

                    Player p = cp.toPlayer();

                    String name = settings.getPageLeaderColor() + cp.getName();
                    String lastSeen = p != null && p.isOnline() && !Helper.isVanished(p) ? ChatColor.GREEN + lang.get("online") 
                                                                                         : ChatColor.WHITE + cp.getLastSeenDaysString();

                    chatBlock.addRow("  " + name, ChatColor.YELLOW + Helper.parseColors(cp.getRank()), lastSeen);
                }

                for (ClanPlayer cp : members) {
                    Player p = cp.toPlayer();

                    String name = (cp.isTrusted() ? settings.getPageTrustedColor() 
                                                  : settings.getPageUnTrustedColor()) + cp.getName();
                    String lastSeen = p != null && p.isOnline() && !Helper.isVanished(p) ? ChatColor.GREEN + lang.get("online") 
                                                                                         : ChatColor.WHITE + cp.getLastSeenDaysString();

                    chatBlock.addRow("  " + name, ChatColor.YELLOW + Helper.parseColors(cp.getRank()), lastSeen);
                }

                boolean more = chatBlock.sendBlock(player, settings.getPageSize());

                if (more) {
                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + MessageFormat.format(lang.get("view.next.page"), settings.getCommandMore()));
                }

                ChatBlock.sendBlank(player);
            } 
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("clan.is.not.verified"));
        } 
        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.roster.tag"), settings.getCommandClan()));
    }
}
