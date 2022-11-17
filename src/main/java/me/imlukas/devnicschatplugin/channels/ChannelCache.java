package me.imlukas.devnicschatplugin.channels;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ChannelCache {

    private final Map<UUID, UUID> playerChannelCache = new HashMap<>();

    public void addPlayer(UUID playerUUID, UUID channelUUID){
        playerChannelCache.put(playerUUID, channelUUID);
    }

    public void removePlayer(UUID playerUUID){
        playerChannelCache.remove(playerUUID);
    }

    public UUID getChannel(UUID playerUUID) {
        return playerChannelCache.get(playerUUID);
    }

    public boolean hasChannel(UUID playerUUID){
        return playerChannelCache.containsKey(playerUUID);
    }






}
