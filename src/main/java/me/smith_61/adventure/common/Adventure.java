package me.smith_61.adventure.common;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public abstract class Adventure {
	
	private final String name;
	
	private final Map<AdventureTeam, AdventureInstance> playingTeams;
	
	protected Adventure(String name) {
		this.name = Preconditions.checkNotNull(name, "name");
		
		this.playingTeams = new HashMap<AdventureTeam, AdventureInstance>();
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final AdventureTeam[] getAdventureTeams() {
		return this.playingTeams.keySet().toArray(new AdventureTeam[0]);
	}
	
	public final void startAdventure(AdventureTeam team) {
		if(team.getCurrentAdventure() != null) {
			team.getCurrentAdventure().stopAdventure(team);
		}
		
		AdventureLogger.logf(Level.INFO, "Team: %s joining adventure: %s", team.getName(), this.getName());
		
		PendingAdventureInstance instance = new PendingAdventureInstance();
		instance.setAdventure(this).setTeam(team);
		
		Futures.addCallback(this.createInstance(team), instance);
		
		this.playingTeams.put(team, instance);
		team.setCurrentAdventure(instance);
	}
	
	public final void stopAdventure(AdventureTeam team) {
		if(!this.playingTeams.containsKey(team)) {
			return;
		}
		
		AdventureLogger.logf(Level.INFO, "Team: %s leaving adventure: %s", team.getName(), this.getName());
		
		AdventureInstance instance = this.playingTeams.get(team);
		
		for(AdventurePlayer player : team.getTeammates()) {
			player.leaveAdventure(instance);
		}
		instance.destroyInstance();
		
		this.playingTeams.remove(team);
		team.setCurrentAdventure(null);
	}
	
	protected abstract ListenableFuture<AdventureInstance> createInstance(AdventureTeam team);
	
	private class PendingAdventureInstance extends AdventureInstance implements FutureCallback<AdventureInstance> {
		
		private boolean destroyInstance = false;
		
		@Override
		protected void destroyInstance() {
			this.destroyInstance = true;
		}

		@Override
		protected boolean isPlayerInAdventure(AdventurePlayer player) {
			return false;
		}

		public void onFailure(Throwable error) {
			if(this.destroyInstance) {
				return;
			}
			AdventureTeam team = this.getTeam();
			
			AdventureLogger.logf(Level.SEVERE, error, "Error creating adventure instance for team: %s. Aborting", team.getName());
			
			for(AdventurePlayer player : team.getTeammates()) {
				player.sendMessage("Error creating adventure. Aborting");
			}
			
			Adventure.this.playingTeams.remove(team);
			team.setCurrentAdventure(null);
		}

		public void onSuccess(AdventureInstance instance) {
			instance.setAdventure(this.getAdventure()).setTeam(this.getTeam());
			if(this.destroyInstance) {
				instance.destroyInstance();
				return;
			}
			
			Adventure.this.playingTeams.put(this.getTeam(), instance);
			this.getTeam().setCurrentAdventure(instance);
			
			for(AdventurePlayer player : this.getTeam().getTeammates()) {
				player.joinAdventure(instance);
			}
		}
		
	}
}
