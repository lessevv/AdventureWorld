package com.djempie.mapswap.commands;

import com.djempie.mapswap.MapSwap;
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

public class TeamInfo implements @Nullable CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // get teamName & error checking
        if(args.length <= 0) { return false; }
        String teamName = args[0].trim();
        // get AdventureManager
        AdventureManager manager = MapSwap.getInstance().getAdventureManager();
        // create dummy team
        AdventureTeam team = null;
        if(teamName.isEmpty()) {
            if(sender instanceof Player) {
                // get current player
                AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
                // get player's team
                team = player.getCurrentTeam();
                // error checking
                if(team == null) {
                    sender.sendMessage(ChatColor.RED + "Not in a team.");
                    return false;
                }
            }
            else {
                // player not valid, send message
                sender.sendMessage(ChatColor.RED + "Must be a player to issue command without team name.");
                return false;
            }
        }
        else {
            // get default team ????
            team = manager.getAdventureTeam(teamName);
        }
        // no team found
        if(team == null) {
            sender.sendMessage(ChatColor.RED + "No team found for name: " + teamName);
            return false;
        }
        // start printing team info
        sender.sendMessage(ChatColor.RED + "Info for team " + team.getName() + ":");
        // start printing adventure info
        String adventureName = "None";
        if(team.getCurrentAdventure() != null) {
            adventureName = team.getCurrentAdventure().getName();
        }
        sender.sendMessage(ChatColor.GRAY + "    - Adventure: " + adventureName);
        // start printing players info
        AdventurePlayer[] players = team.getTeammates();
        sender.sendMessage(ChatColor.GRAY + "    - Players( " + players.length + " ):");
        for(AdventurePlayer player : players) {
            if(player == team.getLeader()) {
                sender.sendMessage(ChatColor.GRAY + "        - " + player.getName() + " (Leader)");
            }
            else {
                sender.sendMessage(ChatColor.GRAY + "        - " + player.getName());
            }
        }
        // return valid command
        return true;
    }
}
