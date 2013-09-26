package me.smith_61.adventure.bukkit;

import java.util.logging.Level;

import me.smith_61.adventure.bukkit.commands.CommandAdventure;
import me.smith_61.adventure.common.Adventure;
import me.smith_61.adventure.common.AdventureLogger;
import me.smith_61.adventure.common.AdventureManager;
import me.smith_61.adventure.common.AdventurePlayer;
import me.smith_61.adventure.common.AdventureTeam;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
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
		
		ConfigurationSection adventureSection = this.getConfig().getConfigurationSection("adventures");
		for(String name : adventureSection.getKeys(false)) {
			ConfigurationSection section = adventureSection.getConfigurationSection(name);
			
			String worldName = section.getString("world");
			World world = null;
			if(worldName == null || (world = Bukkit.getWorld(worldName)) == null) {
				AdventureLogger.logf(Level.WARNING, "Invalid world name: %s", worldName);
				continue;
			}
			
			this.adventureManager.addAdventure(new BukkitAdventure(name, world));
		}
		
		this.getServer().getPluginManager().registerEvents(new Listener(), this);
		for(Player player : this.getServer().getOnlinePlayers()) {
			this.adventureManager.addAdventurePlayer(new BukkitAdventurePlayer(player));
		}
		
		this.commandHandler = new CommandHandler(this);
		this.commandHandler.registerCommands(new CommandAdventure());
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
