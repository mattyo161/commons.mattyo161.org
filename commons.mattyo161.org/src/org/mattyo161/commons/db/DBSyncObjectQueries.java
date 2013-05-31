package org.mattyo161.commons.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.mattyo161.commons.db.schema.SchemaTools;

/**
 * this DBSyncObject takes a series of sql query strings with appropriate "?" value place holders
 * and generate the needed PreparedStatments used by DBSync, this is the most flexible of all DBSyncObjects
 * as it allows the user to specify any combination of queries, so table schemas and in some cases even types
 * do not have to match in order for accurate synching to be achieved.
 * 
 * I also recommend using complete inserts, in the append sql.
 * @author mattyo1
 *
 */
public class DBSyncObjectQueries implements DBSyncObject {
	private String sqlSelect = "";
	private String sqlAppend = "";
	private String sqlUpdate = "";
	private String sqlDelete = "";
	private Connection conn = null;
	private List appendFields;
	private List updateFields;
	private List keyFields;
	private List<Integer> keySort;
	private String name = "";
	
	public DBSyncObjectQueries(String name, Connection conn) {
		this.name = name;
		this.conn = conn;
	}
	
	public String getSqlAppend() {
		return sqlAppend;
	}

	public String getSqlDelete() {
		return sqlDelete;
	}

	public String getSqlSelect() {
		return sqlSelect;
	}

	public String getSqlUpdate() {
		return sqlUpdate;
	}

	public List getAppendFields() {
		return appendFields;
	}

	public List getKeyFields() {
		return keyFields;
	}

	public List getUpdateFields() {
		return updateFields;
	}
	
	/**
	 * Will automatically generate a select sql statement based on the keyFields that have been defined. If the
	 * keyFields are empty or null it will throw an exception. It will also use the "name" given to the DBSyncObject
	 * as the table name for the query, it will request all fields using "select * from <table_name> .." syntax, it is recommended 
	 * to print out the generated sql and verify that it is correct before using it. In most cases the generatedSql 
	 * should be addequate for synching most queries.
	 * 
	 * TEMPLATE:
	 * select * from <table_name> where <keyField> = ? [and <keyField> = ? ...]
	 * @return
	 */
	public String generateSqlSelect() throws SQLException {
		if (getKeyFields() != null && getKeyFields().size() > 0 && getName() != null && !getName().equals("")) {
			String sqlSelect = "select * from " + getName() + " order by " + StringUtils.join(getKeyFields().iterator(), ", ");
			return sqlSelect;
		} else {
			throw new SQLException("Failed to generate SelectSql: KeyFields or Name property are empty or null.");
		}
	}

	/**
	 * Will automatically generate an insert sql statement based on the appendFields that have been defined. If the
	 * appendFields are empty or null it will throw an exception. It will also use the "name" given to the DBSyncObject
	 * as the table name for the query, it will use complete inserts, it is recommended to print out the generated sql
	 * and verify that it is correct before using it. In most cases the generatedSql should be addequate for synching most
	 * queries.
	 * 
	 * TEMPLATE:
	 * insert into <table_name> (<keyField>[, <keyfield>...]) VALUES (?[, ?, ?...])
	 * @return
	 */
	public String generateSqlAppend() throws SQLException {
		if (getAppendFields() != null && getAppendFields().size() > 0 && getName() != null && !getName().equals("")) {
			// Use complete inserts
			StringBuffer appendSql = new StringBuffer().append("insert into ").append(getName());
			appendSql.append(" (" + StringUtils.join(getAppendFields().iterator(), ", ") + ")");
			appendSql.append(" values (");
			for (int i = 0; i < getAppendFields().size(); i++) {
				if (i > 0) {
					appendSql.append(", ?");
				} else {
					appendSql.append("?");
				}
			}
			appendSql.append(")");
			return appendSql.toString();
		} else {
			throw new SQLException("Failed to generate AppendSql: AppendFields or Name property are empty or null.");
		}
	}

	/**
	 * Will automatically generate a delete sql statement based on the keyFields that have been defined. If the
	 * keyFields are empty or null it will throw an exception. It will also use the "name" given to the DBSyncObject
	 * as the table name for the query. It is recommended to print out the generated sql
	 * and verify that it is correct before using it. In most cases the generatedSql should be addequate for synching most
	 * queries.
	 * 
	 * TEMPLATE:
	 * delete from <table_name> where <keyField> = ? [and <keyField> = ? ...]
	 * @return
	 */
	public String generateSqlDelete() throws SQLException {
		if (getKeyFields() != null && getKeyFields().size() > 0 && getName() != null && !getName().equals("")) {
			// Create batch prepared statments for deleting rows, updating and inserting rows
			String deleteSql = "delete from " + getName() + " where " + StringUtils.join(getKeyFields().iterator(), " = ? and ") + " = ?";
			return deleteSql;
		} else {
			throw new SQLException("Failed to generate DeleteSql: KeyFields or Name property are empty or null.");
		}
	}

	/**
	 * Will automatically generate an update sql statement based on the keyFields & updateFields that have been defined. If the
	 * keyFields/updateFields are empty or null it will throw an exception. It will also use the "name" given to the DBSyncObject
	 * as the table name for the query. It is recommended to print out the generated sql
	 * and verify that it is correct before using it. In most cases the generatedSql should be addequate for synching most
	 * queries.
	 * 
	 * TEMPLATE:
	 * update <table_name> set (<keyField> = ?[, <keyField> = ?,...] where <keyField> = ? [and <keyField> = ? ...]
	 * @return
	 */
	public String generateSqlUpdate() throws SQLException {
		if (getUpdateFields() != null && getUpdateFields().size() > 0 && getKeyFields() != null && getKeyFields().size() > 0 && getName() != null && !getName().equals("")) {
			// also do complete updates, because I don't really now how else to do it then to use preparedStatements
			StringBuffer updateSql = new StringBuffer().append("update ").append(getName()).append(" set ");
			for (int i = 0; i < getUpdateFields().size(); i++) {
				if (i > 0) {
					updateSql.append(", ");
				}
				updateSql.append((String) getUpdateFields().get(i)).append(" = ?");
			}
			updateSql.append(" where ");
			for (int i = 0; i < getKeyFields().size(); i++) {
				if (i > 0) {
					updateSql.append(" and ");
				}
				updateSql.append((String) getKeyFields().get(i)).append(" = ?");
			}
			return updateSql.toString();
		} else if (getUpdateFields() != null && getUpdateFields().size() == 0 && getKeyFields() != null && getKeyFields().size() > 0 && getName() != null && !getName().equals("")) {
			System.out.println("WARNING: Update Fields are empty, could not generate UpdateSql");
			return "";
		} else {
			throw new SQLException("Failed to generate UpdateSql: UpdateFields, KeyFields or Name property are empty or null.");
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAppendFields(List appendFields) {
		this.appendFields = appendFields;
	}
	
	public void setKeyFields(Object[] keyFields) {
		this.keyFields = new Vector();
		CollectionUtils.addAll(this.keyFields, keyFields);
	}
	

	public void setAppendFields(Object[] appendFields) {
		this.appendFields = new Vector();
		CollectionUtils.addAll(this.appendFields, appendFields);
	}

	public void setUpdateFields(Object[] updateFields) {
		this.updateFields = new Vector();
		CollectionUtils.addAll(this.updateFields, updateFields);
	}

	public void setKeyFields(List keyFields) {
		this.keyFields = keyFields;
	}

	public void setUpdateFields(List updateFields) {
		this.updateFields = updateFields;
	}

	public void setKeySort(Integer[] keySort) {
		this.keySort = new Vector<Integer>();
		CollectionUtils.addAll(this.keySort, keySort);
	}
	
	public void setKeySort(List<Integer> keySort) {
		this.keySort = keySort;
	}
	
	public List<Integer> getKeySort() {
		if (this.keySort == null) {
			// we should create it and set them all to 1 the default
			this.keySort = new Vector<Integer>();
			for (int i = 0; i < getKeyFields().size(); i++) {
				this.keySort.add(1);
			}
		}
		return this.keySort;
	}


	
	/**
	 * Automatically generate all the update sql statements (Append, Update and Delete) based on appropriate generate sql methods.
	 *
	 */
	public void setSqlAuto() throws SQLException {
		this.setSqlAppend(generateSqlAppend());
		this.setSqlUpdate(generateSqlUpdate());
		this.setSqlDelete(generateSqlDelete());
		
	}
	
	public void setSqlAppend(String sqlAppend) {
		this.sqlAppend = sqlAppend;
	}

	public void setSqlDelete(String sqlDelete) {
		this.sqlDelete = sqlDelete;
	}

	public void setSqlSelect(String sqlSelect) {
		this.sqlSelect = sqlSelect;
	}

	public void setSqlUpdate(String sqlUpdate) {
		this.sqlUpdate = sqlUpdate;
	}

	/**
	 * Get a prepared statement setup with the defined sqlSelect property, if no sqlSelect property is defined
	 * then it will generate the preparted statement using generateSqlSelect().
	 */
	public PreparedStatement getSelection() throws SQLException {
		if (this.sqlSelect != null && !this.sqlSelect.equals("")) {
			// Make sure the resultset is readonly and forward only
			return conn.prepareStatement(this.sqlSelect, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		} else {
			// Make sure the resultset is readonly and forward only
			return conn.prepareStatement(generateSqlSelect(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		}
	}

	/**
	 * Get a prepared statement setup with the defined sqlUpdate property, if no sqlUpdate property is defined
	 * then it will generate the preparted statement using generateSqlUpdate().
	 */
	public PreparedStatement getUpdate() throws SQLException {
		if (this.sqlUpdate != null && !this.sqlUpdate.equals("")) {
			return conn.prepareStatement(this.sqlUpdate);
		} else {
			return conn.prepareStatement(generateSqlUpdate());
		}
	}

	/**
	 * Get a prepared statement setup with the defined sqlAppend property, if no sqlAppend property is defined
	 * then it will generate the preparted statement using generateSqlSelect().
	 */
	public PreparedStatement getAppend() throws SQLException {
		if (this.sqlAppend != null && !this.sqlAppend.equals("")) {
			return conn.prepareStatement(this.sqlAppend);
		} else {
			return conn.prepareStatement(generateSqlAppend());
		}
	}

	/**
	 * Get a prepared statement setup with the defined sqlDelete property, if no sqlDelete property is defined
	 * then it will generate the preparted statement using generateSqlSelect().
	 */
	public PreparedStatement getDelete() throws SQLException {
		if (this.sqlDelete != null && !this.sqlDelete.equals("")) {
			return conn.prepareStatement(this.sqlDelete);
		} else {
			return conn.prepareStatement(generateSqlDelete());
		}
	}
	

	
	/**
	 * Return a Create table string in the desired dbType based on SchemaTools.DBTYP_*
	 * @return
	 */
	public String getCreateTable(int dbType) {
		return SchemaTools.createTableFromSchema(this.name, this.appendFields, dbType);
	}

	public boolean getAutoCommit() {
		try {
			if (this.conn != null) {
				return this.conn.getAutoCommit();
			}
		} catch (SQLException e) {
			DBConnection.printSQLStackTrace(e);
		}
		return false;
	}

	public void commit() {
		try {
			if (this.conn != null) {
				if (!this.conn.getAutoCommit()) {
					this.conn.commit();
				}
			}
		} catch (SQLException e) {
			DBConnection.printSQLStackTrace(e);
		}
	}


}
