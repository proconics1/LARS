package bsi.lars.backend;

import java.util.Vector;

public class UpdateCalculationNotifier {

	Vector<UpdateCalculationListener> updateCalculationListeners = new Vector<UpdateCalculationListener>();
	public void addUpdateCalculationListener(UpdateCalculationListener ucl) {
		updateCalculationListeners.add(ucl);
	}
	
	public void notifyUpdateCalculationListeners() {
		for(UpdateCalculationListener ucl : updateCalculationListeners) {
			ucl.valueChanged(new UpdateCalculationEvent(this));
		}
	}
}
