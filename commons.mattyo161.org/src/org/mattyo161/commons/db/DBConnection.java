/*
 * DBConnection.java
 *
 * Created on March 18, 2004, 9:01 PM
 */

package org.mattyo161.commons.db;

import java.sql.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.Executor;

import org.mattyo161.commons.db.schema.Index;
import org.mattyo161.commons.db.schema.IndexColumn;
import org.mattyo161.commons.db.schema.Indexes;
import org.mattyo161.commons.db.schema.TableSchema;
import org.mattyo161.commons.util.DataMap;

/**
 * 
 * <b>Large ResultSets</b> Here are some notes on dealing with large ResultSets
 * using the different drivers.<br />
 * MySQL: is plagued with out of memory errors when dealing with large sets.
 * there are two version of the driver configuration "mysql2" uses the version
 * from mysql.com and "mysql" which uses a different version I believe, that was
 * folded into the mysql.com version. I have found the following solutions with
 * this driver.<br />
 * You must used ResultSet.TYPE_FORWARD_ONLY and ResultSet.CONCUR_READ_ONLY when
 * creating Statments that will be retrieving large sets of data<br />
 * <code>
 * Example:
 * Connection conn = new DBConnection("mysql2://<user>:<pass>@<server>/<db>");
 * Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
 * // Make sure you set the FetchSize otherwise all the rows are returned at once and it runs out of memory
 * st.setFetchSize(Integer.MIN_VALUE);
 * ResultSet rs = st.executeQuery("select * from <largetable>");
 * </code>
 * This means that you cannot use an updatable query, meaning update the rows as
 * you go, so you must do batch updates or do immediate updates with "update
 * <table> set .." sql statments, kind of a pain.<br />
 * Also you must navigate forward, you cannot go backward or jump around, mysql
 * definitely needs a better driver.<br />
 * 
 * MSSQL: works pretty well with large datasets<br/> using the following
 * options gives the following results <code>TYPE_FORWARD_ONLY</code> gives
 * you a fast starting point to work with the ResultSet, but closing it can take
 * a while depending on how many rows you have retrieved.
 * <code>TYPE_SCROLL_INSENSITIVE</code> gives you a slow starting point to
 * work with the ResultSet, but closing it can will be fast, plus you can move
 * around in the ResultSet.
 * 
 * Sybase: works great with large datasets<br/> using the following options
 * gives the following results <code>TYPE_FORWARD_ONLY</code> gives you a fast
 * starting point to work with the ResultSet, but closing it can take a while
 * depending on how many rows you have retrieved.
 * <code>TYPE_SCROLL_INSENSITIVE</code> gives you a fast starting point to
 * work with the ResultSet, but closing it can will be fast, plus you can move
 * around in the ResultSet. (This is the best method for sybase)
 * 
 * @author mattyo1
 */
public class DBConnection implements Connection {
	private Connection myConn = null;

	private DatabaseMetaData metaDb = null;

	private String[] quoting = new String[] { "", "" };

	private ConnectionProperties connProps = null;

	private String jdbcDriver = "";

	private String jdbcUrl = "";

	public ArrayList getDbs() {
		ArrayList dbNames = new ArrayList();
		try {
			DatabaseMetaData mDb = this.getMetaDb();
			if (mDb != null) {
				ResultSet rs = mDb.getCatalogs();
				while (rs.next()) {
					dbNames.add(rs.getString("TABLE_CAT"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbNames;
	}

	public ArrayList getTables(String dbName) {
		ArrayList tableNames = new ArrayList();
		try {
			DatabaseMetaData mDb = this.getMetaDb();
			if (mDb != null) {
				ResultSet rs = mDb.getTables(dbName, null, null,
						new String[] { "TABLE" });
				while (rs.next()) {
					tableNames.add(rs.getString("TABLE_NAME"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNames;
	}

	public ArrayList getTables() {
		ArrayList tableNames = new ArrayList();
		try {
			DatabaseMetaData mDb = this.getMetaDb();
			if (mDb != null) {
				ResultSet rs = mDb.getTables(null, null, null,
						new String[] { "TABLE" });
				while (rs.next()) {
					tableNames.add(rs.getString("TABLE_NAME"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNames;
	}


	
	public DataMap getFieldDataMap(String dbName, String tableName) {
		DataMap fieldDM = new DataMap();
		try {
			DatabaseMetaData mDb = this.getMetaDb();
			if (mDb != null) {
				ResultSet rs = mDb.getColumns(dbName, null, tableName, null);
				ResultSetMetaData rsMD = rs.getMetaData();
				int colCount = rsMD.getColumnCount();
				ArrayList fieldNames = new ArrayList();
				for (int i = 1; i <= colCount; i++) {
					fieldNames.add(rsMD.getColumnName(i));
				}
				int fieldNum = 1;
				while (rs.next()) {
					DataMap tmpDM = new DataMap();
					for (int i = 1; i <= colCount; i++) {
						tmpDM.set((String) fieldNames.get(i - 1), rs
								.getObject(i));
						if (rs.getObject(i) == null) {
							tmpDM.set((String) fieldNames.get(i - 1), "");
						}
					}
					System.out.println(fieldNum + " -> " + tmpDM);
					fieldDM.set(("" + fieldNum++), tmpDM);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fieldDM;
	}

	/**
	 * Return a Table schema object that contains all of the information
	 * for the specified tableName
	 * @param tableName
	 * @return
	 */	
	public TableSchema getTableSchema(String tableName) throws SQLException {
		return new TableSchema(getMetaData(), tableName);
	}
	
	/**
	 * Return a Table schema object that contains all of the information
	 * for the specified dbName and tableName
	 * @param tableName
	 * @return
	 */	
	public TableSchema getTableSchema(String dbName, String tableName) throws SQLException {
		return new TableSchema(getMetaData(), dbName, tableName);
	}
	
	/**
	 * Return an Indexes object that contains all of the indexes
	 * for the specified tableName
	 * @param tableName
	 * @return
	 */
	public Indexes getIndexes(String tableName) {
		return getIndexes(null, tableName);
	}
	
	/**
	 * Return an Indexes object that contains all of the indexes
	 * for the speified tableName in the speified database if dbName
	 * is set to null then it will lookup the table in the default database
	 * @param dbName
	 * @param tableName
	 * @return
	 */
	public Indexes getIndexes(String dbName, String tableName) {
		Indexes indexes = new Indexes();
		try {
			DatabaseMetaData mDb = this.getMetaDb();
			if (mDb != null) {
				ResultSet rs = mDb.getIndexInfo(dbName, null, tableName, false,
						true);
				ResultSetMetaData rsMD = rs.getMetaData();
				while (rs.next()) {
					// we want to ignore statistic indexes
					if (rs.getInt("type") != DatabaseMetaData.tableIndexStatistic) {
						// check to see if the index already exists
						String indexName = rs.getString("index_name");
						Index currIndex = indexes.getIndex(indexName);
						if (currIndex == null) {
							// create a new index and add it to indexes
							currIndex = new Index(indexName);
							currIndex.setUnique(rs.getInt("non_unique") == 0);
							currIndex.setType(rs.getInt("type"));
							indexes.addIndex(currIndex);
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
		return indexes;
	}

	public String getTableDef(String dbName, String tableName) {
		DataMap tableDM = getTableDataMap(dbName, tableName);
		StringBuffer tableDef = new StringBuffer();

		tableDef.append("DROP TABLE IF EXISTS `" + tableName + "`;\n");
		tableDef.append("CREATE TABLE `" + tableName + "` (\n");
		List fields = tableDM.getList("fields");
		int fieldNum = 0;
		String fieldRemarks = "";
		for (Iterator i = fields.iterator(); i.hasNext();) {
			DataMap currField = (DataMap) i.next();
			if (fieldNum > 0) {
				tableDef.append(",\t#" + fieldRemarks + "\n");
			}
			if (currField.getString("TYPE_NAME").equalsIgnoreCase("decimal")) {
				tableDef
						.append("\t`"
								+ currField.getString("COLUMN_NAME")
								+ "` "
								+ currField.getString("TYPE_NAME")
								+ "("
								+ currField.getObject("COLUMN_SIZE")
								+ ","
								+ currField.getObject("DECIMAL_DIGITS")
								+ ") "
								+ (currField.getString("IS_NULLABLE").equals(
										"NO") ? "NOT NULL" : "NULL"));
			} else {
				tableDef
						.append("\t`"
								+ currField.getString("COLUMN_NAME")
								+ "` "
								+ currField.getString("TYPE_NAME")
								+ "("
								+ currField.getObject("COLUMN_SIZE")
								+ ") "
								+ (currField.getString("IS_NULLABLE").equals(
										"NO") ? "NOT NULL" : "NULL"));
			}
			fieldRemarks = currField.getString("REMARKS");
			fieldNum++;
		}
		tableDef.append("\t#" + fieldRemarks + "\n);\n");
		// build indexes
		Set indexSet = tableDM.getDataMap("indexes").getKeys();
		for (Iterator i = indexSet.iterator(); i.hasNext();) {
			String indexName = (String) i.next();
			DataMap currIndex = tableDM.getDataMap("indexes").getDataMap(
					indexName);
			tableDef.append("CREATE ");
			if (!currIndex.getBoolean("NON_UNIQUE")) {
				tableDef.append("UNIQUE ");
			}
			tableDef.append("INDEX `" + indexName + "` ON `" + tableName
					+ "` (");
			List indexColumns = currIndex.getList("COLUMNS");
			int colNum = 0;
			for (Iterator j = indexColumns.iterator(); j.hasNext();) {
				DataMap currColumn = (DataMap) j.next();
				if (colNum > 0) {
					tableDef.append(", ");
				}
				tableDef
						.append("`" + currColumn.getString("COLUMN_NAME") + "`");
				if (currColumn.getString("ASC_OR_DESC").equals("D")) {
					tableDef.append(" DESC");
				}
				colNum++;
			}
			tableDef.append(");\n");
		}
		return tableDef.toString();
	}

	public DataMap getTableDataMap(String dbName, String tableName) {
		DataMap tableDM = new DataMap();
		tableDM.set("name", tableName);
		tableDM.set("fields", new ArrayList());
		tableDM.set("fieldNames", new DataMap());
		tableDM.set("indexes", new DataMap());
		try {
			DatabaseMetaData mDb = this.getMetaDb();
			if (mDb != null) {
				ResultSet rs = mDb.getColumns(dbName, null, tableName, null);
				int colIndex = 1;
				while (rs.next()) {
					DataMap tmpDM = new DataMap();
					tmpDM.set("COLUMN_NAME", rs.getString("COLUMN_NAME"));
					tmpDM.set("DATA_TYPE", rs.getObject("DATA_TYPE"));
					tmpDM.set("TYPE_NAME", rs.getString("TYPE_NAME"));
					tmpDM.set("COLUMN_SIZE", rs.getObject("COLUMN_SIZE"));
					tmpDM.set("DECIMAL_DIGITS", rs.getObject("DECIMAL_DIGITS"));
					tmpDM.set("IS_NULLABLE", rs.getString("IS_NULLABLE"));
					tmpDM.set("REMARKS", rs.getString("REMARKS"));
					tableDM.getList("fields").add(tmpDM);
					tableDM.getDataMap("fieldNames")
							.set(rs.getString("COLUMN_NAME").toLowerCase(),
									colIndex);
					colIndex++;
				}
				rs = mDb.getIndexInfo(dbName, null, tableName, false, true);
				while (rs.next()) {
					DataMap tmpDM = tableDM.getDataMap("indexes").getDataMap(
							rs.getString("INDEX_NAME"));
					if (tmpDM == null) {
						tmpDM = new DataMap();
						tableDM.getDataMap("indexes").set(
								rs.getString("INDEX_NAME"), tmpDM);
						tmpDM.set("COLUMNS", new ArrayList());
						tmpDM.set("NON_UNIQUE", new Boolean(rs
								.getBoolean("NON_UNIQUE")));
					}
					DataMap tmpColumnDM = new DataMap();
					tmpColumnDM.set("COLUMN_NAME", rs.getString("COLUMN_NAME"));
					tmpColumnDM.set("ASC_OR_DESC", rs.getString("ASC_OR_DESC"));
					tmpDM.getList("COLUMNS").add(tmpColumnDM);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableDM;
	}

	private DatabaseMetaData getMetaDb() {
		try {
			if (this.metaDb == null && this.myConn != null) {
				this.metaDb = this.myConn.getMetaData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.metaDb;
	}

	public DBConnection(Connection conn) {
		myConn = conn;
		// need to figure some stuff out here.

	}

	public DBConnection(String src) throws Exception {
		myConn = ConnectionFactory.getConnection(src);
		connProps = ConnectionFactory.getConnectionProps(src);
		quoting = ConnectionFactory.getQuoting(connProps.getServerType());
		jdbcDriver = connProps.getJdbcDriver();
		jdbcUrl = connProps.getJdbcUrl();
	}

	public DBConnection(String serverType, String server, String port,
			String user, String password, String db, String options)
			throws Exception {
		myConn = ConnectionFactory.getConnection(serverType, server, port, user, password, db, options);
		connProps = ConnectionFactory.getConnectionProps(serverType, server, port, user, password, db, options);
		quoting = ConnectionFactory.getQuoting(connProps.getServerType());
		jdbcDriver = connProps.getJdbcDriver();
		jdbcUrl = connProps.getJdbcUrl();
	}

	public DBConnection(String serverType, String server, String port,
			String user, String password, String db) throws Exception {
		myConn = ConnectionFactory.getConnection(serverType, server, port, user, password, db);
		connProps = ConnectionFactory.getConnectionProps(serverType, server, port, user, password, db);
		quoting = ConnectionFactory.getQuoting(connProps.getServerType());
		jdbcDriver = connProps.getJdbcDriver();
		jdbcUrl = connProps.getJdbcUrl();
	}

	public DBConnection(String serverType, String server, String port,
			String user, String password) throws Exception {
		myConn = ConnectionFactory.getConnection(serverType, server, port, user, password);
		connProps = ConnectionFactory.getConnectionProps(serverType, server, port, user, password);
		quoting = ConnectionFactory.getQuoting(connProps.getServerType());
		jdbcDriver = connProps.getJdbcDriver();
		jdbcUrl = connProps.getJdbcUrl();
	}

	public void clearWarnings() throws java.sql.SQLException {
		myConn.clearWarnings();
	}

	public void close() throws java.sql.SQLException {
		myConn.close();
	}

	public void commit() throws java.sql.SQLException {
		myConn.commit();
	}

	public java.sql.Statement createStatement() throws java.sql.SQLException {
		return myConn.createStatement();
	}

	public java.sql.Statement createStatement(int param, int param1)
			throws java.sql.SQLException {
		return myConn.createStatement(param, param1);
	}

	public java.sql.Statement createStatement(int param, int param1, int param2)
			throws java.sql.SQLException {
		return myConn.createStatement(param, param1, param2);
	}

	public boolean getAutoCommit() throws java.sql.SQLException {
		return myConn.getAutoCommit();
	}

	public String getCatalog() throws java.sql.SQLException {
		return myConn.getCatalog();
	}

	public int getHoldability() throws java.sql.SQLException {
		return myConn.getHoldability();
	}

	public java.sql.DatabaseMetaData getMetaData() throws java.sql.SQLException {
		return myConn.getMetaData();
	}

	public int getTransactionIsolation() throws java.sql.SQLException {
		return myConn.getTransactionIsolation();
	}

	public java.util.Map getTypeMap() throws java.sql.SQLException {
		return myConn.getTypeMap();
	}

	public java.sql.SQLWarning getWarnings() throws java.sql.SQLException {
		return myConn.getWarnings();
	}

	public boolean isClosed() throws java.sql.SQLException {
		return myConn.isClosed();
	}

	public boolean isReadOnly() throws java.sql.SQLException {
		return myConn.isReadOnly();
	}

	public String nativeSQL(String str) throws java.sql.SQLException {
		return myConn.nativeSQL(str);
	}

	public java.sql.CallableStatement prepareCall(String str)
			throws java.sql.SQLException {
		return myConn.prepareCall(str);
	}

	public java.sql.CallableStatement prepareCall(String str, int param,
			int param2) throws java.sql.SQLException {
		return myConn.prepareCall(str, param, param2);
	}

	public java.sql.CallableStatement prepareCall(String str, int param,
			int param2, int param3) throws java.sql.SQLException {
		return myConn.prepareCall(str, param, param2, param3);
	}

	public java.sql.PreparedStatement prepareStatement(String str)
			throws java.sql.SQLException {
		return myConn.prepareStatement(str);
	}

	public java.sql.PreparedStatement prepareStatement(String str, String[] str1)
			throws java.sql.SQLException {
		return myConn.prepareStatement(str, str1);
	}

	public java.sql.PreparedStatement prepareStatement(String str, int param)
			throws java.sql.SQLException {
		return myConn.prepareStatement(str, param);
	}

	public java.sql.PreparedStatement prepareStatement(String str, int[] values)
			throws java.sql.SQLException {
		return myConn.prepareStatement(str, values);
	}

	public java.sql.PreparedStatement prepareStatement(String str, int param,
			int param2) throws java.sql.SQLException {
		return myConn.prepareStatement(str, param, param2);
	}

	public java.sql.PreparedStatement prepareStatement(String str, int param,
			int param2, int param3) throws java.sql.SQLException {
		return myConn.prepareStatement(str, param, param2, param3);
	}

	public void releaseSavepoint(java.sql.Savepoint savepoint)
			throws java.sql.SQLException {
		myConn.commit();
	}

	public void rollback() throws java.sql.SQLException {
		myConn.rollback();
	}

	public void rollback(java.sql.Savepoint savepoint)
			throws java.sql.SQLException {
		myConn.rollback(savepoint);
	}

	public void setAutoCommit(boolean param) throws java.sql.SQLException {
		myConn.setAutoCommit(param);
	}

	public void setCatalog(String str) throws java.sql.SQLException {
		myConn.setCatalog(str);
	}

	public void setHoldability(int param) throws java.sql.SQLException {
		myConn.setHoldability(param);
	}

	public void setReadOnly(boolean param) throws java.sql.SQLException {
		myConn.setReadOnly(param);
	}

	public java.sql.Savepoint setSavepoint() throws java.sql.SQLException {
		return myConn.setSavepoint();
	}

	public java.sql.Savepoint setSavepoint(String str)
			throws java.sql.SQLException {
		return myConn.setSavepoint(str);
	}

	public void setTransactionIsolation(int param) throws java.sql.SQLException {
		myConn.setTransactionIsolation(param);
	}

	public static void printResultSet(ResultSet rs, int maxRows) {
		ResultSetMetaData rsmd = null;
		try {
			rsmd = rs.getMetaData();
			System.out.println("<table border=\"1\">");
			int currRow = 0;
			int numCols = rsmd.getColumnCount();
			System.out.print("<tr>");
			System.out.print("<th>Row#</th>");
			for (int currCol = 1; currCol <= numCols; currCol++) {
				System.out
						.print("<th>" + rsmd.getColumnName(currCol) + "</th>");
			}
			System.out.println("</tr>");
			String currValue = null;
			while (rs.next() && currRow++ < maxRows) {
				System.out.print("<tr>");
				// System.out.print("<td>" + rs.getRow() + "</td>");
				System.out.print("<td>" + currRow + "</td>");
				for (int currCol = 1; currCol <= numCols; currCol++) {
					currValue = rs.getString(currCol);
					if (currValue == null) {
						currValue = "<center>&lt;NULL&gt;</center>";
					} else {
						currValue = currValue.toString().trim();
					}
					if (rs.wasNull()) {
						currValue = "<center>&lt;NULL&gt;</center>";
					} else if (currValue.equals("")) {
						currValue = "&nbsp;";
					}
					System.out.print("<td>" + currValue + "</td>");
				}
				System.out.println("</tr>");
			}
			System.out.println("</table>");
		} catch (Exception e) {
			System.out.println(e.toString());
			System.out.println("ERROR: in printResultSet");
			e.printStackTrace();
		}
	}

	public static void printResultSet(ResultSet rs, int maxRows,
			java.io.PrintWriter out) {
		ResultSetMetaData rsmd = null;
		try {
			rsmd = rs.getMetaData();
			out.println("<table border=\"1\">");
			int currRow = 0;
			int numCols = rsmd.getColumnCount();
			out.print("<tr>");
			out.print("<th>Row#</th>");
			for (int currCol = 1; currCol <= numCols; currCol++) {
				out.print("<th>" + rsmd.getColumnName(currCol) + "</th>");
			}
			out.println("</tr>");
			String currValue = null;
			while (rs.next() && currRow++ < maxRows) {
				out.print("<tr>");
				out.print("<td>" + rs.getRow() + "</td>");
				for (int currCol = 1; currCol <= numCols; currCol++) {
					currValue = rs.getString(currCol);
					if (currValue == null) {
						currValue = "<center>&lt;NULL&gt;</center>";
					} else {
						currValue = currValue.toString().trim();
					}
					if (rs.wasNull()) {
						currValue = "<center>&lt;NULL&gt;</center>";
					} else if (currValue.equals("")) {
						currValue = "&nbsp;";
					}
					out.print("<td>" + currValue + "</td>");
				}
				out.println("</tr>");
			}
			out.println("</table>");
		} catch (Exception e) {
			out.println(e.toString());
			System.out.println("ERROR: in printResultSet");
			e.printStackTrace();
		}
	}

	/**
	 * @return Returns the jdbcDriver.
	 */
	public String getJdbcDriver() {
		return jdbcDriver;
	}

	/**
	 * @return Returns the jdbcUrl.
	 */
	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return myConn.createArrayOf(typeName, elements);
	}

	public Blob createBlob() throws SQLException {
		return myConn.createBlob();
	}

	public Clob createClob() throws SQLException {
		return myConn.createClob();
	}

	public NClob createNClob() throws SQLException {
		return myConn.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		return myConn.createSQLXML();
	}

	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return myConn.createStruct(typeName, attributes);
	}

	public Properties getClientInfo() throws SQLException {
		return myConn.getClientInfo();
	}

	public String getClientInfo(String name) throws SQLException {
		return myConn.getClientInfo(name);
	}

	public boolean isValid(int timeout) throws SQLException {
		return myConn.isValid(timeout);
	}

	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		myConn.setClientInfo(properties);
	}

	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		myConn.setClientInfo(name, value);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return myConn.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return myConn.isWrapperFor(iface);
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		myConn.setTypeMap(map);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		myConn.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return myConn.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		myConn.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		myConn.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return 0;
	}
}
