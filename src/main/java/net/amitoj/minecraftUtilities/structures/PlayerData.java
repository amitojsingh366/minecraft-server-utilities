package net.amitoj.minecraftUtilities.structures;

public class PlayerData {
    public String uuid;
    public String discordId;
    public String ip;

    public PlayerData(String uuid, String discordId, String ip) {
        this.uuid = uuid;
        this.discordId = discordId;
        this.ip = ip;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }
}
