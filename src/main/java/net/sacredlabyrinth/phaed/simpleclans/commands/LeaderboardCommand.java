package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class LeaderboardCommand implements ClanCommand  {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();
        SettingsManager settings = plugin.getSettingsManager();
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.leaderboard")) {
                List<ClanPlayer> clanPlayers = plugin.getClanManager().getAllClanPlayers();
                plugin.getClanManager().sortClanPlayersByKDR(clanPlayers);

                ChatBlock chatBlock = new ChatBlock();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, settings.getServerName(), subColor, " ", lang.get("leaderboard.command"), 
                        " ", headColor, Helper.generatePageSeparator(settings.getPageSep()));
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, headColor, MessageFormat.format(
                        lang.get("total.clan.players.0"), subColor, clanPlayers.size()));
                ChatBlock.sendBlank(player);

                chatBlock.setAlignment("c", "l", "c", "c", "c", "c");
                chatBlock.addRow("  " + headColor + lang.get("rank"), lang.get("player"), lang.get("kdr"), lang.get("clan"), lang.get("seen"));

                int rank = 1;

                for (ClanPlayer cp : clanPlayers) {
                    Player p = cp.toPlayer();

                    boolean isOnline = false;

                    if (p != null) {
                        isOnline = true;
                    }


                    String name = (cp.isLeader() ? settings.getPageLeaderColor() 
                                                 : (cp.isTrusted() ? settings.getPageTrustedColor() 
                                                                   : settings.getPageUnTrustedColor())) + cp.getName();
                    String lastSeen = isOnline ? ChatColor.GREEN + lang.get("online") 
                                               : ChatColor.WHITE + cp.getLastSeenDaysString();

                    String clanTag = ChatColor.WHITE + lang.get("free.agent");

                    if (cp.getClan() != null) {
                        clanTag = cp.getClan().getColorTag();
                    }

                    chatBlock.addRow("  " + rank, name, ChatColor.YELLOW + "" + formatter.format(cp.getKDR()), ChatColor.WHITE + clanTag, lastSeen);
                    rank++;
                }
                boolean more = chatBlock.sendBlock(player, settings.getPageSize());

                if (more) {
                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor, MessageFormat.format(lang.get("view.next.page"), settings.getCommandMore()));
                }
                ChatBlock.sendBlank(player);
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.leaderboard"), settings.getCommandClan()));
    }
}
