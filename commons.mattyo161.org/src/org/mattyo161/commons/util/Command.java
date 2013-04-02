package org.mattyo161.commons.util;

public interface Command extends PersistentQueueItem {
	public void run();
	
}
