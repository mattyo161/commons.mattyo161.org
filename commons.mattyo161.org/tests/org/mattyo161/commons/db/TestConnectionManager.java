/*
 * Created on Mar 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.db;

import java.sql.Connection;

import junit.framework.TestCase;

/**
 * @author ssmyth
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestConnectionManager extends TestCase {

	public void testPPIConnection() {

        ConnectionManager.getConnectionManager();
		Connection ppiConn = ConnectionManager.getConnection("aptproduction");
		assertTrue(ppiConn != null);
	}

}
