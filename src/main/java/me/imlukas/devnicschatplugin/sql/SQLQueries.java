package me.imlukas.devnicschatplugin.sql;

public class SQLQueries {

    public static final String CREATE_CHATMANAGER_TABLE = "CREATE TABLE IF NOT EXISTS ChatManager (UUID VARCHAR(36) UNIQUE NOT NULL," +
            " Channel varchar(255)," +
            " ChannelID varchar(36));";
    public static final String GET_PLAYER_CHANNEL = "SELECT ChannelID FROM ChatManager WHERE UUID = ?";
    public static final String INSERT_PLAYER = "INSERT INTO ChatManager (UUID, Channel, ChannelID) VALUES (?, global, 1)";
    public static final String UPDATE_PLAYER_CHANNEL = "UPDATE ChatManager SET Channel = ?, ChannelID = ? WHERE UUID = ?";
    public static final String RESET_PLAYER_CHANNELS = "UPDATE ChatManager SET Channel = ?, ChannelID = ? WHERE Channel = ?";
}
