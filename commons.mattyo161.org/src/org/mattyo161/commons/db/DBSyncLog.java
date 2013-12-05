package org.mattyo161.commons.db;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

import org.mattyo161.commons.cal.Cal;

/**
 * This is a simple PoJo that keeps track of the counts for a given sync process
 * @author matt
 *
 */
public class DBSyncLog {
	
	private String name = "";
	private long processed = 0;
	private long appends = 0;
	private long updates = 0;
	private long deletes = 0;
	private long failedAppends = 0;
	private long failedUpdates = 0;
	private long failedDeletes = 0;
	private Cal startTime = null;
	private Cal endTime = null;
	private long appendTime = 0;
	private long updateTime = 0;
	private long deleteTime = 0;
	private Cal startAppend = null;
	private Cal startDelete = null;
	private Cal startUpdate = null;
	private List<String> errors = null;
	
	public DBSyncLog(String name) {
		this.name = name;
		this.startTime = new Cal();
		this.errors = new Vector<String>();
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		DecimalFormat sFormat = new DecimalFormat("#,##0");
		DecimalFormat msFormat = new DecimalFormat("#,##0.000");
		buff.append(this.name).append(" ").append(sFormat.format(this.processed)).append(" rows in ")
			.append(msFormat.format(this.endTime.diff(this.startTime)/1000.0)).append(" secs")
			;
		if (this.getErrors().size() > 0) {
			buff.append(" with ").append(sFormat.format(this.getErrors().size())).append(" ERRORS");
		}
		if (this.appends > 0) {
			buff.append("; ").append(sFormat.format(this.appends));
			if (this.failedAppends > 0) {
				buff.append(" (").append(sFormat.format(this.failedAppends)).append(" failed)");
			}
			buff.append(" appends in ").append(sFormat.format(this.appendTime)).append(" ms");
		}
		if (this.deletes > 0) {
			buff.append("; ").append(sFormat.format(this.deletes));
			if (this.failedDeletes > 0) {
				buff.append(" (").append(sFormat.format(this.failedDeletes)).append(" failed)");
			}
			buff.append(" deletes in ").append(sFormat.format(this.deleteTime)).append(" ms");
		}
		
		if (this.updates > 0) {
			buff.append("; ").append(sFormat.format(this.updates));
			if (this.failedUpdates > 0) {
				buff.append(" (").append(sFormat.format(this.failedUpdates)).append(" failed)");
			}
			buff.append(" updates in ").append(sFormat.format(this.updateTime)).append(" ms");
		}
		
		return buff.toString();
	}
	
	public void addError(Exception e) {
		addError(null, e);
	}
	
	public void addError(String message, Exception e) {
		StringBuffer buff = new StringBuffer();
		if (message != null) {
			buff.append(message).append(":\n");
		}
		buff.append(e.getClass().toString()).append(" ").append(e.getMessage()).append("\n");
		StackTraceElement[] trace = e.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			buff.append("\tat ").append(trace[i].toString()).append("\n");
		}
		if (SQLException.class.isInstance(e)) {
			SQLException nextException = ((SQLException) e).getNextException();
			while (nextException != null) {
				buff.append(nextException.getClass().toString()).append(" ").append(nextException.getMessage()).append("\n");
				trace = nextException.getStackTrace();
				for (int i = 0; i < trace.length; i++) {
					buff.append("\tat ").append(trace[i].toString()).append("\n");
				}
				nextException = nextException.getNextException();
			}
		}
		this.errors.add(buff.toString());
	}
	
	public void incrementAppends() {
		this.appends++;
	}
	
	public void incrementAppends(int count) {
		this.appends += count;
	}
	
	public void incrementDeletes() {
		this.deletes++;
	}
	
	public void incrementDeletes(int count) {
		this.deletes += count;
	}
	
	public void incrementUpdates() {
		this.updates++;
	}
	
	public void incrementUpdates(int count) {
		this.updates += count;
	}
	
	public void incrementFailedAppends() {
		this.failedAppends++;
	}
	
	public void incrementFailedAppends(int count) {
		this.failedAppends += count;
	}
	
	public void incrementFailedDeletes() {
		this.failedDeletes++;
	}
	
	public void incrementFailedDeletes(int count) {
		this.failedDeletes += count;
	}
	
	public void incrementFailedUpdates() {
		this.failedUpdates++;
	}
	
	public void incrementFailedUpdates(int count) {
		this.failedUpdates += count;
	}
	
	public void done() {
		this.endTime = new Cal();
	}
	
	public void startAppend() {
		this.startAppend = new Cal();
	}
	
	public void doneAppend() {
		this.appendTime += new Cal().diff(this.startAppend);
	}
	
	public void startDelete() {
		this.startDelete = new Cal();
	}
	
	public void doneDelete() {
		this.deleteTime += new Cal().diff(this.startDelete);
	}
	
	public void startUpdate() {
		this.startUpdate = new Cal();
	}
	
	public void doneUpdate() {
		this.updateTime += new Cal().diff(this.startUpdate);
	}

	public Cal getStartTime() {
		return startTime;
	}

	public void setStartTime(Cal startTime) {
		this.startTime = startTime;
	}

	public String getName() {
		return name;
	}

	public long getProcessed() {
		return processed;
	}

	public long getAppends() {
		return appends;
	}

	public long getUpdates() {
		return updates;
	}

	public long getDeletes() {
		return deletes;
	}

	public Cal getEndTime() {
		return endTime;
	}

	public long getAppendTime() {
		return appendTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public long getDeleteTime() {
		return deleteTime;
	}

	public long getFailedAppends() {
		return failedAppends;
	}

	public long getFailedUpdates() {
		return failedUpdates;
	}

	public long getFailedDeletes() {
		return failedDeletes;
	}

	public void incrementProcessed() {
		this.processed++;
	}
	
	public List getErrors() {
		return this.errors;
	}
}
