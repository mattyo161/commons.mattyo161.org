package org.mattyo161.commons.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.TestCase;

public class JDBCTemplateTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(JDBCTemplateTest.class);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		this.foundIt = false;
	}

	/*
	 * Test method for 'org.mattyo161.commons.db.JDBCTemplate.query(String, String, Object, String)'
	 */
	public void testQueryStringStringObjectString() {

	}

	/*
	 * Test method for 'org.mattyo161.commons.db.JDBCTemplate.query(String, String, Object, String, Object[])'
	 */
//	public void testQueryStringStringObjectStringObjectArray() {
//		String query = "select * from displayad where displayad_id = ?";
//		JDBCTemplate.query(query, "contentserver", this, "process1", new Object[] {new Integer(287529)});
//		assertEquals("1234", displayAdName);
//	}
//	
//	public void testQueryStringStringObjectStringObjectArray2() {
//		String query = "select count(*) from displayad where depth = ? and starttime >= ?";
//		JDBCTemplate.query(query, "contentserver", this, "process2", new Object[] {new Double(11.0), "2005-01-01"});
//		assertTrue(this.foundIt);
//	}
//	
	private void process1(ResultSet rs) throws SQLException {
		displayAdName = rs.getString("displayad_name");
	}
	
	private void process2(ResultSet rs) throws SQLException {
		this.foundIt = true;
	}

	/*
	 * Test method for 'org.mattyo161.commons.db.JDBCTemplate.queryUpdate(String, String, Object[])'
	 */
	public void testQueryUpdateStringStringObjectArray() {

	}

	/*
	 * Test method for 'org.mattyo161.commons.db.JDBCTemplate.query(String, String, IResultSetProcessor)'
	 */
	public void testQueryStringStringIResultSetProcessor() {

	}

	/*
	 * Test method for 'org.mattyo161.commons.db.JDBCTemplate.queryUpdate(String, String)'
	 */
	public void testQueryUpdateStringString() {

	}

	private String displayAdName = "";
	private boolean foundIt = false;
}
