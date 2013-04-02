/*
 * Created on Dec 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.util;

import java.lang.reflect.Field;
//import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;
//import java.util.Set;
//import java.util.Map;

/**
 * @author dcs
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertyReflection {
	private Field field;

	public static Map findReflectedProperties(Class k) {
		Field[] allFields = k.getDeclaredFields();
		Map properties = new HashMap(allFields.length);
		for (int i = 0; i< allFields.length; i++) {
			Field f = (Field) allFields[i];
			// transient fields aren't counted. ala XStream. nor do we want to process constants
			int modifiers = f.getModifiers();
			if (!(Modifier.isTransient(modifiers) || (Modifier.isStatic(modifiers)) || (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) )) {
				f.setAccessible(true);
				PropertyReflection p = new PropertyReflection(f);
				properties.put(p.getName(),p);
			}
		}
		
		Class parent = k.getSuperclass();
		if (!Object.class.equals(parent)) {
			properties.putAll(findReflectedProperties(parent));
//			parent = parent.getSuperclass();
		}
		return properties;
	}

	public PropertyReflection(Field f) {
		field = f;
	}
	public Field getField() {
		return field;
	}
	public String getName() {
		return field.getName();
	}
	public Object getValue(Object o) {
		try {
			return field.get(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public void setValue(Object o, Object value) {
		try {
			field.set(o,value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	public static void setReflectedProperty(Object o, String name, Object value) {
//	Field f;
//	try {
//		f = o.getClass().getDeclaredField(name);
//		f.set(o,value);
//	} catch (Exception e1) {
//		e1.printStackTrace();
//		// Java's inheritance stinks.
////		Class parent = o.getClass().getSuperclass();
////		while (parent != null) {
////			try {
////				f = parent.getDeclaredField(name);	
////				f.set(o,value);
////				break;
////			} catch (Exception e) {
////				// couldn't find the method.
////				e.printStackTrace();
////			}
////			parent = parent.getSuperclass();
////		}
//	}
//}
	
//	public static Object getProperty(Object o, String property) {
//	Field f;
//	Object returnval = null;
//	try {
//		f = o.getClass().getDeclaredField(property);
//		returnval = f.get(o);
//	} catch (Exception e) {
//		// if there's an exception, try going up the hierarchy
//		Class parent = o.getClass().getSuperclass();
//		while (!Object.class.equals(parent)) {
//			try {
//				f = parent.getDeclaredField(property);
//				returnval = f.get(o);
//				break;
//			} catch (Exception ex) {
//				e.printStackTrace();
//			}
//			parent = parent.getSuperclass();
//		}
//	}
//	return returnval;
//}
}
