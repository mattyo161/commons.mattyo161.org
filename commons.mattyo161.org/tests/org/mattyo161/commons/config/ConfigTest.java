/*
 * Created on Feb 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.mattyo161.commons.cal.Cal;

import junit.framework.TestCase;

/**
 * @author mattyo1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConfigTest extends TestCase {
	public void testConfig() {
		Object[] configFiles = {
				getClass().getResource("ConfigTest.properties")
			};
		Config.addConfigManager("ConfigTest", configFiles);
		assertEquals(Arrays.asList(new String[] {"1","2","3","4","5"}), Config.getObject("ConfigTest","testvalue1"));
		assertEquals("this is a test",Config.get("ConfigTest","testvalue2"));
		// Make sure that config is working
		assertEquals("this is a test",Config.get("config.testvalue1"));
		assertEquals("1",Config.get("config.testvalue2"));
		assertEquals("1,2,3,4",Config.get("config.testvalue3"));
	}
}
