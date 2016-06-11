package net.sacredlabyrinth.phaed.simpleclans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import net.sacredlabyrinth.phaed.simpleclans.executors.AcceptCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.AllyCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.DenyCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.GlobalCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.MoreCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCEntityListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCPlayerListener;
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

    private ArrayList<String> messages = new ArrayList<>();
    private static SimpleClans instance;
    private static final Logger logger = Logger.getLogger("Minecraft");
    private ClanManager clanManager;
    private RequestManager requestManager;
    private StorageManager storageManager;
    private static SettingsManager settingsManager;
    private PermissionsManager permissionsManager;
    private TeleportManager teleportManager;
    private LanguageManager languageManager;
//    private boolean hasUUID;

    /**
     * @return the logger
     */
    public static Logger getLog() {
        return logger;
    }

    /**
     * @param msg
     */
    public static void debug(String msg) {
        if (settingsManager.isDebugging()) {
            logger.log(Level.INFO, msg);
        }
    }

    /**
     * @return the instance
     */
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
//        hasUUID = UUIDMigration.canReturnUUID();
        languageManager = new LanguageManager();

        permissionsManager = new PermissionsManager();
        requestManager = new RequestManager();
        clanManager = new ClanManager();
        storageManager = new StorageManager();
        teleportManager = new TeleportManager();

        logger.info(MessageFormat.format(getLang("version.loaded"), getDescription().getName(), getDescription().getVersion()));

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

        pullMessages();
//        logger.info("[SimpleClans] Online Mode: " + hasUUID);
        logger.info("[SimpleClans] Modo Multithreading: " + settingsManager.getUseThreads());
        logger.info("[SimpleClans] Modo BungeeCord: " + settingsManager.getUseBungeeCord());
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        storageManager.closeConnection();
        permissionsManager.savePermissions();
    }

    public void pullMessages() {
        if (settingsManager.isDisableMessages()) {
            return;
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://minecraftcubed.net/pluginmessage/").openStream()
            		, StandardCharsets.UTF_8));

            String message;
            while ((message = in.readLine()) != null) {
                messages.add(message);
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + message);
            }
            in.close();

        }
        catch (IOException e) {
            // do nothing
        }
    }

    /**
     * @return the clanManager
     */
    public ClanManager getClanManager() {
        return clanManager;
    }

    /**
     * @return the requestManager
     */
    public RequestManager getRequestManager() {
        return requestManager;
    }

    /**
     * @return the storageManager
     */
    public StorageManager getStorageManager() {
        return storageManager;
    }

    /**
     * @return the settingsManager
     */
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    /**
     * @return the permissionsManager
     */
    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    /**
     * @return the lang
     */
    public String getLang(String msg) {
        return languageManager.get(msg);
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public List<String> getMessages() {
        return messages;
    }

    /**
//     * @return the hasUUID
//     */
//    public boolean hasUUID() {
//        return hasUUID;
//    }

//    /**
//     * @param trueOrFalse
//     */
//    public void setUUID(boolean trueOrFalse) {
//        hasUUID = trueOrFalse;
//    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }
}
