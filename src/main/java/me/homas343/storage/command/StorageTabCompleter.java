package me.homas343.storage.command;

import me.homas343.storage.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class StorageTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("items");
            completions.add("player");
            completions.add("save");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
            completions.addAll(Core.getInstance().getConnectManager().getPlayersInTable());
        }

        return completions;
    }
}
