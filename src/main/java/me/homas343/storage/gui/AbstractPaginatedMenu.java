package me.homas343.storage.gui;

import me.homas343.storage.Core;
import me.homas343.storage.PaginationManager;
import me.homas343.storage.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractPaginatedMenu {
    private final Serializer serializer = new Serializer();
    private final PaginationManager paginationManager = Core.getInstance().getPaginationManager();

    public void openPaginatedInventory(Player player, int page, HashMap<String, String> storedItems) {
        paginationManager.setCurrentPage(player, page);

        int itemsPerPage = 45;
        int totalPages = calculateTotalPages(storedItems.size(), itemsPerPage);
        int startIndex = calculateStartIndex(page, itemsPerPage);
        int endIndex = calculateEndIndex(page, itemsPerPage, storedItems.size());

        Inventory inventory = createInventory(page, totalPages);
        populateInventoryWithItems(inventory, storedItems, startIndex, endIndex);
        fillEmptySlotsWithGlass(inventory);
        addNavigationButtons(inventory, page, totalPages);

        player.openInventory(inventory);
    }

    private int calculateTotalPages(int totalItems, int itemsPerPage) {
        return (int) Math.ceil(totalItems / (double) itemsPerPage);
    }

    private int calculateStartIndex(int page, int itemsPerPage) {
        return (page - 1) * itemsPerPage;
    }

    private int calculateEndIndex(int page, int itemsPerPage, int totalItems) {
        return Math.min(page * itemsPerPage, totalItems);
    }

    private Inventory createInventory(int page, int totalPages) {
        return Bukkit.createInventory(null, 54, getMenuTitle() + " " + page + "/" + totalPages);
    }

    private void populateInventoryWithItems(Inventory inventory, HashMap<String, String> storedItems,
                                            int startIndex, int endIndex) {
        int slot = 0;
        List<Map.Entry<String, String>> itemsList = new ArrayList<>(storedItems.entrySet());

        for (int i = startIndex; i < endIndex; i++) {
            if (slot == 45 || slot == 53) {
                slot++;
            }
            if (slot >= inventory.getSize()) break;

            Map.Entry<String, String> entry = itemsList.get(i);
            ItemStack item = createItemFromStoredData(entry);

            inventory.setItem(slot++, item);
        }
    }

    private ItemStack createItemFromStoredData(Map.Entry<String, String> entry) {
        String encodedObject = entry.getValue();
        String playerName = entry.getKey();

        ItemStack item = serializer.deserializeStringToItemStack(encodedObject);
        List<String> modifiedLore = modifyItemLore(playerName);

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            setItemMetaLore(itemMeta, modifiedLore);
            item.setItemMeta(itemMeta);
        }

        return item;
    }

    private List<String> modifyItemLore(String playerName) {
        List<String> configLore = getLore(playerName);
        return configLore.stream()
                .map(line -> line.replace("&", "§").replace("%player_name%", playerName))
                .collect(Collectors.toList());
    }

    private void setItemMetaLore(ItemMeta itemMeta, List<String> modifiedLore) {
        if (itemMeta.getLore() == null) {
            itemMeta.setLore(modifiedLore);
        } else {
            List<String> newLore = new ArrayList<>(itemMeta.getLore());
            newLore.addAll(modifiedLore);
            itemMeta.setLore(newLore);
        }
    }

    private void addNavigationButtons(Inventory inventory, int page, int totalPages) {
        if (page < totalPages) {
            ItemStack nextPageItem = createNavigationButton("§eСледующая страница");
            inventory.setItem(53, nextPageItem);
        }

        if (page > 1) {
            ItemStack prevPageItem = createNavigationButton("§eПредыдущая страница");
            inventory.setItem(45, prevPageItem);
        }
    }

    private ItemStack createNavigationButton(String displayName) {
        ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void fillEmptySlotsWithGlass(Inventory inventory) {
        ItemStack glassItem = createGlassItem();
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, glassItem);
        }
    }

    private ItemStack createGlassItem() {
        ItemStack glassItem = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta meta = glassItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§7");
            glassItem.setItemMeta(meta);
        }
        return glassItem;
    }

    protected abstract List<String> getLore(String playerName);

    protected abstract String getMenuTitle();
}
