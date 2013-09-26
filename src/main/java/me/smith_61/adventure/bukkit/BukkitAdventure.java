package me.smith_61.adventure.bukkit;

import javax.annotation.Nullable;

import org.bukkit.World;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import me.smith_61.adventure.bukkit.tasks.BukkitExecutor;
import me.smith_61.adventure.bukkit.tasks.CloneWorld;
import me.smith_61.adventure.common.Adventure;
import me.smith_61.adventure.common.AdventureInstance;
import me.smith_61.adventure.common.AdventureTeam;

public class BukkitAdventure extends Adventure {
	
	private final World bukkitWorld;
	
	public BukkitAdventure(String name, World bukkitWorld) {
		super(name);
		
		this.bukkitWorld = Preconditions.checkNotNull(bukkitWorld, "bukkitWorld");
	}
	
	public World getBukkitWorld() {
		return this.bukkitWorld;
	}

	@Override
	protected ListenableFuture<AdventureInstance> createInstance(final AdventureTeam team) {
		ListenableFuture<World> cloneWorld = CloneWorld.cloneWorld(this.bukkitWorld, BukkitExecutor.ASYNC);
		
		return Futures.transform(cloneWorld, new CreateInstance());
	}

	
	private class CreateInstance implements Function<World, AdventureInstance> {

		@Override
		public AdventureInstance apply(@Nullable World world) {
			return new BukkitAdventureInstance(world);
		}
		
	}
	
}
