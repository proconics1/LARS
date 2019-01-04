package bsi.lars.backend.data;

/**
 * Representation of a project. Represents the data of the database
 * 
 *
 */
public class Case {

	private int id;
	private String name;
	private String description;
	private int user_id;
	private int created_time;

	public Case(int id, String name, String desc, int user_id, int created_time) {
		this.id           = id          ;
		this.name         = name        ;
		this.description  = desc        ;
		this.user_id      = user_id     ;
		this.created_time = created_time;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getUser_id() {
		return user_id;
	}

	public int getCreated_time() {
		return created_time;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String toXML() {
		return "<case>" + toCData(toString()) + "</case>\n";
	}

	protected String toCData(String data) {
		return "<![CDATA["+data+"]]>";
	}
}
