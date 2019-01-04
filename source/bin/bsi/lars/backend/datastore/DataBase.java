package bsi.lars.backend.datastore;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
* Represents the connection to the database.
 * This class accepts only the requests and returns the results. There is no processing.
 * 
 *
 */
public class DataBase {
	
	private static final DataBase dbcontroller = new DataBase();
	private static Connection connection;
	private static final boolean DEBUG = false;

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("Fehler beim Laden des JDBC-Treibers");
			e.printStackTrace();
		}
	}

	private QueryBuilder queryBuilder;
	private String DB_PATH;

	private DataBase() {
	}

	public static DataBase getInstance() {
		return dbcontroller;
	}
	
	public void initDB(String dbPath) throws FileNotFoundException, SQLException {
		DB_PATH = dbPath;
		if(!new java.io.File(DB_PATH).exists()) {
			throw new FileNotFoundException();
		}
		initDBConnection();
	}

	private void initDBConnection() throws SQLException {
		if (connection != null) {
			return;
		}
		if(DEBUG)System.out.println("Verbinde zur Datenbank ...");
		connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
		if (!connection.isClosed()) {
			if(DEBUG)System.out.println("... Verbindung hergestellt");
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					if (!connection.isClosed() && connection != null) {
						connection.close();
						if (connection.isClosed()) {
							if(DEBUG)System.out.println("Verbindung zur Datenbank geschlossen");
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ResultSet executeQuery(QueryBuilder.queries query, Object ... params) throws SQLException {
		return exec(query, params).executeQuery();
	}
	
	public void execute(QueryBuilder.queries query, Object ... params) throws SQLException {
		exec(query, params);
	}
	
	private PreparedStatement exec(QueryBuilder.queries query, Object ... params) throws SQLException {
		initDBConnection();
		if(this.queryBuilder == null) {
			this.queryBuilder = QueryBuilder.getInstance();
		}
		PreparedStatement pq = queryBuilder.getQuery(connection, query, params);
		pq.execute();
		return pq;
	}
	
}
