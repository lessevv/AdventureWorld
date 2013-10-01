package me.smith_61.adventure.bukkit;

import java.io.File;
import java.util.logging.Level;

import me.smith_61.adventure.bukkit.commands.CommandAdventure;
import me.smith_61.adventure.common.Adventure;
import me.smith_61.adventure.common.AdventureLogger;
import me.smith_61.adventure.common.AdventureManager;
import me.smith_61.adventure.common.AdventurePlayer;
import me.smith_61.adventure.common.AdventureTeam;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import se.ranzdo.bukkit.methodcommand.CommandHandler;

public class BukkitPlugin extends JavaPlugin {

	private static BukkitPlugin INSTANCE;
	
	public static BukkitPlugin getInstance() {
		return BukkitPlugin.INSTANCE;
	}
	
	private CommandHandler commandHandler;
	
	private AdventureManager adventureManager;
	private World lobbyWorld;
	
	public AdventureManager getAdventureManager() {
		return this.adventureManager;
	}
	
	public World getLobbyWorld() {
		return this.lobbyWorld;
	}
	
	public File getWorldSaveDir() {
		return this.getLobbyWorld().getWorldFolder().getParentFile();
	}
	
	@Override
	public void onLoad() {
		AdventureLogger.setLogger(this.getLogger());
	}
	
	@Override
	public void onEnable() {
		BukkitPlugin.INSTANCE = this;
		
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
						AdventureLogger.logf(Level.SEVERE, ale, "Error reading in adventure from file: %s", adventureFile.getName());
					}
				}
			}
		}
		
		this.commandHandler = new CommandHandler(this);
		this.commandHandler.registerCommands(new CommandAdventure());
		
		Bukkit.getPluginManager().registerEvents(new Listener(), this);
	}
	
	@Override
	public void onDisable() {
		this.commandHandler = null;
		
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
		BukkitPlugin.INSTANCE = null;
	}
	
	
}
