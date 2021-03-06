package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;

/**
 * @author phaed
 */
public class FfCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.ff")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                if (arg.length == 1) {
                    String action = arg[0];

                    if (action.equalsIgnoreCase(lang.get("allow"))) {
                        cp.setFriendlyFire(true);
                        plugin.getStorageManager().updateClanPlayer(cp);
                        ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("personal.friendly.fire.is.set.to.allowed"));
                    }
                    else if (action.equalsIgnoreCase(lang.get("auto"))) {
                        cp.setFriendlyFire(false);
                        plugin.getStorageManager().updateClanPlayer(cp);
                        ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("friendy.fire.is.now.managed.by.your.clan"));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.ff.allow.auto"), plugin.getSettingsManager().getCommandClan()));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.ff.allow.auto"), plugin.getSettingsManager().getCommandClan()));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
    }
}
