package me.homas343.storage;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PaginationManager {
    private final Map<Player, Integer> playerPages = new HashMap<>();
    public int getCurrentPage(Player player) {
        return playerPages.getOrDefault(player, 1);
    }
    public void setCurrentPage(Player player, int page) {
        playerPages.put(player, page);
    }
}
