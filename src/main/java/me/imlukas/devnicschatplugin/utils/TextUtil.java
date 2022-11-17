package me.imlukas.devnicschatplugin.utils;

import me.imlukas.devnicschatplugin.ChannelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {


    private final FileConfiguration config;
    private static Pattern pattern;

    public TextUtil(ChannelsPlugin main) {
        this.config = main.getConfig();
        pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String color(String text) {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        int minorVer = Integer.parseInt(split[1]);

        if (minorVer >= 16) {
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                String color = text.substring(matcher.start(), matcher.end());
                text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                matcher = pattern.matcher(text);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getColorConfig(String key) {
        return color(config.getString(key));
    }
}
