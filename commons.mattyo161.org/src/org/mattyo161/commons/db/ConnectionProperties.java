package org.mattyo161.commons.db;

/**
 * This class holds a collection of properties needed to establish a JDBC
 * Connection
 * 
 * @author mattyo1
 * 
 */
public class ConnectionProperties {
	private String serverType = "";
	private String server = "";
	private String port = "";
	private String user = "";
	private String password = "";
	private String db = "";
	private String options = "";
	private String jdbcDriver = "";
	private String jdbcUrl = "";

	public ConnectionProperties() {
		super();
	}

	/**
	 * @return Returns the db.
	 */
	public String getDb() {
		return db;
	}

	/**
	 * @param db The db to set.
	 */
	public void setDb(String db) {
		this.db = db;
	}

	/**
	 * @return Returns the jdbcDriver.
	 */
	public String getJdbcDriver() {
		return jdbcDriver;
	}

	/**
	 * @param jdbcDriver The jdbcDriver to set.
	 */
	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	/**
	 * @return Returns the jdbcUrl.
	 */
	public String getJdbcUrl() {
		return jdbcUrl;
	}

	/**
	 * @param jdbcUrl The jdbcUrl to set.
	 */
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	/**
	 * @return Returns the options.
	 */
	public String getOptions() {
		return options;
	}

	/**
	 * @param options The options to set.
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Returns the port.
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port The port to set.
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return Returns the server.
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param server The server to set.
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * @return Returns the serverType.
	 */
	public String getServerType() {
		return serverType;
	}

	/**
	 * @param serverType The serverType to set.
	 */
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	/**
	 * @return Returns the user.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user The user to set.
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	
}
