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

        System.out.println("Opening menu " + menu);
        lastClosedMenu = peekLast();

        System.out.println("Last closed menu: " + lastClosedMenu);

        chain(menu);
        menu.open(player);
    }

    public void goBack(Player player) {
        Menu previous = unchain();

        if (previous == null) {
            System.out.println("No previous menu");
            return;
        }

        MenuMetadata metadata = previous.getMetadata();
        metadata.wipeTransient();

        previous = previous.getMenuManager().getGUI(previous.getOriginalName());
        previous.getMetadata().copyFrom(metadata);

        System.out.println("Going back to " + previous.getOriginalName());
        previous.build(player); // update internals
        previous.open(player);

    }
}
