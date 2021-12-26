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

public class Kick {
    private MinecraftUtilities _plugin;

    public Kick(MinecraftUtilities plugin) {
        this._plugin = plugin;
    }

    public void execute(SlashCommandEvent event) {
        String uuid = event.getOption("username").getAsString();
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player != null) {
            Poll poll = this._plugin.polls.createPoll(PollType.KICK, player.getName());
            Message message = generatePollEmbed(poll, player.getName(), false);
            event.reply(message).queue(poll::set_iHook);
        } else {
            event.reply("Player needs to be online!").queue();
        }
    }
}


