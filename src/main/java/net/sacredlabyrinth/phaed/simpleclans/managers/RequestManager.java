package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.ClanRequest;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.Request;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.VoteResult;
import net.sacredlabyrinth.phaed.simpleclans.events.RequestEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.RequestFinishedEvent;

/**
 * @author phaed
 */
public final class RequestManager {
    private SimpleClans plugin;
    HashMap<String, Request> requests = new HashMap<>();

    public RequestManager() {
        plugin = SimpleClans.getInstance();
        askerTask();
    }

    /**
     * Check whether the clan has a pending request
     *
     * @param tag
     * @return
     */
    public boolean hasRequest(String tag) {
        return requests.containsKey(tag);
    }
    public Request getRequest(String tag) {
        return requests.get( tag );
    }
    /**
     * Add a demotion request
     *
     * @param requester
     * @param demotedName
     * @param clan
     */
    public void addDemoteRequest(ClanPlayer requester, String demotedName, Clan clan) {
        String msg = MessageFormat.format( plugin.getLanguageManager()
                        .get( "asking.for.the.demotion" ), Helper.capitalize(requester.getName()), demotedName);

        ClanPlayer demotedTp = plugin.getClanManager().getClanPlayer( Bukkit.getPlayer( demotedName ));

        Set<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());
        acceptors.remove(demotedTp);

        Request req = new Request( ClanRequest.DEMOTE, acceptors, requester, demotedName, clan, msg);
        requests.put(clan.getTag(), req);
        ask(req);
    }

    /**
     * Add a promotion request
     *
     * @param requester
     * @param promotedName
     * @param clan
     */
    public void addPromoteRequest(ClanPlayer requester, String promotedName, Clan clan) {
        String msg = MessageFormat.format(plugin.getLanguageManager()
                        .get( "asking.for.the.promotion"), Helper.capitalize(requester.getName()), promotedName);

        Set<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request( ClanRequest.PROMOTE, acceptors, requester, promotedName, clan, msg);
        requests.put(clan.getTag(), req);
        ask(req);
    }

    /**
     * Add a clan disband request
     *
     * @param requester
     * @param clan
     */
    public void addDisbandRequest(ClanPlayer requester, Clan clan) {
        String msg = MessageFormat.format(plugin.getLanguageManager()
                        .get( "asking.for.the.deletion"), Helper.capitalize(requester.getName()));

        Set<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request( ClanRequest.DISBAND, acceptors, requester, clan.getTag(), clan, msg);
        requests.put(clan.getTag(), req);
        ask(req);
    }

    /**
     * Add a member invite request
     *
     * @param requester
     * @param invitedName
     * @param clan
     */
    public void addInviteRequest(ClanPlayer requester, UUID invited, Clan clan)  {
        String msg = MessageFormat.format(plugin.getLanguageManager()
                        .get( "inviting.you.to.join"), Helper.capitalize(requester.getName()), clan.getName());
        
        Request req = new Request( ClanRequest.INVITE, requester, invited, clan, msg);
        requests.put(invited.toString(), req);
        ask(req);
    }

    /**
     * Add an clan war request
     *
     * @param requester
     * @param warClan
     * @param requestingClan
     */
    public void addWarStartRequest(ClanPlayer requester, Clan warClan, Clan requestingClan) {
        String msg = MessageFormat.format(plugin.getLanguageManager()
                .get( "proposing.war"), Helper.capitalize(requestingClan.getName()), Helper.stripColors(warClan.getColorTag()));

        Set<ClanPlayer> acceptors = Helper.stripOffLinePlayers(warClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request( ClanRequest.START_WAR, acceptors, requester, warClan.getTag(), requestingClan, msg);
        requests.put(warClan.getTag(), req);
        ask(req);
    }

    /**
     * Add an war end request
     *
     * @param requester
     * @param warClan
     * @param requestingClan
     */
    public void addWarEndRequest(ClanPlayer requester, Clan warClan, Clan requestingClan) {
        String msg = MessageFormat.format(plugin.getLanguageManager()
                .get( "proposing.to.end.the.war"), Helper.capitalize(requestingClan.getName()), Helper.stripColors(warClan.getColorTag()));

        Set<ClanPlayer> acceptors = Helper.stripOffLinePlayers(warClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request( ClanRequest.END_WAR, acceptors, requester, warClan.getTag(), requestingClan, msg);
        requests.put(warClan.getTag(), req);
        ask(req);
    }

    /**
     * Add an clan alliance request
     *
     * @param requester
     * @param allyClan
     * @param requestingClan
     */
    public void addAllyRequest(ClanPlayer requester, Clan allyClan, Clan requestingClan) {
        String msg = MessageFormat.format(plugin.getLanguageManager()
                .get( "proposing.an.alliance"), Helper.capitalize(requestingClan.getName()), Helper.stripColors(allyClan.getColorTag()));

        Set<ClanPlayer> acceptors = Helper.stripOffLinePlayers(allyClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request( ClanRequest.CREATE_ALLY, acceptors, requester, allyClan.getTag(), requestingClan, msg);
        requests.put(allyClan.getTag(), req);
        ask(req);
    }

    /**
     * Add an clan rivalry break request
     *
     * @param requester
     * @param rivalClan
     * @param requestingClan
     */
    public void addRivalryBreakRequest(ClanPlayer requester, Clan rivalClan, Clan requestingClan) {
        String msg = MessageFormat.format(plugin.getLanguageManager()
                .get( "proposing.to.end.the.rivalry"), Helper.capitalize(requestingClan.getName()), Helper.stripColors(rivalClan.getColorTag()));

        Set<ClanPlayer> acceptors = Helper.stripOffLinePlayers(rivalClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request( ClanRequest.BREAK_RIVALRY, acceptors, requester, rivalClan.getTag(), requestingClan, msg);
        requests.put(rivalClan.getTag(), req);
        ask(req);
    }

    /**
     * Record one player's accept vote
     *
     * @param cp
     */
    public void accept(ClanPlayer cp) {
        Request req = requests.get(cp.getTag());

        if (req != null) {
            req.vote(cp.getUniqueId(), VoteResult.ACCEPT);
            processResults(req);
        }
        else {
            req = requests.get(cp.getCleanName());

            if (req != null) {
                processInvite(req, VoteResult.ACCEPT);
            }
        }
    }

    /**
     * Record one player's deny vote
     *
     * @param cp
     */
    public void deny(ClanPlayer cp) {
        Request req = requests.get(cp.getTag());

        if (req != null) {
            req.vote(cp.getUniqueId(), VoteResult.DENY);
            processResults(req);
        }
        else {
            req = requests.get(cp.getCleanName());

            if (req != null) {
                processInvite(req, VoteResult.DENY);
            }
        }
    }

    /**
     * Process the answer from an invite and add the player to the clan if accepted
     *
     * @param req
     * @param vote
     */
    public void processInvite(Request req, VoteResult vote) {
        Clan clan = req.getClan();
        UUID invited = UUID.fromString( req.getTarget() );
        LanguageManager lang = plugin.getLanguageManager();
        
        if (vote.equals(VoteResult.ACCEPT)) {
            ClanPlayer cp = plugin.getClanManager().getCreateClanPlayer(invited);
            if (cp == null) {
                return;
            }

            clan.addBb(ChatColor.AQUA + MessageFormat.format( lang.get("joined.the.clan"), cp.getName()));
            plugin.getClanManager().serverAnnounce(MessageFormat.format(lang.get("has.joined"), cp.getName(), clan.getName()));
            clan.addPlayerToClan(cp);
        }
        else clan.leaderAnnounce(
                ChatColor.RED + MessageFormat.format(lang.get("membership.invitation"), Bukkit.getPlayer( invited ).getName()));

        requests.remove(req.getTarget().toLowerCase());
    }

    /**
     * Check to see if votes are complete and process the result
     *
     * @param req
     */
    public void processResults(Request req) {
        LanguageManager lang = plugin.getLanguageManager();
        Clan clan = req.getClan();

        
        if (req.getType().equals(ClanRequest.START_WAR)) {
            Clan war = plugin.getClanManager().getClan(req.getTarget());
            ClanPlayer cp = req.getRequester();

            if (war != null && clan != null) {
                List<String> accepts = req.getAccepts();
                List<String> denies = req.getDenies();

                if (!accepts.isEmpty()) {
                    clan.addWarringClan(war);
                    war.addWarringClan(clan);

                    war.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("you.are.at.war"), Helper.capitalize(war.getName()), clan.getColorTag()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("you.are.at.war"), Helper.capitalize(clan.getName()), war.getColorTag()));
                }
                else {
                    war.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("denied.war.req"), Helper.capitalize(denies.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("end.war.denied"), Helper.capitalize(war.getName())));
                }
            }
        }
        else if (req.getType().equals(ClanRequest.END_WAR)) {
            Clan war = plugin.getClanManager().getClan(req.getTarget());
            ClanPlayer cp = req.getRequester();

            if (war != null && clan != null) {
                List<String> accepts = req.getAccepts();
                List<String> denies = req.getDenies();

                if (!accepts.isEmpty()) {
                    clan.removeWarringClan(war);
                    war.removeWarringClan(clan);

                    war.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("you.are.no.longer.at.war"), Helper.capitalize(accepts.get(0)), clan.getColorTag()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("you.are.no.longer.at.war"), Helper.capitalize(clan.getName()), Helper.capitalize(war.getColorTag())));
                }
                else {
                    war.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("denied.war.end"), Helper.capitalize(denies.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("end.war.denied"), Helper.capitalize(war.getName())));
                }
            }
        }
        else if (req.getType().equals(ClanRequest.CREATE_ALLY)) {
            Clan ally = plugin.getClanManager().getClan(req.getTarget());
            ClanPlayer cp = req.getRequester();

            if (ally != null && clan != null) {
                List<String> accepts = req.getAccepts();
                List<String> denies = req.getDenies();

                if (!accepts.isEmpty()) {
                    clan.addAlly(ally);

                    ally.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("accepted.an.alliance"), Helper.capitalize(accepts.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("created.an.alliance"), Helper.capitalize(cp.getName()), Helper.capitalize(ally.getName())));
                }
                else {
                    ally.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("denied.an.alliance"), Helper.capitalize(denies.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("the.alliance.was.denied"), Helper.capitalize(ally.getName())));
                }
            }
        }
        else if (req.getType().equals(ClanRequest.BREAK_RIVALRY)) {
            Clan rival = plugin.getClanManager().getClan(req.getTarget());
            ClanPlayer cp = req.getRequester();

            if (rival != null && clan != null) {
                List<String> accepts = req.getAccepts();
                List<String> denies = req.getDenies();

                if (!accepts.isEmpty()) {
                    clan.removeRival(rival);
                    rival.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("broken.the.rivalry"), Helper.capitalize(accepts.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("broken.the.rivalry.with"), Helper.capitalize(cp.getName()), Helper.capitalize(rival.getName())));
                }
                else {
                    rival.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("denied.to.make.peace"), Helper.capitalize(denies.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(lang.get("peace.agreement.denied"), Helper.capitalize(rival.getName())));
                }
            }
        }
        else if (req.votingFinished()) {
            List<String> denies = req.getDenies();

            if (req.getType().equals(ClanRequest.DEMOTE)) {
                String demoted = req.getTarget();
                UUID demotedUniqueId = Helper.getCachedPlayerUUID(demoted);

                if ( demotedUniqueId != null) return;

                if (denies.isEmpty()) {
                    clan.addBb(lang.get("leaders"), ChatColor.AQUA + MessageFormat.format(lang.get("demoted.back.to.member"), Helper.capitalize(demoted)));           
                    clan.demote(demotedUniqueId);   
                }
                else {
                    String deniers = Helper.capitalize(String.join(", ", denies));
                    clan.leaderAnnounce(ChatColor.RED + MessageFormat.format(lang.get("denied.demotion"), deniers, demoted));
                }
            }
            else if (req.getType().equals(ClanRequest.PROMOTE)) {
                String promoted = req.getTarget();
                UUID promotedUniqueId = Helper.getCachedPlayerUUID(promoted);

                if (promotedUniqueId == null) return; 
                
                if (denies.isEmpty()) {
                    clan.addBb(lang.get("leaders"), ChatColor.AQUA + MessageFormat.format(lang.get("promoted.to.leader"), Helper.capitalize(promoted)));                   
                    clan.promote(promotedUniqueId);                   
                }
                else {
                    String deniers = Helper.capitalize(String.join(", ", denies));
                    clan.leaderAnnounce(ChatColor.RED + MessageFormat.format(lang.get("denied.the.promotion"), deniers, promoted));
                }
            }
            else if (req.getType().equals(ClanRequest.DISBAND)) {
                
                if (denies.isEmpty()) {
                    clan.addBb(lang.get("leaders"), ChatColor.AQUA + MessageFormat.format(lang.get("has.been.disbanded"), clan.getName()));
                    clan.disband();
                }
                else {
                    String deniers = Helper.capitalize(String.join(", ", denies));
                    clan.leaderAnnounce(ChatColor.RED + MessageFormat.format(lang.get("clan.deletion"), deniers));
                }
            }
            req.cleanVotes();
        }
        requests.remove(req.getTarget());
        Bukkit.getPluginManager().callEvent(new RequestFinishedEvent(req));
    }

    /**
     * End a pending request prematurely
     *
     * @param playerName
     * @return
     */
    public boolean endPendingRequest(String playerName) {
        LanguageManager lang = plugin.getLanguageManager();
        
        for ( Request req : new LinkedList<Request>( requests.values() ) ) {
            
            for ( ClanPlayer cp : req.getAcceptors() ) {
                
                if ( cp.getName().equalsIgnoreCase(playerName) ) {
                    
                    req.getClan().leaderAnnounce(
                            MessageFormat.format( lang.get("signed.off.request.cancelled"), 
                                                ChatColor.RED + Helper.capitalize(playerName), req.getType() ) );
                    
                    requests.remove( req.getClan().getTag() );
                    break;
                }
            }
        }
        return false;
    }

    /**
     * Starts the task that asks for the votes of all requests
     */
    public void askerTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for ( Iterator<Map.Entry<String, Request>> iter = requests.entrySet().iterator(); iter.hasNext(); ) {
                    Request req = iter.next().getValue();

                    if (req == null) {
                        continue;
                    }
                    if (req.reachedRequestLimit()) {
                        iter.remove();
                    }
                    ask(req);
                    req.incrementAskCount();
                }
            }
        }.runTaskTimerAsynchronously( plugin, 0, plugin.getSettingsManager().getRequestFreqencySecs() * 20L );
    }

    /**
     * Asks a request to players for votes
     *
     * @param req
     */
    public void ask(Request req) {
        SettingsManager settings = plugin.getSettingsManager();
        
        final String tag = String.join( "", settings.getClanChatBracketColor(),
                                            settings.getClanChatTagBracketLeft(),
                                            settings.getTagDefaultColor(),
                                            req.getClan().getColorTag(),
                                            settings.getClanChatBracketColor(),
                                            settings.getClanChatTagBracketRight() );
        
        final String message = String.join( "", tag, " ", settings.getRequestMessageColor(), req.getMsg() );
        
        final String options = MessageFormat.format(
                plugin.getLanguageManager().get( "accept.or.deny" ), 
                                                String.join( "", ChatBlock.makeEmpty( Helper.stripColors(tag) ),
                                                                 " ", ChatColor.DARK_GREEN.toString(),
                                                                 "/", settings.getCommandAccept(),
                                                                 settings.getPageHeadingsColor() ), 
                                                String.join( "/", ChatColor.DARK_RED.toString(), settings.getCommandDeny() ) );

        if (req.getType().equals(ClanRequest.INVITE)) {
            Player player = Helper.getPlayer(req.getTarget());

            if (player != null) {
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, message);
                ChatBlock.sendMessage(player, options);
                ChatBlock.sendBlank(player);
            }
        }
        else {
            for (ClanPlayer cp : req.getAcceptors()) {
                if ( !cp.hasVote(req) ) {
                    Player player = cp.toPlayer();

                    if (player != null) {
                        ChatBlock.sendBlank(player);
                        ChatBlock.sendMessage(player, message);
                        ChatBlock.sendMessage(player, options);
                        ChatBlock.sendBlank(player);
                    }
                }
            }
        }

        Bukkit.getPluginManager().callEvent(new RequestEvent(req));
    }
}
