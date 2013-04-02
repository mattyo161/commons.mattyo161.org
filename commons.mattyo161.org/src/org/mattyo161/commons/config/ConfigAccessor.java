package org.mattyo161.commons.config;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SubsetConfiguration;

/**
 * This is a utility class for accesing ConfigManagers properties. You can create
 * a class with a single method that will create a ConfigManager in the Config
 * singleton and then return a new ConfigAccessor.
 * @author mattyo1
 *
 */
public class ConfigAccessor {
	String configName = null;
	ConfigManager configManager = null;
	
	public ConfigAccessor(String group) {
		this.configName = group;
	}
	
	/**
	 * Get the ConfigManager defined for this Config group
	 * @return
	 */
	protected ConfigManager getMyConfigManager() {
		// Check to see if there is a config defined for the given name if so retrieve it
		// but only if one has not been defined alread
		if (configManager == null) {
			this.configManager = Config.getConfigManager(configName);
		}
		return configManager;
	}
	
	/**
	 * Get the Commons Configuration stored in the ConfigManager Class
	 * @return
	 */
	private Configuration getMyConfiguration() {
		ConfigManager c = getMyConfigManager();
		if (c == null) {
			return null;
		} else {
			return c.getConfig();
		}
	}

	// Additional methods added for convience
	
	/**
	 * Simply get the given property as a String, no matter what type of property it is.
	 */
	public String get(String key) {
		Object returnValue = getProperty(key);
		if (returnValue == null) {
			return null;
		} else {
			return returnValue.toString();
		}
	}
	
	/**
	 * Return a map of the Configuration
	 * @return
	 */
	public Map getAsMap() {
		ConfigManager c = getMyConfigManager();
		if (c == null) {
			return null;
		} else {
			return c.getAsMap();
		}
	}
	
	/**
	 * Build a string of all values stored in the Configuration
	 * @return
	 */
	public String getDumpData() {
		ConfigManager c = getMyConfigManager();
		if (c == null) {
			return null;
		} else {
			return c.getDumpData();
		}
	}

	// Standard Methods provided by the Commons Configuration class that are implemented here
	
    /**
     * Return a decorator Configuration containing every key from the current
     * Configuration that starts with the specified prefix. The prefix is
     * removed from the keys in the subset. For example, if the configuration
     * contains the following properties:
     *
     * <pre>
     *    prefix.number = 1
     *    prefix.string = Apache
     *    prefixed.foo = bar
     *    prefix = Jakarta</pre>
     *
     * the Configuration returned by <code>subset("prefix")</code> will contain
     * the properties:
     *
     * <pre>
     *    number = 1
     *    string = Apache
     *    = Jakarta</pre>
     *
     * (The key for the value "Jakarta" is an empty string)
     * <p>
     * Since the subset is a decorator and not a modified copy of the initial
     * Configuration, any change made to the subset is available to the
     * Configuration, and reciprocally.
     *
     * @param prefix The prefix used to select the properties.
     *
     * @see SubsetConfiguration
     */
    public Configuration subset(String prefix) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.subset(prefix);
		}
	}


    /**
     * Check if the configuration is empty.
     *
     * @return <code>true</code> if the configuration contains no property,
     *         <code>false</code> otherwise.
     */
    public boolean isEmpty() {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return false;
		} else {
			return c.isEmpty();
		}
	}

    /**
     * Check if the configuration contains the specified key.
     *
     * @param key the key whose presence in this configuration is to be tested
     *
     * @return <code>true</code> if the configuration contains a value for this
     *         key, <code>false</code> otherwise
     */
    public boolean containsKey(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return false;
		} else {
			return c.containsKey(key);
		}
	}


    /**
     * Add a property to the configuration. If it already exists then the value
     * stated here will be added to the configuration entry. For example, if
     * the property:
     *
     * <pre>resource.loader = file</pre>
     *
     * is already present in the configuration and you call
     *
     * <pre>addProperty("resource.loader", "classpath")</pre>
     *
     * Then you will end up with a List like the following:
     *
     * <pre>["file", "classpath"]</pre>
     *
     * @param key The key to add the property to.
     * @param value The value to add.
     */
//    void addProperty(String key, Object value);

    /**
     * Set a property, this will replace any previously set values. Set values
     * is implicitly a call to clearProperty(key), addProperty(key, value).
     *
     * @param key The key of the property to change
     * @param value The new value
     */
//    void setProperty(String key, Object value);

    /**
     * Remove a property from the configuration.
     *
     * @param key the key to remove along with corresponding value.
     */
//    void clearProperty(String key);

    /**
     * Remove all properties from the configuration.
     */
//    void clear();

    /**
     * Gets a property from the configuration.
     *
     * @param key property to retrieve
     * @return the value to which this configuration maps the specified key, or
     *         null if the configuration contains no mapping for this key.
     */
    public Object getProperty(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getProperty(key);
		}
	}


    /**
     * Get the list of the keys contained in the configuration that match the
     * specified prefix.
     *
     * @param prefix The prefix to test against.
     * @return An Iterator of keys that match the prefix.
     */
    public Iterator getKeys(String prefix) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getKeys(prefix);
		}
	}


    /**
     * Get the list of the keys contained in the configuration.
     *
     * @return An Iterator.
     */
    public Iterator getKeys() {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getKeys();
		}
	}

    /**
     * Get a list of properties associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated properties if key is found.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a String/List.
     *
     * @throws IllegalArgumentException if one of the tokens is
     *         malformed (does not contain an equals sign).
     */
    public Properties getProperties(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getProperties(key);
		}
	}


    /**
     * Get a boolean associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated boolean.
     *
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Boolean.
     */
    public boolean getBoolean(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return false;
		} else {
			return c.getBoolean(key);
		}
	}


    /**
     * Get a boolean associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated boolean.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Boolean.
     */
	public boolean getBoolean(String key, boolean defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return false;
		} else {
			return c.getBoolean(key, defaultValue);
		}
	}


    /**
     * Get a {@link Boolean} associated with the given configuration key.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated boolean if key is found and has valid
     *         format, default value otherwise.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Boolean.
     */
    public Boolean getBoolean(String key, Boolean defaultValue) throws NoClassDefFoundError {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getBoolean(key, defaultValue);
		}
	}

    /**
     * Get a byte associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated byte.
     *
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Byte.
     */
    public byte getByte(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getByte(key);
		}
	}


    /**
     * Get a byte associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated byte.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Byte.
     */
	public byte getByte(String key, byte defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getByte(key, defaultValue);
		}
	}


    /**
     * Get a {@link Byte} associated with the given configuration key.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated byte if key is found and has valid format, default
     *         value otherwise.
     *
     * @throws ConversionException is thrown if the key maps to an object that
     *         is not a Byte.
     */
	public Byte getByte(String key, Byte defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getByte(key, defaultValue);
		}
	}


    /**
     * Get a double associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated double.
     *
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Double.
     */
    public double getDouble(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getDouble(key);
		}
	}


    /**
     * Get a double associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated double.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Double.
     */
	public double getDouble(String key, double defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getDouble(key, defaultValue);
		}
	}


    /**
     * Get a {@link Double} associated with the given configuration key.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated double if key is found and has valid
     *         format, default value otherwise.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Double.
     */
	public Double getDouble(String key, Double defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getDouble(key, defaultValue);
		}
	}


    /**
     * Get a float associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated float.
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Float.
     */
    public float getFloat(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getFloat(key);
		}
	}

    /**
     * Get a float associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated float.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Float.
     */
	public float getFloat(String key, float defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getFloat(key, defaultValue);
		}
	}


    /**
     * Get a {@link Float} associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated float if key is found and has valid
     *         format, default value otherwise.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Float.
     */
	public Float getFloat(String key, Float defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getFloat(key, defaultValue);
		}
	}


    /**
     * Get a int associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated int.
     *
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Integer.
     */
    public int getInt(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getInt(key);
		}
	}


    /**
     * Get a int associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated int.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Integer.
     */
	public int getInt(String key, int defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getInt(key, defaultValue);
		}
	}


    /**
     * Get an {@link Integer} associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated int if key is found and has valid format, default
     *         value otherwise.
     *
     * @throws ConversionException is thrown if the key maps to an object that
     *         is not a Integer.
     */
	public Integer getInteger(String key, Integer defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getInteger(key, defaultValue);
		}
	}


    /**
     * Get a long associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated long.
     *
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Long.
     */
    public long getLong(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getLong(key);
		}
	}


    /**
     * Get a long associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated long.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Long.
     */
	public long getLong(String key, long defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getLong(key, defaultValue);
		}
	}


    /**
     * Get a {@link Long} associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated long if key is found and has valid
     * format, default value otherwise.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Long.
     */
	public Long getLong(String key, Long defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getLong(key, defaultValue);
		}
	}


    /**
     * Get a short associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated short.
     *
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Short.
     */
    public short getShort(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getShort(key);
		}
	}


    /**
     * Get a short associated with the given configuration key.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated short.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Short.
     */
	public short getShort(String key, short defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return 0;
		} else {
			return c.getShort(key, defaultValue);
		}
	}


    /**
     * Get a {@link Short} associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated short if key is found and has valid
     *         format, default value otherwise.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a Short.
     *
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     */
	public Short getShort(String key, Short defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getShort(key, defaultValue);
		}
	}


    /**
     * Get a {@link BigDecimal} associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated BigDecimal if key is found and has valid format
     *
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     */
    public BigDecimal getBigDecimal(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getBigDecimal(key);
		}
	}


    /**
     * Get a {@link BigDecimal} associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key          The configuration key.
     * @param defaultValue The default value.
     *
     * @return The associated BigDecimal if key is found and has valid
     *         format, default value otherwise.
     */
	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getBigDecimal(key, defaultValue);
		}
	}


    /**
     * Get a {@link BigInteger} associated with the given configuration key.
     *
     * @param key The configuration key.
     *
     * @return The associated BigInteger if key is found and has valid format
     *
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     */
    public BigInteger getBigInteger(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getBigInteger(key);
		}
	}


    /**
     * Get a {@link BigInteger} associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key          The configuration key.
     * @param defaultValue The default value.
     *
     * @return The associated BigInteger if key is found and has valid
     *         format, default value otherwise.
     */
	public BigInteger getBigInteger(String key, BigInteger defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getBigInteger(key, defaultValue);
		}
	}


    /**
     * Get a string associated with the given configuration key.
     *
     * @param key The configuration key.
     * @return The associated string.
     *
     * @throws ConversionException is thrown if the key maps to an object that
     *         is not a String.
     *
     * @throws NoSuchElementException is thrown if the key doesn't
     *         map to an existing object.
     */
    public String getString(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getString(key);
		}
	}


    /**
     * Get a string associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated string if key is found and has valid
     *         format, default value otherwise.
     *
     * @throws ConversionException is thrown if the key maps to an object that
     *         is not a String.
     */
	public String getString(String key, String defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getString(key, defaultValue);
		}
	}


    /**
     * Get an array of strings associated with the given configuration key.
     * If the key doesn't map to an existing object an empty array is returned
     *
     * @param key The configuration key.
     * @return The associated string array if key is found.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a String/List of Strings.
     */
    public String[] getStringArray(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getStringArray(key);
		}
	}

    /**
     * Get a List of strings associated with the given configuration key.
     * If the key doesn't map to an existing object an empty List is returned.
     *
     * @param key The configuration key.
     * @return The associated List.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a List.
     */
    public List getList(String key) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getList(key);
		}
	}


    /**
     * Get a List of strings associated with the given configuration key.
     * If the key doesn't map to an existing object, the default value
     * is returned.
     *
     * @param key The configuration key.
     * @param defaultValue The default value.
     * @return The associated List of strings.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a List.
     */
	public List getList(String key, List defaultValue) {
		Configuration c = getMyConfiguration();
		if (c == null) {
			return null;
		} else {
			return c.getList(key, defaultValue);
		}
	}
}
