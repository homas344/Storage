package me.homas343.storage;

import me.homas343.storage.mysql.ConnectManager;
import me.homas343.storage.command.StorageCommand;
import me.homas343.storage.command.StorageTabCompleter;
import me.homas343.storage.listeners.InventoryClickListener;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {
    private static Core instance;
    private ConnectManager connectManager;
    private PaginationManager paginationManager;

    @Override
    public void onEnable() {
        instance = this;
        paginationManager = new PaginationManager();
        ConsoleCommandSender c = Bukkit.getConsoleSender();

        getCommand("storage").setExecutor(new StorageCommand());
        getCommand("storage").setTabCompleter(new StorageTabCompleter());
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        saveDefaultConfig();

        connectManager = new ConnectManager();
        connectManager.connect();

        if (connectManager.isConnected()) c.sendMessage("§6[Storage] §7| §fПодключено к базе данных");
        else c.sendMessage("§6[Storage] §7| §cНе удалось подключиться к базе данных");
    }

    @Override
    public void onDisable() {
        ConsoleCommandSender c = Bukkit.getConsoleSender();
        if (connectManager != null && connectManager.isConnected()) {
            connectManager.disconnect();
            c.sendMessage("§6[Storage] §7| §fОтключено от базы данных");
        }
    }

    public static Core getInstance() {
        return instance;
    }
    public ConnectManager getConnectManager() {
        return connectManager;
    }
    public PaginationManager getPaginationManager() {
        return paginationManager;
    }
}
