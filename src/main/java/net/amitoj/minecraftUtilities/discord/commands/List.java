package net.amitoj.minecraftUtilities.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class List {
    public List() {}

    public void execute(SlashCommandInteractionEvent event) {
        StringBuilder onlinePlayerNames = new StringBuilder();
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();

        if (onlinePlayers.isEmpty()) {
            event.reply("No one is online at the moment").setEphemeral(true).queue();
            return;
        }

        for (Player player : onlinePlayers) {
            onlinePlayerNames.append(player.getName()).append(", ");
        }

        event.reply("Players online: " + onlinePlayerNames.substring(0, onlinePlayerNames.length() - 2))
                .queue();
    }
}
