package me.imlukas.devnicschatplugin.channels.listeners;

import me.imlukas.devnicschatplugin.DevnicsChatPlugin;
import me.imlukas.devnicschatplugin.channels.data.ChannelCache;
import me.imlukas.devnicschatplugin.channels.DefaultChannels;
import me.imlukas.devnicschatplugin.sql.SQLHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final SQLHandler sqlHandler;
    private final ChannelCache channelCache;

    public PlayerJoinListener(DevnicsChatPlugin main) {
        this.sqlHandler = main.getSqlHandler();
        this.channelCache = main.getChannelCache();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

       if (sqlHandler.playerExists(playerUUID).join()) {
           sqlHandler.getChannel(playerUUID).thenAccept((uuid) -> {

               UUID channelUUID = UUID.fromString(uuid);
               channelCache.setPlayer(playerUUID, channelUUID);

           });
           return;
       }

       sqlHandler.addPlayer(playerUUID);
       channelCache.setPlayer(playerUUID, DefaultChannels.GLOBAL.channelUUID);

    }


}
