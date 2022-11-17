package me.imlukas.devnicschatplugin.channels.data;

import lombok.Getter;
import me.imlukas.devnicschatplugin.channels.DefaultChannels;

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
     * Remove a player from the cache
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

    /**
     * Reset all the players that are in a certain channel
     * This is to avoid errors when deleting channels
     * @param channelUUID the deleted channel's UUID
     */
    public void resetPlayers(UUID channelUUID) {

        for(Map.Entry<UUID, UUID> entry : playerChannelCache.entrySet()) {
            if (!(entry.getValue().equals(channelUUID))){
                continue;
            }
            setPlayer(entry.getKey(), DefaultChannels.GLOBAL.channelUUID);
        }
    }
}
