package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class GlobalffCommand implements ClanCommand {

    @Override
    public void execute(CommandSender player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();

        if (arg.length == 1) {
            String action = arg[0];

            if (action.equalsIgnoreCase(lang.get("allow"))) {
                if (settings.isGlobalff())
                    ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("global.friendly.fire.is.already.being.allowed"));
                else {
                    settings.setGlobalff(true);
                    ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("global.friendly.fire.is.set.to.allowed"));
                }
            }
            else if (action.equalsIgnoreCase(lang.get("auto"))) {
                if (!settings.isGlobalff())
                    ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("global.friendy.fire.is.already.being.managed.by.each.clan"));
                else {
                    settings.setGlobalff(false);
                    ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("global.friendy.fire.is.now.managed.by.each.clan"));
                }
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                    lang.get("usage.0.globalff.allow.auto"), settings.getCommandClan()));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(
                lang.get("usage.0.ff.allow.auto"), settings.getCommandClan()));
    }
}
