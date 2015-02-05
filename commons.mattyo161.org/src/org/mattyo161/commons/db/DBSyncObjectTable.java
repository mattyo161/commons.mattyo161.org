package org.mattyo161.commons.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import org.mattyo161.commons.db.schema.Column;
import org.mattyo161.commons.db.schema.Index;
import org.mattyo161.commons.db.schema.IndexColumn;
import org.mattyo161.commons.db.schema.SchemaTools;
import org.mattyo161.commons.db.schema.TableSchema;

public class DBSyncObjectTable extends DBSyncObjectQueries {
	private TableSchema tableSchema = null;
	private boolean requirePrimaryKey;
	private List<String> ignoreFields;
	
	public DBSyncObjectTable(Connection conn, String tableName) throws SQLException {
		super(tableName, conn);
		this.requirePrimaryKey = true;
		this.ignoreFields = new Vector();
		init();
	}
	public DBSyncObjectTable(Connection conn, String tableName, String description) throws SQLException {
		super(tableName, conn);
		this.requirePrimaryKey = true;
		this.ignoreFields = new Vector();
		init();
		setDescription(description);
	}
	public DBSyncObjectTable(Connection conn, String tableName, boolean requirePrimaryKey) throws SQLException {
		super(tableName, conn);
		this.requirePrimaryKey = requirePrimaryKey;
		this.ignoreFields = new Vector();
		init();
	}

	public void init() throws SQLException {
		init(this.getConnection(), this.getName(), this.requirePrimaryKey);
	}
	
	public void ignoreFields(String[] fieldNames) throws SQLException {
		for (int i = 0; i < fieldNames.length; i++) {
			this.ignoreFields.add(fieldNames[i].toLowerCase());
		}
		init();
	}
	
	public void init(Connection conn, String tableName, boolean requirePrimaryKey) throws SQLException {
		DatabaseMetaData dbMeta = conn.getMetaData();
		tableSchema = new TableSchema(dbMeta, tableName);
		// now that we have the schema we need to get the key fields and the snycFields
		List cols = tableSchema.getColumns();
		List appendFields = new Vector();
		List updateFields = new Vector();
		List keyFields = new Vector();
		
		for (Iterator i = cols.iterator(); i.hasNext(); ) {
			Column currCol = (Column) i.next();
			if (!this.ignoreFields.contains(currCol.getName().toLowerCase())) {
				appendFields.add(currCol.getName().toLowerCase());
			}
		}
		// now we define the keyFields from the the primary key
		Index pk = tableSchema.getPrimaryKey();
		if (pk == null) {
			// we could not get a primary key, thrown an exception
			if (requirePrimaryKey) {
				throw new SQLException("No Primary Key available for table '" + tableName + "'.");
			} else {
				// if the primary key is not required then set the key fields to all of the fields, this way
				// if there are any changes a delete/append will occur, but at least the changes will be made
				keyFields.addAll(appendFields);
			}
		} else {
			cols = tableSchema.getPrimaryKey().getColumns();
			for (Iterator i = cols.iterator(); i.hasNext(); ) {
				IndexColumn currCol = (IndexColumn) i.next();
				keyFields.add(currCol.getColumnName().toLowerCase());
			}
		}
		// update fields are the appendFields - keyFields
		updateFields.addAll(appendFields);
		updateFields.removeAll(keyFields);
		
		// now lets set the field lists
		setAppendFields(appendFields);
		setKeyFields(keyFields);
		setUpdateFields(updateFields);
		
		// now lets generate the sql
		try {
			setSqlSelect(generateSqlSelect());
		} catch (SQLException e) {
			if (requirePrimaryKey) {
				throw e;
			} else {
				System.err.println(e);
				e.printStackTrace();
			}
		}
		try {
			setSqlDelete(generateSqlDelete());
		} catch (SQLException e) {
			if (requirePrimaryKey) {
				throw e;
			} else {
				System.err.println(e);
				e.printStackTrace();
			}
		}
		try {
			setSqlAppend(generateSqlAppend());
		} catch (SQLException e) {
			if (requirePrimaryKey) {
				throw e;
			} else {
				System.err.println(e);
				e.printStackTrace();
			}
		}
		// if update fields is empty which occurs in the case of no Primary key being available then the generateSqlUpdate()
		// will through an exception so when that is the case we will add a field to updateFields, generate the sql and then
		// remove the field
		try {
			if (getUpdateFields().size() > 0) {
				setSqlUpdate(generateSqlUpdate());
			} else {
				getUpdateFields().add(getAppendFields().get(0));
				setSqlUpdate(generateSqlUpdate());
				getUpdateFields().remove(getAppendFields().get(0));
			}
		} catch (SQLException e) {
			if (requirePrimaryKey) {
				throw e;
			} else {
				System.err.println(e);
				e.printStackTrace();
			}
		}
	}
	
	public TableSchema getTableSchema() {
		return tableSchema;
	}
	public void setTableSchema(TableSchema tableSchema) {
		this.tableSchema = tableSchema;
	}

	
	/**
	 * Return a Create table string in the desired dbType based on SchemaTools.DBTYP_*
	 * @return
	 */
	public String getCreateTable(int dbType) {
		return SchemaTools.createTableFromSchema(this.tableSchema, dbType);
	}



	/**
	 * You will want to run this command if you update the AppendField or KeyFields, this will take
	 * all the AppendFields and remove the KeyFields and set that UpdateFields to that value. At the
	 * same time it will reset the UpdateQuery
	 */
	public void resetUpdateFields() {
		// update fields are the appendFields - keyFields
		List updateFields = new Vector();
		updateFields.addAll(this.getAppendFields());
		updateFields.removeAll(this.getKeyFields());
		setUpdateFields(updateFields);

		// if update fields is empty which occurs in the case of no Primary key being available then the generateSqlUpdate()
		// will through an exception so when that is the case we will add a field to updateFields, generate the sql and then
		// remove the field
		try {
			if (getUpdateFields().size() > 0) {
				setSqlUpdate(generateSqlUpdate());
				setSqlDelete(generateSqlDelete());
			} else {
				getUpdateFields().add(getAppendFields().get(0));
				setSqlUpdate(generateSqlUpdate());
				setSqlDelete(generateSqlDelete());
				getUpdateFields().remove(getAppendFields().get(0));
			}
		} catch (SQLException e) {
			DBConnection.printSQLStackTrace(e);
		}
	}
	
}
