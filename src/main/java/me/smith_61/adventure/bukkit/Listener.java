package me.smith_61.adventure.bukkit;

import me.smith_61.adventure.common.AdventureManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listener implements org.bukkit.event.Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		BukkitPlugin.getInstance().getAdventureManager().addAdventurePlayer(new BukkitAdventurePlayer(event.getPlayer()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		AdventureManager manager = BukkitPlugin.getInstance().getAdventureManager();
		
		manager.removeAdventurePlayer(manager.getAdventurePlayer(event.getPlayer().getName()));
	}
}
