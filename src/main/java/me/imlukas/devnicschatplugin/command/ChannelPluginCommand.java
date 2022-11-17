package me.imlukas.devnicschatplugin.command;

import me.imlukas.devnicschatplugin.ChannelsPlugin;
import me.imlukas.devnicschatplugin.utils.storage.MessagesFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChannelPluginCommand implements CommandExecutor {

    private final MessagesFile messages;

    public ChannelPluginCommand(ChannelsPlugin main) {
        this.messages = main.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)){
            return true;
        }
        String action = args[0];
        if (!(player.hasPermission("channelplugin.admin"))){
            messages.sendMessage(player, "global.no-permission");
            return true;
        }
        if (action.equalsIgnoreCase("toggleprefix")) {
            if (messages.togglePrefix()) {
                messages.sendMessage(player, "global.feature-on", (message) -> message.replace("%feature%", "Prefix"));
            } else {
                messages.sendMessage(player, "global.feature-off", (message) -> message.replace("%feature%", "Prefix"));
            }
            return true;
        }
        if (action.equalsIgnoreCase("toggleactionbar")) {
            if (messages.toggleActionBar()) {
                messages.sendMessage(player, "global.feature-on", (message) -> message.replace("%feature%", "ActionBar"));
            } else {
                messages.sendMessage(player, "global.feature-off", (message) -> message.replace("%feature%", "ActionBar"));
            }
            return true;
        }

        return true;
    }
}
