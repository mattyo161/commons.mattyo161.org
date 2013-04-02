package org.mattyo161.commons.db.schema;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Vector;

/**
 * This is a class to maanage all of the indexes for a given table it has some
 * additional lookup methods to find things like the primary key, or unique
 * indexes or to find indexes by name
 * 
 * @author mattyo1
 * 
 */
public class Indexes {
	private Vector indexes = new Vector();

	public Indexes() {
		super();
	}

	/**
	 * Build an indexes object from the DatabaseMetaData and the tableName
	 * 
	 * @param metaData
	 * @param tableName
	 * @return
	 */
	public Indexes(DatabaseMetaData metaData, String tableName) {
		init(metaData, null, tableName);
	}

	/**
	 * Build an indexes object from the DatabaseMetaData, dbName and the
	 * tableName
	 * 
	 * @param metaData
	 * @param dbName
	 * @param tableName
	 * @return
	 */
	public Indexes(DatabaseMetaData metaData, String dbName, String tableName) {
		init(metaData, dbName, tableName);
	}

	private void init(DatabaseMetaData metaData, String dbName, String tableName) {
		// reset the current index list
		this.indexes = new Vector();
		try {
			if (metaData != null) {
				ResultSet rs = metaData.getIndexInfo(dbName, null, tableName, false, true);
				while (rs.next()) {
					// we want to ignore statistic indexes
					if (rs.getInt("type") != DatabaseMetaData.tableIndexStatistic) {
						// check to see if the index already exists
						String indexName = rs.getString("index_name");
						Index currIndex = this.getIndex(indexName);
						if (currIndex == null) {
							// create a new index and add it to indexes
							currIndex = new Index(indexName);
							currIndex.setUnique(!rs.getBoolean("non_unique"));
							currIndex.setType(rs.getInt("type"));
							this.addIndex(currIndex);
						}
						// Now we need to add the current column
						IndexColumn currColumn = new IndexColumn(rs.getString("column_name"), rs.getString("asc_or_desc"));
						currIndex.addColumn(currColumn, rs.getInt("ORDINAL_POSITION") - 1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return indexes.toString();
	}

	public void addIndex(Index index) {
		this.indexes.add(index);
	}

	/**
	 * Retrieve an index by the indexName
	 * 
	 * @param indexName
	 * @return
	 */
	public Index getIndex(String indexName) {
		for (int i = 0; i < this.indexes.size(); i++) {
			Index currIndex = (Index) this.indexes.get(i);
			if (indexName.equalsIgnoreCase(currIndex.getIndexName())) {
				return currIndex;
			}
		}
		return null;
	}

	/**
	 * Retrieve the first unique index in the list of indexes.
	 * 
	 * @param indexName
	 * @return
	 */
	public Index getUniqueIndex() {
		for (int i = 0; i < this.indexes.size(); i++) {
			Index currIndex = (Index) this.indexes.get(i);
			if (currIndex.isUnique()) {
				return currIndex;
			}
		}
		return null;
	}

	/**
	 * Return the list of indexes
	 * 
	 * @return
	 */
	public List getIndexes() {
		return (List) this.indexes;
	}
}
