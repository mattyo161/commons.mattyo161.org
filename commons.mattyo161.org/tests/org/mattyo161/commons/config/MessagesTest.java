/*
 * Created on Feb 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.config;

import java.util.Iterator;

import org.mattyo161.commons.cal.Cal;

import junit.framework.TestCase;

/**
 * @author mattyo1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MessagesTest extends TestCase {
	public void testConfig() {
		// Make sure that config is working
		assertEquals("this is a test",Messages.get("messages.testvalue1"));
		assertEquals("1",Messages.get("messages.testvalue2"));
		assertEquals("1,2,3,4",Messages.get("messages.testvalue3"));
		System.out.println("AdCentral Version: " + Messages.get("adcentral.version"));
		System.out.println("AdCentral Version: " + Messages.get("adcentral.version"));
		
		System.out.println("Classpath = '" + System.getProperty("java.class.path"));
		
		// List all of the messages
		System.out.println(Messages.getGroup().getDumpData());
		
		System.out.println("Messsages as Map:");
		System.out.println(Messages.getGroup().getAsMap());
		
		assertNull(Messages.get("config.testvalue1"));
		assertNull(Messages.get("config.testvalue2"));
		assertNull(Messages.get("config.testvalue3"));
		
	}
}
