package bsi.lars.gui;

import javax.swing.JPanel;

import bsi.lars.backend.ScoreListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Ampel, die das Ergebnis der Eingaben des Benutzers anzeigt
 * 
 *
 */
public class ResultLight extends JPanel implements ScoreListener {

	private static final long serialVersionUID = 8474073148749525571L;

	private enum states{
		undecided, darkred, red, orange, yellow, green
	}
	
	private states state = states.undecided;
	
	private Color[][] colors; 
	
	private int diameter;
	
	/**
	 * Erzeuge das Panel
	 */
	public ResultLight() {
		diameter = getHeight();
		Dimension size = new Dimension(diameter * 3, diameter);
		setSize(size);
		setPreferredSize(size);
		
		colors = new Color[states.values().length][];
		Color gy = Color.GRAY;
		Color rd = Color.RED;
		Color or = Color.ORANGE;
		Color ye = Color.YELLOW;
		Color gn = Color.GREEN;
		Color dr = Color.RED.darker();
		
		states[] values = states.values();
		for (int i = 0; i < values.length; i++) {
			switch(values[i]) {
			case undecided:
				colors[i] = new Color[]{gy,gy,gy,gy,gy};
				break;
			case darkred:
				colors[i] = new Color[]{dr,gy,gy,gy,gy};
				break;
			case red:
				colors[i] = new Color[]{gy,rd,gy,gy,gy};
				break;
			case orange:
				colors[i] = new Color[]{gy,gy,or,gy,gy};
				break;
			case yellow:
				colors[i] = new Color[]{gy,gy,gy,ye,gy};
				break;
			case green:
				colors[i] = new Color[]{gy,gy,gy,gy,gn};
				break;
			}
		}
	}
	
	@Override
	public void paint(Graphics g) {
		diameter = (int) (getVisibleRect().getWidth() / 5.);
		Dimension size = new Dimension(diameter * 5, diameter);
		setSize(size);
		setPreferredSize(size);

		diameter -= 3;
		
		if(g instanceof Graphics2D) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		int y = (int) ((getVisibleRect().getHeight() - diameter) / 2.);
		super.paint(g);
		for(int i = 0 ; i < 5 ; ++i) {
			g.setColor(colors[state.ordinal()][i]);
			g.fillOval(i * diameter, y, diameter, diameter);
			g.setColor(Color.BLACK);
			g.drawOval(i * diameter, y, diameter, diameter);
		}
	}

	@Override
	public void valueChanged(int score) {
		switch (score) {
		case -1:
			state = states.undecided;
			break;
		case 0:
			state = states.darkred;
			break;
		case 1:
			state = states.red;
			break;
		case 2:
			state = states.orange;
			break;
		case 3:
			state = states.yellow;
			break;
		case 4:
			state = states.green;
			break;
		}
		repaint();
	}
}
