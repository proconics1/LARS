package bsi.lars.gui.editor;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bsi.lars.gui.editor.elements.EMeasure3;

public class TMeasure3ElementPanel extends TElementPanel<EMeasure3> {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.editor.messages"); //$NON-NLS-1$

	public static final String DESCRPT_LONG = "descrpt_long"; //$NON-NLS-1$
	private static final long serialVersionUID = 4125507999732049722L;
	private JTextArea textDesc_long;

	/**
	 * Create the panel.
	 */
	public TMeasure3ElementPanel(Class<EMeasure3> clazz) {
		super(clazz);
	}
	
	@Override
	protected Vector<JComponent[]> furtherComponents() {
		Vector<JComponent[]> result = new Vector<JComponent[]>();
		textDesc_long = new JTextArea();
		result.add(new JComponent[]{new JLabel(RESOURCES.getString("TMeasure3ElementPanel.LongDescription") + ":"), new JScrollPane(textDesc_long)}); //$NON-NLS-1$ //$NON-NLS-2$
		return result;
	}
	
	@Override
	protected boolean[] furtherComponentsVerticalStretch() {
		return new boolean[]{true};
	}
	
	@Override
	protected void loadMoreData(EMeasure3 ce) {
		textDesc_long.setText(ce.getDescription_long());
	}

	@Override
	protected void gatherMoreInformation(HashMap<String, Object> values) {
		values.put(DESCRPT_LONG, textDesc_long.getText());
	}
}
