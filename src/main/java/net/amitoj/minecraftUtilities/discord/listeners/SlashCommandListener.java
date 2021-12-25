package net.amitoj.minecraftUtilities.discord.listeners;

import net.amitoj.minecraftUtilities.discord.commands.List;
import net.amitoj.minecraftUtilities.discord.commands.Stats;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class SlashCommandListener extends ListenerAdapter {
    private String _guildID = "";

    public SlashCommandListener(String guildID) {
        this._guildID = guildID;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getGuild() == null) {
            event.reply("This command cannot be run in this server").queue();
            return;
        }
        if (!event.getGuild().getId().equals(_guildID)) {
            event.reply("This command cannot be run in this server").queue();
            return;
        }
        switch (event.getName()) {
            case "list":
                new List(event);
                break;
            case "stats":
                new Stats(event);
                break;
            default:
                Bukkit.getLogger().info("Discord Command Used: " + event.getName());
                event.reply("Coming soon ;)").queue();
                break;
        }

    }
}
