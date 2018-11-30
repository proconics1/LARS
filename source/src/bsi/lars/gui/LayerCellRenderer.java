package bsi.lars.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import bsi.lars.backend.datastore.layers.Layer;

/**
 * {@link ListCellRenderer} und {@link TreeCellRenderer} für {@link Layer} Elemente
 * 
 *
 */
public class LayerCellRenderer implements ListCellRenderer<Layer>, TreeCellRenderer {

	private static final int iconSize = 10;
	private DefaultListCellRenderer dlcr;
	private DefaultTreeCellRenderer dtcr;

	private static final Icon blackIcon = new Icon() {
		private static final int size = iconSize;
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.BLACK);g.fillOval(x, y, size, size);}
		@Override
		public int getIconWidth() {return size;}
		@Override
		public int getIconHeight() {return size;}
	};

	private static final Icon darkredIcon = new Icon() {
		private static final int size = iconSize;
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.RED.darker());g.fillOval(x, y, size, size);}
		@Override
		public int getIconWidth() {return size;}
		@Override
		public int getIconHeight() {return size;}
	};

	private static final Icon redIcon = new Icon() {
		private static final int size = iconSize;
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.RED);g.fillOval(x, y, size, size);}
		@Override
		public int getIconWidth() {return size;}
		@Override
		public int getIconHeight() {return size;}
	};
	
	private static final Icon orangeIcon = new Icon() {
		private static final int size = iconSize;
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.ORANGE);g.fillOval(x, y, size, size);}
		@Override
		public int getIconWidth() {return size;}
		@Override
		public int getIconHeight() {return size;}
	};
	
	private static final Icon yellowIcon = new Icon() {
		private static final int size = iconSize;
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.YELLOW);g.fillOval(x, y, size, size);}
		@Override
		public int getIconWidth() {return size;}
		@Override
		public int getIconHeight() {return size;}
	};
	
	private static final Icon greenIcon = new Icon() {
		private static final int size = iconSize;
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.GREEN);g.fillOval(x, y, size, size);}
		@Override
		public int getIconWidth() {return size;}
		@Override
		public int getIconHeight() {return size;}
	};
	
	public LayerCellRenderer() {
		dlcr = new DefaultListCellRenderer();
		dtcr = new DefaultTreeCellRenderer();
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Layer> list, Layer value, int index, boolean isSelected, boolean cellHasFocus) {
		return dlcr.getListCellRendererComponent(list, value == null ? "" : value.getName(), index, isSelected, cellHasFocus);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Object v = value;
		dtcr.setLeafIcon(blackIcon);
		dtcr.setOpenIcon(blackIcon);
		dtcr.setClosedIcon(blackIcon);
		if(value instanceof Layer) {
			Layer l = (Layer) value;
			v = l.getName();// +" "+ l.getScore();
			
			Color c = new Color(1, 1, 1, 1);
			switch(l.getScore()) {
			case 0:
				c = Color.RED.darker();
				dtcr.setLeafIcon(darkredIcon);
				dtcr.setOpenIcon(darkredIcon);
				dtcr.setClosedIcon(darkredIcon);
				break;
			case 1:
				c = Color.RED;
				dtcr.setLeafIcon(redIcon);
				dtcr.setOpenIcon(redIcon);
				dtcr.setClosedIcon(redIcon);
				break;
			case 2:
				c = Color.ORANGE;
				dtcr.setLeafIcon(orangeIcon);
				dtcr.setOpenIcon(orangeIcon);
				dtcr.setClosedIcon(orangeIcon);
				break;
			case 3:
				c = Color.YELLOW;
				dtcr.setLeafIcon(yellowIcon);
				dtcr.setOpenIcon(yellowIcon);
				dtcr.setClosedIcon(yellowIcon);
				break;
			case 4:
				c = Color.GREEN;
				dtcr.setLeafIcon(greenIcon);
				dtcr.setOpenIcon(greenIcon);
				dtcr.setClosedIcon(greenIcon);
				break;
			}
			
			Component cmp = dtcr.getTreeCellRendererComponent(tree, v, selected, expanded, leaf, row, hasFocus);

			return new TreeEntryPanel(cmp, l.isUnanswered(), l.isFullyAnswered(), c);
		}
		
		return dtcr.getTreeCellRendererComponent(tree, v, selected, expanded, leaf, row, hasFocus);
	}
	
}
