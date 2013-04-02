package org.mattyo161.commons.db;

import org.apache.commons.configuration.MapConfiguration;

import org.mattyo161.commons.config.Config;
import org.mattyo161.commons.config.ConfigAccessor;
import org.mattyo161.commons.config.ConfigManager;
import org.mattyo161.commons.util.TomcatEnvironment;

/*
 * This is a simple class to make it easier to get values from the adCentral configuration
 */
public class ConnectionManagerConfig  {
	public static ConfigAccessor config() {
		ConfigManager c = Config.getConfigManager("org.mattyo161.commons.db.ConnectionManager");
		if (c == null) {
			Object[] configFiles = {
				 	"/usr/local/etc/ConnectionManager.properties", // load host properties
				 	ConnectionManagerConfig.class.getResource("ConnectionManager.properties") //load default properties
			 	};
			Config.addConfigManager("org.mattyo161.commons.db.ConnectionManager", configFiles);
		}
		return new ConfigAccessor("org.mattyo161.commons.db.ConnectionManager");
	}
}
