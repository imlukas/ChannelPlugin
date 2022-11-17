package me.imlukas.devnicschatplugin.utils.menu;

import me.imlukas.devnicschatplugin.utils.menu.data.PaginableElement;
import me.imlukas.devnicschatplugin.utils.menu.placeholder.Placeholder;
import me.imlukas.devnicschatplugin.utils.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;
import java.util.function.Consumer;

public class PaginableMenu extends Menu {

    private final List<PaginableElement> elements = new LinkedList<>();
    private final List<Integer> elementSlots = new LinkedList<>();
    private final Map<Integer, Consumer<InventoryClickEvent>> elementClickHandlers = new HashMap<>();
    private MenuItem filler;
    private MenuItem activeItem;
    private MenuItem previousPage;
    private MenuItem nextPage;
    private int page = 1;

    public PaginableMenu(MenuManager menuManager, String originalName) {
        super(menuManager, originalName);
    }

    public void init(MenuLayout layout) {
        ConfigurationSection config = layout.getConfig().getConfigurationSection("pagination");

        filler = getItem(config.getString("filler", "."));
        previousPage = getItem(config.getString("previous-page", "."));
        nextPage = getItem(config.getString("next-page", "."));
        activeItem = getItem(config.getString("active-item", "."));

        elementSlots.clear();
        elementSlots.addAll(getFillerSlots());

        if (previousPage != null) {
            previousPage.onClickAction((player) -> {
                if (page > 1) {
                    page--;
                    renderPage(player);
                }
            });
        }

        if (nextPage != null) {
            nextPage.onClickAction((player) -> {
                if (page < getMaxPages()) {
                    page++;
                    renderPage(player);
                }
            });
        }
    }

    @Override
    public void build(Player player) {
        super.build(player);

        // Render elements on page, on elementSlots
        renderPage(player);
    }

    public void clearElements() {
        elements.clear();
    }

    public void addElement(Placeholder<Player>... elements) {
        List<Placeholder<Player>> list = new LinkedList<>(Arrays.asList(elements));

        this.elements.add(new PaginableElement(list, activeItem));
    }


    public void addElement(Consumer<Player> clickAction, Placeholder<Player>... elements) {
        List<Placeholder<Player>> list = new LinkedList<>(Arrays.asList(elements));

        int index = this.elements.size();
        elementClickHandlers.put(index,
                (event) -> clickAction.accept((Player) event.getWhoClicked()));
        this.elements.add(new PaginableElement(list, activeItem));
    }

    public void addElement(Consumer<Player> clickAction, Collection<Placeholder<Player>> elements) {
        int index = this.elements.size();
        elementClickHandlers.put(index,
                (event) -> clickAction.accept((Player) event.getWhoClicked()));
        this.elements.add(new PaginableElement(elements, activeItem));
    }


    public void addElement(Collection<Placeholder<Player>> elements) {
        this.elements.add(new PaginableElement(elements, activeItem));
    }

    public void addElement(PaginableElement element) {
        this.elements.add(element);
    }

    private void renderPage(Player player) {
        int start = (page - 1) * elementSlots.size();
        int end = start + elementSlots.size();



        for (int index = start; index < end; index++) {


            if (index - start >= elements.size()) {

                setItems.put(elementSlots.get(index - start), filler.clone());
                getInventory().setItem(elementSlots.get(index - start) - 1,
                        filler.clone().getItem());
                continue;
            }

            PaginableElement element = elements.get(index);
            MenuItem item = element.getDisplayItem().clone();
            Collection<Placeholder<Player>> placeholders = element.getPlaceholders();

            Consumer<InventoryClickEvent> clickAction = elementClickHandlers.get(index);

            if (clickAction != null) {
                item.onClick(clickAction);
            }


            setItems.put(elementSlots.get(index - start), item);
            getInventory().setItem(elementSlots.get(index - start) - 1,
                    process(item.getItem().clone(), player, placeholders));
        }

        player.updateInventory();

        new ScheduleBuilder(menuManager.getPlugin())
                .in(1).ticks()
                .run(player::updateInventory)
                .sync()
                .start();
    }

    public void onClick(int elementSlot, Consumer<InventoryClickEvent> handler) {
        elementClickHandlers.put(elementSlot, handler);
    }

    public void onPlayerClick(int elementSlot, Consumer<Player> handler) {
        elementClickHandlers.put(elementSlot, (event) -> {
            handler.accept((Player) event.getWhoClicked());
        });
    }


    @Override
    public void setProtected(boolean value) {
        super.setProtected(value);


        activeItem.setClickable(!value);
        filler.setClickable(!value);
        previousPage.setClickable(!value);
        nextPage.setClickable(!value);
    }

    private int getMaxPages() {
        return (int) Math.ceil((double) elements.size() / elementSlots.size());
    }

    private List<Integer> getFillerSlots() {
        List<Integer> slots = new LinkedList<>();

        if (filler == null) {
            return slots;
        }

        for (Map.Entry<Integer, MenuItem> entry : setItems.entrySet()) {
            if (entry.getValue().getOriginalCharacter().equals(filler.getOriginalCharacter())) {
                slots.add(entry.getKey());
            }
        }

        return slots;
    }
}
