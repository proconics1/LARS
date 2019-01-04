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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import bsi.lars.backend.Backend;
import bsi.lars.backend.NoUserSelectedException;

/**
 * Verpackt das {@link CaseManagerPanel} und enthält die Logik für die Projektverwaltung
 * 
 *
 */
public class CaseManagerDialog extends JDialog implements ActionListener, MouseListener {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.messages"); //$NON-NLS-1$

	private static final long serialVersionUID = -3703930563498619644L;
	
	private final JPanel contentPanel = new JPanel();

	private CaseManagerPanel caseManagerPanel;

	/**
	 * Erzeuge den Dialog
	 */
	public CaseManagerDialog(JFrame parent) {
		super(parent, parent==null?ModalityType.TOOLKIT_MODAL:ModalityType.APPLICATION_MODAL);
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(CaseManagerDialog.class.getResource("/resources/Lars.png"))); //$NON-NLS-1$
		
		setTitle(RESOURCES.getString("CaseManagerDialog.pleaseSelectCase")); //$NON-NLS-1$
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			caseManagerPanel = new CaseManagerPanel();
			caseManagerPanel.getCancelButton().addActionListener(this);
			caseManagerPanel.getCreateNewButton().addActionListener(this);
			caseManagerPanel.getDeleteButton().addActionListener(this);
			caseManagerPanel.getLoadButton().addActionListener(this);
			caseManagerPanel.getList().addMouseListener(this);
			contentPanel.add(caseManagerPanel);
		}
		setLocationRelativeTo(parent);
	}

	//ActionListener
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		Object s = ev.getSource();
		if(s != null) {
			if(s.equals(caseManagerPanel.getCancelButton())) {
				this.dispose();
			}else if(s.equals(caseManagerPanel.getCreateNewButton())) {
				try{
					Backend.getInstance().createCase(caseManagerPanel.getNewCaseName());
				}catch(NoUserSelectedException e) {
					JOptionPane.showMessageDialog(this, RESOURCES.getString("CaseManagerDialog.noUserSelected")); //$NON-NLS-1$
				}
				caseManagerPanel.reloadList();
			}else if(s.equals(caseManagerPanel.getDeleteButton())) {
				JOptionPane.showMessageDialog(this, "Delete\n" + caseManagerPanel.getSelectedCase()); //$NON-NLS-1$
				Backend.getInstance().deleteCase(caseManagerPanel.getSelectedCase());
			}else if(s.equals(caseManagerPanel.getLoadButton())) {
				loadCase();
			}
		}
	}

	private void loadCase() {
		Backend.getInstance().selectCase(caseManagerPanel.getSelectedCase());
		this.dispose();
	}
	
	//MouseListener
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource().equals(caseManagerPanel.getList())) {
			if(e.getClickCount() > 1) {
				loadCase();
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
