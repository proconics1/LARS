package bsi.lars.backend.datastore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import bsi.lars.answer.CategoryAnswer;
import bsi.lars.answer.MeasureAnswer;
import bsi.lars.backend.Backend;
import bsi.lars.backend.InvalidDatabaseStructureException;
import bsi.lars.backend.data.Case;
import bsi.lars.backend.data.Comment;
import bsi.lars.backend.data.User;
import bsi.lars.backend.datastore.QueryBuilder.queries;
import bsi.lars.backend.datastore.layers.AssetType;
import bsi.lars.backend.datastore.layers.Category;
import bsi.lars.backend.datastore.layers.Domain;
import bsi.lars.backend.datastore.layers.Layer;
import bsi.lars.backend.datastore.layers.Measure;
import bsi.lars.gui.TMainFrame;

/**
* Represents the connection to the database.
 * This class accepts only the requests and returns the results. There is no processing.
 * 
 *
 */
public class Datastore {
	
	private static final boolean CACHING = true;
	private Backend backend;
	private DataBase db;
	private HashMap<Layer, Layer[]> cache;

	private static Datastore instance;
	
	private Datastore(Backend backend) {
		db = DataBase.getInstance();
		
		this.backend = backend;
		this.cache = new HashMap<Layer, Layer[]>();
	}
	
	public static Datastore init(Backend backend) {
		instance = new Datastore(backend);
		return instance;
	}
	
	public static Datastore getInstance() {
		return instance;
	}
	
	//Modelldaten
	
	public AssetType[] getAssetTypes() throws InvalidLayerStackException {
		{
			AssetType[] cached = (AssetType[]) lookup(null);
			if(cached != null) {
				return cached;
			}
		}
		Vector<AssetType> result = new Vector<AssetType>();
		try{
			ResultSet res = db.executeQuery(QueryBuilder.queries.assettypes);
			while(res.next()) {
				result.add(new AssetType(res.getInt("id"), res.getString("name"), res.getString("descrpt"), backend));
			}
		}catch(SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		
		AssetType[] array = result.toArray(new AssetType[result.size()]);
		
		cache(null, array);
		
		return array;
	}
	
	public Domain[] getDomains(AssetType assetType) throws InvalidLayerStackException {
		{
			Domain[] cached = (Domain[]) lookup(assetType);
			if(cached != null) {
				return cached;
			}
		}
		Vector<Domain> result = new Vector<Domain>();
		try{
			ResultSet res = db.executeQuery(QueryBuilder.queries.domainfrommassettype, assetType.getId());
			while(res.next()) {
				result.add(new Domain(assetType, res.getInt("id"), res.getString("name"), res.getString("descrpt"), backend));
			}
		}catch(SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		
		Domain[] array = result.toArray(new Domain[result.size()]);
		cache(assetType, array);
		return array;
	}

	//                getLayer2
	public Category[] getCategories(Domain domain) throws InvalidLayerStackException {
		{
			Category[] cached = (Category[]) lookup(domain);
			if(cached != null) {
				return cached;
			}
		}
		Vector<Category> result = new Vector<Category>();
		try{
			ResultSet res = db.executeQuery(QueryBuilder.queries.categoryfromdomain, domain.getParent().getId(), domain.getId());
			while(res.next()) { 								//														   verpflichtend oder nicht
				result.add(new Category(domain, res.getInt("id"), res.getString("name"), res.getString("descrpt"), false/*res.getBoolean("mandatory")*/, backend));
			}
		}catch(SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		Category[] array = result.toArray(new Category[result.size()]);
		cache(domain, array);
		return array;
	}

	public Measure[] getMeasures(Category category) throws InvalidLayerStackException {
		return _getMeasures(category);
	}
	
	public Measure[] getMeasures(Measure measure) throws InvalidLayerStackException {
		return _getMeasures(measure);
	}
	
	public Measure[] _getMeasures(Layer layer) throws InvalidLayerStackException {
		{
			Measure[] cached = (Measure[]) lookup(layer);
			if(cached != null) {
				return cached;
			}
		}
		Vector<Measure> result = new Vector<Measure>();
		try{
			Object[] args = null;
			queries query = null;
			
			if(layer instanceof Category) {
				query = queries.measurefromcategory;
				args = new Object[]{layer.getParent().getParent().getId(), layer.getParent().getId(), layer.getId()};
			}else if(layer instanceof Measure) {
				query = queries.measuresfrommeasure;
				args = new Object[]{layer.getId()};
			}else{
				throw new InvalidLayerStackException("Versuchte die Maßnahmen von folgender Schicht zu bekommen: " + layer.getClass() + " (" + layer.toString() + ")");
			}
			
			ResultSet res = db.executeQuery(query, args);
			while(res.next()) {
				Integer to_meas_id = res.getInt("to_measure_id");
//				if(to_meas_id >= 0 && to_meas_id != 1) {
					result.add(new Measure(layer, res.getInt("id"), res.getString("name"), res.getString("descrpt"), to_meas_id, res.getString("descrpt_long"), res.getInt("number"), res.getInt("score"), backend));
//				}
			}
		}catch(SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		
		Measure[] array = result.toArray(new Measure[result.size()]);
		cache(layer, array);
		return array;
	}
	
	
	public Asset[] getAssets(AssetType assetType, Domain domain, Case currentcase) throws InvalidLayerStackException {
		Vector<Asset> result = new Vector<Asset>();
		try{
			ResultSet res = db.executeQuery(QueryBuilder.queries.assetsfromassettypeanddomain, assetType.getId(), domain.getId(), currentcase.getId());
			while(res.next()) {
				result.add(new Asset(assetType, domain, res.getString("name"), backend));
			}
		}catch(SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		Asset[] array = result.toArray(new Asset[result.size()]);
		return array;
	}
	
	

	private Layer[] lookup(Layer layer) {
		if(!CACHING) {
			return null;
		}
		return cache.get(layer);
	}
	
	private void cache(Layer layer, Layer[] children) {
		if(!CACHING) {
			return;
		}
		cache.put(layer, children);
	}
	
	
	//User data
	
	public User[] getUsers() {
		Vector<User> users = new Vector<User>();
		try {
			ResultSet result = db.executeQuery(queries.getusers);
			while(result.next()) {
				users.add(new User(result.getInt("id"), result.getString("longname"), result.getString("shortname"), result.getBoolean("is_editor"), result.getBoolean("is_admin"), result.getBoolean("is_viewer"), result.getString("userpw_hash"), result.getString("userlogin_name")));
			}
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		return users.toArray(new User[users.size()]);
	}
	
	public User getUser(int id) throws InvalidDatabaseStructureException {
		User user = null;
		try {
			ResultSet result = db.executeQuery(queries.getuser, id);
			while(result.next()) {
				if(user != null) {
					throw new InvalidDatabaseStructureException("Mehr als ein Benutzer mit Id " + id);
				}else{
					user = new User(result.getInt("id"), result.getString("longname"), result.getString("shortname"), result.getBoolean("is_editor"), result.getBoolean("is_admin"), result.getBoolean("is_viewer"), result.getString("userpw_hash"), result.getString("userlogin_name"));
				}
			}
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		return user;
	}
	
	//Case data
	
	public Case[] getCases() {
		Vector<Case> cases = new Vector<Case>();
		try {
			ResultSet result = db.executeQuery(queries.getcases);
			while(result.next()) {
				cases.add(new Case(result.getInt("id"), result.getString("name"), result.getString("desc"), result.getInt("user_id"), result.getInt("created_time")));
			}
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		return cases.toArray(new Case[cases.size()]);
	}
	
	public Case getCase(int id) throws InvalidDatabaseStructureException {
		Case c = null;
		try {
			ResultSet result = db.executeQuery(queries.getcase, id);
			while(result.next()) {
				if(c != null) {
					throw new InvalidDatabaseStructureException("Mehr als ein Benutzer mit Id " + id);
				}else{
					c = new Case(result.getInt("id"), result.getString("name"), result.getString("desc"), result.getInt("user_id"), result.getInt("created_time"));
				}
			}
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		return c;
	}
	
	//Answer

	public Comment getCategoryComment(Category c, int case_id) throws InvalidDatabaseStructureException {
		Integer id = null;
		Integer status = null;
		String text = null;
		Integer created_user_id = null;
		Integer edited_user_id = null;
		Long created_time = null;
		Long edited_time = null;
		try {
			ResultSet result = db.executeQuery(queries.getcomment, case_id, c.getParent().getParent().getId(), c.getParent().getId(), c.getId(), 0, 0);
			while(result.next()) {
				if(status != null) {
					throw new InvalidDatabaseStructureException("Mehr als eine Antwort für Kategorie " + c.toDebugString());
				}else{
					id              = result.getInt("id");
					status          = result.getInt("status_id");
					text            = result.getString("text");
					created_user_id = result.getInt("created_user_id");
					edited_user_id  = result.getInt("edited_user_id");
					created_time    = result.getLong("created_time");
					edited_time     = result.getLong("edited_time");
				}
			}
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		if(status == null) {
			return null;//new Comment(0, "");
		}
		return new Comment(id, status, text, created_user_id, edited_user_id, created_time, edited_time);
	}

	public Comment getMeasureComment(Measure m, int case_id) throws InvalidDatabaseStructureException {
		Integer id = null;
		Integer status = null;
		String text = null;
		Integer created_user_id = null;
		Integer edited_user_id = null;
		Long created_time = null;
		Long edited_time = null;
		try {
			Vector<Integer> path = new Vector<Integer>();
			Layer tmp = m;
			do{
				path.add(0, tmp.getId());
				tmp = tmp.getParent();
			}while(tmp != null);
			if(path.size() == 4) {// Wenn es keine Frage auf Ebene 3 gibt, füge Ebene 3 Index = 0 ein.
				path.add(3, 0);
			}
			if(path.size() > 5) {
				throw new SQLException("Zu viele Argumentes.");
			}
			Object[] params = new Object[path.size() + 1];
			params[0] = case_id;
			for(int i = 1 ; i < params.length ; ++i) {
				params[i] = path.get(i - 1);
			}
			ResultSet result = db.executeQuery(queries.getcomment, params);
			while(result.next()) {
				if(status != null) {
					throw new InvalidDatabaseStructureException("Mehr als eine Antwort für Maßnahme " + m.toDebugString());
				}else{
					id              = result.getInt("id");
					status          = result.getInt("status_id");
					text            = result.getString("text");
					created_user_id = result.getInt("created_user_id");
					edited_user_id  = result.getInt("edited_user_id");
					created_time    = result.getLong("created_time");
					edited_time     = result.getLong("edited_time");
				}
			}
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
		if(status == null) {
			return null;//new Comment(0, "");
		}
		return new Comment(id, status, text, created_user_id, edited_user_id, created_time, edited_time);
	}

	public void createCase(String caseName, User user, long created_time) {
		try {
			db.execute(queries.setcase, caseName, user.getId(), created_time);
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
	}
	
	public void deleteCase(Case _case) {
		try {
			db.execute(queries.disablecase, _case.getId());
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
	}
	
	public void createUser(String userLongName, String userName) {
		try {
			db.execute(queries.setuser, userLongName, userName);
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
	}
	
	public void deleteUser(User user) {
		try {
			db.execute(queries.disableuser, user.getId());
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
	}

	public void storeCategoryAnswer(CategoryAnswer answer, int case_id, int user_id, long time) {
		try {
			//-1 Yes
			//-2 No
			// 0 Not set
			int ans = answer.getAnswer();
			if(ans < 0) {
				ans = 0;
			}else{
				if(ans == 0) {
					ans = -1;
				}else if(ans == 1) {
					ans = -2;
				}else{
					throw new IllegalArgumentException("Ungültige Antwort. (Programmierfehler)");
				}
			}
			
			Layer tmp = answer.getLayer();
			
			int measure_id = 0;
			int measure_l3_id = 0;
			int category_id = tmp.getId(); tmp = tmp.getParent();
			int domain_id = tmp.getId(); tmp = tmp.getParent();
			int assettype_id = tmp.getId();
			
			store_Answer(case_id, assettype_id, domain_id, category_id, measure_l3_id, measure_id, answer.getComment(), ans, user_id, time, answer.getLoadedComment());
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
	}

	public void storeMeasureAnswer(MeasureAnswer answer, int case_id, int user_id, long time) {
		try {
			int ans = 0;
			if(answer.isYesNo()) {
				int tmp = answer.getYesNoAnswer();
				if(tmp == 0) {
					ans = -1;
				}else if(tmp == 1) {
					ans = -2;
				}else if(tmp == -1) {
					ans = 0;
				}else{
					throw new IllegalArgumentException("Ungültige Antwort. (Programmierfehler)");
				}
			}else if(answer.isComplex()){
				int tmp = answer.getComplexAnswer();
				if(tmp == -1) {
					ans = 0;
				}else{
					ans = tmp;
				}
			}else{
				throw new IllegalArgumentException("Ungültige Antwort. (Programmierfehler)");
			}

			Layer tmp = answer.getLayer();
			
			int measure_id = tmp.getId(); tmp = tmp.getParent();
			int measure_l3_id;
			if (tmp instanceof Measure) {
				measure_l3_id = tmp.getId();
				tmp = tmp.getParent();
			}else{
				measure_l3_id = 0;
			}
			int category_id = tmp.getId(); tmp = tmp.getParent();
			int domain_id = tmp.getId(); tmp = tmp.getParent();
			int assettype_id = tmp.getId();
			
			store_Answer(case_id, assettype_id, domain_id, category_id, measure_l3_id, measure_id, answer.getComment(), ans, user_id, time, answer.getLoadedComment());
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
	}
	
	private void store_Answer(int case_id, int assettype_id, int domain_id, int category_id, int measure_l3_id, int measure_id, String comment, int status_id
								,int user_id, long time, Comment oldcomment) throws SQLException {
		if(oldcomment == null) {
			if((comment != null && !comment.isEmpty()) || (status_id != 0)) {
				db.execute(queries.insertcomment, case_id, assettype_id, domain_id, category_id, measure_l3_id, measure_id, comment, status_id, user_id, time);
	
				//Hole id der neu eingefügten Zeile
				ResultSet result = db.executeQuery(queries.getcomment, case_id, assettype_id, domain_id, category_id, measure_l3_id, measure_id);
				Integer id = null;
				while(result.next()) {
					if(id  != null) {
						throw new SQLException("Mehr als eine Antwort gefunden.");
					}else{
						id = result.getInt("id");
					}
				}
				if(id == null) {
					throw new SQLException("Zeile nicht eingefügt.");
				}
				
				db.execute(queries.updatecommentcreation, user_id, time, id);
			}
		}else{
			if(!(oldcomment.getText().equals(comment) && oldcomment.getStatus() == status_id)) {
				db.execute(queries.updatecomment, comment, status_id, user_id, time, case_id, assettype_id, domain_id, category_id, measure_l3_id, measure_id);
				
				//Get id of new inserted row
				ResultSet result = db.executeQuery(queries.getcomment, case_id, assettype_id, domain_id, category_id, measure_l3_id, measure_id);
				Integer id = null;
				while(result.next()) {
					if(id  != null) {
						throw new SQLException("Mehr als eine Antwort gefunden.");
					}else{
						id = result.getInt("id");
					}
				}
				if(id == null) {
					throw new SQLException("Zeile nicht eingefügt.");
				}
				
				db.execute(queries.updatecommentcreation, user_id, time, id);
			}
		}
	}

	
	
	public void storeAsset(Asset asset, int case_id) {
		try {
			db.execute(queries.setasset, asset.getAssetType().getId(), asset.getDomain().getId(), asset.getName(), case_id);
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
	}
	
	public void deleteAsset(Asset asset, int case_id) {
		try {
			db.execute(queries.unsetasset, asset.getAssetType().getId(), asset.getDomain().getId(), asset.getName(), case_id);
		} catch (SQLException e) {
			TMainFrame.getInstance().exitOnException(e);
		}
	}
	
}
