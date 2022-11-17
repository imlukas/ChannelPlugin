package me.imlukas.devnicschatplugin.utils.menu;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.imlukas.devnicschatplugin.utils.item.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

@Getter
@EqualsAndHashCode
public class MenuItem implements Cloneable {

    private final String originalCharacter;

    private final ItemStack item;

    private final List<Consumer<InventoryClickEvent>> clickActions = new LinkedList<>();
    private final List<Consumer<Player>> runActions = new LinkedList<>();

    @Setter
    private boolean clickable;

    private final ConfigurationSection originalSection;


    public MenuItem(String originalCharacter, MenuManager manager, ConfigurationSection section) {
        this.originalCharacter = originalCharacter;
        this.originalSection = section;

        this.item = ItemBuilder.fromSection(section);
        if (section.contains("sound")) {
            String sound = section.getString("sound").toUpperCase(Locale.ROOT);
            runActions.add(player -> player.playSound(player.getLocation(), Sound.valueOf(sound), 0.8f, 1));
        }

        if (section.contains("target-gui")) {
            String target = section.getString("target-gui");

            if (target != null && target.equalsIgnoreCase("%PREVIOUS%")) {
                runActions.add((player) -> {
                    manager.getChain(player).goBack(player);
                });
                return;
            }
            runActions.add(
                    (player) -> {
                        Menu menu = manager.getGUI(target);
                        System.out.println("Opening chain menu " + menu);
                        manager.getChain(player).open(menu, player);
                    });
        }
    }

    MenuItem(String originalCharacter, ItemStack item, ConfigurationSection section) {
        this.originalCharacter = originalCharacter;
        this.item = item;
        this.originalSection = section;
    }

    @SneakyThrows
    @Override
    public MenuItem clone() {
        MenuItem clone = new MenuItem(originalCharacter, item.clone(), originalSection);

        clone.clickActions.clear();
        clone.clickActions.addAll(clickActions);

        clone.runActions.clear();
        clone.runActions.addAll(runActions);

        return clone;
    }

    public void onClick(Consumer<InventoryClickEvent> action) {
        clickActions.add(action);
    }

    public void onClickAction(Consumer<Player> action) {
        runActions.add(action);
    }

    public void runActions(InventoryClickEvent event) {
        if (!clickable) {
            event.setCancelled(true);
        }

        clickActions.forEach(action -> action.accept(event));
        Player player = (Player) event.getWhoClicked();

        runActions.forEach(action -> action.accept(player));
    }


}
