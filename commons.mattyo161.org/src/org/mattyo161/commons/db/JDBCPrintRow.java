package org.mattyo161.commons.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCPrintRow implements IResultSetProcessor {
	int rows = 0;

	public JDBCPrintRow() {
		this.rows = 0;
	}
	
	@Override
	public void processRow(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		rows++;
		for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			if (i > 1) {
				System.out.print(", ");
			}
			System.out.print(rs.getString(i));
		}
		System.out.println();
	}

	public int getRows() {
		// TODO Auto-generated method stub
		return this.rows;
	}

}
