package net.amitoj.minecraftUtilities.structures;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;


public class Vote {
    public String voterId;
    public OfflinePlayer player;
    public PlayerData playerData;
    public VoteType type;
    public Boolean wasDouble = false;
    public Boolean valid = true;

    private MinecraftUtilities _plugins;

    public Vote(MinecraftUtilities plugin, String discordId, VoteType type) {
        this.voterId = discordId;
        this._plugins = plugin;
        this.playerData = _plugins.database.getPlayerDataByDiscordId(discordId);
        this.type = type;
        if (this.playerData != null) {
            this.player = Bukkit.getOfflinePlayer(UUID.fromString(this.playerData.uuid));
        } else {
            this.valid = false;
        }
    }

    public void setWasDouble(Boolean wasDouble) {
        this.wasDouble = wasDouble;
    }
}

