/*
 * DataMap.java
 *
 * Created on April 7, 2004, 4:35 PM
 */

package org.mattyo161.commons.util;

import java.sql.*;
import java.lang.*;
import java.text.*;
import java.util.*;
import org.mattyo161.commons.cal.*;
import org.mattyo161.commons.util.*;


/**
 * DataMap is a powerful class for storing and retrieving data from a TreeMap, it stores all data internally as a TreeMap
 * and allows you to set and get items from that TreeMap using strings as pointers. The concept is very powerful, since
 * there are methods that return specific types, so if you know the structure of what is mapped then you can retrieve it
 * with the correct cast, allowing you to directly manipulate the data. There will be more to come, including examples.
 * Put it allows you to create Hashes of data in a more similar fashion to what is done in langulates like perl and php.
 * @author  mattyo1
 */
public class DataMap {
    private Map dataMap = new TreeMap();

    /**
     * Creat a new instance of DataMap
     */
    public DataMap() {
    }

    /**
     * Add an Object of any Class to the DataMap
     * @param key the key to reference the object by
     * @param obj the Object to add
     */
    public void set(String key, Object obj) {
        dataMap.put(key, obj);
    }

    /**
     * Add an int to the DataMap, it will convert it to an Integer for storage and should be retrieved with getInt
     * @param key the key to reference the object by
     * @param obj the int to add
     */
    public void set(String key, int obj) {
        dataMap.put(key, new Integer(obj));
    }

    /**
     * Add a boolean to the DataMap, it will convert it to an Boolean for storage and should be retrieved with getBoolean
     * @param key the key to reference the object by
     * @param obj the int to add
     */
    public void set(String key, boolean obj) {
        dataMap.put(key, new Boolean(obj));
    }

   /**
    * Add a double to the DataMap, it will convert it to an Double for storage and should be retrieved with getDouble
    * @param key the key to reference the object by
    * @param obj the int to add
    */
   public void set(String key, double obj) {
        dataMap.put(key, new Double(obj));
    }

   /**
    * Retrieves the Object stored with the given key
    * @param key the key to lookup the object by
    * @return Object stored by the given key
    */
    public Object getObject(String key) {
        return dataMap.get(key);
    }

   /**
    * Retrieves the int stored with the given key
    * @param key the key to lookup the object by
    * @return int stored by the given key
    */
    public int getInt(String key) {
        Integer tmp = (Integer)dataMap.get(key);
        return tmp.intValue();
    }

   /**
    * Retrieves the int stored with the given key
    * @param key the key to lookup the object by
    * @return int stored by the given key
    */
    public boolean getBoolean(String key) {
        Boolean tmp = (Boolean)dataMap.get(key);
        return tmp.booleanValue();
    }

   /**
    * Retrieves the double stored with the given key
    * @param key the key to lookup the object by
    * @return double stored by the given key
    */
    public double getDouble(String key) {
        Double tmp = (Double)dataMap.get(key);
        return tmp.doubleValue();
    }

   /**
    * Retrieves the String stored with the given key
    * @param key the key to lookup the object by
    * @return String stored by the given key
    */
    public String getString(String key) {
        return (String)dataMap.get(key);
    }

    /**
     * Retrieves the DataMap stored with the given key
     * @param key the key to lookup the object by
     * @return DataMap stored by the given key
     */
     public DataMap getDataMap(String key) {
         return (DataMap)dataMap.get(key);
     }

     /**
      * Retrieves the Cal stored with the given key
      * @param key the key to lookup the object by
      * @return Cal stored by the given key
      */
      public Cal getCal(String key) {
          return (Cal)dataMap.get(key);
      }
      
   /**
    * Retrieves the List stored with the given key
    * @param key the key to lookup the object by
    * @return List stored by the given key
    */
    public List getList(String key) {
        return (List)dataMap.get(key);
    }

   /**
    * Retrieves the Set stored with the given key
    * @param key the key to lookup the object by
    * @return Set stored by the given key
    */
    public Set getSet(String key) {
        return (Set)dataMap.get(key);
    }
    
   /**
    * Checks to see if the DataMap contains an object stored with the given key
    * @param key the key to lookup the object by
    * @return true if the key exists or false if it does not
    */
    public boolean containsKey(String key) {
        return dataMap.containsKey(key);
    }
    
   /**
    * Get a set of keys for this DataMap
    * @return Set containing all of the keys, in this DataMap
    */
    public Set getKeys() {
        return dataMap.keySet();
    }
    
    /**
     * Retruns a string representation of the DataMap, used mostly for debugging, it can be fairly ugly.
     */
    public String toString() {
        Set keys = dataMap.keySet();
        StringBuffer retValue = new StringBuffer("[");
        int count = 0;
        for (Iterator i = keys.iterator(); i.hasNext(); count++) {
            String currKey = (String)i.next();
            if (count > 0) { 
                retValue.append("," + currKey + "->" + dataMap.get(currKey).toString());
            } else {
                retValue.append(currKey + "->" + dataMap.get(currKey).toString());
            }
        }
        retValue.append("]");
        
        return retValue.toString();
    }
}
