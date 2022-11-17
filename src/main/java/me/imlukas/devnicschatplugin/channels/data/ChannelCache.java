package me.imlukas.devnicschatplugin.channels.data;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ChannelCache {

    private final Map<UUID, UUID> playerChannelCache = new HashMap<>();

    /**
     * Sets the channel for a player
     * @param playerUUID The player's UUID
     * @param channelUUID The channel's UUID
     */
    public void setPlayer(UUID playerUUID, UUID channelUUID){
        playerChannelCache.put(playerUUID, channelUUID);
    }

    /**
     * Removec a player from the cache
     * @param playerUUID The player's UUID
     */
    public void removePlayer(UUID playerUUID){
        playerChannelCache.remove(playerUUID);
    }


    /**
     * Gets the channel for a player
     * @param playerUUID The player's UUID
     * @return the UUID of the channel the player's currently in
     */
    public UUID getChannel(UUID playerUUID) {
        return playerChannelCache.get(playerUUID);
    }

    /**
     * Checks if a player is in a channel
     * @param playerUUID The player's UUID
     * @return true if the player is in a channel
     */
    public boolean hasChannel(UUID playerUUID){
        return playerChannelCache.containsKey(playerUUID);
    }






}
