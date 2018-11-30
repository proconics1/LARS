package bsi.lars.gui;

import java.util.Vector;

import javax.swing.JComponent;

/**
 * Struktur, die benutzt wird, um nicht beötigte Teile der graphischen Oberfläche zu verstecken.
 * 
 *
 */
public class HideGroup {
	
	private Vector<JComponent> elements;
	private Vector<HideGroup> subgroups;
	private boolean hidden = false;
	
	public HideGroup() {
		this.elements = new Vector<JComponent>();
		this.subgroups = new Vector<HideGroup>();
	}
	
	public HideGroup(boolean hidden) {
		this();
		this.hidden = hidden;
	}

	public void add(JComponent element) {
		elements.add(element);
		setHidden(hidden);
	}
	
	public void add(HideGroup group) {
		subgroups.add(group);
		setHidden(hidden);
	}
	
	public boolean remove(JComponent element) {
		return elements.remove(element);
	}
	
	public boolean remove(HideGroup group) {
		return elements.remove(group);
	}
	
	public boolean isHidden() {
		return hidden;
	}

	public void hide() {
		setHidden(true);
	}
	
	public void unhide() {
		setHidden(false);
	}

	private void setHidden(boolean value) {
		hidden = value;
		for(JComponent e : elements) {
			e.setVisible(!hidden);
		}
		for(HideGroup g : subgroups) {
			g.setHidden(hidden);
		}
	}
	
}
