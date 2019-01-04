package bsi.lars.backend.datastore;

import bsi.lars.backend.Backend;
import bsi.lars.backend.datastore.layers.AssetType;
import bsi.lars.backend.datastore.layers.Domain;

/**
 * Represents the data of the database.
 * 
 *
 */
public class Asset {

	private AssetType assetType;
	private Domain domain;
	private String name;
	private Backend backend;

	public Asset(AssetType assetType, Domain domain, String name, Backend backend) {
		this.assetType = assetType;
		this.domain = domain;
		this.name = name;
		this.backend = backend;
	}

	public AssetType getAssetType() {
		return assetType;
	}
	public Domain getDomain() {
		return domain;
	}
	public String getName() {
		return name;
	}
	protected Backend getBackend() {
		return backend;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(getClass().getSimpleName().toLowerCase());
		sb.append(">\n");
		sb.append("<name>");
		sb.append(toCData(getName()));
		sb.append("</name>\n");
		sb.append("</");
		sb.append(getClass().getSimpleName().toLowerCase());
		sb.append(">\n");
		return sb.toString();
	}
	
	protected String toCData(String data) {
		return "<![CDATA["+data+"]]>";
	}
}
