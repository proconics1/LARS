package bsi.lars.gui.editor;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bsi.lars.gui.editor.elements.Element;
import bsi.lars.gui.editor.elements.mapping.MappingElement;

import javax.swing.JSplitPane;

public class TMappingPanel<F extends MappingElement<?, ?>, T extends Element> extends JPanel implements ListSelectionListener, ActionListener, ComponentListener, MouseListener, KeyListener {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.editor.messages"); //$NON-NLS-1$

	private static final long serialVersionUID = -8364826905803846843L;
	private DefaultListModel<T> toMapModel;
	private DefaultListModel<F> elementsModel;
	private DefaultListModel<T> mappedModel;
	private Class<F> clazz;
	private Class<T> clazz2;
	private JList<F> lstElements;
	private JList<T> lstMapped;
	private JList<T> lstToMap;
	private JButton btnRemove;
	private JButton btnAdd;

	/**
	 * Create the panel.
	 */
	public TMappingPanel(Class<F> clazz, Class<T> clazz2) {
		this.clazz = clazz;
		this.clazz2 = clazz2;
		elementsModel = new DefaultListModel<F>();
		mappedModel = new DefaultListModel<T>();
		toMapModel = new DefaultListModel<T>();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JSplitPane splitPane_1 = new JSplitPane();
		GridBagConstraints gbc_splitPane_1 = new GridBagConstraints();
		gbc_splitPane_1.fill = GridBagConstraints.BOTH;
		gbc_splitPane_1.gridwidth = 3;
		gbc_splitPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane_1.gridx = 0;
		gbc_splitPane_1.gridy = 0;
		add(splitPane_1, gbc_splitPane_1);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane_1.setLeftComponent(splitPane);
		
		JPanel panelMap = new JPanel();
		splitPane.setLeftComponent(panelMap);
		GridBagLayout gbl_panelMap = new GridBagLayout();
		gbl_panelMap.columnWidths = new int[]{0, 0, 0};
		gbl_panelMap.rowHeights = new int[]{0, 0};
		gbl_panelMap.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panelMap.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelMap.setLayout(gbl_panelMap);
		
		JLabel lblMap = new JLabel(RESOURCES.getString("TMappingPanel.map")); //$NON-NLS-1$
		GridBagConstraints gbc_lblMap = new GridBagConstraints();
		gbc_lblMap.anchor = GridBagConstraints.NORTH;
		gbc_lblMap.insets = new Insets(0, 0, 0, 5);
		gbc_lblMap.gridx = 0;
		gbc_lblMap.gridy = 0;
		panelMap.add(lblMap, gbc_lblMap);
		
		JScrollPane scrollElement = new JScrollPane();
		GridBagConstraints gbc_scrollElement = new GridBagConstraints();
		gbc_scrollElement.fill = GridBagConstraints.BOTH;
		gbc_scrollElement.gridx = 1;
		gbc_scrollElement.gridy = 0;
		panelMap.add(scrollElement, gbc_scrollElement);
		
		lstElements = new JList<F>();
		scrollElement.setViewportView(lstElements);
		lstElements.setModel(elementsModel);
		
		JPanel panelTo = new JPanel();
		splitPane.setRightComponent(panelTo);
		GridBagLayout gbl_panelTo = new GridBagLayout();
		gbl_panelTo.columnWidths = new int[]{0, 0, 0};
		gbl_panelTo.rowHeights = new int[]{0, 0};
		gbl_panelTo.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panelTo.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelTo.setLayout(gbl_panelTo);
		
		JLabel lblTo = new JLabel(RESOURCES.getString("TMappingPanel.to")); //$NON-NLS-1$
		GridBagConstraints gbc_lblTo = new GridBagConstraints();
		gbc_lblTo.anchor = GridBagConstraints.NORTH;
		gbc_lblTo.insets = new Insets(0, 0, 0, 5);
		gbc_lblTo.gridx = 0;
		gbc_lblTo.gridy = 0;
		panelTo.add(lblTo, gbc_lblTo);
		
		JScrollPane scrollMapped = new JScrollPane();
		GridBagConstraints gbc_scrollMapped = new GridBagConstraints();
		gbc_scrollMapped.fill = GridBagConstraints.BOTH;
		gbc_scrollMapped.gridx = 1;
		gbc_scrollMapped.gridy = 0;
		panelTo.add(scrollMapped, gbc_scrollMapped);
		
		lstMapped = new JList<T>();
		scrollMapped.setViewportView(lstMapped);
		lstMapped.setModel(mappedModel);
		splitPane.setDividerLocation(200);
		
		JPanel panelElements = new JPanel();
		splitPane_1.setRightComponent(panelElements);
		GridBagLayout gbl_panelElements = new GridBagLayout();
		gbl_panelElements.columnWidths = new int[]{0, 0, 0};
		gbl_panelElements.rowHeights = new int[]{0, 0};
		gbl_panelElements.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panelElements.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelElements.setLayout(gbl_panelElements);
		
		JLabel lblElements = new JLabel(RESOURCES.getString("TMappingPanel.elements")); //$NON-NLS-1$
		GridBagConstraints gbc_lblElements = new GridBagConstraints();
		gbc_lblElements.anchor = GridBagConstraints.EAST;
		gbc_lblElements.insets = new Insets(0, 0, 0, 5);
		gbc_lblElements.gridx = 0;
		gbc_lblElements.gridy = 0;
		panelElements.add(lblElements, gbc_lblElements);
		
		JScrollPane scrollToMap = new JScrollPane();
		GridBagConstraints gbc_scrollToMap = new GridBagConstraints();
		gbc_scrollToMap.fill = GridBagConstraints.BOTH;
		gbc_scrollToMap.gridx = 1;
		gbc_scrollToMap.gridy = 0;
		panelElements.add(scrollToMap, gbc_scrollToMap);
		
		lstToMap = new JList<T>();
		scrollToMap.setViewportView(lstToMap);
		lstToMap.setModel(toMapModel);
		splitPane_1.setDividerLocation(400);
		
		lstToMap.addMouseListener(this);
		lstToMap.addKeyListener(this);
		lstToMap.addListSelectionListener(this);
		
		lstMapped.addMouseListener(this);
		lstMapped.addListSelectionListener(this);
		
		lstElements.addListSelectionListener(this);
		
		btnRemove = new JButton(RESOURCES.getString("TMappingPanel.remove")); //$NON-NLS-1$
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.insets = new Insets(0, 0, 0, 5);
		gbc_btnRemove.gridx = 1;
		gbc_btnRemove.gridy = 1;
		add(btnRemove, gbc_btnRemove);
		
		btnAdd = new JButton(RESOURCES.getString("TMappingPanel.add")); //$NON-NLS-1$
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.gridx = 2;
		gbc_btnAdd.gridy = 1;
		add(btnAdd, gbc_btnAdd);

		btnRemove.addActionListener(this);
		btnAdd.addActionListener(this);
		
		loadLists();
		
		addComponentListener(this);
	}

	private void loadLists() {
		loadElementsList();
		loadMappedList();
		loadToMapList();
	}

	@SuppressWarnings("unchecked")
	private void loadElementsList() {
		
		elementsModel.removeAllElements();

		MappingElement<?, ?>[] allMappingElements = EditorLogic.getInstance().getAllMappingElements(clazz);
		if(allMappingElements != null) {
			for (MappingElement<?, ?> e : allMappingElements) {
				elementsModel.addElement((F) e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadMappedList() {
		
		mappedModel.removeAllElements();
		
		F elmnt = lstElements.getSelectedValue();
		if(elmnt != null) {
			MappingElement<?, ?>[] mappedElements = EditorLogic.getInstance().getMapping(elmnt);
			if(mappedElements != null) {
				for (MappingElement<?, ?> e : mappedElements) {
					mappedModel.addElement((T) e.getToElement());
				}
			}
		}
	}
	
	private void loadToMapList() {
		
		toMapModel.removeAllElements();
		
		T[] selection = EditorLogic.getInstance().getElements(clazz2);
		for(T s : selection) {
			toMapModel.addElement(s);
		}
	}

	//ListSelectionListener

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) {
			if(e.getSource().equals(lstElements)) {
				loadMappedList();
			}else if(e.getSource().equals(lstMapped)) {
				
			}else if(e.getSource().equals(lstToMap)) {
//				loadToMapList();
			}
		}
	}
	
	//ActionListener
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(btnAdd)) {
			addMapping();
		}else if(e.getSource().equals(btnRemove)) {
			removeMapping();
		}
	}

	private void addMapping() {
//		HashMap<String, Object> values = new HashMap<String, Object>();
//		values.put(NAME, textName.getText());
//		values.put(DESCRPT, textDescription.getText());
//		gatherMoreInformation(values);
		EditorLogic.getInstance().putMapping(lstElements.getSelectedValue(), lstToMap.getSelectedValue());
//		reload Data
		loadMappedList();
	}
	
	private void removeMapping() {
		EditorLogic.getInstance().removeMapping(lstElements.getSelectedValue(), lstMapped.getSelectedValue());
		loadMappedList();
	}
	
	//MouseListener
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() > 1) {
			if(e.getSource().equals(lstMapped)) {
				removeMapping();
			}else if(e.getSource().equals(lstToMap)) {
				addMapping();
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
	
	//KeyListener
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			addMapping();
		}
	};
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	
	//ComponentListener
	
	@Override
	public void componentHidden(ComponentEvent e) {
	}
	@Override
	public void componentMoved(ComponentEvent e) {
	}
	@Override
	public void componentResized(ComponentEvent e) {
	}
	@Override
	public void componentShown(ComponentEvent e) {
		loadLists();
	}
	
}
