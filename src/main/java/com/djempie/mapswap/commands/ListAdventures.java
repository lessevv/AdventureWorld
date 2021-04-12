package com.djempie.mapswap.commands;

import com.djempie.mapswap.MapSwap;
import com.djempie.mapswap.adventure.Adventure;
import com.djempie.mapswap.adventure.AdventureManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ListAdventures implements @Nullable CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // get AdventureManager
        AdventureManager manager = MapSwap.getInstance().getAdventureManager();
        // get all adventures
        Adventure[] adventures = manager.getAdventures();
        sender.sendMessage(ChatColor.RED + "Adventures ( " + adventures.length + " ):");
        // print adventures
        for(Adventure adventure : adventures) {
            sender.sendMessage(ChatColor.GRAY + "    - " + adventure.getName() + " ( " + adventure.getAdventureTeams().length + " )");
        }
        // return valid command
        return true;
    }
}
