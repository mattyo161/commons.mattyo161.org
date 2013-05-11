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
	
	public DBSyncObjectTable(Connection conn, String tableName) throws SQLException {
		super(tableName, conn);
		init(conn, tableName, true);
	}
	public DBSyncObjectTable(Connection conn, String tableName, boolean requirePrimaryKey) throws SQLException {
		super(tableName, conn);
		init(conn, tableName, requirePrimaryKey);
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
			appendFields.add(currCol.getName().toLowerCase());
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
		setSqlSelect(generateSqlSelect());
		setSqlDelete(generateSqlDelete());
		setSqlAppend(generateSqlAppend());
		// if update fields is empty which occurs in the case of no Primary key being available then the generateSqlUpdate()
		// will through an exception so when that is the case we will add a field to updateFields, generate the sql and then
		// remove the field
		if (getUpdateFields().size() > 0) {
			setSqlUpdate(generateSqlUpdate());
		} else {
			getUpdateFields().add(getAppendFields().get(0));
			setSqlUpdate(generateSqlUpdate());
			getUpdateFields().remove(getAppendFields().get(0));
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

}
