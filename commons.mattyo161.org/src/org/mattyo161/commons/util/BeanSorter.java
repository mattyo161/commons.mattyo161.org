/*
 * Created on Nov 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.util;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * @author dcs
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BeanSorter implements Comparator{
	private Method getter = null;
	private boolean ascending = true;
	
	/**
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * 
	 */
	public BeanSorter(String propertyName, Object bean, boolean ascending) throws SecurityException, NoSuchMethodException {
		super();
		this.ascending = ascending;
		Class[] signature = {};
		try {
			getter = bean.getClass().getDeclaredMethod("get"+propertyName,signature);
		} catch (Exception e) {
			Class parent = bean.getClass().getSuperclass();
			while (!Object.class.equals(parent)) {
				try {
					getter = parent.getDeclaredMethod("get"+propertyName,signature);
					break;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				parent = parent.getSuperclass();
			}
		}
		if (getter == null) {
			throw new NoSuchMethodException(propertyName);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		int returnval = 0;
		Comparable result0;
		Comparable result1;
		
		try {
			Comparable[] params = {};
			result0 = (Comparable) this.getter.invoke(arg0,params);
			result1 = (Comparable) this.getter.invoke(arg1,params);
			int comparison;
			if (ascending) {
				comparison = result0.compareTo(result1);
			} else {
				comparison = result1.compareTo(result0);
			}
			if (comparison < 1) {
				returnval = -1;
			} else if (comparison == 0) {
				returnval = 0;
			} else if (comparison > 1) {
				returnval = 1;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println(returnval);
		return returnval;
	}
	
}
