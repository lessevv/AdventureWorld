package com.djempie.mapswap;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.bukkit.configuration.ConfigurationSection;

public class BukkitAdventureWorld {

	private final String name;
	
	private final ZipFile zipFile;
	private final ZipEntry worldZipEntry;
	
	private BukkitAdventureWorld(String name, ZipFile zipFile, ZipEntry entry) {
		this.name = name;
		
		this.zipFile = zipFile;
		this.worldZipEntry = entry;
	}
	
	public String getName() {
		return this.name;
	}
	
	public ZipInputStream getInputStream() throws IOException {
		return new ZipInputStream(this.zipFile.getInputStream(this.worldZipEntry));
	}

	/**
	 * returns a BukkitAdventureWorld instance with a name, the original zip file and the world zip file
	 * @param section configuration secion containing the world name
	 * @param zipFile zip file containing the adventure maps and config file
	 * @return a BukkitAdventureWorld containing the specified world
	 * @throws AdventureLoadException
	 */
	public static BukkitAdventureWorld fromDescription(ConfigurationSection section, ZipFile zipFile) throws AdventureLoadException {
		// get world name
		String name = section.getString("name");
		if(name == null || name.isEmpty()) {
			throw new AdventureLoadException("Missing required field: 'name' in world description for environment: '" + section.getName() + "'");
		}
		// get world entry
		ZipEntry entry = zipFile.getEntry("maps/" + name + ".zip");
		if(entry == null) {
			throw new AdventureLoadException("Missing world zip file: maps/" + name + ".zip");
		}
		// return new BukkitAdventureWorld instance containing the entry
		return new BukkitAdventureWorld(name, zipFile, entry);
	}
}
