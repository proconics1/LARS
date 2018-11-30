package bsi.lars.gui;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JList;

import java.awt.GridBagConstraints;

import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.Insets;

import javax.swing.JButton;

import java.awt.Dimension;

import javax.swing.ListSelectionModel;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bsi.lars.backend.Backend;
import bsi.lars.backend.data.User;

import java.util.ResourceBundle;

/**
 * Element der Graphischen Oberfläche für die Benutzerverwaltung
 * 
 *
 */
public class UserManagerPanel extends JPanel implements ListSelectionListener {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.messages"); //$NON-NLS-1$

	private static final long serialVersionUID = 369282522838105819L;
	
	private JTextField txtUserLongName;
	private JTextField txtUsername;

	JList<User> list;
	
	private DefaultListModel<User> listModel;

	private JButton btnSave;
	private JButton btnSelect;
	private JButton btnCancel;
	private JButton btnDelete;
	
	public UserManagerPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0};
		setLayout(gridBagLayout);
		

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weightx = 0.5;
		gbc_scrollPane.gridheight = 5;
		gbc_scrollPane.insets = new Insets(5, 5, 0, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);
		
		list = new JList<User>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setMinimumSize(new Dimension(100, 0));
		list.setPreferredSize(new Dimension(100, 0));
		scrollPane.setViewportView(list);
		listModel = new DefaultListModel<User>();
		list.setModel(listModel);
		
		
		txtUserLongName = new JTextField();
		txtUserLongName.setText(RESOURCES.getString("UserManagerPanel.txtUserLongName.text")); //$NON-NLS-1$
		GridBagConstraints gbc_txtUserLongName = new GridBagConstraints();
		gbc_txtUserLongName.gridwidth = 3;
		gbc_txtUserLongName.insets = new Insets(5, 0, 5, 0);
		gbc_txtUserLongName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtUserLongName.gridx = 1;
		gbc_txtUserLongName.gridy = 0;
		add(txtUserLongName, gbc_txtUserLongName);
		txtUserLongName.setColumns(10);
		
		txtUsername = new JTextField();
		txtUsername.setText(RESOURCES.getString("UserManagerPanel.txtUsername.text")); //$NON-NLS-1$
		GridBagConstraints gbc_txtUsername = new GridBagConstraints();
		gbc_txtUsername.gridwidth = 3;
		gbc_txtUsername.insets = new Insets(0, 0, 5, 0);
		gbc_txtUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtUsername.gridx = 1;
		gbc_txtUsername.gridy = 1;
		add(txtUsername, gbc_txtUsername);
		txtUsername.setColumns(10);
		
		btnSave = new JButton(RESOURCES.getString("UserManagerPanel.btnSave.text")); //$NON-NLS-1$
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.weightx = 0.25;
		gbc_btnSave.insets = new Insets(0, 0, 5, 5);
		gbc_btnSave.gridx = 1;
		gbc_btnSave.gridy = 2;
		add(btnSave, gbc_btnSave);
		
		btnDelete = new JButton(RESOURCES.getString("UserManagerPanel.btnDelete.text")); //$NON-NLS-1$
		GridBagConstraints gbc_btnLschen = new GridBagConstraints();
		gbc_btnLschen.insets = new Insets(0, 0, 5, 5);
		gbc_btnLschen.gridx = 2;
		gbc_btnLschen.gridy = 2;
		add(btnDelete, gbc_btnLschen);
		
		Component verticalGlue = Box.createVerticalGlue();
		GridBagConstraints gbc_verticalGlue = new GridBagConstraints();
		gbc_verticalGlue.gridwidth = 2;
		gbc_verticalGlue.insets = new Insets(0, 0, 5, 5);
		gbc_verticalGlue.gridx = 1;
		gbc_verticalGlue.gridy = 3;
		add(verticalGlue, gbc_verticalGlue);
		
		btnSelect = new JButton(RESOURCES.getString("UserManagerPanel.btnChoose.text")); //$NON-NLS-1$
		GridBagConstraints gbc_btnChoose = new GridBagConstraints();
		gbc_btnChoose.insets = new Insets(0, 0, 0, 5);
		gbc_btnChoose.gridx = 1;
		gbc_btnChoose.gridy = 4;
		add(btnSelect, gbc_btnChoose);
		
		btnCancel = new JButton(RESOURCES.getString("UserManagerPanel.btnCancel.text")); //$NON-NLS-1$
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.weightx = 0.25;
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 4;
		add(btnCancel, gbc_btnCancel);
		
		loadList();
		
		list.addListSelectionListener(this);
		
		User u = Backend.getInstance().getCurrentUser();
		if(u != null) {
			list.setSelectedValue(u, true);
		}else{
			list.setSelectedIndex(0);
		}
	}
	
	private void loadList() {
		listModel.removeAllElements();
		for(User u : Backend.getInstance().getUsers()) {
			listModel.addElement(u);
		}
	}

	public String getUserLongName() {
		return txtUserLongName.getText();
	}

	public String getUserName() {
		return txtUsername.getText();
	}

	public JButton getSaveButton() {
		return btnSave;
	}

	public JButton getDeleteButton() {
		return btnDelete;
	}
	
	public JButton getCancelButton() {
		return btnCancel;
	}
	
	public JButton getSelectButton() {
		return btnSelect;
	}
	
	public User getSelectedUser() {
		return list.getSelectedValue();
	}

	public void reloadList() {
		loadList();
		if(listModel.getSize() > 0) {
			list.setSelectedIndex(0);
		}
	}
	
	public JList<User> getList() {
		return list;
	}

	//ListSelectionListener
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) {
			User selectedUser = list.getSelectedValue();
			if(selectedUser != null) {
				txtUsername.setText(selectedUser.getUserlogin_name());
				txtUserLongName.setText(selectedUser.getLongname());
			}
		}
	}
}
