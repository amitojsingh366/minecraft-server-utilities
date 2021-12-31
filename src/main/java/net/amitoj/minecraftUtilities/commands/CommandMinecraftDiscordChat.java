package net.amitoj.minecraftUtilities.commands;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.util.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandMinecraftDiscordChat implements CommandExecutor {
    Config _config;

    public CommandMinecraftDiscordChat(MinecraftUtilities plugin) {
        this._config = plugin.config;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args[0]) {
            case "on":
            case "enable":
                _config.setEnabled(true);
                sender.sendMessage("Disabled Minecraft Discord Chat, you might need to restart your server once to apply changes");
                return true;
            case "off":
            case "disable":
                _config.setEnabled(false);
                sender.sendMessage("Enabled Minecraft Discord Chat, you might need to restart your server once to apply changes");
                return true;
            default:
                return false;
        }
    }
}
