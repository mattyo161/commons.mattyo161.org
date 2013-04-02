/*
 * Created on Dec 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.db;

import junit.framework.TestCase;

/**
 * @author mattyo1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConnectionFactoryTest extends TestCase {

    public void testGetJdbcDriverFromUrl() {
        String dbUrl = "mysql://reporting:reporting@servername/dbname";
        assertEquals("org.gjt.mm.mysql.Driver", ConnectionFactory.getJdbcDriverFromUrl(dbUrl));
        
        dbUrl = "mssql65://reporting:reporting@servername/dbname";
        assertEquals("net.sourceforge.jtds.jdbc.Driver", ConnectionFactory.getJdbcDriverFromUrl(dbUrl));
        
        dbUrl = "as400://reporting:reporting@servername/dbname";
        assertEquals("com.ibm.as400.access.AS400JDBCDriver", ConnectionFactory.getJdbcDriverFromUrl(dbUrl));
        
        dbUrl = "oraclethin://reporting:reporting@servername/dbname";
        assertEquals("oracle.jdbc.driver.OracleDriver", ConnectionFactory.getJdbcDriverFromUrl(dbUrl));
        
        dbUrl = "sybase://reporting:reporting@servername/dbname";
        assertEquals("com.sybase.jdbc2.jdbc.SybDriver", ConnectionFactory.getJdbcDriverFromUrl(dbUrl));
        
        dbUrl = "mssql2000://reporting:reporting@servername/dbname";
        assertEquals("net.sourceforge.jtds.jdbc.Driver", ConnectionFactory.getJdbcDriverFromUrl(dbUrl));
    }

    public void testGetJdbcUrlFromUrl() {
        String dbUrl = "mysql://reporting:reporting@servername/dbname";
        assertEquals("jdbc:mysql://servername/dbname", ConnectionFactory.getJdbcUrlFromUrl(dbUrl));
        
        dbUrl = "mssql65://reporting:reporting@servername/dbname";
        assertEquals("jdbc:jtds:sqlserver://servername:1433/dbname;TDS=4.2", ConnectionFactory.getJdbcUrlFromUrl(dbUrl));
        
        dbUrl = "as400://reporting:reporting@servername/dbname";
        assertEquals("jdbc:as400://servername/dbname", ConnectionFactory.getJdbcUrlFromUrl(dbUrl));
        
        dbUrl = "oraclethin://reporting:reporting@servername/dbname";
        assertEquals("jdbc:oracle:thin:@servername:1521:dbname", ConnectionFactory.getJdbcUrlFromUrl(dbUrl));
        
        dbUrl = "sybase://reporting:reporting@servername/dbname";
        assertEquals("jdbc:sybase:Tds:servername/dbname", ConnectionFactory.getJdbcUrlFromUrl(dbUrl));
        
        dbUrl = "mssql2000://reporting:reporting@servername/dbname";
        assertEquals("jdbc:jtds:sqlserver://severname:1433/dbname", ConnectionFactory.getJdbcUrlFromUrl(dbUrl));
    }

}
