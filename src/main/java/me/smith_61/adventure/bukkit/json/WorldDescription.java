package me.smith_61.adventure.bukkit.json;

public class WorldDescription {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String validate() {
		if(this.name == null) {
			return "name";
		}
		this.name = this.name.trim();
		if(this.name.isEmpty()) {
			return "name";
		}
		
		return null;
	}
}
