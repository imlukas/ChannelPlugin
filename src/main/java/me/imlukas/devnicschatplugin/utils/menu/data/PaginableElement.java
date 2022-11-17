package me.imlukas.devnicschatplugin.utils.menu.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.imlukas.devnicschatplugin.utils.menu.MenuItem;
import me.imlukas.devnicschatplugin.utils.menu.placeholder.Placeholder;
import org.bukkit.entity.Player;

import java.util.Collection;

@Data
@AllArgsConstructor
public class PaginableElement {

    private final Collection<Placeholder<Player>> placeholders;
    private MenuItem displayItem;
}
