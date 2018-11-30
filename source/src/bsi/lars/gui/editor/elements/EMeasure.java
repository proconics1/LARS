package bsi.lars.gui.editor.elements;

public class EMeasure extends EMeasure3 {

	private int number;
	private int score;

	public EMeasure(int id, String name, String description, int to_measure_id, String description_long, int number, int score) {
		super(id, name, description, to_measure_id, description_long);
		this.number = number;
		this.score = score;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getScore() {
		return score;
	}
}
