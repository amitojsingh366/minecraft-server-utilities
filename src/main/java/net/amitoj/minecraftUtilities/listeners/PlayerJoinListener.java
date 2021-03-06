package net.amitoj.minecraftUtilities.listeners;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.structures.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Objects;

import static net.amitoj.minecraftUtilities.util.Util.sendWH;
import static net.amitoj.minecraftUtilities.util.Util.sendLoginDM;

public class PlayerJoinListener implements Listener {
    private boolean _enabled = true;
    private String _webhookUrl;
    private String _serverName;
    private String _serverIcon;
    private MinecraftUtilities _plugin;

    public PlayerJoinListener(MinecraftUtilities plugin) {
        this._enabled = plugin.config.enabled;
        this._webhookUrl = plugin.config.eventsWebhookUrl;
        this._serverName = plugin.config.serverName;
        this._serverIcon = plugin.config.serverIcon;
        this._plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (_enabled) {
            JSONObject postData = new JSONObject();
            postData.put("content", "");


            JSONArray embeds = new JSONArray();
            JSONObject embed = new JSONObject();
            JSONObject thumbnail = new JSONObject();
            thumbnail.put("url", "https://mc-heads.net/avatar/" + event.getPlayer().getPlayerProfile().getName());

            embed.put("title", "Player Joined");
            embed.put("description", event.getPlayer().getPlayerProfile().getName() + " has joined the server!");
            embed.put("color", 65280);
            embed.put("thumbnail", thumbnail);
            embeds.add(embed);

            postData.put("embeds", embeds);
            postData.put("username", _serverName);
            postData.put("avatar_url", _serverIcon);

            sendWH(postData, _webhookUrl);

            PlayerData playerData = _plugin.database.getPlayerData(event.getPlayer());
            if (!Objects.equals(playerData.discordId, "")) {
                if (!Objects.equals(playerData.ip, event.getPlayer().getAddress().getHostName())) {
                    sendLoginDM(_plugin.discordClient.jda, playerData.discordId, event.getPlayer());
                }
            } else {
                event.getPlayer().sendMessage("Please save your discord tag using the command /discord (/discord Amitoj#0001)");
            }
        }
    }
}

// https://cdn.amitoj.net/mc.amitoj.net/server-icon.png