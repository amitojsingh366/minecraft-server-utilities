package net.amitoj.minecraftUtilities.discord;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.discord.listeners.ButtonListener;
import net.amitoj.minecraftUtilities.discord.listeners.ReadyListener;
import net.amitoj.minecraftUtilities.discord.listeners.SlashCommandListener;
import net.amitoj.minecraftUtilities.discord.listeners.MessageListener;
import net.amitoj.minecraftUtilities.util.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

import static net.amitoj.minecraftUtilities.util.Util.syncCommands;

public class DiscordClient extends ListenerAdapter {
    private Config _config;
    private MinecraftUtilities _plugin;

    public JDA jda;
    MessageListener messageListener;


    public DiscordClient(MinecraftUtilities plugin) {
        this._plugin = plugin;
        this._config = plugin.config;

        if (_config.enabled) {
            JDABuilder builder = JDABuilder.createDefault(_config.discordToken);
            builder.setActivity(Activity.watching("your every move"));

            try {
                messageListener = new MessageListener(_config.channelID);
                builder.addEventListeners(new ReadyListener());
                builder.addEventListeners(new SlashCommandListener(_plugin, _config.guildID));
                builder.addEventListeners(new ButtonListener(plugin));
                builder.addEventListeners(messageListener);
                builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
                builder.setMemberCachePolicy(MemberCachePolicy.ALL);
                builder.setChunkingFilter(ChunkingFilter.ALL);


                jda = builder.build();
                jda.awaitReady();

                syncCommands(jda, _config.guildID);
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public User getUserByTagOrId(String tagOrId) {
        try {
            Member member = jda.getGuildById(_config.guildID).getMemberById(tagOrId);
            if (member != null) return member.getUser();
        } catch (NumberFormatException ignored){}
        return jda.getGuildById(_config.guildID).getMemberByTag(tagOrId).getUser();
    }

    public void shutdown() {
        jda.shutdown();
    }
}
