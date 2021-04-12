package com.djempie.mapswap.commands;

import com.djempie.mapswap.MapSwap;
import com.djempie.mapswap.adventure.AdventureManager;
import com.djempie.mapswap.adventure.AdventurePlayer;
import com.djempie.mapswap.adventure.AdventureTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.checkerframework.framework.qual.QualifierArgument;
import org.jetbrains.annotations.NotNull;

public class JoinTeam implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // get teamName & error checking
        if(args.length <= 0) { return false; }
        String teamName = args[0].trim();
        if(teamName.isEmpty()) { return false; }
        // get AdventureManager
        AdventureManager manager = MapSwap.getInstance().getAdventureManager();
        // get AdventurePlayer
        AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
        // get AdventureTeam && error checking
        AdventureTeam team = manager.getAdventureTeam(teamName);
        if(team == null) {
            player.sendMessage("No team found for name: " + teamName);
            return false;
        }
        // join team
        player.joinTeam(team);
        // return valid command
        return true;
    }
}
