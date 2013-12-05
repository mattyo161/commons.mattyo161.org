package org.mattyo161.commons.db;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.mattyo161.commons.cal.Cal;

public class DBSyncLogger {
	private List<DBSyncLog> logs = null;
	long totalAppends = 0;
	long totalDeletes = 0;
	long totalErrors = 0;
	long totalProcessed = 0;
	long totalUpdates = 0;
	Cal startTime = null;
	Cal endTime = null;
	
	public DBSyncLogger() {
		this.logs = new Vector<DBSyncLog>();
		this.startTime = new Cal();
	}
	
	public void add(DBSyncLog log) {
		this.logs.add(log);
	}
	
	public DBSyncLog get(String name) {
		for (Iterator<DBSyncLog> i = this.logs.iterator(); i.hasNext(); ) {
			DBSyncLog log = i.next();
			if (log.getName().equalsIgnoreCase(name)) { 
				return log;
			}
		}
		return null;
	}
	
	public List<DBSyncLog> getLogs() {
		return this.logs;
	}
	
	public void done() {
		this.endTime = new Cal();
		// Total up all the appends, errors, etc.
		this.totalAppends = 0;
		this.totalDeletes = 0;
		this.totalErrors = 0;
		this.totalProcessed = 0;
		this.totalUpdates = 0;
		for (Iterator<DBSyncLog> i = this.logs.iterator(); i.hasNext(); ) {
			DBSyncLog log = i.next();
			this.totalAppends += log.getAppends();
			this.totalDeletes += log.getDeletes();
			this.totalErrors += log.getErrors().size();
			this.totalProcessed += log.getProcessed();
			this.totalUpdates += log.getUpdates();
		}
	}

	public long getTotalAppends() {
		return totalAppends;
	}

	public long getTotalDeletes() {
		return totalDeletes;
	}

	public long getTotalErrors() {
		return totalErrors;
	}

	public long getTotalProcessed() {
		return totalProcessed;
	}

	public long getTotalUpdates() {
		return totalUpdates;
	}

	public Cal getStartTime() {
		return startTime;
	}

	public Cal getEndTime() {
		return endTime;
	}
}
