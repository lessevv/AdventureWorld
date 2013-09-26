package me.smith_61.adventure.bukkit.tasks;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import me.smith_61.adventure.common.Utils;

import org.bukkit.World;
import org.bukkit.WorldCreator;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

public class CloneWorld implements Callable<WorldCreator> {
	
	private static final String WORLD_SAVE_FORMAT = "%s_clone_%d";

	public static ListenableFuture<World> cloneWorld(World world) {
		return CloneWorld.cloneWorld(world, LoadWorld.INSTANCE, BukkitExecutor.SYNC);
	}
	
	public static ListenableFuture<World> cloneWorld(World world, Executor executor) {
		return CloneWorld.cloneWorld(world, LoadWorld.INSTANCE, executor);
	}
	
	public static ListenableFuture<World> cloneWorld(World world, Function<WorldCreator, World> loader) {
		return CloneWorld.cloneWorld(world, loader, BukkitExecutor.SYNC);
	}
	
	public static ListenableFuture<World> cloneWorld(World world, Function<WorldCreator, World> loader, Executor executor) {
		Preconditions.checkNotNull(world, "world");
		Preconditions.checkNotNull(loader, "loader");
		Preconditions.checkNotNull(executor, "executor");
		
		ListenableFutureTask<WorldCreator> copyDir = ListenableFutureTask.create(new CloneWorld(world));
		executor.execute(copyDir);
		
		return Futures.transform(copyDir, loader, BukkitExecutor.SYNC);
	}
	
	private final World worldToClone;
	
	private CloneWorld(World world) {
		this.worldToClone = world;
	}
	
	@Override
	public WorldCreator call() throws Exception {
		File oldWorldSave = this.worldToClone.getWorldFolder();
		File newWorldSave = null;
		
		File saveDir = oldWorldSave.getParentFile();
		
		String worldName = String.format(WORLD_SAVE_FORMAT, this.worldToClone.getName(), System.currentTimeMillis());
		
		while((newWorldSave = new File(saveDir, worldName)).exists()) {
			worldName += "_";
		}
		
		Utils.copyTo(oldWorldSave, newWorldSave);
		new File(newWorldSave, "uid.dat").delete(); // Bukkit safeguard for cloned worlds
		
		return WorldCreator.name(worldName).environment(this.worldToClone.getEnvironment());
	}

	
}
