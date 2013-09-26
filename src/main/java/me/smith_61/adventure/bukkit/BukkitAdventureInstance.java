package me.smith_61.adventure.bukkit;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.google.common.base.Preconditions;

import me.smith_61.adventure.bukkit.tasks.BukkitExecutor;
import me.smith_61.adventure.bukkit.tasks.DeleteFiles;
import me.smith_61.adventure.common.AdventureInstance;
import me.smith_61.adventure.common.AdventurePlayer;

public class BukkitAdventureInstance extends AdventureInstance {

	private final World clonedWorld;
	
	private final Set<AdventurePlayer> players;
	
	public BukkitAdventureInstance(World clonedWorld) {
		this.clonedWorld = Preconditions.checkNotNull(clonedWorld, "clonedWorld");
		
		this.players = new HashSet<AdventurePlayer>();
	}
	
	public World getBukkitWorld() {
		return this.clonedWorld;
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
				World world = BukkitAdventureInstance.this.clonedWorld;
				
				Bukkit.getServer().unloadWorld(world, false);
				BukkitExecutor.ASYNC.execute(new DeleteFiles(world.getWorldFolder()));
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
