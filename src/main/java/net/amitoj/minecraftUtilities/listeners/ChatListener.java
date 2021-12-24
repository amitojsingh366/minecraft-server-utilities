package net.amitoj.minecraftUtilities.listeners;


import io.papermc.paper.event.player.AsyncChatEvent;
import net.amitoj.minecraftUtilities.MinecraftUtilities;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.json.simple.JSONObject;

import static net.amitoj.minecraftUtilities.util.Util.sendWH;


public class ChatListener implements Listener {
    private boolean _enabled = true;
    private String _webhookUrl;

    public ChatListener(MinecraftUtilities plugin) {
        this._enabled = plugin.config.enabled;
        this._webhookUrl = plugin.config.chatWebhookUrl;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (_enabled) {
            JSONObject postData = new JSONObject();
            postData.put("content", ChatColor.stripColor(event.message().toString()));
            postData.put("username", event.getPlayer().getPlayerProfile().getName());
            postData.put("avatar_url", "https://mc-heads.net/avatar/" + event.getPlayer().getPlayerProfile().getName());

            sendWH(postData, _webhookUrl);
        }
    }


    public String get_webhookUrl() {
        return _webhookUrl;
    }

    public void set_webhookUrl(String _webhookUrl) {
        this._webhookUrl = _webhookUrl;
    }

    public void set_enabled(boolean _enabled) {
        this._enabled = _enabled;
    }
}
