package bsi.lars.answer;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import bsi.lars.backend.InvalidDatabaseStructureException;
import bsi.lars.backend.NoCaseSelectedException;
import bsi.lars.backend.NoUserSelectedException;
import bsi.lars.backend.data.Comment;
import bsi.lars.backend.datastore.layers.Layer;

/**
 * Sammelt die Antwort aus der graphischen Oberfläche.
 * 
 *
 */
public abstract class Answer {

	/**
	 * Wenn das element zu dem die Antwort gehört kein Blatt ist, werden hier die Unterantworten verlinkt.
	 */
	private Vector<Answer> subanswers = new Vector<Answer>();
	/**
	 * Der {@link Layer} zu dem das Answer Objekt gehört.
	 */
	private Layer layer;
	/**
	 * Die {@link javax.swing.JComponent}en die das Answer Object zum bestimmen der Antwort benötigt..
	 */
	private JComponent[] components;
	private Comment loadedComment;

	public Answer(Layer layer, JComponent ... components) {
		this.layer = layer;
		this.components = components;
	}

	public JComponent[] getComponents() {
		return components;
	}
	
	public Layer getLayer() {
		return layer;
	}
	
	public void add(Answer subanswer) {
		subanswers.add(subanswer);
	}
	
	public boolean hasSubanswers() {
		return subanswers.size() > 0;
	}
	
	public Answer[] getSubanswers() {
		return subanswers.toArray(new Answer[subanswers.size()]);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Answer for ");
		sb.append(layer.getName());//toString());
		sb.append(' ');
		for(JComponent c : components) {
			sb.append(getValue(c));
			sb.append(' ');
		}
		appendSubanswers(sb);
		return sb.toString();
	}

	protected void appendSubanswers(StringBuilder sb) {
		Answer[] sanswers = getSubanswers();
		if(sanswers != null && sanswers.length > 0) {
			sb.append(" [ ");
			for (int i = 0; i < sanswers.length; i++) {
				Answer a = sanswers[i];
				sb.append(a.toString());
				if(i < sanswers.length - 1) {
					sb.append(", ");
				}
			}
			sb.append(" ]");
		}
	}
	
	protected static JComponent[] append(JComponent[] array, JComponent component) {
		JComponent[] result = Arrays.copyOf(array, array.length + 1);
		result[result.length - 1] = component;
		return result;
	}
	
	protected static JComponent[] append(JComponent component, JComponent[] array) {
		JComponent[] result = new JComponent[array.length + 1];
		result[0] = component;
		for(int i = 0 ; i < array.length ; ++i) {
			result[i + 1] = array[i];
		}
		return result;
	}

	protected String getValue(JComponent c) {
		if(c instanceof JLabel) {
			return ((JLabel) c).getText();
		}else if(c instanceof JTextArea) {
			return ((JTextArea) c).getText();
		}else if(c instanceof JRadioButton) {
			return ((Boolean) ((JRadioButton) c).isSelected()).toString();
		}else{
			throw new IllegalArgumentException(c.toString() + " nicht implementiert.");
		}
	}
	
	/**
	 * Herausfinden, ob die Antwort vollständig ist.
	 * @return true wenn das Answer Objekt vollständig beantwortet ist, sonst false
	 */
	public boolean isFullyAnswered() {
		for(JComponent c : components) {
			if(c instanceof JLabel) {
				if(((JLabel) c).getText() == null) {
					return false;
				}
			}else if(c instanceof JTextArea) {
				if(((JTextArea) c).getText() == null) {
					return false;
				}
			}else if(c instanceof JRadioButton) {
				ButtonModel model = ((JRadioButton) c).getModel();
				if(model instanceof DefaultButtonModel) {
					if (((((DefaultButtonModel) model).getGroup()).getSelection()) == null) {
						return false;
					}
				} else{
					throw new IllegalArgumentException(c.toString() + " nicht implementiert.");
				}
			}else if(c instanceof JCheckBox) {
				//Wenn JCheckBox, dann muss es eine Category sein.
				//Checkbox true => nicht benötigt => vollständig beantwortet, was auch immer in den subanswers steht.
				if(((JCheckBox) c).isSelected()) {
					return true;
				}
			}else{
				throw new IllegalArgumentException(c.toString() + " nicht implementiert.");
			}
		}
		for(Answer subanswer : subanswers) {
			if(!subanswer.isFullyAnswered()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Herausfinden, ob der Benutzer für diese Antwort noch keine Eingabe getätigt hat.
	 * @return true, wenn der Benutzer noch nichts ausgewählt hat, sonst false.
	 */
	public boolean isUnanswered() {
		for(JComponent c : components) {
			if(c instanceof JLabel) {
				if(((JLabel) c).getText() != null) {
					return false;
				}
			}else if(c instanceof JTextArea) {
				if(((JTextArea) c).getText() != null) {
					return false;
				}
			}else if(c instanceof JRadioButton) {
				ButtonModel model = ((JRadioButton) c).getModel();
				if(model instanceof DefaultButtonModel) {
					if (((((DefaultButtonModel) model).getGroup()).getSelection()) != null) {
						return false;
					}
				} else{
					throw new IllegalArgumentException(c.toString() + " nicht implementiert.");
				}
			}else if(c instanceof JCheckBox) {
				//Wenn JCheckBox, dann muss es eine Category sein.
				//Diese ist standardmäßig auf ja gesetzt, also nie nicht beantwortet.
				return false;
			}else{
				throw new IllegalArgumentException(c.toString() + " nicht implementiert.");
			}
		}
		for(Answer subanswer : subanswers) {
			if(!subanswer.isUnanswered()) {
				return false;
			}
		}
		return true;
	}

	protected void setLoadedComment(Comment loadedComment) {
		if(this.loadedComment != null) {
			System.out.println();
		}
		this.loadedComment = loadedComment;
	}

	public Comment getLoadedComment() {
		return loadedComment;
	}
	
	/**
	 * Daten aus der Datenbank laden
	 * @throws InvalidDatabaseStructureException
	 * @throws NoCaseSelectedException
	 * @throws NoUserSelectedException
	 */
	public abstract void load() throws InvalidDatabaseStructureException, NoCaseSelectedException, NoUserSelectedException;

	public String toXML() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(getClass().getSimpleName().toLowerCase());
		sb.append(">\n");
		
		sb.append(specificXML());
		
		for(Answer a : getSubanswers()) {
			sb.append(a.toXML());
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
}
