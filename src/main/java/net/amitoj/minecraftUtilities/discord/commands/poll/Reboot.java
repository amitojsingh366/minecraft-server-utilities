package net.amitoj.minecraftUtilities.discord.commands.poll;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.structures.Poll;
import net.amitoj.minecraftUtilities.structures.PollType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import static net.amitoj.minecraftUtilities.util.Util.generatePollEmbed;

public class Reboot {
    private MinecraftUtilities _plugin;

    public Reboot(MinecraftUtilities plugin) {
        this._plugin = plugin;
    }

    public void execute(SlashCommandEvent event){
        Poll poll = this._plugin.polls.createPoll(PollType.REBOOT, "");
        Message message = generatePollEmbed(poll, "", false);
        event.reply(message).queue(poll::set_iHook);
    }
}
