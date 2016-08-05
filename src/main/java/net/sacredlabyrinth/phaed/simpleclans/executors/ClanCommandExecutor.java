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
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;

/**
 * @author phaed
 */
public final class ClanCommandExecutor implements CommandExecutor {
    private SimpleClans plugin = SimpleClans.getInstance();
    private Map<String, ClanCommand> commands = new HashMap<>();
    private Map<String, ClanCommand> consoleCommands = new HashMap<>();
    private MenuCommand menuCommand;
    
    public interface ClanCommand {
        public void execute(CommandSender player, String[] arg);
    }

    public ClanCommandExecutor() {
        LanguageManager lang = plugin.getLanguageManager();
        
        menuCommand = new MenuCommand();
        
        commands.put( lang.get("create.command"), new CreateCommand() );
        commands.put( lang.get("list.command"), new ListCommand() );
        commands.put( lang.get("profile.command"), new ProfileCommand() );
        commands.put( lang.get("roster.command"), new RosterCommand() );
        commands.put( lang.get("lookup.command"), new LookupCommand() );
        commands.put( lang.get("leaderboard.command"), new LeaderboardCommand() );
        commands.put( lang.get("alliances.command"), new AlliancesCommand() );
        commands.put( lang.get("rivalries.command"), new RivalriesCommand() );
        commands.put( lang.get("vitals.command"), new VitalsCommand() );
        commands.put( lang.get("coords.command"), new CoordsCommand() );
        commands.put( lang.get("stats.command"), new StatsCommand() );
        commands.put( lang.get("ally.command"), new AllyCommand() );
        commands.put( lang.get("rival.command"), new RivalCommand() );
        commands.put( lang.get("bb.command"), new BbCommand() );
        commands.put( lang.get("modtag.command"), new ModtagCommand() );
        commands.put( lang.get("toggle.command"), new ToggleCommand() );
        commands.put( lang.get("invite.command"), new InviteCommand() );
        commands.put( lang.get("kick.command"), new KickCommand() );
        commands.put( lang.get("trust.command"), new TrustCommand() );
        commands.put( lang.get("untrust.command"), new UntrustCommand() );
        commands.put( lang.get("promote.command"), new PromoteCommand() );
        commands.put( lang.get("cape.command"), new CapeCommand() );
        commands.put( lang.get("demote.command"), new DemoteCommand() );
        commands.put( lang.get("clanff.command"), new ClanffCommand() );
        commands.put( lang.get("ff.command"), new FfCommand() );
        commands.put( lang.get("resign.command"), new ResignCommand() );
        commands.put( lang.get("disband.command"), new DisbandCommand() );
        commands.put( lang.get("verify.command"), new VerifyCommand() );
        commands.put( lang.get("ban.command"), new BanCommand() );
        commands.put( lang.get("unban.command"), new UnbanCommand() );
        commands.put( lang.get("reload.command"), new ReloadCommand() );
        commands.put( lang.get("globalff.command"), new GlobalffCommand() );
        commands.put( lang.get("war.command"), new WarCommand() );
        commands.put( lang.get("home.command"), new HomeCommand() );
        commands.put( lang.get("kills.command"), new KillsCommand() );
        commands.put( lang.get("mostkilled.command"), new MostKilledCommand() );
        commands.put( lang.get("setrank.command"), new SetRankCommand() );
        commands.put( lang.get("bank.command"), new BankCommand() );
        commands.put( lang.get("place.command"), new PlaceCommand() );
        commands.put( lang.get("resetkdr.command"), new ResetKDRCommand() );
        consoleCommands.put( lang.get("verify.command"), new VerifyCommand() );
        consoleCommands.put( lang.get("reload.command"), new ReloadCommand() );
        consoleCommands.put( lang.get("place.command"), new PlaceCommand() );
        consoleCommands.put( lang.get("list.command"), new ListCommand() );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName()))
                return true;

            if (plugin.getBansManager().isBanned(player.getUniqueId())) {
                ChatBlock.sendMessage(player, ChatColor.RED, plugin.getLanguageManager().get("banned"));
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
        try {
            cmds.get( args[0].toLowerCase() ).execute( sender, Helper.removeFirst(args) );
            return true;
        }
        catch ( NullPointerException e ) {
            ChatBlock.sendMessage(sender, ChatColor.RED, plugin.getLanguageManager().get("does.not.match"));
            return false;
        }
    }
}
