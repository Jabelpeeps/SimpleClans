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
    private static final Logger logger = Logger.getLogger("Minecraft");
    private ClanManager clanManager;
    private RequestManager requestManager;
    private StorageManager storageManager;
    private static SettingsManager settingsManager;
    private PermissionsManager permissionsManager;
    private TeleportManager teleportManager;
    private LanguageManager languageManager;
    private BansManager bansManager;

    public static Logger getLog() {
        return logger;
    }

    public static void debug(String msg) {
        if (settingsManager.isDebugging()) {
            logger.log(Level.INFO, msg);
        }
    }

    public static SimpleClans getInstance() {
        return instance;
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

        settingsManager = new SettingsManager();
        languageManager = new LanguageManager();
        permissionsManager = new PermissionsManager();
        requestManager = new RequestManager();
        clanManager = new ClanManager();
        storageManager = new StorageManager();
        teleportManager = new TeleportManager();

        logger.info( MessageFormat.format( 
                languageManager.get( "version.loaded" ), getDescription().getName(), getDescription().getVersion()));

        Bukkit.getPluginManager().registerEvents(new SCEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new SCPlayerListener(), this);

        permissionsManager.loadPermissions();

        CommandHelper.registerCommand(settingsManager.getCommandClan());
        CommandHelper.registerCommand(settingsManager.getCommandAccept());
        CommandHelper.registerCommand(settingsManager.getCommandDeny());
        CommandHelper.registerCommand(settingsManager.getCommandMore());
        CommandHelper.registerCommand(settingsManager.getCommandAlly());
        CommandHelper.registerCommand(settingsManager.getCommandGlobal());

        getCommand(settingsManager.getCommandClan()).setExecutor(new ClanCommandExecutor());
        getCommand(settingsManager.getCommandAccept()).setExecutor(new AcceptCommandExecutor());
        getCommand(settingsManager.getCommandDeny()).setExecutor(new DenyCommandExecutor());
        getCommand(settingsManager.getCommandMore()).setExecutor(new MoreCommandExecutor());
        getCommand(settingsManager.getCommandAlly()).setExecutor(new AllyCommandExecutor());
        getCommand(settingsManager.getCommandGlobal()).setExecutor(new GlobalCommandExecutor());

        getCommand(settingsManager.getCommandClan()).setTabCompleter(new PlayerNameTabCompleter());

        logger.info("[SimpleClans] Modo Multithreading: " + settingsManager.getUseThreads());
        logger.info("[SimpleClans] Modo BungeeCord: " + settingsManager.getUseBungeeCord());
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        storageManager.closeConnection();
        permissionsManager.savePermissions();
        bansManager.saveBans();
    }

    public ClanManager getClanManager() { return clanManager; }
    public RequestManager getRequestManager() { return requestManager; }
    public StorageManager getStorageManager() { return storageManager; }
    public SettingsManager getSettingsManager() { return settingsManager; }
    public PermissionsManager getPermissionsManager() { return permissionsManager; }
    public String getLang(String msg) { return languageManager.get(msg); }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public LanguageManager getLanguageManager() { return languageManager; }
    public BansManager getBansManager() { return bansManager; }
}
