package me.smith_61.adventure.bukkit.tasks;

import java.util.concurrent.Executor;

import me.smith_61.adventure.bukkit.BukkitPlugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.common.base.Preconditions;

public abstract class BukkitExecutor implements Executor {

	public static final BukkitExecutor SYNC = new SyncExecutor();
	public static final BukkitExecutor ASYNC = new ASyncExecutor();
	
	private final BukkitScheduler scheduler;
	
	private BukkitExecutor() {
		this.scheduler = Bukkit.getScheduler();
	}

	private static class SyncExecutor extends BukkitExecutor {

		@Override
		public void execute(Runnable task) {
			BukkitPlugin plugin = BukkitPlugin.getInstance();
			if(plugin == null || !plugin.isEnabled()) {
				task.run();
			}
			else {
				try {
					super.scheduler.runTask(plugin, task);
				}
				catch(IllegalPluginAccessException ipae) {
					// Thrown if plugin is disabled. If plugin is disabled we will run the task immediately
					// It is possible that the plugin is disabled after our initial check.
					task.run();
				}
			}
		}
		
	}
	
	private static class ASyncExecutor extends BukkitExecutor {
		
		@Override
		public void execute(Runnable task) {
			Preconditions.checkNotNull(task, "task");
			BukkitPlugin plugin = BukkitPlugin.getInstance();
			if(plugin == null || !plugin.isEnabled()) {
				task.run();
			}
			else {
				try {
					super.scheduler.runTaskAsynchronously(plugin, task);
				}
				catch(IllegalPluginAccessException ipae) {
					// Thrown if plugin is disabled. If plugin is disabled we will run the task immediately
					// It is possible that the plugin is disabled after our initial check.
					task.run();
				}
			}
		}
	}
}
