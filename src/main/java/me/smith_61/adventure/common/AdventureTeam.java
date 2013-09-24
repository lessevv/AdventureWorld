package me.smith_61.adventure.common;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class AdventureTeam {

	private final AdventureManager manager;
	private final String name;
	private final AdventurePlayer leader;
	
	private final List<AdventurePlayer> teammates;
	
	private AdventureInstance curAdventure;
	
	AdventureTeam(AdventureManager manager, String name, AdventurePlayer leader) {
		this.manager = Preconditions.checkNotNull(manager, "manager");
		this.name = Preconditions.checkNotNull(name, "name");
		this.leader = Preconditions.checkNotNull(leader, "leader");
		
		this.teammates = new ArrayList<AdventurePlayer>();
		this.addPlayer(leader);
		
		this.curAdventure = null;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final AdventurePlayer getLeader() {
		return this.leader;
	}
	
	public final AdventurePlayer[] getTeammates() {
		return this.teammates.toArray(new AdventurePlayer[0]);
	}
	
	public final void addPlayer(AdventurePlayer player) {
		if(this.teammates.contains(player)) {
			return;
		}
		
		if(player.getCurrentTeam() != null) {
			player.getCurrentTeam().removePlayer(player);
		}
		
		this.teammates.add(player);
		player.setCurrentTeam(this);
		
		player.sendMessage("Joined team: " + this.getName());
	}
	
	public final void removePlayer(AdventurePlayer player) {
		if(!this.teammates.contains(player)) {
			return;
		}
		
		if(this.curAdventure != null && this.curAdventure.isPlayerInAdventure(player)) {
			player.leaveAdventure(this.curAdventure);
		}
		
		this.teammates.remove(player);
		player.setCurrentTeam(null);
		
		player.sendMessage("Left team: " + this.getName());
		
		if(player == this.leader) {
			this.joinAdventure(null);
			
			for(AdventurePlayer other : this.getTeammates()) {
				this.removePlayer(other);
			}
			
			this.manager.removeTeam(this);
		}
	}
	
	public final void joinAdventure(Adventure adventure) {
		if(adventure == null) {
			if(this.curAdventure != null) {
				this.curAdventure.getAdventure().stopAdventure(this);
			}
			this.curAdventure = null;
		}
		else {
			adventure.startAdventure(this);
		}
	}
	
	public final Adventure getCurrentAdventure() {
		if(this.curAdventure != null) {
			return this.curAdventure.getAdventure();
		}
		return null;
	}
	
	final void setCurrentAdventure(AdventureInstance adventure) {
		this.curAdventure = adventure;
	}
}
