package bsi.lars.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Scrollable;

import bsi.lars.answer.CategoryAnswer;
import bsi.lars.backend.Backend;
import bsi.lars.backend.InvalidDatabaseStructureException;
import bsi.lars.backend.NoCaseSelectedException;
import bsi.lars.backend.NoUserSelectedException;
import bsi.lars.backend.UpdateCalculationEvent;
import bsi.lars.backend.UpdateCalculationListener;
import bsi.lars.backend.datastore.layers.Category;
import bsi.lars.backend.datastore.layers.Layer;
import bsi.lars.backend.datastore.layers.Measure;

import javax.swing.JScrollPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Element der Graphischen Oberfläche für eine {@link Category}
 * 
 *
 */
public class CategoryPanel extends JPanel implements ActionListener, Scrollable {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.messages"); //$NON-NLS-1$

	private static final long serialVersionUID = 1304734060733782286L;
	
	private JTextArea txtComment;

	private JCheckBox chkbxNotNeeded;

	private Category category;
	private CategoryAnswer answer;

	public CategoryPanel(Category c) throws NoCaseSelectedException, NoUserSelectedException {
		category = c;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		
		
		JLabel lblName = new JLabel(c.getName());
		lblName.setFont(lblName.getFont().deriveFont(Font.BOLD).deriveFont(13f));
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.gridwidth = 4;
		gbc_lblName.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblName.insets = new Insets(0, 5, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		add(lblName, gbc_lblName);
		
		chkbxNotNeeded = new JCheckBox();
		chkbxNotNeeded.setText(RESOURCES.getString("CategoryPanel.chkbxNotNeeded.text")); //$NON-NLS-1$
		GridBagConstraints gbc_chkbxNotNeeded = new GridBagConstraints();
		gbc_chkbxNotNeeded.gridwidth = 2;
		gbc_chkbxNotNeeded.anchor = GridBagConstraints.NORTHWEST;
		gbc_chkbxNotNeeded.insets = new Insets(0, 0, 5, 5);
		gbc_chkbxNotNeeded.gridx = 0;
		gbc_chkbxNotNeeded.gridy = 1;
		add(chkbxNotNeeded, gbc_chkbxNotNeeded);
		
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		add(scrollPane, gbc_scrollPane);
		
		txtComment = new JTextArea();
		txtComment.setRows(5);
		txtComment.setLineWrap(true);
		scrollPane.setViewportView(txtComment);
//		txtComment.setText("Kommentar");
		txtComment.setColumns(10);
		
		answer = c.answer(txtComment, chkbxNotNeeded);
		
		chkbxNotNeeded.addActionListener(this);
		
		HideGroup hg = new HideGroup(true);
		
		Layer[] measures = category.getChildren();
		PossibleMeasure pm = new PossibleMeasure(chkbxNotNeeded);
		if((measures != null && measures.length > 0)) {
			int startgridy = 2 + 1;// +1 because commentheight
			for(Layer meas : measures) {
				if(meas instanceof Measure) {
					Measure measure = (Measure) meas;
					HideGroup hgm = new HideGroup();
					pm.add(1, hgm);//Add to yes list
					hg.add(hgm);
					startgridy = MeasurePanel.generateMeasurePanel(/*bodyPanel*/this, startgridy, measure, answer, hgm);
				}else{
					add(new JLabel(meas.getClass() + "ist keine Maßnahme. (id:" + meas.getId() + ' ' + meas.toString() + ')'));
				}
			}
		}
		
		try {
			answer.load();
		} catch (InvalidDatabaseStructureException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(chkbxNotNeeded)) {
			changeText(chkbxNotNeeded.isSelected(), txtComment, RESOURCES.getString("CategoryPanel.notNeededDefaultComment")); //$NON-NLS-1$
			revalidate();
		}
		if(e.getSource() instanceof TButtonGroup) {
			for(String status : Backend.getInstance().getStati()) {
				if(e.getActionCommand().equals(status)) {
					JTextArea comment = ((TButtonGroup) e.getSource()).getCommentField();
//					if(comment.getText().isEmpty()) {
//						comment.se tText(Backend.getInstance().getStatusDefaultComment(status));
						for(String stat : Backend.getInstance().getStati()) {
							changeText(false, comment, Backend.getInstance().getStatusDefaultComment(stat));
						}
						changeText(true, comment, Backend.getInstance().getStatusDefaultComment(status));
//					}
				}
			}
		}
		notifyUpdateCalculationListeners();
	}
	
	/**
	 * Ändere den Text eines Kommentarfeldes
	 * @param prepend wenn true, wird der übergebene Text vorne angesetzt, wenn false wird der übergebene Text entfernt wenn er am Anfang des Textes im Kommentarfeld steht
	 * @param commentField das Kommentarfeld, das geändert werden soll
	 * @param text der Text, der vorne angesetzt, oder entfernt werden soll
	 */
	private void changeText(boolean prepend, JTextArea commentField, String text) {
		if(prepend) {
			//prepend
			if(!commentField.getText().startsWith(text)) {
				commentField.setText(text + " " + commentField.getText());
			}
		}else{
			//deprepend
			if(commentField.getText().startsWith(text)) {
				commentField.setText(commentField.getText().substring(text.length() + 1));
			}
		}
	}

	public CategoryAnswer getAnswer() {
		return answer;
	}

	//Scrollable
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getParent().getParent().getSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 50;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	Vector<UpdateCalculationListener> updateCalculationListeners = new Vector<UpdateCalculationListener>();
	public void addUpdateCalculationListener(UpdateCalculationListener ucl) {
		updateCalculationListeners.add(ucl);
	}
	
	private void notifyUpdateCalculationListeners() {
		for(UpdateCalculationListener ucl : updateCalculationListeners) {
			ucl.valueChanged(new UpdateCalculationEvent(this));
		}
	}

}
