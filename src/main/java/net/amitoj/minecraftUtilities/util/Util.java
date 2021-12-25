package net.amitoj.minecraftUtilities.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Objects;

public class Util {

    public static void sendWH(JSONObject data, String url) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(data.toJSONString().getBytes());
            }
            con.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendServerStartStopMessage(Config config, String event) {


        JSONObject postData = new JSONObject();
        postData.put("content", "");

        JSONArray embeds = new JSONArray();
        JSONObject embed = new JSONObject();

        if (event.equals("start")) {
            embed.put("title", "Server Started");
            embed.put("description", "The server has started");
            embed.put("color", 65280);
        } else {
            embed.put("title", "Server Shutting Down");
            embed.put("description", "The minecraft server is shutting down");
            embed.put("color", 16711680);
        }

        embeds.add(embed);

        postData.put("embeds", embeds);
        postData.put("username", config.serverName);
        postData.put("avatar_url", config.serverIcon);

        sendWH(postData, config.eventsWebhookUrl);
    }

    public static void syncCommands(JDA jda, String guildID) {
        OptionData usernameOptionData = new OptionData(OptionType.STRING, "username",
                "Username of the player to perform the action on", true);

        if (Bukkit.hasWhitelist()) {
            Collection<? extends OfflinePlayer> whitelistedPlayers = Bukkit.getServer().getWhitelistedPlayers();
            if (!whitelistedPlayers.isEmpty()) {
                for (OfflinePlayer player : whitelistedPlayers) {
                    usernameOptionData.addChoice(Objects.requireNonNull(player.getName()), player.getUniqueId().toString());
                }
            }
        }

        CommandData statsCommand = new CommandData("stats",
                "Get basic stats about your minecraft server");
        jda.getGuildById(guildID)
                .upsertCommand(statsCommand)
                .queue();

        CommandData listCommand = new CommandData("list",
                "List all the online people on your minecraft server");
        jda.getGuildById(guildID)
                .upsertCommand(listCommand)
                .queue();

        CommandData pollCommand = new CommandData("poll",
                "Vote on a certain aspect of the Minecraft Server");

        SubcommandData kickSubCommand = new SubcommandData("kick", "Vote on a player to be kicked");
        kickSubCommand.addOptions(usernameOptionData);
        SubcommandData banSubCommand = new SubcommandData("ban", "Vote on a player to be banned");
        banSubCommand.addOptions(usernameOptionData);
        SubcommandData whitelistSubCommand = new SubcommandData("whitelist", "Vote on a player to be whitelisted")
                .addOption(OptionType.STRING, "username", "The username of the player to be whitelisted", true);

        pollCommand.addSubcommands(kickSubCommand);
        pollCommand.addSubcommands(banSubCommand);
        pollCommand.addSubcommands(whitelistSubCommand);

        jda.getGuildById(guildID)
                .upsertCommand(pollCommand)
                .queue();

        // String offlineId = java.util.UUID.nameUUIDFromBytes( ( "OfflinePlayer:" + getName() ).getBytes( Charsets.UTF_8 ) );
    }

    public static void sendLoginDM(JDA jda, String discordId, Player loggedPlayer) {
        MessageEmbed loginMessageEmbed = new MessageEmbed("",
                "New Login",
                "Someone Just Logged into the server\nwith your account: `" + loggedPlayer.getName() +
                        "`\nFrom the IP: `" + loggedPlayer.getAddress().getHostName() +
                        "`\nIf this was you please click the **green** button below, This will **whitelist** your current IP" +
                        "\nIf this was not you please click the **red** button below, this will **ban** the IP",
                EmbedType.RICH,
                OffsetDateTime.now(),
                Integer.parseInt("FFFF00", 16),
                new MessageEmbed.Thumbnail("https://mc-heads.net/avatar/" + loggedPlayer.getName(), "https://mc-heads.net/avatar/" + loggedPlayer.getName(), 0, 0),
                null,
                null,
                null,
                null,
                null,
                null);

        Button whitelistButton = Button.success("whitelist:" + loggedPlayer.getUniqueId() + ":" + discordId, "Whitelist IP")
                .withEmoji(Emoji.fromUnicode("✅"));
        Button banButton = Button.danger("ban:" + loggedPlayer.getUniqueId() + ":" + discordId, "Ban IP")
                .withEmoji(Emoji.fromUnicode("❎"));

        Message loginMessage = new MessageBuilder()
                .setEmbeds(loginMessageEmbed)
                .setActionRows(ActionRow.of(whitelistButton, banButton))
                .build();
        jda.getUserById(discordId).openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(loginMessage))
                .queue();
    }

    public static void sendAccountChangeDM(Player player, User oldUser, User newUser) {
        MessageEmbed messageEmbed = new MessageEmbed("",
                "Account change request",
                "You have requested to change your discord account from" +
                        "\n`" + oldUser.getAsTag() + "` -> `" + newUser.getAsTag() + "`",
                EmbedType.RICH,
                OffsetDateTime.now(),
                Integer.parseInt("FF0000", 16),
                new MessageEmbed.Thumbnail("https://mc-heads.net/avatar/" + player.getName(), "https://mc-heads.net/avatar/" + player.getName(), 0, 0),
                null,
                null,
                null,
                null,
                null,
                null);

        Button allowButton = Button.success("acchange:allow:" + player.getUniqueId() + ":" + newUser.getId(), "Allow Change")
                .withEmoji(Emoji.fromUnicode("✅"));
        Button denyButton = Button.danger("acchange:deny:" + player.getUniqueId() + ":" + newUser.getId(), "Deny Change")
                .withEmoji(Emoji.fromUnicode("❎"));

        Message message = new MessageBuilder()
                .setEmbeds(messageEmbed)
                .setActionRows(ActionRow.of(allowButton, denyButton))
                .build();

        oldUser.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(message))
                .queue();
    }
}
