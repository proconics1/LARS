package bsi.lars.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bsi.lars.backend.Backend;
import bsi.lars.backend.NoCaseSelectedException;
import bsi.lars.backend.NoUserSelectedException;
import bsi.lars.backend.datastore.Asset;
import bsi.lars.backend.datastore.layers.AssetType;
import bsi.lars.backend.datastore.layers.Domain;
import javax.swing.JLabel;
import javax.swing.JButton;

/**
 * Element der Graphischen Oberfläche für eine {@link Domain}
 * 
 *
 */
public class DomainPanel extends JPanel implements ActionListener {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.messages"); //$NON-NLS-1$

	private static final long serialVersionUID = 6158091687148384412L;

	private String noUserMsg = RESOURCES.getString("DomainPanel.noUserSelected"); //$NON-NLS-1$
	private String noCaseMsg = RESOURCES.getString("DomainPanel.noCaseSelected"); //$NON-NLS-1$

	private JButton btnOpenAssetmanager;

	public DomainPanel(Domain currentLayer) {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);
		
		JList<Asset> list = new JList<Asset>();
		scrollPane.setViewportView(list);
		
		DefaultListModel<Asset> listModel = new DefaultListModel<Asset>();
		list.setModel(listModel);
		
		JLabel lblAssets = new JLabel(RESOURCES.getString("DomainPanel.assignedAssets")); //$NON-NLS-1$
		add(lblAssets, BorderLayout.NORTH);
		
		btnOpenAssetmanager = new JButton(RESOURCES.getString("DomainPanel.openAssetManager")); //$NON-NLS-1$
		add(btnOpenAssetmanager, BorderLayout.SOUTH);
		btnOpenAssetmanager.addActionListener(this);
		
		try {
			for(Asset a :Backend.getInstance().getAssets((AssetType) currentLayer.getParent(), currentLayer)) {
				listModel.addElement(a);
			}
		} catch (NoCaseSelectedException e) {
			listModel.removeAllElements();
			JOptionPane.showMessageDialog(this, noCaseMsg);
		} catch (NoUserSelectedException e) {
			listModel.removeAllElements();
			JOptionPane.showMessageDialog(this, noUserMsg);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(btnOpenAssetmanager)) {
			TMainFrame.openAssetManager_UI(null);
		}
	}
}
