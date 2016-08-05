package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
public class CoordsCommand implements ClanCommand  {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.coords")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTrusted()) {
                        if (arg.length == 0) {
                            ChatBlock chatBlock = new ChatBlock();

                            chatBlock.setFlexibility(true, false, false, false);
                            chatBlock.setAlignment("l", "c", "c", "c");

                            chatBlock.addRow("  " + headColor + lang.get("name"), lang.get("distance"), lang.get("coords.upper"), lang.get("world"));

                            Set<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getMembers());

                            Map<Integer, List<String>> rows = new TreeMap<>();

                            for (ClanPlayer cpm : members) {
                                Player p = cpm.toPlayer();

                                if (p != null) {
                                    String name = (cpm.isLeader() ? settings.getPageLeaderColor() 
                                                                  : (cpm.isTrusted() ? settings.getPageTrustedColor() 
                                                                                     : settings.getPageUnTrustedColor() ) ) + cpm.getName();
                                    Location loc = p.getLocation();
                                    int distance = (int) Math.ceil(loc.toVector().distance(player.getLocation().toVector()));
                                    String coords = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
                                    String world = loc.getWorld().getName();

                                    List<String> cols = new ArrayList<>();
                                    cols.add("  " + name);
                                    cols.add(ChatColor.AQUA + "" + distance);
                                    cols.add(ChatColor.WHITE + "" + coords);
                                    cols.add(world);
                                    rows.put(distance, cols);
                                }
                            }
                            if (!rows.isEmpty()) {
                                for (List<String> col : rows.values())  {
                                    chatBlock.addRow(col.get(0), col.get(1), col.get(2), col.get(3));
                                }

                                ChatBlock.sendBlank(player);
                                ChatBlock.saySingle(player, settings.getPageClanNameColor(),
                                                            Helper.capitalize( clan.getName() ),
                                                            subColor, " " , lang.get("coords"), 
                                                            " ", headColor, 
                                                            Helper.generatePageSeparator( settings.getPageSep() ) );
                                ChatBlock.sendBlank(player);

                                boolean more = chatBlock.sendBlock(player, settings.getPageSize());

                                if (more) {
                                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                                    ChatBlock.sendBlank(player);
                                    ChatBlock.sendMessage(player, headColor + MessageFormat.format(
                                            lang.get("view.next.page"), settings.getCommandMore()));
                                }
                                ChatBlock.sendBlank(player);
                            }
                            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("you.are.the.only.member.online"));
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.coords"), settings.getCommandClan()));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("only.trusted.players.can.access.clan.coords"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("clan.is.not.verified"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
    }
}
