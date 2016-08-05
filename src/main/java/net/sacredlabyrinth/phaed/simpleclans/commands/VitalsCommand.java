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
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class VitalsCommand implements ClanCommand  {
 
    @Override
    public void execute(CommandSender sender, String[] arg) {
        Player player = (Player) sender;
        SimpleClans plugin = SimpleClans.getInstance();
        LanguageManager lang = plugin.getLanguageManager();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.vitals")) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("insufficient.permissions"));
            return;
        }  

        ClanManager clanMan = plugin.getClanManager();
        ClanPlayer cp = clanMan.getClanPlayer(player);
        
        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("not.a.member.of.any.clan"));
            return;
        } 
        
        Clan clan = cp.getClan();

        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("clan.is.not.verified"));
            return;
        }
        
        if (!cp.isTrusted()) {
            ChatBlock.sendMessage(player, ChatColor.RED, lang.get("only.trusted.players.can.access.clan.vitals"));
            return;
        }

        SettingsManager settings = plugin.getSettingsManager();
        
        if (arg.length != 0) {
            ChatBlock.sendMessage(player, ChatColor.RED, MessageFormat.format(lang.get("usage.0.vitals"), settings.getCommandClan())); 
            return;
        }

        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();
        
        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, settings.getPageClanNameColor(), Helper.capitalize( clan.getName() ), 
                                    subColor, " ", lang.get("vitals"), " ",
                                    headColor, Helper.generatePageSeparator( settings.getPageSep() ) );
        ChatBlock.sendBlank(player);
        ChatBlock.sendMessage(player, headColor, lang.get("weapons"), ": ", 
                                      MessageFormat.format( lang.get("0.s.sword.1.2.b.bow.3.4.a.arrow"), 
                        ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE));
        ChatBlock.sendMessage(player, 
                        headColor, lang.get("materials"), ": ", ChatColor.AQUA.toString(), lang.get("diamond"),
                        ChatColor.DARK_GRAY.toString(), ", ", ChatColor.YELLOW.toString(), lang.get("gold"),
                        ChatColor.DARK_GRAY.toString(), ", ", ChatColor.GRAY.toString(), lang.get("stone"),
                        ChatColor.DARK_GRAY.toString(), ", ", ChatColor.WHITE.toString(), lang.get("iron"),
                        ChatColor.DARK_GRAY.toString(), ", ", ChatColor.GOLD.toString(), lang.get("wood"));

        ChatBlock.sendBlank(player);
        
        ChatBlock chatBlock = new ChatBlock();
        chatBlock.setFlexibility(true, false, false, false, false, false);
        chatBlock.setAlignment("l", "l", "l", "c", "c", "c");

        chatBlock.addRow("  ", headColor, lang.get("name"), lang.get("health"), lang.get("hunger"), 
                                          lang.get("food"), lang.get("armor"), lang.get("weapons"));
        
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
            ChatBlock.sendMessage(player, headColor, MessageFormat.format(lang.get("view.next.page"), settings.getCommandMore()));
        }
        ChatBlock.sendBlank(player); 
    }
}
