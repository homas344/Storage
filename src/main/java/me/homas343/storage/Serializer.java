package me.homas343.storage;

import me.homas343.storage.bd.SaveManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Serializer {
    private final SaveManager saveManager;

    public Serializer() {
        this.saveManager = new SaveManager();
    }

    public void serializeItemInMainHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage("§6Storage §7| §fПредмет в руке §cне найден");
            return;
        }

        String playerName = player.getDisplayName();
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        saveManager.saveItemData(playerName, serializeItemStackToString(player, item));
    }

    public ItemStack deserializeStringToItemStack(String string) {
        try {
            byte[] serializedObject = Base64.getDecoder().decode(string);

            try (ByteArrayInputStream in = new ByteArrayInputStream(serializedObject);
                 BukkitObjectInputStream is = new BukkitObjectInputStream(in)) {

                return (ItemStack) is.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String serializeItemStackToString(Player player, ItemStack item) {
        String encodedObject;
        ByteArrayOutputStream io = new ByteArrayOutputStream();
        try (BukkitObjectOutputStream os = new BukkitObjectOutputStream(io)) {
            os.writeObject(item);
        } catch (IOException e) {
            player.sendMessage("§6Storage §7| §cОшибка при сериализации предмета.");
            e.printStackTrace();
        }

        byte[] serializedObject = io.toByteArray();
        encodedObject = Base64.getEncoder().encodeToString(serializedObject);
        return encodedObject;
    }
}
