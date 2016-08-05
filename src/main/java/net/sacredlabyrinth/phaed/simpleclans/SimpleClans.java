package net.sacredlabyrinth.phaed.simpleclans;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.sacredlabyrinth.phaed.simpleclans.executors.AcceptCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.AllyCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.DenyCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.GlobalCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.MoreCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCEntityListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCPlayerListener;
import net.sacredlabyrinth.phaed.simpleclans.managers.BansManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.RequestManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.TeleportManager;

/**
 * @author Phaed
 */
public class SimpleClans extends JavaPlugin {

    private static SimpleClans instance;
    private static Logger logger;
    private ClanManager clanMan;
    private RequestManager reqMan;
    private StorageManager stor;
    private static SettingsManager settings;
    private PermissionsManager perms;
    private TeleportManager teleportManager;
    private LanguageManager lang;
    private BansManager bansManager;

    public static void debug(String msg) {
        if (settings.isDebugging()) {
            logger.log(Level.INFO, msg);
        }
    }

    public static void log(String msg, Object... arg) {
        if (arg == null || arg.length == 0) {
            logger.log(Level.INFO, msg);
        } else {
            logger.log(Level.INFO, MessageFormat.format(msg, arg));
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        settings = new SettingsManager();
        lang = new LanguageManager();
        perms = new PermissionsManager();
        reqMan = new RequestManager();
        clanMan = new ClanManager();
        stor = new StorageManager();
        teleportManager = new TeleportManager();
        bansManager = new BansManager();

        logger.info( MessageFormat.format( 
                lang.get( "version.loaded" ), getDescription().getName(), getDescription().getVersion() ) );

        Bukkit.getPluginManager().registerEvents( new SCEntityListener(), this );
        Bukkit.getPluginManager().registerEvents( new SCPlayerListener(), this );

        perms.loadPermissions();

        CommandHelper.registerCommand( settings.getCommandClan() );
        CommandHelper.registerCommand( settings.getCommandAccept() );
        CommandHelper.registerCommand( settings.getCommandDeny() );
        CommandHelper.registerCommand( settings.getCommandMore() );
        CommandHelper.registerCommand( settings.getCommandAlly() );
        CommandHelper.registerCommand( settings.getCommandGlobal() );

        getCommand( settings.getCommandClan() ).setExecutor( new ClanCommandExecutor() );
        getCommand( settings.getCommandAccept() ).setExecutor( new AcceptCommandExecutor() );
        getCommand( settings.getCommandDeny() ).setExecutor( new DenyCommandExecutor() );
        getCommand( settings.getCommandMore() ).setExecutor( new MoreCommandExecutor() );
        getCommand( settings.getCommandAlly() ).setExecutor( new AllyCommandExecutor() );
        getCommand( settings.getCommandGlobal() ).setExecutor( new GlobalCommandExecutor() );

        getCommand( settings.getCommandClan() ).setTabCompleter( new PlayerNameTabCompleter() );

        logger.info( "Modo Multithreading: " + settings.getUseThreads() );
        logger.info( "Modo BungeeCord: " + settings.getUseBungeeCord() );
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        stor.closeConnection();
        perms.savePermissions();
        bansManager.saveBans();
    }

    public static SimpleClans getInstance() { return instance; }
    public ClanManager getClanManager() { return clanMan; }
    public RequestManager getRequestManager() { return reqMan; }
    public StorageManager getStorageManager() { return stor; }
    public SettingsManager getSettingsManager() { return settings; }
    public PermissionsManager getPermissionsManager() { return perms; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public LanguageManager getLanguageManager() { return lang; }
    public BansManager getBansManager() { return bansManager; }
}
