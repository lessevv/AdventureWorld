package com.djempie.mapswap.adventure;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class AdventureManager {

	private final Map<String, Adventure> adventures; // Synchronized on self
	private final Map<String, AdventurePlayer> adventurePlayers; // Synchronized on self
	private final Map<String, AdventureTeam> adventureTeams; // Synchronized on self
	
	public AdventureManager() {
		this.adventures = new HashMap<String, Adventure>();
		this.adventurePlayers = new HashMap<String, AdventurePlayer>();
		this.adventureTeams = new HashMap<String, AdventureTeam>();
	}
	
	
	public Adventure getAdventure(String name) {
		synchronized(this.adventures) {
			return this.adventures.get(name.trim().toLowerCase());
		}
	}
	
	public void addAdventure(Adventure adventure) {
		String name = adventure.getName().trim().toLowerCase();
		
		synchronized(this.adventures) {
			if(this.adventures.containsKey(name)) {
				throw new IllegalArgumentException("Adventure already exists with name: " + name);
			}
			
			this.adventures.put(name, adventure);
		}
		AdventureLogger.logf(Level.INFO, "Added adventure: %s", name);
	}
	
	public Adventure[] getAdventures() {
		synchronized(this.adventures) {
			return this.adventures.values().toArray(new Adventure[0]);
		}
	}
	
	public void removeAdventure(Adventure adventure) {
		String name = adventure.getName().trim().toLowerCase();
		
		synchronized(this.adventures) {
			if(this.adventures.get(name) != adventure) {
				throw new IllegalArgumentException("Adventure is not in this manager");
			}
			
			for(AdventureTeam team : adventure.getAdventureTeams()) {
				team.startAdventure(null);
			}
			this.adventures.remove(name);
		}
		
		AdventureLogger.logf(Level.INFO, "Removed adventure: %s", name);
	}
	
	public AdventurePlayer getAdventurePlayer(String name) {
		synchronized(this.adventurePlayers) {
			return this.adventurePlayers.get(name.trim().toLowerCase());
		}
	}
	
	public AdventurePlayer[] getAdventurePlayers() {
		synchronized(this.adventurePlayers) {
			return this.adventurePlayers.values().toArray(new AdventurePlayer[0]);
		}
	}
	
	public void addAdventurePlayer(AdventurePlayer player) {
		String name = player.getName().trim().toLowerCase();
		
		synchronized(this.adventurePlayers) {
			if(this.adventurePlayers.containsKey(name)) {
				throw new IllegalArgumentException("AdventurePlayer already exists with name: " + name);
			}
			
			this.adventurePlayers.put(name, player);
		}
		
		AdventureLogger.logf(Level.INFO, "Added player: %s", name);
	}
	
	public void removeAdventurePlayer(AdventurePlayer player) {
		String name = player.getName().trim().toLowerCase();
		
		synchronized(this.adventurePlayers) {
			if(this.adventurePlayers.get(name) != player) {
				throw new IllegalArgumentException("AdventurePlayer is not a part of this manager");
			}
			
			player.joinTeam(null);
		
			this.adventurePlayers.remove(name);
		}
		
		AdventureLogger.logf(Level.INFO, "Removed player: %s", name);
	}
	
	public AdventureTeam getAdventureTeam(String name) {
		synchronized(this.adventureTeams) {
			return this.adventureTeams.get(name.trim().toLowerCase());
		}
	}
	
	public AdventureTeam[] getAdventureTeams() {
		synchronized(this.adventureTeams) {
			return this.adventureTeams.values().toArray(new AdventureTeam[0]);
		}
	}
	
	public AdventureTeam createTeam(String name, AdventurePlayer leader) {
		name = name.trim().toLowerCase();
		
		AdventureTeam team;
		synchronized(this.adventureTeams) {
			if(this.adventureTeams.containsKey(name)) {
				throw new IllegalArgumentException("AdventureTeam already exists with name: " + name);
			}
			
			team = new AdventureTeam(this, name, leader);
			this.adventureTeams.put(name, team);
		}
		
		AdventureLogger.logf(Level.INFO, "Created team: %s with leader: %s", name, leader.getName());
		
		return team;
	}
	
	void removeTeam(AdventureTeam team) {
		synchronized(this.adventureTeams) {
			this.adventureTeams.remove(team.getName().trim().toLowerCase());
		}
	}
}
