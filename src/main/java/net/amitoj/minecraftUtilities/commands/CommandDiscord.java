package net.amitoj.minecraftUtilities.commands;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.structures.PlayerData;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static net.amitoj.minecraftUtilities.util.Util.sendAccountChangeDM;

public class CommandDiscord implements CommandExecutor {
    private MinecraftUtilities _plugin;

    public CommandDiscord(MinecraftUtilities plugin) {
        this._plugin = plugin;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        _plugin.getLogger().info("tag: " + args[0]);
        User newDiscordUser = _plugin.discordClient.getUserByTagOrId(args[0]);
        if (newDiscordUser != null) {
            Player player = Bukkit.getPlayer(sender.getName());
            PlayerData oldPlayerData = _plugin.database.getPlayerData(player);
            if (!Objects.equals(oldPlayerData.discordId, "")) {
                User oldDiscordUser = _plugin.discordClient.getUserByTagOrId(oldPlayerData.discordId);
                sendAccountChangeDM(player, oldDiscordUser, newDiscordUser);
                sender.sendMessage("Please check your DM for additional verification");
            } else {
                PlayerData newPlayerData = new PlayerData(player.getUniqueId().toString(),
                        newDiscordUser.getId(),
                        player.getAddress().getHostName());
                _plugin.database.updatePlayerData(newPlayerData);
                sender.sendMessage("Discord tag saved! Login notifications will now be sent to ", newDiscordUser.getAsTag());
            }
        } else {
            sender.sendMessage("Invalid discord tag! Make sure you provide the full tag (Amitoj#0001)");
        }
        return true;
    }

}
