package bsi.lars.answer;

import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import bsi.lars.backend.Backend;
import bsi.lars.backend.InvalidDatabaseStructureException;
import bsi.lars.backend.NoCaseSelectedException;
import bsi.lars.backend.NoUserSelectedException;
import bsi.lars.backend.data.Comment;
import bsi.lars.backend.datastore.layers.Layer;
import bsi.lars.backend.datastore.layers.Measure;

/**
 * Antwort für eine {@link Measure}
 * 
 *
 */
public class MeasureAnswer extends Answer {

	private enum types{yesno, complex}
	private types type;
	
	public MeasureAnswer(Layer meas, JTextArea comment, JRadioButton yes, JRadioButton no) {
		super(meas, comment, yes, no);
		this.type = types.yesno;
	}

	public MeasureAnswer(Measure meas, JTextArea comment, JRadioButton[] selection) {
		super(meas, append(comment, selection));
		this.type = types.complex;
	}

	public boolean isYesNo() {
		return type.equals(types.yesno);
	}
	
	public boolean isComplex() {
		return type.equals(types.complex);
	}
	
	/**
	 * Gibt 0 für ja, 1 für nein und -1 für nicht gesetzt zurück.
	 * @return Gibt 0 für ja, 1 für nein und -1 für nicht gesetzt zurück.
	 */
	public int getYesNoAnswer() {
		if(isYesNo()) {
			if(((JRadioButton) getComponents()[1]).isSelected()) {
				return 0;
			}
			if(((JRadioButton) getComponents()[2]).isSelected()) {
				return 1;
			}
			return -1;
		}else{
			throw new IllegalArgumentException("YesNo Methode einer Answer aufgerufen, die nicht von Typ YesNo ist.");
		}
	}
	
	/**
	 * 
	 * @return gibt den Index der gewählten Antwort zurück, oder -1 wenn nichts gewählt ist; verwende getComponents()[index] um das gewählte JComponent Objekt zu ermitteln.
	 */
	public int getComplexAnswer() {
		if(isComplex()) {
			JComponent[] c = getComponents();
			for(int i = 0 ; i < c.length ; ++i) {
				if(c[i] instanceof JRadioButton) {
					if(((JRadioButton) c[i]).isSelected()) {
						return i;
					}
				}
			}
			return -1;
		}else{
			throw new IllegalArgumentException("Complex Methode einer Answer aufgerufen, die nicht von Typ Complex ist.");
		}
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
		sb.append("Antwort für Maßnahme ");
		sb.append(getLayer().getName());//.toString());
		sb.append(' ');

		if(isUnanswered())sb.append(" (unbeantwortet) ");
		if(isFullyAnswered())sb.append(" (vollständig beantwortet) ");
		
		switch(type) {
		case yesno:
			sb.append("JaNein: ");
			sb.append(getYesNoAnswer());
			break;
		case complex:
			sb.append("Complexe Antwort: ");
			sb.append(getComplexAnswer());
			break;
		}
		
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
				((JRadioButton) getComponents()[Math.abs(ans.getStatus())]).setSelected(true);
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
	public String specificXML() throws Exception {
		StringBuilder sb = new StringBuilder();
		switch(type) {
		case yesno:
			sb.append("<yesno>");
			switch(getYesNoAnswer()) {
			case 0:
				sb.append(toCData("yes"));
				break;
			case 1:
				sb.append(toCData("no"));
				break;
			default:
				sb.append("");
				break;
			}
			sb.append("</yesno>\n");
			break;
		case complex:
			sb.append("<complex>");
			int answer = getComplexAnswer();
			if(answer >= 0) {
				sb.append(toCData(Backend.getInstance().getStati()[answer-1]));
			}else if(answer == -1) {
				sb.append(toCData(Backend.getInstance().getUnanswered()));
			}else{
				sb.append("");
			}
			sb.append("</complex>\n");
			break;
		default:
			throw new Exception("Implementierungsfehler.");
		}
		return sb.toString();
	}

}
