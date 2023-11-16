package io.nightworks.adventureWorld.bukkit.tasks;

import java.io.File;
import java.util.logging.Level;

import io.nightworks.adventureWorld.common.AdventureLogger;
import io.nightworks.adventureWorld.common.Utils;

import com.google.common.base.Preconditions;

public class DeleteFiles implements Runnable {

	private final File root;
	
	public DeleteFiles(File root) {
		this.root = Preconditions.checkNotNull(root, "root");
	}
	
	@Override
	public void run() {
		Utils.deleteFileTree(this.root, false);
		if(root.exists()) {
			AdventureLogger.logf(Level.WARNING, "Unable to delete all files in tree for parent: %s. Deleting on JVM exit.", this.root);
			Utils.deleteFileTree(this.root, true);
		}
	}
}
