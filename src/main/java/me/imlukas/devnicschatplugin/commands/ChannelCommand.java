package me.imlukas.devnicschatplugin.commands;

import me.imlukas.devnicschatplugin.DevnicsChatPlugin;
import me.imlukas.devnicschatplugin.channels.impl.Channel;
import me.imlukas.devnicschatplugin.channels.config.ChannelConfig;
import me.imlukas.devnicschatplugin.channels.impl.ChannelData;
import me.imlukas.devnicschatplugin.sql.SQLHandler;
import me.imlukas.devnicschatplugin.utils.storage.MessagesFile;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class ChannelCommand implements CommandExecutor {

    private final DevnicsChatPlugin main;
    private final SQLHandler sqlHandler;
    private final ChannelConfig channelConfig;
    private final MessagesFile messages;

    public ChannelCommand(DevnicsChatPlugin main) {
        this.main = main;
        this.sqlHandler = main.getSqlHandler();
        this.channelConfig = main.getChannelConfig();
        this.messages = main.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            main.getMenuManager().getGUI("chat-list").open(player);
            return true;
        }

        if (!(player.hasPermission("chatmanager.admin"))) {
            messages.sendMessage(player, "global.no-permission");
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            createChannel(player, args);
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length == 2) {
                deleteChannel(player, args[1]);
                return true;
            }
            messages.sendStringMessage(player, "&c&l[Error]&7 Usage: /chat delete <channel-uuid>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null){
            messages.sendMessage(player, "global.player-not-found");
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 3) {
                setChannel(target, args[2]);
                return true;
            }
            messages.sendStringMessage(player, "&c&l[Error]&7 Usage: /chat set <channel-uuid> <player>");
            return true;
        }
        return true;
    }

    private void setChannel(Player player, String uuid){
        UUID channelUUID = parseUUID(uuid);

        if (channelUUID == null){
            messages.sendStringMessage(player, "&c&l[Error]&7 Invalid UUID");
            return;
        }
        ChannelData channelData = channelConfig.getChannelData(channelUUID);

        if (channelData == null){
            messages.sendMessage(player, "global.channel-not-found", (message) -> message.replace("%channel%", uuid));
            return;
        }

        sqlHandler.setChannel(player.getUniqueId(), channelUUID);
    }

    private void deleteChannel(Player player, String uuid) {
        UUID channelUUID = parseUUID(uuid);

        if (channelUUID == null){
            messages.sendStringMessage(player, "&c&l[Error]&7 Invalid UUID");
            return;
        }

        channelConfig.deleteChannel(channelUUID);
        messages.sendMessage(player, "chat.delete-success");

    }


    private void createChannel(Player player, String[] args) {
        Channel channel = new Channel(main);

        if (args.length == 3) {
            channel.name(args[1])
                    .prefix(args[2])
                    .create();
        }
        if (args.length == 2) {
            channel.name(args[1])
                    .create();
        }

        int range = parse(args[3]);

        if (range <= 0) {
            messages.sendStringMessage(player, "&c&l[Error]&7 Range must be bigger than 0");
            return;
        }

        if (args.length == 4) {
            channel.name(args[1])
                    .prefix(args[2])
                    .range(range)
                    .create();
        }

        Set<World> worlds = new HashSet<>();
        for (int i = 5; i < args.length; i++) {
            worlds.add(Bukkit.getWorld(args[i]));
        }
        channel.name(args[1])
                .prefix(args[2])
                .range(range)
                .worlds(worlds)
                .create();
    }

    private UUID parseUUID(String string){
        try {
            return UUID.fromString(string);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public int parse(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
