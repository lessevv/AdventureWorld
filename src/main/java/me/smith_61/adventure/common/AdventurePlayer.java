package me.smith_61.adventure.common;

import com.google.common.base.Preconditions;

public abstract class AdventurePlayer {

	private final String name;
	
	private AdventureTeam curTeam;
	
	protected AdventurePlayer(String name) {
		this.name = Preconditions.checkNotNull(name, "name");
		
		this.curTeam = null;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void joinTeam(AdventureTeam team) {
		if(team == null) {
			if(this.curTeam != null) {
				this.curTeam.removePlayer(this);
			}
		}
		else {
			team.addPlayer(this);
		}
	}
	
	public AdventureTeam getCurrentTeam() {
		return this.curTeam;
	}
	
	public abstract void sendMessage(String message);
	
	protected abstract void joinAdventure(AdventureInstance adventure);
	
	protected abstract void leaveAdventure(AdventureInstance adventure);
	
	final void setCurrentTeam(AdventureTeam team) {
		this.curTeam = team;
	}
}
