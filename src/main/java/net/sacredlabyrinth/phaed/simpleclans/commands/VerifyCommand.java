package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class VerifyCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            Clan clan = cp == null ? null : cp.getClan();

            if (    clan != null 
                    && !clan.isVerified() 
                    && settings.isRequireVerification() 
                    && settings.isePurchaseVerification()) {
                
                if (arg.length == 0 && plugin.getClanManager().purchaseVerification(player)) {
                	clan.verifyClan();
                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(
                                            lang.get("clan.0.has.been.verified"), clan.getName()));
                    ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("the.clan.has.been.verified"));
                }
            }
            else if (plugin.getPermissionsManager().has(player, "simpleclans.mod.verify")) {
                if (arg.length == 1) {
                    Clan cclan = plugin.getClanManager().getClan(arg[0]);

                    if (cclan != null) {
                        if (!cclan.isVerified()) {
                            cclan.verifyClan();
                            cclan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("clan.0.has.been.verified"), cclan.getName()));
                            ChatBlock.sendMessage(player, ChatColor.AQUA, lang.get("the.clan.has.been.verified"));
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("the.clan.is.already.verified"));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("the.clan.does.not.exist"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.verify.tag"), settings.getCommandClan()));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
        }
        else if (arg.length == 1) {
            Clan cclan = plugin.getClanManager().getClan(arg[0]);

            if (cclan != null) {
                if (!cclan.isVerified()) {
                    cclan.verifyClan();
                    cclan.addBb(sender.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("clan.0.has.been.verified"), cclan.getName()));
                    ChatBlock.sendMessage(sender, ChatColor.AQUA, lang.get("the.clan.has.been.verified"));
                }
                else ChatBlock.sendMessage(sender, ChatColor.RED, lang.get("the.clan.is.already.verified"));
            }
            else ChatBlock.sendMessage(sender, ChatColor.RED, lang.get("the.clan.does.not.exist"));
        }
        else ChatBlock.sendMessage(sender, ChatColor.RED, MessageFormat.format(lang.get("usage.0.verify.tag"), settings.getCommandClan()));  
    }
}
