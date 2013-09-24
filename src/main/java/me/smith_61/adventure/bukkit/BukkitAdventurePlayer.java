package me.smith_61.adventure.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import me.smith_61.adventure.common.AdventureInstance;
import me.smith_61.adventure.common.AdventurePlayer;

public class BukkitAdventurePlayer extends AdventurePlayer {

	private final Player bukkitPlayer;
	
	private GameMode gameMode;
	
	private ItemStack[] inventoryContents;
	private ItemStack[] armorContents;
	
	private Location bedSpawnLocation;
	
	public BukkitAdventurePlayer(Player bukkitPlayer) {
		super(Preconditions.checkNotNull(bukkitPlayer).getName());
		
		this.bukkitPlayer = bukkitPlayer;
	}
	
	public Player getBukkitPlayer() {
		return this.bukkitPlayer;
	}
	
	@Override
	public void sendMessage(String message) {
		this.getBukkitPlayer().sendMessage(ChatColor.GOLD + message);
	}

	@Override
	protected void joinAdventure(AdventureInstance adventure) {
		this.getBukkitPlayer().teleport(((BukkitAdventureInstance)adventure).getBukkitWorld().getSpawnLocation());
		
		this.inventoryContents = this.getBukkitPlayer().getInventory().getContents();
		this.armorContents = this.getBukkitPlayer().getInventory().getArmorContents();
		this.getBukkitPlayer().getInventory().clear();
		
		this.gameMode = this.getBukkitPlayer().getGameMode();
		this.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
		
		this.bedSpawnLocation = this.getBukkitPlayer().getBedSpawnLocation();
		this.getBukkitPlayer().setBedSpawnLocation(((BukkitAdventureInstance)adventure).getBukkitWorld().getSpawnLocation(), true);
	}

	@Override
	protected void leaveAdventure(AdventureInstance adventure) {
		this.getBukkitPlayer().teleport(BukkitPlugin.getInstance().getLobbyWorld().getSpawnLocation());
		
		this.getBukkitPlayer().getInventory().setContents(this.inventoryContents);
		this.getBukkitPlayer().getInventory().setArmorContents(this.armorContents);
		this.inventoryContents = null;
		this.armorContents = null;
		
		this.getBukkitPlayer().setGameMode(this.gameMode);
		this.gameMode = null;
		
		this.getBukkitPlayer().setBedSpawnLocation(this.bedSpawnLocation, true);
		this.bedSpawnLocation = null;
	}

}
