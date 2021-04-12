package com.djempie.mapswap;

public class AdventureLoadException extends Exception {

	private static final long serialVersionUID = -3081705579412309814L;

	public AdventureLoadException() { }
	
	public AdventureLoadException(String reason) {
		super(reason);
	}
	
	public AdventureLoadException(String reason, Exception cause) {
		super(reason, cause);
	}
}
