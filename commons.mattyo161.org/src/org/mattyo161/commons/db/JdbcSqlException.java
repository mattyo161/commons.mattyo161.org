/**
 * 
 */
package org.mattyo161.commons.db;

/**
 * @author ssmyth
 *
 */
public class JdbcSqlException extends RuntimeException {

	/**
	 * 
	 */
	public JdbcSqlException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public JdbcSqlException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public JdbcSqlException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public JdbcSqlException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
