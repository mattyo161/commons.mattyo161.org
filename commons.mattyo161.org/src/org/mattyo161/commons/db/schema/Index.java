package org.mattyo161.commons.db.schema;

import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Vector;

/**
 * This class represents a single index in a database table
 * TYPE short => index type:
 *   DatabaseMetaData.tableIndexStatistic - this identifies table statistics that are returned in conjuction with a table's index descriptions
 *   DatabaseMetaData.tableIndexClustered - this is a clustered index
 *   DatabaseMetaData.tableIndexHashed - this is a hashed index
 *   DatabaseMetaData.tableIndexOther - this is some other style of index
 * @author mattyo1
 *
 */
public class Index {
	private String indexName = "";
	private String description = "";
	private List<IndexColumn> columns = new Vector<IndexColumn>();
	private boolean unique = false;
	private boolean primaryKey = false;
	private int type = 0;
	
	
	public Index(String indexName) {
		this.indexName = indexName;
	}
	
	public Index(String indexName, boolean unique, int type) {
		this.indexName = indexName;
		this.unique = unique;
		this.type = type;
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("{indexName = '").append(this.indexName).append("', unique = ");
		if (this.unique) {
			buff.append("TRUE");
		} else {
			buff.append("FALSE");
		}
		buff.append(", type = '");
		if (this.type == DatabaseMetaData.tableIndexClustered) {
			buff.append("CLUSTERED");
		} else if (this.type == DatabaseMetaData.tableIndexHashed) {
			buff.append("HASHED");
		} else if (this.type == DatabaseMetaData.tableIndexOther) {
			buff.append("OTHER");
		} else if (this.type == DatabaseMetaData.tableIndexStatistic) {
			buff.append("STATISTIC");
		} else {
			buff.append("UNKNOWN");
		}
		buff.append("', columns = ").append(columns).append("}");
		
		return buff.toString();
	}
	
	/**
	 * Add a column to the index at the end of the column list, columns
	 * must be inserted in order
	 * @param column
	 */
	public void addColumn(IndexColumn column) {
		this.columns.add(column);
	}
	
	/**
	 * Add a column to the index at a specific position, columns can be
	 * inserted in any order
	 * @param column
	 * @param position
	 */
	public void addColumn(IndexColumn column, int position) {
		this.columns.add(position, column);
	}
	
	public List<IndexColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<IndexColumn> columns) {
		this.columns = columns;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public boolean isUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
