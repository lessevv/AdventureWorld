package me.smith_61.adventure.common;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class AdventureManager {

	private final Map<String, Adventure> adventures;
	private final Map<String, AdventurePlayer> adventurePlayers;
	private final Map<String, AdventureTeam> adventureTeams;
	
	public AdventureManager() {
		this.adventures = new HashMap<String, Adventure>();
		this.adventurePlayers = new HashMap<String, AdventurePlayer>();
		this.adventureTeams = new HashMap<String, AdventureTeam>();
	}
	
	
	public Adventure getAdventure(String name) {
		return this.adventures.get(name.trim().toLowerCase());
	}
	
	public void addAdventure(Adventure adventure) {
		String name = adventure.getName().trim().toLowerCase();
		
		if(this.adventures.containsKey(name)) {
			throw new IllegalArgumentException("Adventure already exists with name: " + name);
		}
		
		AdventureLogger.logf(Level.INFO, "Added adventure: %s", name);
		this.adventures.put(name, adventure);
	}
	
	public Adventure[] getAdventures() {
		return this.adventures.values().toArray(new Adventure[0]);
	}
	
	public void removeAdventure(Adventure adventure) {
		String name = adventure.getName().trim().toLowerCase();
		
		if(this.adventures.get(name) != adventure) {
			throw new IllegalArgumentException("Adventure is not in this manager");
		}
		
		for(AdventureTeam team : adventure.getAdventureTeams()) {
			adventure.stopAdventure(team);
		}
		
		AdventureLogger.logf(Level.INFO, "Removed adventure: %s", name);
		this.adventures.remove(name);
	}
	
	public AdventurePlayer getAdventurePlayer(String name) {
		return this.adventurePlayers.get(name.trim().toLowerCase());
	}
	
	public AdventurePlayer[] getAdventurePlayers() {
		return this.adventurePlayers.values().toArray(new AdventurePlayer[0]);
	}
	
	public void addAdventurePlayer(AdventurePlayer player) {
		String name = player.getName().trim().toLowerCase();
		
		if(this.adventurePlayers.containsKey(name)) {
			throw new IllegalArgumentException("AdventurePlayer already exists with name: " + name);
		}
		
		AdventureLogger.logf(Level.INFO, "Added player: %s", name);
		this.adventurePlayers.put(name, player);
	}
	
	public void removeAdventurePlayer(AdventurePlayer player) {
		String name = player.getName().trim().toLowerCase();
		
		if(this.adventurePlayers.get(name) != player) {
			throw new IllegalArgumentException("AdventurePlayer is not a part of this manager");
		}
		
		if(player.getCurrentTeam() != null) {
			player.joinTeam(null);
		}
		
		AdventureLogger.logf(Level.INFO, "Removed player: %s", name);
		this.adventurePlayers.remove(name);
	}
	
	public AdventureTeam getAdventureTeam(String name) {
		return this.adventureTeams.get(name.trim().toLowerCase());
	}
	
	public AdventureTeam[] getAdventureTeams() {
		return this.adventureTeams.values().toArray(new AdventureTeam[0]);
	}
	
	public AdventureTeam createTeam(String name, AdventurePlayer leader) {
		name = name.trim().toLowerCase();
		
		if(this.adventureTeams.containsKey(name)) {
			throw new IllegalArgumentException("AdventureTeam already exists with name: " + name);
		}
		
		AdventureLogger.logf(Level.INFO, "Created team: %s with leader: %s", name, leader.getName());
		
		AdventureTeam team = new AdventureTeam(this, name, leader);
		this.adventureTeams.put(name, team);
		
		return team;
	}
	
	void removeTeam(AdventureTeam team) {
		this.adventureTeams.remove(team.getName().trim().toLowerCase());
	}
}
