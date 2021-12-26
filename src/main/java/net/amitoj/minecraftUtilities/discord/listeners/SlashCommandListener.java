package net.amitoj.minecraftUtilities.discord.listeners;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.discord.commands.List;
import net.amitoj.minecraftUtilities.discord.commands.Stats;
import net.amitoj.minecraftUtilities.discord.commands.poll.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class SlashCommandListener extends ListenerAdapter {
    private MinecraftUtilities _plugin;
    private String _guildID = "";

    public List listCommand;
    public Stats statsCommand;
    public Ban banCommand;
    public Unban unbanCommand;
    public Kick kickCommand;
    public Reboot rebootCommand;
    public Whitelist whitelistCommand;


    public SlashCommandListener(MinecraftUtilities plugin, String guildID) {
        this._plugin = plugin;
        this._guildID = guildID;

        this.listCommand = new List();
        this.statsCommand = new Stats();
        this.banCommand = new Ban(plugin);
        this.unbanCommand = new Unban(plugin);
        this.kickCommand = new Kick(plugin);
        this.rebootCommand = new Reboot(plugin);
        this.whitelistCommand = new Whitelist(plugin);
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
                listCommand.execute(event);
                break;
            case "stats":
                statsCommand.execute(event);
                break;
            case "poll":
                switch (event.getSubcommandName()) {
                    case "kick":
                        kickCommand.execute(event);
                        break;
                    case "ban":
                        banCommand.execute(event);
                        break;
                    case "unban":
                        unbanCommand.execute(event);
                        break;
                    case "whitelist":
                        whitelistCommand.execute(event);
                        break;
                    case "reboot":
                        rebootCommand.execute(event);
                        break;
                }
                break;
            default:
                event.reply("Coming soon ;)").queue();
                break;
        }

    }
}
