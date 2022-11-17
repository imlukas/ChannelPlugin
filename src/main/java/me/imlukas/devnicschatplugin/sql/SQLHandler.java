package me.imlukas.devnicschatplugin.sql;

import me.imlukas.devnicschatplugin.DevnicsChatPlugin;
import me.imlukas.devnicschatplugin.channels.config.ChannelConfig;
import me.imlukas.devnicschatplugin.channels.impl.ChannelData;
import me.imlukas.devnicschatplugin.channels.DefaultChannels;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.imlukas.devnicschatplugin.sql.SQLQueries.*;

public class SQLHandler {

    private final SQLSetup sqlSetup;
    private final Connection connection;
    private final ChannelConfig channelConfig;
    private final FileConfiguration config;
    private PreparedStatement query;

    public SQLHandler(DevnicsChatPlugin main) {
        this.config = main.getConfig();
        this.channelConfig = main.getChannelConfig();
        sqlSetup = new SQLSetup(
                config.getString("mysql.host"),
                config.getString("mysql.database"),
                config.getString("mysql.username"),
                config.getString("mysql.password"),
                config.getInt("mysql.port"));
        sqlSetup.createTables();
        connection = sqlSetup.get();
    }

    /**
     * Adds a player to the database
     * Throws SQLException.
     * @param playerUUID The UUID of the player
     */
    public void addPlayer(UUID playerUUID) {
        CompletableFuture.runAsync(() -> {
            try {
                query = connection.prepareStatement(INSERT_PLAYER);
                query.setString(1, playerUUID.toString());
                query.executeUpdate();

            } catch (SQLException e) {
                System.out.println("[DevnicsChat] Failed to add player to database.");
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Boolean> playerExists(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                query = connection.prepareStatement(GET_PLAYER_CHANNEL);
                query.setString(1, playerUUID.toString());
                ResultSet rs = query.executeQuery();
                if (rs.next()) {
                    return true;
                }
                rs.close();
            } catch (SQLException e) {
                System.out.println("[DevnicsChat] Failed to check if player exists.");
                e.printStackTrace();
            }
            return false;
        });

    }

    public CompletableFuture<String> getChannel(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                query = connection.prepareStatement(GET_PLAYER_CHANNEL);
                query.setString(1, playerUUID.toString());

                ResultSet resultSet = query.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("channelid");
                }
                resultSet.close();

            } catch (Exception e) {
                System.out.println("[DevnicsChat] Error while getting channel from player " + playerUUID.toString());
                e.printStackTrace();
            }
            return "";
        });
    }

    public void setChannel(UUID uniqueId, UUID channelUUID) {
        CompletableFuture.runAsync(() -> {
            ChannelData channelData = channelConfig.getChannelData(channelUUID);
            try {
                query = connection.prepareStatement(UPDATE_PLAYER_CHANNEL);
                query.setString(1, channelData.getName());
                query.setString(2, channelData.getUUID().toString());
                query.setString(3, uniqueId.toString());
                query.executeUpdate();
            }
            catch (Exception e){
                System.out.println("[DevnicsChat] Error while setting channel for player " + uniqueId.toString());
                e.printStackTrace();
            }
        });

    }

    public void resetPlayers(String channelName) {
        CompletableFuture.runAsync(() -> {
            try {
                query = connection.prepareStatement(RESET_PLAYER_CHANNELS);
                query.setString(1, DefaultChannels.GLOBAL.toString());
                query.setString(2, DefaultChannels.GLOBAL.channelUUID.toString());
                query.setString(3, channelName);
                query.executeUpdate();
            }
            catch (Exception e){
                System.out.println("[DevnicsChat] Error while resetting players.");
                e.printStackTrace();
            }
        });
    }
}
