package bsi.lars.backend.datastore;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.HashMap;
import java.util.Properties;

/**
 * Klasse die die Anfragen bereitstellt.
 * Manche Anfragen werden gepuffert, um die Ausführung zu Beschleunigen.
 * 
 *
 */
public class QueryBuilder {

	private static final String queriesFile = "queries.txt";
	private static final boolean DEBUG = false;
	private static QueryBuilder instance;
	private Properties queriesList;
	private HashMap<queries, HashMap<Object[], PreparedStatement>> cache;

	/**
	 * Dieser Enum ist das Kernstück dieser Klasse. Alle Anfragen müssen hier referenziert werden.
	 * 
	 *
	 */
	public enum queries{
		assettypes, domains, categories, measures3, measures,
		domainfrommassettype, categoryfromdomain, measurefromcategory, measuresfrommeasure,
		assetsfromassettypeanddomain, setasset, unsetasset,
		getusers, getuser, setuser, disableuser,
		getcases, getcase, setcase, disablecase,
		getcomment, insertcomment, updatecomment, updatecommentcreation,
		a2d, ad2c, adc2m3, adcm32m, adc2m,
		setassettype, setdomain, setcategory, setmeasure3, setmeasure,
		set_a2d, set_ad2c, set_adc2m3, set_adcm32m, set_adc2m,
		unset_a2d, unset_ad2c, unset_adc2m3, unset_adcm32m, unset_adc2m
	}
	
	private QueryBuilder() throws SQLException {
		InputStream is = QueryBuilder.class.getResourceAsStream("/resources/" + queriesFile);
		if(is != null) {
			queriesList = new Properties();
			try {
				queriesList.load(is);
				if(DEBUG) {
					System.out.println("Folgende Anfragen in der Datei gefunden:");
					for(String key : queriesList.stringPropertyNames()) {
						System.out.println(key);
					}
				}
				for(queries q : queries.values()) {
					if(!queriesList.containsKey(q.name())) {
						throw new SQLException("Konnte Anfrage " + q.name() + " in folgender Datei nicht finden: " + queriesFile);
					}
				}
				if(queriesList.size() > queries.values().length) {
					System.out.println("WARNUNG: Mehr Aafragen gefunden als benötigt.");
				}
			} catch (IOException e) {
				throw new SQLException("Kann Property Datei nicht laden: " + queriesFile + "\n" + e.getMessage());
			}
		}else{
			throw new SQLException("Kann Property Datei nicht laden: " + queriesFile);
		}
	}
	
	public static QueryBuilder getInstance() throws SQLException {
		if(instance == null) {
			instance = new QueryBuilder();
		}
		return instance;
	}

	public PreparedStatement getQuery(Connection c, queries query, Object ... params) throws SQLException {
		{
			PreparedStatement cachedStatement = lookup(query, params);
			if(cachedStatement != null) {
				return cachedStatement;
			}
		}
		PreparedStatement result = c.prepareStatement(queriesList.getProperty(query.name()));
		if(result.getParameterMetaData().getParameterCount() != params.length) {
			throw new SQLException("Parameteranzahl passt nicht.");
		}
		
		for(int i = 0 ; i < params.length ; ++i) {
			if(params[i] == null) {
				throw new SQLException("Parameter " + i + "ist null.");
			}else if(params[i] instanceof Boolean) {
				result.setBoolean(i + 1, (Boolean) params[i]);
			}else if(params[i] instanceof Byte) {
				result.setByte(i + 1, (Byte) params[i]);
			}else if(params[i] instanceof Short) {
				result.setShort(i + 1, (Short) params[i]);
			}else if(params[i] instanceof Integer) {
				result.setInt(i + 1, (Integer) params[i]);
			}else if(params[i] instanceof Long) {
				result.setLong(i + 1, (Long) params[i]);
			}else if(params[i] instanceof Float) {
				result.setFloat(i + 1, (Float) params[i]);
			}else if(params[i] instanceof Double) {
				result.setDouble(i + 1, (Double) params[i]);
			}else if(params[i] instanceof BigDecimal) {
				result.setBigDecimal(i + 1, (BigDecimal) params[i]);
			}else if(params[i] instanceof String) {
				result.setString(i + 1, (String) params[i]);
			}else if(params[i] instanceof byte[]) {
				result.setBytes(i + 1, (byte[]) params[i]);
			}else if(params[i] instanceof java.sql.Date) {
				result.setDate(i + 1, (java.sql.Date) params[i]);
			}else if(params[i] instanceof java.sql.Time) {
				result.setTime(i + 1, (java.sql.Time) params[i]);
			}else if(params[i] instanceof java.sql.Timestamp) {
				result.setTimestamp(i + 1, (java.sql.Timestamp) params[i]);
			}else if(params[i] instanceof java.sql.Ref) {
				result.setRef (i + 1, (java.sql.Ref) params[i]);
			}else if(params[i] instanceof java.sql.Blob) {
				result.setBlob (i + 1, (java.sql.Blob) params[i]);
			}else if(params[i] instanceof java.sql.Clob) {
				result.setClob (i + 1, (java.sql.Clob) params[i]);
			}else if(params[i] instanceof java.sql.Array) {
				result.setArray (i + 1, (java.sql.Array) params[i]);
			}else if(params[i] instanceof java.net.URL) {
				result.setURL(i + 1, (java.net.URL) params[i]);
			}else if(params[i] instanceof java.sql.RowId) {
				result.setRowId(i + 1, (java.sql.RowId) params[i]);
			}else if(params[i] instanceof java.sql.NClob) {
				result.setNClob(i + 1, (java.sql.NClob) params[i]);
			}else if(params[i] instanceof SQLXML) {
				result.setSQLXML(i + 1, (java.sql.SQLXML) params[i]);
			}
		}
		if(cached(query)) {
			cache(query, params, result);
		}
		return result;
	}

	private boolean cached(queries query) {
		switch(query) {
		case assettypes:
		case domainfrommassettype:
		case categoryfromdomain:
		case measurefromcategory:
		case measuresfrommeasure:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Puffert Anfragen, die über cached(queries query) pufferbar gesetzt wurden
	 * @param query Anfrage
	 * @param params Parameter
	 * @param statement SQL Statement
	 */
	private void cache(queries query, Object[] params, PreparedStatement statement) {
		if(!cached(query)) {
			return;
		}
		if(cache == null) {
			cache = new HashMap<queries, HashMap<Object[], PreparedStatement>>();
		}
		if(!cache.containsKey(query)) {
			cache.put(query, new HashMap<Object[], PreparedStatement>());
		}
		cache.get(query).put(params, statement);
	}

	private PreparedStatement lookup(queries query, Object[] params) {
		if(cache == null) {
			return null;
		}
		if(cache.containsKey(query)) {
			return cache.get(query).get(params);
		}
		return null;
	}

}
