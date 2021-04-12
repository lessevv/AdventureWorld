package com.djempie.mapswap.adventure;

public abstract class AdventureInstance {

	private AdventureTeam team;
	private Adventure adventure;
	
	public AdventureTeam getTeam() {
		return this.team;
	}
	
	public Adventure getAdventure() {
		return this.adventure;
	}
	
	protected abstract void startAdventure();
	
	protected abstract void leaveAdventure(AdventurePlayer player);
	
	protected abstract boolean isPlayerInAdventure(AdventurePlayer player);
	
	protected abstract void destroyInstance();
	
	AdventureInstance setTeam(AdventureTeam team) {
		this.team = team;
		
		return this;
	}
	
	AdventureInstance setAdventure(Adventure adventure) {
		this.adventure = adventure;
		
		return this;
	}
}
