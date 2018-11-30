package bsi.lars.backend.datastore.layers;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import bsi.lars.backend.Backend;
import bsi.lars.backend.datastore.InvalidLayerStackException;

/**
 * Die Grunddatenstruktur des Programms ist ein geschichteter Baum.
 * Die Schichten in korrekter REihenfolge sind: {@link AssetType}, {@link Domain} und {@link Measure}.
 * Ein Layer objekt repräsentiert nicht die Gesamtheit aller Elemente, die in der Schicht zu finden sind, sondern nur eines davon. Das im Hinterkopf behalten, wenn die Dokumentation gelesen wird.
 * 
 *
 */
public abstract class Layer implements MutableTreeNode {
	
	private Layer parent;
	private int id;
	private String name;
	private String description;
	protected int layerid;
	private Backend backend;

	
	private int score = -1;
	
	public Layer(Layer parent, int id, String name, String description, Backend backend) throws InvalidLayerStackException {
		this(parent, id, name, description, parent.layerid + 1, backend);
	}
	
	public Layer(Layer parent, int id, String name, String description, int layerid, Backend backend) throws InvalidLayerStackException {
		this.parent = parent;
		this.id = id;
		this.name = name;
		this.description = description;
		this.layerid = layerid;
		this.backend = backend;
		if(layerid != 0 && parent.getLayerid() + 1 != layerid) {
			throw new InvalidLayerStackException(parent, parent.getLayerid(), name, layerid);
		}
	}
	
	/**
	 * Legt fest, ob das Layer Objekt beantwortet werden kann.
	 * @return true wenn eine antwort erwartet wird, sonst false
	 */
	public abstract boolean isQuestion();
	
	public Layer getParent() {
		return parent;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isRoot() { 
		return parent == null;
	}
	
	private int getLayerid() {
		return layerid;
	}
	
	protected Backend getBackend() {
		return backend;
	}
	
	@Override
	public String toString() {
		return ( (parent != null) ? (parent.toString() + "->") : "" ) + getName();
	}
	
	public String toDebugString() {
		return ( (parent != null) ? (parent.toDebugString() + "->") : "" ) + getName() + "(" + getId() + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Layer) {
			if(getName() == null) {
				if(((Layer) obj).getName() != null) {
					return false;
				}
			}
				
			if(getName() == null || ((Layer) obj).getName().equals(getName())) {
				Layer oParent = ((Layer) obj).getParent();
				Layer mParent = getParent();
				if(oParent == null && mParent == null) {
					return true;
				}
				if(oParent != null && mParent != null && oParent.equals(mParent)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Layer[] getChildren() {
		try{
			return backend.getElements(this);
		}catch(InvalidLayerStackException e) {
			return null;
		}
	}
	
	/**
	 * Herausfinden, ob ein Benutzer dieses Layer Objekt vollständig beantwortet hat.
	 * @return true wenn das Layer Objekt beantwortet werden kann und vollständig beantwortet ist, sonst false
	 */
	public boolean isFullyAnswered() {
		if(isAnswerable()) {
			if(!isLocalFullyAnswered()) {
				return false;
			}
		}
		if(doCheckSubanswers()) {
			for(Layer c : getChildren()) {
				if(!c.isFullyAnswered()) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Herausfinden, ob der Benutzer noch keine eintragungen an diesem Layer Objekt vorgenimmen hat
	 * @return true wenn der BEnutzer noch keine Eintragungen vorgenommen hat, sonst false
	 */
	public boolean isUnanswered() {
		if(isAnswerable()) {
			if(!isLocalUnanswered()) {
				return false;
			}
		}
		for(Layer c : getChildren()) {
			if(!c.isUnanswered()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Herausfinden, ob das Layer Objekt eine Antwort erwartet.
	 * @return true wenn das Layer Objekt eine Antwort erwartet, sonst false.
	 */
	protected abstract boolean isAnswerable();
	/**
	 * Im Gegensatz zu {@link #isFullyAnswered()} muss diese Methode für die einzelnen Schichten implementiert werden
	 * @return true wenn die Schicht vollständig beantwortet ist, sonst false
	 */
	protected abstract boolean isLocalFullyAnswered();
	/**
	 * Ähnlich zu {@link #isLocalFullyAnswered()}
	 * @return true wenn die Schicht lokal unbeantwortet ist
	 */
	protected abstract boolean isLocalUnanswered();
	/**
	 * Heausfinden, ob die Unterantworten für das Endergebnis geprüft werden müssen.
	 * @return true wenn die Unterantworten relevant sind, sonst false
	 */
	protected abstract boolean doCheckSubanswers();
	
	//MutableTreeNode
	
	@Override
	public Enumeration<Layer> children() {
		Layer[] children = getChildren();
		if(children == null) {
			children = new Layer[0];
		}
		return new Vector<Layer>(Arrays.asList(children)).elements();
	}

	@Override
	public boolean getAllowsChildren() {
		if(layerid < 3) {
			return true;
		}else{
			return false;
		}
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return getChildren()[childIndex];
	}

	@Override
	public int getChildCount() {
		return getChildren().length;
	}

	@Override
	public int getIndex(TreeNode node) {
		return Arrays.asList(getChildren()).indexOf(node);
	}

	@Override
	public boolean isLeaf() {
		if(getChildren() == null) {
			return true;
		}
		return getChildren().length == 0;
	}

	//MutableTreeNode
	
	@Override
	public void insert(MutableTreeNode child, int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(MutableTreeNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUserObject(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeFromParent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParent(MutableTreeNode newParent) {
		throw new UnsupportedOperationException();
	}

	public String toXML(boolean includechildren) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(getClass().getSimpleName().toLowerCase());
		sb.append(">\n");
		sb.append("<name>");
		sb.append(toCData(getName()));
		sb.append("</name>\n");
		sb.append("<description>");
		sb.append(getDescription() == null ? "" : toCData(getDescription()));
		sb.append("</description>\n");
		
		sb.append(specificXML());
		
		if(includechildren) {
			for(Layer l : getChildren()) {
				sb.append(l.toXML(includechildren));
			}
		}
		
		sb.append("</");
		sb.append(getClass().getSimpleName().toLowerCase());
		sb.append(">\n");
		return sb.toString();
	}
	
	protected abstract String specificXML() throws Exception;
	
	protected String toCData(String data) {
		return "<![CDATA["+data+"]]>";
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	public int getScore() {
		return score;
	}
}
