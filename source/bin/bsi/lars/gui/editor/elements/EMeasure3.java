package bsi.lars.gui.editor.elements;

public class EMeasure3 extends Element {
	
	private int to_measure_id;
	private String description_long;

	public EMeasure3(int id, String name, String description, int to_measure_id, String description_long) {
		super(id, name, description);
		this.to_measure_id = to_measure_id;
		this.description_long = description_long;
	}
	
	public int getTo_measure_id() {
		return to_measure_id;
	}
	
	public String getDescription_long() {
		return description_long;
	}
}
