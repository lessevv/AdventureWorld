package com.djempie.mapswap.adventure;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public abstract class Adventure {
	
	private final String name;
	
	private final Map<AdventureTeam, AdventureInstance> playingTeams; // Synchronized on self
	
	protected Adventure(String name) {
		this.name = Preconditions.checkNotNull(name, "name");
		
		this.playingTeams = new HashMap<AdventureTeam, AdventureInstance>();
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final AdventureTeam[] getAdventureTeams() {
		synchronized(this.playingTeams) {
			return this.playingTeams.keySet().toArray(new AdventureTeam[0]);
		}
	}
	
	final AdventureInstance startAdventure(AdventureTeam team) {
		synchronized(this.playingTeams) {
			if(this.playingTeams.containsKey(team)) {
				return this.playingTeams.get(team);
			}
			
			PendingAdventureInstance instance = new PendingAdventureInstance();
			instance.setAdventure(this).setTeam(team);
			
			this.playingTeams.put(team, instance);
			
			Futures.addCallback(this.createInstance(team), instance);
			
			return instance;
		}
	}
	
	protected abstract ListenableFuture<AdventureInstance> createInstance(AdventureTeam team);
	
	private class PendingAdventureInstance extends AdventureInstance implements FutureCallback<AdventureInstance> {
		
		private AdventureInstance instance;
		
		private boolean startAdventure = false;
		private boolean destroyInstance = false;
		
		@Override
		protected void destroyInstance() {
			synchronized(this) {
				if(this.destroyInstance) {
					return;
				}
				
				if(this.instance != null) {
					this.instance.destroyInstance();
				}
				else {
					this.destroyInstance = true;
				}
			}
			
			synchronized(Adventure.this.playingTeams) {
				Adventure.this.playingTeams.remove(this.getTeam());
			}
		}

		@Override
		protected boolean isPlayerInAdventure(AdventurePlayer player) {
			synchronized(this) {
				if(this.instance != null) {
					return this.instance.isPlayerInAdventure(player);
				}
				return false;
			}
		}
		
		@Override
		public void startAdventure() {
			synchronized(this) {
				if(this.instance != null) {
					this.instance.startAdventure();
				}
				else {
					this.startAdventure = true;
				}
			}
		}

		public void onFailure(Throwable error) {
			synchronized(this) {
				if(this.destroyInstance) {
					return;
				}
			}
			AdventureTeam team = this.getTeam();
			
			if(team.leaveAdventure(this)) {
				AdventureLogger.logf(Level.SEVERE, error, "Error creating adventure instance for team: %s. Aborting", team.getName());
				
				for(AdventurePlayer player : team.getTeammates()) {
					player.sendMessage("Error creating adventure. Aborting");
				}
			}
		}

		public void onSuccess(AdventureInstance instance) {
			instance.setAdventure(this.getAdventure()).setTeam(this.getTeam());
			synchronized(this) {
				if(this.destroyInstance) {
					instance.destroyInstance();
					return;
				}
				
				this.instance = instance;
				
				if(this.startAdventure) {
					instance.startAdventure();
				}
			}
		}

		@Override
		protected void leaveAdventure(AdventurePlayer player) {
			synchronized(this) {
				if(this.instance != null) {
					this.instance.leaveAdventure(player);
				}
			}
		}
		
	}
}
