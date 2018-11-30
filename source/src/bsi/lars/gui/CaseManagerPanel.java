package bsi.lars.gui;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.GridBagLayout;

import javax.swing.JList;

import java.awt.GridBagConstraints;
import java.awt.Dimension;

import javax.swing.JTextField;

import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.ListSelectionModel;

import bsi.lars.backend.Backend;
import bsi.lars.backend.data.Case;
import java.util.ResourceBundle;

/**
 * Element der Graphischen Oberfläche für die Projektverwaltung
 * 
 *
 */
public class CaseManagerPanel extends JPanel {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.messages"); //$NON-NLS-1$

	private static final long serialVersionUID = 8104949057192776854L;

	private JTextField txtCaseName;

	private DefaultListModel<Case> listModel;

	private JButton btnCancel;

	private JButton btnCreateNew;

	private JButton btnDelete;

	private JButton btnLoad;

	private JList<Case> list;

	/**
	 * Create the panel.
	 */
	public CaseManagerPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { Double.MIN_VALUE, 0.0 };
		gridBagLayout.rowWeights = new double[] { Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);

		list = new JList<Case>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(list);
		list.setPreferredSize(new Dimension(100, 0));
		list.setMinimumSize(new Dimension(100, 0));
		listModel = new DefaultListModel<Case>();
		list.setModel(listModel);

		btnLoad = new JButton(RESOURCES.getString("CaseManagerPanel.btnLoad.text")); //$NON-NLS-1$
		GridBagConstraints gbc_btnLoad = new GridBagConstraints();
		gbc_btnLoad.weightx = 0.5;
		gbc_btnLoad.insets = new Insets(0, 5, 5, 5);
		gbc_btnLoad.gridx = 0;
		gbc_btnLoad.gridy = 1;
		add(btnLoad, gbc_btnLoad);

		btnDelete = new JButton(RESOURCES.getString("CaseManagerPanel.btnDelete.text")); //$NON-NLS-1$
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.weightx = 0.5;
		gbc_btnDelete.insets = new Insets(0, 0, 5, 5);
		gbc_btnDelete.gridx = 1;
		gbc_btnDelete.gridy = 1;
		add(btnDelete, gbc_btnDelete);

		txtCaseName = new JTextField();
		txtCaseName.setText(RESOURCES.getString("CaseManagerPanel.txtCaseName.text")); //$NON-NLS-1$
		GridBagConstraints gbc_txtCaseName = new GridBagConstraints();
		gbc_txtCaseName.gridwidth = 2;
		gbc_txtCaseName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCaseName.insets = new Insets(0, 5, 5, 5);
		gbc_txtCaseName.gridx = 0;
		gbc_txtCaseName.gridy = 2;
		add(txtCaseName, gbc_txtCaseName);
		txtCaseName.setColumns(10);

		btnCreateNew = new JButton(RESOURCES.getString("CaseManagerPanel.btnCreateNew.text")); //$NON-NLS-1$
		GridBagConstraints gbc_btnCreateNew = new GridBagConstraints();
		gbc_btnCreateNew.insets = new Insets(0, 5, 5, 5);
		gbc_btnCreateNew.gridx = 0;
		gbc_btnCreateNew.gridy = 3;
		add(btnCreateNew, gbc_btnCreateNew);

		btnCancel = new JButton(RESOURCES.getString("CaseManagerPanel.btnCancel.text")); //$NON-NLS-1$
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 5, 5, 5);
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 3;
		add(btnCancel, gbc_btnCancel);

		loadList();
	}

	private void loadList() {
		listModel.removeAllElements();
		for (Case u : Backend.getInstance().getCases()) {
			listModel.addElement(u);
		}
		if(listModel.size() > 0) {
			list.setSelectedIndex(0);
		}
	}

	public JButton getCancelButton() {
		return btnCancel;
	}

	public JButton getCreateNewButton() {
		return btnCreateNew;
	}

	public JButton getDeleteButton() {
		return btnDelete;
	}

	public JButton getLoadButton() {
		return btnLoad;
	}

	public Case getSelectedCase() {
		return list.getSelectedValue();
	}

	public String getNewCaseName() {
		return txtCaseName.getText();
	}
	
	public JList<Case> getList() {
		return list;
	}

	public void reloadList() {
		loadList();
	}

}
