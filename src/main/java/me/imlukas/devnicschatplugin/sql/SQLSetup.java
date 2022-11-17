package me.imlukas.devnicschatplugin.sql;

import me.imlukas.devnicschatplugin.utils.SQLConnectionProvider;

import java.sql.DriverManager;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static me.imlukas.devnicschatplugin.sql.SQLQueries.CREATE_CHATMANAGER_TABLE;


public class SQLSetup extends SQLConnectionProvider {

    private static final String[] TABLES = {
            CREATE_CHATMANAGER_TABLE
    };

    private final String host, database, username, password;
    private final int port;

    private final Logger log = Logger.getLogger("SQL");

    public SQLSetup(String host, String database, String username, String password, int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.database = database;
    }

    @Override
    public void load() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            System.out.println("[DevnicsChat] Connected to MySQL server.");
        } catch (Exception e) {
            log.info(e.toString());
            System.out.println("[DevnicsChat] Failed to connect to MySQL server.");
        }
    }

    public void createTables() {
        CompletableFuture.runAsync(() -> {
            try {
                for (String query : TABLES) {
                    connection.createStatement().execute(query);
                }
            } catch (Exception e) {
                log.info(e.toString());
            }
        });
    }


}
