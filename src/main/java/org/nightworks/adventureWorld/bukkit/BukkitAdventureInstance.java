package io.nightworks.adventureWorld.bukkit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;

import com.google.common.base.Preconditions;

import io.nightworks.adventureWorld.bukkit.tasks.BukkitExecutor;
import io.nightworks.adventureWorld.bukkit.tasks.DeleteFiles;
import io.nightworks.adventureWorld.common.AdventureInstance;
import io.nightworks.adventureWorld.common.AdventurePlayer;

public class BukkitAdventureInstance extends AdventureInstance {

	private final World adventureWorld;
	private final Map<Environment, World> worlds;
	
	private final Set<AdventurePlayer> players;
	
	public BukkitAdventureInstance(World entryWorld, Map<Environment, World> worlds) {
		this.adventureWorld = Preconditions.checkNotNull(entryWorld, "entryWorld");
		this.worlds = Preconditions.checkNotNull(worlds, "worlds");
		
		this.players = new HashSet<AdventurePlayer>();
	}
	
	public World getEntryWorld() {
		return this.adventureWorld;
	}
	
	public World getWorld(Environment environment) {
		return this.worlds.get(environment);
	}
	
	@Override
	protected void destroyInstance() {
		for(AdventurePlayer player : this.getTeam().getTeammates()) {
			if(player instanceof BukkitAdventurePlayer) {
				((BukkitAdventurePlayer)player).leaveAdventure(this);
			}
		}
		this.players.clear();
		
		BukkitExecutor.SYNC.execute(new Runnable() {
			
			@Override
			public void run() {
				for(World world : BukkitAdventureInstance.this.worlds.values()) {
					Bukkit.getServer().unloadWorld(world, false);
					BukkitExecutor.ASYNC.execute(new DeleteFiles(world.getWorldFolder()));
				}
			}
			
		});
	}

	@Override
	protected boolean isPlayerInAdventure(AdventurePlayer player) {
		return this.players.contains(player);
	}

	@Override
	protected void startAdventure() {
		for(AdventurePlayer player : this.getTeam().getTeammates()) {
			if(player instanceof BukkitAdventurePlayer) {
				((BukkitAdventurePlayer)player).joinAdventure(this);
				
				this.players.add(player);
			}
		}
	}

	@Override
	protected void leaveAdventure(AdventurePlayer player) {
		if(player instanceof BukkitAdventurePlayer && this.players.contains(player)) {
			((BukkitAdventurePlayer) player).leaveAdventure(this);
		}
	}
}
