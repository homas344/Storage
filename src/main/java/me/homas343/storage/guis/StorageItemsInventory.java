package me.homas343.storage.guis;

import me.homas343.storage.Core;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StorageItemsInventory extends PaginatedMenu {
    @Override
    protected List<String> getLore(String playerName) {
        return new ArrayList<>();
    }

    @Override
    protected String getMenuTitle() {
        return "Мои предметы ";
    }

    public void openMyItems(Player player, int page) {
        if (Core.getInstance().getConnectManager().getItemsByPlayerName(player.getDisplayName()).isEmpty()) {
            player.sendMessage("§6Storage §7| §fВаше хранилище пустое");
            player.closeInventory();
            return;
        }
        List<Map<String, String>> storedItems =
                Core.getInstance().getConnectManager().getItemsByPlayerName(player.getDisplayName());
        openPaginatedInventory(player, page, storedItems);
    }
}
