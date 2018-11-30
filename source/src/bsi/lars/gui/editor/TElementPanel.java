package bsi.lars.gui.editor;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JScrollPane;

import java.awt.GridBagConstraints;

import javax.swing.JList;
import javax.swing.JLabel;

import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.FlowLayout;

import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bsi.lars.gui.editor.elements.Element;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

public class TElementPanel<E extends Element> extends JPanel implements ListSelectionListener, ActionListener {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.editor.messages"); //$NON-NLS-1$

	public static final String DESCRPT = "descrpt"; //$NON-NLS-1$

	public static final String NAME = "name"; //$NON-NLS-1$

	private static final long serialVersionUID = -9212588752449756730L;
	
	private JTextField textName;

	private DefaultListModel<E> listModel;

	private Class<E> clazz;

	private JList<E> currentElements;

	private JTextArea textDescription;

	private JButton btnSave;

	private JButton btnDelete;
	
	private boolean isUpdating;

	/**
	 * Create the panel.
	 */
	public TElementPanel(Class<E> clazz) {
		this.clazz = clazz;
		
		setLayout(new BorderLayout(0, 0));
		
		JPanel centerPanel = new JPanel();
		JSplitPane split = new JSplitPane();
		split.setRightComponent(centerPanel);
		add(split);
		
		GridBagLayout gbl_centerPanel = new GridBagLayout();
		gbl_centerPanel.columnWidths = new int[]{0};
		gbl_centerPanel.rowHeights = new int[]{0};
		gbl_centerPanel.columnWeights = new double[]{0.0, 1.0};
		double[] rWeights = new double[]{0.0, 1.0};//, 0.0}; active?
		boolean[] furtherStretch = furtherComponentsVerticalStretch();
		if(furtherStretch.length > 0) {
			double[] tmp = Arrays.copyOf(rWeights, rWeights.length + furtherStretch.length);
			for(int i = 0 ; i < furtherStretch.length ; ++i) {
				tmp[rWeights.length + i] = furtherStretch[i] ? 1.0 : 0.0;
			}
			rWeights = tmp;
		}
		gbl_centerPanel.rowWeights = rWeights;
		centerPanel.setLayout(gbl_centerPanel);
		
		int gridy = 0;
		
		JLabel lblName = new JLabel(RESOURCES.getString("TElementPanel.Name") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = gridy;
		centerPanel.add(lblName, gbc_lblName);
		
		textName = new JTextField();
		GridBagConstraints gbc_textName = new GridBagConstraints();
		gbc_textName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textName.insets = new Insets(0, 0, 5, 5);
		gbc_textName.gridx = 1;
		gbc_textName.gridy = gridy++;
		centerPanel.add(textName, gbc_textName);
		textName.setColumns(10);
		
		JLabel lblDescription = new JLabel(RESOURCES.getString("TElementPanel.Description") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = gridy;
		centerPanel.add(lblDescription, gbc_lblDescription);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = gridy++;
		centerPanel.add(scrollPane, gbc_scrollPane);
		
		textDescription = new JTextArea();
		scrollPane.setViewportView(textDescription);
		
//		JLabel lblActive = new JLabel("Active:");
//		GridBagConstraints gbc_lblActive = new GridBagConstraints();
//		gbc_lblActive.anchor = GridBagConstraints.WEST;
//		gbc_lblActive.insets = new Insets(0, 0, 5, 5);
//		gbc_lblActive.gridx = 1;
//		gbc_lblActive.gridy = gridy;
//		centerPanel.add(lblActive, gbc_lblActive);
//		
//		JCheckBox chckbxActive = new JCheckBox("active?");
//		GridBagConstraints gbc_chckbxActive = new GridBagConstraints();
//		gbc_chckbxActive.anchor = GridBagConstraints.WEST;
//		gbc_chckbxActive.insets = new Insets(0, 0, 5, 5);
//		gbc_chckbxActive.gridx = 2;
//		gbc_chckbxActive.gridy = gridy++;
//		centerPanel.add(chckbxActive, gbc_chckbxActive);
		
		Vector<JComponent[]> furtherComponents = furtherComponents();
		boolean[] s = furtherStretch;
		for (int j = 0; j < furtherComponents.size(); j++) {
			JComponent[] c = furtherComponents.get(j);
			int gridx = 0;
			for (int i = 0; i < c.length; i++) {
				JComponent cc = c[i];
				GridBagConstraints gbc = new GridBagConstraints();
				if(i == c.length - 1) {
					if(s[j]) {
						gbc.fill = GridBagConstraints.BOTH;
					}else{
						gbc.fill = GridBagConstraints.HORIZONTAL;
					}
				}else{
					if(s[j]) {
						gbc.anchor = GridBagConstraints.NORTHWEST;						
					}else{
						gbc.anchor = GridBagConstraints.WEST;
					}
				}
				gbc.insets = new Insets(0, 0, 5, 5);
				gbc.gridx = gridx++;
				gbc.gridy = gridy;
				centerPanel.add(cc, gbc);
			}
			gridy++;
		}

		JScrollPane scrollCurrentElements = new JScrollPane();
//		GridBagConstraints gbc_scrollCurrentElements = new GridBagConstraints();
//		gbc_scrollCurrentElements.fill = GridBagConstraints.BOTH;
//		gbc_scrollCurrentElements.gridheight = gridy+1;
//		gbc_scrollCurrentElements.insets = new Insets(0, 0, 5, 5);
//		gbc_scrollCurrentElements.gridx = 0;
//		gbc_scrollCurrentElements.gridy = 0;
//		centerPanel.add(scrollCurrentElements, gbc_scrollCurrentElements);
		split.setLeftComponent(scrollCurrentElements);
		
		currentElements = new JList<E>();
		scrollCurrentElements.setViewportView(currentElements);
		
		listModel = new DefaultListModel<E>();
		currentElements.setModel(listModel);
		
		JPanel buttonsPanel = new JPanel();
		FlowLayout fl_buttonsPanel = (FlowLayout) buttonsPanel.getLayout();
		fl_buttonsPanel.setAlignment(FlowLayout.RIGHT);
		add(buttonsPanel, BorderLayout.SOUTH);
		
		btnSave = new JButton(RESOURCES.getString("TElementPanel.Save")); //$NON-NLS-1$
		buttonsPanel.add(btnSave);
		
		btnDelete = new JButton(RESOURCES.getString("TElementPanel.Delete")); //$NON-NLS-1$
		btnDelete.setEnabled(false);
		buttonsPanel.add(btnDelete);
		
		loadList();
		
		currentElements.addListSelectionListener(this);
		
		btnSave.addActionListener(this);
		btnDelete.addActionListener(this);
	}
	
	
	@SuppressWarnings("unchecked")
	private void loadList() {
		isUpdating = true;
		listModel.removeAllElements();
		for(Element u : EditorLogic.getInstance().getElements(clazz)) {
			listModel.addElement((E) u);
		}
		isUpdating = false;
	}
	
	private void loadData() {
		E ce = currentElements.getSelectedValue();
		textName.setText(ce.getName());
		textDescription.setText(ce.getDescription());
		loadMoreData(ce);
	}

	protected void loadMoreData(E ce) {
	}

	protected boolean[] furtherComponentsVerticalStretch() {
		return new boolean[0];
	}

	protected Vector<JComponent[]> furtherComponents() {
		return new Vector<JComponent[]>();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!isUpdating && !e.getValueIsAdjusting()) {
			loadData();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(btnSave)) {
			HashMap<String, Object> values = new HashMap<String, Object>();
			values.put(NAME, textName.getText());
			values.put(DESCRPT, textDescription.getText());
			gatherMoreInformation(values);
			EditorLogic.getInstance().putElement(currentElements.getSelectedValue(), values);
			int selected = currentElements.getSelectedIndex();
			loadList();
			currentElements.setSelectedIndex(selected);
			loadData();
		}else if(e.getSource().equals(btnDelete)) {
			
		}
	}

	protected void gatherMoreInformation(HashMap<String, Object> values) {
	}
	
}
