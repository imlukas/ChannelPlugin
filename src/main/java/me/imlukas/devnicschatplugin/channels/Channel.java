package me.imlukas.devnicschatplugin.channels;

import lombok.Getter;
import me.imlukas.devnicschatplugin.ChannelsPlugin;
import me.imlukas.devnicschatplugin.channels.config.ChannelConfig;
import me.imlukas.devnicschatplugin.channels.data.ChannelData;

import java.util.List;
import java.util.UUID;

@Getter
public class Channel {

    private final ChannelConfig channelConfig;
    private final UUID channelID;
    private String channelName, channelPrefix;
    private int range;
    private List<String> worlds;

    public Channel(ChannelsPlugin main) {
        this.channelID = UUID.randomUUID();
        this.channelConfig = main.getChannelConfig();
    }


    public Channel name(String channelName){
        this.channelName = channelName;
        return this;
    }

    public Channel prefix(String channelPrefix){
        this.channelPrefix = channelPrefix;
        return this;
    }

    public Channel range(int distance){
        this.range = distance;
        return this;
    }

    public Channel worlds(List<String> worldsList){
        this.worlds = worldsList;
        return this;
    }

    public void create(){
        channelConfig.addChannel(this);
    }

    public ChannelData getChannelData(){
        return channelConfig.getChannelData(channelID);
    }
}
