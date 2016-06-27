package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.Set;

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
        ClanManager clanMan = plugin.getClanManager();
        SettingsManager settings = plugin.getSettingsManager();
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.vitals")) {
            ClanPlayer cp = clanMan.getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTrusted()) {
                        if (arg.length == 0) {
                            ChatBlock chatBlock = new ChatBlock();
                            ChatBlock.sendBlank(player);
                            ChatBlock.saySingle(player, settings.getPageClanNameColor() + Helper.capitalize(clan.getName()) + subColor+ " " + plugin.getLang("vitals") + " " + headColor + Helper.generatePageSeparator(settings.getPageSep()));
                            ChatBlock.sendBlank(player);
                            ChatBlock.sendMessage(player, headColor + plugin.getLang("weapons") + ": " + MessageFormat.format(plugin.getLang("0.s.sword.1.2.b.bow.3.4.a.arrow"), ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE));
                            ChatBlock.sendMessage(player, headColor + plugin.getLang("materials") + ": " + ChatColor.AQUA + plugin.getLang("diamond") + ChatColor.DARK_GRAY + ", " + ChatColor.YELLOW + plugin.getLang("gold") + ChatColor.DARK_GRAY + ", " + ChatColor.GRAY + plugin.getLang("stone") + ChatColor.DARK_GRAY + ", " + ChatColor.WHITE + plugin.getLang("iron") + ChatColor.DARK_GRAY + ", " + ChatColor.GOLD + plugin.getLang("wood"));

                            ChatBlock.sendBlank(player);

                            chatBlock.setFlexibility(true, false, false, false, false, false);
                            chatBlock.setAlignment("l", "l", "l", "c", "c", "c");

                            chatBlock.addRow("  " + headColor + plugin.getLang("name"), plugin.getLang("health"), plugin.getLang("hunger"), plugin.getLang("food"), plugin.getLang("armor"), plugin.getLang("weapons"));

                            Set<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getLeaders());
                            members.addAll(Helper.stripOffLinePlayers(clan.getNonLeaders()));

                            for (ClanPlayer cpm : members) {
                                Player p = cpm.toPlayer();

                                if (p != null) {
                                    String name = (cpm.isLeader() ? settings.getPageLeaderColor() : (cpm.isTrusted() ? settings.getPageTrustedColor() : settings.getPageUnTrustedColor())) + cpm.getName();
                                    String health = clanMan.getHealthString(p.getHealth());
                                    String hunger = clanMan.getHungerString(p.getFoodLevel());
                                    String armor = clanMan.getArmorString(p.getInventory());
                                    String weapons = clanMan.getWeaponString(p.getInventory());
                                    String food = clanMan.getFoodString(p.getInventory());

                                    chatBlock.addRow("  " + name, ChatColor.RED + health, hunger, ChatColor.WHITE + food, armor, weapons);
                                }
                            }

                            chatBlock.addRow(" -- Allies -- ", "","","","","");

                            Set<ClanPlayer> allAllyMembers = clan.getAllAllyMembers();

                            for (ClanPlayer cpm : allAllyMembers) {
                                Player p = cpm.toPlayer();

                                if (p != null) {
                                    String name = (cpm.isLeader() ? settings.getPageLeaderColor() : (cpm.isTrusted() ? settings.getPageTrustedColor() : settings.getPageUnTrustedColor())) + cpm.getName();
                                    String health = clanMan.getHealthString(p.getHealth());
                                    String hunger = clanMan.getHungerString(p.getFoodLevel());
                                    String armor = clanMan.getArmorString(p.getInventory());
                                    String weapons = clanMan.getWeaponString(p.getInventory());
                                    String food = clanMan.getFoodString(p.getInventory());

                                    chatBlock.addRow("  " + name, ChatColor.RED + health, hunger, ChatColor.WHITE + food, armor, weapons);
                                }
                            }

                            boolean more = chatBlock.sendBlock(player, settings.getPageSize());

                            if (more) {
                                plugin.getStorageManager().addChatBlock(player, chatBlock);
                                ChatBlock.sendBlank(player);
                                ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), settings.getCommandMore()));
                            }

                            ChatBlock.sendBlank(player);
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.vitals"), settings.getCommandClan()));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.vitals"));
                }
                else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            }
            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
    }
}
