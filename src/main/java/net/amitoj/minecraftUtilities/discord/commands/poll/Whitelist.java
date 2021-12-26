package net.amitoj.minecraftUtilities.discord.commands.poll;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.structures.Poll;
import net.amitoj.minecraftUtilities.structures.PollType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.amitoj.minecraftUtilities.util.Util.generatePollEmbed;

public class Whitelist {
    public Whitelist(SlashCommandEvent event, MinecraftUtilities plugin) {
        String username = event.getOption("username").getAsString();
        Poll poll = plugin.polls.createPoll(PollType.WHITELIST, username);
        Message message = generatePollEmbed(poll, username, false);
        event.reply(message).queue(poll::set_iHook);
    }
}
