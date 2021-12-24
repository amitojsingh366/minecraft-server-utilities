package net.amitoj.minecraftUtilities.commands;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import net.amitoj.minecraftUtilities.util.Config;
import net.amitoj.minecraftUtilities.util.Updater;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandMinecraftDiscordChat implements CommandExecutor {
    Config _config;
    Updater _updater;

    public CommandMinecraftDiscordChat(MinecraftUtilities plugin) {
        this._config = plugin.config;
        this._updater = plugin.updater;
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
            case "update":
                _updater.checkForUpdates();
                _updater.tryUpdating();
                sender.sendMessage("Checking for updates...");
                return true;
            default:
                return false;
        }
    }
}
