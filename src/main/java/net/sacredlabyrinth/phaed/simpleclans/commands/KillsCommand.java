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
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;

public class KillsCommand implements ClanCommand {

    @Override
    public void execute(CommandSender player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        StorageManager stor = plugin.getStorageManager();
        SettingsManager settings = plugin.getSettingsManager();
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (plugin.getPermissionsManager().has((Player) player, "simpleclans.member.kills")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer((Player) player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTrusted()) {
                        
                        if (arg.length == 1) {
                            player = Helper.getPlayer( arg[0] );
                        }

                        ChatBlock chatBlock = new ChatBlock();

                        chatBlock.setFlexibility(true, false);
                        chatBlock.setAlignment("l", "c");

                        chatBlock.addRow("  " + headColor + plugin.getLang("victim"), plugin.getLang("killcount"));

                        Map<String, Integer> killsPerPlayerUnordered = stor.getKillsPerPlayer(((Player)player).getUniqueId());

                        if (killsPerPlayerUnordered.isEmpty()) {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("nokillsfound"));
                            return;
                        }

                        Map<String, Integer> killsPerPlayer = Helper.sortByValue(killsPerPlayerUnordered);

                        for (Entry<String, Integer> playerKills : killsPerPlayer.entrySet()) {
                            int count = playerKills.getValue();

                            chatBlock.addRow("  " + playerKills.getKey(), ChatColor.AQUA + "" + count);
                        }

                        ChatBlock.saySingle(player, settings.getPageClanNameColor() + Helper.capitalize(((Player)player).getName()) + subColor + " " + plugin.getLang("kills") + " " + headColor + Helper.generatePageSeparator(settings.getPageSep()));
                        ChatBlock.sendBlank(player);

                        boolean more = chatBlock.sendBlock(player, settings.getPageSize());

                        if (more) {
                            stor.addChatBlock(player, chatBlock);
                            ChatBlock.sendBlank(player);
                            ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), settings.getCommandMore()));
                        }
                        ChatBlock.sendBlank(player);
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.stats"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
    }
}
