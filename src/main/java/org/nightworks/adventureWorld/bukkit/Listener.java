package io.nightworks.adventureWorld.bukkit;

import io.nightworks.adventureWorld.common.AdventureManager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

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
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPortal(PlayerPortalEvent event) {
		Environment env;
		
		switch(event.getCause()) {
			case NETHER_PORTAL:
				env = Environment.NETHER;
				break;
			case END_PORTAL:
				env = Environment.THE_END;
				break;
			default:
				return;
		}
		Player bukkitPlayer = event.getPlayer();
		BukkitAdventurePlayer player = (BukkitAdventurePlayer)BukkitPlugin.getInstance().getAdventureManager().getAdventurePlayer(bukkitPlayer.getName());
		if(bukkitPlayer.getWorld().getEnvironment() == env) {
			env = Environment.NORMAL;
		}
		
		BukkitAdventureInstance instance = player.getAdventureInstance();
		if(instance != null) {
			World world = instance.getWorld(env);
			if(world == null) {
				player.sendMessage("No world found for: " + env);
				event.setCancelled(true);
				return;
			}
			
			Location to = event.getFrom().clone();
			
			if(event.getCause() == TeleportCause.NETHER_PORTAL) {
				double scale = 1d;
				if(env == Environment.NORMAL) {
					scale = 8d;
				}
				else if(env == Environment.NETHER) {
					scale = 1 / 8d;
				}
				to.setX(to.getX() * scale);
				to.setZ(to.getZ() * scale);
			}
			else {
				if(env == Environment.NORMAL) {
					to = world.getSpawnLocation().clone();
				}
				else if(env == Environment.THE_END) {
					to = new Location(world, 100, 50, 0);
				}
			}
			to.setWorld(world);
			
			event.setTo(to);
		}
	}
}
