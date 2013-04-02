package org.mattyo161.commons.db;

import java.sql.*;

/**
 * An implementation of the IConnection2Db Interface which is used to create a connection
 * to an Sybase Server. This funciton should not be called individually
 * instead refer to ConnectionFactory for creating Connections using this method.
 * New database types can be implemented by creating new classes that begin with 
 * Connection2 and end with the serverType name. The classes must implement the
 * IConnection2Db interface.
 *
 * @see ConnectionFactory
 * @author Matt Ouellette
 * @version 1.00 March 11, 2004
 */

class Connection2oraclethin implements IConnection2Db 
{
    /**
     *Return the left and right characters to surround quoted fields with
     *used by getQuotedName for Table and Field classes
     */
    public String[] getQuoting() {
        return new String[] {"", ""};
    }

    /**
     * Return the jdbc driver used to access the database
     * @return
     */
    public String getJdbcDriver() {
    		return "oracle.jdbc.driver.OracleDriver";
    }
    
    /**
     * Return the url used to access the database
     * @return
     */
	public String getJdbcUrl(String server,
	        String port,
	        String db,
	        String options) 
	{
	    String dbURL = "jdbc:oracle:thin:@" + server;
        if (!port.equals("")) {
            dbURL += ":" + port;
        }
        if (!db.equals("")) {
            dbURL += ":" + db;
        }
        return dbURL;
	}
   /**
     *Will load the JDBC driver and create a Connection object for the specified
     *Oracle (server, port, user, password, db and options)
     */

    public Connection getConnection(String server,
            String port,
            String user,
            String password,
            String db,
            String options)
            throws Exception
    {
        Connection conn = null;

        // Make sure to replace nulls with empty strings
        if (server == null) server = "";
        if (port == null) port = "";
        if (user == null) user = "";
        if (password == null) password = "";
        if (db == null) db = "";
        if (options == null) options = "";
 
        try {
            // Specify the JDBC driver and URL
            String dbDriver = getJdbcDriver();
            String dbURL = getJdbcUrl(server, port, db, options);
            
            // Try to create a new instance of the driver
            if (dbDriver != "") {
                try
                {
                   Class.forName(dbDriver).newInstance();
                } catch (Exception e) {
                    String errorString = "Unable to Load JDBC Driver:\n" +
                        "DriverClass: " + dbDriver + "\n" +
                        "Error: " + e.toString();
                    throw new Exception(errorString);
                }
            }

            // get a new connection object 
            if (dbDriver != "" && dbURL != "") {
                try {
                    conn = DriverManager.getConnection(dbURL, user, password);
                } catch (Exception e) {
                    String errorString = "Unable to Connect to JDBC Driver:\n" +
                        "DriverClass: " + dbDriver + "\n" +
                        "URL: " + dbURL + "\n" +
                        "Error: " + e.toString();
                    throw new Exception(errorString);
                }
            }
             
        } catch (Exception e) {
            // Unkown error has occured
            throw e;
        }
        
        return conn;
    }
    
    /**
     *Will load the JDBC driver and create a Connection object for the specified
     *Sybase (server, port, user and password)
     */

    public Connection getConnection(String server, 
            String port, 
            String user, 
            String password) 
            throws Exception
    {
        return getConnection(server, port, user, password, "", "");
    }
    
    /**
     *Will load the JDBC driver and create a Connection object for the specified
     *Sybase (server, port, user, password and db)
     */

    public Connection getConnection(String server, 
            String port, 
            String user, 
            String password, 
            String db) 
            throws Exception
    {
        return getConnection(server, port, user, password, db, "");
    }
    
}