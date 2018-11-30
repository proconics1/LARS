package bsi.lars.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Element der Graphischen Oberfläche für um {@link bsi.lars.backend.datastore.layers.Measure} zu umschließen
 * 
 *
 */
public class PossibleMeasure implements ActionListener, AncestorListener {
	
	private JCheckBox notNeeded;
	private HideGroup no = new HideGroup();
	private HideGroup yes = new HideGroup();

	public PossibleMeasure(JCheckBox notNeeded) {
		this.notNeeded = notNeeded;
		
		notNeeded.addActionListener(this);

		notNeeded.addAncestorListener(this);
	}

	public void add(Integer to_measure_id, HideGroup hgsm) {
		if(to_measure_id < 0) {
			no.add(hgsm);
		}else if(to_measure_id > 0){
			yes.add(hgsm);
		}else{
			throw new RuntimeException("Jemand hat die Datenstruktur zerstört.");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		update();
	}

	private void update() {
		if(notNeeded.isSelected()) {
			yes.hide();
			no.unhide();
		}else{
			yes.unhide();
			no.hide();
		}
	}

	@Override
	public void ancestorAdded(AncestorEvent event) {
		update();
	}

	@Override
	public void ancestorRemoved(AncestorEvent event) {
	}

	@Override
	public void ancestorMoved(AncestorEvent event) {
	}

}
