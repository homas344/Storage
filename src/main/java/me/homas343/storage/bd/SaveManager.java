package me.homas343.storage.bd;

import me.homas343.storage.Core;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SaveManager {

    public void saveItemData(String playerName, String encodedObject) {
        String sql = "INSERT INTO storage_plugin (player_name, item_value) VALUES (?, ?)";
        Connection connection = null;
        boolean closeConnection = false;

        try {
            connection = Core.getInstance().getConnectManager().connect();
            if (connection.isClosed()) {
                connection = Core.getInstance().getConnectManager().connect();
                closeConnection = true;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerName);
                statement.setString(2, encodedObject);
                statement.executeUpdate();
            }

            Bukkit.getPlayerExact(playerName).sendMessage("§6Storage §7| §fПредмет добавлен в хранилище");
            Core.getInstance().getConnectManager().refreshTable();

        } catch (SQLException e) {
            Bukkit.getPlayerExact(playerName).sendMessage("§6Storage §7| §cОшибка при сохранении предмета");
            e.printStackTrace();

        } finally {
            if (closeConnection && connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
