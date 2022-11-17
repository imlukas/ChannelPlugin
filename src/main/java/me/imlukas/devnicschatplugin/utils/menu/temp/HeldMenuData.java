package me.imlukas.devnicschatplugin.utils.menu.temp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.imlukas.devnicschatplugin.utils.menu.Menu;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class HeldMenuData {

    private final Menu menu;
    private final String holdName;
    private final UUID playerId;
}
