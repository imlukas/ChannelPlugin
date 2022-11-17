package me.imlukas.devnicschatplugin.utils.menu.temp;

import lombok.Getter;
import me.imlukas.devnicschatplugin.utils.menu.Menu;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class InputHeldMenuData extends HeldMenuData {

    private final Consumer<String> callback;

    public InputHeldMenuData(Menu menu, String holdName, UUID playerId, Consumer<String> callback) {
        super(menu, holdName, playerId);
        this.callback = callback;
    }
}
