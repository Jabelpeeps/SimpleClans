package net.sacredlabyrinth.phaed.simpleclans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

public class CommandHelper {
    
    private static CommandMap commandMap = null;
    private static SimpleClans plugin = SimpleClans.getInstance();
	private CommandHelper() {}
	
    public static void registerCommand(String... aliases) {
        if (aliases != null) {
            PluginCommand command = getCommand(aliases[0]);

            if (command != null) {
                command.setAliases(Arrays.asList(aliases));
                getCommandMap().register(plugin.getDescription().getName(), command);
            }
        }
    }

    private static PluginCommand getCommand(String name) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return command;
    }

    private static CommandMap getCommandMap() {        

        if ( commandMap == null ) {
            try {
                if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                    Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                    f.setAccessible(true);

                    commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }  
        return commandMap;
    }
}
