package me.imlukas.devnicschatplugin.utils.menu;

import lombok.Getter;
import me.imlukas.devnicschatplugin.utils.menu.data.MenuMetadata;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class MenuChain extends LinkedList<Menu> {

    @Getter
    private Menu lastClosedMenu;

    public void chain(Menu other) {
        add(other);
    }

    public Menu unchain() {
        lastClosedMenu = pollLast(); // remove current
        return peekLast(); // get previous
    }

    public void open(Menu menu, Player player) {

        lastClosedMenu = peekLast();

        chain(menu);
        menu.open(player);
    }

    public void goBack(Player player) {
        Menu previous = unchain();

        if (previous == null) {
            return;
        }

        MenuMetadata metadata = previous.getMetadata();
        metadata.wipeTransient();

        previous = previous.getMenuManager().getGUI(previous.getOriginalName());
        previous.getMetadata().copyFrom(metadata);

        previous.build(player); // update internals
        previous.open(player);

    }
}
