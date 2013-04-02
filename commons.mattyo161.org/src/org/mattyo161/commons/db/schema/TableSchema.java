package org.mattyo161.commons.db.schema;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.mattyo161.commons.db.schema.Column;

public class TableSchema {
	private String catalog = "";
	private String schema = "";
	private String name = "";
	private String remarks = "";
	private String type = "";
	private List columns = new Vector();
	private Map columnLookup = new HashMap();
	private Indexes indexes = new Indexes();
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("{catalog = '").append(this.catalog).append("', ")
			.append("schema = '").append(this.schema).append("', ")
			.append("name = '").append(this.name).append("', ")
			.append("remarks = '").append(this.remarks).append("', ")
			.append("type = '").append(this.type).append("', ")
			.append("columns = ").append(this.columns).append(", ")
			.append("indexes = ").append(this.indexes).append("}");
		return buff.toString();
	}
	
	public TableSchema() {
		super();
	}
	
    public TableSchema(DatabaseMetaData md, String tableName) throws java.sql.SQLException {
        ResultSet rs = md.getTables(null, null, tableName, null);
        if (rs.next()) {
            setCatalog(rs.getString("TABLE_CAT"));
            setSchema(rs.getString("TABLE_SCHEM"));
            setName(rs.getString("TABLE_NAME"));
            setType(rs.getString("TABLE_TYPE"));
            setRemarks(rs.getString("REMARKS"));
            ResultSet col = md.getColumns(null, null, tableName, null);
            while (col.next()) {
                this.addColumn(new Column(col));
            }
            this.indexes = new Indexes(md, tableName);
        }
    }
	
    public TableSchema(DatabaseMetaData md, String dbName, String tableName) throws java.sql.SQLException {
        ResultSet rs = md.getTables(dbName, null, tableName, null);
        if (rs.next()) {
            setCatalog(rs.getString("TABLE_CAT"));
            setSchema(rs.getString("TABLE_SCHEM"));
            setName(rs.getString("TABLE_NAME"));
            setType(rs.getString("TABLE_TYPE"));
            setRemarks(rs.getString("REMARKS"));
            ResultSet col = md.getColumns(null, null, tableName, null);
            while (col.next()) {
                this.addColumn(new Column(col));
            }
            this.indexes = new Indexes(md, tableName);
        }
    }
	
	public TableSchema(String name) {
		this.name = name;
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

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		if (catalog == null) {
			this.catalog = "";
		} else {
			this.catalog = catalog;
		}
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		if (schema == null) {
			this.schema = "";
		} else {
			this.schema = schema;
		}
	}

	public List getColumns() {
		return columns;
	}

	public void setColumns(List columns) {
		this.columns = columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			this.name = "";
		} else {
			this.name = name;
		}
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		if (remarks == null) {
			this.remarks = "";
		} else {
			this.remarks = remarks;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Indexes getIndexes() {
		return indexes;
	}

	public void setIndexes(Indexes indexes) {
		this.indexes = indexes;
	}
	
	public Index getPrimaryKey() {
		if (indexes == null) {
			return null;
		} else {
			return indexes.getUniqueIndex();
		}
	}
}
