package org.mattyo161.commons.db;

import java.sql.*;
import java.util.regex.Pattern;

/**
 * An implementation of the IConnection2Db Interface which is used to create a connection
 * to an Access Database File. This funciton should not be called individually
 * instead refer to ConnectionFactory for creating Connections using this method.
 * New database types can be implemented by creating new classes that begin with 
 * Connection2 and end with the serverType name. The classes must implement the
 * IConnection2Db interface.
 *
 * @see ConnectionFactory
 * @author Matt Ouellette
 * @version 1.00 March 11, 2004
 */


class Connection2rmi implements IConnection2Db 
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
    		return "org.objectweb.rmijdbc.Driver";
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
	    String dbURL = "jdbc:rmi://" + server;
        if (!port.equals("")) {
        		dbURL += ":" + port;
        }
        
        // Because java.rmi routes jdbc requests to another server the combination of db and options makes up the 
        // jdbc driver and url pair to tell the rmi server how to connect to the database that you want it to connect to
        // therefore we will combine them here, also we will remove any extra slashes that may exist in the front of this
        // url as they can cause problems
        
        String remoteRmiUrl = db;
        if (!options.equals("")) {
            remoteRmiUrl += ";" + options;
	    }
        
        // Check to see if the format matches the ConnectionFactory url format of "^\\w+:\\/\\/" if so then
        // generate get the url from the connection factory and replace it here.
        if (Pattern.compile("^\\w+:\\/\\/").matcher(remoteRmiUrl).find()) {
            remoteRmiUrl = ConnectionFactory.getJdbcUrlFromUrl(remoteRmiUrl);
        }
        
        // remove any extra "/"s at the beginning of the url
        remoteRmiUrl = remoteRmiUrl.replaceAll("^\\/+", "");

        
        dbURL += "/" + remoteRmiUrl;
        return dbURL;
	}
    
    /**
     *Will load the JDBC driver and create a Connection object for the specified
     *MySQL (server, port, user, password, db and options)
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
     *MySQL (server, port, user and password)
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
     *MySQL (server, port, user, password and db)
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