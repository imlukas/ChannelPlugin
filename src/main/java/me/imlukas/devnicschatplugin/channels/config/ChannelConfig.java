package me.imlukas.devnicschatplugin.channels.config;

import me.imlukas.devnicschatplugin.ChannelsPlugin;
import me.imlukas.devnicschatplugin.channels.Channel;
import me.imlukas.devnicschatplugin.channels.data.ChannelData;
import me.imlukas.devnicschatplugin.sql.SQLHandler;
import me.imlukas.devnicschatplugin.utils.storage.MessagesFile;
import me.imlukas.devnicschatplugin.utils.storage.YMLBase;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ChannelConfig extends YMLBase {

    private final MessagesFile messages;
    private final SQLHandler sqlHandler;

    public ChannelConfig(ChannelsPlugin main) {
        super(main, "channels.yml");
        this.messages = main.getMessages();
        this.sqlHandler = main.getSqlHandler();
    }

    /**
     * Gets the channel data from the channel UUID
     * @param channelUUID The channel UUID
     * @return the channel's data or null if the channel doesn't exist
     */
    public ChannelData getChannelData(UUID channelUUID){

        ConfigurationSection section = getConfiguration().getConfigurationSection("channels." + channelUUID.toString());

        if (section == null){
            return null;
        }

        List<String> worlds = new ArrayList<>(getConfiguration().getStringList("channels." + channelUUID + ".worlds"));

        return new ChannelData(
                channelUUID,
                section.getString("name"),
                section.getString("prefix", ""),
                section.getInt("range", 0),
                worlds
        );
    }


    /**
     * Add a channel to the config
     * @param channelData channel data of the channel.
     */
    public void addChannel(Channel channelData){
        getConfiguration().set("channels." + channelData.getChannelID() + ".name", channelData.getChannelName());

        if (channelData.getChannelPrefix() != null){
            getConfiguration().set("channels." + channelData.getChannelID() + ".prefix", channelData.getChannelPrefix());
        }
        if (channelData.getRange() != 0){
            getConfiguration().set("channels." + channelData.getChannelID() + ".range", channelData.getRange());
        }
        if (channelData.getWorlds() != null){
            getConfiguration().set("channels." + channelData.getChannelID() + ".worlds", channelData.getWorlds());
        }

        save();

    }

    /**
     * Remove a channel from the config
     * @param channelUUID channel uuid of the channel.
     */
    public void deleteChannel(UUID channelUUID){
        getConfiguration().set("channels." + channelUUID, null);
        save();
    }

    /**
     * Get all channels from the config
     * @return list of all channels
     */
    public CompletableFuture<LinkedList<ChannelData>> getChannels() {
        return CompletableFuture.supplyAsync(() -> {
            LinkedList<ChannelData> channels = new LinkedList<>();
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
