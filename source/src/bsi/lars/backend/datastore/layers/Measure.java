package bsi.lars.backend.datastore.layers;

import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import bsi.lars.answer.MeasureAnswer;
import bsi.lars.backend.Backend;
import bsi.lars.backend.data.Comment;
import bsi.lars.backend.datastore.InvalidLayerStackException;

/**
 * Stellt die Daten der Datenbank dar.
 * 
 *
 */
public class Measure extends Layer {

	private Integer to_measure_id;
	private String description_long;
	private int number;
	private MeasureAnswer answer;
	private int measurescore;

	public Measure(Layer parent, int id, String name, String description, Integer to_measure_id, String description_long, int number, int measurescore, Backend backend) throws InvalidLayerStackException {
		super(parent, id, name, description, backend);
		this.to_measure_id = to_measure_id;
		this.description_long = description_long;
		this.number = number;
		this.measurescore = measurescore;
	}

	@Override
	public boolean isQuestion() {
		return true;
	}
	
	public Integer getTo_measure_id() {
		return to_measure_id;
	}

	public String getDescriptionLong() {
		return description_long;
	}

	public int getNumber() {
		return number;
	}
	
	public int getMeasureScore() {
		return measurescore;
	}

	public MeasureAnswer answer(JTextArea comment, JRadioButton yes, JRadioButton no) {
		return this.answer = new MeasureAnswer(this, comment, yes, no);
	}

	public MeasureAnswer answer(JTextArea comment, JRadioButton[] selection) {
		return this.answer = new MeasureAnswer(this, comment, selection);
	}
	
	@Override
	protected boolean isAnswerable() {
		return true;
	}
	
	@Override
	protected boolean isLocalFullyAnswered() {
		if(answer == null) {
			return false;
		}
		return answer.isFullyAnswered();
	}
	
	@Override
	protected boolean isLocalUnanswered() {
		if(answer == null) {
			return true;
		}
		return answer.isUnanswered();
	}
	
	public boolean isLayer3() {
		if(getNumber() != 0) {
			return false;
		}
		return true;
	}
	
	public MeasureAnswer getAnswer() {
		return answer;
	}
	
	@Override
	protected boolean doCheckSubanswers() {
		return true;
	}


	@Override
	protected String specificXML() throws Exception {
		Comment c = getBackend().getComment(this);
		
		StringBuilder sb = new StringBuilder();

		sb.append("<created>\n");
		if(c != null) {
			sb.append("<user>");
			sb.append(toCData(getBackend().getUserName(c.getCreated_user_id())));
			sb.append("</user>\n");
			sb.append("<time>");
			sb.append(toCData(c.getCreated_time() + ""));
			sb.append("</time>\n");
		}
		sb.append("</created>\n");


		sb.append("<edited>\n");
		if(c != null) {
			sb.append("<user>");
			sb.append(toCData(getBackend().getUserName(c.getEdited_user_id())));
			sb.append("</user>\n");
			sb.append("<time>");
			sb.append(toCData(c.getEdited_time() + ""));
			sb.append("</time>\n");
		}
		sb.append("</edited>\n");

		sb.append("<score>\n");
		sb.append(getMeasureScore());
		sb.append("</score>\n");
		
		sb.append(getAnswer().toXML());
		
		return sb.toString();
	}
	
	public String toXMLString(boolean assettype, boolean domain, boolean category, boolean answer, boolean comment) throws Exception {
		return
				"<measure>\n" +
				(assettype ? ("<assettype>" + toCData(getParent().getParent().getParent().getName()) + "</assettype>\n") : "") +
				(domain ? ("<domain>" + toCData(getParent().getParent().getName()) + "</domain>\n") : "") +
				(category ? ("<category>" + toCData(getParent().getName()) + "</category>\n") : "") +
				("<name>" + toCData(getName()) + "</name>\n") +
				(answer ? getAnswer().toXML() : "") +
				(comment ? "<comment>" + toCData(getAnswer().getComment()) + "</comment>\n" : "") +
				("<measurescore>" + (getMeasureScore() == 0 ? "KO" : getMeasureScore()) + "</measurescore>") +
				"</measure>\n";
	}
}
