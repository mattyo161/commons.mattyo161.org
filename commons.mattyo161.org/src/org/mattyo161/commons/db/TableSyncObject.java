package org.mattyo161.commons.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.mattyo161.commons.db.schema.Column;
import org.mattyo161.commons.db.schema.IndexColumn;
import org.mattyo161.commons.db.schema.TableSchema;

/**
 * This class is used to store the properties needed to use the TableSync class
 * @author mattyo1
 *
 */
public class TableSyncObject {
	private Connection connection = null;
	private List keyFields = new Vector();
	private List syncFields = new Vector();
	private TableSchema tableSchema = null;
	private String whereClause = "";
	
	public TableSyncObject() {
		super();
	}
	
	/**
	 * Build a TableSyncObject using a Connection and a tableName this
	 * only works when the two table objects have the same schema
	 * @param conn
	 * @param tableName
	 */
	public TableSyncObject(Connection conn, String tableName) throws SQLException {
		this.connection = conn;
		// now we need to get the TableSchema
		DatabaseMetaData dbMeta = conn.getMetaData();
		this.tableSchema = new TableSchema(dbMeta, tableName);
		// now that we have the schema we need to get the key fields and the snycFields
		List cols = this.tableSchema.getColumns();
		for (Iterator i = cols.iterator(); i.hasNext(); ) {
			Column currCol = (Column) i.next();
			this.syncFields.add(currCol.getName().toLowerCase());
		}
		// now we define the keyFields from the the primary key
		cols = this.tableSchema.getPrimaryKey().getColumns();
		for (Iterator i = cols.iterator(); i.hasNext(); ) {
			IndexColumn currCol = (IndexColumn) i.next();
			this.keyFields.add(currCol.getColumnName().toLowerCase());
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public List getKeyFields() {
		return keyFields;
	}

	public List getSyncFields() {
		return syncFields;
	}

	public TableSchema getTableSchema() {
		return tableSchema;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setConnection(Connection conn) {
		this.connection = conn;
	}

	public void setKeyFields(List keyFields) {
		this.keyFields = keyFields;
	}

	public void setSyncFields(List syncFields) {
		this.syncFields = syncFields;
	}

	public void setTableSchema(TableSchema tableSchema) {
		this.tableSchema = tableSchema;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	
	
	
}
