package bsi.lars.gui.editor.elements.mapping;

import bsi.lars.gui.editor.elements.EAssetType;

public class MAssettype extends MappingElement<MNothing, EAssetType> {

	public MAssettype(EAssetType toElement) {
		super(new MNothing(), toElement);
	}
	
	@Override
	public MNothing getFromElement() {
		throw new IllegalAccessError();
	}
}
