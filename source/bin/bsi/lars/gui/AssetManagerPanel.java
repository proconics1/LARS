package bsi.lars.gui;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JTextField;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bsi.lars.backend.Backend;
import bsi.lars.backend.NoCaseSelectedException;
import bsi.lars.backend.NoUserSelectedException;
import bsi.lars.backend.datastore.Asset;
import bsi.lars.backend.datastore.InvalidLayerStackException;
import bsi.lars.backend.datastore.layers.AssetType;
import bsi.lars.backend.datastore.layers.Domain;
import bsi.lars.backend.datastore.layers.Layer;

import javax.swing.ListSelectionModel;

/**
 * Element der Graphischen Oberfläche für die Assetverwaltung
 * 
 *
 */
public class AssetManagerPanel extends JPanel implements ListSelectionListener, ActionListener {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.messages"); //$NON-NLS-1$

	private JSplitPane splitPane;
	private JSplitPane splitPane_1;
	private JSplitPane splitPane_2;
	private DefaultListModel<Asset> listModelAsset;
	private DefaultListModel<Domain> listModelDomain;
	private DefaultListModel<AssetType> listModelAssetType;
	private JList<Asset> listAsset;
	private JList<Domain> listDomain;
	private JList<AssetType> listAssetType;
	private String noUserMsg = RESOURCES.getString("AssetManagerPanel.noUserSelected"); //$NON-NLS-1$
	private String noCaseMsg = RESOURCES.getString("AssetManagerPanel.noCaseSelected"); //$NON-NLS-1$

	private JButton btnAdd;

	private JButton btnDelete;
	
	public AssetManagerPanel() {
		setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		
		splitPane_1 = new JSplitPane();
		splitPane.setRightComponent(splitPane_1);
		
		splitPane_2 = new JSplitPane();
		splitPane_1.setRightComponent(splitPane_2);
		
		JPanel panelAsset = new JPanel();
		splitPane_2.setLeftComponent(panelAsset);
		panelAsset.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneAsset = new JScrollPane();
		panelAsset.add(scrollPaneAsset, BorderLayout.CENTER);
		
		listAsset = new JList<Asset>();
		listAsset.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listModelAsset = new DefaultListModel<Asset>();
		listAsset.setModel(listModelAsset);
		scrollPaneAsset.setViewportView(listAsset);
		
		JLabel lblAsset = new JLabel(RESOURCES.getString("AssetManagerPanel.assets")); //$NON-NLS-1$
		panelAsset.add(lblAsset, BorderLayout.NORTH);
		
		btnDelete = new JButton(RESOURCES.getString("AssetManagerPanel.delete")); //$NON-NLS-1$
		panelAsset.add(btnDelete, BorderLayout.SOUTH);
		
		JPanel panelNewAsset = new JPanel();
		splitPane_2.setRightComponent(panelNewAsset);
		GridBagLayout gbl_panelNewAsset = new GridBagLayout();
		gbl_panelNewAsset.columnWidths = new int[]{0, 0, 0};
		gbl_panelNewAsset.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panelNewAsset.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panelNewAsset.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelNewAsset.setLayout(gbl_panelNewAsset);
		
		JLabel lblNewAsset = new JLabel(RESOURCES.getString("AssetManagerPanel.asset")); //$NON-NLS-1$
		GridBagConstraints gbc_lblNewAsset = new GridBagConstraints();
		gbc_lblNewAsset.gridwidth = 2;
		gbc_lblNewAsset.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewAsset.gridx = 0;
		gbc_lblNewAsset.gridy = 1;
		panelNewAsset.add(lblNewAsset, gbc_lblNewAsset);
		
		txtAssetName = new JTextField();
		GridBagConstraints gbc_txtAssetName = new GridBagConstraints();
		gbc_txtAssetName.gridwidth = 2;
		gbc_txtAssetName.insets = new Insets(0, 5, 5, 5);
		gbc_txtAssetName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtAssetName.gridx = 0;
		gbc_txtAssetName.gridy = 2;
		panelNewAsset.add(txtAssetName, gbc_txtAssetName);
		txtAssetName.setColumns(10);
		
		btnAdd = new JButton(RESOURCES.getString("AssetManagerPanel.add")); //$NON-NLS-1$
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.gridx = 1;
		gbc_btnAdd.gridy = 3;
		panelNewAsset.add(btnAdd, gbc_btnAdd);
		splitPane_2.setDividerLocation(150);
		
		JPanel panelDomain = new JPanel();
		splitPane_1.setLeftComponent(panelDomain);
		panelDomain.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneDomain = new JScrollPane();
		panelDomain.add(scrollPaneDomain, BorderLayout.CENTER);
		
		listDomain = new JList<Domain>();
		listDomain.setCellRenderer(new LayerCellRenderer());
		listDomain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listModelDomain = new DefaultListModel<Domain>();
		listDomain.setModel(listModelDomain);
		scrollPaneDomain.setViewportView(listDomain);
		
		JLabel lblDomain = new JLabel(RESOURCES.getString("AssetManagerPanel.domain")); //$NON-NLS-1$
		panelDomain.add(lblDomain, BorderLayout.NORTH);
		splitPane_1.setDividerLocation(150);
		
		JPanel panelAssetType = new JPanel();
		splitPane.setLeftComponent(panelAssetType);
		panelAssetType.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneAssetType = new JScrollPane();
		panelAssetType.add(scrollPaneAssetType, BorderLayout.CENTER);
		
		listAssetType = new JList<AssetType>();
		listAssetType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listModelAssetType = new DefaultListModel<AssetType>();
		listAssetType.setModel(listModelAssetType);
		scrollPaneAssetType.setViewportView(listAssetType);
		
		JLabel lblAssettype = new JLabel(RESOURCES.getString("AssetManagerPanel.assetType")); //$NON-NLS-1$
		panelAssetType.add(lblAssettype, BorderLayout.NORTH);
		splitPane.setDividerLocation(150);

		btnAdd.addActionListener(this);
		btnDelete.addActionListener(this);
		listAssetType.addListSelectionListener(this);
		listDomain.addListSelectionListener(this);
		
		loadAssetTypes();
	}
	
	private void loadAssetTypes() {
		listModelAssetType.removeAllElements();
		for(AssetType at : Backend.getInstance().getAssetTypes()) {
			listModelAssetType.addElement(at);
		}
	}
	
	private void loadDomains() throws InvalidLayerStackException {
		listModelDomain.removeAllElements();
		AssetType at = listAssetType.getSelectedValue();
		if(at != null) {
			for(Layer l : Backend.getInstance().getElements(at)) {
				if(l instanceof Domain) {
					listModelDomain.addElement((Domain) l);
				}else{
					throw new InvalidLayerStackException("Found " + l + " beneath AssetType"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}
	
	private void loadAssets() {
		listModelAsset.removeAllElements();

		AssetType at = listAssetType.getSelectedValue();
		Domain d = listDomain.getSelectedValue();
		if(at != null && d != null) {
			try {
				for(Asset a : Backend.getInstance().getAssets(at, d)) {
					listModelAsset.addElement(a);
				}
			} catch (NoCaseSelectedException e) {
				JOptionPane.showMessageDialog(this, noCaseMsg);
			} catch (NoUserSelectedException e) {
				JOptionPane.showMessageDialog(this, noUserMsg);
			}
		}
	}
	
	private void saveAsset() {
		AssetType at = listAssetType.getSelectedValue();
		Domain d = listDomain.getSelectedValue();
		String name = txtAssetName.getText();
		if(at != null && d != null) {
			if(!name.isEmpty()) {
				try {
					Backend.getInstance().writeAsset(new Asset(at, d, name, Backend.getInstance()));
					loadAssets();
				} catch (NoCaseSelectedException e) {
					JOptionPane.showMessageDialog(this, noCaseMsg);
				} catch (NoUserSelectedException e) {
					JOptionPane.showMessageDialog(this, noUserMsg);
				}
			}
		}
	}
	
	private void deleteAsset() {
		Asset a = listAsset.getSelectedValue();
		if(a != null) {
			try {
				Backend.getInstance().deleteAsset(a);
				loadAssets();
			} catch (NoCaseSelectedException e) {
				JOptionPane.showMessageDialog(this, noCaseMsg);
			} catch (NoUserSelectedException e) {
				JOptionPane.showMessageDialog(this, noUserMsg);
			}
		}
	}

	private static final long serialVersionUID = 1966011365632686835L;
	private JTextField txtAssetName;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

	
	//ListSelectionListener
	
	@Override
	public void valueChanged(ListSelectionEvent ev) {
		if(!ev.getValueIsAdjusting()) {
			if(ev.getSource().equals(listAssetType)) {
				try {
					loadDomains();
				} catch (InvalidLayerStackException e) {
					TMainFrame.getInstance().exitOnException(e);
				}
			}else if(ev.getSource().equals(listDomain)) {
				loadAssets();
			}
		}
	}
	
	//ActionListener
	//TODO: extract Action code to AssetManagerDialog
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(btnAdd)) {
			saveAsset();
		}else if(e.getSource().equals(btnDelete)) {
			deleteAsset();
		}
	}
}
