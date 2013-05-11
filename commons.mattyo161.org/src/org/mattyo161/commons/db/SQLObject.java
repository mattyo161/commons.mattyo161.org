package org.mattyo161.commons.db;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Date;

import org.mattyo161.commons.base.BaseType;

/**
 * @author dcs
 *
 */
public abstract class SQLObject extends BaseType {
	public transient String database = "";
	public transient String table = "";
	public transient String primaryKey = "";
	
	public SQLObject load(String criteria, Object[] params) {
		if (database == null || database.equals("") || table == null || table.equals("")) {
			throw new RuntimeException("database not configured for " + this.getClass().getName());
		}
		String query = "select * from " + table + " where ";
		query += parseCriteria(criteria);
		//query += " limit 1";
		final SQLObject target = this;
		IResultSetProcessor loader = new IResultSetProcessor() {
			public void processRow(ResultSet rs) throws SQLException {
				fillTarget(rs, target);
			}
		};
		if (params == null) JDBCTemplate.query(query,database,loader);
		else JDBCTemplate.query(query, database, loader, params);
		
		return this;
	}
	
	public SQLObject load(String criteria) {
		return load(criteria, null);
	}
	
	public List search(String criteria, int numResults) {
		return search(criteria, null, numResults);
	}
	
	public List search(int numResults) {
		return search(null, null, numResults);
	}
	
	public List search(String criteria, Object[] params, int numResults) {
		if (database == null || database.equals("") || table == null || table.equals("")) {
			throw new RuntimeException("database not configured for " + this.getClass().getName());
		}
		String query;
		if (criteria != null) {
			query = "select * from " + table + " where " + parseCriteria(criteria);
		} else {
			query = "select * from " + table;
		}
		if (numResults > 0) query += " limit " + numResults;
		
		final List searchResults = new Vector();
		final Class targetClass = this.getClass();
		IResultSetProcessor loader = new IResultSetProcessor() {
			public void processRow(ResultSet rs) throws SQLException {
				SQLObject target = null;
				try {
					target = (SQLObject) targetClass.newInstance();
					fillTarget(rs, target);
				} catch (Exception e) {	}
				
				searchResults.add(target);
			}
		};
		if (params == null) JDBCTemplate.query(query, database, loader);
		else JDBCTemplate.query(query, database, loader, params);
		
		return searchResults;
	}
	
	public static List fromQuery(String query, Class c) {
		// this method assumes that the query will make sense when applied
		// to the provided class.
		SQLObject obj = null;
		try {
			obj = (SQLObject) c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		Connection conn = ConnectionManager.getConnection(obj.getDatabaseName());
		List results = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			results = fromResultSet(rs, c);
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try { conn.close(); } catch (SQLException e) {};
		}
		return results;
	}
	
	public static List fromResultSet(ResultSet rs, Class c) {
		if (!c.getSuperclass().getName().equals(SQLObject.class.getName())) throw new RuntimeException("Class must extend SQLObject");
		List results = new ArrayList();

		try {
			while (rs.next()) {
				SQLObject obj = (SQLObject) c.newInstance();
				obj.fillTarget(rs,obj);
				results.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return results;
	}
	
	public void save() {
		this._save();
	}
	
	private void _save() {
		if (database == null || database.equals("") || table == null || table.equals("")) {
			throw new RuntimeException("database not configured for " + this.getClass().getName());
		}
		String query = null;
		List params = new ArrayList();
		boolean multiplePrimaryKeys = false;
		if (!primaryKey.equals("")) {
			List primaryKeyList = new Vector();
			multiplePrimaryKeys = true;
			String primaryKeys[] = primaryKey.split(",");
			Object primaryKeyValues[] = new Object[primaryKeys.length];
			for (int i = 0;i < primaryKeys.length;i++) {
				primaryKeyValues[i] = _getPrimaryKey(primaryKeys[i]);
				primaryKeyList.add(primaryKeys[i]);
			}
			if (primaryKeyValues[0] == null) {
				// this is a new object
				query = insertQuery(params, primaryKeyList);
			} else {
				// this is an update of an existing object
				query = updateQuery(params, primaryKeyList);
			}
			if (primaryKeyValues[0] != null) {
				query += " where ";
				for (int i = 0;i < primaryKeys.length;i++) {
					primaryKeyValues[i] = _getPrimaryKey(primaryKeys[i]);
					if (i != 0) {
						query += " and ";
					}
					query += primaryKeys[i] + " = ?";
					params.add(primaryKeyValues[i]);
				}
			}
//				System.err.println(query);
			Object[] paramArray = params.toArray();
			int autoKey = JDBCTemplate.queryUpdate(query,database,paramArray);
			if (primaryKeyValues[0] == null && autoKey > 0 && !multiplePrimaryKeys) this.put(primaryKey,new Integer(autoKey));
		}
	}
	
	private String updateQuery(List params, List primaryKeys) {
		String query = "update " + table + " set ";
		Set props = this.entrySet();
		Iterator i = props.iterator();
		
		// refactor this.  ugly looping to make the query.
		boolean firstOne = true;
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			String fieldName = (String) entry.getKey();
			String colName = _mapField(fieldName);
			//If we have multiple primary keys - a composite key - then these are not system generated keys
			//therefore we must update them in the database
			if (colName == null || (primaryKeys.contains(colName) && (primaryKeys.size() == 1))) continue;
			if (firstOne) {
				firstOne = false;
			} else {
				query += ", ";
			}
			query +=  colName + " = ?";
			Object value = this.get(fieldName);
			if (value.getClass().equals(java.util.Date.class)) {
				value = new java.sql.Timestamp(((java.util.Date)value).getTime());
			}
			params.add(value);
		}
		return query;
	}
	
	private String insertQuery(List params, List primaryKeys) {
		String query = "insert into " + table + " ( ";
		Set props = this.entrySet();
		Iterator i = props.iterator();
		
		// refactor this.  ugly looping to make the query.
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			String fieldName = (String) entry.getKey();
			String colName = _mapField(fieldName);
			//If we have multiple primary keys - a composite key - then these are not system generated keys
			//therefore we must update them in the database
			if (colName == null || (primaryKeys.contains(colName) && (primaryKeys.size() == 1))) continue;
			query +=  colName + ",";
			Object value = this.get(fieldName);
			if (value.getClass().equals(java.util.Date.class)) {
				value = new java.sql.Timestamp(((java.util.Date)value).getTime());
			}
			params.add(value);
		}
		query = query.substring(0, query.length() -1);
		query += " ) values ( ";
		for (int n = 0; n < params.size(); n++) {
			query += "?";
			if (n < params.size() - 1) query += ", ";
		}
		
		query += " ) ";
		return query;
	}
	
	private Object _getPrimaryKey(String primaryKey) {
		Object value = this.get(primaryKey);
		Class valueClass = value.getClass();
		if (valueClass.equals(Integer.class) && ((Integer) value).intValue() < 1) return null;
		else if (valueClass.equals(String.class) && ((String) value).equals("")) return null;
		return value;
	}
	
	private String parseCriteria(String criteria) {
		String parsed = criteria;
		Map colmapping = _getColMapping();
		// if the criteria contains @, we need to replace
		// something.  well, maybe.
		if (colmapping != null && parsed.indexOf("@") != -1) {
			Set entries = colmapping.entrySet();
			Iterator i = entries.iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				String colName = (String) entry.getKey();
				String fieldName = (String) entry.getValue();
				// replace @field with the column name
				parsed = parsed.replaceAll("@" + fieldName, colName);
			}
		} 		
		return parsed;
	}
	
	private void fillTarget(ResultSet rs, SQLObject target) throws SQLException {
		ResultSetMetaData rsMeta = rs.getMetaData();
		String colName;
		for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
			colName = rsMeta.getColumnName(i);
			String fieldName = _mapColumn(colName);
			if (!fieldName.equals("")) {
				//System.out.println("Putting fieldName" + fieldName);
				target.put(fieldName,_convertSpecial(rs.getObject(i)));
				//System.out.println("Puted fieldName" + fieldName);
			}
		
		}
	}
	
	private Map _getColMapping() {
		Map mapping = null;
		
		try {
			mapping = (Map) this.getClass().getDeclaredField("colnames2fieldnames").get(this);
		} catch (Exception e) {	}
		return mapping;
	}
		
	private String _mapColumn(String colName) {
		Map colMap = _getColMapping();
		String fieldName = null;
		if (colMap != null) fieldName = (String) colMap.get(colName);
		if (fieldName == null) fieldName = colName;
		// map the field to no property
		return fieldName;
	}
	
	private String _mapField(String fieldName) {
		Map colMap = _getColMapping();
		String colName = fieldName;
		if (colMap != null && colMap.containsValue(fieldName)) {
			Set keys = colMap.keySet();
			Iterator i = keys.iterator();
			while (i.hasNext()) {
				String key = (String) i.next();
				String value = (String) colMap.get(key);
				if (value.equals(fieldName)) return key;
			}
		}
		return colName;
	}
	private Object _convertSpecial(Object value) {
		// handles null values.  maybe.
		if (value == null) return value;
		Class valueClass = value.getClass();
		// if it's a timestamp, convert to java.util.Date
		if (valueClass.equals(Timestamp.class)) {
			value = new Date(((Timestamp)value).getTime());
			valueClass = Date.class;
		} else if (valueClass.equals(java.sql.Date.class)) {
			value = new Date(((java.sql.Date)value).getTime());
			valueClass = Date.class;
		} else if (valueClass.equals(BigDecimal.class)) {
			value = new Integer(((java.math.BigDecimal)value).intValue());
		}
		return value;
	}
	
	public String getDatabaseName() { return this.database; }
}
