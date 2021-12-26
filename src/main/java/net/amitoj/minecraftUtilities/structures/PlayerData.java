package net.amitoj.minecraftUtilities.structures;

import java.util.UUID;

public class PlayerData {
    public UUID uuid;
    public String discordId;
    public String ip;

    public PlayerData(String uuid, String discordId, String ip) {
        this.uuid = UUID.fromString(uuid);
        this.discordId = discordId;
        this.ip = ip;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }
}
