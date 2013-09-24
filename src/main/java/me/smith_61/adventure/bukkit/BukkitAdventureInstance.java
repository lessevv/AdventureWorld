package me.smith_61.adventure.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.google.common.base.Preconditions;

import me.smith_61.adventure.common.AdventureInstance;
import me.smith_61.adventure.common.AdventurePlayer;
import me.smith_61.adventure.common.Utils;

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
		Utils.deleteFileTree(this.getBukkitWorld().getWorldFolder(), true);
		Bukkit.getServer().unloadWorld(this.getBukkitWorld(), false);
	}

	@Override
	protected boolean isPlayerInAdventure(AdventurePlayer player) {
		if(player instanceof BukkitAdventurePlayer) {
			return ((BukkitAdventurePlayer)player).getBukkitPlayer().getWorld() == this.clonedWorld;
		}
		return false;
	}

}
