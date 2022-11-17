package me.imlukas.devnicschatplugin.channels.config;

import me.imlukas.devnicschatplugin.DevnicsChatPlugin;
import me.imlukas.devnicschatplugin.channels.impl.Channel;
import me.imlukas.devnicschatplugin.channels.impl.ChannelData;
import me.imlukas.devnicschatplugin.sql.SQLHandler;
import me.imlukas.devnicschatplugin.utils.storage.MessagesFile;
import me.imlukas.devnicschatplugin.utils.storage.YMLBase;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ChannelConfig extends YMLBase {

    private final MessagesFile messages;
    private final SQLHandler sqlHandler;

    public ChannelConfig(DevnicsChatPlugin main) {
        super(main, "channels.yml");
        this.messages = main.getMessages();
        this.sqlHandler = main.getSqlHandler();
    }

    public ChannelData getChannelData(UUID channelUUID){

        ConfigurationSection section = getConfiguration().getConfigurationSection("channels." + channelUUID.toString());

        if (section == null){
            return null;
        }
        List<World> worlds = new ArrayList<>();

        for (String world : getConfiguration().getStringList("channels." + channelUUID + ".worlds")) {

            if (world.equalsIgnoreCase("%player_world%")) {
                continue;
            }
            worlds.add(Bukkit.getWorld(world));
        }

        return new ChannelData(
                channelUUID,
                section.getString("name"),
                section.getString("prefix", ""),
                section.getInt("range", 0),
                worlds
        );
    }


    public void addChannel(Channel channelData){
        getConfiguration().set("channels." + channelData.getChannelID() + ".name", channelData.getChannelName());

        if (channelData.getChannelPrefix() != null){
            getConfiguration().set("channels." + channelData.getChannelID() + ".prefix", channelData.getChannelPrefix());
        }
        if (channelData.getDistance() != 0){
            getConfiguration().set("channels." + channelData.getChannelID() + ".distance", channelData.getDistance());
        }
        if (channelData.getWorlds() != null){
            getConfiguration().set("channels." + channelData.getChannelID() + ".worlds", channelData.getWorlds());
        }

        save();

    }

    public void deleteChannel(UUID channelID){
        sqlHandler.resetPlayers(getChannelData(channelID).getName());
        getConfiguration().set("channels." + channelID, null);
        save();
    }

    public CompletableFuture<Set<ChannelData>> getChannels() {
        return CompletableFuture.supplyAsync(() -> {
            Set<ChannelData> channels = new HashSet<>();
            for (String key : getConfiguration().getConfigurationSection("channels").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                ChannelData channelData = getChannelData(uuid);
                if (channelData != null){
                    channels.add(channelData);
                }
            }
            return channels;
        });


    }
}
