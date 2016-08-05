package net.sacredlabyrinth.phaed.simpleclans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;

public class PlayerNameTabCompleter implements TabCompleter {
    SimpleClans plugin = SimpleClans.getInstance();
    private String clanCommand = plugin.getSettingsManager().getCommandClan();
    private List<String> commands = new ArrayList<>();
    LanguageManager lang = plugin.getLanguageManager();
    
    PlayerNameTabCompleter() {
        commands.add( lang.get("lookup.command") );
        commands.add( lang.get("ban.command") );
        commands.add( lang.get("unban.command") );
        commands.add( lang.get("kick.command") );
        commands.add( lang.get("trust.command") );
        commands.add( lang.get("untrust.command") );
        commands.add( lang.get("promote.command") );
        commands.add( lang.get("demote.command") );
        commands.add( lang.get("setrank.command") );
        commands.add( lang.get("place.command") );
        commands.add( lang.get("invite.command") );
        commands.add( lang.get("kills.command") );
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
