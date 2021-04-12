package com.djempie.mapswap.commands;

import com.djempie.mapswap.MapSwap;
import com.djempie.mapswap.adventure.AdventureManager;
import com.djempie.mapswap.adventure.AdventurePlayer;
import com.djempie.mapswap.adventure.AdventureTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StopAdventure implements @Nullable CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // get AdventureManager
        AdventureManager manager = MapSwap.getInstance().getAdventureManager();
        // get AdventurePlayer
        AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
        // get AdventureTeam
        AdventureTeam team = player.getCurrentTeam();
        // no adventure in progress
        if(team == null || team.getCurrentAdventure() == null) {
            player.sendMessage("You are not currently in an adventure.");
            return false;
        }
        // player is not the leader
        if(team.getLeader() != player) {
            player.sendMessage("You are not the leader of your team.");
            return false;
        }
        // stop adventure
        team.startAdventure(null);
        // return valid command
        return true;
    }
}
