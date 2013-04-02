/*
 * Created on Feb 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.config;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.lang.StringUtils;



/**
 * @author mattyo1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Config {
//	private static final String BUNDLE_NAME = "org.mattyo161.commons.config.Config";
//	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	private static Config singleton = null;
	
	// new store for configs called ConfigManager
	private Map configGroups = new HashMap();
	// a map of all the configGroups as Maps, useful for JSPs
	private Map configGroupsMap = new HashMap();
	private ConfigManager defaultGroup = null;
	
	private Config() {
		// Initialize the default set of properties
		Object[] configPaths = {
				"~/etcommons-config.properties", // user specific config properties
				"/usr/local/etc/etcommons-config.properties", // host specific properties override
				getClass().getResource("Config.properties") // default properties
			};
		this.configGroups = new HashMap();
		this.configGroupsMap = new HashMap();
		this.defaultGroup = new ConfigManager("etcommons-default", configPaths);
		this.defaultGroup.addConfiguration(new SystemConfiguration());
	}
	
	public static Config getInstance() {
		if (singleton == null) {
			singleton = new Config();
		}
		return singleton;
	}
	
	/**
	 * Get an Iterator to cycle through the properties for the given ConfigManager
	 * @param group
	 * @return
	 */
	public static Iterator getKeys(String group) {
		ConfigManager cg = getConfigManager(group);
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
		ConfigManager cg = getConfigManager(null);
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
	public static void addConfigManager(String group, Object[] configFiles) {
		getInstance().configGroups.put(group, new ConfigManager(group, configFiles));
		getInstance().configGroupsMap.put(group, getConfigManager(group).getAsMap());
	}
	
	/**
	 * Get the passed ConfigManager
	 * @param group
	 */
	public static ConfigManager getConfigManager(String group) {
		if (group == null || group.equals("")) {
			return getInstance().defaultGroup;
		} else {
			return (ConfigManager) getInstance().configGroups.get(group);
		}
	}
	
	/**
	 * Get the default ConfigManager
	 * @return
	 */
	public static ConfigManager getConfigManager() {
		return getInstance().defaultGroup;
	}
	
	/**
	 * Get an Object from the given group
	 * @param group
	 * @param property
	 * @return
	 */
	public static Object getObject(String group, String property) {
		ConfigManager cg = getConfigManager(group);
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
		ConfigManager cg = getConfigManager(group);
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
		ConfigManager cg = getConfigManager();
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
	
	
	public static Map getAsMap() {
		return getInstance().configGroupsMap;
	}
	
	/**
	 * Generate a string dump of all the configuration information for all the ConfigGroups
	 * @return
	 */
	public static String getDumpData() {
		StringBuffer buff = new StringBuffer();
		if (getConfigManager() != null) {
			buff.append(getConfigManager().getDumpData());
			buff.append("\n\n");
		}
		for (Iterator i = getInstance().configGroups.keySet().iterator(); i.hasNext(); ) {
			ConfigManager cg = getConfigManager((String) i.next());
			if (cg != null) {
				buff.append(cg.getDumpData());
				buff.append("\n\n");
			}
		}
		
		return buff.toString();
	}
	
	/**
	 * reload all of the ConfigGroups
	 *
	 */
	public static void reload() {
		if (getConfigManager() != null) {
			getConfigManager().reload();
		}
		for (Iterator i = getInstance().configGroups.keySet().iterator(); i.hasNext(); ) {
			String group = (String) i.next();
			if (getConfigManager(group) != null) {
				getConfigManager(group).reload();
			}
		}
		
	}

}
