package bsi.lars.gui.editor;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bsi.lars.gui.editor.elements.EMeasure;

public class TMeasureElementPanel extends TElementPanel<EMeasure> {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.editor.messages"); //$NON-NLS-1$

	public static final String SCORE = "score"; //$NON-NLS-1$
	public static final String NUMBER = "number"; //$NON-NLS-1$
	public static final String DESCRPT_LONG = "descrpt_long"; //$NON-NLS-1$
	private static final long serialVersionUID = 4125507999732049722L;
	private JTextField textNumber;
	private JTextField textScore;
	private JTextArea textDesc_long;

	/**
	 * Create the panel.
	 */
	public TMeasureElementPanel(Class<EMeasure> clazz) {
		super(clazz);
	}
	
	@Override
	protected Vector<JComponent[]> furtherComponents() {
		Vector<JComponent[]> result = new Vector<JComponent[]>();
		textNumber = new JTextField();
		result.add(new JComponent[]{new JLabel(RESOURCES.getString("TMeasureElementPanel.Number") + ":"), textNumber}); //$NON-NLS-1$ //$NON-NLS-2$
		textScore = new JTextField();
		result.add(new JComponent[]{new JLabel(RESOURCES.getString("TMeasureElementPanel.Score") + ":"), textScore}); //$NON-NLS-1$ //$NON-NLS-2$
		textDesc_long = new JTextArea();
		result.add(new JComponent[]{new JLabel(RESOURCES.getString("TMeasureElementPanel.LongDescription") + ":"), new JScrollPane(textDesc_long)}); //$NON-NLS-1$ //$NON-NLS-2$
		return result;
	}
	
	@Override
	protected boolean[] furtherComponentsVerticalStretch() {
		return new boolean[]{false, false, true};
	}
	
	@Override
	protected void loadMoreData(EMeasure ce) {
		textDesc_long.setText(ce.getDescription_long());
		textNumber.setText(ce.getNumber() + ""); //$NON-NLS-1$
		textScore.setText(ce.getScore() + ""); //$NON-NLS-1$
	}
	
	@Override
	protected void gatherMoreInformation(HashMap<String, Object> values) {
		values.put(DESCRPT_LONG, textDesc_long.getText());
		values.put(NUMBER, Integer.parseInt(textNumber.getText()));
		values.put(SCORE, Integer.parseInt(textScore.getText()));
	}

}
