package me.smith_61.adventure.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Nullable;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import me.smith_61.adventure.bukkit.json.AdventureDescription;
import me.smith_61.adventure.bukkit.json.WorldDescription;
import me.smith_61.adventure.bukkit.tasks.BukkitExecutor;
import me.smith_61.adventure.bukkit.tasks.ExtractWorld;
import me.smith_61.adventure.bukkit.tasks.LoadWorld;
import me.smith_61.adventure.common.Adventure;
import me.smith_61.adventure.common.AdventureInstance;
import me.smith_61.adventure.common.AdventureTeam;

public class BukkitAdventure extends Adventure {
	
	private static final String WORLD_NAME_FORMAT = "%s_%s_%s";
	
	private final Environment entryEnvironment;
	private final Map<Environment, BukkitAdventureWorld> worlds;
	
	private BukkitAdventure(String name, Environment entryEnvironment, Map<Environment, BukkitAdventureWorld> worlds) {
		super(name);
		
		this.entryEnvironment = entryEnvironment;
		this.worlds = worlds;
	}

	@Override
	protected ListenableFuture<AdventureInstance> createInstance(final AdventureTeam team) {
		List<ListenableFuture<World>> futures = new ArrayList<ListenableFuture<World>>();
		
		for(Entry<Environment, BukkitAdventureWorld> entry : this.worlds.entrySet()) {
			String worldName = String.format(WORLD_NAME_FORMAT, team.getName(), this.getName(), entry.getValue().getName());
			
			File worldSaveDir = BukkitPlugin.getInstance().getWorldSaveDir();
			File worldDir = null;
			
			while((worldDir = new File(worldSaveDir, worldName)).exists()) {
				worldName += "_";
			}
			worldDir.mkdirs();
			
			WorldCreator options = WorldCreator.name(worldName).environment(entry.getKey());
			
			ListenableFutureTask<String> extractWorld = ListenableFutureTask.create(new ExtractWorld(worldDir, entry.getValue()));
			BukkitExecutor.ASYNC.execute(extractWorld);
			
			futures.add(Futures.transform(extractWorld, new LoadWorld(options), BukkitExecutor.SYNC));
		}
		
		return Futures.transform(Futures.allAsList(futures), new CreateInstance());
	}
	
	public static BukkitAdventure loadAdventure(File file) throws AdventureLoadException {
		try {
			ZipFile zipFile = new ZipFile(file);
			
			ZipEntry jsonEntry = zipFile.getEntry("adventure.json");
			if(jsonEntry == null || jsonEntry.isDirectory()) {
				throw new AdventureLoadException(file.getName() + " is missing required adventure.json file");
			}
			
			InputStream in = null;
			try {
				in = zipFile.getInputStream(jsonEntry);
				
				JsonReader reader = new JsonReader(new InputStreamReader(in));
				
				Gson gson = new Gson();
				
				AdventureDescription description = gson.fromJson(reader, AdventureDescription.class);
				String reason = description.validate();
				if(reason != null) {
					throw new AdventureLoadException("Invalid adventure description file. Missing field " + reason);
				}
				
				Map<Environment, BukkitAdventureWorld> worlds = new HashMap<Environment, BukkitAdventureWorld>();
				for(Entry<Environment, WorldDescription> entry : description.getWorlds().entrySet()) {
					worlds.put(entry.getKey(), BukkitAdventureWorld.fromDescription(entry.getValue(), zipFile));
				}
				
				return new BukkitAdventure(description.getName(), description.getEntry(), worlds);
			}
			finally {
				if(in != null) {
					in.close();
				}
			}
		}
		catch(IOException ioe) {
			throw new AdventureLoadException("Error loading adventure from file: " + file.getName(), ioe);
		}
	}
	
	private class CreateInstance implements Function<List<World>, AdventureInstance> {
		
		private CreateInstance() {
		}
		
		@Override
		public AdventureInstance apply(@Nullable List<World> worlds) {
			Map<Environment, World> worldsMap = new HashMap<Environment, World>();
			
			for(World world : worlds) {
				worldsMap.put(world.getEnvironment(), world);
			}
			
			return new BukkitAdventureInstance(worldsMap.get(BukkitAdventure.this.entryEnvironment), worldsMap);
		}
		
	}
	
}
