package bsi.lars.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import bsi.lars.backend.Backend;

/**
 * Verpackt das {@link UserManagerPanel} und enthält die Logik für die Benutzerverwaltung
 * 
 *
 */
public class UserManagerDialog extends JDialog implements ActionListener, MouseListener {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.messages"); //$NON-NLS-1$

	private static final long serialVersionUID = 6969609454057831005L;
	
	private final JPanel contentPanel = new JPanel();

	private UserManagerPanel userManagerPanel;

	/**
	 * Erzeuge den Dialog
	 */
	public UserManagerDialog(JFrame parent) {
		super(parent, parent==null?ModalityType.TOOLKIT_MODAL:ModalityType.APPLICATION_MODAL);
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(UserManagerDialog.class.getResource("/resources/Lars.png"))); //$NON-NLS-1$
		
		setTitle(RESOURCES.getString("UserManagerDialog.pleaseSelectUser")); //$NON-NLS-1$
		setLocationRelativeTo(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			userManagerPanel = new UserManagerPanel();
			userManagerPanel.getSaveButton().addActionListener(this);
			userManagerPanel.getDeleteButton().addActionListener(this);
			userManagerPanel.getSelectButton().addActionListener(this);
			userManagerPanel.getCancelButton().addActionListener(this);
			userManagerPanel.getList().addMouseListener(this);
			contentPanel.add(userManagerPanel);
		}
	}

	//ActionListener
	@Override
	public void actionPerformed(ActionEvent ev) {
		Object s = ev.getSource();
		if(s != null) {
			if(s.equals(userManagerPanel.getSaveButton())) {
				Backend.getInstance().createUser(userManagerPanel.getUserLongName(), userManagerPanel.getUserName());
				userManagerPanel.reloadList();
			}else if(s.equals(userManagerPanel.getDeleteButton())) {
				Backend.getInstance().deleteUser(userManagerPanel.getSelectedUser());
				userManagerPanel.reloadList();
			}else if(s.equals(userManagerPanel.getCancelButton())) {
				this.dispose();
			}else if(s.equals(userManagerPanel.getSelectButton())) {
				loadUser();
			}
		}
	}

	private void loadUser() {
		Backend.getInstance().selectUser(userManagerPanel.getSelectedUser());
		this.dispose();
	}
	
	//MouseListener
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getSource().equals(userManagerPanel.getList())) {
				if(e.getClickCount() > 1) {
					loadUser();
				}
			}
		}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
}
