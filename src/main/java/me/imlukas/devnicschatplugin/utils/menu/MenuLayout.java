package me.imlukas.devnicschatplugin.utils.menu;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MenuLayout {

    private final Map<Integer, MenuItem> resultingItems = new HashMap<>();
    private final List<MenuItem> registeredItems = new ArrayList<>();
    private int rows;
    private String title;
    private FileConfiguration config;

    public static MenuLayout parse(MenuManager manager, FileConfiguration section) {
        MenuLayout layout = new MenuLayout();
        try {
            layout.parseConfig(manager, section);
        } catch (Exception e) {
            System.err.println("[University] Error parsing menu layout " + section.getName());
            e.printStackTrace();
        }
        return layout;
    }

    private void parseConfig(MenuManager manager, FileConfiguration config) {
        this.config = config;

        rows = config.getInt("rows");
        title = config.getString("title");

        ConfigurationSection items = config.getConfigurationSection("items");

        if (items == null) {
            System.err.println(
                    "[University] Error parsing menu layout " + config.getName()
                            + ": no items section");
            return;
        }

        Map<String, MenuItem> mappedItems = new HashMap<>();

        for (String key : items.getKeys(false)) {
            if (key.isBlank() || key.isEmpty()) {
                continue;
            }

            ConfigurationSection section = items.getConfigurationSection(key);

            MenuItem item = new MenuItem(key, manager, section);
            mappedItems.put(key, item);
        }

        int slot = 0;
        for (String line : config.getStringList("layout")) {
            for (String character : line.split(" ")) {
                slot++;
                MenuItem item = mappedItems.get(character);

                if (item == null) {
                    item = new MenuItem(character, new ItemStack(Material.AIR), null);
                    mappedItems.put(character, item);
                }

                resultingItems.put(slot, item);
            }
        }

        registeredItems.addAll(mappedItems.values());
    }

    public void apply(Menu menu) {
        menu.setTitle(title);
        menu.setRows(rows);

        Map<Integer, MenuItem> results = new HashMap<>();

        for (Map.Entry<Integer, MenuItem> entry : resultingItems.entrySet()) {
            results.put(entry.getKey(), entry.getValue().clone());
        }

        menu.associateItems(results);
        menu.getRegisteredItems().addAll(registeredItems);

        if (menu instanceof PaginableMenu) {
            ((PaginableMenu) menu).init(this);
        }
    }
}
