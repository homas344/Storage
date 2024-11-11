package me.homas343.storage.gui;

import me.homas343.storage.Core;

import java.util.List;

public class PaginatedGlobalInventory extends AbstractPaginatedMenu {

    @Override
    protected List<String> getLore(String playerName) {
        return Core.getInstance().getConfig().getStringList("oth.lore");
    }
    @Override
    protected String getMenuTitle() {
        return "Хранилище";
    }
}
