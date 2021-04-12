package com.djempie.mapswap.adventure;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.google.common.base.Preconditions;

public class AdventureTeam {

	private final AdventureManager manager;
	private final String name;
	private final AdventurePlayer leader;
	
	private final List<AdventurePlayer> teammates; // Edits and gets synchronized on this field
	private volatile boolean isDissolved; // Edits synchronized on field "teammates"
	
	private final Object ADV_LOCK = new Object();
	private AdventureInstance adventureInstance; // Synchronized on field "ADV_LOCK"
	
	AdventureTeam(AdventureManager manager, String name, AdventurePlayer leader) {
		this.manager = Preconditions.checkNotNull(manager, "manager");
		this.name = Preconditions.checkNotNull(name, "name");
		this.leader = Preconditions.checkNotNull(leader, "leader");
		
		this.teammates = new ArrayList<AdventurePlayer>();
		leader.joinTeam(this);
		
		this.adventureInstance = null;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final AdventurePlayer getLeader() {
		return this.leader;
	}
	
	public final AdventurePlayer[] getTeammates() {
		synchronized(this.teammates) {
			return this.teammates.toArray(new AdventurePlayer[0]);
		}
	}
	
	public final void startAdventure(Adventure newAdventure) {
		if(this.isDissolved() && newAdventure != null) {
			AdventureLogger.logf(Level.SEVERE, "Team does not exists or new adventure is null.");
			return;
		}
		synchronized(this.ADV_LOCK) {
			Adventure curAdventure = this.getCurrentAdventure();
			if(curAdventure == newAdventure) {
				AdventureLogger.logf(Level.SEVERE, "Adventure already in progress.");
				return;
			}
			AdventureLogger.logf(Level.INFO, "Copying adventure...(%s)", newAdventure.getName());
			if(this.adventureInstance != null) {
				// destroy current adventure and set to null
				this.adventureInstance.destroyInstance();
				this.adventureInstance = null;
			}
			if(newAdventure != null) {
				// start new
				this.adventureInstance = newAdventure.startAdventure(this);
				this.adventureInstance.startAdventure();
			}
			AdventureLogger.logf(Level.INFO, "...done(%s)", this.adventureInstance.getAdventure().getName());
		}
	}
	
	public final Adventure getCurrentAdventure() {
		if(this.isDissolved()) {
			return null;
		}
		synchronized(this.ADV_LOCK) {
			if(this.adventureInstance != null) {
				return this.adventureInstance.getAdventure();
			}
			return null;
		}
	}
	
	public final void dissolveTeam() {
		AdventurePlayer[] players;
		
		synchronized(this.teammates) {
			if(this.isDissolved()) {
				return;
			}
			this.isDissolved = true;
			
			players = this.getTeammates();
			
			this.manager.removeTeam(this);
		}
		AdventureLogger.logf(Level.INFO, "Disolved team: %s", this.getName());
		
		this.startAdventure(null);
		
		for(AdventurePlayer player : players) {
			if(player.leaveTeam(this)) {
				player.sendMessage("Left team: " + this.getName());
			}
		}
	}
	
	public final boolean isDissolved() {
		return this.isDissolved;
	}
	
	final void addPlayer(AdventurePlayer player) {
		synchronized(this.teammates) {
			if(this.isDissolved() || this.teammates.contains(player)) {
				return;
			}
			
			this.teammates.add(player);
			player.sendMessage("Joined team: " + this.getName());
		}
	}
	
	final void removePlayer(AdventurePlayer player) {
		if(player == this.leader && !this.isDissolved()) {
			this.dissolveTeam();
			return;
		}
		synchronized(this.teammates) {
			if(this.teammates.remove(player)) {
				player.sendMessage("Left team: " + this.getName());
			}
			else {
				return;
			}
		}
		player.leaveTeam(this);
		
		synchronized(this.ADV_LOCK) {
			if(this.adventureInstance.isPlayerInAdventure(player)) {
				this.adventureInstance.leaveAdventure(player);
			}
		}
	}
	
	final boolean leaveAdventure(AdventureInstance instance) {
		synchronized(this.ADV_LOCK) {
			if(this.adventureInstance == instance) {
				this.startAdventure(null);
				return true;
			}
			return false;
		}
	}
}
