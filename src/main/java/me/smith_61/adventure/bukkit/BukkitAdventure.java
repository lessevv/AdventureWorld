package me.smith_61.adventure.bukkit;

import java.io.File;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import me.smith_61.adventure.common.Adventure;
import me.smith_61.adventure.common.AdventureInstance;
import me.smith_61.adventure.common.AdventureLogger;
import me.smith_61.adventure.common.AdventureTeam;
import me.smith_61.adventure.common.Utils;

public class BukkitAdventure extends Adventure {

	private static final String WORLD_SAVE_FORMAT = "%s_%s_%d";
	private static final Random WORLD_NAME_SEED = new Random();
	
	private final World bukkitWorld;
	private final LoadWorld loadWorld;
	
	public BukkitAdventure(String name, World bukkitWorld) {
		super(name);
		
		this.bukkitWorld = Preconditions.checkNotNull(bukkitWorld, "bukkitWorld");
		this.loadWorld = new LoadWorld();
	}
	
	public World getBukkitWorld() {
		return this.bukkitWorld;
	}

	@Override
	protected ListenableFuture<AdventureInstance> createInstance(final AdventureTeam team) {
		ListenableFutureTask<String> copyDir = ListenableFutureTask.create(new Callable<String>() {

			public String call() throws Exception {
				File oldWorldSave = BukkitAdventure.this.bukkitWorld.getWorldFolder();
				File newWorldSave = null;
				
				File saveDir = oldWorldSave.getParentFile();
				
				String worldName = String.format(WORLD_SAVE_FORMAT, BukkitAdventure.this.getName(), team.getName(), WORLD_NAME_SEED.nextLong());
				
				while((newWorldSave = new File(saveDir, worldName)).exists()) {
					worldName += "_";
				}
				
				Utils.copyTo(oldWorldSave, newWorldSave);
				new File(newWorldSave, "uid.dat").delete();
				
				return worldName;
			}
			
		});
		
		
		Bukkit.getScheduler().runTaskAsynchronously(BukkitPlugin.getInstance(), copyDir);
		
		return Futures.transform(copyDir, this.loadWorld, BukkitExecutor.INSTANCE);
	}

	
	private class LoadWorld implements Function<String, AdventureInstance> {

		public AdventureInstance apply(String worldName) {
			AdventureLogger.logf(Level.INFO, "Loadng world with name: %s", worldName);
			World clonedWorld = Bukkit.createWorld(WorldCreator.name(worldName).copy(BukkitAdventure.this.bukkitWorld));
			
			return new BukkitAdventureInstance(clonedWorld);
		}
		
	}
	
}
