package me.smith_61.adventure.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import me.smith_61.adventure.bukkit.tasks.BukkitExecutor;
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

	protected void joinAdventure(final AdventureInstance adventure) {
		BukkitExecutor.SYNC.execute(new Runnable() {
			
			@Override
			public void run() {
				BukkitAdventurePlayer player = BukkitAdventurePlayer.this;
				
				player.getBukkitPlayer().teleport(((BukkitAdventureInstance)adventure).getBukkitWorld().getSpawnLocation());
				
				player.inventoryContents = player.getBukkitPlayer().getInventory().getContents();
				player.armorContents = player.getBukkitPlayer().getInventory().getArmorContents();
				player.getBukkitPlayer().getInventory().clear();
				
				player.gameMode = player.getBukkitPlayer().getGameMode();
				player.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
				
				player.bedSpawnLocation = player.getBukkitPlayer().getBedSpawnLocation();
				player.getBukkitPlayer().setBedSpawnLocation(((BukkitAdventureInstance)adventure).getBukkitWorld().getSpawnLocation(), true);
			}
		});
	}

	protected void leaveAdventure(final AdventureInstance adventure) {
		BukkitExecutor.SYNC.execute(new Runnable() {
			
			@Override
			public void run() {
				BukkitAdventurePlayer player = BukkitAdventurePlayer.this;
				
				player.getBukkitPlayer().teleport(BukkitPlugin.getInstance().getLobbyWorld().getSpawnLocation());
				
				player.getBukkitPlayer().getInventory().setContents(player.inventoryContents);
				player.getBukkitPlayer().getInventory().setArmorContents(player.armorContents);
				player.inventoryContents = null;
				player.armorContents = null;
				
				player.getBukkitPlayer().setGameMode(player.gameMode);
				player.gameMode = null;
				
				player.getBukkitPlayer().setBedSpawnLocation(player.bedSpawnLocation, true);
				player.bedSpawnLocation = null;
			}
		});
	}

}
