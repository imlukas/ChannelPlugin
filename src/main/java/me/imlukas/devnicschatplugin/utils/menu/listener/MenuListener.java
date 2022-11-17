package me.imlukas.devnicschatplugin.utils.menu.listener;

import me.imlukas.devnicschatplugin.utils.menu.Menu;
import me.imlukas.devnicschatplugin.utils.menu.MenuChain;
import me.imlukas.devnicschatplugin.utils.menu.MenuManager;
import me.imlukas.devnicschatplugin.utils.menu.temp.HeldMenuData;
import me.imlukas.devnicschatplugin.utils.menu.temp.InputHeldMenuData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class MenuListener implements Listener {

    private final MenuManager manager;

    public MenuListener(MenuManager manager) {
        this.manager = manager;
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        Inventory top = event.getInventory();
        InventoryHolder holder = top.getHolder();

        if (holder instanceof Menu menu) {
            menu.handleClick(event);
        }
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        Inventory top = event.getInventory();
        InventoryHolder holder = top.getHolder();

        if (holder instanceof Menu menu) {
            MenuChain chain = manager.getChain((Player) event.getPlayer());
            Menu lastClosed = chain.getLastClosedMenu();

            if (lastClosed == null) {
                return;
            }
            if (menu.getOriginalName().equals(lastClosed.getOriginalName()))
                return;

            if (!manager.getHeldMenus(event.getPlayer().getUniqueId()).isEmpty())
                return;

            System.out.println("Breaking chain for player " + event.getPlayer().getName() + " because they closed the menu " + menu.getOriginalName() + " instead of the last closed menu " + lastClosed.getOriginalName());
            chain.clear(); // Break the chain - The player isn't transferring between menus, instead they're closing the menu
        }
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        /*Component message = event.getMessage()

        if(!(message instanceof TextComponent text))
            return;

         */

        String content = event.getMessage();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        for (HeldMenuData data : manager.getHeldMenus(uuid)) {
            if (!(data instanceof InputHeldMenuData input)) {
                continue;
            }

            System.out.println("Canceling event");
            event.setCancelled(true);
            manager.removeHeldMenu(data);

            if (content.equalsIgnoreCase("cancel")) {
                continue;
            }

            input.getCallback().accept(content);
        }
    }
}
