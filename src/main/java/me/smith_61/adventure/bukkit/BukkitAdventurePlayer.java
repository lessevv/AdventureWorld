package me.smith_61.adventure.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
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
	
	private BukkitAdventureInstance adventureInstance;
	
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
				Player bukkitPlayer = player.getBukkitPlayer();
				
				bukkitPlayer.teleport(((BukkitAdventureInstance)adventure).getEntryWorld().getSpawnLocation(), TeleportCause.PLUGIN);
				
				player.inventoryContents = bukkitPlayer.getInventory().getContents();
				player.armorContents = bukkitPlayer.getInventory().getArmorContents();
				player.getBukkitPlayer().getInventory().clear();
				
				player.gameMode = bukkitPlayer.getGameMode();
				bukkitPlayer.setGameMode(GameMode.ADVENTURE);
				
				player.bedSpawnLocation = bukkitPlayer.getBedSpawnLocation();
				bukkitPlayer.setBedSpawnLocation(((BukkitAdventureInstance)adventure).getEntryWorld().getSpawnLocation(), true);
				
				player.adventureInstance = (BukkitAdventureInstance)adventure;
			}
		});
	}

	protected void leaveAdventure(final AdventureInstance adventure) {
		BukkitExecutor.SYNC.execute(new Runnable() {
			
			@Override
			public void run() {
				BukkitAdventurePlayer player = BukkitAdventurePlayer.this;
				Player bukkitPlayer = player.getBukkitPlayer();
				
				bukkitPlayer.teleport(BukkitPlugin.getInstance().getLobbyWorld().getSpawnLocation(), TeleportCause.PLUGIN);
				
				bukkitPlayer.getInventory().setContents(player.inventoryContents);
				bukkitPlayer.getInventory().setArmorContents(player.armorContents);
				player.inventoryContents = null;
				player.armorContents = null;
				
				bukkitPlayer.setGameMode(player.gameMode);
				player.gameMode = null;
				
				bukkitPlayer.setBedSpawnLocation(player.bedSpawnLocation, true);
				player.bedSpawnLocation = null;
				
				player.adventureInstance = null;
				
				bukkitPlayer.saveData();
			}
		});
	}

	BukkitAdventureInstance getAdventureInstance() {
		return this.adventureInstance;
	}
}
