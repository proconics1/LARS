package bsi.lars.gui.editor.elements.mapping;

import java.util.Collections;
import java.util.Vector;

import bsi.lars.gui.editor.elements.Element;

public abstract class MappingElement<M extends MappingElement<?,?>, T extends Element> {
	private M fromElement;
	private T toElement;
	
	public MappingElement(M fromElement, T toElement) {
		this.fromElement = fromElement;
		this.toElement = toElement;
	}
	
	public M getFromElement() {
		return fromElement;
	}
	
	public T getToElement() {
		return toElement;
	}
	
	public Integer[] getPath() {
		Vector<Integer> result = new Vector<Integer>();
		if(! (fromElement instanceof MNothing) ) {
			Collections.addAll(result, fromElement.getPath());
		}
		result.add(toElement.getId());
		return result.toArray(new Integer[result.size()]);
	}
	
	@Override
	public String toString() {
		String result = "";
		if(! (fromElement instanceof MNothing) ) {
			result += fromElement.toString() + "->";
		}
		return result + toElement.toString();
	}
}
