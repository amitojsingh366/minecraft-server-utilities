package net.amitoj.minecraftUtilities.commands;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.util.Config;
import net.amitoj.minecraftUtilities.util.PlayerData;
import net.amitoj.minecraftUtilities.util.Updater;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDiscord implements CommandExecutor {
    private MinecraftUtilities _plugin;

    public CommandDiscord(MinecraftUtilities plugin) {
        this._plugin = plugin;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        _plugin.getLogger().info("tag: " + args[0]);
        User discordUser = _plugin.discordClient.getUser(args[0]);
        if (discordUser != null) {
            Player player = Bukkit.getPlayer(sender.getName());
            PlayerData playerData = new PlayerData(player.getUniqueId().toString(),
                    discordUser.getId(),
                    player.getAddress().getHostName());
            _plugin.database.updatePlayerData(playerData);
            sender.sendMessage("Discord tag saved! Login notifications will now be sent to ", discordUser.getAsTag());
        } else {
            sender.sendMessage("Invalid discord tag! Make sure you provide the full tag (Amitoj#0001)");
        }
        return true;
    }

}
