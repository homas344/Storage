package me.homas343.storage.guis;

import me.homas343.storage.Core;

import java.util.List;

public class StorageGlobalInventory extends PaginatedMenu {

    @Override
    protected List<String> getLore(String playerName) {
        return Core.getInstance().getConfig().getStringList("oth.lore");
    }
    @Override
    protected String getMenuTitle() {
        return "Хранилище";
    }
}
