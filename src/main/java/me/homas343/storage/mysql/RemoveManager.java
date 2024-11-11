package me.homas343.storage.mysql;

import me.homas343.storage.Core;
import me.homas343.storage.Serializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveManager {

    public void removeItem(Player player, ItemStack item) {
        String encodedItemValue = encodeItem(player, item);

        String sql = "DELETE FROM storage_plugin WHERE player_name = ? AND item_value = ? LIMIT 1";
        Connection connection = null;
        boolean closeConnection = false;

        try {
            connection = Core.getInstance().getConnectManager().connect();
            if (connection.isClosed()) {
                connection = Core.getInstance().getConnectManager().connect();
                closeConnection = true;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, player.getName());
                statement.setString(2, encodedItemValue);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    player.sendMessage("§6Storage §7| §fВы забрали предмет §7- §a" + item.getType() + " §7x" + item.getAmount());
                    Core.getInstance().getConnectManager().refreshTable();
                } else {
                    player.sendMessage("§6Storage §7| §cПредмет не найден в хранилище");
                }
            }

        } catch (SQLException e) {
            player.sendMessage("§6Storage §7| §cОшибка при удалении предмета");
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

    private String encodeItem(Player player, ItemStack item) {
        Serializer serializer = new Serializer();
        return serializer.serializeItemStackToString(player, item);
    }
}
