package bsi.lars.answer;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import bsi.lars.backend.Backend;
import bsi.lars.backend.InvalidDatabaseStructureException;
import bsi.lars.backend.NoCaseSelectedException;
import bsi.lars.backend.NoUserSelectedException;
import bsi.lars.backend.data.Comment;
import bsi.lars.backend.datastore.layers.Category;

/**
 * Antwort für eine {@link Category}
 * 
 *
 */
public class CategoryAnswer extends Answer {

	public CategoryAnswer(Category cat, JTextArea comment, JCheckBox notNeeded) {
		super(cat, comment, notNeeded);
	}
	
	/**
	 * Wird nicht verwendet. Kann verwendet werden, um die Category verpflichtend zu machen.
	 * @return Gibt true zurück, wenn die Kategorie verpflichtend sein soll
	 */
	public boolean isNeeded() {
		if(((Category) getLayer()).isMandatory()) {
			return true;
		}
		return ((JRadioButton) getComponents()[0]).isSelected();
	}
	
	/**
	 * siehe {@link MeasureAnswer#getYesNoAnswer()}
	 * @return siehe {@link MeasureAnswer#getYesNoAnswer()}
	 */
	public int getAnswer() {
		JComponent[] c = getComponents();
		for(int i = 1 ; i < c.length ; ++i) {
			if(c[i] instanceof JCheckBox) {
				if(((JCheckBox) c[i]).isSelected()) {
					return 1;//Nicht benötigt
				}else{
					return 0;//Benötigt
				}
			}
		}
		return -1;
	}
	
	public String getComment() {
		JComponent[] c = getComponents();
		for(int i = 0 ; i < c.length ; ++i) {
			if(c[i] instanceof JTextArea) {
				String text = ((JTextArea) c[i]).getText();
				if(text != null) {
					return text;
				}
			}
		}
		return "";
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Antwort für Kategorie ");
		sb.append(getLayer().getName());
		sb.append(' ');

		
		sb.append("Ja: ");
		sb.append(getValue(getComponents()[0]));
		sb.append(' ');
		
		sb.append("Nein: ");
		sb.append(getValue(getComponents()[1]));
		
		appendSubanswers(sb);
		
		return sb.toString();
	}

	/**
	 * Daten aus der Datenbank laden.
	 */
	@Override
	public void load() throws InvalidDatabaseStructureException, NoCaseSelectedException, NoUserSelectedException {
		Comment ans = Backend.getInstance().getComment(getLayer());
		setLoadedComment(ans);
		if(ans != null) {
			if(ans.getStatus() != 0) {
				switch(ans.getStatus()) {
				case -1:
					((JCheckBox) getComponents()[1]).setSelected(false);
					break;
				case -2:
					((JCheckBox) getComponents()[1]).setSelected(true);
					break;
				}
			}
			if(ans.getText() != null) {
				((JTextArea) getComponents()[0]).setText(ans.getText());
			}
		}
		for(Answer sub : getSubanswers()) {
			sub.load();
		}
	}
	
	@Override
	public boolean isFullyAnswered() {
		boolean result = super.isFullyAnswered();
		switch(getAnswer()) {
		case 0:
			return result;
		case 1:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public String toXML() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(getClass().getSimpleName().toLowerCase());
		sb.append(">\n");
		
		sb.append(specificXML());
		
//		for(Answer a : getSubanswers()) {
//			sb.append(a.toXML());
//		}
		
		sb.append("</");
		sb.append(getClass().getSimpleName().toLowerCase());
		sb.append(">\n");
		return sb.toString();
	}
	
	@Override
	protected String specificXML() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<yesno>");
		switch(getAnswer()) {
		case 0:
			sb.append(toCData("yes"));
			break;
		case 1:
			sb.append(toCData("no"));
			break;
		default:
//			sb.append(toCData("not selected"));
			break;
		}
		sb.append("</yesno>\n");
		return sb.toString();
	}
}
