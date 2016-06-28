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

/**
 * @author phaed
 */
public class BankCommand implements ClanCommand {

    @Override
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        Player player = (Player) sender;

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.bank")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            double plmoney = plugin.getPermissionsManager().playerGetMoney(player);
            double money = 0;

            if (cp != null) {
                Clan clan = cp.getClan();
                double clanbalance = clan.getBalance();
                if (clan.isMember(player)) {
                    if (clan.isVerified()) {
                        if (cp.isTrusted()) {
                            if (arg.length == 1)  {
                                if (arg[0].equalsIgnoreCase("status")) {
                                    player.sendMessage(ChatColor.AQUA + MessageFormat.format("Clan-Balance: {0}", clanbalance));
                                }
                            }
                            else if (arg.length == 2) {
                                if (arg[1].matches("[0-9]+")) {
                                    money = Double.parseDouble(arg[1]);
                                }
                                if (arg[0].equalsIgnoreCase("deposit")) {
                                    if (cp.getClan().isLeader(player) || clan.isAllowDeposit()) {
                                        if (arg[1].equalsIgnoreCase("all")) {
                                            clan.deposit(plmoney, player);
                                        }
                                        else
                                            clan.deposit(money, player);
                                    }
                                    else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                                }
                                else if (arg[0].equalsIgnoreCase("withdraw")) {
                                    if (cp.getClan().isLeader(player) || clan.isAllowWithdraw()) {
                                        if (arg[1].equalsIgnoreCase("all")) {
                                            clan.withdraw(clanbalance, player);
                                        }
                                        else
                                            clan.withdraw(money, player);
                                    }
                                    else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                                }
                                else ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.bank"), plugin.getSettingsManager().getCommandClan()));
                            }
                            else ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.bank"), plugin.getSettingsManager().getCommandClan()));
                        }
                        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.stats"));
                    }
                    else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                }
            }
            else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
        }
        else ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
    }
}
