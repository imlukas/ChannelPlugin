package me.imlukas.devnicschatplugin.listeners;

import me.imlukas.devnicschatplugin.DevnicsChatPlugin;
import me.imlukas.devnicschatplugin.channels.ChannelCache;
import me.imlukas.devnicschatplugin.channels.config.ChannelConfig;
import me.imlukas.devnicschatplugin.channels.impl.ChannelData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;
import java.util.UUID;

public class AsyncPlayerChatListener implements Listener {

    private final DevnicsChatPlugin main;
    private final ChannelConfig channelConfig;
    private final ChannelCache channelCache;

    public AsyncPlayerChatListener(DevnicsChatPlugin main) {
        this.main = main;
        this.channelConfig = main.getChannelConfig();
        this.channelCache = main.getChannelCache();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String message = event.getMessage();

        UUID channelUUID = channelCache.getChannel(playerUUID);

        if (channelUUID == null) {
            return;
        }

        ChannelData channelData = channelConfig.getChannelData(channelUUID);

        if (channelData.getRange() != 0){
            event.getRecipients().removeIf((recipient) ->
                    recipient.getLocation().distance(player.getLocation()) > channelData.getRange());
        }

        if (!(channelData.getWorlds().isEmpty())) {
            event.getRecipients().removeIf((recipient) ->
                    !channelData.getWorlds().contains(Bukkit.getWorld(recipient.getWorld().getName())));
        }

        event.setFormat(channelData.getPrefix() + " " + player.getName() + ": " + message);







    }
}
