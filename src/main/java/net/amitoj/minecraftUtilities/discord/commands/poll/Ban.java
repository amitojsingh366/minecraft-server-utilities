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

public class Ban {
    private MinecraftUtilities _plugin;

    public Ban(MinecraftUtilities plugin) {
        this._plugin = plugin;
    }

    public void execute(SlashCommandInteractionEvent event) {
        String uuid = event.getOption("username").getAsString();
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player != null) {
            Poll poll = this._plugin.polls.createPoll(PollType.BAN, player.getName());
            MessageEditData message = generatePollEmbed(poll, player.getName(), false);
            event.reply(MessageCreateData.fromEditData((message))).queue(poll::set_iHook);
        } else {
            event.reply("Player needs to be online!").queue();
        }
    }
}
