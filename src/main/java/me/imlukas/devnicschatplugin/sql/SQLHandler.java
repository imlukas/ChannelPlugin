package me.imlukas.devnicschatplugin.sql;

import me.imlukas.devnicschatplugin.DevnicsChatPlugin;
import me.imlukas.devnicschatplugin.channels.config.ChannelConfig;
import me.imlukas.devnicschatplugin.channels.data.ChannelData;
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

    private final Connection connection;
    private final ChannelConfig channelConfig;
    private PreparedStatement query;

    public SQLHandler(DevnicsChatPlugin main) {
        this.channelConfig = main.getChannelConfig();
        SQLSetup sqlSetup = main.getSqlSetup();
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
                query.setString(2, DefaultChannels.GLOBAL.channelUUID.toString());
                query.executeUpdate();
                System.out.println("Added player " + playerUUID + " to the database.");
            } catch (SQLException e) {
                System.out.println("[DevnicsChat] Failed to add player to database.");
                e.printStackTrace();
            }
        });
    }

    /**
     * Checks if a player is in the database
     * @param playerUUID The UUID of the player
     * @return true if the player is in the database, false if not
     */
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

    /**
     * Gets the channel of a player
     * @param playerUUID The UUID of the player
     * @return UUID of the channel that the player is in
     */
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


    /**
     * Sets the channel of a player
     * @param playerUUID The UUID of the player
     * @param channelUUID The UUID of the channel
     */
    public void setChannel(UUID playerUUID, UUID channelUUID) {
        CompletableFuture.runAsync(() -> {
            ChannelData channelData = channelConfig.getChannelData(channelUUID);
            try {
                query = connection.prepareStatement(UPDATE_PLAYER_CHANNEL);
                query.setString(1, channelData.getName());
                query.setString(2, channelData.getUUID().toString());
                query.setString(3, playerUUID.toString());
                query.executeUpdate();
            }
            catch (Exception e){
                System.out.println("[DevnicsChat] Error while setting channel for player " + playerUUID.toString());
                e.printStackTrace();
            }
        });

    }

    /**
     * Resets all the player's channel
     * Used when deleting a channel, to reset all the players to the default channel
     * @param channelUUID The UUID of the channel that was deleted
     */
    public void resetPlayers(UUID channelUUID) {
        CompletableFuture.runAsync(() -> {
            try {
                query = connection.prepareStatement(RESET_PLAYER_CHANNELS);
                query.setString(1, DefaultChannels.GLOBAL.toString());
                query.setString(2, DefaultChannels.GLOBAL.channelUUID.toString());
                query.setString(3, channelUUID.toString());
                query.executeUpdate();
            }
            catch (Exception e){
                System.out.println("[DevnicsChat] Error while resetting players.");
                e.printStackTrace();
            }
        });
    }
}
