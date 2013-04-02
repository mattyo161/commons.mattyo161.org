/*
 * Created on Nov 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
//import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;


/**
 * @author dcs
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConnectionManager {
	private PoolingDriver driver;
	private static ConnectionManager cm = null;

	/**
	 * @param doConfigure - If true will configure all databases
	 */
	private ConnectionManager(boolean doConfigure) {
		//
		// create the PoolingDriver itself...
		//
		try {
			Class.forName("org.apache.commons.dbcp.PoolingDriver");
			driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
			if (doConfigure) {
				configure();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Returns the ConnectionManager singleton. If not created, will create it configuring all databases.
	 * @return
	 */
	public static synchronized ConnectionManager getConnectionManager() {
		return ConnectionManager.getConnectionManager(true);
	}

	/**
	 * Returns the ConnectionManager singleton. If not created, will create it and not configure all databases.
	 * @return
	 */
	public static synchronized ConnectionManager getConnectionManager(boolean doConfigure) {
		if (cm == null) {
			cm = new ConnectionManager(doConfigure);
		}
		return cm;
	}

	public void configure() {
		Iterator i = ConnectionManagerConfig.config().getKeys();

		while (i.hasNext()) {
			String name = (String) i.next();
			String value = ConnectionManagerConfig.config().getString(name);
			parseConfigItem(name, value);
		}

	}

	/**
	 * Check the passed name to determine if it is in the configuration file for
	 * ConnectionManager
	 * @param name
	 * @return
	 */
	public static boolean isValidName(String name) {
		String value = ConnectionManagerConfig.config().getString(name);
		if (value != null && !value.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private void parseConfigItem(String key, String row) {
		String classname = "";
		String url = "";
		String user = "";
		String passwd = "";
		// check to see if this row is using the DBConnection method or the standard method
		if (row.matches("^\\w+://.+")) {
			ConnectionProperties connProps = org.mattyo161.commons.db.ConnectionFactory.getConnectionProps(row);
			classname = connProps.getJdbcDriver();
			url = connProps.getJdbcUrl();
			user = connProps.getUser();
			passwd = connProps.getPassword();
		} else {
			// each line will be in the form:
			// name: class!url!user!passwd
			String[] value = row.split("!");
			classname = value[0];
			url = value[1];
			user = value[2];
			passwd = value[3];
		}
		try {
			Class.forName(classname);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		System.err.println("name = " + key + ", " +
				"url = " + url);
		try {
			setupDriver(key,url,user,passwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * Reconfigure a specific driver based on a config value, so if the value changes
	 * then the driver can be reloaded with the new configuration if necessary or a
	 * dynamic load of a new driver can be acheived.
	 */
	public void reconfigure(String type, String value) {
		try {
			String[] poolnames = this.driver.getPoolNames();
			boolean poolExists = false;
			for (int i = 0; i < poolnames.length; i++) {
				if (type.equals(poolnames[i])) {
					poolExists = true;
				}
			}
			if (poolExists) {
				try {
					// remove the old pool and add a new pool
					System.err.println("Closing Pool: " + type);
					this.driver.closePool(type);
					System.err.println("Reconfiguring Database Pool: " + type);
					parseConfigItem(type, value);
				} catch (SQLException e) {
					System.err.println("Failed to close pool: " + type);
					e.printStackTrace();
				}
			} else {
				// Add the new pool
				System.err.println("Reconfiguring Database Pool: " + type);
				parseConfigItem(type, value);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reconfigure() {
		try {
			String[] poolnames = this.driver.getPoolNames();
			for (int i = 0; i < poolnames.length; i++) {
				System.err.println("Closing Pool: " + poolnames[i]);
				this.driver.closePool(poolnames[i]);

			}
			this.driver = null;
			this.driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("Reconfiguring Database Pools");
		this.configure();
	}
	public void setupDriver(String name, String connectURI,String user, String passwd) throws Exception {
		//
		// First, we'll need a ObjectPool that serves as the
		// actual pool of connections.
		//
		// We'll use a GenericObjectPool instance, although
		// any ObjectPool implementation will suffice.
		//
		ObjectPool connectionPool = new StackObjectPool(null);

		//
		// Next, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string passed in the command line
		// arguments.
		//
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI,user,passwd);

		//
		// Now we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);

		//
		// ...and register our pool with it.
		//
		driver.registerPool(name,connectionPool);

		//
		// Now we can just use the connect string "jdbc:apache:commons:dbcp:example"
		// to access our pool of Connections.
		//
	}
	public static Connection getConnection(String type, boolean doConfigure) {
		try {
			if (cm == null) {
				ConnectionManager.getConnectionManager(doConfigure);
			}
			return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+type);
		} catch (SQLException e) {
			// Sometimes this error can be caused by a broken connection from network
			// interruptions or idle timeouts.  In that case, we should be able to try again
			// and get a connection.

			// If there is a failure then lets attempt to check the config to see if there is an entry
			// for the type, if there is then load it and try and connect to the database again.
			String value = ConnectionManagerConfig.config().getString(type);
			if (value != null && !value.equals("")) {
				getConnectionManager(doConfigure).reconfigure(type, value);
			}
			try {
				return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+type);
			} catch (SQLException e2) {
				e.printStackTrace();
				// oh well.  that didn't work...
			}
			return null;
		}
	}

	public static Connection getConnection(String type) {
		return getConnection(type, false);
	}

	public static void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		ConnectionManager cm = ConnectionManager.getConnectionManager();
		String[] names = cm.driver.getPoolNames();
		Connection conn;
		for (int i = 0; i < names.length; i++) {
			System.err.println(names[i]);
			System.err.println("Creating connection.");
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:"+names[i]);
			conn.close();
		}
	}
}
