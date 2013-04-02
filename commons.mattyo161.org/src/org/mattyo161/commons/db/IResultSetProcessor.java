/**
 * 
 */
package org.mattyo161.commons.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ssmyth
 *
 */
public interface IResultSetProcessor {

	public void processRow(ResultSet rs) throws SQLException;
}
