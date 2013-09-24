package me.smith_61.adventure.bukkit;

import java.util.concurrent.Executor;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class BukkitExecutor implements Executor {

	public static final BukkitExecutor INSTANCE = new BukkitExecutor();
	
	private final BukkitScheduler scheduler;
	
	private BukkitExecutor() {
		this.scheduler = Bukkit.getScheduler();
	}
	
	public void execute(Runnable task) {
		this.scheduler.runTask(BukkitPlugin.getInstance(), task);
	}

}
