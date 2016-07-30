package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 * @author phaed
 */
public final class SettingsManager {

    private SimpleClans plugin = SimpleClans.getInstance();;
    private File main;
    private FileConfiguration config;
    
    private boolean onlineMode;
    private boolean disableMessages;
    private String clanChatRankColor;
    private boolean tagBasedClanChat;
    private boolean teleportOnSpawn;
    private boolean dropOnHome;
    private boolean keepOnHome;
    private boolean debugging;
    private boolean mChatIntegration;
    private boolean pvpOnlywhileInWar;
    private boolean useColorCodeFromPrefix;
    private boolean confirmationForPromote;
    private boolean confirmationForDemote;
    private boolean globalff;
    private boolean showUnverifiedOnList;
    private boolean requireVerification;
    private List<Integer> itemsList;
    private List<String> blacklistedWorlds;
    private List<String> disallowedWords;
    private List<String> disallowedColors;
    private List<String> unRivableClans;
    private int rivalLimitPercent;
    private boolean ePurchaseCreation;
    private boolean ePurchaseVerification;
    private boolean ePurchaseInvite;
    private boolean ePurchaseHomeTeleport;
    private boolean ePurchaseHomeTeleportSet;
    private double eCreationPrice;
    private double eVerificationPrice;
    private double eInvitePrice;
    private double eHomeTeleportPrice;
    private double eHomeTeleportPriceSet;
    private String serverName;
    private boolean chatTags;
    private int purgeClan;
    private int purgeUnverified;
    private int purgePlayers;
    private int requestFreqencySecs;
    private String requestMessageColor;
    private int pageSize;
    private String pageSep;
    private String pageHeadingsColor;
    private String pageSubTitleColor;
    private String pageLeaderColor;
    private String pageTrustedColor;
    private String pageUnTrustedColor;
    private boolean bbShowOnLogin;
    private int bbSize;
    private String bbColor;
    private String bbAccentColor;
    private String commandClan;
    private String commandAlly;
    private String commandGlobal;
    private String commandMore;
    private String commandDeny;
    private String commandAccept;
    private int clanMinSizeToAlly;
    private int clanMinSizeToRival;
    private int clanMinLength;
    private int clanMaxLength;
    private String pageClanNameColor;
    private int tagMinLength;
    private int tagMaxLength;
    private String tagDefaultColor;
    private String tagSeparator;
    private String tagSeparatorColor;
    private String tagSeparatorLeaderColor;
    private String tagBracketLeft;
    private String tagBracketRight;
    private String tagBracketColor;
    private String tagBracketLeaderColor;
    private boolean clanTrustByDefault;
    private boolean allyChatEnable;
    private String allyChatMessageColor;
    private String allyChatNameColor;
    private String allyChatTagColor;
    private String allyChatTagBracketLeft;
    private String allyChatTagBracketRight;
    private String allyChatBracketColor;
    private String allyChatPlayerBracketLeft;
    private String allyChatPlayerBracketRight;
    private boolean clanChatEnable;
    private String clanChatAnnouncementColor;
    private String clanChatMessageColor;
    private String clanChatNameColor;
    private String clanChatTagBracketLeft;
    private String clanChatTagBracketRight;
    private String clanChatBracketColor;
    private String clanChatPlayerBracketLeft;
    private String clanChatPlayerBracketRight;
    private boolean clanFFOnByDefault;
    private double kwRival;
    private double kwNeutral;
    private double kwCivilian;
    private boolean useMysql;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private boolean safeCivilians;
    private boolean compatMode;
    private boolean homebaseSetOnce;
    private int waitSecs;
    private boolean enableAutoGroups;
    private boolean moneyperkill;
    private double KDRMultipliesPerKill;
    private boolean teleportBlocks;
    private boolean AutoGroupGroupName;
    private boolean tamableMobsSharing;
    private boolean allowReGroupCommand;
    private boolean useThreads;
    private boolean useBungeeCord;
    private boolean forceCommandPriority;
    private int maxAsksPerRequest;
    private int maxMembers;

    public SettingsManager() {
        config = plugin.getConfig();
        main = new File(plugin.getDataFolder() + File.separator + "config.yml");
        load();
    }

    public void load() {

        if (main.exists()) {
            try {
                config.options().copyDefaults(true);
                config.load(main);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            config.options().copyDefaults(true);
        }

        onlineMode = config.getBoolean("settings.online-mode");
        disableMessages = config.getBoolean("settings.disable-messages");
        teleportOnSpawn = config.getBoolean("settings.teleport-home-on-spawn");
        dropOnHome = config.getBoolean("settings.drop-items-on-clan-home");
        keepOnHome = config.getBoolean("settings.keep-items-on-clan-home");
        itemsList = config.getIntegerList("settings.item-list");
        debugging = config.getBoolean("settings.show-debug-info");
        mChatIntegration = config.getBoolean("settings.mchat-integration");
        pvpOnlywhileInWar = config.getBoolean("settings.pvp-only-while-at-war");
        enableAutoGroups = config.getBoolean("settings.enable-auto-groups");
        useColorCodeFromPrefix = config.getBoolean("settings.use-colorcode-from-prefix-for-name");
        compatMode = config.getBoolean("settings.chat-compatibility-mode");
        disallowedColors = config.getStringList("settings.disallowed-tag-colors");
        blacklistedWorlds = config.getStringList("settings.blacklisted-worlds");
        disallowedWords = config.getStringList("settings.disallowed-tags");
        unRivableClans = config.getStringList("settings.unrivable-clans");
        showUnverifiedOnList = config.getBoolean("settings.show-unverified-on-list");
        requireVerification = config.getBoolean("settings.new-clan-verification-required");
        serverName = config.getString("settings.server-name");
        chatTags = config.getBoolean("settings.display-chat-tags");
        rivalLimitPercent = config.getInt("settings.rival-limit-percent");
        ePurchaseCreation = config.getBoolean("economy.purchase-clan-create");
        ePurchaseVerification = config.getBoolean("economy.purchase-clan-verify");
        ePurchaseInvite = config.getBoolean("economy.purchase-clan-invite");
        ePurchaseHomeTeleport = config.getBoolean("economy.purchase-home-teleport");
        ePurchaseHomeTeleportSet = config.getBoolean("economy.purchase-home-teleport-set");
        eCreationPrice = config.getDouble("economy.creation-price");
        eVerificationPrice = config.getDouble("economy.verification-price");
        eInvitePrice = config.getDouble("economy.invite-price");
        eHomeTeleportPrice = config.getDouble("economy.home-teleport-price");
        eHomeTeleportPriceSet = config.getDouble("economy.home-teleport-set-price");
        purgeClan = config.getInt("purge.inactive-clan-days");
        purgeUnverified = config.getInt("purge.unverified-clan-days");
        purgePlayers = config.getInt("purge.inactive-player-data-days");
        requestFreqencySecs = config.getInt("request.ask-frequency-secs");
        requestMessageColor = config.getString("request.message-color");
        maxAsksPerRequest = config.getInt("request.max-asks-per-request");
        pageSize = config.getInt("page.size", 100);
        pageSep = config.getString("page.separator");
        pageSubTitleColor = config.getString("page.subtitle-color");
        pageHeadingsColor = config.getString("page.headings-color");
        pageLeaderColor = config.getString("page.leader-color");
        pageTrustedColor = config.getString("page.trusted-color");
        pageUnTrustedColor = config.getString("page.untrusted-color");
        pageClanNameColor = config.getString("page.clan-name-color");
        bbShowOnLogin = config.getBoolean("bb.show-on-login");
        bbSize = config.getInt("bb.size");
        bbColor = config.getString("bb.color");
        bbAccentColor = config.getString("bb.accent-color");
        commandClan = config.getString("commands.clan");
        commandAlly = config.getString("commands.ally");
        commandGlobal = config.getString("commands.global");
        commandMore = config.getString("commands.more");
        commandDeny = config.getString("commands.deny");
        commandAccept = config.getString("commands.accept");
        forceCommandPriority = config.getBoolean("commands.force-priority");
        homebaseSetOnce = config.getBoolean("clan.homebase-can-be-set-only-once");
        waitSecs = config.getInt("clan.homebase-teleport-wait-secs");
        confirmationForPromote = config.getBoolean("clan.confirmation-for-demote");
        confirmationForDemote = config.getBoolean("clan.confirmation-for-promote");
        clanTrustByDefault = config.getBoolean("clan.trust-members-by-default");
        clanMinSizeToAlly = config.getInt("clan.min-size-to-set-ally");
        clanMinSizeToRival = config.getInt("clan.min-size-to-set-rival");
        clanMinLength = config.getInt("clan.min-length");
        clanMaxLength = config.getInt("clan.max-length");
        clanFFOnByDefault = config.getBoolean("clan.ff-on-by-default");
        tagMinLength = config.getInt("tag.min-length");
        tagMaxLength = config.getInt("tag.max-length");
        tagDefaultColor = config.getString("tag.default-color");
        tagSeparator = config.getString("tag.separator.char");
        tagSeparatorColor = config.getString("tag.separator.color");
        tagSeparatorLeaderColor = config.getString("tag.separator.leader-color");
        tagBracketColor = config.getString("tag.bracket.color");
        tagBracketLeaderColor = config.getString("tag.bracket.leader-color");
        tagBracketLeft = config.getString("tag.bracket.left");
        tagBracketRight = config.getString("tag.bracket.right");
        allyChatEnable = config.getBoolean("allychat.enable");
        allyChatMessageColor = config.getString("allychat.message-color");
        allyChatTagColor = config.getString("allychat.tag-color");
        allyChatNameColor = config.getString("allychat.name-color");
        allyChatBracketColor = config.getString("allychat.tag-bracket.color");
        allyChatTagBracketLeft = config.getString("allychat.tag-bracket.left");
        allyChatTagBracketRight = config.getString("allychat.tag-bracket.right");
        allyChatPlayerBracketLeft = config.getString("allychat.player-bracket.left");
        allyChatPlayerBracketRight = config.getString("allychat.player-bracket.right");
        clanChatEnable = config.getBoolean("clanchat.enable");
        tagBasedClanChat = config.getBoolean("clanchat.tag-based-clan-chat");
        clanChatAnnouncementColor = config.getString("clanchat.announcement-color");
        clanChatMessageColor = config.getString("clanchat.message-color");
        clanChatNameColor = config.getString("clanchat.name-color");
        clanChatRankColor = config.getString("clanchat.rank.color");
        clanChatBracketColor = config.getString("clanchat.tag-bracket.color");
        clanChatTagBracketLeft = config.getString("clanchat.tag-bracket.left");
        clanChatTagBracketRight = config.getString("clanchat.tag-bracket.right");
        clanChatPlayerBracketLeft = config.getString("clanchat.player-bracket.left");
        clanChatPlayerBracketRight = config.getString("clanchat.player-bracket.right");
        kwRival = config.getDouble("kill-weights.rival");
        kwNeutral = config.getDouble("kill-weights.neutral");
        kwCivilian = config.getDouble("kill-weights.civilian");
        useMysql = config.getBoolean("mysql.enable");
        host = config.getString("mysql.host");
        port = config.getInt("mysql.port");
        database = config.getString("mysql.database");
        username = config.getString("mysql.username");
        password = config.getString("mysql.password");
        port = config.getInt("mysql.port");
        safeCivilians = config.getBoolean("safe-civilians");
        moneyperkill = config.getBoolean("economy.money-per-kill");
        KDRMultipliesPerKill = config.getDouble("economy.money-per-kill-kdr-multipier");
        teleportBlocks = config.getBoolean("settings.teleport-blocks");
        AutoGroupGroupName = config.getBoolean("permissions.auto-group-groupname");
        tamableMobsSharing = config.getBoolean("settings.tameable-mobs-sharing");
        allowReGroupCommand = config.getBoolean("settings.allow-regroup-command");
        useThreads = config.getBoolean("performance.use-threads");
        useBungeeCord = config.getBoolean("performance.use-bungeecord");
        maxMembers = config.getInt("clan.max-members");

        if (tagSeparator.equals(" .")) {
            tagSeparator = ".";
        }
        if (tagSeparator == null) {
            tagSeparator = "";
        }
        // migrate from old way of adding ports
        if (database.contains(":")) {
            String[] strings = database.split(":");
            database = strings[0];
            port = Integer.valueOf(strings[1]);
        }
        save();
    }

    public void save() {
        
        // TODO why does this have no apparent saving of the individual config values?
        try {
            config.save(main);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check whether a world is blacklisted
     *
     * @param world the world
     * @return whether the world is blacklisted
     */
    public boolean isBlacklistedWorld(String world) {
        return blacklistedWorlds.parallelStream().anyMatch( w -> w.equalsIgnoreCase( world ) );
    }

    /**
     * Check whether a word is disallowed
     *
     * @param word the world
     * @return whether its a disallowed word
     */
    public boolean isDisallowedWord(String word) {
        return  disallowedWords.parallelStream().anyMatch( w -> w.equalsIgnoreCase( word ) )
                || word.equalsIgnoreCase("clan") 
                || word.equalsIgnoreCase(commandMore) 
                || word.equalsIgnoreCase(commandDeny) 
                || word.equalsIgnoreCase(commandAccept);
    }

    /**
     * Check whether a string has a disallowed color
     *
     * @param str the string
     * @return whether the string contains the color code
     */
    public boolean hasDisallowedColor(String str) {
        return disallowedColors.parallelStream().anyMatch( c -> str.contains( "&" + c ) );
    }

    /**
     * @return a comma delimited string with all disallowed colors
     */
    public String getDisallowedColorString() {
        return String.join( ", ", disallowedColors );
    }

    /**
     * Check whether a clan is un-rivable
     *
     * @param tag the tag
     * @return whether the clan is unrivable
     */
    public boolean isUnrivable(String tag) {
        return unRivableClans.contains( tag );
    }

    public boolean isRequireVerification() { return requireVerification; }
    public List<String> getDisallowedColors() { return Collections.unmodifiableList(disallowedColors); }
    public List<String> getunRivableClans() { return Collections.unmodifiableList(unRivableClans); }
    public int getRivalLimitPercent() { return rivalLimitPercent; }
    public String getServerName() { return Helper.parseColors(serverName); }
    public boolean isChatTags() { return chatTags; }
    public int getPurgeClan() { return purgeClan; }
    public int getPurgeUnverified() { return purgeUnverified; }
    public int getPurgePlayers() { return purgePlayers; }
    public int getRequestFreqencySecs() { return requestFreqencySecs; }
    public String getRequestMessageColor() { return Helper.toColor(requestMessageColor); }
    public int getPageSize() { return pageSize; }
    public String getPageSep() { return pageSep; }
    public String getPageHeadingsColor() { return Helper.toColor(pageHeadingsColor); }
    public String getPageSubTitleColor() { return Helper.toColor(pageSubTitleColor); }
    public String getPageLeaderColor() { return Helper.toColor(pageLeaderColor); }
    public int getBbSize() { return bbSize; }
    public String getBbColor() { return Helper.toColor(bbColor); }
    public String getBbAccentColor() { return Helper.toColor(bbAccentColor); }
    public String getCommandClan() { return commandClan; }
    public String getCommandMore() { return commandMore; }
    public String getCommandDeny() { return commandDeny; }
    public String getCommandAccept() { return commandAccept; }
    public int getClanMinSizeToAlly() { return clanMinSizeToAlly; }
    public int getClanMinSizeToRival() { return clanMinSizeToRival; }
    public int getClanMinLength() { return clanMinLength; }
    public int getClanMaxLength() { return clanMaxLength; }
    public String getPageClanNameColor() { return Helper.toColor(pageClanNameColor); }
    public int getTagMinLength() { return tagMinLength; }
    public int getTagMaxLength() { return tagMaxLength; }
    public String getTagDefaultColor() { return Helper.toColor(tagDefaultColor); }
    public String getTagSeparator() { return tagSeparator; }
    public String getTagSeparatorColor() { return Helper.toColor(tagSeparatorColor); }
    public String getClanChatAnnouncementColor() { return Helper.toColor(clanChatAnnouncementColor); }
    public String getClanChatMessageColor() { return Helper.toColor(clanChatMessageColor); }
    public String getClanChatNameColor() { return Helper.toColor(clanChatNameColor); }
    public String getClanChatTagBracketLeft() { return clanChatTagBracketLeft; }
    public String getClanChatTagBracketRight() { return clanChatTagBracketRight; }
    public String getClanChatBracketColor() { return Helper.toColor(clanChatBracketColor); }
    public String getClanChatPlayerBracketLeft() { return clanChatPlayerBracketLeft; }
    public String getClanChatPlayerBracketRight() { return clanChatPlayerBracketRight; }
    public double getKwRival() { return kwRival; }
    public double getKwNeutral() { return kwNeutral; }
    public double getKwCivilian() { return kwCivilian; }
    public boolean isUseMysql() { return useMysql; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isShowUnverifiedOnList() { return showUnverifiedOnList; }
    public boolean isClanTrustByDefault() { return clanTrustByDefault; }
    public String getPageTrustedColor() { return Helper.toColor(pageTrustedColor); }
    public String getPageUnTrustedColor() { return Helper.toColor(pageUnTrustedColor); }
    public boolean isGlobalff() { return globalff; }
    public void setGlobalff(boolean _globalff) { globalff = _globalff; }
    public boolean getClanChatEnable() { return clanChatEnable; }
    public String getTagBracketLeft() { return tagBracketLeft; }
    public String getTagBracketRight() { return tagBracketRight; }
    public String getTagBracketColor() { return Helper.toColor(tagBracketColor); }
    public boolean isePurchaseCreation() { return ePurchaseCreation; }
    public boolean isePurchaseVerification() { return ePurchaseVerification; }
    public boolean isePurchaseInvite() { return ePurchaseInvite; }
    public double getCreationPrice() { return eCreationPrice; }
    public double getVerificationPrice() { return eVerificationPrice; }
    public double getInvitePrice() { return eInvitePrice; }
    public boolean isBbShowOnLogin() { return bbShowOnLogin; }
    public boolean getSafeCivilians() { return safeCivilians; }
    public boolean isConfirmationForPromote() { return confirmationForPromote; }
    public boolean isConfirmationForDemote() { return confirmationForDemote; }
    public boolean isUseColorCodeFromPrefix() { return useColorCodeFromPrefix; }
    public String getCommandAlly() { return commandAlly; }
    public boolean isAllyChatEnable() { return allyChatEnable; }
    public String getAllyChatMessageColor() { return Helper.toColor(allyChatMessageColor); }
    public String getAllyChatNameColor() { return Helper.toColor(allyChatNameColor); }
    public String getAllyChatTagBracketLeft() { return allyChatTagBracketLeft; }
    public String getAllyChatTagBracketRight() { return allyChatTagBracketRight; }
    public String getAllyChatBracketColor()  { return Helper.toColor(allyChatBracketColor); }
    public String getAllyChatPlayerBracketLeft() { return allyChatPlayerBracketLeft; }
    public String getAllyChatPlayerBracketRight() { return allyChatPlayerBracketRight; }
    public String getCommandGlobal() { return commandGlobal; }
    public String getAllyChatTagColor() { return Helper.toColor(allyChatTagColor); }
    public boolean isClanFFOnByDefault() { return clanFFOnByDefault; }
    public boolean isCompatMode() { return compatMode; }
    public boolean isHomebaseSetOnce() { return homebaseSetOnce; }
    public int getWaitSecs() { return waitSecs; }
    public void setWaitSecs(int _waitSecs) { waitSecs = _waitSecs; }
    public boolean isEnableAutoGroups() { return enableAutoGroups; }
    public boolean isPvpOnlywhileInWar() { return pvpOnlywhileInWar; }
    public boolean ismChatIntegration() { return mChatIntegration; }
    public boolean isDebugging() { return debugging; }
    public boolean isKeepOnHome() { return keepOnHome; }
    public boolean isDropOnHome() { return dropOnHome; }
    public List<Integer> getItemsList() { return Collections.unmodifiableList(itemsList); }
    public boolean isTeleportOnSpawn() { return teleportOnSpawn; }
    public boolean isTagBasedClanChat() { return tagBasedClanChat; }
    public String getClanChatRankColor() { return Helper.toColor(clanChatRankColor); }
    public boolean isePurchaseHomeTeleport() { return ePurchaseHomeTeleport; }
    public double getHomeTeleportPrice() { return eHomeTeleportPrice; }
    public boolean isePurchaseHomeTeleportSet() { return ePurchaseHomeTeleportSet; }
    public double getHomeTeleportPriceSet() { return eHomeTeleportPriceSet; }
    public boolean isMoneyPerKill() { return moneyperkill; }
    public double getKDRMultipliesPerKill() { return KDRMultipliesPerKill; }
    public boolean isTeleportBlocks() { return teleportBlocks; }
    public boolean isAutoGroupGroupName() { return AutoGroupGroupName; }
    public boolean isTamableMobsSharing() { return tamableMobsSharing; }
    public boolean isOnlineMode() { return onlineMode; }
    public boolean isDisableMessages() { return disableMessages; }
    public boolean getAllowReGroupCommand() { return allowReGroupCommand; }
    public boolean getUseThreads() { return useThreads; }
    public boolean getUseBungeeCord() { return useBungeeCord; }
    public String getTagSeparatorLeaderColor() { return Helper.toColor(tagSeparatorLeaderColor); }
    public String getTagBracketLeaderColor() { return Helper.toColor(tagBracketLeaderColor); }
    public int getMaxAsksPerRequest() { return maxAsksPerRequest; }
    public boolean isForceCommandPriority() { return forceCommandPriority; }
    public int getMaxMembers() { return maxMembers; }
}
