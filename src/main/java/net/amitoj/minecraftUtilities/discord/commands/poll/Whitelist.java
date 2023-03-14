package net.amitoj.minecraftUtilities.discord.commands.poll;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.structures.Poll;
import net.amitoj.minecraftUtilities.structures.PollType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.amitoj.minecraftUtilities.util.Util.generatePollEmbed;

public class Whitelist {
    private MinecraftUtilities _plugin;

    public Whitelist(MinecraftUtilities plugin) {
        this._plugin = plugin;
    }

    public void execute(SlashCommandInteractionEvent event) {
        String username = event.getOption("username").getAsString();
        Poll poll = this._plugin.polls.createPoll(PollType.WHITELIST, username);
        MessageEditData message = generatePollEmbed(poll, username, false);
        event.reply(MessageCreateData.fromEditData((message))).queue(poll::set_iHook);
    }
}
