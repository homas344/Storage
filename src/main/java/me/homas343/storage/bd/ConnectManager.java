package me.homas343.storage.bd;

import me.homas343.storage.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectManager {
    private final String host = Core.getInstance().getConfig().getString("db.host");
    private final int port = Core.getInstance().getConfig().getInt("db.port");
    private final String user = Core.getInstance().getConfig().getString("db.user");
    private final String password = Core.getInstance().getConfig().getString("db.password");
    private final String database = Core.getInstance().getConfig().getString("db.db_name");

    private Connection connection;
    private List<Map<String, String>> currentTable;

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
        currentTable = new ArrayList<>();
        String query = "SELECT player_name, item_value FROM storage_plugin";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("player_name", resultSet.getString("player_name"));
                row.put("item_value", resultSet.getString("item_value"));
                currentTable.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> getItemsByPlayerName(String playerName) {
        List<Map<String, String>> playerItems = new ArrayList<>();
        String query = "SELECT player_name, item_value FROM storage_plugin WHERE player_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, String> row = new HashMap<>();
                    row.put("player_name", resultSet.getString("player_name"));
                    row.put("item_value", resultSet.getString("item_value"));
                    playerItems.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerItems;
    }

    public void refreshTable() {
        loadTable();
    }

    public List<Map<String, String>> getCurrentTable() {
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
                String playerName = resultSet.getString("player_name");
                players.add(playerName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }
}
