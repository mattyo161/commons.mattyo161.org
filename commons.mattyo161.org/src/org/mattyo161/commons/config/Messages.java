/*
 * Created on Feb 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.config;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author mattyo1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Messages {
	private static Messages singleton = null;
	
	// new store for configs called ConfigManager
	private Map messageGroups = new HashMap();
	private ConfigManager defaultGroup = null;
	
	private Messages() {
		// Initialize the default set of properties
		Object[] configPaths = {
				"~/etcommons-messages.properties", // user specific config properties
				"/usr/local/etc/etcommons-messages.properties", // host specific properties override
				"Messages.properties", // override of properties in classpath
				getClass().getResource("Messages.properties") // default properties
			};
		this.defaultGroup = new ConfigManager("etcommons-default", configPaths);
	}
	
	public static Messages getInstance() {
		if (singleton == null) {
			singleton = new Messages();
		}
		return singleton;
	}
	
	/**
	 * Get an Iterator to cycle through the properties for the given ConfigManager
	 * @param group
	 * @return
	 */
	public static Iterator getKeys(String group) {
		ConfigManager cg = getGroup(group);
		Iterator returnValue = null;
		if (cg != null) {
			returnValue = cg.getConfig().getKeys();
		}
		return returnValue;
	}
	
	/**
	 * Get an Iterator to cycle through the properties for the default ConfigManager
	 * @return
	 */
	public static Iterator getKeys() {
		ConfigManager cg = getGroup(null);
		Iterator returnValue = null;
		if (cg != null) {
			returnValue = cg.getConfig().getKeys();
		}
		return returnValue;
	}
	
	/**
	 * Find a config file, this should probably be depricated or modified to support
	 * ConfigManager file search
	 * @param property
	 * @return
	 */
	public static File getFile(String property) {
		File file = null;
		// lookup the property
		String fileString = get(property);
		if (!fileString.equals("")) {
			// lets see if this file exists if it does not then try and get a resource
			file = new File(fileString);
			if (!file.exists()) {
				// in order to get a resource for a class we need to have a class
				if (singleton == null) {
					getInstance();
				}
				// we only want to look for the name of the file as a resource, remove any path information
				URL url = singleton.getClass().getResource(file.getName());
				if (url != null) {
					file = new File(url.getFile());
					if (!file.exists()) {
						file = null;
					}
				}
			}
		}
		return file;
	}
	
	/**
	 * add a ConfigManager to Config for retrival
	 * @param group
	 * @param configFiles
	 */
	public static void addGroup(String group, Object[] configFiles) {
		getInstance().messageGroups.put(group, new ConfigManager(group, configFiles));
	}
	
	/**
	 * Get the passed ConfigManager
	 * @param group
	 */
	public static ConfigManager getGroup(String group) {
		if (group == null || group.equals("")) {
			return getInstance().defaultGroup;
		} else {
			return (ConfigManager) getInstance().messageGroups.get(group);
		}
	}
	
	/**
	 * Get the default ConfigManager
	 * @return
	 */
	public static ConfigManager getGroup() {
		return getInstance().defaultGroup;
	}
	
	/**
	 * Get an Object from the given group
	 * @param group
	 * @param property
	 * @return
	 */
	public static Object getObject(String group, String property) {
		ConfigManager cg = getGroup(group);
		Object returnValue = null;
		if (cg != null) {
			returnValue = cg.getConfig().getProperty(property);
		}
		return returnValue;
	}
	
	/**
	 * Get an Object from the default group
	 * @param property
	 * @return
	 */
	public static Object getObject(String property) {
		return getObject(null, property);
	}
	
	/**
	 * A very simple get a string from the given group
	 * @param group
	 * @param property
	 * @return
	 */
	public static String get(String group, String property) {
		ConfigManager cg = getGroup(group);
		String returnValue = null;
		if (cg != null) {
			returnValue = cg.getConfig().getString(property);
		}
		return returnValue;
	}
	
	/**
	 * A very simple get a string from the default configGroup, keep compatability
	 * with old code
	 * @param property
	 * @return
	 */
	public static String get(String property) {
		ConfigManager cg = getGroup();
		String returnValue = null;
		if (cg != null) {
			returnValue = cg.getConfig().getString(property);
		}
		return returnValue;
		
//		if (RESOURCE_BUNDLE != null) {
//			// at this point I don't want it to return errors so lets wrap it in a try
//			try {
//				return RESOURCE_BUNDLE.getString(property);
//			} catch (Exception e) {
//				return "";
//			}
//		}
//		// may want to do some other stuff here to possibly lookup other bundles for different classes
//		// or something or have global, class, application specific examples
//		return "";
	}
}
