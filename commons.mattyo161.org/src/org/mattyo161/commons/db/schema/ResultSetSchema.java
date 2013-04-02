package org.mattyo161.commons.db.schema;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.mattyo161.commons.db.schema.Column;

/**
 * ResultSetSchema is a utility class to make dealing with ResultSetMetaData a little
 * easier, it will allow column lookups by name and position. It will build Column objects
 * for every column in the result set, similar to the TableSchema objects.
 * @author mattyo1
 *
 */
public class ResultSetSchema {
	private List columns = new Vector();
	private Map columnLookup = new HashMap();
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("{columns = ").append(this.columns).append("}");
		return buff.toString();
	}
	
	public ResultSetSchema() {
		super();
	}
	
    public ResultSetSchema(ResultSetMetaData md) throws java.sql.SQLException {
    		// we need to loop through all of the columns by there position and get all the
    		// information we can about them
    		int colCount = md.getColumnCount();
    		for (int i = 1; i <= colCount; i++) {
    			this.addColumn(new Column(md, i));
    		}
    }

	public void addColumn(Column col) {
		columns.add(col);
		columnLookup.put(col.getName().toLowerCase(), col);
	}
	
	/**
	 * Fetch the column in the schema, it will accept an Integer object for position, otherwise it will
	 * assume the column is a string.
	 * @param columnObject
	 * @return
	 */
	public Column getColumn(Object columnObject) {
		if (Integer.class.isInstance(columnObject)) {
			return (Column) columns.get(((Integer) columnObject).intValue());
		} else {
			return (Column) columnLookup.get(columnObject.toString().toLowerCase());
		}
	}
	
	public Column getColumn(String columnName) {
		return (Column) columnLookup.get(columnName.toLowerCase());
	}
	
	public Column getColumn(int columnPos) {
		return (Column) columns.get(columnPos);
	}
	
	public List getColumns() {
		return columns;
	}

	public void setColumns(List columns) {
		this.columns = columns;
	}
}
