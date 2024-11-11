package me.homas343.storage.mysql;

import me.homas343.storage.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectManager {
    private final String host = Core.getInstance().getConfig().getString("db.host");
    private final int port = Core.getInstance().getConfig().getInt("db.port");
    private final String user = Core.getInstance().getConfig().getString("db.user");
    private final String password = Core.getInstance().getConfig().getString("db.password");
    private final String database = Core.getInstance().getConfig().getString("db.db_name");

    private Connection connection;
    private HashMap<String, String> currentTable;

    public Connection connect() {
        ConsoleCommandSender c = Bukkit.getConsoleSender();
        if (connection != null) {
            return connection;
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        try {
            connection = DriverManager.getConnection(url, user, password);
            c.sendMessage("§6[Storage] §7| §fУспешно подключено к бд");

            createTable();
            loadTable();
        } catch (SQLException e) {
            c.sendMessage("§6[Storage] §7| §cНе удалось подключиться к бд");
            e.printStackTrace();
        }

        return connection;
    }

    private void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS storage_plugin (" +
                "player_name VARCHAR(100) NOT NULL, " +
                "item_value TEXT NOT NULL " +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTable() {
        currentTable = new HashMap<>();
        String query = "SELECT player_name, item_value FROM storage_plugin";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                currentTable.put(resultSet.getString("player_name"), resultSet.getString("item_value"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getItemsByPlayerName(String playerName) {
        HashMap<String, String> playerItem = new HashMap<>();
        String query = "SELECT item_value FROM storage_plugin WHERE player_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    playerItem.put(playerName, resultSet.getString("item_value"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerItem;
    }

    public void refreshTable() {
        loadTable();
    }

    public HashMap<String, String> getCurrentTable() {
        return currentTable;
    }

    public void disconnect() {
        ConsoleCommandSender c = Bukkit.getConsoleSender();
        if (connection != null) {
            try {
                connection.close();
                c.sendMessage("§6[Storage] §7| §fОтключение от бд");
            } catch (SQLException e) {
                c.sendMessage("§6[Storage] §7| §cПроизошла ошибка при отключении");
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getPlayersInTable() {
        List<String> players = new ArrayList<>();
        String query = "SELECT DISTINCT player_name FROM storage_plugin";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                players.add(resultSet.getString("player_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }
}
