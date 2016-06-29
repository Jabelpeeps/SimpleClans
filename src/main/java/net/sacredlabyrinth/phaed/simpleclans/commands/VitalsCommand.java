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
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class VitalsCommand implements ClanCommand  {
 
    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.vitals")) {
            ChatBlock.sendMessage(player, ChatColor.RED.toString(), plugin.getLang("insufficient.permissions"));
            return;
        }  

        ClanManager clanMan = plugin.getClanManager();
        ClanPlayer cp = clanMan.getClanPlayer(player);
        
        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED.toString(), plugin.getLang("not.a.member.of.any.clan"));
            return;
        } 
        
        Clan clan = cp.getClan();

        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED.toString(), plugin.getLang("clan.is.not.verified"));
            return;
        }
        
        if (!cp.isTrusted()) {
            ChatBlock.sendMessage(player, ChatColor.RED.toString(), plugin.getLang("only.trusted.players.can.access.clan.vitals"));
            return;
        }

        SettingsManager settings = plugin.getSettingsManager();
        
        if (arg.length != 0) {
            ChatBlock.sendMessage(player, ChatColor.RED.toString(), MessageFormat.format(plugin.getLang("usage.0.vitals"), settings.getCommandClan())); 
            return;
        }

        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();
        
        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, 
                settings.getPageClanNameColor(), Helper.capitalize( clan.getName() ), 
                subColor, " ", plugin.getLang("vitals"), " ",
                headColor, Helper.generatePageSeparator( settings.getPageSep() ) );
        ChatBlock.sendBlank(player);
        ChatBlock.sendMessage(player, 
                headColor, plugin.getLang("weapons"), ": ", 
                MessageFormat.format( plugin.getLang("0.s.sword.1.2.b.bow.3.4.a.arrow"), 
                        ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE));
        ChatBlock.sendMessage(player, 
                headColor, plugin.getLang("materials"), ": ", ChatColor.AQUA.toString(), plugin.getLang("diamond"),
                ChatColor.DARK_GRAY.toString(), ", ", ChatColor.YELLOW.toString(), plugin.getLang("gold"),
                ChatColor.DARK_GRAY.toString(), ", ", ChatColor.GRAY.toString(), plugin.getLang("stone"),
                ChatColor.DARK_GRAY.toString(), ", ", ChatColor.WHITE.toString(), plugin.getLang("iron"),
                ChatColor.DARK_GRAY.toString(), ", ", ChatColor.GOLD.toString(), plugin.getLang("wood"));

        ChatBlock.sendBlank(player);
        
        ChatBlock chatBlock = new ChatBlock();
        chatBlock.setFlexibility(true, false, false, false, false, false);
        chatBlock.setAlignment("l", "l", "l", "c", "c", "c");

        chatBlock.addRow("  ", headColor, plugin.getLang("name"), plugin.getLang("health"), plugin.getLang("hunger"), 
                                    plugin.getLang("food"), plugin.getLang("armor"), plugin.getLang("weapons"));
        
        for (ClanPlayer cpm : Helper.stripOffLinePlayers(clan.getAllMembers())) {
            Player p = cpm.toPlayer();

            if (p != null) {
                String name = (cpm.isLeader() ? settings.getPageLeaderColor() 
                                              : (cpm.isTrusted() ? settings.getPageTrustedColor() 
                                                                 : settings.getPageUnTrustedColor())) + cpm.getName();
                String health = clanMan.getBarString((int) p.getHealth());
                String hunger = clanMan.getBarString(p.getFoodLevel());
                String armor = clanMan.getArmorString(p.getInventory());
                String weapons = clanMan.getWeaponString(p.getInventory());
                String food = clanMan.getFoodString(p.getInventory());

                chatBlock.addRow("  ", name, ChatColor.RED.toString(), health, hunger, 
                                             ChatColor.WHITE.toString(), food, armor, weapons);
            }
        }
        chatBlock.addRow(" -- Allies -- ", "","","","","");

        for (ClanPlayer cpm : clan.getAllAllyMembers()) {
            Player p = cpm.toPlayer();

            if (p != null) {
                String name = (cpm.isLeader() ? settings.getPageLeaderColor() 
                                              : (cpm.isTrusted() ? settings.getPageTrustedColor() 
                                                                 : settings.getPageUnTrustedColor())) + cpm.getName();
                String health = clanMan.getBarString((int) p.getHealth());
                String hunger = clanMan.getBarString(p.getFoodLevel());
                String armor = clanMan.getArmorString(p.getInventory());
                String weapons = clanMan.getWeaponString(p.getInventory());
                String food = clanMan.getFoodString(p.getInventory());

                chatBlock.addRow("  ", name, ChatColor.RED.toString(), health, hunger, 
                                             ChatColor.WHITE.toString(), food, armor, weapons);
            }
        }

        boolean more = chatBlock.sendBlock(player, settings.getPageSize());

        if (more) {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor, MessageFormat.format(plugin.getLang("view.next.page"), settings.getCommandMore()));
        }

        ChatBlock.sendBlank(player); 
    }
}
