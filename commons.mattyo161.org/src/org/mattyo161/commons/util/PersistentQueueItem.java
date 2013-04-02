package org.mattyo161.commons.util;

import java.io.Serializable;

public interface PersistentQueueItem extends Serializable {
	public String getQueueUID();
	public void setQueueUID(String id);
}
