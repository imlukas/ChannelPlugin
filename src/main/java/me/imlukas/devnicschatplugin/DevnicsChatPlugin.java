package me.imlukas.devnicschatplugin;

import lombok.Getter;
import me.imlukas.devnicschatplugin.channels.data.ChannelCache;
import me.imlukas.devnicschatplugin.channels.config.ChannelConfig;
import me.imlukas.devnicschatplugin.channels.listeners.PlayerJoinListener;
import me.imlukas.devnicschatplugin.channels.listeners.PlayerLeaveListener;
import me.imlukas.devnicschatplugin.commands.ChannelCommand;
import me.imlukas.devnicschatplugin.gui.ChannelListMenu;
import me.imlukas.devnicschatplugin.listeners.AsyncPlayerChatListener;
import me.imlukas.devnicschatplugin.sql.SQLHandler;
import me.imlukas.devnicschatplugin.sql.SQLSetup;
import me.imlukas.devnicschatplugin.utils.TextUtil;
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
    private TextUtil textUtil;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        textUtil = new TextUtil(this);
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
        ChannelListMenu.init(this);
        registerCommands();
        registerListeners();

    }
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);
    }
    private void registerCommands(){
        getCommand("channel").setExecutor(new ChannelCommand(this));
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
