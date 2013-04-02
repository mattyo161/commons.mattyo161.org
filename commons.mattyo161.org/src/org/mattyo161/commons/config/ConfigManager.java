package org.mattyo161.commons.config;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.ConfigurationException;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationMap;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * A package for managing jakarta Commons Configuration classes. This is mainly used in the
 * Config and Message classes
 * Details on Properties files: http://commons.apache.org/configuration/userguide/howto_properties.html#Properties_files
 * @author mattyo1
 *
 */
public class ConfigManager {
	private String name;
	private CompositeConfiguration config;
	// this property is used to keep track of configFiles loaded, so that when reload is
	// run in can attempt to find files again, if they did not exist at first.
	private Vector configFileObjects = new Vector();
	// keep a list of the urls that were used to generate the CompositeConfig
	private Vector configUrls = new Vector();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		URL configUrl = null;
		try {
			configUrl = Class.forName("org.mattyo161.commons.config.Config").getResource("Config.properties");
		} catch (Exception e) { e.printStackTrace(); }
		ConfigManager test = new ConfigManager("default",new Object[] {"~/User-config.properties",configUrl});
		System.out.println(test.name);
		System.out.println(test.config.getString("Workstation.db"));
		System.out.println(test.config.getString("test"));
		System.out.println(test.config.getProperty("test2"));	
		
		System.out.println(test.config.getString("Workstation.db"));
		System.out.println(test.config.getString("test"));
		System.out.println(test.config.getProperty("test2"));		
		

	}

	/**
	 * Build a Composite configuration based on the config files provided they will be added
	 * in order and can be Strings, URLs or Files
	 * @param name
	 * @param configFiles
	 */
	public ConfigManager(String name, Object[] configFiles) {
		// Create a Composite Config
		this.name = name;
		this.config = new CompositeConfiguration();
		for (int i = 0; i < configFiles.length; i++) {
			System.out.println("Getting config: " + configFiles[i]);
			addConfiguration(configFiles[i]);
		}
		
	}
	
	/**
	 * Add a configuration file to the given configGroup
	 * @param configFile
	 */
	public void addConfiguration(Object configFile) {
//		System.out.println("Adding Config: " + configFile);
		this.configFileObjects.add(configFile);
		Configuration config = getNewConfiguration(configFile);
		if (config != null) {
			this.config.addConfiguration(config);
			if (Configuration.class.isInstance(configFile)) {
				this.configUrls.add(configFile.getClass().getName());
			} else {
				URL path = pathToUrl(configFile);
				if (path != null) {
					this.configUrls.add(path);
				} else {
					this.configUrls.add(configFile.getClass().getName());
				}
			}
		}
	}
	
	/**
	 * will clear the current configuration removing all properties and then load all the
	 * properties over again, this may need a little work to ensure that properties are 
	 * available while it is reloading
	 *
	 */
	public void reload() {
		// Create a new Config object
		CompositeConfiguration newConfig = new CompositeConfiguration();
		Vector newConfigUrls = new Vector();
		for (Iterator i = configFileObjects.iterator(); i.hasNext(); ) {
			Object configFileObject = i.next();
			// we need to do this here if we use addConfig it will modify the Vector
			System.out.println("Reloading config: " + configFileObject);
			Configuration tmpConfig = getNewConfiguration(configFileObject);
			if (tmpConfig != null) {
				newConfig.addConfiguration(tmpConfig);
				if (Configuration.class.isInstance(configFileObject)) {
					newConfigUrls.add(configFileObject.getClass().getName());
				} else {
					URL path = pathToUrl(configFileObject);
					if (path != null) {
						newConfigUrls.add(path);
					} else {
						newConfigUrls.add(configFileObject.getClass().getName());
					}
				}
			}
		}
		// clear the current configuration out of memory and load the new one in its place
		// this may not be a good method
		this.config.clear();
		this.config = newConfig;
		this.configUrls = newConfigUrls;
	}
	
	/**
	 * Get the configuration that is stored in the config group as a map, this is useful in JSPs using expression language
	 * There are some limitations to the map interface it is not dynamic so if the 
	 * attributes are added or removed they are not updated. would need to do a reload
	 * @return
	 */
	public Map getAsMap() {
		return new ConfigurationMap(this.config);
	}
	
	/**
	 * Convert a String, File or URL to a URL that can be used to load a config file
	 * @param configFile
	 * @return
	 */
	private static URL pathToUrl(Object configFile) {
		URL pathUrl = null;
		try {
			if (configFile.getClass() == String.class) {
				String path = (String) configFile;
				if (path.startsWith("~")) {
					path = System.getProperty("user.home") + path.substring(1);
					pathUrl = new File(path).toURI().toURL();
				} else if (path.startsWith(".")) {
					path = System.getProperty("user.dir") + "/" + path;
					pathUrl = new File(path).toURI().toURL();
				} else if (!path.startsWith("/")) {
					pathUrl = ClassLoader.getSystemResource(path);
				} else {
					pathUrl = new File(path).toURI().toURL();
				}
			} else if (configFile.getClass() == File.class) {
				pathUrl = ((File) configFile).toURI().toURL();
			} else if (configFile.getClass() == URL.class) {
				pathUrl = (URL) configFile;
			}
		} catch (Exception e) {
			// do nothing
//			System.out.println("\n*** Config Error:");
//			e.printStackTrace();
//			System.out.println("***\n\n");
		}
		return pathUrl;
	}
	
	/**
	 * Generate a string containing all of the URLs used to build the ConfigManager
	 * and the properties with values
	 * @return
	 */
	public String getDumpData() {
		StringBuffer buff = new StringBuffer();
		buff.append(this.name + " ConfigManager URLs:\n");
		for (Iterator i = getUrls().iterator(); i.hasNext(); ) {
			Object currUrl = i.next();
			if (currUrl != null) {
				buff.append(currUrl.toString() + "\n");
			} else {
				buff.append("NULL\n");
			}
		}
		buff.append("\n");
		buff.append(this.name + " ConfigManager Values:\n");
		for (Iterator i = this.config.getKeys(); i.hasNext(); ) {
			String key = (String) i.next();
			buff.append(key + " = '" + this.config.getProperty(key) + "'\n");
		}
		return buff.toString();
	}
	
	/**
	 * Create a new Configuration object based on the configFile passed it can be a 
	 * String, File or URL if it is a string then ~ is replaced with user dir, and
	 * paths without a leading slash will use the ClassLoader to find the file in the
	 * classpath
	 * @param configFile
	 * @return
	 */
	private static Configuration getNewConfiguration(Object configFile) {
		Configuration config = null;
		if (configFile != null) { 
			try {
				if (Configuration.class.isInstance(configFile)) {
					return (Configuration) configFile;
				} else {
					URL pathUrl = pathToUrl(configFile);
					if (pathUrl != null) {
						// check to see if this is an xml or properties file if it is not an xml assume it is a properties file
						if (pathUrl.getPath().toLowerCase().endsWith(".xml")) {
							XMLConfiguration propConfig = new XMLConfiguration(pathUrl);
							// add a file watch to this config if it is a file
							if (pathUrl.getProtocol().equals("file")) {
								propConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
							}
						} else {
							PropertiesConfiguration propConfig = new PropertiesConfiguration(pathUrl);
							// add a file watch to this config if it is a file
							if (pathUrl.getProtocol().equals("file")) {
								propConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
							}
							config = propConfig;
						}
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
//				System.out.println("\n*** Config Error:");
//				e.printStackTrace();
//				System.out.println("***\n\n");
			}
		}
		return config;
	}

	public List getUrls() {
		return this.configUrls;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Configuration getConfig() {
		return config;
	}
}
