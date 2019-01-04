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
 * Collect the answer from the graphical interface.
 * 
 *
 */
public abstract class Answer {

	/**
	 * If the element to which the answer belongs is not a leaf, the sub-answers are linked here.
	 */
	private Vector<Answer> subanswers = new Vector<Answer>();
	/**
	 *The {@link Layer} to which the Answer object belongs.
	 */
	private Layer layer;
	/**
	 * The {@link javax.swing.JComponent} which requires the Answer Object to determine the answer..
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
			throw new IllegalArgumentException(c.toString() + " Not implemented.");
		}
	}
	
	/**
	 * Find out if the answer is complete.
	 * @return true if the Answer object is completely answered, otherwise false
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
					throw new IllegalArgumentException(c.toString() + " Not implemented.");
				}
			}else if(c instanceof JCheckBox) {
				//If JCheckBox, then it has to be a category.
				//Checkbox true => not required => completely answered, whatever is in the subanswers.
				if(((JCheckBox) c).isSelected()) {
					return true;
				}
			}else{
				throw new IllegalArgumentException(c.toString() + " Not implemented.");
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
	 * Find out if the user has not yet entered an entry for this answer.
	 * @return true, if the user has not selected anything yet, otherwise false.
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
					throw new IllegalArgumentException(c.toString() + " Not Implemented.");
				}
			}else if(c instanceof JCheckBox) {
				// If JCheckBox, then it must be a Category.
				// This is set to yes by default, so never answered.
				return false;
			}else{
				throw new IllegalArgumentException(c.toString() + " Not Implemented.");
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
	 * Load data from the database
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
