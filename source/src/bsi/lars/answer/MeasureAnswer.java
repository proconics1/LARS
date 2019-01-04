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
 * Answer for a {@link Measure}
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
	 * Returns 0 for yes, 1 for no, and -1 for not set.
	 * @return Returns 0 for yes, 1 for no, and -1 for not set.
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
			throw new IllegalArgumentException("YesNo Method of an answer called that is not of type YesNo is.");
		}
	}
	
	/**
	 * 
	 * @return returns the index of the selected answer, or -1 if nothing is selected; use getComponents () [index] to get the chosen JComponent object.
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
			throw new IllegalArgumentException("Complex method called an Answer that is not of type complex.");
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
		sb.append("Answer for Action ");
		sb.append(getLayer().getName());//.toString());
		sb.append(' ');

		if(isUnanswered())sb.append(" (unanswered) ");
		if(isFullyAnswered())sb.append(" (completely Answered) ");
		
		switch(type) {
		case yesno:
			sb.append("YesNo: ");
			sb.append(getYesNoAnswer());
			break;
		case complex:
			sb.append("Completely Answered: ");
			sb.append(getComplexAnswer());
			break;
		}
		
		appendSubanswers(sb);
		
		return sb.toString();
	}
	
	/**
	 * Load data from the Database.
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
			throw new Exception("Implementation errors.");
		}
		return sb.toString();
	}

}
