package io.nightworks.adventureWorld.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import io.nightworks.adventureWorld.bukkit.BukkitPlugin;
import io.nightworks.adventureWorld.common.Adventure;
import io.nightworks.adventureWorld.common.AdventureManager;
import io.nightworks.adventureWorld.common.AdventurePlayer;
import io.nightworks.adventureWorld.common.AdventureTeam;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@CommandAlias("adventureworld|aw")
public class CommandAdventureWorld extends BaseCommand {
    @Subcommand("start")
    public void start(Player sender, String adventureName) {
        AdventureManager manager = BukkitPlugin.getInstance().getAdventureManager();
        AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
        AdventureTeam team = player.getCurrentTeam();

        if(team == null) {
            player.sendMessage("You are not a part of any adventure team.");
            return;
        }

        if(team.getLeader() != player) {
            player.sendMessage("You are not the leader of your team.");
            return;
        }

        if(team.getCurrentAdventure() != null) {
            player.sendMessage("You are currently in an adventure. You must leave the current adventure before joining a new adventure.");
            return;
        }

        Adventure adventure = manager.getAdventure(adventureName);
        if(adventure == null) {
            player.sendMessage("No adventure found for name: " + adventureName);
            return;
        }

        team.startAdventure(adventure);
    }

    @Subcommand("stop")
    public void stop(Player sender) {
        AdventureManager manager = BukkitPlugin.getInstance().getAdventureManager();
        AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
        AdventureTeam team = player.getCurrentTeam();

        if(team == null || team.getCurrentAdventure() == null) {
            player.sendMessage("You are not currently in an adventure.");
            return;
        }

        if(team.getLeader() != player) {
            player.sendMessage("You are not the leader of your team.");
            return;
        }

        team.startAdventure(null);
    }

    @Subcommand("info")
    public void info(CommandSender sender, String adventureName) {
        AdventureManager manager = BukkitPlugin.getInstance().getAdventureManager();

        Adventure adventure = null;
        if(adventureName.isEmpty()) {
            if(sender instanceof Player) {
                AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
                AdventureTeam team = player.getCurrentTeam();
                if(team == null) {
                    sender.sendMessage(ChatColor.RED + "Not in a team.");
                    return;
                }

                adventure = team.getCurrentAdventure();
                if(adventure == null) {
                    sender.sendMessage(ChatColor.RED + "Not currently in an adventure.");
                    return;
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Must be a player to execute this command without an adventure name.");
                return;
            }
        }
        else {
            adventure = manager.getAdventure(adventureName);
        }

        if(adventure == null) {
            sender.sendMessage(ChatColor.RED + "No adventure found for name: " + adventureName);
            return;
        }

        sender.sendMessage(ChatColor.RED + "Info for " + adventure.getName() + ":");

        AdventureTeam[] teams = adventure.getAdventureTeams();
        sender.sendMessage(ChatColor.GRAY + "    - Teams ( " + teams.length + " ):");
        for(AdventureTeam team : teams) {
            sender.sendMessage(ChatColor.GRAY + "        - " + team.getName());
        }
    }

    @Subcommand("list")
    public void list(CommandSender sender) {
        AdventureManager manager = BukkitPlugin.getInstance().getAdventureManager();

        Adventure[] adventures = manager.getAdventures();
        sender.sendMessage(ChatColor.RED + "Adventures ( " + adventures.length + " ):");

        for(Adventure adventure : adventures) {
            sender.sendMessage(ChatColor.GRAY + "    - " + adventure.getName() + " ( " + adventure.getAdventureTeams().length + " )");
        }

    }

    @NotNull
    private static LinkedHashMap<String, Object> getAdventureYamlFile(String adventureName) {
        LinkedHashMap<String, Object> rootYaml = new LinkedHashMap<String, Object>();
        LinkedHashMap<String, Object> worldsYaml = new LinkedHashMap<String, Object>();
        LinkedHashMap<String, String> normalYaml = new LinkedHashMap<String, String>();

        normalYaml.put("name", adventureName);

        worldsYaml.put("entry", "NORMAL");
        worldsYaml.put("NORMAL", normalYaml);

        rootYaml.put("name", adventureName);
        rootYaml.put("worlds", worldsYaml);

        return rootYaml;
    }

    @Subcommand("team")
    public class Team extends BaseCommand {
        @Subcommand("create")
        public void create(CommandSender sender, String teamName) {
            teamName = teamName.trim();
            if(teamName.isEmpty()) {
                sender.sendMessage("No name has been given");
                return;
            }

            AdventureManager manager = BukkitPlugin.getInstance().getAdventureManager();

            AdventurePlayer player = manager.getAdventurePlayer(sender.getName());

            if(manager.getAdventureTeam(teamName) != null) {
                player.sendMessage("Adventure team already exists with name: " + teamName);
                return;
            }
            manager.createTeam(teamName, player);

            player.sendMessage("Created adventure team with name: " + teamName);
        }

        @Subcommand("leave")
        public void leave(Player sender) {
            AdventureManager manager = BukkitPlugin.getInstance().getAdventureManager();

            AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
            player.joinTeam(null);
        }

        @Subcommand("join")
        public void join(Player sender, String teamName) {
            AdventureManager manager = BukkitPlugin.getInstance().getAdventureManager();
            AdventurePlayer player = manager.getAdventurePlayer(sender.getName());

            AdventureTeam team = manager.getAdventureTeam(teamName);
            if(team == null) {
                player.sendMessage("No team found for name: " + teamName);
                return;
            }

            player.joinTeam(team);
        }

        @Subcommand("list")
        public void list(CommandSender sender) {
            AdventureManager manager = BukkitPlugin.getInstance().getAdventureManager();

            AdventureTeam[] teams = manager.getAdventureTeams();
            sender.sendMessage(ChatColor.RED + "Adventure Teams ( " + teams.length + " ):");

            for(AdventureTeam team : teams) {
                sender.sendMessage(ChatColor.GRAY + "    - " + team.getName() + " ( " + team.getTeammates().length + " )");
            }
        }

        @Subcommand("info")
        public void info(CommandSender sender, String teamName) {
            AdventureManager manager = BukkitPlugin.getInstance().getAdventureManager();


            AdventureTeam team = null;
            if(teamName.isEmpty()) {
                if(sender instanceof Player) {
                    AdventurePlayer player = manager.getAdventurePlayer(sender.getName());
                    team = player.getCurrentTeam();

                    if(team == null) {
                        sender.sendMessage(ChatColor.RED + "Not in a team.");
                        return;
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Must be a player to issue command without team name.");
                    return;
                }
            }
            else {
                team = manager.getAdventureTeam(teamName);
            }

            if(team == null) {
                sender.sendMessage(ChatColor.RED + "No team found for name: " + teamName);
                return;
            }

            sender.sendMessage(ChatColor.RED + "Info for team " + team.getName() + ":");
            String adventureName = "None";
            if(team.getCurrentAdventure() != null) {
                adventureName = team.getCurrentAdventure().getName();
            }
            sender.sendMessage(ChatColor.GRAY + "    - Adventure: " + adventureName);

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
        }
    }
}
