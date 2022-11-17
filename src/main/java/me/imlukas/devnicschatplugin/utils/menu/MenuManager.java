package me.imlukas.devnicschatplugin.utils.menu;


import lombok.Getter;
import me.imlukas.devnicschatplugin.utils.menu.listener.MenuListener;
import me.imlukas.devnicschatplugin.utils.menu.temp.HeldMenuData;
import me.imlukas.devnicschatplugin.utils.storage.YMLBase;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class MenuManager {

    private final Map<String, Consumer<Menu>> postLoadTasks = new HashMap<>();
    private final Map<String, MenuLayout> loadedLayouts = new HashMap<>();

    private final Map<UUID, MenuChain> menuChains = new HashMap<>();

    @Getter
    private final JavaPlugin plugin;

    private final Set<HeldMenuData> heldMenus = new HashSet<>();


    public MenuManager(JavaPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new MenuListener(this), plugin);
        load(plugin.getDataFolder());
    }

    public MenuChain getChain(UUID uuid) {
        return menuChains.computeIfAbsent(uuid, k -> new MenuChain());
    }

    public void removeChain(UUID uuid) {
        menuChains.remove(uuid);
    }

    public MenuChain getChain(Player player) {
        return getChain(player.getUniqueId());
    }

    public void reload() {
        loadedLayouts.clear();

        load(plugin.getDataFolder());
    }

    private void load(File folder) {
        File menuFolder = new File(folder + File.separator + "menu");

        if (!menuFolder.exists()) {
            menuFolder.mkdirs();
        }

        File[] files = menuFolder.listFiles();

        System.out.println("Loading " + files.length + " menu layouts");

        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }

            FileConfiguration config = new YMLBase(plugin, file, true).getConfiguration();

            initMenu(file, config);
        }
    }

    private void initMenu(File file, FileConfiguration config) {
        MenuLayout layout = MenuLayout.parse(this, config);
        String name = file.getName().replace(".yml", "");

        System.out.println("Loaded menu layout: " + name);
        loadedLayouts.put(name, layout);
    }

    public void associateMenuInit(String menuName, Consumer<Menu> initTask) {
        postLoadTasks.put(menuName, initTask);
    }


    private Menu applyLayout(Menu menu, String layoutName) {
        MenuLayout layout = loadedLayouts.get(layoutName);

        if (layout != null) {
            layout.apply(menu);
        }

        return menu;
    }

    public Menu getGUI(String menuName) {
        if (!loadedLayouts.containsKey(menuName)) {
            File file = new File(
                    plugin.getDataFolder() + File.separator + "menu" + File.separator + menuName
                            + ".yml");

            try {
                FileConfiguration config = new YMLBase(plugin, file, true).getConfiguration();
                initMenu(file, config);
                return getGUI(menuName);
            } catch (Exception e) {
                System.out.println("Failed to load menu: " + menuName);
                return null;
            }

        }

        MenuLayout layout = loadedLayouts.get(menuName);

        Menu menu;

        if (layout.getConfig().contains("pagination")) {
            menu = new PaginableMenu(this, menuName);
        } else {
            menu = new Menu(this, menuName);
        }

        applyLayout(menu, menuName);

        if (postLoadTasks.containsKey(menuName)) {
            postLoadTasks.get(menuName).accept(menu);
        }
        return menu;
    }

    public FileConfiguration getOriginalConfig(String menuName) {
        if (!loadedLayouts.containsKey(menuName)) {
            File file = new File(
                    plugin.getDataFolder() + File.separator + "menu" + File.separator + menuName
                            + ".yml");

            try {
                FileConfiguration config = new YMLBase(plugin, file, true).getConfiguration();
                initMenu(file, config);
                return config;
            } catch (Exception e) {
                return null;
            }

        }

        return loadedLayouts.get(menuName).getConfig();
    }

    public void removeHeldMenu(HeldMenuData heldMenu) {
        heldMenus.remove(heldMenu);
    }

    public Set<HeldMenuData> getHeldMenus(UUID playerId) {
        Set<HeldMenuData> heldMenus = new HashSet<>();

        for (HeldMenuData heldMenu : this.heldMenus) {
            if (heldMenu.getPlayerId().equals(playerId)) {
                heldMenus.add(heldMenu);
            }
        }

        return heldMenus;
    }

    public HeldMenuData tryGetHeldData(UUID playerId, String reason) {
        for (HeldMenuData heldMenu : this.heldMenus) {
            if (heldMenu.getPlayerId().equals(playerId) && heldMenu.getHoldName()
                    .equalsIgnoreCase(reason)) {
                return heldMenu;
            }
        }

        return null;
    }

    public List<String> getMenuNames() {
        return new ArrayList<>(loadedLayouts.keySet());
    }

    void hold(HeldMenuData heldMenu) {
        heldMenus.add(heldMenu);
    }

}
