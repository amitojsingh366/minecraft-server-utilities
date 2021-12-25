package net.amitoj.minecraftUtilities.structures;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.structures.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;


public class Vote {
    public OfflinePlayer player;
    public PlayerData playerData;
    public Integer vote;
    public Boolean valid = true;

    private MinecraftUtilities _plugins;

    public Vote(MinecraftUtilities plugin, String discordId, Integer vote) {
        this._plugins = plugin;
        this.playerData = _plugins.database.getPlayerDataByDiscordId(discordId);
        this.vote = vote;
        if (this.playerData != null) {
            this.player = Bukkit.getOfflinePlayer(UUID.fromString(this.playerData.uuid));
        } else {
            this.valid = false;
        }
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }
}
