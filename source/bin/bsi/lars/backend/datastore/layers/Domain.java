package bsi.lars.backend.datastore.layers;

import bsi.lars.backend.Backend;
import bsi.lars.backend.datastore.Asset;
import bsi.lars.backend.datastore.InvalidLayerStackException;

/**
 * Represents the data of the database.
 * 
 *
 */
public class Domain extends Layer {

	public Domain(Layer parent, int id, String name, String description, Backend backend) throws InvalidLayerStackException {
		super(parent, id, name, description, 1, backend);
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
	protected String specificXML() throws Exception {
		StringBuilder sb = new StringBuilder();
		
		for(Asset a : Backend.getInstance().getAssets((AssetType) getParent(), this)) {
			sb.append(a.toXML());
		}
		
		return sb.toString();
	}
}
