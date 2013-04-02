package org.mattyo161.commons.db;

/*
 * as400test.java
 *
 * Created on April 13, 2004, 12:21 PM
 */
import java.sql.*;
import java.sql.Date;

import org.mattyo161.commons.cal.Cal;
import org.mattyo161.commons.db.DBConnection;
import org.mattyo161.commons.db.schema.Column;
import org.mattyo161.commons.db.schema.Index;
import org.mattyo161.commons.db.schema.IndexColumn;
import org.mattyo161.commons.db.schema.Indexes;

import java.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.*;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author mattyo1
 */
public class TableSync {
	String classFile = null;

	String classDir = null;

	Properties props = new Properties();

	/** Creates a new instance of as400test */
	public TableSync() {
		this.classFile = getClass().getResource(getClass().getName() + ".class").getFile();
		this.classDir = new File(this.classFile).getParent();
		if (this.classDir.endsWith(".jar!")) {
			String tmp = new File(this.classDir).getParent().replaceAll("^\\w+:", "");
		}
		try {
			this.props.load(getClass().getResourceAsStream(getClass().getName() + ".properties"));
		} catch (IOException e) { /* failed */
		} catch (Exception e) { /* failed */
		}
	}

	/**
	 * get a list of fields that make up the primary key
	 * 
	 * @param conn
	 * @param tableName
	 * @return
	 */
	public static ArrayList getPrimaryKey(Connection conn, String tableName) {
		ArrayList keys = new ArrayList();
		try {
			Indexes indexes = new DBConnection(conn).getIndexes(tableName);
			Index uniqueIndex = indexes.getUniqueIndex();
			if (uniqueIndex != null) {
				List columns = uniqueIndex.getColumns();
				for (Iterator i = columns.iterator(); i.hasNext();) {
					IndexColumn currColumn = (IndexColumn) i.next();
					keys.add(currColumn.getColumnName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return keys;
	}
	
	public static void synchronizeTable(TableSyncObject fromTable, TableSyncObject toTable) {
		synchronizeTable(fromTable, toTable, false, false);
	}
	
	public static void synchronizeTable(TableSyncObject fromTable, TableSyncObject toTable, boolean doDeletes) {
		synchronizeTable(fromTable, toTable, doDeletes, false);
	
	}
	
	public static void synchronizeTable(TableSyncObject fromTable, TableSyncObject toTable, boolean doDeletes, boolean doUpdates) {
		// TODO Auto-generated method stub
		// the key fields are used for sorting records so they are in the same
		// order in both databases
		String fromKeyFields = "";
		String toKeyFields = "";
		
		// Let's create some working variables to make the code a little easier
		// to read
		String fromTableName = fromTable.getTableSchema().getName();
		String toTableName = toTable.getTableSchema().getName();

		String tableKey = "";
		String currField = "";
		try {
			// build a list of the types of fields that the to table requires
			Map toFieldSqlTypes = new TreeMap();
			Map toFieldTypes = new TreeMap();
			for (Iterator i = toTable.getSyncFields().iterator(); i.hasNext();) {
				String currColStr = (String) i.next();
				// lookup this column in the TableSchema
				Column currCol = toTable.getTableSchema().getColumn(currColStr);
				toFieldSqlTypes.put(currColStr.toLowerCase(), new Integer(currCol.getSqlType()));
				toFieldTypes.put(currColStr.toLowerCase(), currCol.getDbType());
			}

			System.out.println("Synching table '" + fromTable.getTableSchema().getSchema() + "." + fromTable.getTableSchema().getCatalog() + "." + fromTableName + "' " +
					"to '" + toTable.getTableSchema().getSchema() + "." + toTable.getTableSchema().getCatalog() + "." + toTableName + "'");
			fromKeyFields = StringUtils.join(fromTable.getKeyFields().iterator(), ", ");
			toKeyFields = StringUtils.join(toTable.getKeyFields().iterator(), ", ");

			// Configure the To table statements, this is currently setup for
			String sqlCmdTo = "select * from " + toTableName;
			if (toKeyFields != "") {
				sqlCmdTo += " order by " + toKeyFields;
			}

			PreparedStatement psToTable = toTable.getConnection().prepareStatement(sqlCmdTo, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			psToTable.setFetchDirection(ResultSet.FETCH_FORWARD);

			// Configure the From table statements
			String sqlCmdFrom = "select * from " + fromTableName;
			if (fromKeyFields != "") {
				sqlCmdFrom += " order by " + fromKeyFields;
			}
			PreparedStatement psFromTable = fromTable.getConnection().prepareStatement(sqlCmdFrom, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			psToTable.setFetchDirection(ResultSet.FETCH_FORWARD);

			// Create batch prepared statments for deleting rows, updating and
			// inserting rows
			String deleteSql = "delete from " + toTableName + " where " + StringUtils.join(toTable.getKeyFields().iterator(), " = ? and ") + " = ?";

			// Use complete inserts
			StringBuffer appendSql = new StringBuffer().append("insert into ").append(toTableName);
			appendSql.append(" (" + StringUtils.join(toTable.getSyncFields().iterator(), ", ") + ")");
			appendSql.append(" values (");
			for (int i = 0; i < toTable.getSyncFields().size(); i++) {
				if (i > 0) {
					appendSql.append(", ?");
				} else {
					appendSql.append("?");
				}
			}
			appendSql.append(")");
			
			// also do complete updates, because I don't really now how else to do it then to use preparedStatements
			StringBuffer updateSql = new StringBuffer().append("update ").append(toTableName).append(" set ");
			for (int i = 0; i < toTable.getSyncFields().size(); i++) {
				if (i > 0) {
					updateSql.append(", ");
				}
				updateSql.append((String) toTable.getSyncFields().get(i)).append(" = ?");
			}
			updateSql.append(" where ");
			for (int i = 0; i < toTable.getKeyFields().size(); i++) {
				if (i > 0) {
					updateSql.append(" and ");
				}
				updateSql.append((String) toTable.getKeyFields().get(i)).append(" = ?");
			}

			PreparedStatement psToTableDelete = toTable.getConnection().prepareStatement(deleteSql);
			PreparedStatement psToTableAppend = toTable.getConnection().prepareStatement(appendSql.toString());
			PreparedStatement psToTableUpdate = toTable.getConnection().prepareStatement(updateSql.toString());
			
			// setup variables for keeping track of where we are in the synch
			// process
			long appendRowCount = 0;
			long deleteRowCount = 0;
			long deleteRowCountExecuted = 0;
			long updateRowCount = 0;

			ResultSet rsToTable = psToTable.executeQuery();
			ResultSet rsFromTable = psFromTable.executeQuery();

			// get the next rows, make sure to keep track of the returnValues as
			// these are used to determine
			// if an append, delete or update is needed.
			boolean rsToTableNext = rsToTable.next();
			boolean rsFromTableNext = rsFromTable.next();
			int currRowNum = 0;
			while (rsToTableNext || rsFromTableNext) {

				if (++currRowNum % 500 == 0) {
					System.out.println("Processed " + currRowNum + " rows.");
				}

				if (!rsToTableNext) {
					// then we have reached the end of the to records and we
					// must append
					appendRowCount++;
					// use temp arraylist to hold append key values, this can be commented out if it is not needed.
					ArrayList temp = new ArrayList();
					for (int j = 0; j < fromTable.getKeyFields().size(); j++) {
						Object fromObj = rsFromTable.getObject((String) fromTable.getSyncFields().get(j));
						temp.add(fromObj);
					}
					System.out.println("\t" + toTableName + " Appending Row " + temp.toString());
					for (int j = 0; j < fromTable.getSyncFields().size(); j++) {
						try {
							Object fromObj = rsFromTable.getObject((String) fromTable.getSyncFields().get(j));
							psToTableAppend.setObject(j + 1, fromObj);
						} catch (Exception e) {
							// Debugging stuff
							System.out.println(e);
							// System.out.println("KeyFields: " + keyFields);
							System.out.println(tableKey);
							System.out.println("Setting field " + currField + " to \"\"");
							psToTableAppend.setString(j, "");
						}
					}
					psToTableAppend.addBatch();
					rsFromTableNext = rsFromTable.next();
				} else if (!rsFromTableNext) {
					// we have run out of rows in the From table so the rest
					// is extra and should be deleted
					if (doDeletes) {
						ArrayList temp = new ArrayList();
						// psToTableDelete.clearParameters();
						for (int j = 0; j < toTable.getKeyFields().size(); j++) {
							Object toObj = rsToTable.getObject((String) toTable.getKeyFields().get(j));
							psToTableDelete.setObject(j + 1, toObj);
							temp.add(toObj);
						}
						psToTableDelete.addBatch();
						// deleteRows.add(temp);
						deleteRowCount++;
						System.out.println("\t" + toTableName + " Deleting Row " + temp.toString());
					}
					rsToTableNext = rsToTable.next();
					if (!rsToTableNext && psToTable.getMaxRows() > 0) {
						if (psToTable.getMoreResults()) {
							rsToTable = psToTable.getResultSet();
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
						String toColName = (String) toTable.getKeyFields().get(j);
						String fromColName = (String) fromTable.getKeyFields().get(j);
						Object toObj = rsToTable.getObject(toColName);
						Object fromObj = rsFromTable.getObject(fromColName);
						int test = -1;
						Integer colType = (Integer) toFieldSqlTypes.get(toColName.toLowerCase());
						test = compareSqlObject(colType.intValue(), toObj, fromObj);

						
						temp.add(fromObj.toString());
						if (test != 0) {
							match = test;
							break;
						}
					}
					tableKey = fromTableName + ":" + StringUtils.join(temp.iterator(), ", ");
					if (match == 0) {
						if (doUpdates) {
							// keep track of the current field we are on incase we throw an exception
							String currToField = "";
							try {
								boolean recordsMatch = true;
								boolean test = false;
								// Create a HashMap to store the changed valued
								HashMap recordChanges = new HashMap();
								List updateFields = new Vector();
								List updateValues = new Vector();
								for (int k = 0; k < toTable.getSyncFields().size(); k++) {
									Object toObj = rsToTable.getObject((String) toTable.getSyncFields().get(k));
									Object fromObj = rsFromTable.getObject((String) fromTable.getSyncFields().get(k));
									currToField = (String) toTable.getSyncFields().get(k);
	
									Integer colType = (Integer) toFieldSqlTypes.get(currToField.toLowerCase());
									test = equalsSqlObject(colType.intValue(), toObj, fromObj);

	
									if (!test) {
										recordsMatch = false;
										// store the changes in an array so they can
										// be logged
										ArrayList changes = new ArrayList();
										changes.add(toObj);
										changes.add(fromObj);
	
										// build the update string, we will use ? so
										// that we can use a preparedStatment to
										// enter all the values
										StringBuffer updateString = new StringBuffer(toTable.getSyncFields().get(k) + " = ");
										test = equalsSqlObject(colType.intValue(), toObj, fromObj);
										
										// log the change to the field for debugging
										recordChanges.put(currToField, changes);
										// update the update Lists, that will be
										// used to generate the update SQL
										updateFields.add(updateString);
										updateValues.add(fromObj);
									}
								}
								if (recordsMatch) {
//									System.out.println("\t" + toTableName + " Records match keys " + temp.toString());
								} else {
									// lets update or commit the changes
//									String updateSQL = "update " + toTableName + " set " + StringUtils.join(updateFields.iterator(), ", ") + " where "
//											+ updateWhereClause;
//									temp = new ArrayList();
//									for (int k = 0; k < toTable.getKeyFields().size(); k++) {
//										temp.add(rsToTable.getObject((String) toTable.getKeyFields().get(k)));
//									}
	
									// first we need to update the new values from the fromTable
									for (int x = 0; x < fromTable.getSyncFields().size(); x++) {
										psToTableUpdate.setObject(x + 1, rsFromTable.getObject((String) fromTable.getSyncFields().get(x)));
									}
									// now we need to add the where fields but we have to start at the end of the synchFieldCount
									int syncFieldCount = fromTable.getSyncFields().size();
									for (int x = 0; x < toTable.getKeyFields().size(); x++) {
										psToTableUpdate.setObject(syncFieldCount + x + 1, rsFromTable.getObject((String) toTable.getSyncFields().get(x)));
									}
									
									updateRowCount++;
									try {
//										sToTableUpdate.addBatch(updateSQL);
										psToTableUpdate.addBatch();
									} catch (Exception e) {
										System.out.println("UPDATE Error: " + e.toString());
									}
	
									System.out.println("\t" + toTableName + " Records match keys " + temp.toString() + " Values Differ");
									for (Iterator k = recordChanges.keySet().iterator(); k.hasNext();) {
										String key = (String) k.next();
										List currObject = (List) recordChanges.get(key);
										System.out.println("\t" + key + "\t->\tFROM:'" + currObject.get(0) + "'\tTO:'" + currObject.get(1) + "'");
									}
								}
							} catch (Exception e) {
	
								System.out.println(tableKey);
	//							System.out.println("KeyFields: " + keyFields);
								System.out.println("AtField: " + currToField);
								e.printStackTrace();
							}
						}
						rsToTableNext = rsToTable.next();
						if (!rsToTableNext && psToTable.getMaxRows() > 0) {
							if (psToTable.getMoreResults()) {
								rsToTable = psToTable.getResultSet();
								rsToTableNext = rsToTable.next();
							}
						}
						rsFromTableNext = rsFromTable.next();
					} else if (match < 0) {
						// we need to delete the row
						if (doDeletes) {
							ArrayList deleteTemp = new ArrayList();
							// psToTableDelete.clearParameters();
							for (int j = 0; j < toTable.getKeyFields().size(); j++) {
								psToTableDelete.setObject(j + 1, rsToTable.getObject((String) toTable.getKeyFields().get(j)));
								deleteTemp.add(rsToTable.getObject((String) toTable.getKeyFields().get(j)));
							}
							psToTableDelete.addBatch();
							// deleteRows.add(temp);
							deleteRowCount++;
							System.out.println("\t" + toTableName + " Deleting Row " + deleteTemp.toString());
						}
						rsToTableNext = rsToTable.next();
					} else if (match > 0) {
						// we need to append this row
						appendRowCount++;
						// use temp arraylist to hold append key values, this can be commented out if it is not needed.
						ArrayList appendTemp = new ArrayList();
						for (int j = 0; j < fromTable.getKeyFields().size(); j++) {
							Object fromObj = rsFromTable.getObject((String) fromTable.getSyncFields().get(j));
							appendTemp.add(fromObj);
						}
						System.out.println("\t" + toTableName + " Appending Row " + appendTemp.toString());
						for (int j = 0; j < fromTable.getSyncFields().size(); j++) {
							currField = (String) fromTable.getSyncFields().get(j);
							try {
								psToTableAppend.setObject(j + 1, rsFromTable.getObject(currField));
							} catch (Exception e) {
								// Debugging stuff
								System.out.println(e);
								// System.out.println("KeyFields: " +
								// keyFields);
								System.out.println(tableKey);
								System.out.println("Setting field " + currField + " to \"\"");
								psToTableAppend.setString(j, "");
							}
						}
						psToTableAppend.addBatch();
						rsFromTableNext = rsFromTable.next();
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

				// make sure to execute deletes before appends, so we don't
				// have conflicts
				if (deleteRowCount % 500 == 0 && deleteRowCount > 0 && !rsToTableNext) {
					// We need to execute the batch
					System.out.println(deleteRowCount + " deletes have accumulated, executing delete now.");
					try {
						int[] deleteReturn = psToTableDelete.executeBatch();
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
						System.out.println(successfulUpdates + " Deletes Sucseeded " + failedUpdates + " failed.");
					} catch (Exception e) {
						System.out.println("Failure to delete records.");
						e.printStackTrace();
					}
					psToTableDelete.clearParameters();
					deleteRowCountExecuted = deleteRowCount;
				}
				if (updateRowCount % 500 == 0 && updateRowCount > 0 && !rsToTableNext) {
					// We need to execute the batch
					System.out.println(updateRowCount + " updates have accumulated, executing update now.");
					try {
						int[] updateReturn = psToTableUpdate.executeBatch();
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
						System.out.println(successfulUpdates + " Updates Succeeded " + failedUpdates + " failed.");
					} catch (Exception e) {
						System.out.println("Failure to update records.");
						e.printStackTrace();
					}
					psToTableUpdate.clearParameters();
				}
				if (appendRowCount % 500 == 0 && appendRowCount > 0 && !rsToTableNext) {
					// make sure we execute any deletes that need to be
					// performed
					if (deleteRowCount > deleteRowCountExecuted) {
						// We need to execute the batch
						System.out.println(deleteRowCount + " deletes have accumulated, executing delete before append.");
						try {
							int[] deleteReturn = psToTableDelete.executeBatch();
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
							System.out.println(successfulUpdates + " Deletes Sucseeded " + failedUpdates + " failed.");
						} catch (Exception e) {
							System.out.println("Failure to delete records.");
							e.printStackTrace();
						}
						psToTableDelete.clearParameters();
						deleteRowCountExecuted = deleteRowCount;
					}
					// We need to execute the batch
					System.out.println(appendRowCount + " appends have accumulated, executing insert now.");
					try {
						int[] appendReturn = psToTableAppend.executeBatch();
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
						System.out.println(successfulUpdates + " Appends Sucseeded " + failedUpdates + " failed.");
					} catch (Exception e) {
						System.out.println("Failure to append records.");
						e.printStackTrace();
					}
					psToTableAppend.clearParameters();
				}
			}
			rsToTable.close();
			rsFromTable.close();

			psToTable.close();
			psFromTable.close();
			System.out.println("Processed " + currRowNum + " rows.");
			
			if (updateRowCount > 0) {
				System.out.println(updateRowCount + " updates have accumulated, executing update now.");
				try {
					int[] updateReturn = psToTableUpdate.executeBatch();
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
					System.out.println(successfulUpdates + " Updates Succeeded " + failedUpdates + " failed.");
				} catch (Exception e) {
					System.out.println("Failure to update records.");
					e.printStackTrace();
				}
			}
			psToTableUpdate.close();
			if (deleteRowCount > 0) {
				System.out.println(deleteRowCount + " deletes have accumulated, executing delete now.");
				try {
					int[] deleteReturn = psToTableDelete.executeBatch();
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
					System.out.println(successfulUpdates + " Deletes Sucseeded " + failedUpdates + " failed.");
				} catch (Exception e) {
					System.out.println("Failure to delete records.");
					e.printStackTrace();
				}
			}
			psToTableDelete.close();
			if (appendRowCount > 0) {
				System.out.println(appendRowCount + " appends have accumulated, executing insert now.");
				try {
					int[] appendReturn = psToTableAppend.executeBatch();
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
					System.out.println(successfulUpdates + " Appends Sucseeded " + failedUpdates + " failed.");
				} catch (Exception e) {
					System.out.println("Failure to append records.");
					e.printStackTrace();
				}
			}
			psToTableAppend.close();
		} catch (Exception e) {
			System.out.println("Error in may try clause:");
			e.printStackTrace();
		}

	}

	private static boolean equalsSqlObject(int matchType, Object toObj, Object fromObj) {
		// TODO Auto-generated method stub
		boolean test = false;
		if (toObj == null && fromObj == null) {
			return true;
		} else if ((toObj == null && fromObj != null) || (toObj != null && fromObj == null)) {
			return false;
		}
		switch (matchType) {
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			// remove trailing spaces from string
			// comparisons
			test = ((String) toObj).replaceAll("\\s+$", "").equalsIgnoreCase(((String) fromObj).replaceAll(" +$", ""));
			break;
		case Types.BIGINT:
			test = ((BigInteger) toObj).equals(fromObj);
			break;
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			test = ((Integer) toObj).equals(fromObj);
			break;
		case Types.FLOAT:
		case Types.REAL:
			test = ((Float) toObj).equals(fromObj);
			break;
		case Types.DOUBLE:
			if (fromObj.getClass() == Float.class) {
				test = ((Double) toObj).equals(new Double( ((Float) fromObj).doubleValue()) ) ;
			} else {
				test = ((Double) toObj).equals(fromObj);
			}
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			test = ((BigDecimal) toObj).equals(fromObj);
			break;
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			test = new Cal(toObj).equals(new Cal(fromObj));
			break;
//		case Types.DATE:
//			test = ((Date) toObj).equals(fromObj);
//			break;
//		case Types.TIME:
//			test = ((Time) toObj).equals(fromObj);
//			break;
//		case Types.TIMESTAMP:
//			test = ((Timestamp) toObj).equals(fromObj);
//			break;
		case Types.CLOB:
			try {
				byte[] buffer = new byte[256];
				InputStream contents = ((Clob) toObj).getAsciiStream();
				test = true;
				int count = 0;
				while (contents.read(buffer) > 0 && test) {
					String fromString = new String(buffer);
					String toString = null;
					if (fromObj.toString().length() < (count * 256)) {
						test = false;
					} else if (fromObj.toString().length() < ((count + 1) * 256)) {
						toString = fromObj.toString().substring(count * 256, fromObj.toString().length());
					} else {
						toString = fromObj.toString().substring(count * 256, 256 * (count + 1));
					}
					test = fromString.trim().equals(toString.trim());
					count++;
					// need to clear out the buffer
					buffer = new byte[256];
				}
			} catch (Exception e) {
				e.printStackTrace();
				test = false;
			}
			break;
//		case Types.BLOB:
//			try {
//				test = ((Blob) toObj).getBinaryStream().equals(fromObj);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			break;
		default:
			test = toObj.toString().trim().equalsIgnoreCase(fromObj.toString().trim());
			break;
		}
		return test;
	}

	private static int compareSqlObject(int matchType, Object toObj, Object fromObj) {
		// TODO Auto-generated method stub
		int test = 0;
		switch (matchType) {
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			// remove trailing spaces from string
			// comparisons
			test = ((String) toObj).replaceAll("\\s+$", "").compareToIgnoreCase(((String) fromObj).replaceAll(" +$", ""));
			break;
		case Types.BIGINT:
			test = ((BigInteger) toObj).compareTo((BigInteger)fromObj);
			break;
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			test = ((Integer) toObj).compareTo((Integer)fromObj);
			break;
		case Types.FLOAT:
			test = ((Float) toObj).compareTo((Float)fromObj);
			break;
		case Types.REAL:
		case Types.DOUBLE:
			test = ((Double) toObj).compareTo((Double)fromObj);
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			test = ((BigDecimal) toObj).compareTo((BigDecimal)fromObj);
			break;
		case Types.DATE:
			test = ((Date) toObj).compareTo((Date)fromObj);
			break;
		case Types.TIME:
			test = ((Time) toObj).compareTo((Time)fromObj);
			break;
		case Types.TIMESTAMP:
			test = ((Timestamp) toObj).compareTo((Timestamp)fromObj);
			break;
		default:
			test = toObj.toString().trim().compareToIgnoreCase(fromObj.toString().trim());
			break;
		}
		return test;
	}
}
