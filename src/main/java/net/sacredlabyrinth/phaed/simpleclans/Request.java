package net.sacredlabyrinth.phaed.simpleclans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author phaed
 */
public final class Request {
    private Set<ClanPlayer> acceptors = new HashSet<>();
    private Clan clan;
    private String msg;
    private String target;
    private ClanRequest type;
    private ClanPlayer requester;
    private int askCount;

    public Request( ClanRequest _type, Set<ClanPlayer> _acceptors, 
                            ClanPlayer _requester, String _target, Clan _clan, String _msg) {
        type = _type;
        target = _target;
        clan = _clan;
        msg = _msg;
        if (_acceptors != null) {
            acceptors = _acceptors;
        }
        requester = _requester;
        cleanVotes();
    }

    public Request( ClanRequest _type, ClanPlayer _requester, 
                                            UUID invited, Clan _clan, String _msg ) {
        type = _type;
        target = invited.toString();
        clan = _clan;
        msg = _msg;
        requester = _requester;
        cleanVotes();
    }

    public ClanRequest getType() { return type; }
    public Set<ClanPlayer> getAcceptors() { return Collections.unmodifiableSet(acceptors); }
    public Clan getClan() { return clan; }
    public String getMsg() { return msg; }
    public String getTarget() { return target; }
    public ClanPlayer getRequester() { return requester; }

    /**
     * Used for leader voting
     *
     * @param playerNAme
     * @param vote
     */
    public void vote(UUID playeruuid, VoteResult vote) {
        for (ClanPlayer cp : acceptors) {
            if (cp.getUniqueId().equals(playeruuid)) {
                cp.setVote(vote, this);
            }
        }
    }

    /**
     * Check whether all leaders have voted
     *
     * @return
     */
    public boolean votingFinished() {
        return acceptors.parallelStream().allMatch( a -> a.hasVote(this) );
    }

    /**
     * Returns the players who have denied the request
     *
     * @return
     */
    public List<String> getDenies() {
        List<String> out = new ArrayList<>();

        for (ClanPlayer cp : acceptors) {
            if (cp.hasVote(this) && cp.getVote(this).equals(VoteResult.DENY)) {
            	out.add(cp.getName());
            }
        }
        return out;
    }

    /**
     * Returns the players who have accepts the request
     *
     * @return
     */
    public List<String> getAccepts() {
        List<String> out = new ArrayList<>();

        for (ClanPlayer cp : acceptors) {
            if (cp.hasVote(this) && cp.getVote(this).equals(VoteResult.ACCEPT)) {
            	out.add(cp.getName());
            }
        }
        return out;
    }

    public void cleanVotes() {
        acceptors.forEach( a -> a.clearVote(this) );
    }

    public void incrementAskCount() {
        askCount += 1;
    }
    public boolean reachedRequestLimit() {
        return askCount > SimpleClans.getInstance().getSettingsManager().getMaxAsksPerRequest();
    }
}
