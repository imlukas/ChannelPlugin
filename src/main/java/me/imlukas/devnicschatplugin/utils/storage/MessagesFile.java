package me.imlukas.devnicschatplugin.utils.storage;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.text.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesFile extends YMLBase {

    @Getter
    private final String prefix, arrow;
    @Getter
    private boolean usePrefixConfig, useActionBar;
    private String msg;
    private final Pattern pattern;

    public MessagesFile(JavaPlugin plugin) {
        super(plugin, new File(plugin.getDataFolder(), "messages.yml"), true);
        pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        prefix = getConfiguration().getString("messages.prefix");
        arrow = getConfiguration().getString("messages.arrow");
        usePrefixConfig = getConfiguration().getBoolean("messages.use-prefix");
        useActionBar = getConfiguration().getBoolean("messages.actionbar.enabled");

    }

    public String setColor(String message) {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        int minorVer = Integer.parseInt(split[1]);

        if (minorVer >= 16) {
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String setMessage(String name, boolean usePrefix) {
        return setMessage(name, usePrefix, (s) -> s);
    }

    private String setMessage(String name, boolean usePrefix, Function<String, String> action) {
        if (!getConfiguration().contains("messages." + name))
            return "";
        msg = "";
        if (usePrefixConfig && usePrefix) {
            msg = prefix + " " + arrow + " " + getMessage(name);
        } else {
            msg = getMessage(name).replace("%prefix%", prefix);
        }
        msg = action.apply(msg);
        return setColor(msg);
    }

    public void sendStringMessage(CommandSender player, String msg) {
        player.sendMessage(setColor(msg));
    }

    public void sendMessage(CommandSender player, String name) {
        sendMessage(player, name, true, (s) -> s);
    }
    public void sendMessage(CommandSender player, String name, boolean usePrefix) {
        sendMessage(player, name, usePrefix, (s) -> s);
    }
    public void sendMessage(CommandSender player, String name, Function<String, String> action) {
        sendMessage(player, name, true, action);
    }
    public void sendMessage(CommandSender player, String name, boolean usePrefix, Function<String, String> action) {

        if (useActionBar && player instanceof Player) {
            sendActionBarMessage((Player) player, name, action);
            return;
        }

        if (getConfiguration().isList("messages." + name)) {
            for (String str : getConfiguration().getStringList("messages." + name)) {
                msg = str.replace("%prefix%", prefix);
                msg = action.apply(msg);
                player.sendMessage(setColor(msg));
            }
            return;
        }

        msg = setMessage(name, usePrefix, action);
        player.sendMessage(msg);
    }

    public void sendActionBarMessage(Player player, String key) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(setMessage(key, true)));
    }

    public void sendActionBarMessage(Player player, String key, Function<String, String> action) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(setMessage(key, true, action)));
    }

    public String getMessage(String name) {
        return getConfiguration().getString("messages." + name);
    }

    public boolean togglePrefix() {
        boolean isEnabled = usePrefixConfig;
        if (isEnabled) {
            getConfiguration().set("messages.use-prefix", false);
        } else {
            getConfiguration().set("messages.use-prefix", true);
        }
        save();
        usePrefixConfig = getConfiguration().getBoolean("messages.use-prefix");
        return !isEnabled;
    }

    public boolean toggleActionBar() {
        boolean isEnabled = useActionBar;
        if (isEnabled) {
            getConfiguration().set("messages.actionbar.enabled", false);
        } else {
            getConfiguration().set("messages.actionbar.enabled", true);
        }
        save();
        useActionBar = getConfiguration().getBoolean("messages.actionbar.enabled");
        return !isEnabled;
    }
}

