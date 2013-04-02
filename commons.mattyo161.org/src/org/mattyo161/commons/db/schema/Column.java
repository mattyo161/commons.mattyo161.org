/*
 * Column.java
 *
 * Created on April 29, 2004, 9:39 PM
 */

package org.mattyo161.commons.db.schema;

import java.sql.*;

/**
 * 
 * @author mattyo1
 */
public class Column {

	private String catalog;

	private String schema;

	private String table;

	private String name;

	private int sqlType;

	private String dbType;

	private int size;

	private int digits;

	private int radix;

	private int nullable;

	private String remarks;

	private String defaultValue;

	private int octetLength;

	private int position;

	private String[] quoting = new String[] { "", "" };

	/** Creates a new instance of Column */
	public Column() {
	}

	/** Creates a new instance of Column from a ResultSet ro */
	public Column(ResultSet rs) throws java.sql.SQLException {
		setCatalog(rs.getString("TABLE_CAT"));
		setSchema(rs.getString("TABLE_SCHEM"));
		setTable(rs.getString("TABLE_NAME"));
		setName(rs.getString("COLUMN_NAME"));
		setSqlType(rs.getInt("DATA_TYPE"));
		setDbType(rs.getString("TYPE_NAME"));
		setSize(rs.getInt("COLUMN_SIZE"));
		setDigits(rs.getInt("DECIMAL_DIGITS"));
		setRadix(rs.getInt("NUM_PREC_RADIX"));
		setNullable(rs.getInt("NULLABLE"));
		setRemarks(rs.getString("REMARKS"));
		setDefaultValue(rs.getString("COLUMN_DEF"));
		setOctetLength(rs.getInt("CHAR_OCTET_LENGTH"));
		setPosition(rs.getInt("ORDINAL_POSITION"));
	}

	/**
	 * Build a Column object based on Column information from the passed ResultSetMetaData at the given position
	 * @param metaData
	 * @param position
	 */
	public Column(ResultSetMetaData metaData, int position) throws java.sql.SQLException {
		// TODO Auto-generated constructor stub
		setCatalog(metaData.getCatalogName(position));
		setSchema(metaData.getSchemaName(position));
		setTable(metaData.getTableName(position));
		setName(metaData.getColumnName(position));
		setSqlType(metaData.getColumnType(position));
		setDbType(metaData.getColumnTypeName(position));
		setSize(metaData.getColumnDisplaySize(position));
		setDigits(metaData.getPrecision(position));
		setRadix(metaData.getScale(position));
		setNullable(metaData.isNullable(position));
		setRemarks(metaData.getColumnLabel(position));
		setDefaultValue(null);
		setOctetLength(metaData.getColumnDisplaySize(position));
		setPosition(position);
	}

	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("{").append("catalog = '").append(catalog).append("',").append("schema = '").append(schema).append("',").append("table = '").append(table)
				.append("\',").append("name = '").append(name).append("',").append("sqlType =").append(sqlType).append(",").append("dbType = '").append(dbType)
				.append("',").append("size = ").append(size).append(",").append("digits =").append(digits).append(",").append("radix ").append(radix)
				.append(",").append("nullable ").append(nullable).append(",").append("remarks = '").append(remarks).append("',").append("defalutValue = '")
				.append(defaultValue).append("',").append("octetLength = ").append(octetLength).append(",").append("position ").append(position).append("}");
		return temp.toString();
	}

	/**
	 * Getter for property position.
	 * 
	 * @return Value of property position.
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Setter for property position.
	 * 
	 * @param position
	 *            New value of property position.
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Getter for property octetLength.
	 * 
	 * @return Value of property octetLength.
	 */
	public int getOctetLength() {
		return octetLength;
	}

	/**
	 * Setter for property octetLength.
	 * 
	 * @param octetLength
	 *            New value of property octetLength.
	 */
	public void setOctetLength(int octetLength) {
		this.octetLength = octetLength;
	}

	/**
	 * Getter for property catalog.
	 * 
	 * @return Value of property catalog.
	 */
	public java.lang.String getCatalog() {
		return catalog;
	}

	/**
	 * Setter for property catalog.
	 * 
	 * @param catalog
	 *            New value of property catalog.
	 */
	public void setCatalog(java.lang.String catalog) {
		if (catalog == null) {
			this.catalog = "";
		} else {
			this.catalog = catalog;
		}
	}

	/**
	 * Getter for property schema.
	 * 
	 * @return Value of property schema.
	 */
	public java.lang.String getSchema() {
		return schema;
	}

	/**
	 * Setter for property schema.
	 * 
	 * @param schema
	 *            New value of property schema.
	 */
	public void setSchema(java.lang.String schema) {
		if (schema == null) {
			this.schema = "";
		} else {
			this.schema = schema;
		}
	}

	/**
	 * Getter for property table.
	 * 
	 * @return Value of property table.
	 */
	public java.lang.String getTable() {
		return table;
	}

	/**
	 * Setter for property table.
	 * 
	 * @param table
	 *            New value of property table.
	 */
	public void setTable(java.lang.String table) {
		if (table == null) {
			this.table = "";
		} else {
			this.table = table;
		}
	}

	/**
	 * set the quoting array for this column it should come from the Connection
	 * 
	 * @param array
	 *            of two strings representing the left and right values for
	 *            quoting fields
	 * @return Value of property name.
	 */
	public void setQuoting(java.lang.String[] quoting) {
		this.quoting = quoting;
	}

	/**
	 * the quoting array for this column, it is an array with the left and right
	 * strings for quoting a column for the given db
	 * 
	 * @return Value of property name.
	 */
	public java.lang.String[] getQuoting() {
		return quoting;
	}

	/**
	 * get the complete name for the column including the table name and is also
	 * quoted.
	 * 
	 * @return Value of property name.
	 */
	public java.lang.String getNameComplete() {
		return quoting[0] + table + quoting[1] + "." + quoting[0] + name + quoting[1];
	}

	/**
	 * Get the name of the column using its quoted value for the particular Db.
	 * 
	 * @return Quoted version of the Column Name.
	 */
	public java.lang.String getNameQuoted() {
		return quoting[0] + name + quoting[1];
	}

	/**
	 * Getter for property name.
	 * 
	 * @return Value of property name.
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Setter for property name.
	 * 
	 * @param name
	 *            New value of property name.
	 */
	public void setName(java.lang.String name) {
		if (name == null) {
			this.name = "";
		} else {
			this.name = name;
		}
	}

	/**
	 * Getter for property sqlType.
	 * 
	 * @return Value of property sqlType.
	 */
	public int getSqlType() {
		return sqlType;
	}

	/**
	 * Setter for property sqlType.
	 * 
	 * @param sqlType
	 *            New value of property sqlType.
	 */
	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	/**
	 * Getter for property dbType.
	 * 
	 * @return Value of property dbType.
	 */
	public java.lang.String getDbType() {
		return dbType;
	}

	/**
	 * Setter for property dbType.
	 * 
	 * @param dbType
	 *            New value of property dbType.
	 */
	public void setDbType(java.lang.String dbType) {
		if (dbType == null) {
			this.dbType = "";
		} else {
			this.dbType = dbType;
		}
	}

	/**
	 * Getter for property size.
	 * 
	 * @return Value of property size.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Setter for property size.
	 * 
	 * @param size
	 *            New value of property size.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Getter for property digits.
	 * 
	 * @return Value of property digits.
	 */
	public int getDigits() {
		return digits;
	}

	/**
	 * Setter for property digits.
	 * 
	 * @param digits
	 *            New value of property digits.
	 */
	public void setDigits(int digits) {
		this.digits = digits;
	}

	/**
	 * Getter for property radix.
	 * 
	 * @return Value of property radix.
	 */
	public int getRadix() {
		return radix;
	}

	/**
	 * Setter for property radix.
	 * 
	 * @param radix
	 *            New value of property radix.
	 */
	public void setRadix(int radix) {
		this.radix = radix;
	}

	/**
	 * Return if the Column allows nulls
	 * 
	 * @return true if column allows nulls.
	 */
	public boolean isNullable() {
		return nullable == java.sql.DatabaseMetaData.columnNullable;
	}

	/**
	 * Getter for property nullable.
	 * 
	 * @return Value of property nullable.
	 */
	public int getNullable() {
		return nullable;
	}

	/**
	 * Setter for property nullable.
	 * 
	 * @param nullable
	 *            New value of property nullable.
	 */
	public void setNullable(int nullable) {
		this.nullable = nullable;
	}

	/**
	 * Getter for property remarks.
	 * 
	 * @return Value of property remarks.
	 */
	public java.lang.String getRemarks() {
		return remarks;
	}

	/**
	 * Setter for property remarks.
	 * 
	 * @param remarks
	 *            New value of property remarks.
	 */
	public void setRemarks(java.lang.String remarks) {
		if (remarks == null) {
			this.remarks = "";
		} else {
			this.remarks = remarks;
		}
	}

	/**
	 * Getter for property columnDef.
	 * 
	 * @return Value of property columnDef.
	 */
	public java.lang.String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Setter for property columnDef.
	 * 
	 * @param columnDef
	 *            New value of property columnDef.
	 */
	public void setDefaultValue(java.lang.String defaultValue) {
		if (defaultValue == null) {
			this.defaultValue = "";
		} else {
			this.defaultValue = defaultValue;
		}
	}

}
