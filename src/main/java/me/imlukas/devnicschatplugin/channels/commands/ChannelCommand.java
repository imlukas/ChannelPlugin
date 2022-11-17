package me.imlukas.devnicschatplugin.channels.commands;

import me.imlukas.devnicschatplugin.ChannelsPlugin;
import me.imlukas.devnicschatplugin.channels.Channel;
import me.imlukas.devnicschatplugin.channels.DefaultChannels;
import me.imlukas.devnicschatplugin.channels.config.ChannelConfig;
import me.imlukas.devnicschatplugin.channels.data.ChannelCache;
import me.imlukas.devnicschatplugin.channels.data.ChannelData;
import me.imlukas.devnicschatplugin.sql.SQLHandler;
import me.imlukas.devnicschatplugin.utils.TextUtil;
import me.imlukas.devnicschatplugin.utils.storage.MessagesFile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class ChannelCommand implements CommandExecutor, TabCompleter {

    private final static List<String> SUB_COMMANDS = List.of("delete", "create", "uuid", "reset", "set", "join");
    private final ChannelsPlugin main;
    private final SQLHandler sqlHandler;
    private final ChannelConfig channelConfig;
    private final ChannelCache channelCache;
    private final MessagesFile messages;

    public ChannelCommand(ChannelsPlugin main) {
        this.main = main;
        this.sqlHandler = main.getSqlHandler();
        this.channelConfig = main.getChannelConfig();
        this.channelCache = main.getChannelCache();
        this.messages = main.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            main.getMenuManager().getGUI("channel-list").open(player);
            return true;
        }

        if (!(player.hasPermission("channelplugin.admin"))) {
            messages.sendMessage(player, "global.no-permission");
            return true;
        }
        if (args[0].equalsIgnoreCase("uuid")) {
            UUID channelUUID = channelCache.getChannel(player.getUniqueId());

            TextComponent text = new TextComponent(TextUtil.color("&7Channel UUID: " + "&e" + channelUUID));
            TextComponent click = new TextComponent(TextUtil.color(" &7[Click to copy]"));
            click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to copy").create()));
            click.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, channelUUID.toString()));

            player.spigot().sendMessage(text, click);
            return true;
        }

        if (args.length < 2) {
            messages.sendMessage(player, "global.invalid-args");
            return true;
        }

        switch (args[0]) {
            case "create" -> {
                createChannel(player, args);
                return true;
            }
            case "delete" -> {
                deleteChannel(player, args[1]);
                return true;
            }
            case "join" -> {
                joinChannel(player, args[1]);
                return true;
            }
            case "reset" -> {
                resetChannel(player);
                return true;
            }
        }

        if (args.length < 3) {
            messages.sendMessage(player, "global.invalid-args");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            messages.sendMessage(player, "global.player-not-found", (message) -> message.replace("%player%", args[1]));
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 3) {
                setChannel(target, args[2]);
                return true;
            }
            messages.sendStringMessage(player, "&c&l[Error]&7 Usage: /channel set <channel-uuid> <player>");
            return true;
        }
        return true;
    }

    private void resetChannel(Player player){
        channelCache.setPlayer(player.getUniqueId(), DefaultChannels.GLOBAL.channelUUID);
        messages.sendMessage(player, "channel.reset");
    }

    private void joinChannel(Player player, String UUID) {
        ChannelData channelData = getChannelData(player, UUID);

        if (channelData == null) {
            messages.sendMessage(player, "global.channel-not-found", (message) -> message.replace("%channel%", "" + UUID));
            return;
        }

        String channelName = channelData.getName();
        UUID channelUUID = channelData.getUUID();

        channelCache.setPlayer(player.getUniqueId(), channelUUID);

        messages.sendMessage(player, "channel.join", (message) -> message.replace("%channel%", channelName)
                .replace("%player%", player.getName()));

    }

    private void setChannel(Player player, String uuid) {
        ChannelData channelData = getChannelData(player, uuid);

        if (channelData == null) {
            messages.sendMessage(player, "global.channel-not-found", (message) -> message.replace("%channel%", uuid));
            return;
        }

        String channelName = channelData.getName();
        UUID channelUUID = channelData.getUUID();

        channelCache.setPlayer(player.getUniqueId(), channelUUID);

        messages.sendMessage(player, "channel.set", (message) -> message.replace("%channel%", channelName)
                .replace("%player%", player.getName()));
    }

    private void deleteChannel(Player player, String uuid) {
        ChannelData channelData = getChannelData(player, uuid);

        if (channelData == null) {
            return;
        }

        String channelName = channelData.getName();
        UUID channelUUID = channelData.getUUID();

        if (channelUUID.equals(DefaultChannels.GLOBAL.channelUUID)) {
            messages.sendStringMessage(player, "&c&l[Error]&7 You can't delete the global channel!");
            return;
        }
        channelConfig.deleteChannel(channelUUID);
        sqlHandler.resetPlayers(channelUUID);
        channelCache.resetPlayers(channelUUID);

        messages.sendMessage(player, "channel.delete", (message) -> message.replace("%channel%", channelName)
                .replace("%player%", player.getName()));

    }


    private void createChannel(Player player, String[] args) {
        Channel channel = new Channel(main);

        int range = 0;
        if (args.length > 3) {
            range = parse(args[3]);

            if (range < 0) {
                messages.sendStringMessage(player, "&c&l[Error]&7 Range must be bigger than 0 or equals to 0 (Unlimited range)");
                return;
            }
        }

        switch (args.length) {
            case 2 -> channel.name(args[1])
                    .prefix("&7[&e" + args[1] + "&7]&r")
                    .create();

            case 3 -> channel.name(args[1])
                    .prefix(args[2])
                    .create();

            case 4 -> channel.name(args[1])
                    .prefix(args[2])
                    .range(range)
                    .create();

            case 5 -> {
                List<String> worlds = new ArrayList<>(Arrays.asList(args).subList(4, args.length));

                channel.name(args[1])
                        .prefix(args[2])
                        .range(range)
                        .worlds(worlds)
                        .create();
            }
        }
        messages.sendMessage(player, "channel.create", (message) -> message.replace("%channel%", args[1]));
    }

    private ChannelData getChannelData(Player player, String UUID) {

        UUID channelUUID = parseUUID(UUID);

        if (channelUUID == null) {
            messages.sendStringMessage(player, "&c&l[Error]&7 Invalid UUID");
            return null;
        }

        return channelConfig.getChannelData(channelUUID);
    }

    private UUID parseUUID(String string) {
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {

            case 1 -> StringUtil.copyPartialMatches(args[0], SUB_COMMANDS, completions);
            case 2 -> {
                if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("join")) {
                    for (ChannelData channelData : channelConfig.getChannels().join()) {
                        completions.add("" + channelData.getUUID() + " - " + channelData.getName());
                    }
                }
                if (args[0].equalsIgnoreCase("set")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }

                }
                if (args[0].equalsIgnoreCase("create")) {
                    completions.add("<name>");
                }
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("set")) {
                    for (ChannelData channelData : channelConfig.getChannels().join()) {
                        completions.add("" + channelData.getUUID() + " - " + channelData.getName());
                    }
                }
                if (args[0].equalsIgnoreCase("create")) {
                    completions.add("<prefix>");
                }
            }
            case 4 -> {
                if (args[0].equalsIgnoreCase("create")) {
                    completions.add("<range>");
                }
            }
            case 5 -> {
                if (args[0].equalsIgnoreCase("create")) {
                    for (World world : Bukkit.getWorlds()) {
                        completions.add(world.getName());
                    }
                }
            }
        }
        return completions;
    }
}
