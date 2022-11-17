package me.imlukas.devnicschatplugin.gui;

import me.imlukas.devnicschatplugin.DevnicsChatPlugin;
import me.imlukas.devnicschatplugin.channels.data.ChannelData;
import me.imlukas.devnicschatplugin.utils.menu.MenuItem;
import me.imlukas.devnicschatplugin.utils.menu.PaginableMenu;
import me.imlukas.devnicschatplugin.utils.menu.concurrent.Reference;
import me.imlukas.devnicschatplugin.utils.menu.data.PaginableElement;
import me.imlukas.devnicschatplugin.utils.menu.placeholder.Placeholder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ChannelListMenu {


    public static void init(DevnicsChatPlugin main){

        main.getMenuManager().associateMenuInit("channel-list", (baseMenu) -> baseMenu.setBuildAction((player) -> {
            PaginableMenu menu = (PaginableMenu) baseMenu;

            MenuItem channelItem = menu.getItem("d");
            MenuItem activeItem = menu.getItem("d-active");

            menu.clearElements();

            main.getChannelConfig().getChannels().thenAccept(channels -> {

                for (ChannelData channel : channels) {


                    UUID channelUUID = main.getChannelCache().getChannel(player.getUniqueId());


                    Reference<Boolean> isSelected = new Reference<>(channelUUID.equals(channel.getUUID()));


                    List<Placeholder<Player>> placeholders = new ArrayList<>();
                    placeholders.add(new Placeholder<>("channel-name", channel.getName()));
                    placeholders.add(new Placeholder<>("channel-range", String.valueOf(channel.getRange())));
                    placeholders.add(new Placeholder<>("channel-prefix", channel.getPrefix()));

                    MenuItem item = (isSelected.get() ? activeItem : channelItem).clone();
                    PaginableElement element = new PaginableElement(placeholders, item);
                    
                    Reference<Consumer<Player>> clickAction = new Reference<>(null);
                    clickAction.set((clicker) -> {
                        MenuItem newItem = (isSelected.get() ? channelItem : activeItem).clone();
                        newItem.onClickAction(clickAction.get());

                        if (!isSelected.get()){
                            main.getChannelCache().setPlayer(player.getUniqueId(), channel.getUUID());
                            element.setDisplayItem(newItem);
                            menu.build(clicker);
                        }
                    });

                    item.onClickAction(clickAction.get());

                    menu.addElement(element);
                }

            });
        }));
    }
}
