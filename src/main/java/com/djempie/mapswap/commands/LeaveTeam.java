package com.djempie.mapswap.commands;

import com.djempie.mapswap.MapSwap;
import com.djempie.mapswap.adventure.AdventureManager;
import com.djempie.mapswap.adventure.AdventurePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LeaveTeam implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // get AdventureManager
        AdventureManager manager = MapSwap.getInstance().getAdventureManager();
        // get AdventurePlayer
        AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
        // set player's team to null
        player.joinTeam(null);
        // return valid command
        return true;
    }
}
