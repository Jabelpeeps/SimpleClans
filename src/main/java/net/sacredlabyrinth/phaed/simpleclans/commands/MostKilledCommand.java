package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

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

public class MostKilledCommand implements ClanCommand {

    @Override
    public void execute(CommandSender player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();

        if (plugin.getPermissionsManager().has((Player) player, "simpleclans.mod.mostkilled")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer((Player) player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTrusted()) {
                        ChatBlock chatBlock = new ChatBlock();

                        chatBlock.setFlexibility(true, false, false);
                        chatBlock.setAlignment("l", "c", "l");

                        chatBlock.addRow("  " + headColor + lang.get("victim"), headColor + lang.get("killcount"), headColor + lang.get("attacker"));

                        Map<String, Integer> killsPerPlayerUnordered = plugin.getStorageManager().getMostKilled();

                        if (killsPerPlayerUnordered.isEmpty()) {
                            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("nokillsfound"));
                            return;
                        }

                        Map<String, Integer> killsPerPlayer = Helper.sortByValue(killsPerPlayerUnordered);

                        for (Entry<String, Integer> attackerVictim : killsPerPlayer.entrySet()) {
                            String[] split = attackerVictim.getKey().split(" ");

                            if (split.length < 2) {
                                continue;
                            }

                            int count = attackerVictim.getValue();
                            String attacker = split[0];
                            String victim = split[1];

                            chatBlock.addRow("  " + ChatColor.WHITE + victim, ChatColor.AQUA + "" + count, ChatColor.YELLOW + attacker);
                        }

                        ChatBlock.saySingle(player, settings.getServerName(), subColor, " ", lang.get("mostkilled"),
                                                    " ", headColor, Helper.generatePageSeparator(settings.getPageSep()));
                        ChatBlock.sendBlank(player);

                        boolean more = chatBlock.sendBlock(player, settings.getPageSize());

                        if (more) {
                            plugin.getStorageManager().addChatBlock(player, chatBlock);
                            ChatBlock.sendBlank(player);
                            ChatBlock.sendMessage(player, headColor, MessageFormat.format(lang.get("view.next.page"), settings.getCommandMore()));
                        }
                        ChatBlock.sendBlank(player);
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
