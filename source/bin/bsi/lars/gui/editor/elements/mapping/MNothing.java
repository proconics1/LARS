package bsi.lars.gui.editor.elements.mapping;

import bsi.lars.gui.editor.elements.Element;

public class MNothing extends MappingElement<MNothing, Element> {
	public MNothing() {
		super(null, null);
	}
	
	@Override
	public MNothing getFromElement() {
		throw new IllegalAccessError();
	}
	
	@Override
	public Element getToElement() {
		throw new IllegalAccessError();
	}
}
