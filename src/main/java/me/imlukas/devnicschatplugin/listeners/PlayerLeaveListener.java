package me.imlukas.devnicschatplugin.listeners;

import me.imlukas.devnicschatplugin.ChannelsPlugin;
import me.imlukas.devnicschatplugin.channels.data.ChannelCache;
import me.imlukas.devnicschatplugin.channels.DefaultChannels;
import me.imlukas.devnicschatplugin.sql.SQLHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerLeaveListener implements Listener {

    private final SQLHandler sqlHandler;
    private final ChannelCache channelCache;

    public PlayerLeaveListener(ChannelsPlugin main) {
        this.sqlHandler = main.getSqlHandler();
        this.channelCache = main.getChannelCache();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        UUID channelUUID = channelCache.getChannel(playerUUID);

        if (channelUUID == null) {
            channelUUID = DefaultChannels.GLOBAL.channelUUID;
        }

        sqlHandler.setChannel(playerUUID, channelUUID);
        channelCache.removePlayer(playerUUID);

    }


}
