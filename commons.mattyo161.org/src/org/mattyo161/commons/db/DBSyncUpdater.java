package org.mattyo161.commons.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mattyo161.commons.cal.Cal;
import org.mattyo161.commons.db.schema.ResultSetSchema;

/* Will take a collection of updates, keep track of the number of total updates performed
 * Generate Prepared statements based on what items are updated, add them to individual
 * batches and then execute all the batches
 */
public class DBSyncUpdater {
	DBSyncObject table = null;
	DBSync sync = null;
	ResultSetSchema rsSchema = null;
	Map<String, PreparedStatement> pStatements = null;
	Map<String, Integer> pStatementCounts = null;
	int updates = 0;
	
	public DBSyncUpdater(DBSync sync, DBSyncObject toTable, ResultSetSchema rsSchema) {
		// TODO Auto-generated constructor stub
		this.table = toTable;
		this.sync = sync;
		this.updates = 0;
		this.rsSchema = rsSchema;
		this.pStatements = new HashMap<>();
		this.pStatementCounts = new HashMap<>();
	}

	public void execute() throws SQLException {
		// Loop through all the prepared statements and execute ones that have entries and rest the entries
		for (Iterator<String> i = pStatementCounts.keySet().iterator(); i.hasNext(); ) {
			Cal sqlStartTime = new Cal();
			String updateName = i.next();
			int updateCount = pStatementCounts.get(updateName).intValue();
			if (updateCount > 0) {
				PreparedStatement ps = pStatements.get(updateName);
				int[] updateReturn = ps.executeBatch();
				if (this.table.getAutoCommit() && !this.sync.isCommitAtEnd()) {
					System.out.print(" Commiting changes ");
					this.table.commit();
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
				this.sync.getLog().incrementUpdates(successfulUpdates);
				this.sync.getLog().incrementFailedUpdates(failedUpdates);
				System.out.println(" (" + successfulUpdates + " Updates Succeeded " + failedUpdates + " failed of Total " + updateCount + " Updates) in " + new Cal().diff(sqlStartTime) + " ms.");
				// reset the count to 0
				pStatementCounts.put(updateName, new Integer(0));
			}
		}

		
	}
	
	
	public void addUpdate(Map<String, List<Object>> fields, ResultSet rs) throws SQLException {
		this.updates++;
		
		// Build a string for the fields, we will need to create a prepared statement for each field set
		String fieldNames = fields.keySet().toString().toLowerCase();
		// check to see if this prepared statement exists
		if (!this.pStatements.containsKey(fieldNames)) {
			addPreparedStatement(fields);
		}
		// double check to make sure that a statement exists
		if (this.pStatements.containsKey(fieldNames)) {
			PreparedStatement ps = this.pStatements.get(fieldNames);
			// Now lets build the prepared statement
			// first we need to go through the fields and apply the appropriate values
			int fieldPosition = 1;
			for (Iterator<String> i = fields.keySet().iterator(); i.hasNext(); ) {
				String fieldName = i.next();
				
				if (this.sync.isTypeMatching()) {
					ps.setObject(fieldPosition, DBSync.getSqlValue(rs, fieldName, this.rsSchema));
				} else {
					ps.setObject(fieldPosition, DBSync.getSqlValue(rs, fieldName));
				}
				fieldPosition++;
			}
			// now we need to add the where fields but we have to start at the end of the updateFieldCount
			for (int x = 0; x < this.table.getKeyFields().size(); x++) {
				if (this.sync.isTypeMatching()) {
					ps.setObject(fieldPosition, DBSync.getSqlValue(rs, this.table.getKeyFields().get(x), rsSchema));
				} else {
					ps.setObject(fieldPosition, DBSync.getSqlValue(rs, this.table.getKeyFields().get(x)));
				}
				fieldPosition++;
			}
			this.pStatementCounts.put(fieldNames, new Integer(this.pStatementCounts.get(fieldNames).intValue() + 1));
			ps.addBatch();
		}
	}

	public void addPreparedStatement(Map<String, List<Object>> fields) throws SQLException {
		String fieldNames = fields.keySet().toString().toLowerCase();
		if (fields != null && fields.keySet() != null && fields.keySet().size() > 0 && this.table.getKeyFields() != null && this.table.getKeyFields().size() > 0 && this.table.getName() != null && !this.table.getName().equals("")) {
			// if (getUpdateFields() != null && getUpdateFields().size() > 0 && getKeyFields() != null && getKeyFields().size() > 0 && getName() != null && !getName().equals("")) {
			// also do complete updates, because I don't really now how else to do it then to use preparedStatements
			StringBuffer updateSql = new StringBuffer().append("update ").append(this.table.getName()).append(" set ");
			int fieldCount = 0;
			for (Iterator<String> i = fields.keySet().iterator(); i.hasNext(); ) {
				String fieldName = i.next();
				if (fieldCount > 0) {
					updateSql.append(", ");
				}
				updateSql.append(fieldName).append(" = ?");
				fieldCount++;
			}
			updateSql.append(" where ");
			for (int i = 0; i < this.table.getKeyFields().size(); i++) {
				if (i > 0) {
					updateSql.append(" and ");
				}
				updateSql.append((String) this.table.getKeyFields().get(i)).append(" = ?");
			}
			System.out.println("Preparing statement \"" + fieldNames + "\" = '" + updateSql.toString() + "'");
			PreparedStatement ps = this.table.getConnection().prepareStatement(updateSql.toString());
			if (this.table.getUpdateTimeout() > 0) {
				ps.setQueryTimeout(this.table.getUpdateTimeout());
			}
			this.pStatements.put(fieldNames, ps);
			this.pStatementCounts.put(fieldNames, new Integer(0));
		} else if (fields != null && fields.keySet() != null && fields.keySet().size() == 0 && this.table.getKeyFields() != null && this.table.getKeyFields().size() > 0 && this.table.getName() != null && !this.table.getName().equals("")) {
			System.out.println("WARNING: Update Fields are empty, could not generate UpdateSql");
		} else {
			throw new SQLException("Failed to generate UpdateSql: UpdateFields, KeyFields or Name property are empty or null.");
		}
	}

	
	public int getUpdates() {
		return this.updates;
	}

	public void close() {
		// Go through and close all the prepared statements and remove them from the updater in case it gets reused later
		for (Iterator<String> i = pStatementCounts.keySet().iterator(); i.hasNext(); ) {
			String updateName = i.next();
			PreparedStatement ps = pStatements.get(updateName);
			DBConnection.closeSQLObject(ps);
		}
		// now lets reset all the values
		this.updates = 0;
		this.pStatements = new HashMap<>();
		this.pStatementCounts = new HashMap<>();
	}


}
