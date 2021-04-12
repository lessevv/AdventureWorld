package com.djempie.mapswap;

import com.djempie.mapswap.adventure.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.djempie.mapswap.commands.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class MapSwap extends JavaPlugin {
    // instance
    private static MapSwap INSTANCE;
    public static MapSwap getInstance() {
        return MapSwap.INSTANCE;
    }
    // adventureManager
    private AdventureManager adventureManager;
    public AdventureManager getAdventureManager() {
        return this.adventureManager;
    }
    // thread
    private Thread mainThread;
    public boolean isMainThread() {
        return this.mainThread == Thread.currentThread();
    }
    // world
    private World lobbyWorld;
    public World getLobbyWorld() {
        return this.lobbyWorld;
    }
    public File getWorldSaveDir() {
        return this.getLobbyWorld().getWorldFolder().getParentFile();
    }

    @Override
    public void onLoad() {
        Logger logger = this.getLogger();
        AdventureLogger.setLogger(logger);

        try {
            FileHandler handler = new FileHandler(this.getDataFolder() + "/AdventurePlugin.log");
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        }
        catch(IOException ioe) {
            AdventureLogger.log(Level.SEVERE, "Error setting up logger output file.", ioe);
        }
    }

    @Override
    public void onEnable() {
        // init instance
        MapSwap.INSTANCE = this;
        this.mainThread = Thread.currentThread();
        this.adventureManager = new AdventureManager();
        // set lobby
        String lobbyWorldName = this.getConfig().getString("lobby-world", "world");
        if((this.lobbyWorld = Bukkit.getWorld(lobbyWorldName)) == null) {
            this.lobbyWorld = Bukkit.getWorlds().get(0);
        }
        // get adventures
        File adventuresDir = new File(this.getDataFolder(), "adventures");
        if(!adventuresDir.exists()) {
            adventuresDir.mkdirs();
        }
        // init adventures
        if(adventuresDir.isDirectory()) {
            // for all files in the adventure directory
            for(File adventureFile : adventuresDir.listFiles()) {
                // if found file is a file and ends with .zuip
                if(adventureFile.isFile() && adventureFile.getName().endsWith(".zip")) {
                    try {
                        Adventure adventure = BukkitAdventure.loadAdventure(adventureFile);

                        this.adventureManager.addAdventure(adventure);
                    }
                    catch(IllegalArgumentException iae) {
                        AdventureLogger.log(Level.SEVERE, "Adventure already exists.", iae);
                    }
                    catch(AdventureLoadException ale) {
                        if(ale.getCause() != null) {
                            AdventureLogger.logf(Level.SEVERE, "Error reading in adventure from file: %s", adventureFile.getName());
                        }
                        else {
                            AdventureLogger.logf(Level.SEVERE, "Error reading in adventure from file: %s. Reason: %s", adventureFile.getName(), ale.getMessage());
                        }
                    }
                }
            }
        }
        else {
            AdventureLogger.logf(Level.SEVERE, "'%s' is not a directory.", adventuresDir);
        }
        CreateGroup group = new CreateGroup();
        // initialize commands
        this.getCommand("createGroup").setExecutor(group);
        this.getCommand("leaveTeam").setExecutor(new LeaveTeam());
        this.getCommand("joinTeam").setExecutor(new JoinTeam());
        this.getCommand("listTeams").setExecutor(new ListTeams());
        this.getCommand("teamInfo").setExecutor(new TeamInfo());
        this.getCommand("startAdventure").setExecutor(new StartAdventure());
        this.getCommand("stopAdventure").setExecutor(new StopAdventure());
        this.getCommand("listAdventures").setExecutor(new ListAdventures());
        this.getCommand("adventureInfo").setExecutor(new AdventureInfo());
        // register event listeners
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
    }

    @Override
    public void onDisable() {
        for(Adventure adventure : this.adventureManager.getAdventures()) {
            this.adventureManager.removeAdventure(adventure);
        }

        for(AdventureTeam team : this.adventureManager.getAdventureTeams()) {
            team.dissolveTeam();
        }

        for(AdventurePlayer player : this.adventureManager.getAdventurePlayers()) {
            this.adventureManager.removeAdventurePlayer(player);
        }

        this.lobbyWorld = null;
        this.adventureManager = null;
        this.mainThread = null;
        MapSwap.INSTANCE = null;
    }
}
