package com.djempie.mapswap.commands;

import com.djempie.mapswap.adventure.AdventureLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.String;
import java.util.logging.Level;

import com.djempie.mapswap.MapSwap;
import com.djempie.mapswap.adventure.AdventureManager;
import com.djempie.mapswap.adventure.AdventurePlayer;

public class CreateGroup implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // get teamName & error checking
        if(args.length <= 0) {
            AdventureLogger.logf(Level.SEVERE, "no arguments were passed");
            Bukkit.broadcastMessage("no args passed");
            return false;
        }
        String teamName = args[0].trim();
        if(teamName.isEmpty()) {
            AdventureLogger.logf(Level.SEVERE, "teamname empty");
            Bukkit.broadcastMessage("teamname is empty");
            return false;
        }
        // get AdventureManager
        AdventureManager manager = MapSwap.getInstance().getAdventureManager();
        if(manager == null) {
            AdventureLogger.logf(Level.SEVERE, "adventuremanager is null");
            Bukkit.broadcastMessage("adventuremanager is null");
            return false;
        }
        // get player
        AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
        if(player == null) {
            AdventureLogger.logf(Level.SEVERE, "player is null");
            Bukkit.broadcastMessage("player is null");
            return false;
        }
        // check if teamName exists
        if(manager.getAdventureTeam(teamName) != null) {
            player.sendMessage("Adventure team already exists with name: " + teamName);
            return false;
        }
        // create team
        manager.createTeam(teamName, player);
        // send message
        player.sendMessage("Created adventure team with name: " + teamName);
        // return valid command
        return true;
    }
}