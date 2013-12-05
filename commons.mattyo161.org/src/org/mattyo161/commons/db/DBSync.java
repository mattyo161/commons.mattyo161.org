package org.mattyo161.commons.db;

/*
 * as400test.java
 *
 * Created on April 13, 2004, 12:21 PM
 */
import java.sql.*;

import org.mattyo161.commons.cal.*;
import org.mattyo161.commons.db.schema.Column;
import org.mattyo161.commons.db.schema.ResultSetSchema;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mattyo1
 */
public class DBSync {
	// setup variables for keeping track of where we are in the synch
	// process
	long appendRowCount = 0;
	long appendRowCountExecuted = 0;
	long deleteRowCount = 0;
	long deleteRowCountExecuted = 0;
	long updateRowCount = 0;
	long updateRowCountExecuted = 0;
	long processedRows = 0;
	// fetchSize
	int fetchSize = 0;
	// determine if commit should be done at the end of the session this helps with cursor issues that happen on postgres
	boolean commitAtEnd = false;

	int incrementalLog = 5000;
	int incrementalAppend = 5000;
	int incrementalUpdate = 5000;
	int incrementalDelete = 5000;

	boolean logAppends = true;

	boolean logDeletes = true;
	boolean logUpdates = true;
	boolean logMatches = false;
	
	boolean typeMatching = false;
	
	private DBSyncLogger dbSyncLogger = null;


	public void synchronizeTable(DBSyncObject fromTable, DBSyncObject toTable) {
		synchronizeTable(fromTable, toTable, true, true, true);
	}

	public void synchronizeTable(DBSyncObject fromTable, DBSyncObject toTable, boolean doAppends, boolean doDeletes, boolean doUpdates) {
		// TODO Auto-generated method stub

		//reset counters:

		// Create a new log
		DBSyncLog log = new DBSyncLog(toTable.getName());
		this.getDbSyncLogger().add(log);
		
		appendRowCount = 0;
		appendRowCountExecuted = 0;
		deleteRowCount = 0;
		deleteRowCountExecuted = 0;
		updateRowCount = 0;
		updateRowCountExecuted = 0;
		processedRows = 0;

		// Let's create some working variables to make the code a little easier
		// to read
		String fromTableName = fromTable.getName();
		String toTableName = toTable.getName();
		Cal startTime = new Cal();

		String tableKey = "";
		String currField = "";

		PreparedStatement psToTable = null;
		PreparedStatement psFromTable = null;
		PreparedStatement psToTableDelete = null;
		PreparedStatement psToTableAppend = null;
		PreparedStatement psToTableUpdate = null;
		ResultSet rsToTable = null;
		ResultSet rsFromTable = null;

		
		try {
			System.out.println("\n\n" + StringUtils.repeat("*", 50));
			System.out.println("** Synching table '" + fromTableName + "' " +
					"to '" + toTableName + "' at " + startTime);
			System.out.println(StringUtils.repeat("*", 50));

			// build the prepared statements that will be used in the synching process, we will need select statements from
			// both objects and we will need the update, append and delete statements only from the to object.
			psToTable = toTable.getSelection();
			if (fetchSize > 0) {
				psToTable.setFetchSize(fetchSize);
			}
			// check to see if this is a MySql connection if so then put it in steaming mode
			// the statement should already be in forward only and read only
			if (psToTable.getConnection().getClass().toString().indexOf("mysql") > 0) {
				psToTable.setFetchSize(Integer.MIN_VALUE);
			}

			psFromTable = fromTable.getSelection();
			if (fetchSize > 0) {
				psFromTable.setFetchSize(fetchSize);
			}
			// check to see if this is a MySql connection if so then put it in steaming mode
			// the statement should already be in forward only and read only
			if (psFromTable.getConnection().getClass().toString().indexOf("mysql") > 0) {
				psFromTable.setFetchSize(Integer.MIN_VALUE);
			}
			
			if (doDeletes) {
				psToTableDelete = toTable.getDelete();
			}
			if (doAppends) {
				psToTableAppend = toTable.getAppend();
			}
			if (doUpdates) {
				psToTableUpdate = toTable.getUpdate();
			}



			// Get the two result sets
			System.out.println("Getting to table ResultSet at " + new Cal());
			rsToTable = new DBSyncResultSet(psToTable.executeQuery());
			System.out.println("Getting from table ResultSet at " + new Cal());
			rsFromTable = new DBSyncResultSet(psFromTable.executeQuery());
//			DBSyncObjectQueries tempSync = (DBSyncObjectQueries) fromTable;
//			ResultSet rsFromTable = tempSync.getConnection().createStatement().executeQuery(tempSync.getSqlSelect());

			// we will need to loop through all fields defined in append, update and key field lists, if a field is already
			// defined we will not process it again.
			ResultSetSchema rsSchema = new ResultSetSchema(rsToTable.getMetaData());
			ResultSetSchema fromSchema = new ResultSetSchema(rsFromTable.getMetaData());

			// get the next rows, make sure to keep track of the returnValues as
			// these are used to determine
			// if an append, delete or update is needed.
			boolean rsToTableNext = rsToTable.next();
			boolean rsFromTableNext = rsFromTable.next();
			while (rsToTableNext || rsFromTableNext) {
				log.incrementProcessed();
				if (++processedRows % incrementalLog == 0) {
					System.out.println("Processed " + processedRows + " rows after " + (new Cal().diff(startTime)/1000) + " secs.");
				}

				if (!rsToTableNext) {
					// then we have reached the end of the to records and we
					// must append
					if (doAppends) {
						appendRowCount++;
						// use temp arraylist to hold append key values, which is used for debuging purposes.
						ArrayList temp = new ArrayList();
						for (int j = 0; j < fromTable.getKeyFields().size(); j++) {
							Object fromObj = getSqlValue(rsFromTable, fromTable.getKeyFields().get(j));

							if (fromObj == null) {
								temp.add("<NULL>");
							} else {
								temp.add(fromObj.toString());
							}
						}
						if (logAppends) {
							System.out.println("\t" + toTableName + " Appending Row " + temp.toString());
						}
						Object fromObj = null;
						for (int j = 0; j < fromTable.getAppendFields().size(); j++) {
							currField = (String) fromTable.getAppendFields().get(j);
							try {
								if (isTypeMatching()) {
									fromObj = getSqlValue(rsFromTable, fromTable.getAppendFields().get(j), rsSchema);
								} else {
									fromObj = getSqlValue(rsFromTable, fromTable.getAppendFields().get(j));
								}
								psToTableAppend.setObject(j + 1, fromObj);
							} catch (Exception e) {
								// Debugging stuff
								System.out.println(e);
								e.printStackTrace();
								// System.out.println("KeyFields: " + keyFields);
								log.addError("ERROR Appending row (" + tableKey + ") couldn't set field '" + currField + "' to '" + fromObj + "'",e);
								System.out.println(tableKey);
								throw new SQLException("ERROR: Setting field '" + currField + "' to '" + fromObj + "' (" + e + ").");
//								System.out.println("Setting field " + currField + " to \"\"");
//								psToTableAppend.setString(j + 1, "");
							}
						}
						psToTableAppend.addBatch();
					}
					rsFromTableNext = rsFromTable.next();
					if (!rsFromTableNext && psFromTable.getMaxRows() > 0) {
						if (psFromTable.getMoreResults()) {
							rsFromTable = new DBSyncResultSet(psFromTable.getResultSet());
							rsFromTableNext = rsFromTable.next();
						}
					}
				} else if (!rsFromTableNext) {
					// we have run out of rows in the From table so the rest
					// is extra and should be deleted
					if (doDeletes) {
						ArrayList temp = new ArrayList();
						// psToTableDelete.clearParameters();
						for (int j = 0; j < toTable.getKeyFields().size(); j++) {
							Object toObj = getSqlValue(rsToTable, toTable.getKeyFields().get(j));
							psToTableDelete.setObject(j + 1, toObj);

							if (toObj == null) {
								temp.add("<NULL>");
							} else {
								temp.add(toObj.toString());
							}
						}
						psToTableDelete.addBatch();
						// deleteRows.add(temp);
						deleteRowCount++;
						if (logDeletes) {
							System.out.println("\t" + toTableName + " Deleting Row " + temp.toString());
						}
					}
					rsToTableNext = rsToTable.next();
					if (!rsToTableNext && psToTable.getMaxRows() > 0) {
						if (psToTable.getMoreResults()) {

							rsToTable = new DBSyncResultSet(psToTable.getResultSet());
							rsToTableNext = rsToTable.next();
						}
					}
				} else {
					// we are in the middle of the tables and we need to do
					// a key match
					// and we need to perform an append, delete or update
					// depending on the compare results.
					int match = 0;
					ArrayList temp = new ArrayList();
					for (int j = 0; j < toTable.getKeyFields().size(); j++) {
						Object toObj = getSqlValue(rsToTable, toTable.getKeyFields().get(j));
						Object fromObj = getSqlValue(rsFromTable, fromTable.getKeyFields().get(j));
						// we need to get the sorting position of at least the toTable, since they should both match only one is needed
						int keySort = toTable.getKeySort().get(j).intValue();
						int test = -1;
						int colType = rsSchema.getColumn(toTable.getKeyFields().get(j)).getSqlType();
						try {
							test = compareSqlObject(colType, toObj, fromObj) * keySort;
						} catch (Exception e) {
							System.out.println("Error comparing fields " +
									fromTable.getKeyFields().get(j) + " (" + fromObj + ") -> " +
									toTable.getKeyFields().get(j) + " (" + toObj + ")");
							log.addError("Error comparing fields " +
									fromTable.getKeyFields().get(j) + " (" + fromObj + ") -> " +
									toTable.getKeyFields().get(j) + " (" + toObj + ")", e);
							e.printStackTrace();
							throw new RuntimeException("Failed to compare sql values "+
									fromTable.getKeyFields().get(j) + " (" + fromObj + ") -> " +
									toTable.getKeyFields().get(j) + " (" + toObj + ")");
						}

						if (fromObj == null) {
							temp.add("<NULL>");
						} else {
							temp.add(fromObj.toString());
						}
						if (test != 0) {
							match = test;
							break;
						}
					}
					tableKey = fromTableName + ":" + StringUtils.join(temp.iterator(), ", ");
					if (match == 0) {
						// the key fields match and we need to do an update
						if (doUpdates) {
							// keep track of the current field we are on incase we throw an exception
							Object currToField = "";
							Object currFromField = "";
							try {
								boolean recordsMatch = true;
								boolean test = false;
								// Create a HashMap to store the changed valued
								HashMap recordChanges = new HashMap();
								for (int k = 0; k < toTable.getUpdateFields().size(); k++) {
									currToField = toTable.getUpdateFields().get(k);
									currFromField = fromTable.getUpdateFields().get(k);
									Object toObj = getSqlValue(rsToTable, toTable.getUpdateFields().get(k));
									Object fromObj = getSqlValue(rsFromTable, fromTable.getUpdateFields().get(k));

									int colType = rsSchema.getColumn(currToField).getSqlType();
									
									test = equalsSqlObject(colType, toObj, fromObj);


									if (!test) {
										recordsMatch = false;
										// store the changes in an array so they can
										// be logged
										ArrayList changes = new ArrayList();
										changes.add(toObj);
										changes.add(fromObj);

										// log the change to the field for debugging
										recordChanges.put(currToField, changes);
									}
								}
								if (recordsMatch) {
									if (logMatches) {
										System.out.println("\t" + toTableName + " Records match keys " + temp.toString());
									}
								} else {
									// lets update or commit the changes

									// first we need to update the new values from the fromTable
									for (int x = 0; x < fromTable.getUpdateFields().size(); x++) {
										if (isTypeMatching()) {
											psToTableUpdate.setObject(x + 1, getSqlValue(rsFromTable, fromTable.getUpdateFields().get(x), rsSchema));
										} else {
											psToTableUpdate.setObject(x + 1, getSqlValue(rsFromTable, fromTable.getUpdateFields().get(x)));
										}
									}
									// now we need to add the where fields but we have to start at the end of the updateFieldCount
									int updateFieldCount = fromTable.getUpdateFields().size();
									for (int x = 0; x < toTable.getKeyFields().size(); x++) {
										if (isTypeMatching()) {
											psToTableUpdate.setObject(updateFieldCount + x + 1, getSqlValue(rsToTable, toTable.getKeyFields().get(x), rsSchema));
										} else {
											psToTableUpdate.setObject(updateFieldCount + x + 1, getSqlValue(rsToTable, toTable.getKeyFields().get(x)));
										}

									}

									updateRowCount++;
									try {
										psToTableUpdate.addBatch();
									} catch (Exception e) {
										log.addError("UPDATE Error", e);
										System.out.println("UPDATE Error: " + e.toString());
									}
									if (logUpdates) {
										System.out.println("\t" + toTableName + " Records match keys " + temp.toString() + " Values Differ");
										for (Iterator k = recordChanges.keySet().iterator(); k.hasNext();) {
											String key = (String) k.next();
											List currObject = (List) recordChanges.get(key);
											System.out.println("\t" + key + "\t->\tFROM:'" + currObject.get(0) + "'\tTO:'" + currObject.get(1) + "'");
										}
									}
								}
							} catch (Exception e) {
								log.addError("Key: (" + tableKey + "); AtField: FromField(" + currFromField + ") ToField(" + currToField + ")", e);
								System.out.println(tableKey);
	//							System.out.println("KeyFields: " + keyFields);
								System.out.println("AtField: FromField(" + currFromField + ") ToField(" + currToField + ")");
								e.printStackTrace();
							}
						}
						rsToTableNext = rsToTable.next();
						if (!rsToTableNext && psToTable.getMaxRows() > 0) {
							if (psToTable.getMoreResults()) {
								rsToTable = new DBSyncResultSet(psToTable.getResultSet());
								rsToTableNext = rsToTable.next();
							}
						}
						rsFromTableNext = rsFromTable.next();
						if (!rsFromTableNext && psFromTable.getMaxRows() > 0) {
							if (psFromTable.getMoreResults()) {
								rsFromTable = new DBSyncResultSet(psFromTable.getResultSet());
								rsFromTableNext = rsFromTable.next();
							}
						}
					} else if (match < 0) {
						// we need to delete the row
						if (doDeletes) {
							ArrayList deleteTemp = new ArrayList();
							// psToTableDelete.clearParameters();
							for (int j = 0; j < toTable.getKeyFields().size(); j++) {
								Object toObj = getSqlValue(rsToTable, toTable.getKeyFields().get(j));
								psToTableDelete.setObject(j + 1, toObj);

								if (toObj == null) {
									deleteTemp.add("<NULL>");
								} else {
									deleteTemp.add(toObj.toString());
								}
							}
							psToTableDelete.addBatch();
							// deleteRows.add(temp);
							deleteRowCount++;
							if (logDeletes) {
								System.out.println("\t" + toTableName + " Deleting Row " + deleteTemp.toString());
							}
						}
						rsToTableNext = rsToTable.next();
						if (!rsToTableNext && psToTable.getMaxRows() > 0) {
							if (psToTable.getMoreResults()) {
								rsToTable = new DBSyncResultSet(psToTable.getResultSet());
								rsToTableNext = rsToTable.next();
							}
						}
					} else if (match > 0) {
						if (doAppends) {
							// we need to append this row
							appendRowCount++;
							// use temp arraylist to hold append key values for debugging purposes
							ArrayList appendTemp = new ArrayList();
							for (int j = 0; j < fromTable.getKeyFields().size(); j++) {
								Object fromObj = getSqlValue(rsFromTable, fromTable.getKeyFields().get(j));
								if (fromObj == null) {
									appendTemp.add("<NULL>");
								} else {
									appendTemp.add(fromObj.toString());
								}
							}
							if (logAppends) {
								System.out.println("\t" + toTableName + " Appending Row " + appendTemp.toString());
							}
							for (int j = 0; j < fromTable.getAppendFields().size(); j++) {
								currField = (String) fromTable.getAppendFields().get(j);
								try {
									if (isTypeMatching()) {
										psToTableAppend.setObject(j + 1, getSqlValue(rsFromTable, currField, rsSchema));
									} else {
										psToTableAppend.setObject(j + 1, getSqlValue(rsFromTable, currField));
									}
								} catch (Exception e) {
									// Debugging stuff
									System.out.println(e);
									// System.out.println("KeyFields: " +
									// keyFields);
									log.addError("Key (" + tableKey + ") Setting field " + currField + " to \"\"", e);
									System.out.println(tableKey);
									System.out.println("Setting field " + currField + " to \"\"");
									psToTableAppend.setString(j + 1, "");
								}
							}
							psToTableAppend.addBatch();
						}
						rsFromTableNext = rsFromTable.next();
						if (!rsFromTableNext && psFromTable.getMaxRows() > 0) {
							if (psFromTable.getMoreResults()) {
								rsFromTable = new DBSyncResultSet(psFromTable.getResultSet());
								rsFromTableNext = rsFromTable.next();
							}
						}
					}

				}

				/*
				 * Had problems executing inserts and deletes while streaming
				 * data for MySQL so will stop doing this and simply execute the
				 * append and delete at the end of the process. Might need to
				 * find another way. found that will run out of memory with
				 * 22000 rows to append, will now check to see if we reached the
				 * end of the to table and if so then append
				 */

				// lets see what happens if we don't wait until we reach the end of the rows in the to table.

				// make sure to execute deletes before appends, so we don't
				// have conflicts
				if (deleteRowCount % incrementalDelete == 0 && deleteRowCount > deleteRowCountExecuted) { // && !rsToTableNext) {
					// We need to execute the batch
					log.startDelete();
					System.out.print(deleteRowCount + " deletes have accumulated, executing delete after " + (new Cal().diff(startTime) / 1000) + " secs.");
					Cal sqlStartTime = new Cal();
					try {
						int[] deleteReturn = psToTableDelete.executeBatch();
						if (!toTable.getAutoCommit() && !isCommitAtEnd()) {
							System.out.print(" Commiting changes ");
							toTable.commit();
						}
						int successfulUpdates = 0;
						int failedUpdates = 0;
						for (int y = 0; y < deleteReturn.length; y++) {
							int currentResult = deleteReturn[y];
							if (currentResult != Statement.EXECUTE_FAILED) {
								successfulUpdates += currentResult;
							} else {
								failedUpdates++;
							}
						}
						log.incrementDeletes(successfulUpdates);
						log.incrementFailedDeletes(failedUpdates);
						System.out.println(" (" + successfulUpdates + " Deletes Succeeded " + failedUpdates + " failed) in " + new Cal().diff(sqlStartTime) + " ms.");
					} catch (SQLException e) {
						log.addError("Failure to delete records", e);
						System.out.println("\nFailure to delete records. " + e.toString());
						e.printStackTrace();
						SQLException nextE = e.getNextException();
						while (nextE != null) {
							System.out.println(nextE.toString());
							nextE.printStackTrace();
							nextE = nextE.getNextException();
						}
					} catch (Exception e) {
						log.addError("Failure to delete records", e);
						System.out.println("\nFailure to delete records.");
						e.printStackTrace();
					}
					log.doneDelete();
					psToTableDelete.clearParameters();
					deleteRowCountExecuted = deleteRowCount;
				}
				if (updateRowCount % incrementalUpdate == 0 && updateRowCount > updateRowCountExecuted) { // && !rsToTableNext) {
					// We need to execute the batch
					log.startUpdate();
					System.out.print(updateRowCount + " updates have accumulated, executing update after " + (new Cal().diff(startTime) / 1000) + " secs.");
					Cal sqlStartTime = new Cal();
					try {
						int[] updateReturn = psToTableUpdate.executeBatch();
						if (!toTable.getAutoCommit() && !isCommitAtEnd()) {
							System.out.print(" Commiting changes ");
							toTable.commit();
						}
						int successfulUpdates = 0;
						int failedUpdates = 0;
						for (int y = 0; y < updateReturn.length; y++) {
							int currentResult = updateReturn[y];
							if (currentResult != Statement.EXECUTE_FAILED) {
								successfulUpdates += currentResult;
							} else {
								failedUpdates++;
							}
						}
						log.incrementUpdates(successfulUpdates);
						log.incrementFailedUpdates(failedUpdates);
						System.out.println(" (" + successfulUpdates + " Updates Succeeded " + failedUpdates + " failed) in " + new Cal().diff(sqlStartTime) + " ms.");
					} catch (SQLException e) {
						log.addError("Failure to update records", e);
						System.out.println("\nFailure to update records. " + e.toString());
						e.printStackTrace();
						SQLException nextE = e.getNextException();
						while (nextE != null) {
							System.out.println(nextE.toString());
							nextE.printStackTrace();
							nextE = nextE.getNextException();
						}
					} catch (Exception e) {
						log.addError("Failure to update records", e);
						System.out.println("\nFailure to update records.");
						e.printStackTrace();
					}
					log.doneUpdate();
					updateRowCountExecuted = updateRowCount;
					psToTableUpdate.clearParameters();
				}
				if (appendRowCount % incrementalAppend == 0 && appendRowCount > appendRowCountExecuted) {
					// make sure we execute any deletes that need to be
					// performed
					if (deleteRowCount > deleteRowCountExecuted) {
						// We need to execute the batch
						log.startDelete();
						System.out.print(deleteRowCount + " deletes have accumulated, executing delete before append.");
						Cal sqlStartTime = new Cal();
						try {
							int[] deleteReturn = psToTableDelete.executeBatch();
							if (!toTable.getAutoCommit() && !isCommitAtEnd()) {
								System.out.print(" Commiting changes ");
								toTable.commit();
							}
							int successfulUpdates = 0;
							int failedUpdates = 0;
							for (int y = 0; y < deleteReturn.length; y++) {
								int currentResult = deleteReturn[y];
								if (currentResult != Statement.EXECUTE_FAILED) {
									successfulUpdates += currentResult;
								} else {
									failedUpdates++;
								}
							}
							log.incrementDeletes(successfulUpdates);
							log.incrementFailedDeletes(failedUpdates);
							System.out.println(" (" + successfulUpdates + " Deletes Succeeded " + failedUpdates + " failed) in " + new Cal().diff(sqlStartTime) + " ms.");
						} catch (SQLException e) {
							log.addError("Failure to delete records", e);
							System.out.println("\nFailure to delete records. " + e.toString());
							e.printStackTrace();
							SQLException nextE = e.getNextException();
							while (nextE != null) {
								System.out.println(nextE.toString());
								nextE.printStackTrace();
								nextE = nextE.getNextException();
							}
						} catch (Exception e) {
							log.addError("Failure to delete records", e);
							System.out.println("\nFailure to delete records.");
							e.printStackTrace();
						}
						log.doneDelete();
						psToTableDelete.clearParameters();
						deleteRowCountExecuted = deleteRowCount;
					}
					// We need to execute the batch
					log.startAppend();
					System.out.print(appendRowCount + " appends have accumulated, executing insert after " + (new Cal().diff(startTime) / 1000) + " secs.");
					Cal sqlStartTime = new Cal();
					try {
						int[] appendReturn = psToTableAppend.executeBatch();
						if (!toTable.getAutoCommit() && !isCommitAtEnd()) {
							System.out.print(" Commiting changes ");
							toTable.commit();
						}
						int successfulUpdates = 0;
						int failedUpdates = 0;
						for (int y = 0; y < appendReturn.length; y++) {
							int currentResult = appendReturn[y];
							if (currentResult != Statement.EXECUTE_FAILED) {
								successfulUpdates += currentResult;
							} else {
								failedUpdates++;
							}
						}
						log.incrementAppends(successfulUpdates);
						log.incrementFailedAppends(failedUpdates);
						System.out.println(" (" + successfulUpdates + " Appends Succeeded " + failedUpdates + " failed) in " + new Cal().diff(sqlStartTime) + " ms.");
					} catch (SQLException e) {
						log.addError("Failure to append records", e);
						System.out.println("\nFailure to append records. " + e.toString());
						e.printStackTrace();
						SQLException nextE = e.getNextException();
						while (nextE != null) {
							System.out.println(nextE.toString());
							nextE.printStackTrace();
							nextE = nextE.getNextException();
						}
					} catch (Exception e) {
						log.addError("Failure to append records", e);
						System.out.println("Failure to append records.");
						e.printStackTrace();
					}
					log.doneAppend();
					psToTableAppend.clearParameters();
					appendRowCountExecuted = appendRowCount;
				}
			}
			rsToTable.close();
			rsFromTable.close();

			psToTable.close();
			psFromTable.close();
			System.out.println("Processed " + processedRows + " rows in " + (new Cal().diff(startTime)) + " ms.");

			if (updateRowCount > 0) {
				log.startUpdate();
				System.out.print(updateRowCount + " updates have accumulated, executing update after " + (new Cal().diff(startTime) / 1000) + " secs.");
				Cal sqlStartTime = new Cal();
				try {
					int[] updateReturn = psToTableUpdate.executeBatch();
					if (!toTable.getAutoCommit() && !isCommitAtEnd()) {
						System.out.print(" Commiting changes ");
						toTable.commit();
					}
					int successfulUpdates = 0;
					int failedUpdates = 0;
					for (int y = 0; y < updateReturn.length; y++) {
						int currentResult = updateReturn[y];
						if (currentResult != Statement.EXECUTE_FAILED) {
							successfulUpdates += currentResult;
						} else {
							failedUpdates++;
						}
					}
					log.incrementUpdates(successfulUpdates);
					log.incrementFailedUpdates(failedUpdates);
					System.out.println(" (" + successfulUpdates + " Updates Succeeded " + failedUpdates + " failed) in " + new Cal().diff(sqlStartTime) + " ms.");
				} catch (SQLException e) {
					log.addError("Failure to update records", e);
					System.out.println("\nFailure to update records. " + e.toString());
					e.printStackTrace();
					SQLException nextE = e.getNextException();
					while (nextE != null) {
						System.out.println(nextE.toString());
						nextE.printStackTrace();
						nextE = nextE.getNextException();
					}
				} catch (Exception e) {
					log.addError("Failure to update records", e);
					System.out.println("\nFailure to update records.");
					e.printStackTrace();
				}
				log.doneUpdate();
			}
			if (psToTableUpdate != null) { 
				psToTableUpdate.close();
			}
			if (deleteRowCount > 0) {
				log.startDelete();
				System.out.print(deleteRowCount + " deletes have accumulated, executing delete after " + (new Cal().diff(startTime) / 1000) + " secs.");
				Cal sqlStartTime = new Cal();
				try {
					int[] deleteReturn = psToTableDelete.executeBatch();
					if (!toTable.getAutoCommit() && !isCommitAtEnd()) {
						System.out.print(" Commiting changes ");
						toTable.commit();
					}
					int successfulUpdates = 0;
					int failedUpdates = 0;
					for (int y = 0; y < deleteReturn.length; y++) {
						int currentResult = deleteReturn[y];
						if (currentResult != Statement.EXECUTE_FAILED) {
							successfulUpdates += currentResult;
						} else {
							failedUpdates++;
						}
					}
					log.incrementDeletes(successfulUpdates);
					log.incrementFailedDeletes(failedUpdates);
					System.out.println(" (" + successfulUpdates + " Deletes Succeeded " + failedUpdates + " failed) in " + new Cal().diff(sqlStartTime) + " ms.");
				} catch (SQLException e) {
					log.addError("Failure to delete records", e);
					System.out.println("\nFailure to delete records. " + e.toString());
					e.printStackTrace();
					SQLException nextE = e.getNextException();
					while (nextE != null) {
						System.out.println(nextE.toString());
						nextE.printStackTrace();
						nextE = nextE.getNextException();
					}
				} catch (Exception e) {
					log.addError("Failure to delete records", e);
					System.out.println("Failure to delete records.");
					e.printStackTrace();
				}
				log.doneDelete();
			}
			if (psToTableDelete != null) { 
				psToTableDelete.close();
			}
			if (appendRowCount > 0) {
				log.startAppend();
				System.out.print(appendRowCount + " appends have accumulated, executing insert after " + (new Cal().diff(startTime) / 1000) + " secs.");
				Cal sqlStartTime = new Cal();
				try {
					int[] appendReturn = psToTableAppend.executeBatch();
					if (!toTable.getAutoCommit() && !isCommitAtEnd()) {
						System.out.print(" Commiting changes ");
						toTable.commit();
					}
					int successfulUpdates = 0;
					int failedUpdates = 0;
					for (int y = 0; y < appendReturn.length; y++) {
						int currentResult = appendReturn[y];
						if (currentResult != Statement.EXECUTE_FAILED) {
							successfulUpdates += currentResult;
						} else {
							failedUpdates++;
						}
					}
					log.incrementAppends(successfulUpdates);
					log.incrementFailedAppends(failedUpdates);
					System.out.println(" (" + successfulUpdates + " Appends Succeeded " + failedUpdates + " failed) in " + new Cal().diff(sqlStartTime) + " ms.");
				} catch (SQLException e) {
					log.addError("Failure to append records", e);
					System.out.println("\nFailure to append records. " + e.toString());
					e.printStackTrace();
					SQLException nextE = e.getNextException();
					while (nextE != null) {
						System.out.println(nextE.toString());
						nextE.printStackTrace();
						nextE = nextE.getNextException();
					}
				} catch (Exception e) {
					log.addError("Failure to append records", e);
					System.out.println("\nFailure to append records.");
					e.printStackTrace();
				}
				log.doneAppend();
			}
			if (psToTableAppend != null) { 
				psToTableAppend.close();
			}

			if (!toTable.getAutoCommit() && isCommitAtEnd()) {
				System.out.println("Now processing all commits");
				Cal startCommitTime = new Cal();
				toTable.commit();
				System.out.println("Commits completed in " + (new Cal().diff(startCommitTime)/1000) + " secs");
			}
			System.out.println("Done synching table '" + fromTableName + "' " +
					"to '" + toTableName + "' at " + new Cal() + " in " + (new Cal().diff(startTime)/1000) + " secs");
		} catch (Exception e) {
			log.addError("Error in main DBSync clause", e);
			System.out.println("Error in main DBSync try clause:");
			e.printStackTrace();
		} finally {
			if (rsFromTable != null) {
				try { rsFromTable.close(); } catch (Exception e ) {}
				rsFromTable = null;
			}
			if (rsToTable != null) {
				try { rsToTable.close(); } catch (Exception e ) {}
				rsToTable = null;
			}
			if (psToTable != null) {
				try { psToTable.close(); } catch (Exception e ) {}
				psToTable = null;
			}
			if (psFromTable != null) {
				try { psFromTable.close(); } catch (Exception e ) {}
				psFromTable = null;
			}
			if (psToTableAppend != null) {
				try { psToTableAppend.close(); } catch (Exception e ) {}
				psToTableAppend = null;
			}
			if (psToTableDelete != null) {
				try { psToTableDelete.close(); } catch (Exception e ) {}
				psToTableDelete = null;
			}
			if (psToTableUpdate != null) {
				try { psToTableUpdate.close(); } catch (Exception e ) {}
				psToTableUpdate = null;
			}
		}
		System.gc();
		log.done();
		System.out.println(log.toString());
	}

	/**
	 * Use this version when there are issues matching up the schema values this will make sure that the result that is returned is compatible with the 
	 * schema that is being written to.
	 * @param rs
	 * @param currField
	 * @param schema
	 * @return
	 * @throws SQLException
	 */
	private static Object getSqlValue(ResultSet rs, Object currField, ResultSetSchema schema) throws SQLException {
		// first get the object the regular way
		Object theObj = getSqlValue(rs, currField);
		// now we will add some checks based on if the schema and theObj do not match up
		Column col = schema.getColumn(currField);
		switch (col.getSqlType()) {
		case Types.FLOAT:
		case Types.REAL:
		case Types.DOUBLE:
			if (String.class.isInstance(theObj)) {
				theObj = new Double(theObj.toString());
			}
			break;
		}
		return theObj;
	}
	
	private static Object getSqlValue(ResultSet rs, Object currField) throws SQLException {
		Object theObj = null;
		if (Integer.class.isInstance(currField)) {
			theObj = rs.getObject(((Integer) currField).intValue());
		} else {
			// we will assume this is a string.
			theObj = rs.getObject(currField.toString());
		}
		// found an issue converting a sybase timestamp to an object, so we convert it here to a regular sqltimestamp
		if (com.sybase.jdbc2.tds.SybTimestamp.class.isInstance(theObj)) {
			theObj = new Cal(theObj).getSqlTimestamp();
		} else if (Clob.class.isInstance(theObj)) {
			Clob theClob = (Clob) theObj;
			StringBuffer buff = new StringBuffer();
			try {
				Reader fromReader = theClob.getCharacterStream();
				char[] fromBuff = new char[1024];
				int pos = 0;
				while (pos < theClob.length()) {
					int fromLen = fromReader.read(fromBuff, 0,1024);
					buff.append(fromBuff, 0, fromLen);
					pos += fromLen;
				}
//				theObj = new String(new String(buff.toString().getBytes(), "ISO-8859-1").getBytes("UTF8"));
				theObj = buff.toString();
			} catch (SQLException e ) {
				// swallow the exception for now.
			} catch (IOException e ) {
				// swallow the exception for now.
			}
			
		}
		return theObj;
	}

	private static boolean equalsSqlObject(int matchType, Object toObj, Object fromObj) {
		// TODO Auto-generated method stub
		boolean test = false;
		// make sure that toObj has a value
		if (toObj == null) {
			if (fromObj != null) {
				return false;
			} else {
				return true;
			}
		} else if (fromObj == null) {
			if (toObj != null) {
				return false;
			} else {
				return true;
			}
		}
		switch (matchType) {
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			// remove trailing spaces from string
			// comparisons
			if (Clob.class.isInstance(fromObj)) {
				Clob fromClob = (Clob) fromObj;
				try {
					if (((String) toObj).length() == fromClob.length()) {
						Reader toReader = new BufferedReader(new StringReader((String) toObj));
						Reader fromReader = fromClob.getCharacterStream();
						char[] toBuff = new char[1024];
						char[] fromBuff = new char[1024];
						int pos = 0;
						boolean clobMatches = true;
						while (pos < fromClob.length() && clobMatches) {
							int toLen = toReader.read(toBuff, 0, 1024);
							fromReader.read(fromBuff, 0,1024);
							for (int x = 0; x < toLen && clobMatches; x++) {
								if (toBuff[x] != fromBuff[x]) {
									clobMatches = false;
								}
							}
							pos += toLen;
						}
						if (clobMatches) {
							test = true;
						}
					}
				} catch (SQLException e ) {
					// swallow the exception for now.
				} catch (IOException e ) {
					// swallow the exception for now.
				}
			} else {
				test = ((String) toObj).replaceAll("\\s+$", "").equalsIgnoreCase(((String) fromObj).replaceAll("\\s+$", ""));
			}
			break;
		case Types.BIGINT:
			test = ((BigInteger) toObj).equals(fromObj);
			break;
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			long a = 0;
			long b = 0;
			if (Integer.class.isInstance(fromObj)) {
				a = ((Integer) fromObj).longValue();
			} else if (Long.class.isInstance(fromObj)) {
				a = ((Long) fromObj).longValue();
			} else if (Float.class.isInstance(fromObj)) {
				a = ((Float) fromObj).longValue();
			} else if (Double.class.isInstance(fromObj)) {
				a = ((Double) fromObj).longValue();
			} else {
				a = (new Long(fromObj.toString())).longValue();
			}
			if (Integer.class.isInstance(toObj)) {
				b = ((Integer) toObj).longValue();
			} else if (Long.class.isInstance(toObj)) {
				b = ((Long) toObj).longValue();
			} else if (Float.class.isInstance(toObj)) {
				b = ((Long) toObj).longValue();
			} else if (Double.class.isInstance(toObj)) {
				b = ((Long) toObj).longValue();
			} else {
				b = (new Long(toObj.toString())).longValue();
			}
			test = (a == b);
			break;
		case Types.BOOLEAN:
			if (Integer.class.isInstance(fromObj)) {
				test = ((Boolean) toObj).equals(new Boolean(((Integer) fromObj).intValue() != 0));
			} else if (String.class.isInstance(fromObj)) {
				test = ((Boolean) toObj).equals(new Boolean(!((String) fromObj).trim().equals("")));
			} else if (Boolean.class.isInstance(fromObj)) {
				test = ((Boolean) toObj).equals((Boolean) fromObj);
			}
			break;
		case Types.FLOAT:
		case Types.REAL:
			if (BigDecimal.class.isInstance(fromObj)) {
				test = ((Float) toObj).equals(new Float(((BigDecimal) fromObj).floatValue()));
			} else if (Float.class.isInstance(fromObj)) {
				test = ((Float) toObj).equals((Float) fromObj);
			} else if (Double.class.isInstance(fromObj)) {
				test = ((Float) toObj).equals(new Float(((Double) fromObj).floatValue()));
			} else {
				test = ((Float) toObj).equals(new Float(fromObj.toString()));
			}
			break;
		case Types.DOUBLE:
			if (BigDecimal.class.isInstance(fromObj)) {
				test = ((Double) toObj).equals(new Double(((BigDecimal) fromObj).doubleValue()));
			} else if (Float.class.isInstance(fromObj)) {
				test = ((Double) toObj).equals(new Double(((Float) fromObj).floatValue()));
			} else if (Double.class.isInstance(fromObj)) {
				test = ((Double) toObj).equals((Double) fromObj);
			} else {
				test = ((Double) toObj).equals(new Double(fromObj.toString()));
			}
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			BigDecimal tmpToObj = (BigDecimal) toObj;
			BigDecimal tmpFromObj = null;

			if (BigDecimal.class.isInstance(fromObj)) {
//				test = ((BigDecimal) toObj).equals((BigDecimal) fromObj);
				tmpFromObj = (BigDecimal) fromObj;
			} else if (Double.class.isInstance(fromObj)) {
//				test = ((BigDecimal) toObj).equals(new BigDecimal(((Double) fromObj).doubleValue()));
				tmpFromObj = new BigDecimal(((Double) fromObj).doubleValue());
			} else if (Float.class.isInstance(fromObj)) {
//				test = ((BigDecimal) toObj).equals(new BigDecimal(((Float) fromObj).floatValue()));
				tmpFromObj = new BigDecimal(((Float) fromObj).floatValue());
			} else {
				// Lets turn it into a BigDecimal, using the string creator
//				test = ((BigDecimal) toObj).equals(new BigDecimal(fromObj.toString()));
				tmpFromObj = new BigDecimal(fromObj.toString());
			}
			// we need to make sure that we compare BigDecimals of the same scale and we want to round up any number if the scale
			// cannot be met
			test = tmpToObj.equals(tmpFromObj.setScale(tmpToObj.scale(),BigDecimal.ROUND_HALF_UP));
			break;
		case Types.DATE:
			test = new Cal(toObj).equals(new Cal(fromObj));
//			test = ((Date) toObj).equals(fromObj);
			break;
		case Types.TIME:
			test = ((Time) toObj).equals(fromObj);
			break;
		case Types.TIMESTAMP:
			test = new Cal(toObj).equals(new Cal(fromObj));
//			test = ((Timestamp) toObj).equals(fromObj);
			break;
		case Types.CLOB:
			// at this point fromObj must also be a blob
//			System.out.println("checking clob");
			if (Clob.class.isInstance(fromObj)) {
				Clob toClob = (Clob) toObj;
				Clob fromClob = (Clob) fromObj;
				try {
					if (toClob.length() == fromClob.length()) {
						Reader toReader = toClob.getCharacterStream();
						Reader fromReader = fromClob.getCharacterStream();
						char[] toBuff = new char[1024];
						char[] fromBuff = new char[1024];
						int pos = 0;
						boolean clobMatches = true;
						while (pos < toClob.length() && clobMatches) {
							int toLen = toReader.read(toBuff, 0, 1024);
							fromReader.read(fromBuff, 0,1024);
							for (int x = 0; x < toLen && clobMatches; x++) {
								if (toBuff[x] != fromBuff[x]) {
									clobMatches = false;
								}
							}
							pos += toLen;
						}
						if (clobMatches) {
							test = true;
						}
					}
				} catch (SQLException e ) {
					// swallow the exception for now.
				} catch (IOException e ) {
					// swallow the exception for now.
				}
			} else if (String.class.isInstance(fromObj) && String.class.isInstance(toObj)) {
				test = toObj.toString().trim().equalsIgnoreCase(fromObj.toString().trim());
			}
			break;
		case Types.BLOB:
			// at this point fromObj must also be a blob
//			System.out.println("checking blob");
			if (Blob.class.isInstance(fromObj)) {
				Blob toBlob = (Blob) toObj;
				Blob fromBlob = (Blob) fromObj;
				try {
					if (toBlob.length() == fromBlob.length()) {
						// now we need to do a byte comparison
						long pos = 0;
						boolean blobMatches = true;
						while (pos < toBlob.length() && blobMatches) {
							byte[] toBytes = toBlob.getBytes(pos, 1024);
							byte[] fromBytes = toBlob.getBytes(pos,1024);
							for (int x = 0; x < toBytes.length && blobMatches; x++) {
								if (toBytes[x] != fromBytes[x]) {
									blobMatches = false;
								}
							}
							pos += toBytes.length;
						}
						if (blobMatches) {
							test = true;
						}
					}
				} catch (SQLException e) {
					// just swallow the exception for now
				}
			}
//			test = ((Blob) toObj).equals(fromObj);
			break;
		default:
			test = toObj.toString().trim().equalsIgnoreCase(fromObj.toString().trim());
			break;
		}
		return test;
	}

	private static int compareSqlObject(int matchType, Object toObj, Object fromObj) {
		// TODO Auto-generated method stub
		int test = 0;
		// make sure that toObj has a value
		if (toObj == null) {
			if (fromObj != null) {
				return 1;
			} else {
				return 0;
			}
		} else if (fromObj == null) {
			if (toObj != null) {
				return 1;
			} else {
				return 0;
			}
		}
		switch (matchType) {
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			// remove trailing spaces from string
			// comparisons
			test = ((String) toObj).replaceAll("\\s+$", "").compareToIgnoreCase(((String) fromObj).replaceAll("\\s+$", ""));
			break;
		case Types.BIGINT:
			if (BigDecimal.class.isInstance(toObj)) {
				toObj = ((BigDecimal) toObj).toBigInteger();
			} else if (Long.class.isInstance(toObj)) {
				toObj = BigInteger.valueOf(((Long) toObj).longValue());
			} else if (Integer.class.isInstance(toObj)) {
				toObj = BigInteger.valueOf(((Integer) toObj).longValue());
			}
			if (BigDecimal.class.isInstance(fromObj)) {
				test = ((BigInteger) toObj).compareTo(((BigDecimal) fromObj).toBigInteger());
			} else if (BigInteger.class.isInstance(fromObj)) {
					test = ((BigInteger) toObj).compareTo((BigInteger)fromObj);
			} else if (Long.class.isInstance(fromObj)) {
				test = ((BigInteger) toObj).compareTo(BigInteger.valueOf(((Long) fromObj).longValue()));
			} else if (Integer.class.isInstance(fromObj)) {
				test = ((BigInteger) toObj).compareTo(BigInteger.valueOf(((Integer) fromObj).longValue()));
			}
			break;
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			if (Long.class.isInstance(fromObj)) {
				test = ((Integer) toObj).compareTo(((Long)fromObj).intValue());;
			} else  {
				test = ((Integer) toObj).compareTo((Integer)fromObj);
			}
			break;
		case Types.BOOLEAN:
			if (Integer.class.isInstance(fromObj)) {
				test = ((Boolean) toObj).compareTo(new Boolean(((Integer) fromObj).intValue() != 0));
			} else if (String.class.isInstance(fromObj)) {
				test = ((Boolean) toObj).compareTo(new Boolean(!((String) fromObj).trim().equals("")));
			} else if (Boolean.class.isInstance(fromObj)) {
				test = ((Boolean) toObj).compareTo((Boolean) fromObj);
			}
			break;
		case Types.FLOAT:
		case Types.REAL:
			if (BigDecimal.class.isInstance(fromObj)) {
				test = ((Float) toObj).compareTo(new Float(((BigDecimal) fromObj).floatValue()));
			} else if (Float.class.isInstance(fromObj)) {
				test = ((Float) toObj).compareTo((Float) fromObj);
			} else if (Double.class.isInstance(fromObj)) {
				test = ((Float) toObj).compareTo(new Float(((Double) fromObj).floatValue()));
			} else {
				test = ((Float) toObj).compareTo(new Float(fromObj.toString()));
			}
			break;
		case Types.DOUBLE:
			if (BigDecimal.class.isInstance(fromObj)) {
				test = ((Double) toObj).compareTo(new Double(((BigDecimal) fromObj).doubleValue()));
			} else if (Float.class.isInstance(fromObj)) {
				test = ((Double) toObj).compareTo(new Double(((Float) fromObj).doubleValue()));
			} else if (Double.class.isInstance(fromObj)) {
				test = ((Double) toObj).compareTo((Double) fromObj);
			} else {
				test = ((Double) toObj).compareTo(new Double(fromObj.toString()));
			}
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			BigDecimal tmpToObj = (BigDecimal) toObj;
			BigDecimal tmpFromObj = null;

			if (BigDecimal.class.isInstance(fromObj)) {
//				test = ((BigDecimal) toObj).compareTo((BigDecimal) fromObj);
				tmpFromObj = (BigDecimal) fromObj;
			} else if (Double.class.isInstance(fromObj)) {
//				test = ((BigDecimal) toObj).compareTo(new BigDecimal(((Double) fromObj).doubleValue()));
				tmpFromObj = new BigDecimal(((Double) fromObj).doubleValue());
			} else if (Float.class.isInstance(fromObj)) {
//				test = ((BigDecimal) toObj).compareTo(new BigDecimal(((Float) fromObj).floatValue()));
				tmpFromObj = new BigDecimal(((Float) fromObj).floatValue());
			} else {
				// Lets turn it into a BigDecimal, using the string creator
//				test = ((BigDecimal) toObj).compareTo(new BigDecimal(fromObj.toString()));
				tmpFromObj = new BigDecimal(fromObj.toString());
			}
			// we need to make sure that we compare BigDecimals of the same scale and we want to round up any number if the scale
			// cannot be met
			test = tmpToObj.compareTo(tmpFromObj.setScale(tmpToObj.scale(),BigDecimal.ROUND_HALF_UP));
			break;
		case Types.DATE:
			test = new Cal(toObj).compareTo(new Cal(fromObj));
//			test = ((Date) toObj).compareTo(fromObj);
			break;
		case Types.TIME:
			test = ((Time) toObj).compareTo((Time)fromObj);
			break;
		case Types.TIMESTAMP:
			test = new Cal(toObj).compareTo(new Cal(fromObj));
//			test = ((Timestamp) toObj).compareTo(fromObj);
			break;
		default:
			test = toObj.toString().trim().compareToIgnoreCase(fromObj.toString().trim());
			break;
		}
		return test;
	}

	/**
	 * @return Returns the incrementalAppend.
	 */
	public int getIncrementalAppend() {
		return incrementalAppend;
	}

	/**
	 * @param incrementalAppend The incrementalAppend to set.
	 */
	public void setIncrementalAppend(int incrementalAppend) {
		this.incrementalAppend = incrementalAppend;
	}

	/**
	 * @return Returns the incrementalDelete.
	 */
	public int getIncrementalDelete() {
		return incrementalDelete;
	}

	/**
	 * @param incrementalDelete The incrementalDelete to set.
	 */
	public void setIncrementalDelete(int incrementalDelete) {
		this.incrementalDelete = incrementalDelete;
	}

	/**
	 * @return Returns the incrementalLog.
	 */
	public int getIncrementalLog() {
		return incrementalLog;
	}

	/**
	 * @param incrementalLog The incrementalLog to set.
	 */
	public void setIncrementalLog(int incrementalLog) {
		this.incrementalLog = incrementalLog;
	}

	/**
	 * @return Returns the incrementalUpdate.
	 */
	public int getIncrementalUpdate() {
		return incrementalUpdate;
	}

	/**
	 * @param incrementalUpdate The incrementalUpdate to set.
	 */
	public void setIncrementalUpdate(int incrementalUpdate) {
		this.incrementalUpdate = incrementalUpdate;
	}

	/**
	 * @return Returns the logAppends.
	 */
	public boolean isLogAppends() {
		return logAppends;
	}

	/**
	 * @param logAppends The logAppends to set.
	 */
	public void setLogAppends(boolean logAppends) {
		this.logAppends = logAppends;
	}

	/**
	 * @return Returns the logDeletes.
	 */
	public boolean isLogDeletes() {
		return logDeletes;
	}

	/**
	 * @param logDeletes The logDeletes to set.
	 */
	public void setLogDeletes(boolean logDeletes) {
		this.logDeletes = logDeletes;
	}

	/**
	 * @return Returns the logMatches.
	 */
	public boolean isLogMatches() {
		return logMatches;
	}

	/**
	 * @param logMatches The logMatches to set.
	 */
	public void setLogMatches(boolean logMatches) {
		this.logMatches = logMatches;
	}

	/**
	 * @return Returns the logUpdates.
	 */
	public boolean isLogUpdates() {
		return logUpdates;
	}

	/**
	 * @param logUpdates The logUpdates to set.
	 */
	public void setLogUpdates(boolean logUpdates) {
		this.logUpdates = logUpdates;
	}

	/**
	 * @return Returns the appendRowCount.
	 */
	public long getAppendRowCount() {
		return appendRowCount;
	}

	/**
	 * @return Returns the appendRowCountExecuted.
	 */
	public long getAppendRowCountExecuted() {
		return appendRowCountExecuted;
	}

	/**
	 * @return Returns the deleteRowCount.
	 */
	public long getDeleteRowCount() {
		return deleteRowCount;
	}

	/**
	 * @return Returns the deleteRowCountExecuted.
	 */
	public long getDeleteRowCountExecuted() {
		return deleteRowCountExecuted;
	}

	/**
	 * @return Returns the updateRowCount.
	 */
	public long getUpdateRowCount() {
		return updateRowCount;
	}

	/**
	 * @return Returns the updateRowCountExecuted.
	 */
	public long getUpdateRowCountExecuted() {
		return updateRowCountExecuted;
	}

	/**
	 * @return Returns the processedRows.
	 */
	public long getProcessedRows() {
		return processedRows;
	}

	/**
	 * @return Returns the fetchSize.
	 */
	public int getFetchSize() {
		return fetchSize;
	}

	/**
	 * @param fetchSize The fetchSize to set.
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public boolean isCommitAtEnd() {
		return commitAtEnd;
	}

	public void setCommitAtEnd(boolean commitAtEnd) {
		this.commitAtEnd = commitAtEnd;
	}

	public DBSyncLogger getDbSyncLogger() {
		if (dbSyncLogger == null) {
			// lets create one
			this.dbSyncLogger = new DBSyncLogger();
		}
		return dbSyncLogger;
	}

	public void setDbSyncLogger(DBSyncLogger dbSyncLogger) {
		this.dbSyncLogger = dbSyncLogger;
	}
	

	public boolean isTypeMatching() {
		return typeMatching;
	}

	public void setTypeMatching(boolean typeMatching) {
		this.typeMatching = typeMatching;
	}
}
