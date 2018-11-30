package bsi.lars.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class LongDescriptionLabel extends JLabel {

	private static final long serialVersionUID = -125944977539853686L;
	
	private String longDescription;

	private String measureName;

	public LongDescriptionLabel(ImageIcon icon) {
		super(icon);
	}

	public void setLongDescription(String text) {
		longDescription = text;
	}
	
	public String getLongDescription() {
		return longDescription;
	}

	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}
	
	public String getMeasureName() {
		return measureName;
	}
}
