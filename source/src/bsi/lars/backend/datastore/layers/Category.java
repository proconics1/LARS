package bsi.lars.backend.datastore.layers;

import javax.swing.JCheckBox;
import javax.swing.JTextArea;

import bsi.lars.answer.CategoryAnswer;
import bsi.lars.backend.Backend;
import bsi.lars.backend.data.Comment;
import bsi.lars.backend.datastore.InvalidLayerStackException;

/**
 * Stellt die Daten der Datenbank dar.
 * 
 *
 */
public class Category extends Layer {

	private boolean mandatory;
	private CategoryAnswer answer;

	public Category(Layer parent, int id, String name, String description, boolean mandatory, Backend backend)throws InvalidLayerStackException {
		super(parent, id, name, description, 2, backend);
		this.mandatory = mandatory;
	}

	@Override
	public boolean isQuestion() {
		return true;
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}

	public CategoryAnswer answer(JTextArea comment, JCheckBox notNeeded) {
		return this.answer = new CategoryAnswer(this, comment, notNeeded);
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
	
	public CategoryAnswer getAnswer() {
		return answer;
	}

	@Override
	protected boolean doCheckSubanswers() {
		return answer.getAnswer() != 1; //If answer is not No, evaluate subAnswers
	}


	@Override
	protected String specificXML() throws Exception {
		Comment c = getBackend().getComment(this);
		
		StringBuilder sb = new StringBuilder();
		sb.append("<created>\n");
		sb.append("<user>");
		sb.append(toCData(getBackend().getUserName(c.getCreated_user_id())));
		sb.append("</user>\n");
		sb.append("<time>");
		sb.append(toCData(c.getCreated_time() + ""));
		sb.append("</time>\n");
		sb.append("</created>\n");


		sb.append("<edited>\n");
		sb.append("<user>");
		sb.append(toCData(getBackend().getUserName(c.getEdited_user_id())));
		sb.append("</user>\n");
		sb.append("<time>");
		sb.append(toCData(c.getEdited_time() + ""));
		sb.append("</time>\n");
		sb.append("</edited>\n");
		
		sb.append(getAnswer().toXML());
		
		return sb.toString();
	}
	
	public String toXMLString(boolean assettype, boolean domain, boolean answer, boolean comment) throws Exception {
		return
				"<category>\n" +
				(assettype ? ("<assettype>" + toCData(getParent().getParent().getName()) + "</assettype>\n") : "") +
				(domain ? ("<domain>" + toCData(getParent().getName()) + "</domain>\n") : "") +
				("<name>" + toCData(getName()) + "</name>\n") +
				(answer ? getAnswer().toXML() : "") +
				(comment ? "<comment>" + toCData(getAnswer().getComment()) + "</comment>\n" : "") +
				"</category>\n";
	}
}
