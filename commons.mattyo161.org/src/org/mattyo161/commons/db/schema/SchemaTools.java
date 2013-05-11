package org.mattyo161.commons.db.schema;

import java.sql.Connection;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

import org.mattyo161.commons.db.ConnectionManager;

public class SchemaTools {
	public static final int DBTYPE_MSSQL = 1;
	public static final int DBTYPE_SYBASE = 2;
	public static final int DBTYPE_MYSQL = 3;
	public static final int DBTYPE_POSTGRES = 4;
	
	public static void main(String[] args) {
		try {
			Connection conn = ConnectionManager.getConnection("subscriberinfo",false);
			TableSchema ts = new TableSchema(conn.getMetaData(), "sales_fact_temp_et2");
			String[] tables = new String[] {"temp_newsubs", "temp_password_entries","temp_passwords","temp_plussubs","temp_requestlog"};
			for (int i = 0; i < tables.length; i++) {
				System.out.println(createTableFromSchema(new TableSchema(conn.getMetaData(), tables[i]),DBTYPE_MSSQL));
				System.out.println("GO");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Build a create table sql statement based on the information provided in the TableSchema
	 * @param table - table from which to build a create table statement
	 * @param dbtype - one of the supported dbtypes
	 * @return
	 */
	public static String createTableFromSchema(TableSchema table, int dbtype) {
		return createTableFromSchema(table.getName(), table.getColumns(), dbtype);
	}

	public static String[] getQuoteString(int dbtype) {
		switch (dbtype) {
		case DBTYPE_MSSQL:
			return new String[] {"[","]"};
		case DBTYPE_MYSQL:
			return new String[] {"`","`"};
		}
		return new String[] {"",""};
	}
	
	/**
	 * Build a create table sql statement for the given tableName, list of Columns and database type
	 * @param tableName
	 * @param columns
	 * @param dbtype
	 * @return
	 */
	public static String createTableFromSchema(String tableName, List columns, int dbtype) {
		StringBuffer buff = new StringBuffer();
		String[] quoteString = getQuoteString(dbtype);
		switch (dbtype) {
		case DBTYPE_MSSQL:
		case DBTYPE_POSTGRES:
		case DBTYPE_SYBASE:
			buff.append("create table ").append(quoteString[0]).append(tableName).append(quoteString[1]).append(" (\n\t");
			for (Iterator i = columns.iterator(); i.hasNext(); ) {
				Column currCol = (Column) i.next();
				String currColStr = getColumnDef(currCol, dbtype);
				if (!currCol.equals("")) {
					if (currCol.getPosition() > 1) {
						buff.append(",\n\t");
					}
					buff.append(currColStr);
				}
			}
			buff.append("\n)");
			break;
		case DBTYPE_MYSQL:
			buff.append("create table ").append(quoteString[0]).append(tableName).append(quoteString[1]).append(" (\n\t");
			for (Iterator i = columns.iterator(); i.hasNext(); ) {
				Column currCol = (Column) i.next();
				String currColStr = getColumnDef(currCol, dbtype);
				if (!currCol.equals("")) {
					if (currCol.getPosition() > 1) {
						buff.append(",\n\t");
					}
					buff.append(currColStr);
				}
			}
			buff.append("\n)");
			break;
		}
		return buff.toString();
	}
	
	/**
	 * return the sql string for creating the specified column in the specified dbtype.
	 * @param col
	 * @param dbtype
	 * @return
	 */
	private static String getColumnDef(Column col, int dbtype) {
		StringBuffer buff = new StringBuffer();
		String[] quoteString = getQuoteString(dbtype);
		switch (dbtype) {
		case DBTYPE_MSSQL:
			buff.append(quoteString[0]).append(col.getName()).append(quoteString[1])
			.append("\t");
		switch (col.getSqlType()) {
		case Types.CHAR:
			buff.append("char(").append(col.getSize()).append(")");
			break;
		case Types.VARCHAR:
			buff.append("varchar(").append(col.getSize()).append(")");
			break;
		case Types.LONGVARCHAR:
			buff.append("text");
			break;
		case Types.BIGINT:
			buff.append("numeric(").append(col.getSize()).append(",").append(0).append(")");
			break;
		case Types.NUMERIC:
			buff.append("numeric(").append(col.getSize()).append(",").append(col.getDigits()).append(")");
			break;
		case Types.INTEGER:
			buff.append("int");
			break;
		case Types.SMALLINT:
			buff.append("smallint");
			break;
		case Types.TINYINT:
			buff.append("tinyint");
			break;
		case Types.FLOAT:
		case Types.DOUBLE:
			buff.append("float(").append(col.getRadix()).append(")");
			break;
		case Types.REAL:
			buff.append("real");
			break;
		case Types.DECIMAL:
			buff.append("decimal(").append(col.getDigits()).append(",").append(col.getRadix()).append(")");
			break;
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			buff.append("datetime");
			break;
		case Types.BINARY:
			buff.append("binary(").append(col.getSize()).append(")");
			break;
		case Types.VARBINARY:
			buff.append("varbinary(").append(col.getSize()).append(")");
			break;
		case Types.BIT:
			buff.append("bit");
			break;
		}
		if (col.isNullable()) {
			buff.append(" NULL");
		} else {
			buff.append(" NOT NULL");
		}
		break;
		case DBTYPE_SYBASE:
			buff.append(quoteString[0]).append(col.getName()).append(quoteString[1])
				.append("\t");
			switch (col.getSqlType()) {
			case Types.CHAR:
				buff.append("char(").append(col.getSize()).append(")");
				break;
			case Types.VARCHAR:
				buff.append("varchar(").append(col.getSize()).append(")");
				break;
			case Types.LONGVARCHAR:
				buff.append("text");
				break;
			case Types.BIGINT:
				buff.append("numeric(").append(col.getSize()).append(",").append(0).append(")");
				break;
			case Types.NUMERIC:
				buff.append("numeric(").append(col.getSize()).append(",").append(col.getDigits()).append(")");
				break;
			case Types.INTEGER:
				buff.append("int");
				break;
			case Types.SMALLINT:
				buff.append("smallint");
				break;
			case Types.TINYINT:
				buff.append("tinyint");
				break;
			case Types.FLOAT:
				buff.append("float(").append(col.getRadix()).append(")");
				break;
			case Types.REAL:
				buff.append("real");
				break;
			case Types.DOUBLE:
				buff.append("double ").append(col.getRadix());
				break;
			case Types.DECIMAL:
				buff.append("decimal(").append(col.getDigits()).append(",").append(col.getRadix()).append(")");
				break;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				buff.append("datetime");
				break;
			case Types.BINARY:
				buff.append("binary(").append(col.getSize()).append(")");
				break;
			case Types.VARBINARY:
				buff.append("varbinary(").append(col.getSize()).append(")");
				break;
			case Types.BIT:
				buff.append("bit");
				break;
			}
			if (col.isNullable()) {
				buff.append(" NULL");
			} else {
				buff.append(" NOT NULL");
			}
			break;
		case DBTYPE_MYSQL:
			buff.append(quoteString[0]).append(col.getName()).append(quoteString[1])
				.append("\t");
			switch (col.getSqlType()) {
			case Types.CHAR:
				buff.append("char(").append(col.getSize()).append(")");
				break;
			case Types.VARCHAR:
				buff.append("varchar(").append(col.getSize()).append(")");
				break;
			case Types.LONGVARCHAR:
				buff.append("text");
				break;
			case Types.BIGINT:
				buff.append("numeric(").append(col.getSize()).append(",").append(0).append(")");
				break;
			case Types.NUMERIC:
				buff.append("numeric(").append(col.getSize()).append(",").append(col.getDigits()).append(")");
				break;
			case Types.INTEGER:
				buff.append("int");
				break;
			case Types.SMALLINT:
				buff.append("smallint");
				break;
			case Types.TINYINT:
				buff.append("tinyint");
				break;
			case Types.FLOAT:
				buff.append("float(").append(col.getSize()).append(",").append(col.getDigits()).append(")");
				break;
			case Types.REAL:
			case Types.DOUBLE:
				buff.append("double(").append(col.getSize()).append(",").append(col.getDigits()).append(")");
				break;
			case Types.DECIMAL:
				buff.append("decimal(").append(col.getDigits()).append(",").append(col.getRadix()).append(")");
				break;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				buff.append("datetime");
				break;
			case Types.BINARY:
				buff.append("BLOB");
				break;
			case Types.VARBINARY:
				if (col.getSize() <= 255) {
					buff.append("TINYBLOB");
				} else {
					buff.append("BLOB");
				}
				break;
			case Types.BIT:
				buff.append("tinyint");
				break;
			}
			if (col.isNullable()) {
				buff.append(" NULL");
			} else {
				buff.append(" NOT NULL");
			}
			if (col.getDefaultValue() != null && !col.getDefaultValue().equals("")) {
				buff.append(" DEFAULT ").append(col.getDefaultValue());
			}
			break;
		case DBTYPE_POSTGRES:
			buff.append(quoteString[0]).append(col.getName()).append(quoteString[1])
				.append("\t");
			switch (col.getSqlType()) {
			case Types.CHAR:
				buff.append("char(").append(col.getSize()).append(")");
				break;
			case Types.VARCHAR:
				buff.append("varchar(").append(col.getSize()).append(")");
				break;
			case Types.LONGVARCHAR:
				buff.append("text");
				break;
			case Types.BIGINT:
				buff.append("bigint");
				break;
			case Types.NUMERIC:
				buff.append("numeric(").append(col.getSize()).append(",").append(col.getDigits()).append(")");
				break;
			case Types.INTEGER:
				buff.append("int");
				break;
			case Types.SMALLINT:
				buff.append("smallint");
				break;
			case Types.TINYINT:
				buff.append("smallint");
				break;
			case Types.FLOAT:
				buff.append("real");
				break;
			case Types.REAL:
			case Types.DOUBLE:
				buff.append("double precision");
				break;
			case Types.DECIMAL:
				buff.append("decimal(").append(col.getDigits()).append(",").append(col.getRadix()).append(")");
				break;
			case Types.DATE:
				buff.append("date");
				break;
			case Types.TIME:
				buff.append("time");
				break;
			case Types.TIMESTAMP:
				buff.append("timestamp");
				break;
			case Types.BOOLEAN:
				buff.append("boolean");
				break;
			case Types.BINARY:
				buff.append("bytea");
				break;
			case Types.VARBINARY:
				if (col.getSize() <= 255) {
					buff.append("bytea");
				} else {
					buff.append("bytea");
				}
				break;
			case Types.BIT:
				buff.append("boolean");
				break;
			}
			if (col.isNullable()) {
				buff.append(" NULL");
			} else {
				buff.append(" NOT NULL");
			}
			if (col.getDefaultValue() != null && !col.getDefaultValue().equals("")) {
				buff.append(" DEFAULT ").append(col.getDefaultValue());
			}
			break;
		
		}
		return buff.toString();
	}
}


/*
Example for Mssql:

CREATE TABLE [dbo].[sales_fact_temp_et2] ( 
    [sitecode_sort]         	varchar(50) NOT NULL,
    [order_seq]             	numeric(10,0) NOT NULL,
    [trans_date]            	smalldatetime NOT NULL,
    [disc_code]             	char(2) NOT NULL,
    [rundate]               	smalldatetime NOT NULL,
    [inches]                	numeric(10,2) NOT NULL,
    [revenuecat]            	char(1) NOT NULL,
    [assigned_salescode]    	varchar(5) NULL,
    [time_id]               	int NULL,
    [runtime_id]            	int NULL
)
GO


CREATE TABLE sales_fact_temp_et ( 
    sitecode  	varchar(5) NULL,
    pubcode   	varchar(5) NULL,
    account   	varchar(12) NULL DEFAULT 0,
    ordernum  	varchar(10) NULL,
    rundate   	timestamp NULL,
    inches    	decimal NULL DEFAULT 0.0000,
    revenuecat	char(1) NULL,
    revenue   	decimal NULL DEFAULT 0.0000,
    salescode 	varchar(5) NULL,
    salesteam 	varchar(10) NULL 
    )
GO

*/