package org.mattyo161.commons.db.schema;

/**
 * This class represents a single column in an index
 * @author mattyo1
 *
 */
public class IndexColumn {
	private String columnName = "";
	private int order = 0;
	
	public static final int ASCENDING_SORT = 1;
	public static final int DESCENDING_SORT = 2;
	public static final int NO_SORT = 3;

	public IndexColumn(String columnName, int order) {
		this.columnName = columnName;
		this.order = order;
	}
	
	/**
	 * Accept the order in string form directly from a DatabaseMetaData object
	 * @param columnName
	 * @param order
	 */
	public IndexColumn(String columnName, String order) {
		this.columnName = columnName;
		setOrder(order);
	}
	public IndexColumn(String columnName) {
		this.columnName = columnName;
		setOrder(null);
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("{columnName = '").append(this.columnName).append("', order = '");
		if (this.order == ASCENDING_SORT) {
			buff.append("ASC");
		} else if (this.order == DESCENDING_SORT) {
			buff.append("DESC");
		} else {
			buff.append("NONE");
		}
		buff.append("'}");
		return buff.toString();
	}

	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	/**
	 * set the order using the DatabaseMetaData values
	 * @param order
	 */
	public void setOrder(String order) {
		if (order == null) {
			this.order = NO_SORT;
		} else if (order.equalsIgnoreCase("A")) {
			this.order = ASCENDING_SORT;
		} else {
			this.order = DESCENDING_SORT;
		}
	}
	
}
