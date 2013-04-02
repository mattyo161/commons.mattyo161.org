package org.mattyo161.commons.util;

import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;

import javax.naming.*;


/**
 *Will extract the environment variables from Tomcat and make them
 *available in an easy to access format with
 *<pre>
 *envObject.get(<variable>)
 *</pre>
 *and
 *<pre>
 *envObject.getKeys()
 *</pre>
 *
 * @see MyEnvironment
 * @author Matt Ouellette
 * @version 1.00 March 11, 2004
 */


public class TomcatEnvironment extends MyEnvironment {

    /**
     *Read the Tomcat environment variables and store them for future use.
     */
    
    public TomcatEnvironment()
    {
        // pull in environment variables from tomcat
        try {
                Context initCtx = new InitialContext();
                Context envCtx = null;
                
                try {
                    envCtx = (Context) initCtx.lookup("java:comp/env");
                } catch (NoInitialContextException e) {
                    // don't need to do anything here
                }
                
                if (envCtx != null ) {
                    // Get listing of context
                    NamingEnumeration bindings = envCtx.list("");

                    // Go through each item in list
                    while (bindings.hasMore()) {
                            NameClassPair bd = (NameClassPair) bindings.next();
                            props.put(bd.getName(), envCtx.lookup(bd.getName()));
                    }
                } else {
                    // System.out.println("No Tomcat Environtment Variables defined");
                }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     *Return the requested environment variable
     *@param key the environment variable to lookup
     *@return the value of the environment varialbe, null if it could not be found
     */
    
    public String get(String key) {
        return (String) props.get((String) key);   
    }
    
    /** 
     *Return an ArrayList of all the environment variable names
     *@return an ArrayList of all the environment variable names
     */
    
    public ArrayList getKeys() {
        ArrayList returnValue = new ArrayList();
        Set keys = props.keySet();
        for (Iterator a = keys.iterator(); a.hasNext(); ) {
            returnValue.add(a.next());
        }
        
        return returnValue;
    }
 
    public Map getTomcatProps() {
    		return props;
    }
}