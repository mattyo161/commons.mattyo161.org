/**
 *
 */
package org.mattyo161.commons.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;


/**
 * @author ssmyth
 *
 */
public class JDBCTemplate {
	private boolean logSQLCommands = false;
	private static JDBCTemplate singleton = null;
	
	private JDBCTemplate() {
		super();
	}
	
	private static JDBCTemplate getInstance() {
		if (singleton == null) {
			singleton = new JDBCTemplate();
		}
		return singleton;
	}
	
	/**
	 * This will turn on SQL logging so all SQL commands will be logged to standard out, this 
	 * can be used for debugging purposes
	 */
	public static void logSQL() {
		getInstance().logSQLCommands = true;
	}

	public static void query(String sql, String dbName, Object object, String methodName) throws JdbcSqlException {
		query(sql, dbName, object, methodName, false);
	}

	public static void query(String sql, String dbName, Object object, String methodName, boolean updateable) throws JdbcSqlException {
		if (getInstance().logSQLCommands) {
			System.out.println("JDBCTemplate:QUERY:DB:" + dbName);
			System.out.println("JDBCTemplate:QUERY:SQL:" + sql);
			System.out.println("JDBCTemplate:QUERY:Method:" + object.getClass().toString() + "." + methodName);
		}
		Class[] parameterTypes = {ResultSet.class};
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Method method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
			method.setAccessible(true);
			if (ConnectionManager.isValidName(dbName)) {
				connection = ConnectionManager.getConnection(dbName);
			} else {
				try {
					// Try and get the connection from DBConnection
					connection = new DBConnection(dbName);
				} catch (Exception e) {
					throw new SQLException();
				}
			}
			if (connection == null) {
				throw new SQLException();
			}
			if (updateable) {
				stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			} else {
				stmt = connection.createStatement();
			}
			rs = stmt.executeQuery(sql);
			Object[] params = {rs};
			while (rs.next()) {
				method.invoke(object, params);
			}
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
			throw new JdbcSqlException("No Such Method " + methodName + " " + nsme.getMessage());
		} catch (InvocationTargetException nsme) {
			nsme.printStackTrace();
			throw new JdbcSqlException("No Such Method " + methodName + " " + nsme.getMessage());
		} catch (IllegalAccessException nsme) {
			throw new JdbcSqlException("Illegal Access on " + methodName);
		} catch (SQLException sqle) {
			throw new JdbcSqlException("SQL Exception on \n\tSQL Statement: " + sql + "\n\tDBName: " + dbName + "\n\tSQL Error: " + sqle.getMessage());
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) {}
			try { if (stmt != null) stmt.close(); } catch (Exception e) {}
			try { if (connection != null) connection.close(); } catch (Exception e) {}
		}
	}

	public static void query(String sql, String dbName, Object object, String methodName, Object [] params) throws JdbcSqlException {
		if (getInstance().logSQLCommands) {
			System.out.println("JDBCTemplate:QUERY:DB:" + dbName);
			System.out.println("JDBCTemplate:QUERY:SQL:" + sql);
			System.out.println("JDBCTemplate:QUERY:Method:" + object.getClass().toString() + "." + methodName);
			System.out.println("JDBCTemplate:QUERY:Params: [" + StringUtils.join(params, ", ") + "]");
		}
		Class[] parameterTypes = {ResultSet.class};
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			Method method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
			method.setAccessible(true);
			if (ConnectionManager.isValidName(dbName)) {
				connection = ConnectionManager.getConnection(dbName);
			} else {
				try {
					// Try and get the connection from DBConnection
					connection = new DBConnection(dbName);
				} catch (Exception e) {
					throw new SQLException();
				}
			}
			if (connection == null) {
				throw new SQLException();
			}
			stmt = connection.prepareStatement(sql);
			for (int i = 0;i < params.length;i++) {
				Object param = params[i];
				stmt.setObject(i + 1, param);
			}
			rs = stmt.executeQuery();
			while (rs.next()) {
				method.invoke(object, new Object[] {rs});
			}
		} catch (NoSuchMethodException nsme) {
			throw new JdbcSqlException("No Such Method " + methodName + "\n\tSQL Statement: " + sql + "\n\tDBName: " + dbName + "\n\tError: " + nsme.toString());
		} catch (InvocationTargetException nsme) {
			throw new JdbcSqlException("Invocation Target Exception on Method " + methodName + "\n\tSQL Statement: " + sql + "\n\tDBName: " + dbName + "\n\tError: " + nsme.toString());
		} catch (IllegalAccessException nsme) {
			throw new JdbcSqlException("Illegal Access on Method" + methodName + "\n\tSQL Statement: " + sql + "\n\tDBName: " + dbName + "\n\tError: " + nsme.toString());
		} catch (SQLException sqle) {
			throw new JdbcSqlException("SQL Exception on \n\tSQL Statement: " + sql + "\n\tDBName: " + dbName + "\n\tSQL Error: " + sqle.getMessage());
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) {}
			try { if (stmt != null) stmt.close(); } catch (Exception e) {}
			try { if (connection != null) connection.close(); } catch (Exception e) {}
		}
	}

	public static int queryUpdate(String sql, String dbName, Object [] params) throws JdbcSqlException {
		if (getInstance().logSQLCommands) {
			System.out.println("JDBCTemplate:UPDATE:DB: " + dbName);
			System.out.println("JDBCTemplate:UPDATE:SQL: " + sql);
			System.out.println("JDBCTemplate:UPDATE:PARAMS: [" + StringUtils.join(params, ", ") + "]");
		}
		Connection connection = null;
		PreparedStatement stmt = null;
		int generatedKey = -1;
		try {
			if (ConnectionManager.isValidName(dbName)) {
				connection = ConnectionManager.getConnection(dbName);
			} else {
				try {
					// Try and get the connection from DBConnection
					connection = new DBConnection(dbName);
				} catch (Exception e) {
					throw new SQLException();
				}
			}
			if (connection == null) {
				throw new SQLException();
			}
			stmt = connection.prepareStatement(sql);
			for (int i = 0;i < params.length;i++) {
				Object param = params[i];
				stmt.setObject(i + 1, param);
			}
			connection.setAutoCommit(true);
			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				ResultSet keys = stmt.getGeneratedKeys();
				if (keys != null && keys.next()) {
					generatedKey = keys.getInt(1);
					keys.close();
				}

			}
		} catch (SQLException sqle) {
			throw new JdbcSqlException("SQL Exception on \n\tSQL Statement: " + sql + "\n\tDBName: " + dbName + "\n\tSQL Error: " + sqle.getMessage());
		} finally {
			try { if (stmt != null) stmt.close(); } catch (Exception e) {}
			try { if (connection != null) connection.close(); } catch (Exception e) {}
		}
		return generatedKey;
	}

	public static void query(String sql, String dbName, IResultSetProcessor resultSetProcessor) throws JdbcSqlException {
		if (getInstance().logSQLCommands) {
			System.out.println("JDBCTemplate:QUERY:DB:" + dbName);
			System.out.println("JDBCTemplate:QUERY:SQL:" + sql);
			System.out.println("JDBCTemplate:QUERY:Method: Using result set processor");
		}
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if (ConnectionManager.isValidName(dbName)) {
				connection = ConnectionManager.getConnection(dbName);
			} else {
				try {
					// Try and get the connection from DBConnection
					connection = new DBConnection(dbName);
				} catch (Exception e) {
					throw new SQLException();
				}
			}
			if (connection == null) {
				throw new SQLException();
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				resultSetProcessor.processRow(rs);
			}
		} catch (SQLException sqle) {
			throw new JdbcSqlException("SQL Exception on \n\tSQL Statement: " + sql + "\n\tDBName: " + dbName + "\n\tSQL Error: " + sqle.getMessage());
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) {}
			try { if (stmt != null) stmt.close(); } catch (Exception e) {}
			try { if (connection != null) connection.close(); } catch (Exception e) {}
		}
	}

	public static void query(String sql, String dbName, IResultSetProcessor resultSetProcessor, Object[] params) throws JdbcSqlException {
		if (getInstance().logSQLCommands) {
			System.out.println("JDBCTemplate:QUERY:DB:" + dbName);
			System.out.println("JDBCTemplate:QUERY:SQL:" + sql);
			System.out.println("JDBCTemplate:QUERY:Method: Using result set processor");
			System.out.println("JDBCTemplate:QUERY:Params: [" + StringUtils.join(params, ", ") + "]");
		}
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if (ConnectionManager.isValidName(dbName)) {
				connection = ConnectionManager.getConnection(dbName);
			} else {
				try {
					// Try and get the connection from DBConnection
					connection = new DBConnection(dbName);
				} catch (Exception e) {
					throw new SQLException();
				}
			}
			if (connection == null) {
				throw new SQLException();
			}
			stmt = connection.prepareStatement(sql);
			for (int i = 0;i < params.length;i++) {
				Object param = params[i];
				stmt.setObject(i + 1, param);
			}
			rs = stmt.executeQuery();
			while (rs.next()) {
				resultSetProcessor.processRow(rs);
			}
		} catch (SQLException sqle) {
			throw new JdbcSqlException("SQL Exception on \n\tSQL Statement: " + sql + "\n\tDBName: " + dbName + "\n\tSQL Error: " + sqle.getMessage());
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) {}
			try { if (stmt != null) stmt.close(); } catch (Exception e) {}
			try { if (connection != null) connection.close(); } catch (Exception e) {}
		}
	}

	public static void queryUpdate(String sql, String dbName) throws JdbcSqlException {
		if (getInstance().logSQLCommands) {
			System.out.println("JDBCTemplate:UPDATE:DB: " + dbName);
			System.out.println("JDBCTemplate:UPDATE:SQL: " + sql);
		}
		Connection connection = null;
		Statement stmt = null;
		try {
			if (ConnectionManager.isValidName(dbName)) {
				connection = ConnectionManager.getConnection(dbName);
			} else {
				try {
					// Try and get the connection from DBConnection
					connection = new DBConnection(dbName);
				} catch (Exception e) {
					throw new SQLException();
				}
			}
			if (connection == null) {
				throw new SQLException();
			}
			stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException sqle) {
			throw new JdbcSqlException("SQL Exception on \n\tSQL Statement: " + sql + "\n\tDBName: " + dbName + "\n\tSQL Error: " + sqle.getMessage());
		} finally {
			try { if (stmt != null) stmt.close(); } catch (Exception e) {}
			try { if (connection != null) connection.close(); } catch (Exception e) {}
		}
	}


}
