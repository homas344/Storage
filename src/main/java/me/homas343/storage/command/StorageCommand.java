package me.homas343.storage.command;

import me.homas343.storage.Core;
import me.homas343.storage.Serializer;
import me.homas343.storage.guis.StorageGlobalInventory;
import me.homas343.storage.guis.StorageItemsInventory;
import me.homas343.storage.guis.StoragePlayerInventory;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StorageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] label) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if (label.length == 0) {
            StorageGlobalInventory storageGlobalInventory = new StorageGlobalInventory();
            storageGlobalInventory.openPaginatedInventory(player, 1, Core.getInstance().getConnectManager().getCurrentTable());
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN,1,1);
        } else if (label.length == 1 && label[0].equalsIgnoreCase("save")) {
            Serializer serializer = new Serializer();
            serializer.serializeItemInMainHand(player);
            player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG,1,1);
        } else if (label.length == 2 && label[0].equalsIgnoreCase("player")) {
            StoragePlayerInventory storagePlayerInventory = new StoragePlayerInventory();
            storagePlayerInventory.openPlayerInventory(player, label[1], 1);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN,1,1);
        } else if (label.length == 1 && label[0].equalsIgnoreCase("items")) {
            StorageItemsInventory storageItemsInventory = new StorageItemsInventory();
            storageItemsInventory.openMyItems(player, 1);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN,1,1);
        }
        return true;
    }
}
