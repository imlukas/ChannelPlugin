package me.imlukas.devnicschatplugin.listeners;

import me.imlukas.devnicschatplugin.ChannelsPlugin;
import me.imlukas.devnicschatplugin.channels.data.ChannelCache;
import me.imlukas.devnicschatplugin.channels.config.ChannelConfig;
import me.imlukas.devnicschatplugin.channels.data.ChannelData;
import me.imlukas.devnicschatplugin.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AsyncPlayerChatListener implements Listener {

    private final ChannelsPlugin main;
    private final ChannelConfig channelConfig;
    private final ChannelCache channelCache;

    public AsyncPlayerChatListener(ChannelsPlugin main) {
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

        List<String> sWorlds = channelData.getWorlds();

        if (!(channelData.getWorlds().isEmpty())) {
            List<World> worlds = new ArrayList<>();

            for (String world : sWorlds){
                worlds.add(Bukkit.getWorld(world));
            }

            event.getRecipients().removeIf((recipient) ->
                    !worlds.contains(recipient.getWorld()));
        }

        event.setFormat(TextUtil.color(channelData.getPrefix() + " " + player.getName() + ": " + message));







    }
}
