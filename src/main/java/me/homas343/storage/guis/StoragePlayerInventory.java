package me.homas343.storage.guis;

import me.homas343.storage.Core;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoragePlayerInventory extends PaginatedMenu {
    private String playerName;

    @Override
    protected List<String> getLore(String playerName) {
        return new ArrayList<>();
    }

    @Override
    protected String getMenuTitle() {
        return "Хранилище игрока " + playerName;
    }

    public void openPlayerInventory(Player viewPlayer, String targetPlayer, int page) {
        if (!Core.getInstance().getConnectManager().getPlayersInTable().contains(targetPlayer)) {
            viewPlayer.sendMessage("§6Storage §7| §fНе удалось найти игрока §c" + targetPlayer + " §fв базе данных");
            return;
        }
        this.playerName = targetPlayer;
        List<Map<String, String>> storedItems = Core.getInstance().getConnectManager().getItemsByPlayerName(targetPlayer);
        openPaginatedInventory(viewPlayer, page, storedItems);
    }
}
