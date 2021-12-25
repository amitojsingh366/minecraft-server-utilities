package net.amitoj.minecraftUtilities.discord.listeners;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.structures.PlayerData;
import net.dv8tion.jda.api.entities.User;
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
                if (whitelistPlayer != null) {
                    PlayerData whitelistPlayerData = new PlayerData(resolvedAction[1], event.getUser().getId(), whitelistPlayer.getAddress().getHostName());
                    _plugin.database.updatePlayerData(whitelistPlayerData);
                    event.reply("`" + whitelistPlayer.getAddress().getHostName() + "` has been whitelisted!").queue();
                } else {
                    event.reply("The player needs to be connected to be whitelisted!").queue();
                }
                break;
            case "ban":
                Player banPlayer = Bukkit.getServer().getPlayer(UUID.fromString(resolvedAction[1]));
                if (banPlayer != null) {
                    PlayerData banPlayerData = new PlayerData(resolvedAction[1], event.getUser().getId(), "");
                    _plugin.database.updatePlayerData(banPlayerData);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(_plugin, () -> {
                        banPlayer.kick(null);
                        Bukkit.getServer().banIP(banPlayer.getAddress().getHostName());
                    });
                    event.reply("`" + banPlayer.getAddress().getHostName() + "` has been banned!").queue();
                } else {
                    event.reply("The player needs to be connected to be banned!").queue();
                }
                break;
            case "acchange":
                if (Objects.equals(resolvedAction[1], "allow")) {
                    Player player = Bukkit.getPlayer(UUID.fromString(resolvedAction[2]));
                    if (player != null) {
                        User newDiscordUser = _plugin.discordClient.getUserByTagOrId(resolvedAction[3]);
                        PlayerData accChangePlayerData = _plugin.database.getPlayerData(player);
                        accChangePlayerData.setDiscordId(newDiscordUser.getId());
                        _plugin.database.updatePlayerData(accChangePlayerData);
                        event.reply("Discord tag saved! Login notifications will now be sent to " + newDiscordUser.getAsTag()).queue();
                    } else {
                        event.reply("You need to be connected to the server to change your account, try again later").queue();
                    }
                } else {
                    event.reply("Successfully denied account change request").queue();
                }
                break;
            default:
                event.reply("Invalid Button").queue();
                break;
        }

        event.getMessage().delete().queue();
    }
}
