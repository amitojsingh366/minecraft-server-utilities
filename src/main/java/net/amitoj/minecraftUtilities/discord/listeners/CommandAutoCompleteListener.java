package net.amitoj.minecraftUtilities.discord.listeners;

import net.amitoj.minecraftUtilities.util.UsernameOption;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandAutoCompleteListener extends ListenerAdapter {

    public CommandAutoCompleteListener(){
        super();
    }
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getFocusedOption().getName().equals("username")) {
            ArrayList<UsernameOption> usernames = new ArrayList<>();
            if (Objects.equals(event.getSubcommandName(), "unban")) {
                Collection<? extends OfflinePlayer> bannedPlayers = Bukkit.getServer().getBannedPlayers();
                if (!bannedPlayers.isEmpty()) {
                    for (OfflinePlayer player : bannedPlayers) {
                        UsernameOption option = new UsernameOption(Objects.requireNonNull(player.getName()), player.getUniqueId().toString());
                        usernames.add(option);
                    }
                }
            } else {
                if (Bukkit.hasWhitelist()) {
                    Collection<? extends OfflinePlayer> whitelistedPlayers = Bukkit.getServer().getWhitelistedPlayers();
                    if (!whitelistedPlayers.isEmpty()) {
                        for (OfflinePlayer player : whitelistedPlayers) {
                            UsernameOption option = new UsernameOption(Objects.requireNonNull(player.getName()), player.getUniqueId().toString());
                            usernames.add(option);
                        }
                    }
                }
            }
            List<Command.Choice> options = usernames.stream()
                    .filter(user -> user.username.startsWith(event.getFocusedOption().getValue()))
                    .map(user -> new Command.Choice(user.username, user.uuid))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }
}
