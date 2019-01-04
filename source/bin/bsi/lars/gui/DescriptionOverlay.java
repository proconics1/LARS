package bsi.lars.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseListener;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

/**
 * Element der Graphischen Oberfl‰che das die langen Kommentare einer Maﬂnahme anzeigt
 * 
 *
 */
public class DescriptionOverlay extends JPanel {

	private static final long serialVersionUID = -841831317000966094L;
	
	private static final Color TRANSPARENT = new Color(0f, 0f, 0f, .5f);
	private static final int MARGIN = 40;

	private JEditorPane txt;

	private JScrollPane scrl;
	
	private JFrame parent;
	
	public DescriptionOverlay(String message, JFrame parent) {
		String msg = format(message);
		setBackground(TRANSPARENT);
		setLayout(null);
		txt = new JEditorPane("text/html", msg);
		txt.setEditable(false);
		txt.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.parent = parent;
		int pw = parent.getContentPane().getWidth();
		int ph = parent.getContentPane().getHeight();
		scrl = new JScrollPane(txt);
		scrl.setBounds(MARGIN, MARGIN, pw - MARGIN - MARGIN, ph - MARGIN - MARGIN);
		scrl.setOpaque(true);
		add(scrl);
		setOpaque(false);
	}
	
	private String format(String str) {
		return str;//"<html>" + str.replaceAll("\r\n", "<br />").replaceAll("\r", "<br />").replaceAll("\n", "<br />") + "</html>";
	}

	@Override
	public void paint(Graphics g) {
		int pw = parent.getContentPane().getWidth();
		int ph = parent.getContentPane().getHeight();
		scrl.setBounds(MARGIN, MARGIN, pw - MARGIN - MARGIN, ph - MARGIN - MARGIN);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		g.setFont(g.getFont().deriveFont(12f));
		g.drawString("Doubleclick to close.", (getWidth() / 2) - 40, getHeight() - 15);
		super.paint(g);
	};
	
	@Override
	public synchronized void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
		txt.addMouseListener(l);
	}

}
