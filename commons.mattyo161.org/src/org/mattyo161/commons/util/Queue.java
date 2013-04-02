package org.mattyo161.commons.util;

import java.util.LinkedList;

public class Queue {
	private LinkedList storage;
	
	public Queue() {
		storage = new LinkedList();
	}
	
	public synchronized int size() {
		return storage.size();
	}
	public synchronized Object get(int timeout) {
		while (true) {
			if (storage.size() > 0) {
				Object popped = storage.removeFirst();
				return popped;
			} else {
				try {
					wait();
				} catch (InterruptedException e) {}
			}
		}
	}
	
	public synchronized Object get() {
		return get(0);
	}
	public synchronized void put(Object obj) {
		storage.addLast(obj);
		notifyAll();
	}
}
