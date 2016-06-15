package net.sacredlabyrinth.phaed.simpleclans.executors;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.AlliancesCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.AllyCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.BanCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.BankCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.BbCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.CapeCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanffCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.CoordsCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.CreateCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.DemoteCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.DisbandCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.FfCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.GlobalffCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.HomeCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.InviteCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.KickCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.KillsCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.LeaderboardCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.ListCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.LookupCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.MenuCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.ModtagCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.MostKilledCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.PlaceCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.ProfileCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.PromoteCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.ReloadCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.ResetKDRCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.ResignCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.RivalCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.RivalriesCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.RosterCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.SetRankCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.StatsCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.ToggleCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.TrustCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.UnbanCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.UntrustCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.VerifyCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.VitalsCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.WarCommand;

/**
 * @author phaed
 */
public final class ClanCommandExecutor implements CommandExecutor {
    private SimpleClans plugin;
    private Map<String, ClanCommand> commands = new HashMap<>();
    private Map<String, ClanCommand> consoleCommands = new HashMap<>();
    private MenuCommand menuCommand;
    
    public interface ClanCommand {
        public void execute(CommandSender player, String[] arg);
    }

    public ClanCommandExecutor() {
        plugin = SimpleClans.getInstance();
        menuCommand = new MenuCommand();
        
        commands.put( plugin.getLang("create.command"), new CreateCommand() );
        commands.put( plugin.getLang("list.command"), new ListCommand() );
        commands.put( plugin.getLang("profile.command"), new ProfileCommand() );
        commands.put( plugin.getLang("roster.command"), new RosterCommand() );
        commands.put( plugin.getLang("lookup.command"), new LookupCommand() );
        commands.put( plugin.getLang("leaderboard.command"), new LeaderboardCommand() );
        commands.put( plugin.getLang("alliances.command"), new AlliancesCommand() );
        commands.put( plugin.getLang("rivalries.command"), new RivalriesCommand() );
        commands.put( plugin.getLang("vitals.command"), new VitalsCommand() );
        commands.put( plugin.getLang("coords.command"), new CoordsCommand() );
        commands.put( plugin.getLang("stats.command"), new StatsCommand() );
        commands.put( plugin.getLang("ally.command"), new AllyCommand() );
        commands.put( plugin.getLang("rival.command"), new RivalCommand() );
        commands.put( plugin.getLang("bb.command"), new BbCommand() );
        commands.put( plugin.getLang("modtag.command"), new ModtagCommand() );
        commands.put( plugin.getLang("toggle.command"), new ToggleCommand() );
        commands.put( plugin.getLang("invite.command"), new InviteCommand() );
        commands.put( plugin.getLang("kick.command"), new KickCommand() );
        commands.put( plugin.getLang("trust.command"), new TrustCommand() );
        commands.put( plugin.getLang("untrust.command"), new UntrustCommand() );
        commands.put( plugin.getLang("promote.command"), new PromoteCommand() );
        commands.put( plugin.getLang("cape.command"), new CapeCommand() );
        commands.put( plugin.getLang("demote.command"), new DemoteCommand() );
        commands.put( plugin.getLang("clanff.command"), new ClanffCommand() );
        commands.put( plugin.getLang("ff.command"), new FfCommand() );
        commands.put( plugin.getLang("resign.command"), new ResignCommand() );
        commands.put( plugin.getLang("disband.command"), new DisbandCommand() );
        commands.put( plugin.getLang("verify.command"), new VerifyCommand() );
        commands.put( plugin.getLang("ban.command"), new BanCommand() );
        commands.put( plugin.getLang("unban.command"), new UnbanCommand() );
        commands.put( plugin.getLang("reload.command"), new ReloadCommand() );
        commands.put( plugin.getLang("globalff.command"), new GlobalffCommand() );
        commands.put( plugin.getLang("war.command"), new WarCommand() );
        commands.put( plugin.getLang("home.command"), new HomeCommand() );
        commands.put( plugin.getLang("kills.command"), new KillsCommand() );
        commands.put( plugin.getLang("mostkilled.command"), new MostKilledCommand() );
        commands.put( plugin.getLang("setrank.command"), new SetRankCommand() );
        commands.put( plugin.getLang("bank.command"), new BankCommand() );
        commands.put( plugin.getLang("place.command"), new PlaceCommand() );
        commands.put( plugin.getLang("resetkdr.command"), new ResetKDRCommand() );
        consoleCommands.put( plugin.getLang("verify.command"), new VerifyCommand() );
        consoleCommands.put( plugin.getLang("reload.command"), new ReloadCommand() );
        consoleCommands.put( plugin.getLang("place.command"), new PlaceCommand() );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName()))
                return true;

            if (plugin.getSettingsManager().isBanned(player.getUniqueId())) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("banned"));
                return true;
            }

            if (args.length == 0) {
                menuCommand.execute(player);
                return true;
            }
            return runCommand(player, commands, args);
        }
        
        if (args.length == 0) {
            menuCommand.executeSender(sender);
            return true;
        }
        return runCommand(sender, consoleCommands, args);
    }
    
    private boolean runCommand(CommandSender sender, Map<String, ClanCommand> cmds, String[] args ) {
        String subcommand = args[0];
        String[] subargs = Helper.removeFirst(args);
        try {
            cmds.get( subcommand.toLowerCase() ).execute( sender, subargs );
            return true;
        }
        catch ( NullPointerException e ) {
            ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("does.not.match"));
            return false;
        }
    }
}
