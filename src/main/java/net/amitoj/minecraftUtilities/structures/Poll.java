package net.amitoj.minecraftUtilities.structures;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Poll {
    public UUID uuid;
    public PollType type;
    public Integer forVotes;
    public Integer againstVotes;
    public Stack<Vote> votes;
    public Date expires;

    private MinecraftUtilities _plugin;

    public Poll(MinecraftUtilities plugin, PollType type) {
        this._plugin = plugin;
        this.uuid = UUID.randomUUID();
        this.type = type;
        this.forVotes = 0;
        this.againstVotes = 0;

        Calendar date = Calendar.getInstance();
        long timeInSecs = date.getTimeInMillis();
        this.expires = new Date(timeInSecs + (10 * 60 * 1000));
    }

    public Poll incrementVote(String discordId) {
        Vote vote = new Vote(_plugin, discordId, 1);
        if (vote.valid) {
            if (this.type == PollType.KICK || this.type == PollType.WHITELIST) {
                Player onlinePlayer = Bukkit.getPlayer(vote.playerData.uuid);
                if (onlinePlayer != null) vote.setVote(2);
            }
            this.votes.push(vote);
        }
        return this;
    }

    public Poll decrementVote(String discordId) {
        Vote vote = new Vote(_plugin, discordId, -1);
        if (vote.valid) {
            if (this.type == PollType.KICK || this.type == PollType.WHITELIST) {
                Player onlinePlayer = Bukkit.getPlayer(vote.playerData.uuid);
                if (onlinePlayer != null) vote.setVote(-2);
            }
            this.votes.push(vote);
        }
        return this;
    }
}

enum PollType {
    WHITELIST,
    BAN,
    KICK
}
