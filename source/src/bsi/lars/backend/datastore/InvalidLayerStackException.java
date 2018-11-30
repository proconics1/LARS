package bsi.lars.backend.datastore;

import bsi.lars.backend.datastore.layers.Layer;

public class InvalidLayerStackException extends Exception {

	private static final long serialVersionUID = -2484174631688942613L;

	public InvalidLayerStackException(Layer parent, int parentlayerid, String name, int layerid) {
		super("Kann Schicht " + layerid + ' ' + name + " nicht auf Schicht " + parent.getName() + '(' + parentlayerid + ')' + " legen.");
	}

	public InvalidLayerStackException(String string) {
		super(string);
	}

}
