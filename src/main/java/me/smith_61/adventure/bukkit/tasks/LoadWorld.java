package me.smith_61.adventure.bukkit.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

public class LoadWorld implements Function<WorldCreator, World> {

	public static final LoadWorld INSTANCE = new LoadWorld();
	
	@Override
	public World apply(WorldCreator options) {
		Preconditions.checkNotNull("options", options);
		
		return Bukkit.createWorld(options);
	}
}
