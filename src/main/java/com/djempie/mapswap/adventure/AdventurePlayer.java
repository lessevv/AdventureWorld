package com.djempie.mapswap.adventure;

import com.google.common.base.Preconditions;

public abstract class AdventurePlayer {

	private final String name;
	
	private final Object TEAM_LOCK = new Object();
	private volatile AdventureTeam curTeam;
	
	protected AdventurePlayer(String name) {
		this.name = Preconditions.checkNotNull(name, "name");
		
		this.curTeam = null;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final void joinTeam(AdventureTeam newTeam) {
		synchronized(this.TEAM_LOCK) {
			AdventureTeam curTeam = this.getCurrentTeam();
			if(newTeam == curTeam) {
				return;
			}
			
			if(curTeam != null) {
				curTeam.removePlayer(this);
			}
			if(newTeam != null) {
				newTeam.addPlayer(this);
				
				if(newTeam.isDissolved()) {
					newTeam = null;
				}
			}
			
			this.curTeam = newTeam;
		}
	}
	
	public final AdventureTeam getCurrentTeam() {
		return this.curTeam;
	}
	
	public abstract void sendMessage(String message);
	
	// Leaves the current team if this player is on the given team
	final boolean leaveTeam(AdventureTeam curTeam) {
		synchronized(this.TEAM_LOCK) {
			if(this.curTeam == curTeam) {
				this.joinTeam(null);
				return true;
			}
			return false;
		}
	}
}
