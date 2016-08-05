package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
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
public class StatsCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.stats")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTrusted()) {
                        if (arg.length == 0) {
                            ChatBlock chatBlock = new ChatBlock();

                            ChatBlock.saySingle(player, settings.getPageClanNameColor(), Helper.capitalize(clan.getName()), 
                                                        subColor, " ", lang.get("stats"), " ", 
                                                        headColor, Helper.generatePageSeparator(settings.getPageSep()));
                            ChatBlock.sendBlank(player);

                            ChatBlock.sendMessage(player, headColor , lang.get("kdr"), " = ", subColor, lang.get("kill.death.ratio"));
                            ChatBlock.sendMessage(player, headColor, lang.get("weights"), " = ", lang.get("rival"), ": ", 
                                                          subColor, String.valueOf( settings.getKwRival() ), 
                                                          headColor, " ", lang.get("neutral"), ": " , 
                                                          subColor, String.valueOf( settings.getKwNeutral() ), 
                                                          headColor, " ", lang.get("civilian"), ": ", 
                                                          subColor, String.valueOf( settings.getKwCivilian() ) );
                            ChatBlock.sendBlank(player);

                            chatBlock.setFlexibility(true, false, false, false, false, false, false);
                            chatBlock.setAlignment("l", "c", "c", "c", "c", "c", "c");

                            chatBlock.addRow("  ", headColor, lang.get("name"), lang.get("kdr"), lang.get("rival"), 
                                            lang.get("neutral"), lang.get("civilian.abbreviation"), lang.get("deaths"));

                            List<ClanPlayer> leaders = clan.getLeaders();
                            plugin.getClanManager().sortClanPlayersByKDR(leaders);

                            List<ClanPlayer> members = clan.getNonLeaders();
                            plugin.getClanManager().sortClanPlayersByKDR(members);

                            for (ClanPlayer cpm : leaders) {
                                String name = (cpm.isLeader() ? settings.getPageLeaderColor() 
                                                              : (cpm.isTrusted() ? settings.getPageTrustedColor() 
                                                                                 : settings.getPageUnTrustedColor())) 
                                            + cpm.getName();
                                String rival = NumberFormat.getInstance().format(cpm.getRivalKills());
                                String neutral = NumberFormat.getInstance().format(cpm.getNeutralKills());
                                String civilian = NumberFormat.getInstance().format(cpm.getCivilianKills());
                                String deaths = NumberFormat.getInstance().format(cpm.getDeaths());
                                String kdr = formatter.format(cpm.getKDR());

                                chatBlock.addRow("  ", name, ChatColor.YELLOW + kdr, ChatColor.WHITE + rival, 
                                                        ChatColor.GRAY + neutral, ChatColor.DARK_GRAY + civilian, 
                                                        ChatColor.DARK_RED + deaths);
                            }

                            for (ClanPlayer cpm : members) {
                                String name = (cpm.isLeader() ? settings.getPageLeaderColor() 
                                                              : (cpm.isTrusted() ? settings.getPageTrustedColor() 
                                                                                 : settings.getPageUnTrustedColor())) 
                                            + cpm.getName();
                                String rival = NumberFormat.getInstance().format(cpm.getRivalKills());
                                String neutral = NumberFormat.getInstance().format(cpm.getNeutralKills());
                                String civilian = NumberFormat.getInstance().format(cpm.getCivilianKills());
                                String deaths = NumberFormat.getInstance().format(cpm.getDeaths());
                                String kdr = formatter.format(cpm.getKDR());

                                chatBlock.addRow("  ", name, ChatColor.YELLOW + kdr, ChatColor.WHITE + rival, 
                                                        ChatColor.GRAY + neutral, ChatColor.DARK_GRAY + civilian, 
                                                        ChatColor.DARK_RED + deaths);
                            }

                            boolean more = chatBlock.sendBlock(player, settings.getPageSize());

                            if (more) {
                                plugin.getStorageManager().addChatBlock(player, chatBlock);
                                ChatBlock.sendBlank(player);
                                ChatBlock.sendMessage(player, headColor, MessageFormat.format(
                                                        lang.get("view.next.page"), settings.getCommandMore()));
                            }

                            ChatBlock.sendBlank(player);
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.stats"), settings.getCommandClan()));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("only.trusted.players.can.access.clan.stats"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("clan.is.not.verified"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
    }
}
