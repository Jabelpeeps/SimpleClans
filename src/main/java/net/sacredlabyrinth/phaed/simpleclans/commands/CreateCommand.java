package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 *
 * @author phaed
 */
public class CreateCommand implements ClanCommand {
    
    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.create")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
        else if (arg.length < 2) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.create.tag"), settings.getCommandClan()));
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("example.clan.create"));
        } 
        else {
            String tag = arg[0];
            String cleanTag = Helper.cleanTag(arg[0]);

            String name = Helper.toMessage(Helper.removeFirst(arg));
            PermissionsManager perms = plugin.getPermissionsManager();

            if ( !perms.has(player, "simpleclans.mod.bypass") ) {
                if (cleanTag.length() > settings.getTagMaxLength()) {
                    ChatBlock.sendMessage( player, 
                            ChatColor.RED + MessageFormat.format(
                                    plugin.getLang("your.clan.tag.cannot.be.longer.than.characters"), settings.getTagMaxLength()));
                    return;
                }
                if (cleanTag.length() < settings.getTagMinLength()) {
                    ChatBlock.sendMessage(player, 
                            ChatColor.RED + MessageFormat.format(
                                    plugin.getLang("your.clan.tag.must.be.longer.than.characters"), settings.getTagMinLength()));                 
                    return;
                }
                if (settings.hasDisallowedColor(tag)) {
                    ChatBlock.sendMessage(player, 
                            ChatColor.RED + MessageFormat.format(
                                    plugin.getLang("your.tag.cannot.contain.the.following.colors"), settings.getDisallowedColorString()));
                    return;
                }
                if (Helper.stripColors(name).length() > settings.getClanMaxLength()) {
                    ChatBlock.sendMessage(player, 
                            ChatColor.RED + MessageFormat.format(
                                    plugin.getLang("your.clan.name.cannot.be.longer.than.characters"), settings.getClanMaxLength()));                   
                    return;
                } 
                if (Helper.stripColors(name).length() < settings.getClanMinLength()) {
                    ChatBlock.sendMessage(player, 
                            ChatColor.RED + MessageFormat.format(
                                    plugin.getLang("your.clan.name.must.be.longer.than.characters"), settings.getClanMinLength()));                    
                    return;
                }
                if (settings.isDisallowedWord(cleanTag.toLowerCase())) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("that.tag.name.is.disallowed"));
                    return;
                }
            }
            
            if (!cleanTag.matches("[0-9a-zA-Z]*")) {
                ChatBlock.sendMessage(player, 
                        ChatColor.RED + plugin.getLang("your.clan.tag.can.only.contain.letters.numbers.and.color.codes"));
                return;
            }
            if (name.contains("&")) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clan.name.cannot.contain.color.codes"));
                return;
            }    
            ClanManager clanManager = plugin.getClanManager();
            ClanPlayer cp = clanManager.getClanPlayer(player);

            if (cp != null) {
                ChatBlock.sendMessage(player, 
                        ChatColor.RED + MessageFormat.format(
                                plugin.getLang("you.must.first.resign"), cp.getClan().getName()));
                return;
            }
            if (clanManager.isClan(cleanTag)) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.with.this.tag.already.exists"));
                return;
            }
            if (clanManager.purchaseCreation(player)) {
                clanManager.createClan(player, tag, name);

                Clan clan = clanManager.getClan(tag);
                clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.created"), name));
                plugin.getStorageManager().updateClan(clan);

                if (settings.isRequireVerification() && !perms.has(player, "simpleclans.mod.verify")) {
                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("get.your.clan.verified.to.access.advanced.features"));
                }                                                        
            }           
        }  
    }
}
