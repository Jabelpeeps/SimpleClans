package net.sacredlabyrinth.phaed.simpleclans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class PlayerNameTabCompleter implements TabCompleter {
    private String clanCommand = SimpleClans.getInstance().getSettingsManager().getCommandClan();
    private List<String> commands = new ArrayList<>();
    
    PlayerNameTabCompleter() {
        SimpleClans plugin = SimpleClans.getInstance();
        commands.add( plugin.getLang("lookup.command") );
        commands.add( plugin.getLang("ban.command") );
        commands.add( plugin.getLang("unban.command") );
        commands.add( plugin.getLang("kick.command") );
        commands.add( plugin.getLang("trust.command") );
        commands.add( plugin.getLang("untrust.command") );
        commands.add( plugin.getLang("promote.command") );
        commands.add( plugin.getLang("demote.command") );
        commands.add( plugin.getLang("setrank.command") );
        commands.add( plugin.getLang("place.command") );
        commands.add( plugin.getLang("invite.command") );
        commands.add( plugin.getLang("kills.command") );
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        
        if (    strings.length > 1 
                && command.getName().equalsIgnoreCase(clanCommand) 
                && commands.contains( strings[0].toLowerCase() ) ) {

            return Bukkit.getOnlinePlayers().parallelStream()
                                            .map( p -> p.getName() )
                                            .filter( n -> n.startsWith( strings[1] ) )
                                            .collect( Collectors.toList() );
        }
        return null;
    }
}
