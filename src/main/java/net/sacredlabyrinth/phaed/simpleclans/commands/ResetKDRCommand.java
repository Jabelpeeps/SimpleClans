package net.sacredlabyrinth.phaed.simpleclans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;

/**
 * @author phaed
 */
public class ResetKDRCommand  implements ClanCommand {

    @Override
    public void execute(CommandSender player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        if (plugin.getPermissionsManager().has((Player) player, "simpleclans.admin.resetkdr")) {
            for (ClanPlayer cp : SimpleClans.getInstance().getClanManager().getAllClanPlayers()) {
                cp.setCivilianKills(0);
                cp.setNeutralKills(0);
                cp.setRivalKills(0);
                cp.setDeaths(0);
            }
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("kdr.of.all.players.was.reset"));
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
