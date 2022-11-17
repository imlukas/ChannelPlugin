package me.imlukas.devnicschatplugin.commands;

import me.imlukas.devnicschatplugin.DevnicsChatPlugin;
import me.imlukas.devnicschatplugin.channels.Channel;
import me.imlukas.devnicschatplugin.channels.DefaultChannels;
import me.imlukas.devnicschatplugin.channels.config.ChannelConfig;
import me.imlukas.devnicschatplugin.channels.data.ChannelCache;
import me.imlukas.devnicschatplugin.channels.data.ChannelData;
import me.imlukas.devnicschatplugin.sql.SQLHandler;
import me.imlukas.devnicschatplugin.utils.TextUtil;
import me.imlukas.devnicschatplugin.utils.storage.MessagesFile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.rmi.server.UID;
import java.util.*;


public class ChannelCommand implements CommandExecutor, TabCompleter {

    private final static List<String> SUB_COMMANDS = List.of("delete", "create", "uuid", "reset", "set", "join");
    private final DevnicsChatPlugin main;
    private final SQLHandler sqlHandler;
    private final ChannelConfig channelConfig;
    private final ChannelCache channelCache;
    private final MessagesFile messages;

    public ChannelCommand(DevnicsChatPlugin main) {
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

        if (!(player.hasPermission("channelmanager.admin"))) {
            messages.sendMessage(player, "global.no-permission");
            return true;
        }
        if (args[0].equalsIgnoreCase("uuid")) {
            UUID channelUUID = channelCache.getChannel(player.getUniqueId());

            TextComponent text = new TextComponent(TextUtil.color("&7Channel UUID: " + "&e" + channelUUID + "&7Click to copy"));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, channelUUID.toString()));

            player.spigot().sendMessage(text);
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
            messages.sendStringMessage(player, "&c&l[Error]&7 Usage: /channel delete <channel-uuid>");
            return true;
        }
        if (args[0].equalsIgnoreCase("join")) {
            if (args.length == 2) {
                joinChannel(player, args[1]);
                return true;
            }
            messages.sendStringMessage(player, "&c&l[Error]&7 Usage: /channel join <channel-uuid>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            channelCache.setPlayer(player.getUniqueId(), DefaultChannels.GLOBAL.channelUUID);
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            messages.sendMessage(player, "global.player-not-found");
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

        channelConfig.deleteChannel(channelUUID);

        if (channelConfig.getConfiguration().getConfigurationSection("channels." + channelUUID) != null) {
            messages.sendMessage(player, "channel.delete-error", (message) -> message.replace("%channel%", channelName)
                    .replace("%player%", player.getName()));
        }
        messages.sendMessage(player, "channel.delete-success", (message) -> message.replace("%channel%", channelName)
                .replace("%player%", player.getName()));

    }


    private void createChannel(Player player, String[] args) {
        Channel channel = new Channel(main);


        if (args.length == 2) {
            channel.name(args[1])
                    .create();
            return;
        }
        if (args.length == 3) {
            channel.name(args[1])
                    .prefix(args[2])
                    .create();
            return;
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
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], SUB_COMMANDS, completions);
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("join")) {
                for (ChannelData channelData : channelConfig.getChannels().join()) {
                    completions.add(channelData.getName());
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

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("create")) {
                completions.add("<prefix>");
            }
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("create")) {
                completions.add("<range>");
            }
        }

        if (args.length >= 5) {
            if (args[0].equalsIgnoreCase("create")) {
                for (World world : Bukkit.getWorlds()) {
                    completions.add(world.getName());
                }
            }
        }
        return completions;
    }
}
