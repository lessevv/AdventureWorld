package com.djempie.mapswap.commands;

import com.djempie.mapswap.MapSwap;
import com.djempie.mapswap.adventure.AdventureManager;
import com.djempie.mapswap.adventure.AdventureTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ListTeams implements @Nullable CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // get AdventureManager
        AdventureManager manager = MapSwap.getInstance().getAdventureManager();
        // get all teams
        AdventureTeam[] teams = manager.getAdventureTeams();
        // announce teams listing
        sender.sendMessage(ChatColor.RED + "Adventure Teams ( " + teams.length + " ):");
        // list all teams
        for(AdventureTeam team : teams) {
            sender.sendMessage(ChatColor.GRAY + "    - " + team.getName() + " ( " + team.getTeammates().length + " )");
        }
        // return valid command
        return true;
    }
}
