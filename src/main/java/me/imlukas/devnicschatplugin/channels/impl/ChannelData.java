package me.imlukas.devnicschatplugin.channels.impl;

import me.imlukas.devnicschatplugin.DevnicsChatPlugin;
import org.bukkit.World;

import java.util.List;
import java.util.UUID;

public class ChannelData {

    private final UUID channelUUID;
    private final String name, prefix;
    private final int range;
    private final List<World> worlds;

    public ChannelData(UUID channelUUID, String channelName, String channelPrefix, int range, List<World> worlds) {
        this.channelUUID = channelUUID;
        this.name = channelName;
        this.prefix = channelPrefix;
        this.range = range;
        this.worlds = worlds;
    }

    public UUID getUUID() {
        return channelUUID;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getRange() {
        return range;
    }

    public List<World> getWorlds() {
        return worlds;
    }
}
