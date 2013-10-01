package me.smith_61.adventure.bukkit.json;

import java.util.Map;

import org.bukkit.World.Environment;

public class AdventureDescription {

	
	private String name;
	private Map<Environment, WorldDescription> worlds;
	
	private Environment entry;

	public Environment getEntry() {
		return entry;
	}

	public void setEntry(Environment entry) {
		this.entry = entry;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Environment, WorldDescription> getWorlds() {
		return worlds;
	}

	public void setWorlds(Map<Environment, WorldDescription> worlds) {
		this.worlds = worlds;
	}
	
	public String validate() {
		if(this.name == null || this.name.trim().isEmpty()) {
			return "name";
		}
		if(this.entry == null) {
			return "entry";
		}
		if(this.worlds == null || this.worlds.size() == 0) {
			return "worlds";
		}
		
		if(!this.worlds.containsKey(this.entry)) {
			return "entry world in worlds";
		}
		
		for(WorldDescription des : this.worlds.values()) {
			String reason = des.validate();
			if(reason != null) {
				return reason;
			}
		}
		
		return null;
	}
}
