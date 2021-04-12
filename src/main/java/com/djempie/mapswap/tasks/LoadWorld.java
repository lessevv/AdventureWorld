package com.djempie.mapswap.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

public class LoadWorld implements Function<String, World> {
	
	private final WorldCreator options;
	
	public LoadWorld(WorldCreator options) {
		this.options = Preconditions.checkNotNull(options, "options");
	}
	
	@Override
	public World apply(String name) {
		Preconditions.checkNotNull(name, "name");
		
		return Bukkit.createWorld(WorldCreator.name(name).copy(this.options));
	}
}
