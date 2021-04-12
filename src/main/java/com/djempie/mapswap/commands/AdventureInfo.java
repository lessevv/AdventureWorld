package com.djempie.mapswap.commands;

import com.djempie.mapswap.MapSwap;
import com.djempie.mapswap.adventure.Adventure;
import com.djempie.mapswap.adventure.AdventureManager;
import com.djempie.mapswap.adventure.AdventurePlayer;
import com.djempie.mapswap.adventure.AdventureTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdventureInfo implements @Nullable CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // get teamName & error checking
        if(args.length <= 0) { return false; }
        String adventureName = args[0].trim();
        // get adventure manager
        AdventureManager manager = MapSwap.getInstance().getAdventureManager();
        // create dummy adventure
        Adventure adventure = null;
        if(adventureName.isEmpty()) {
            if(sender instanceof Player) {
                // get player's instance
                AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
                // get player's team
                AdventureTeam team = player.getCurrentTeam();
                // player is not in a team
                if(team == null) {
                    sender.sendMessage(ChatColor.RED + "Not in a team.");
                    return false;
                }
                // get current player's current adventure
                adventure = team.getCurrentAdventure();
                if(adventure == null) {
                    sender.sendMessage(ChatColor.RED + "Not currently in an adventure.");
                    return false;
                }
            }
            else {
                // invalid player instance
                sender.sendMessage(ChatColor.RED + "Must be a player to execute this command without an adventure name.");
                return false;
            }
        }
        else {
            // get adventure in progress
            adventure = manager.getAdventure(adventureName);
        }
        // no adventure found
        if(adventure == null) {
            sender.sendMessage(ChatColor.RED + "No adventure found for name: " + adventureName);
            return false;
        }
        // start printing adventure info
        sender.sendMessage(ChatColor.RED + "Info for " + adventure.getName() + ":");
        AdventureTeam[] teams = adventure.getAdventureTeams();
        sender.sendMessage(ChatColor.GRAY + "    - Teams ( " + teams.length + " ):");
        for(AdventureTeam team : teams) {
            sender.sendMessage(ChatColor.GRAY + "        - " + team.getName());
        }
        // return valid command
        return true;
    }
}
