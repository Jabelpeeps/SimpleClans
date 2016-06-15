package net.sacredlabyrinth.phaed.simpleclans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author phaed
 */
public final class Request {
    private List<ClanPlayer> acceptors = new ArrayList<>();
    private Clan clan;
    private String msg;
    private String target;
    private ClanRequest type;
    private ClanPlayer requester;
    private int askCount;

    public Request( SimpleClans plugin, ClanRequest type, List<ClanPlayer> acceptors, 
                            ClanPlayer requester, String target, Clan clan, String msg) {
        this.type = type;
        this.target = target;
        this.clan = clan;
        this.msg = msg;
        if (acceptors != null) {
            this.acceptors = acceptors;
        }
        this.requester = requester;

        cleanVotes();
    }

    public ClanRequest getType() {
        return type;
    }
    public void setType(ClanRequest type) {
        this.type = type;
    }
    public List<ClanPlayer> getAcceptors() {
        return Collections.unmodifiableList(acceptors);
    }
    public void setAcceptors(List<ClanPlayer> acceptors) {
        this.acceptors = acceptors;
    }
    public Clan getClan() {
        return clan;
    }
    public void setClan(Clan clan) {
        this.clan = clan;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Used for leader voting
     *
     * @param playerNAme
     * @param vote
     */
    public void vote(String playerNAme, VoteResult vote) {
        for (ClanPlayer cp : acceptors) {
            if (cp.getName().equalsIgnoreCase(playerNAme)) {
                cp.setVote(vote);
            }
        }
    }

    /**
     * Check whether all leaders have voted
     *
     * @return
     */
    public boolean votingFinished() {
        for (ClanPlayer cp : acceptors) {
            if (cp.getVote() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the players who have denied the request
     *
     * @return
     */
    public List<String> getDenies() {
        List<String> out = new ArrayList<>();

        for (ClanPlayer cp : acceptors) {
            if (cp.getVote() != null && cp.getVote().equals(VoteResult.DENY)) {
            	out.add(cp.getName());
            }
        }
        return out;
    }

    /**
     * Returns the players who have denied the request
     *
     * @return
     */
    public List<String> getAccepts() {
        List<String> out = new ArrayList<>();

        for (ClanPlayer cp : acceptors) {
            if (cp.getVote() != null && cp.getVote().equals(VoteResult.ACCEPT)) {
            	out.add(cp.getName());
            }
        }
        return out;
    }

    /**
     * Cleans votes
     */
    public void cleanVotes() {
        for (ClanPlayer cp : acceptors) {
            cp.setVote(null);
        }
    }
    
    /**
     * @return the requester
     */
    public ClanPlayer getRequester() {
        return requester;
    }
    /**
     * @param requester the requester to set
     */
    public void setRequester(ClanPlayer requester) {
        this.requester = requester;
    }
    public void incrementAskCount() {
        askCount += 1;
    }
    public boolean reachedRequestLimit() {
        return askCount > SimpleClans.getInstance().getSettingsManager().getMaxAsksPerRequest();
    }
}
