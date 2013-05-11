/*
 * Created on Nov 19, 2004
 *
 * Contains implementation of XMLin and XMLout methods for serializing this object
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
public class BaseType {
	private transient Map myproperties = null;

	public Map asMap() {
		HashMap myself = new HashMap();
		Set entries = this.entrySet();
		Iterator i = entries.iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			myself.put(entry.getKey(),entry.getValue());
		}
		return myself;
	}
	
	public String toString() {
	    return asMap().toString();
	}
	
	private Map myProperties() {
		if (this.myproperties == null) {
			this.myproperties = PropertyReflection.findReflectedProperties(this.getClass());
		}
		return this.myproperties;
	}
	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		return this.myProperties().size();
	}
	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object arg0) {
		return this.myProperties().containsKey(arg0);
	}
	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object arg0) {
		if (this.myProperties().containsKey(arg0)) {
			PropertyReflection p = (PropertyReflection) this.myProperties().get(arg0);
			return p.getValue(this);
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object arg0, Object arg1) {
		if (this.myProperties().containsKey(arg0)) {
			PropertyReflection p = (PropertyReflection) this.myProperties().get(arg0);
			p.setValue(this,arg1);
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map arg0) {
		Set entries = arg0.entrySet();
		Iterator i = entries.iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			this.put(entry.getKey(),entry.getValue());
		}
	}
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		return this.myProperties().keySet();
	}
	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection values() {
		ArrayList values = new ArrayList(this.myProperties().size());
		Set entries = this.entrySet();
		Iterator i = entries.iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			values.add(entry.getValue());
		}
		return values;
	}
	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		Set entries = new HashSet();
		Set keys = this.keySet();
		Iterator i = keys.iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			//System.out.println(key);
			Object value = this.get(key);
			if (value instanceof Map) {
				value = new Hashtable((Map) value);
			} else if (value instanceof BaseType) {
				value = new Hashtable(((BaseType) value).asMap());
			} else if (value == null) {
				continue;
			}
			Map.Entry entry = new mapEntry(key,value);
			entries.add(entry);
		}
		return entries;
	}
	public class mapEntry implements Map.Entry {
		private Object key;
		private Object value;
		public mapEntry(Object key, Object value) {
			this.key = key;
			this.value = value;
		}
		/* (non-Javadoc)
		 * @see java.util.Map.Entry#getKey()
		 */
		public Object getKey() {
			return key;
		}

		/* (non-Javadoc)
		 * @see java.util.Map.Entry#getValue()
		 */
		public Object getValue() {
			return value;
		}

		/* (non-Javadoc)
		 * @see java.util.Map.Entry#setValue(java.lang.Object)
		 */
		public Object setValue(Object arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
