package bsi.lars.backend.datastore.layers;

import bsi.lars.backend.Backend;
import bsi.lars.backend.datastore.InvalidLayerStackException;

/**
 * Stellt die Daten der Datenbank dar.
 * 
 *
 */
public class AssetType extends Layer {

	public AssetType(int id, String name, String description, Backend backend) throws InvalidLayerStackException {
		super(null, id, name, description, 0, backend);
	}

	@Override
	public boolean isQuestion() {
		return false;
	}

	@Override
	protected boolean isAnswerable() {
		return false;
	}

	@Override
	protected boolean isLocalFullyAnswered() {
		return false;
	}

	@Override
	protected boolean isLocalUnanswered() {
		return false;
	}
	
	@Override
	protected boolean doCheckSubanswers() {
		return true;
	}

	@Override
	protected String specificXML() {
		return "";
	}
}
