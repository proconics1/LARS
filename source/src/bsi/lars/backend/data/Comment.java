package bsi.lars.backend.data;

/**
 * Repräsentation eines Kommentars. Stellt die Daten der Datenbank dar.
 * 
 *
 */
public class Comment {

	private int id;
	private int status;
	private String text;
	private int created_user_id;
	private int edited_user_id;
	private long created_time;
	private long edited_time;

	public Comment(int id, int status, String text, int created_user_id, int edited_user_id, long created_time, long edited_time) {
		this.id = id;
		this.status = status;
		this.text = text;
		this.created_user_id = created_user_id;
		this.edited_user_id = edited_user_id;
		this.created_time = created_time;
		this.edited_time = edited_time;
	}
	
	public int getId() {
		return id;
	}

	public int getStatus() {
		return status;
	}
	
	public String getText() {
		return text;
	}
	
	public int getCreated_user_id() {
		return created_user_id;
	}
	
	public int getEdited_user_id() {
		return edited_user_id;
	}
	
	public long getCreated_time() {
		return created_time;
	}
	
	public long getEdited_time() {
		return edited_time;
	}
	
}
