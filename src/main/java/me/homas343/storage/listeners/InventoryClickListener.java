package me.homas343.storage.listeners;

import me.homas343.storage.Core;
import me.homas343.storage.bd.RemoveManager;
import me.homas343.storage.guis.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class InventoryClickListener implements Listener {

    private final PaginationManager paginationManager = Core.getInstance().getPaginationManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith("Хранилище")) {
            handleStorageInventoryClick(event, title);
        } else if (title.startsWith("Мои предметы")) {
            handleMyItemsInventoryClick(event);
        }
    }

    private void handleStorageInventoryClick(InventoryClickEvent event, String title) {
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;
        String displayName = clickedItem.getItemMeta().getDisplayName();

        if (title.startsWith("Хранилище игрока")) {
            String targetPlayer = title.split(" ")[2];
            handleNavigationButtons(displayName, event, new StoragePlayerInventory(), targetPlayer);

        } else handleNavigationButtons(displayName, event, new StorageGlobalInventory(), null);
    }

    private void handleMyItemsInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem != null && event.getSlot() < 45) {
            removeItemFromStorage(player, clickedItem);
            updateMyItemsInventory(player);
            player.getInventory().addItem(clickedItem);
        }
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String displayName = clickedItem.getItemMeta().getDisplayName();
        handleNavigationButtons(displayName, event, new StorageItemsInventory(), null);
    }

    private void removeItemFromStorage(Player player, ItemStack item) {
        RemoveManager removeManager = new RemoveManager();
        removeManager.removeItem(player, item);
        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG,1,1);
    }

    private void updateMyItemsInventory(Player player) {
        List<Map<String, String>> playerItems = Core.getInstance().getConnectManager().getItemsByPlayerName(player.getDisplayName());

        if (playerItems.isEmpty()) {
            player.closeInventory();
            return;
        }

        int totalItems = playerItems.size();
        int currentPage = paginationManager.getCurrentPage(player);
        if (totalItems % 45 == 0 && currentPage > 1) {
            currentPage--;
        }

        StorageItemsInventory storageItemsInventory = new StorageItemsInventory();
        storageItemsInventory.openPaginatedInventory(player, currentPage, playerItems);
    }


    private void handleNavigationButtons(String displayName, InventoryClickEvent event, PaginatedMenu paginatedMenu,
                                         String targetPlayer) {

        if (displayName.equals("§eСледующая страница")) {
            handleNextPage(event, paginatedMenu, targetPlayer);
        }

        if (displayName.equals("§eПредыдущая страница")) {
            handlePreviousPage(event, paginatedMenu, targetPlayer);
        }
        Player player = (Player) event.getWhoClicked();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,1,2);
    }

    private void handleNextPage(InventoryClickEvent event, PaginatedMenu paginatedMenu, String targetPlayer) {
        Player player = (Player) event.getWhoClicked();
        int currentPage = paginationManager.getCurrentPage(player);
        if (paginatedMenu instanceof StoragePlayerInventory && targetPlayer != null) {
            ((StoragePlayerInventory) paginatedMenu).openPlayerInventory(player, targetPlayer, currentPage + 1);
        } else if (paginatedMenu instanceof StorageItemsInventory) {
            paginatedMenu.openPaginatedInventory(player, currentPage + 1, Core.getInstance().getConnectManager().getItemsByPlayerName(player.getDisplayName()));
        } else {
            paginatedMenu.openPaginatedInventory(player, currentPage + 1, Core.getInstance().getConnectManager().getCurrentTable());
        }
    }

    private void handlePreviousPage(InventoryClickEvent event, PaginatedMenu paginatedMenu, String targetPlayer) {
        Player player = (Player) event.getWhoClicked();
        int currentPage = paginationManager.getCurrentPage(player);
        if (currentPage > 1) {
            if (paginatedMenu instanceof StoragePlayerInventory && targetPlayer != null) {
                ((StoragePlayerInventory) paginatedMenu).openPlayerInventory(player, targetPlayer, currentPage - 1);
            } else if (paginatedMenu instanceof StorageItemsInventory) {
                paginatedMenu.openPaginatedInventory(player, currentPage - 1, Core.getInstance().getConnectManager().getItemsByPlayerName(player.getDisplayName()));
            } else {
                paginatedMenu.openPaginatedInventory(player, currentPage - 1, Core.getInstance().getConnectManager().getCurrentTable());
            }
        }
    }
}
