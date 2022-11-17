package me.imlukas.devnicschatplugin;

import lombok.Getter;
import me.imlukas.devnicschatplugin.channels.ChannelCache;
import me.imlukas.devnicschatplugin.channels.config.ChannelConfig;
import me.imlukas.devnicschatplugin.commands.ChannelCommand;
import me.imlukas.devnicschatplugin.gui.ChatListMenu;
import me.imlukas.devnicschatplugin.sql.SQLHandler;
import me.imlukas.devnicschatplugin.sql.SQLSetup;
import me.imlukas.devnicschatplugin.utils.menu.MenuManager;
import me.imlukas.devnicschatplugin.utils.storage.MessagesFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


@Getter
public final class DevnicsChatPlugin extends JavaPlugin {

    private SQLHandler sqlHandler;
    private SQLSetup sqlSetup;
    private MessagesFile messages;
    private MenuManager menuManager;
    private ChannelConfig channelConfig;
    private ChannelCache channelCache;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        messages = new MessagesFile(this);
        sqlSetup = new SQLSetup(config.getString("mysql.host"),
                config.getString("mysql.database"),
                config.getString("mysql.username"),
                config.getString("mysql.password"),
                config.getInt("mysql.port"));

        sqlHandler = new SQLHandler(this);
        channelConfig = new ChannelConfig(this);
        menuManager = new MenuManager(this);
        channelCache = new ChannelCache();
        ChatListMenu.init(this);

    }

    private void registerCommands(){
        getCommand("channel").setExecutor(new ChannelCommand(this));
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
