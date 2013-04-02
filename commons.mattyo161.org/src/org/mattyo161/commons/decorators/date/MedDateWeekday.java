/*
 * Created on Feb 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.decorators.date;

import org.displaytag.decorator.ColumnDecorator;
import org.displaytag.exception.DecoratorException;

import org.mattyo161.commons.cal.Cal;

/**
 * @author mattyo1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MedDateWeekday implements ColumnDecorator {

	/* (non-Javadoc)
	 * @see org.displaytag.decorator.ColumnDecorator#decorate(java.lang.Object)
	 */
	public String decorate(Object arg0) throws DecoratorException {
		Cal temp = new Cal(arg0);
		if (temp.getTimeInMillis() == 0) {
			return arg0.toString();
		}
		
		return temp.format("www, mmm d, yyyy");
	}
}
