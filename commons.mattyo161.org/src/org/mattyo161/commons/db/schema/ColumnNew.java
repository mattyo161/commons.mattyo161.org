package org.mattyo161.commons.db.schema;

import java.sql.DatabaseMetaData;



/**
 * Column represents the data defined in DatabaseMetaData
 * Each column description has the following columns: 
 * TABLE_CAT String => table catalog (may be null)
 * TABLE_SCHEM String => table schema (may be null)
 * TABLE_NAME String => table name
 * COLUMN_NAME String => column name
 * DATA_TYPE int => SQL type from java.sql.Types
 * TYPE_NAME String => Data source dependent type name, for a UDT the type name is fully qualified
 * COLUMN_SIZE int => column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.
 * BUFFER_LENGTH is not used.
 * DECIMAL_DIGITS int => the number of fractional digits
 * NUM_PREC_RADIX int => Radix (typically either 10 or 2)
 * NULLABLE int => is NULL allowed.
 *     columnNoNulls - might not allow NULL values
 *     columnNullable - definitely allows NULL values
 *     columnNullableUnknown - nullability unknown
 * REMARKS String => comment describing column (may be null)
 * COLUMN_DEF String => default value (may be null)
 * SQL_DATA_TYPE int => unused
 * SQL_DATETIME_SUB int => unused
 * CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the column
 * ORDINAL_POSITION int	=> index of column in table (starting at 1)
 * IS_NULLABLE String => "NO" means column definitely does not allow NULL values; "YES" means the column might allow NULL values. An empty string means nobody knows.
 * SCOPE_CATLOG String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
 * SCOPE_SCHEMA String => schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
 * SCOPE_TABLE String => table name that this the scope of a reference attribure (null if the DATA_TYPE isn't REF)
 * SOURCE_DATA_TYPE short => source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
 * 
 * @author mattyo1
 *
 */
public class ColumnNew {
	private String name = "";
	private int type = 0;
	private String typeName = "";
	private int size = 0;
	private int decimalDigits = 0;
	private int numPrecRadix = 0;
	private int nullable = 0;
	private String remarks = "";
	private int pos = 0;
	
	public ColumnNew() {
		super();
	}
	
	public int getDecimalDigits() {
		return decimalDigits;
	}



	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getNullable() {
		return nullable;
	}



	public void setNullable(int nullable) {
		this.nullable = nullable;
	}



	public int getNumPrecRadix() {
		return numPrecRadix;
	}



	public void setNumPrecRadix(int numPrecRadix) {
		this.numPrecRadix = numPrecRadix;
	}



	public int getPos() {
		return pos;
	}



	public void setPos(int pos) {
		this.pos = pos;
	}



	public String getRemarks() {
		return remarks;
	}



	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}



	public int getSize() {
		return size;
	}



	public void setSize(int size) {
		this.size = size;
	}



	public int getType() {
		return type;
	}



	public void setType(int type) {
		this.type = type;
	}



	public String getTypeName() {
		return typeName;
	}



	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}



	public boolean isNullable() {
		return nullable == DatabaseMetaData.columnNullable;
	}
}
