package me.smith_61.adventure.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

public class AdventureLogger {
	
	private static Logger logger;
	
	public static Logger getLogger() {
		if(AdventureLogger.logger == null) {
			AdventureLogger.logger = Logger.getLogger("AdventureLogger");
			
			AdventureLogger.log(Level.WARNING, "No logger set. Using default logger.");
		}
		return AdventureLogger.logger;
	}

	public static void log(Level level, String message) {
		AdventureLogger.getLogger().log(level, message);
	}
	
	public static void log(Level level, String message, Throwable error) {
		AdventureLogger.getLogger().log(level, message, error);
	}
	
	public static void logf(Level level, String format, Object... args) {
		AdventureLogger.log(level, String.format(format, args));
	}
	
	public static void logf(Level level, Throwable error, String format, Object... args) {
		AdventureLogger.log(level, String.format(format, args), error);
	}
	
	public static void setLogger(Logger logger) {
		if(AdventureLogger.logger != null) {
			throw new IllegalStateException("Logger already set");
		}
		
		AdventureLogger.logger = Preconditions.checkNotNull(logger);
	}
}
