package bsi.lars.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Verpackt das {@link AssetManagerPanel} und enthält die Logik für die Assetverwaltung
 * 
 *
 */
public class AssetManagerDialog extends JDialog implements ActionListener {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.messages"); //$NON-NLS-1$

	private static final long serialVersionUID = 6969609454057831005L;
	
	private final JPanel contentPanel = new JPanel();

	private AssetManagerPanel assetManagerPanel;

	/**
	 * Erzeuge den Dialog
	 */
	public AssetManagerDialog(JFrame parent) {
		super(parent, parent==null?ModalityType.TOOLKIT_MODAL:ModalityType.APPLICATION_MODAL);
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(AssetManagerDialog.class.getResource("/resources/Lars.png"))); //$NON-NLS-1$
		
		setTitle(RESOURCES.getString("AssetManagerDialog.assetManagement")); //$NON-NLS-1$
		setLocationRelativeTo(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(800, 500);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			assetManagerPanel = new AssetManagerPanel();
//			assetManagerPanel.getSaveButton().addActionListener(this);
//			assetManagerPanel.getDeleteButton().addActionListener(this);
//			assetManagerPanel.getCancelButton().addActionListener(this);
			contentPanel.add(assetManagerPanel);
		}
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		Object s = ev.getSource();
		if(s != null) {
//			if(s.equals(userManagerPanel.getSaveButton())) {
//				Backend.getInstance().createUser(userManagerPanel.getUserLongName(), userManagerPanel.getUserName());
//				userManagerPanel.reloadList();
//				if(getParent() != null && !getParent().isVisible()) {
//					//Only run on startup if there is no user.
//					this.dispose();
//				}
//			}else if(s.equals(userManagerPanel.getDeleteButton())) {
//				JOptionPane.showMessageDialog(this, "Delete user " + userManagerPanel.getSelectedUser()); //$NON-NLS-1$
//			}else if(s.equals(userManagerPanel.getCancelButton())) {
//				this.dispose();
//			}
		}
	}
}
