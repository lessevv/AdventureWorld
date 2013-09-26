package me.smith_61.adventure.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.google.common.base.Preconditions;

import me.smith_61.adventure.bukkit.tasks.BukkitExecutor;
import me.smith_61.adventure.bukkit.tasks.DeleteFiles;
import me.smith_61.adventure.common.AdventureInstance;
import me.smith_61.adventure.common.AdventurePlayer;

public class BukkitAdventureInstance extends AdventureInstance {

	private final World clonedWorld;
	
	public BukkitAdventureInstance(World clonedWorld) {
		this.clonedWorld = Preconditions.checkNotNull(clonedWorld, "clonedWorld");
	}
	
	public World getBukkitWorld() {
		return this.clonedWorld;
	}
	
	@Override
	protected void destroyInstance() {
		Bukkit.getServer().unloadWorld(this.clonedWorld, false);
		BukkitExecutor.ASYNC.execute(new DeleteFiles(this.clonedWorld.getWorldFolder()));
	}

	@Override
	protected boolean isPlayerInAdventure(AdventurePlayer player) {
		if(player instanceof BukkitAdventurePlayer) {
			return ((BukkitAdventurePlayer)player).getBukkitPlayer().getWorld() == this.clonedWorld;
		}
		return false;
	}

}
