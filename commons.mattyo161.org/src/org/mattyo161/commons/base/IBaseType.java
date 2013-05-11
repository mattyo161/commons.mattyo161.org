/*
 * Created on Feb 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.base;

//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
//import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mattyo161.commons.util.PropertyReflection;

/**
 * @author dcs
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IBaseType {
	public Map asMap();
	public int size();
	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty();
	public boolean containsKey(Object arg0);
	public boolean containsValue(Object arg0);
	public Object get(Object arg0);
	public Object put(Object arg0, Object arg1);
	public Object remove(Object arg0);
	public void putAll(Map arg0);
	public void clear();
	public Set keySet();
	public Collection values();
	public Set entrySet();
}

