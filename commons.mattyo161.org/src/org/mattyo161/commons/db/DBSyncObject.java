package org.mattyo161.commons.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


/**
 * Interface for passing DBSync a collection of PreparedStatements that will be 
 * used to synchronize data from one Database Table to another.
 * 
 * NOTE: DBSync will always require two DBSyncObjects to work. Only one will need to have
 * getUpdate, getAppend and getDelete set, as only one table is updated at a time, using a
 * master/slave concept. Both objects must have a getSelection and all Field lists must be defined
 * also the fieldLists must be in the same order, they do not have to have the same columnNames or
 * position values for their respective selections, but the order must match.
 * Ex. this is acceptable
 * FromObject - getAppendFields() = {"orderNum","accountNum","desc",...}
 * ToObject   - getAppendFields() = {"order", "account", "description",...}
 * This is because DBSync works strictly off of a count of columns in these Lists for comparisons and also
 * for grabbing values from the FromObject and passing them to the ToObject.
 * @author mattyo1
 *
 */
public interface DBSyncObject {
	/**
	 * A PreparedStatment that will be used to gather all the records from the given table
	 * that need to be synched. This PreparedStatement must contain all of the columns in
	 * both keyFields and syncFields.
	 * @return
	 */
	public PreparedStatement getSelection() throws SQLException;
	
	/**
	 * PreparedStatment that will be used to update a row from a table if all of the keyFields
	 * match in the synched table. It will need to have the same number of parameters as
	 * updateFields and keyFields combined, as DBSync will pass the values of updateFields (for row updates)
	 * followed by keyFields (for selecting the rows to be updated) to this prepared statement.
	 * @return
	 */
	public PreparedStatement getUpdate() throws SQLException;
	
	/**
	 * PreparedStatment that will be used to append a row from a table if it is missing. 
	 * It will need to have the same number of parameters as appendFields, 
	 * as DBSync will pass the values of appendFields to this prepared statement.
	 * @return
	 */
	public PreparedStatement getAppend() throws SQLException;
	
	/**
	 * PreparedStatment that will be used to delete a row from a table if it does not
	 * exist in the synched table. It will need to have the same number of parameters as
	 * keyFields, as DBSync will pass the values of keyFields to this prepared statement.
	 * @return
	 */
	public PreparedStatement getDelete() throws SQLException;
	
	/**
	 * A list of Strings or Integers representing the columnNames or columnPositions within the
	 * Selection PreparedStatement of the columns containing the data that need to be appended
	 * between the two DBSyncObjects. 
	 * 
	 * NOTE: AppendFields should contain KeyFields.
	 * @return
	 */
	public List getAppendFields();

	
	/**
	 * A list of Strings or Integers representing the columnNames or columnPositions within the
	 * Selection PreparedStatement of the columns containing the data that need to be updated
	 * between the two DBSyncObjects if the data is found not to match. 
	 * 
	 * NOTE: AppendFields should not contain KeyFields so updates do not affect the fields used to key
	 * off of. Some databases might not like updates to these fields.
	 * @return
	 */
	public List getUpdateFields();
	
	/**
	 * A list of Strings representing the columnNames within the
	 * Selection PreparedStatement of the columns containing the data that needs to be synched
	 * between the two DBSyncObjects. 
	 * 
	 * NOTE: SyncFields should not contain KeyFields, this will ensure that Updates do not attempt to
	 * modify the keyFields by mistake. KeyFields and SyncFields will be combined when performing appends.
	 * @return
	 */
	public List getKeyFields();
	
	/**
	 * A list of integers that are used to set the direction of the sort for that field, by default the direction
	 * is ascending so the value would be a 1, however if the direction is descending then the value should be -1 to
	 * reverse the order of the sort. 
	 * @return
	 */
	public List<Integer> getKeySort();

	/**
	 * Name of the table or object that is being synched
	 */
	public String getName();

	/**
	 * Description of the table or object that is being synched
	 */
	public String getDescription();
	
	/**
	 * Return a Create table string in the desired dbType based on SchemaTools.DBTYP_*
	 * @return
	 */
	public String getCreateTable(int dbType);
	
	/**
	 * Return whether the connection is an autocommit connection or not
	 */
	public boolean getAutoCommit();
	
	/**
	 * If the connection for this object is not set to autocommit then this will execute the commit
	 */
	public void commit();
	
	/**
	 * Return the connection element for the give object
	 * @return
	 */
	public Connection getConnection();

	/**
	 * Get the max number of seconds to allow for an update to run
	 */
	public int getUpdateTimeout();
	
	/**
	 * Get the max number of seconds to allow for an append to run
	 */
	public int getAppendTimeout();
}
