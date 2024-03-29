package net.amitoj.minecraftUtilities.structures;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

import static net.amitoj.minecraftUtilities.util.Util.*;

public class Poll {
    public UUID uuid;
    public PollType type;
    public Integer upVotes;
    public Integer downVotes;
    public Stack<Vote> votes = new Stack<>();
    public Boolean expired;
    public String username;

    private final MinecraftUtilities _plugin;
    private final PollManager _manager;
    private InteractionHook _iHook;

    public Poll(PollManager manager, MinecraftUtilities plugin, PollType type, String username) {
        this._manager = manager;
        this._plugin = plugin;
        this.username = username;
        this.uuid = UUID.randomUUID();
        this.type = type;
        this.upVotes = 0;
        this.downVotes = 0;
        this.expired = false;
    }

    public void vote(String discordId, Boolean downVote) {
        Vote exists = votes.stream().filter(v -> Objects.equals(v.voterId, discordId)).findAny().orElse(null);
        if (exists == null) {
            Vote vote = new Vote(_plugin, discordId, downVote ? VoteType.DOWNVOTE : VoteType.UPVOTE);
            if (vote.valid) {
                if (this.type == PollType.KICK || this.type == PollType.WHITELIST || this.type == PollType.REBOOT) {
                    Player onlinePlayer = Bukkit.getPlayer(vote.playerData.uuid);
                    if (onlinePlayer != null) vote.setWasDouble(true);
                }
                this.votes.push(vote);
            }
            calculateVotes();
            if (_iHook != null) {
                MessageEditData m = generatePollEmbed(this, username, false);
                _iHook.editOriginal(m).queue();
            }
            if (votes.size() >= (Math.max(Bukkit.getOnlinePlayers().size(), this.type == PollType.KICK ? 0 : 3)))
                _manager.expirePoll(this);
        }
    }

    public void calculateVotes() {
        int upVotes = 0;
        int downVotes = 0;

        for (Vote vote : votes) {
            if (vote.type == VoteType.UPVOTE) {
                if (vote.wasDouble) upVotes += 2;
                else upVotes += 1;
            } else if (vote.type == VoteType.DOWNVOTE) {
                if (vote.wasDouble) downVotes += 2;
                else downVotes += 1;
            }
        }

        this.upVotes = upVotes;
        this.downVotes = downVotes;
    }

    public void set_iHook(InteractionHook _iHook) {
        this._iHook = _iHook;
    }

    public void onExpire() {
        if (!this.expired) {
            this.expired = true;
            calculateVotes();
            _iHook.editOriginal(generatePollEmbed(this, this.username, true)).queue();
            if (this.upVotes > this.downVotes && (this.type == PollType.KICK || this.votes.size() >=
                    (Math.max(Bukkit.getOnlinePlayers().size(), 3)))) {
                switch (this.type) {
                    case KICK:
                        Player kickPlayer = Bukkit.getPlayer(this.username);
                        if (kickPlayer != null) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(_plugin, () -> {
                                kickPlayer.kick(null);
                            });
                            _iHook.sendMessage("`" + this.username + "` was kicked!").queue();
                        } else {
                            _iHook.sendMessage("`" + this.username + "` is not online anymore!").queue();
                        }
                        break;
                    case BAN:
                        OfflinePlayer banPlayer = Bukkit.getOfflinePlayerIfCached(this.username);
                        if (banPlayer != null) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(_plugin, () -> {
                                banPlayer.banPlayer("Banned by majority vote");
                            });
                            _iHook.sendMessage("`" + this.username + "` was banned!").queue();
                        } else {
                            _iHook.sendMessage("`" + this.username + "` is not a valid player!").queue();
                        }
                        break;
                    case WHITELIST:
                        addToWhitelist(generateOfflineId(this.username), this.username);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(_plugin, Bukkit::reloadWhitelist);
                        _iHook.sendMessage("`" + this.username + "` was whitelisted!").queue();
                        break;
                    case REBOOT:
                        _iHook.sendMessage("This functionality has been removed, please contact the server owner.").queue();
                        break;
                }
            } else {
                _iHook.sendMessage("This poll did not have enough votes in favor of the action to be executed").queue();
            }
        }
    }
}

