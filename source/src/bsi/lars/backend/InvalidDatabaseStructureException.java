package bsi.lars.backend;

import bsi.lars.backend.datastore.InvalidLayerStackException;

public class InvalidDatabaseStructureException extends Exception {
	
	private static final long serialVersionUID = -8240873320990544321L;

	public InvalidDatabaseStructureException(InvalidLayerStackException e) {
		super(e);
	}

	public InvalidDatabaseStructureException(String string) {
		super(string);
	}

}
