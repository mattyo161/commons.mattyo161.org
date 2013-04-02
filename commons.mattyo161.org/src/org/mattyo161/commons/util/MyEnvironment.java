package org.mattyo161.commons.util;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.naming.*;

/**
 *A generic class for holding Environment information
 *<pre>
 *MyEnvirontment env = MyEnvironment.getEnvironment();
 *
 *out.println(env.getKeys());
 *  -- will print a list of the available enviroment variables
 *out.println(env.get("CLASSPATH");
 *  -- will print out the environment value for "CLASSPATH"
 *</pre>
 *
 *Currently there is only support for Tomcat Environment variables
 *
 * @see TomcatEnvironment
 * @author Matt Ouellette
 * @version 1.00 March 11, 2004
 */

public abstract class MyEnvironment {
    protected Map props = new HashMap();

    /**
     *Returns a specific environment variable from the MyEnvironment object
     *@param key the environment variable to lookup
     *@return the value of the environment varialbe, null if it could not be found
     */
    
    public abstract String get(String key);
    
    /**
     *Returns an ArrayList of all of the enviroment variables in the object
     *@return an ArrayList of all the environment variable names
     */
    
    public abstract ArrayList getKeys();
    
    /**
     *The MyEnvironment factory will generate the appropriate Environment object
     *for the environment the java code is running in, this could be preferences for an
     *application, the tomcat env for servlets or unix environment for command line
     *programs. Currently only tomcat is supported.
     *@return a MyEnvironment object that contains all of the environment information in one place
     */
    
    public static MyEnvironment getEnvironment() {
        // Eventually build some logic in here for getting the correct env information
        return new TomcatEnvironment();
    }
}