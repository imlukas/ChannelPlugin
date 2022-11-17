package me.imlukas.devnicschatplugin.utils.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.imlukas.devnicschatplugin.utils.TextUtil;
import me.imlukas.devnicschatplugin.utils.item.ItemUtil;
import me.imlukas.devnicschatplugin.utils.menu.data.MenuMetadata;
import me.imlukas.devnicschatplugin.utils.menu.placeholder.Placeholder;
import me.imlukas.devnicschatplugin.utils.menu.temp.HeldMenuData;
import me.imlukas.devnicschatplugin.utils.menu.temp.InputHeldMenuData;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

@Getter
@Setter(AccessLevel.PROTECTED)
public class Menu implements InventoryHolder {

    protected final MenuManager menuManager;

    private final String originalName;

    protected final Set<Placeholder<Player>> associatedPlaceholders = new HashSet<>();
    protected final Map<Integer, MenuItem> setItems = new HashMap<>();
    protected final List<MenuItem> registeredItems = new ArrayList<>();
    protected final List<Consumer<Player>> openTasks = new ArrayList<>();
    protected final MenuMetadata metadata = new MenuMetadata();
    protected String title;
    protected int rows;
    protected Inventory builtInventory;
    @Setter
    private Consumer<Player> buildAction;

    public Menu(MenuManager menuManager, String originalName) {
        this.menuManager = menuManager;
        this.originalName = originalName;
    }

    void associateItems(Map<Integer, MenuItem> map) {
        setItems.putAll(map);
    }

    public final void associatePlaceholders(Collection<Placeholder<Player>> placeholders) {
        associatedPlaceholders.addAll(placeholders);
    }

    @SafeVarargs
    public final void associatePlaceholders(Placeholder<Player>... placeholder) {
        Set<String> set = new HashSet<>();

        for (Placeholder<Player> playerPlaceholder : placeholder) {
            set.add(playerPlaceholder.getPlaceholder());
        }

        associatedPlaceholders.removeIf((holder) -> set.contains(holder.getPlaceholder()));
        associatedPlaceholders.addAll(Arrays.asList(placeholder));
    }

    public MenuItem getItem(int friendlySlot) {
        return setItems.get(friendlySlot - 1).clone();
    }

    public MenuItem getItem(int x, int y) {
        return getItem(y * 9 + x).clone();
    }

    public MenuItem getItem(String text) {
        for (MenuItem item : setItems.values()) {
            if (item == null)
                continue;

            if (item.getOriginalCharacter().equals(text)) {
                return item;
            }
        }

        for (MenuItem item : registeredItems) {
            if (item.getOriginalCharacter().equals(text)) {
                return item;
            }
        }

        return null;
    }

    public void setItem(String text, MenuItem other) {
        for (int index = 0; index < setItems.size(); index++) {
            MenuItem item = setItems.get(index);

            if (item == null) {
                continue;
            }

            if (item.getOriginalCharacter().equals(text)) {
                setItems.put(index, other);
                return;
            }
        }
    }

    protected String colorize(String text) {
        return TextUtil.color(text);
    }

    protected int rowsToSlots() {
        return rows * 9;
    }

    public void build(Player player) {
        if (buildAction != null) {;
            buildAction.accept(player);
        }

        associatedPlaceholders.add(new Placeholder<>("%player%", player.getName()));

        if (builtInventory == null) {
            builtInventory = Bukkit.createInventory(this, rowsToSlots(), process(title, player));
        }

        for (Map.Entry<Integer, MenuItem> entry : setItems.entrySet()) {
            int slot = entry.getKey() - 1;

            if (slot >= rowsToSlots()) {
                continue;
            }

            MenuItem item = entry.getValue();
            builtInventory.setItem(slot, process(item, player));
        }

        player.updateInventory();
    }

    public void open(Player player) {
        if (builtInventory == null) {
            build(player);
        }

        for (Consumer<Player> task : openTasks) {
            task.accept(player);
        }

        if (Bukkit.isPrimaryThread()) {
            player.openInventory(builtInventory);
        } else {
            Bukkit.getScheduler()
                    .runTask(menuManager.getPlugin(), () -> player.openInventory(builtInventory));
        }
    }

    protected String process(String text, Player player) {
        return colorize(replacePlaceholders(text, player));
    }

    protected ItemStack process(MenuItem item, Player player) {
        ItemStack realItem = item.getItem().clone();
        ItemUtil.replacePlaceholder(realItem, player, associatedPlaceholders);
        return realItem;
    }

    protected ItemStack process(ItemStack item, Player player) {
        ItemUtil.replacePlaceholder(item, player, associatedPlaceholders);
        return item;
    }

    protected ItemStack process(ItemStack item, Player player, Collection<Placeholder<Player>> placeholders) {
        try {
            ItemUtil.replacePlaceholder(item, player, placeholders);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    protected String replacePlaceholders(String text, Player player) {
        for (Placeholder<Player> placeholder : associatedPlaceholders) {
            text = placeholder.replace(text, player);
        }

        return text;
    }

    public void onClickAction(Consumer<Player> consumer) {
        for (MenuItem item : setItems.values()) {
            item.onClickAction(consumer);
        }
    }

    public void onClick(Consumer<InventoryClickEvent> consumer) {
        for (MenuItem item : setItems.values()) {
            item.onClick(consumer);
        }
    }

    public void setProtected(boolean value) {
        for (MenuItem item : setItems.values()) {
            item.setClickable(!value);
        }
    }

    @Override
    public Inventory getInventory() {
        if (builtInventory == null) {
            throw new IllegalStateException("Menu has not been built yet!");
        }
        return builtInventory;
    }

    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot() + 1;

        MenuItem item = setItems.get(slot);

        if (item == null) {
            return;
        }

        item.runActions(event);
    }

    public void handleOpen(Consumer<Player> consumer) {
        openTasks.add(consumer);
    }


    public void hold(String reason) {
        for (HumanEntity player : builtInventory.getViewers()) {
            menuManager.hold(new HeldMenuData(this, reason, player.getUniqueId()));
            player.closeInventory();
        }
    }

    public void hold(String reason, Player player) {
        menuManager.hold(new HeldMenuData(this, reason, player.getUniqueId()));
        player.closeInventory();
    }

    public void holdForInput(String reason, Player player, Consumer<String> consumer) {
        menuManager.hold(new InputHeldMenuData(this, reason, player.getUniqueId(), consumer));
        player.closeInventory();
    }
}
