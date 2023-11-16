package io.nightworks.adventureWorld.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import co.aikar.commands.PaperCommandManager;
import io.nightworks.adventureWorld.bukkit.commands.CommandAdventureWorld;
import io.nightworks.adventureWorld.common.Adventure;
import io.nightworks.adventureWorld.common.AdventureLogger;
import io.nightworks.adventureWorld.common.AdventureManager;
import io.nightworks.adventureWorld.common.AdventurePlayer;
import io.nightworks.adventureWorld.common.AdventureTeam;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {

	private static BukkitPlugin INSTANCE;
	
	public static BukkitPlugin getInstance() {
		return BukkitPlugin.INSTANCE;
	}

	private AdventureManager adventureManager;
	private World lobbyWorld;
	private Thread mainThread;
	
	public AdventureManager getAdventureManager() {
		return this.adventureManager;
	}
	
	public World getLobbyWorld() {
		return this.lobbyWorld;
	}
	
	public File getWorldSaveDir() {
		return this.getLobbyWorld().getWorldFolder().getParentFile();
	}
	
	public boolean isMainThread() {
		return this.mainThread == Thread.currentThread();
	}
	
	@Override
	public void onLoad() {
		Logger logger = this.getLogger();
		AdventureLogger.setLogger(logger);
		
		try {
			FileHandler handler = new FileHandler(this.getDataFolder() + "/AdventureWorld.log");
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
		}
		catch(IOException ioe) {
			AdventureLogger.log(Level.SEVERE, "Error setting up logger output file.", ioe);
		}
	}
	
	@Override
	public void onEnable() {
		BukkitPlugin.INSTANCE = this;
		
		this.mainThread = Thread.currentThread();
		
		this.adventureManager = new AdventureManager();
		
		String lobbyWorldName = this.getConfig().getString("lobby-world", "world");
		if((this.lobbyWorld = Bukkit.getWorld(lobbyWorldName)) == null) {
			this.lobbyWorld = Bukkit.getWorlds().get(0);
		}
		
		File adventuresDir = new File(this.getDataFolder(), "adventures");
		if(!adventuresDir.exists()) {
			adventuresDir.mkdirs();
		}
		
		if(adventuresDir.isDirectory()) {
			for(File adventureFile : adventuresDir.listFiles()) {
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

		PaperCommandManager manager = new PaperCommandManager(this);
		manager.registerCommand(new CommandAdventureWorld());
		
		Bukkit.getPluginManager().registerEvents(new Listener(), this);
	}
	
	@Override
	public void onDisable() {
		//this.commandHandler = null;
		
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
		BukkitPlugin.INSTANCE = null;
	}
	
	
}
