package bsi.lars.backend.data;

/**
 * Repräsentation eines Benutzers. Stellt die Daten der Datenbank dar.
 * 
 *
 */
public class User {
	private int id;
	private String longname;
	private String shortname;
	private boolean is_editor;
	private boolean is_admin;
	private boolean is_viewer;
	private String userpw_hash;
	private String userlogin_name;

	public User(int id, String longname, String shortname, boolean is_editor, boolean is_admin, boolean is_viewer, String userpw_hash, String userlogin_name) {
		this.id             = id            ;
		this.longname       = longname      ;
		this.shortname      = shortname     ;
		this.is_editor      = is_editor     ;
		this.is_admin       = is_admin      ;
		this.is_viewer      = is_viewer     ;
		this.userpw_hash    = userpw_hash   ;
		this.userlogin_name = userlogin_name;
	}
	
	public int getId() {
		return id;
	}

	public String getLongname() {
		return longname;
	}

	public String getShortname() {
		return shortname;
	}

	public boolean isEditor() {
		return is_editor;
	}

	public boolean isAdmin() {
		return is_admin;
	}

	public boolean isViewer() {
		return is_viewer;
	}

	public String getUserpw_hash() {
		return userpw_hash;
	}

	public String getUserlogin_name() {
		return userlogin_name;
	}

	@Override
	public String toString() {
		return longname;
	}

	public String toXML() {
		return "<user>" + toCData(getLongname()) + "</user>";
	}

	protected String toCData(String data) {
		return "<![CDATA["+data+"]]>";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User) {
			return ((User) obj).getId() == getId();
		}
		return false;
	}
}
