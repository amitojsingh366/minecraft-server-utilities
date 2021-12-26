package net.amitoj.minecraftUtilities.structures;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.*;

public class PollManager {
    private MinecraftUtilities _plugin;

    public Map<UUID, Poll> polls = new HashMap<UUID, Poll>();

    public PollManager(MinecraftUtilities plugin) {
        this._plugin = plugin;
    }

    public Poll createPoll(PollType type, String username) {
        Poll poll = new Poll(this, _plugin, type, username);
        polls.put(poll.uuid, poll);
        this.scheduleExpiration(this, poll);
        return poll;
    }

    public void expirePoll(Poll poll) {
        poll.onExpire();
        polls.remove(poll.uuid);
    }

    private void scheduleExpiration(PollManager manager, Poll poll) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (!poll.expired) manager.expirePoll(poll);
                    }
                },
                300000
        );
    }

    public Poll getPoll(UUID uuid) {
        return polls.get(uuid);
    }
}
