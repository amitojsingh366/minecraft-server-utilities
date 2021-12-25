package net.amitoj.minecraftUtilities.discord.listeners;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.util.PlayerData;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class ButtonListener extends ListenerAdapter {
    private MinecraftUtilities _plugin;

    public ButtonListener(MinecraftUtilities plugin) {
        this._plugin = plugin;
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        String buttonId = Objects.requireNonNull(event.getButton()).getId();
        assert buttonId != null;
        String[] resolvedAction = buttonId.split(":");

        switch (resolvedAction[0]) {
            case "whitelist":
                Player whitelistPlayer = Bukkit.getServer().getPlayer(UUID.fromString(resolvedAction[1]));
                PlayerData whitelistPlayerData = new PlayerData(resolvedAction[1], event.getUser().getId(), whitelistPlayer.getAddress().getHostName());
                _plugin.database.updatePlayerData(whitelistPlayerData);
                event.reply("`" + whitelistPlayer.getAddress().getHostName() + "` has been whitelisted!").queue();
                break;
            case "ban":
                Player banPlayer = Bukkit.getServer().getPlayer(UUID.fromString(resolvedAction[1]));
                PlayerData banPlayerData = new PlayerData(resolvedAction[1], event.getUser().getId(), "");
                _plugin.database.updatePlayerData(banPlayerData);

                Bukkit.getScheduler().scheduleSyncDelayedTask(_plugin, () -> {
                    banPlayer.kick(null);
                    Bukkit.getServer().banIP(banPlayer.getAddress().getHostName());
                });
                event.reply("`" + banPlayer.getAddress().getHostName() + "` has been banned!").queue();
                break;
            default:
                event.reply("Invalid Button").queue();
                break;
        }

        event.getMessage().delete().queue();
    }
}
