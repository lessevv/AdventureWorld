package com.djempie.mapswap.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.djempie.mapswap.BukkitAdventureWorld;

public class ExtractWorld implements Callable<String> {

	private final File outputDir;
	private final BukkitAdventureWorld world;
	
	public ExtractWorld(File outputDir, BukkitAdventureWorld world) {
		this.outputDir = outputDir;
		this.world = world;
	}
	
	@Override
	public String call() throws Exception {
		byte[] buffer = new byte[1024];
		int read = 0;
		
		ZipInputStream zipInput = this.world.getInputStream();
		
		ZipEntry entry = null;
		while((entry = zipInput.getNextEntry()) != null) {
			if(entry.isDirectory()) {
				continue;
			}
			File outputFile = new File(this.outputDir, entry.getName());
			outputFile.getParentFile().mkdirs();
			
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(outputFile);
				
				while((read = zipInput.read(buffer)) != -1) {
					fileOut.write(buffer, 0, read);
				}
			}
			finally {
				if(fileOut != null) {
					fileOut.close();
				}
			}
		}
		
		new File(this.outputDir, "uid.dat").delete(); // uid.dat must be deleted or bukkit will complain
		
		return this.outputDir.getName();
	}

}
