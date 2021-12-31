package net.amitoj.minecraftUtilities;

import net.amitoj.minecraftUtilities.commands.CommandCoordinates;
import net.amitoj.minecraftUtilities.commands.CommandDiscord;
import net.amitoj.minecraftUtilities.commands.CommandMinecraftDiscordChat;
import net.amitoj.minecraftUtilities.discord.DiscordClient;
import net.amitoj.minecraftUtilities.listeners.*;
import net.amitoj.minecraftUtilities.structures.PollManager;
import net.amitoj.minecraftUtilities.util.Config;
import net.amitoj.minecraftUtilities.util.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static net.amitoj.minecraftUtilities.util.Util.*;


public final class MinecraftUtilities extends JavaPlugin {
    public DiscordClient discordClient;
    public Config config = new Config(this);
    public Database database = new Database(this);
    public PollManager polls = new PollManager(this);

    @Override
    public void onEnable() {
        ChatListener chatListener = new ChatListener(this);
        PlayerJoinListener playerJoinListener = new PlayerJoinListener(this);
        PlayerQuitListener playerQuitListener = new PlayerQuitListener(this);
        PlayerDeathListener playerDeathListener = new PlayerDeathListener(this);

        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        getServer().getPluginManager().registerEvents(playerQuitListener, this);
        getServer().getPluginManager().registerEvents(playerDeathListener, this);

        sendServerStartStopMessage(config, "start");

        discordClient = new DiscordClient(this);

        this.getCommand("minecraftdiscordchat").setExecutor(new CommandMinecraftDiscordChat(this));
        this.getCommand("coordinates").setExecutor(new CommandCoordinates(this));
        this.getCommand("discord").setExecutor(new CommandDiscord(this));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().isBanned()) {
            syncCommands(this.discordClient.jda, this.config.guildID);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        discordClient.shutdown();
        database.disconnect();
        sendServerStartStopMessage(config, "stop");
    }
}
