package com.djempie.mapswap.commands;

import com.djempie.mapswap.MapSwap;
import com.djempie.mapswap.adventure.Adventure;
import com.djempie.mapswap.adventure.AdventureManager;
import com.djempie.mapswap.adventure.AdventurePlayer;
import com.djempie.mapswap.adventure.AdventureTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StartAdventure implements @Nullable CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {// get teamName & error checking
        if(args.length <= 0) { return false; }
        String adventureName = args[0].trim();
        if(adventureName.isEmpty()) { return false; }
        // get adventure manager
        AdventureManager manager = MapSwap.getInstance().getAdventureManager();
        // get adventure player
        AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
        // get player's team
        AdventureTeam team = player.getCurrentTeam();
        // player is not part of a team
        if(team == null) {
            player.sendMessage("You are not a part of any adventure team.");
            return false;
        }
        // player isn't a leader
        if(team.getLeader() != player) {
            player.sendMessage("You are not the leader of your team.");
            return false;
        }
        // adventure already in progress
        if(team.getCurrentAdventure() != null) {
            player.sendMessage("You are currently in an adventure. You must leave the current adventure before joining a new adventure.");
            return false;
        }
        // adventure invalid
        Adventure adventure = manager.getAdventure(adventureName);
        if(adventure == null) {
            player.sendMessage("No adventure found for name: " + adventureName);
            return false;
        }
        // start new adventure
        team.startAdventure(adventure);
        // return valid command
        return true;
    }
}
