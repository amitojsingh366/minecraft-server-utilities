package net.amitoj.minecraftUtilities.discord.listeners;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.discord.commands.List;
import net.amitoj.minecraftUtilities.discord.commands.Stats;
import net.amitoj.minecraftUtilities.discord.commands.poll.Ban;
import net.amitoj.minecraftUtilities.discord.commands.poll.Kick;
import net.amitoj.minecraftUtilities.discord.commands.poll.Reboot;
import net.amitoj.minecraftUtilities.discord.commands.poll.Whitelist;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class SlashCommandListener extends ListenerAdapter {
    private MinecraftUtilities _plugin;
    private String _guildID = "";

    public SlashCommandListener(MinecraftUtilities plugin, String guildID) {
        this._plugin = plugin;
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
        Bukkit.getLogger().info("Discord Command Used: " + event.getCommandString());
        switch (event.getName()) {
            case "list":
                new List(event);
                break;
            case "stats":
                new Stats(event);
                break;
            case "poll":
                switch (event.getSubcommandName()) {
                    case "kick":
                        new Kick(event, _plugin);
                        break;
                    case "ban":
                        new Ban(event, _plugin);
                        break;
                    case "whitelist":
                        new Whitelist(event, _plugin);
                        break;
                    case "reboot":
                        new Reboot(event, _plugin);
                        break;
                }
                break;
            default:
                event.reply("Coming soon ;)").queue();
                break;
        }

    }
}
