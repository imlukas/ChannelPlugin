package me.imlukas.devnicschatplugin.channels.data;

import java.util.List;
import java.util.UUID;

public class ChannelData {

    private final UUID channelUUID;
    private final String name, prefix;
    private final int range;
    private final List<String> worlds;

    public ChannelData(UUID channelUUID, String channelName, String channelPrefix, int range, List<String> worlds) {
        this.channelUUID = channelUUID;
        this.name = channelName;
        this.prefix = channelPrefix;
        this.range = range;
        this.worlds = worlds;
    }

    /**
     * Gets the channel UUID
     * @return the channel UUID
     */
    public UUID getUUID() {
        return channelUUID;
    }

    /**
     * Gets the channel name
     * @return the channel name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the channel prefix
     * @return the channel prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets the channel range
     * @return the channel range
     */
    public int getRange() {
        return range;
    }

    /**
     * Gets the channel worlds
     * @return the channel worlds
     */
    public List<String> getWorlds() {
        return worlds;
    }
}
