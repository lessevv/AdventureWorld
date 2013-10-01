package me.smith_61.adventure.bukkit;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import me.smith_61.adventure.bukkit.json.WorldDescription;

public class BukkitAdventureWorld {

	private final String name;
	
	private final ZipFile zipFile;
	private final ZipEntry entry;
	
	private BukkitAdventureWorld(String name, ZipFile zipFile, ZipEntry entry) {
		this.name = name;
		
		this.zipFile = zipFile;
		this.entry = entry;
	}
	
	public String getName() {
		return this.name;
	}
	
	public ZipInputStream getInputStream() throws IOException {
		return new ZipInputStream(this.zipFile.getInputStream(this.entry));
	}
	
	public static BukkitAdventureWorld fromDescription(WorldDescription description, ZipFile zipFile) throws AdventureLoadException {
		String name = description.getName();
		
		ZipEntry entry = zipFile.getEntry("maps/" + name + ".zip");
		if(entry == null) {
			throw new AdventureLoadException("Missing world zip file: maps/" + name + ".zip");
		}
		
		return new BukkitAdventureWorld(description.getName(), zipFile, entry);
	}
}
