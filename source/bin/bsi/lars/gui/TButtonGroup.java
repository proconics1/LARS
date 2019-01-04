package bsi.lars.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;

/**
 * Erlaubt es, auf dieEvents einer Schaltflächengruppe zu lauschen
 * 
 *
 */
public class TButtonGroup extends ButtonGroup implements ActionListener {
	
	private static final long serialVersionUID = -8397180589258412105L;
	
	private EventListenerList listeners = new EventListenerList();

	private JTextArea comment;
	
	@Override
	public void add(AbstractButton b) {
		b.addActionListener(this);
		super.add(b);
	}
	
	public void setCommentField(JTextArea comment) {
		this.comment = comment;
	}
	
	public JTextArea getCommentField() {
		return comment;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for(ActionListener al : listeners.getListeners(ActionListener.class)) {
			al.actionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand()));
		}
	}

	public void addActionListener(ActionListener a) {
		listeners.add(ActionListener.class, a);
	}
	
	public void removeActionListener(ActionListener a) {
		listeners.remove(ActionListener.class, a);
	}
}
