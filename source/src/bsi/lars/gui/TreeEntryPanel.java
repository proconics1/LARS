package bsi.lars.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;

/**
 * Element der Graphischen Oberfläche für einen {@link bsi.lars.backend.datastore.layers.Layer} im {@link javax.swing.JTree}
 * 
 *
 */
public class TreeEntryPanel extends JPanel {

	private static final long serialVersionUID = -8443764900330622139L;
	
	private static ImageIcon emptyIcon;
	private static ImageIcon fullAnswIcon;
	private static ImageIcon unAnswIcon;
	
	/**
	 * Create the panel.
	 * @param fullyAnswered 
	 * @param unanswered 
	 * @param cmp 
	 */
	public TreeEntryPanel(Component cmp, boolean unanswered, boolean fullyAnswered, Color background) {
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		
		if(emptyIcon == null) {
			emptyIcon = new ImageIcon(new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB));
		}
		int iconsize = 12;
		if(fullAnswIcon == null) {
			Icon tmp = UIManager.getIcon("OptionPane.warningIcon");
			if(tmp instanceof ImageIcon) {
				fullAnswIcon = new ImageIcon(((ImageIcon) tmp).getImage().getScaledInstance(iconsize, iconsize, Image.SCALE_SMOOTH));
			}
			if(tmp == null) {
				iconsize *= 2;
				BufferedImage img = new BufferedImage(iconsize, iconsize, BufferedImage.TYPE_INT_ARGB);
				Graphics g = img.getGraphics();
				int[] xs,ys = null;int count = 0;
				if(iconsize % 2 == 0) {
					xs = new int[]{0, (iconsize/2)-1, iconsize/2, iconsize - 1};
					ys = new int[]{iconsize - 1, 0, 0, iconsize - 1};
					count = 4;
					g.fillPolygon(xs, ys, count);
					g.drawPolygon(xs, ys, count);
				}else{
					xs = new int[]{0, iconsize/2, iconsize - 1};
					ys = new int[]{iconsize - 1, 0, iconsize - 1};
					count = 3;
				}
				g.setColor(Color.YELLOW);
				g.fillPolygon(xs, ys, count);
				g.setColor(Color.BLACK);
				g.drawPolygon(xs, ys, count);

				iconsize /= 2;
				Image triangle = img.getScaledInstance(iconsize, iconsize, Image.SCALE_SMOOTH);
				img = new BufferedImage(iconsize, iconsize, BufferedImage.TYPE_INT_ARGB);
				
				g = img.getGraphics();
				
				g.drawImage(triangle, 0, 0, null);
				g.setColor(Color.BLACK);
				float fontsize = 11;
				g.setFont(Font.getFont("Arial", g.getFont()).deriveFont(fontsize).deriveFont(Font.BOLD));
				g.drawString("!", (int) ((((float)iconsize) - .1*fontsize) / 2.), (int) ((((float)iconsize) + 1.*fontsize) / 2.));
				
				fullAnswIcon = new ImageIcon(img);
			}
		}
		if(unAnswIcon == null) {
			Icon tmp = UIManager.getIcon("OptionPane.errorIcon");
			if(tmp instanceof ImageIcon) {
				unAnswIcon = new ImageIcon(((ImageIcon) tmp).getImage().getScaledInstance(iconsize, iconsize, Image.SCALE_SMOOTH));
			}
			if(tmp == null) {
				iconsize *= 2;
				BufferedImage img = new BufferedImage(iconsize, iconsize, BufferedImage.TYPE_INT_ARGB);
				Graphics g = img.getGraphics();
				g.setColor(Color.BLACK);
				g.drawOval(0, 0, iconsize, iconsize);
				g.setColor(Color.WHITE);
				g.drawOval(1, 1, iconsize - 2, iconsize - 2);
				g.setColor(Color.RED);
				g.fillOval(2, 2, iconsize - 4, iconsize - 4);
				g.setColor(Color.WHITE);
				int fontsize = 5;//is not fontsize fontsize would be float, but works
				iconsize /= 2;
				Image circle = img.getScaledInstance(iconsize, iconsize, Image.SCALE_SMOOTH);
				img = new BufferedImage(iconsize, iconsize, BufferedImage.TYPE_INT_ARGB);
				g = img.getGraphics();
				g.drawImage(circle, 0, 0, null);
				g.setColor(Color.WHITE);
				g.setFont(Font.getFont("Arial", g.getFont()).deriveFont(11f).deriveFont(Font.BOLD));
				g.drawString("X", (int) ((((float)iconsize) - 1.*fontsize) / 2.), (int) ((((float)iconsize) + 1.6*fontsize) / 2.));
				
				unAnswIcon = new ImageIcon(img);
			}
		}
		
		setBackground(new Color(1, 1, 1, 1));

		JLabel lblStatus = new JLabel(emptyIcon);
		
		if(!fullyAnswered) {
			lblStatus.setIcon(fullAnswIcon);
		}
		if(unanswered) {
			lblStatus.setIcon(unAnswIcon);
		}
		
		add(cmp);
		add(lblStatus);
	}
	
	class JCircleLabel extends JLabel {
		private static final long serialVersionUID = 4470612087400727525L;

		private Color color;
		
		public JCircleLabel(Color c) {
			super("OO");
			setForeground(new Color(1, 1, 1, 1));
			color = c;
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(color);
			int diam = Math.min(getWidth(), getHeight());
			g.fillOval(0, 0, diam, diam);
		}
	}
}
