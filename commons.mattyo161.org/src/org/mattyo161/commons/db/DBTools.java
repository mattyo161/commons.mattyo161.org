package org.mattyo161.commons.db;

/**
 * DBTools is a collection of static methods to make working with databases easier.
 * @author mattyo1
 *
 */
public class DBTools {
	
	/**
	 * Will checked the passed value and if it is null, it will return replacement otherwise
	 * it will return the value
	 * @param value the value to check for null
	 * @param replacement the value to return if value is null
	 * @return
	 */
	public static Object replaceIfNull(Object value, Object replacement) {
		if (value == null) {
			return replacement;
		} else {
			return value;
		}
	}
	
}
