package net.amitoj.minecraftUtilities.structures;

import net.amitoj.minecraftUtilities.MinecraftUtilities;

import java.util.*;

public class PollManager {
    private MinecraftUtilities _plugin;

    public Map<UUID, Poll> polls = new HashMap<UUID, Poll>();

    public PollManager(MinecraftUtilities plugin){
        this._plugin = plugin;
    }

    public Poll createPoll(PollType type){
        Poll poll = new Poll(_plugin, type);
        polls.put(poll.uuid, poll);
        return poll;
    }

    public Poll getPoll(UUID uuid){
        return polls.get(uuid);
    }
}
