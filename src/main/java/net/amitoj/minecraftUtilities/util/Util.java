package net.amitoj.minecraftUtilities.util;

import com.google.common.base.Charsets;
import net.amitoj.minecraftUtilities.structures.Poll;
import net.amitoj.minecraftUtilities.structures.PollType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Util {

    public static void sendWH(JSONObject data, String url) {
        HttpURLConnection con = null;
        try {
            JSONObject allowedMentions = new JSONObject();
            allowedMentions.put("parse", new JSONArray());
            data.put("allowed_mentions", allowedMentions);
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
                "Username of the player to perform the action on", true, true);

        OptionData unbanUsernameOptionData = new OptionData(OptionType.STRING, "username",
                "Username of the player to perform the action on", true);

        SlashCommandData statsCommand = Commands.slash("stats",
                "Get basic stats about your minecraft server");

        jda.getGuildById(guildID)
                .upsertCommand(statsCommand)
                .queue();

        SlashCommandData listCommand = Commands.slash("list",
                "List all the online people on your minecraft server");
        jda.getGuildById(guildID)
                .upsertCommand(listCommand)
                .queue();

        SlashCommandData pollCommand = Commands.slash("poll",
                "Vote on a certain aspect of the Minecraft Server");

        SubcommandData kickSubCommand = new SubcommandData("kick", "Vote on a player to be kicked");
        kickSubCommand.addOptions(usernameOptionData);
        SubcommandData banSubCommand = new SubcommandData("ban", "Vote on a player to be banned");
        banSubCommand.addOptions(usernameOptionData);
        SubcommandData unBanSubCommand = new SubcommandData("unban", "Vote on a player to be unbanned");
        unBanSubCommand.addOptions(unbanUsernameOptionData);
        SubcommandData whitelistSubCommand = new SubcommandData("whitelist", "Vote on a player to be whitelisted")
                .addOption(OptionType.STRING, "username", "The username of the player to be whitelisted", true);
        SubcommandData rebootSubCommand = new SubcommandData("reboot", "Vote on the server to be rebooted");

        pollCommand.addSubcommands(kickSubCommand);
        pollCommand.addSubcommands(banSubCommand);
        pollCommand.addSubcommands(unBanSubCommand);
        pollCommand.addSubcommands(whitelistSubCommand);
        pollCommand.addSubcommands(rebootSubCommand);

        jda.getGuildById(guildID)
                .upsertCommand(pollCommand)
                .queue();
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

        MessageCreateData loginMessage = new MessageCreateBuilder()
                .setEmbeds(loginMessageEmbed)
                .addActionRow(whitelistButton, banButton)
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

        MessageCreateData message = new MessageCreateBuilder()
                .setEmbeds(messageEmbed)
                .addActionRow(allowButton, denyButton)
                .build();

        oldUser.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(message))
                .queue();
    }

    public static MessageEditData generatePollEmbed(Poll poll, String username, Boolean disabled) {
        String action = poll.type == PollType.KICK ? "Kick"
                : poll.type == PollType.BAN ? "Ban" : poll.type == PollType.UNBAN ? "Unban" : "Whitelist";

        MessageEmbed messageEmbed = new MessageEmbed("",
                poll.type == PollType.REBOOT ? "Reboot Poll" : (action + " User Poll"),
                poll.type == PollType.REBOOT ? "Should the server be rebooted?" : ("Should `" + username + "` be " + action.toLowerCase() +
                        ((poll.type == PollType.BAN || poll.type == PollType.UNBAN) ? "ned?" : "ed?")),
                EmbedType.RICH,
                OffsetDateTime.now(),
                poll.type == PollType.REBOOT ? Integer.parseInt("FF0000", 16) : Integer.parseInt("CF9FFF", 16),
                poll.type == PollType.REBOOT ? null :
                        new MessageEmbed.Thumbnail("https://mc-heads.net/avatar/" + username, "https://mc-heads.net/avatar/" + username, 0, 0),
                null,
                null,
                null,
                (poll.type == PollType.KICK || poll.type == PollType.WHITELIST || poll.type == PollType.REBOOT) ?
                        new MessageEmbed.Footer("Online player's votes count 2x", null, null) : null,
                null,
                null);

        Button allowButton = Button.secondary("poll:upvote:" + poll.uuid.toString(), poll.upVotes.toString())
                .withEmoji(Emoji.fromUnicode("\uD83D\uDD3C")).withDisabled(disabled)
                .withStyle(disabled && poll.upVotes > poll.downVotes ? ButtonStyle.SUCCESS : ButtonStyle.SECONDARY);
        Button denyButton = Button.secondary("poll:downvote:" + poll.uuid.toString(), poll.downVotes.toString())
                .withEmoji(Emoji.fromUnicode("\uD83D\uDD3D")).withDisabled(disabled)
                .withStyle(disabled && poll.downVotes > poll.upVotes ? ButtonStyle.SUCCESS : ButtonStyle.SECONDARY);

        MessageEditData message = new MessageEditBuilder()
                .setEmbeds(messageEmbed)
                .setActionRow(allowButton, denyButton)
                .build();

        return message;
    }

    public static UUID generateOfflineId(String username){
        return UUID.nameUUIDFromBytes( ( "OfflinePlayer:" + username ).getBytes( Charsets.UTF_8 ) );
    }

    public static void addToWhitelist(UUID uuid, String username){
        String whitelistPath = Bukkit.getServer().getWorldContainer().getPath() + "/whitelist.json";
        JSONArray whitelist = new JSONArray();
        FileReader reader = null;
        try {
            reader = new FileReader(whitelistPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser();
        try {
            whitelist = (JSONArray) jsonParser.parse(reader);
            reader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        JSONObject newPlayer = new JSONObject();
        newPlayer.put("uuid", uuid.toString());
        newPlayer.put("name", username);

        whitelist.add(newPlayer);

        File file = new File(whitelistPath);
        if (file.delete()) {
            if (!Files.exists(Paths.get(whitelistPath))) {
                try {
                    Files.write(Paths.get(whitelistPath), whitelist.toJSONString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


