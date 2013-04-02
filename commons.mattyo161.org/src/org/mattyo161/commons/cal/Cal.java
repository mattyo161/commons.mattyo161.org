/*
 * cal2.java
 *
 * Created on March 31, 2004, 12:36 PM
 */

package org.mattyo161.commons.cal;

import java.util.*;
import java.sql.DatabaseMetaData;
import java.text.*;
import java.lang.*;

/**
 * <code>Cal</code> extends the <code>GregorianCalendar</code> class in order to add additional functionality
 * to make it easier to work with dates in java. You can automatically, set, get, and construct <code>Cal</code>
 * objects from the following date Classes, (java.lang.Date, java.sql.Date, java.sql.Time, java.sql.Timestamp, 
 * and Calendar). You can also set, get and construct <code>Cal</code> objects from a string and a SimpleDateFormat
 * or a <code>Cal.format</code> which is a modified set of strings based on SimpleDateFormat. <code>Cal</code>
 * also implements the <code>comparable</code> interface so it can be used as a keys in <code>Maps</code>, although this
 * needs some testing, and can be sorted in a <code>Set</code>.<br />
 * Here are a few examples of how to use <code>Cal</code>:
<blockquote>
 System.out.println(Cal.parse("1/1/04"));
 -> 01/01/2004 00:00:00.000
 
 System.out.println(new Cal(2004,2,1,15,4,2));
 -> 02/01/2004 15:04:02.000
 
 Date theDate = new Date();
 Cal theCal = new Cal(theDate);
 System.out.println(theCal);
 -> 04/02/2004 14:51:32.000
 
 java.sql.Time theTime = rs.getTime(1);
 System.out.println(new Cal(theTime).format("wwww, mmmm d, yyyy h:nn aa")
 -> Friday, April 2, 2004 2:51 PM
 
 rs.setTimeStamp(1,new Cal(2004,3,14).getSqlTimeStamp());
 System.out.println(new Cal(theTime).format("wwww, mmmm d, yyyy h:nn aa")
 -> Wednesday, April 14, 2004 12:00 AM
</blockquote>
 * You can also use all of the funcitons that are available in Calendar
 * @author  mattyo1
 * @see java.util.Calendar
 * @see java.util.GregorianCalendar
 */
/**
 * @author mattyo1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cal extends GregorianCalendar implements Comparable<Calendar> {
    // Used to determine which format string was used when running a multiple parse option
	protected String parseFormatString = "";
	
	// will be used by the parse function to convert a string to a Cal
	protected static String[] parseFormatStrings = {
            "MM/dd/yyyy hh:mm:ss.SSS aa",
            "MM/dd/yyyy HH:mm:ss.SSS",
            "M/d/yy hh:mm:ss aa",
            "M/d/yy HH:mm:ss",
            "M/d/yy hh:mm aa",
            "M/d/yy HH:mm",
            "M/d/yy",
            "MMM d, yyyy hh:mm:ss aa",
            "MMM d, yyyy HH:mm:ss",
            "MMM d, yyyy hh:mm aa",
            "MMM d, yyyy HH:mm",
            "MMM d, yyyy",
            "yyyyMMddHHmmssSSS",
            "yyyyMMddHHmmss",
            "yyyyMMddHHmm",
            "yyyyMMdd",
            "EEE, MMM d, yyyy hh:mm:ss aa",
            "EEE, MMM d, yyyy HH:mm:ss",
            "EEE, MMM d, yyyy hh:mm aa",
            "EEE, MMM d, yyyy HH:mm",
            "EEE, MMM d, yyyy",
            "M.d.yy hh:mm:ss aa",
            "yyyy.MM.dd hh:mm:ss aa",
            "M.d.yy HH:mm:ss",
            "yyyy.MM.dd HH:mm:ss",
            "M.d.yy hh:mm aa",
            "M.d.yy HH:mm",
            "M.d.yy",
            "yyyy.MM.dd",
            "M-d-yy hh:mm:ss aa",
            "yyyy-MM-dd hh:mm:ss aa",
            "M-d-yy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "M-d-yy hh:mm aa",
            "M-d-yy HH:mm",
            "M-d-yy",
            "yyyy-MM-dd",
            "yyyy-M-d h:mm:ss aa",
            "yyyy-M-d H:mm:ss",
            "yyyy.M.d h:mm:ss aa",
            "yyyy.M.d H:mm:ss",
            "EEE MMM d HH:mm:ss yyyy",	// perl formated localtime
            "EEE MMM d, yyyy hh:mm:ss aa",
            "EEE MMM d, yyyy HH:mm:ss",
            "EEE MMM d, yyyy hh:mm aa",
            "EEE MMM d, yyyy HH:mm",
            "EEE MMM d, yyyy",
            "EEE MMM d yyyy hh:mm:ss aa",
            "EEE MMM d yyyy HH:mm:ss",
            "EEE MMM d yyyy hh:mm aa",
            "EEE MMM d yyyy HH:mm",
            "EEE MMM d yyyy",
            "MMM d hh:mm:ss aa",
            "MMM d HH:mm:ss",
            "MMM d hh:mm aa",
            "MMM d HH:mm",
            "MMM d",
            "MM/dd",
            "M/d"
    };
    
	
    /**
     * Constructs a <code>Cal</code> with the default time zone and locale.
     */
    public Cal() {
        super.getInstance();
    }
  
    /**
     * Constructs a <code>Cal</code> with the time from the given Object value.
     * Using the default time zone with the default locale.
     * The currently supported objects are:
     * org.mattyo161.commons.cal.Cal, java.util.Date, java.sql.Date, java.sql.Time, java.sql.Timestamp
     * java.lang.String
     * if the Object obj is not one of these types or there is an error it will return a cal with a
     * millis of 0
     * @param obj the value to use for creating the Cal
     */   
    public Cal(Object obj) {
        super.getInstance();
        if (obj != null) {
            Class objClass = obj.getClass();
            long millis = 0;
            // Check to see if the object is an instance of calendar if so then we are compatible
            // I guess we could do the same for date, etc. but that may not be good so we won't for now.
            try {
	            if (Calendar.class.isInstance(obj) || objClass == Cal.class) {
	                millis = ((Calendar) obj).getTimeInMillis();
	            } else if (objClass == Date.class) {
		            millis = new Cal((java.util.Date) obj).getTimeInMillis();
	            } else if (objClass == java.sql.Date.class) {
		            millis = new Cal((java.sql.Date) obj).getTimeInMillis();
	            } else if (objClass == java.sql.Time.class) {
		            millis = new Cal((java.sql.Time) obj).getTimeInMillis();
	            } else if (java.sql.Timestamp.class.isInstance(obj)) {
		            millis = new Cal((java.sql.Timestamp) obj).getTimeInMillis();
	            } else if (objClass == java.lang.String.class) {
		            Cal tmpCal = new Cal((String) obj);
		            millis = tmpCal.getTimeInMillis();
		            // set the format string used when parsing the date
		            this.parseFormatString = tmpCal.getParseFromatString();
	            }
	            // try and set the time provided that 
                super.setTimeInMillis(millis);
            } catch (Exception e) {
                // do nothing
                super.setTimeInMillis(0);
            }
        } else {
            super.setTimeInMillis(0);
        }
    }
    
    /**
     * Constructs a GregorianCalendar based on the current time
     * in the given time zone with the default locale.
     * @param zone the given time zone.
     */
    public Cal(TimeZone zone) {
        super(zone, Locale.getDefault());
    }

    /**
     * Constructs a GregorianCalendar based on the current time
     * in the default time zone with the given locale.
     * @param aLocale the given locale.
     */
    public Cal(Locale aLocale) {
        super(TimeZone.getDefault(), aLocale);
    }

    /**
     * Constructs a GregorianCalendar based on the current time
     * in the given time zone with the given locale.
     * @param zone the given time zone.
     * @param aLocale the given locale.
     */
    public Cal(TimeZone zone, Locale aLocale) {
        super(zone, aLocale);
        setTimeInMillis(System.currentTimeMillis());
    }

    
    /**
     * Constructs a <code>Cal</code> with the time from the given long value.
     * Using the default time zone with the default locale.
     * @param millis the new time in UTC milliseconds from the epoch
     */    
    public Cal(long millis) {
        super.getInstance();
        super.setTimeInMillis(millis);
    }
    
    /**
     * Constructs a <code>Cal</code> with the parsed String value, see <code>parse</code> for a description
     * of the accepted formats.
     * Using the default time zone with the default locale.
     * @param theDateString date to parse
     * @see <a href="#parse(java.lang.String)">parse(String)</a>
     */    
    public Cal(String theDateString) {
        super.getInstance();
        Cal tmpCal = parse(theDateString);
        if (tmpCal != null) {
            super.setTimeInMillis(tmpCal.getTimeInMillis());
            // set the format string used when parsing the date
            this.parseFormatString = tmpCal.getParseFromatString();
        }
    }
    
    /**
     * Constructs a <code>Cal</code> with the parsed String value and format, see <code>parse</code> for a 
     * description of the accept options.
     * Using the default time zone with the default locale.
     * @param theDateString date to parse
     * @see <a href="#parse(java.lang.String, java.lang.String)">parse(String, String)</a>
     */    
    public Cal(String theDateString, String formatString) {
        super.getInstance();
        // parse will perform the format2SimpleDateFormat, if performing it twice we will get bad results
        //Cal tmpCal = parse(theDateString, format2SimpleDateFormat(formatString));
        Cal tmpCal = parse(theDateString, formatString);
        if (tmpCal != null) {
            super.setTimeInMillis(tmpCal.getTimeInMillis());
            // set the format string used when parsing the date
            this.parseFormatString = tmpCal.getParseFromatString();
        }
    }
    
    /**
     * Sets this <code>Cal's</code> current time to the given <code>Calendar</code> value. This
     * will perform a copy of the passed <code>Calendar</code>.
     * Using the default time zone with the default locale.
     * @param cal <code>Calendar</code> to copy from
     */    
    public Cal(Calendar cal) {
        if (cal == null) {
            cal = super.getInstance();
        }
        super.getInstance();
        super.setTimeInMillis(cal.getTimeInMillis());
    }
    
    /**
     * Constructs a <code>Cal</code> with the <code>java.util.Date</code> value. This
     * will perform a copy of the passed <code>java.util.Date</code>.
     * Using the default time zone with the default locale.
     * @param date <code>java.util.Date</code> to copy from
     */    
    public Cal(java.util.Date date) {
        super.getInstance();
        super.setTime(date);
    }
    
    /**
     * Constructs a <code>Cal</code> with the <code>java.sql.Date</code> value. This
     * will perform a copy of the passed <code>java.sql.Date</code>.
     * Using the default time zone with the default locale.
     * @param date <code>java.sql.Date</code> to copy from
     */    
    public Cal(java.sql.Date date) {
        super.getInstance();
        super.setTimeInMillis(date.getTime());
    }
    
    /**
     * Constructs a <code>Cal</code> with the <code>java.sql.Time</code> value. This
     * will perform a copy of the passed <code>java.sql.Time</code>.
     * Using the default time zone with the default locale.
     * @param date <code>java.sql.Time</code> to copy from
     */    
    public Cal(java.sql.Time date) {
        super.getInstance();
        super.setTimeInMillis(date.getTime());
    }
    
    /**
     * Constructs a <code>Cal</code> with the <code>java.sql.Timestamp</code> value. This
     * will perform a copy of the passed <code>java.sql.Timestamp</code>.
     * Using the default time zone with the default locale.
     * @param date <code>java.sql.Timestamp</code> to copy from
     */    
    public Cal(java.sql.Timestamp date) {
        super.getInstance();
        super.setTimeInMillis(date.getTime());
    }
    
    /**
     * Constructs a <code>Cal</code> with the given date set in the default time zone with the default locale.
     * @param year the value used to set the <code>YEAR</code> time field in the calendar.
     * @param month the value used to set the <code>MONTH</code> time field in the calendar. Month value is 0-based. e.g., 0 for January.
     * @param day the value used to set the <code>DATE</code> time field in the calendar.
     */
    public Cal(int year, int month, int day) {
        super.getInstance();
        super.set(year, month, day, 0, 0, 0);
        // Need to set the millis to 0 as well
        super.set(Calendar.MILLISECOND, 0);
    }
    
    /**
     * Constructs a <code>Cal</code> with the given date set in the default time zone with the default locale.
     * @param year the value used to set the <code>YEAR</code> time field in the calendar.
     * @param month the value used to set the <code>MONTH</code> time field in the calendar. Month value is 0-based. e.g., 0 for January.
     * @param day the value used to set the <code>DATE</code> time field in the calendar.
     * @param hour the value used to set the <code>HOUR</code> time field in the calendar.
     */
    public Cal(int year, int month, int day, int hour) {
        super.getInstance();
        super.set(year, month, day, hour, 0, 0);
        // Need to set the millis to 0 as well
        super.set(Calendar.MILLISECOND, 0);
    }
    
    /**
     * Constructs a <code>Cal</code> with the given date set in the default time zone with the default locale.
     * @param year the value used to set the <code>YEAR</code> time field in the calendar.
     * @param month the value used to set the <code>MONTH</code> time field in the calendar. Month value is 0-based. e.g., 0 for January.
     * @param day the value used to set the <code>DATE</code> time field in the calendar.
     * @param hour the value used to set the <code>HOUR</code> time field in the calendar.
     * @param minute the value used to set the <code>MINUTE</code> time field in the calendar.
     */
    public Cal(int year, int month, int day, int hour, int minute) {
        super.getInstance();
        super.set(year, month, day, hour, minute, 0);
        // Need to set the millis to 0 as well
        super.set(Calendar.MILLISECOND, 0);
    }
    
    /**
     * Constructs a <code>Cal</code> with the given date set in the default time zone with the default locale.
     * @param year the value used to set the <code>YEAR</code> time field in the calendar.
     * @param month the value used to set the <code>MONTH</code> time field in the calendar. Month value is 0-based. e.g., 0 for January.
     * @param day the value used to set the <code>DATE</code> time field in the calendar.
     * @param hour the value used to set the <code>HOUR</code> time field in the calendar.
     * @param minute the value used to set the <code>MINUTE</code> time field in the calendar.
     * @param second the value used to set the <code>SECOND</code> time field in the calendar.
     */
    public Cal(int year, int month, int day, int hour, int minute, int second) {
        super.getInstance();
        super.set(year, month, day, hour,  minute, second);
        // Need to set the millis to 0 as well
        super.set(Calendar.MILLISECOND, 0);
    }
    
    /**
     * Constructs a <code>Cal</code> with the given date set in the default time zone with the default locale.
     * @param year the value used to set the <code>YEAR</code> time field in the calendar.
     * @param month the value used to set the <code>MONTH</code> time field in the calendar. Month value is 0-based. e.g., 0 for January.
     * @param day the value used to set the <code>DATE</code> time field in the calendar.
     * @param hour the value used to set the <code>HOUR</code> time field in the calendar.
     * @param minute the value used to set the <code>MINUTE</code> time field in the calendar.
     * @param second the value used to set the <code>SECOND</code> time field in the calendar.
     * @param milli the value used to set the <code>MILLISECOND</code> time field in the calendar.
     */
    public Cal(int year, int month, int day, int hour, int minute, int second, int milli) {
        super.getInstance();
        super.set(year, month, day, hour,  minute, second);
        // Need to set the millis to 0 as well
        super.set(Calendar.MILLISECOND, milli);
    }
    
    /**
     * Returns a timestamp using a default format
     * @return a string that is the timestamp
     */    
    public static String getTimeStampString() {
    	return Cal.getTimeStampString("yyyy-mm-dd hh:nn:ss aa");
    }
    
    public static String getShortTimeStampString() {
    	return Cal.getTimeStampString("yyyymmddhhnnss");
    }
    
    /**
     * Returns a timestamp given the format.
     * @param format to use in creating the timestamp
     * @return a string that is the timestamp
     */    
    public static String getTimeStampString(String format) {
    	return new Cal().format(format);
    }
    
    /**
     * Compares this calendar to the specified object. The result is <code>true</code>
     * if and only if the argument is not <code>null</code> and
     * is a <code>Calendar</code> object that represents the same calendar as
     * this object.
     * @param obj object to compare to
     * @return <code>true</code> if the objets are the same; <code>false</code> otherwise
     */    
    public boolean equals(Cal objCal) {
        boolean retValue = false;
        if (objCal != null) {
            retValue = (objCal.getTimeInMillis() == this.getTimeInMillis());
        }
        return retValue;
    }

    /**
     * Compares this calendar to the specified object. The result is <code>true</code>
     * if and only if the argument is not <code>null</code> and
     * is a <code>Calendar</code> object that represents the same calendar as
     * this object.
     * @param obj object to compare to
     * @throws ClassCastException If the <code>Object</code> parameter does not implement the <code>Calendar</code> interface
     * @return <code>true</code> if the objets are the same; <code>false</code> otherwise
     */    
    public boolean equals(Object obj) throws ClassCastException {
        boolean retValue = false;
        if (obj != null) {
            Class objClass = obj.getClass();
            // Check to see if the object is an instance of calendar if so then we are compatible
            // I guess we could do the same for date, etc. but that may not be good so we won't for now.
            if (Calendar.class.isInstance(obj)) {
                Calendar objCal = (Calendar) obj;
                retValue = (objCal.getTimeInMillis() == this.getTimeInMillis());
            } else if (objClass.equals(new Cal().getClass())) {
	            Cal objCal = (Cal) obj;
	            retValue = this.equals(objCal);
            } else {
                throw new ClassCastException("An Ojbect of Class " + this.getClass().getName() + " cannot be compared to an Object of Class " + obj.getClass().getName());
            }
        }
        return retValue;
    }
    
    /**
     * Compares this calendar to the specified object. The object must be a <code>Calendar</code> ojbect
     * or be derived from a class that implements <code>Calendar</code>, if not a
     * <code>ClassCastException</code> is thrown. The comparison is currently based simply on the objects
     * time in milliseconds.
     * @param obj object to compare to
     * @throws ClassCastException If the <code>Object</code> parameter does not implement the <code>Calendar</code> interface
     * @return the value 0 if the two objects are equal;<br />
     * a value less than 0 if this <code>Calendar</code> is less then the passed <code>Calendar</code>;<br />
     * and a value greater than 0 if this <code>Calendar</code> greater then the passed <code>Calendar</code>.
     */    
    public int compareTo(Cal obj) throws ClassCastException {
        int retValue = -1;
        if (obj != null) {
            Class objClass = obj.getClass();
            // Check to see if the object is an instance of calendar if so then we are compatible
            // I guess we could do the same for date, etc. but that may not be good so we won't for now.
            if (Calendar.class.isInstance(obj)) {
                try {
                    Calendar objCal = (Calendar) obj;
                    long tmpValue = objCal.getTimeInMillis() - this.getTimeInMillis();
                    if (tmpValue < 0) {
                        retValue = 1;
                    } else if (tmpValue == 0) {
                        retValue = 0;
                    } else if (tmpValue > 0) {
                        retValue = -1;
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (objClass.equals(new Cal().getClass())) {
	            Cal objCal = (Cal) obj;
	            retValue = this.compareTo(objCal);
            } else {
                throw new ClassCastException("An Ojbect of Class " + getClass().getName() + " cannot be compared to an Object of Class " + obj.getClass().getName());
            }
        }
        return retValue;
    }
    
    public int compareTo(Calendar obj) throws ClassCastException {
    	return compareTo(new Cal(obj));
    }
    
    public int compareTo(Date obj) throws ClassCastException {
    	return compareTo(new Cal(obj));
    }
    
    public int compareTo(java.sql.Date obj) throws ClassCastException {
    	return compareTo(new Cal(obj));
    }
    
    public int compareTo(java.sql.Time obj) throws ClassCastException {
    	return compareTo(new Cal(obj));
    }
    
    public int compareTo(java.sql.Timestamp obj) throws ClassCastException {
    	return compareTo(new Cal(obj));
    }
    
    public int compareTo(String obj) throws ClassCastException {
    	return compareTo(new Cal(obj));
    }

    /**
     * Will format the <code>Cal</code> object based on the <code>formatString</code> passed and according
     * to the rules of the <code>SimpleFormatDate</code> Class.
 The following pattern letters are defined (all other characters from
 <code>'A'</code> to <code>'Z'</code> and from <code>'a'</code> to
 <code>'z'</code> are reserved):
 <blockquote>
 <table border=0 cellspacing=3 cellpadding=0 summary="Chart shows pattern letters, date/time component, presentation, and examples.">
     <tr bgcolor="#ccccff">
         <th align=left>Letter
         <th align=left>Date or Time Component
         <th align=left>Presentation
         <th align=left>Examples
     <tr>
         <td><code>G</code>
         <td>Era designator
         <td><a href="#text">Text</a>
         <td><code>AD</code>
     <tr bgcolor="#eeeeff">
         <td><code>y</code>
         <td>Year
         <td><a href="#year">Year</a>
         <td><code>1996</code>; <code>96</code>
     <tr>
         <td><code>M</code>
         <td>Month in year
         <td><a href="#month">Month</a>
         <td><code>July</code>; <code>Jul</code>; <code>07</code>
     <tr bgcolor="#eeeeff">
         <td><code>w</code>
         <td>Week in year
         <td><a href="#number">Number</a>
         <td><code>27</code>
     <tr>
         <td><code>W</code>
         <td>Week in month
         <td><a href="#number">Number</a>
         <td><code>2</code>
     <tr bgcolor="#eeeeff">
         <td><code>D</code>
         <td>Day in year
         <td><a href="#number">Number</a>
         <td><code>189</code>
     <tr>
         <td><code>d</code>
         <td>Day in month
         <td><a href="#number">Number</a>
         <td><code>10</code>
     <tr bgcolor="#eeeeff">
         <td><code>F</code>
         <td>Day of week in month
         <td><a href="#number">Number</a>
         <td><code>2</code>
     <tr>
         <td><code>E</code>
         <td>Day in week
         <td><a href="#text">Text</a>
         <td><code>Tuesday</code>; <code>Tue</code>
     <tr bgcolor="#eeeeff">
         <td><code>a</code>
         <td>Am/pm marker
         <td><a href="#text">Text</a>
         <td><code>PM</code>
     <tr>
         <td><code>H</code>
         <td>Hour in day (0-23)
         <td><a href="#number">Number</a>
         <td><code>0</code>
     <tr bgcolor="#eeeeff">
         <td><code>k</code>
         <td>Hour in day (1-24)
         <td><a href="#number">Number</a>
         <td><code>24</code>
     <tr>
         <td><code>K</code>
         <td>Hour in am/pm (0-11)
         <td><a href="#number">Number</a>
         <td><code>0</code>
     <tr bgcolor="#eeeeff">
         <td><code>h</code>
         <td>Hour in am/pm (1-12)
         <td><a href="#number">Number</a>
         <td><code>12</code>
     <tr>
         <td><code>m</code>
         <td>Minute in hour
         <td><a href="#number">Number</a>
         <td><code>30</code>
     <tr bgcolor="#eeeeff">
         <td><code>s</code>
         <td>Second in minute
         <td><a href="#number">Number</a>
         <td><code>55</code>
     <tr>
         <td><code>S</code>
         <td>Millisecond
         <td><a href="#number">Number</a>
         <td><code>978</code>
     <tr bgcolor="#eeeeff">
         <td><code>z</code>
         <td>Time zone
         <td><a href="#timezone">General time zone</a>
         <td><code>Pacific Standard Time</code>; <code>PST</code>; <code>GMT-08:00</code>
     <tr>
         <td><code>Z</code>
         <td>Time zone
         <td><a href="#rfc822timezone">RFC 822 time zone</a>
         <td><code>-0800</code>
 </table>
 </blockquote>
 Pattern letters are usually repeated, as their number determines the
 exact presentation:
 <ul>
 <li><strong><a name="text">Text:</a></strong>
     For formatting, if the number of pattern letters is 4 or more,
     the full form is used; otherwise a short or abbreviated form
     is used if available.
     For parsing, both forms are accepted, independent of the number
     of pattern letters.
 <li><strong><a name="number">Number:</a></strong>
     For formatting, the number of pattern letters is the minimum
     number of digits, and shorter numbers are zero-padded to this amount.
     For parsing, the number of pattern letters is ignored unless
     it's needed to separate two adjacent fields.
 <li><strong><a name="year">Year:</a></strong>
     For formatting, if the number of pattern letters is 2, the year
     is truncated to 2 digits; otherwise it is interpreted as a
     <a href="#number">number</a>.
     <p>For parsing, if the number of pattern letters is more than 2,
     the year is interpreted literally, regardless of the number of
     digits. So using the pattern "MM/dd/yyyy", "01/11/12" parses to
     Jan 11, 12 A.D.
     <p>For parsing with the abbreviated year pattern ("y" or "yy"),
     <code>SimpleDateFormat</code> must interpret the abbreviated year
     relative to some century.  It does this by adjusting dates to be
     within 80 years before and 20 years after the time the <code>SimpleDateFormat</code>
     instance is created. For example, using a pattern of "MM/dd/yy" and a
     <code>SimpleDateFormat</code> instance created on Jan 1, 1997,  the string
     "01/11/12" would be interpreted as Jan 11, 2012 while the string "05/04/64"
     would be interpreted as May 4, 1964.
     During parsing, only strings consisting of exactly two digits, as defined by
     <A HREF="../../java/lang/Character.html#isDigit(char)"><CODE>Character.isDigit(char)</CODE></A>, will be parsed into the default century.
     Any other numeric string, such as a one digit string, a three or more digit
     string, or a two digit string that isn't all digits (for example, "-1"), is
     interpreted literally.  So "01/02/3" or "01/02/003" are parsed, using the
     same pattern, as Jan 2, 3 AD.  Likewise, "01/02/-3" is parsed as Jan 2, 4 BC.
 <li><strong><a name="month">Month:</a></strong>
     If the number of pattern letters is 3 or more, the month is
     interpreted as <a href="#text">text</a>; otherwise,
     it is interpreted as a <a href="#number">number</a>.
 <li><strong><a name="timezone">General time zone:</a></strong>
     Time zones are interpreted as <a href="#text">text</a> if they have
     names. For time zones representing a GMT offset value, the
     following syntax is used:
     <pre>
     <a name="GMTOffsetTimeZone"><i>GMTOffsetTimeZone:</i></a>
             <code>GMT</code> <i>Sign</i> <i>Hours</i> <code>:</code> <i>Minutes</i>
     <i>Sign:</i> one of
             <code>+ -</code>
     <i>Hours:</i>
             <i>Digit</i>
             <i>Digit</i> <i>Digit</i>
     <i>Minutes:</i>
             <i>Digit</i> <i>Digit</i>
     <i>Digit:</i> one of
             <code>0 1 2 3 4 5 6 7 8 9</code></pre>
     <i>Hours</i> must be between 0 and 23, and <i>Minutes</i> must be between
     00 and 59. The format is locale independent and digits must be taken
     from the Basic Latin block of the Unicode standard.
     <p>For parsing, <a href="#rfc822timezone">RFC 822 time zones</a> are also
     accepted.
 <li><strong><a name="rfc822timezone">RFC 822 time zone:</a></strong>
     For formatting, the RFC 822 4-digit time zone format is used:
     <pre>
     <i>RFC822TimeZone:</i>
             <i>Sign</i> <i>TwoDigitHours</i> <i>Minutes</i>
     <i>TwoDigitHours:</i>
             <i>Digit Digit</i></pre>
     <i>TwoDigitHours</i> must be between 00 and 23. Other definitions
     are as for <a href="#timezone">general time zones</a>.
     <p>For parsing, <a href="#timezone">general time zones</a> are also
     accepted.
 </ul>
 <code>SimpleDateFormat</code> also supports <em>localized date and time
 pattern</em> strings. In these strings, the pattern letters described above
 may be replaced with other, locale dependent, pattern letters.
 <code>SimpleDateFormat</code> does not deal with the localization of text
 other than the pattern letters; that's up to the client of the class.
 <p>

 <h4>Examples</h4>

 The following examples show how date and time patterns are interpreted in
 the U.S. locale. The given date and time are 2001-07-04 12:08:56 local time
 in the U.S. Pacific Time time zone.
 <blockquote>
 <table border=0 cellspacing=3 cellpadding=0 summary="Examples of date and time patterns interpreted in the U.S. locale">
     <tr bgcolor="#ccccff">
         <th align=left>Date and Time Pattern
         <th align=left>Result
     <tr>
         <td><code>"yyyy.MM.dd G 'at' HH:mm:ss z"</code>
         <td><code>2001.07.04 AD at 12:08:56 PDT</code>
     <tr bgcolor="#eeeeff">
         <td><code>"EEE, MMM d, ''yy"</code>
         <td><code>Wed, Jul 4, '01</code>
     <tr>
         <td><code>"h:mm a"</code>
         <td><code>12:08 PM</code>
     <tr bgcolor="#eeeeff">
         <td><code>"hh 'o''clock' a, zzzz"</code>
         <td><code>12 o'clock PM, Pacific Daylight Time</code>
     <tr>
         <td><code>"K:mm a, z"</code>
         <td><code>0:08 PM, PDT</code>
     <tr bgcolor="#eeeeff">
         <td><code>"yyyyy.MMMMM.dd GGG hh:mm aaa"</code>
         <td><code>02001.July.04 AD 12:08 PM</code>
     <tr>
         <td><code>"EEE, d MMM yyyy HH:mm:ss Z"</code>
         <td><code>Wed, 4 Jul 2001 12:08:56 -0700</code>
     <tr bgcolor="#eeeeff">
         <td><code>"yyMMddHHmmssZ"</code>
         <td><code>010704120856-0700</code>
 </table>
 </blockquote>
     * @param formatString a SimpleDateFormat string to use for writing <code>Cal</code> to <code>String</code>
     * @return a <code>String</code> of the date representation
     * @see java.text.SimpleDateFormat
     */    
    public String formatSimpleDate(String formatString) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatString);
        return sdf.format(this.getTime());
    }
    
    /**
     * Will format the <code>Cal</code> object based on the <code>formatString</code> passed and according
     * to a slightly modified set of rules based on the <code>SimpleDateFormat</code> class, the changes
     * are marked in <b>bold</b>.
 The following pattern letters are defined (all other characters from
 <code>'A'</code> to <code>'Z'</code> and from <code>'a'</code> to
 <code>'z'</code> are reserved):
 <blockquote>
 <table border=0 cellspacing=3 cellpadding=0 summary="Chart shows pattern letters, date/time component, presentation, and examples.">
     <tr bgcolor="#ccccff">
         <th align=left>Letter
         <th align=left>Date or Time Component
         <th align=left>Presentation
         <th align=left>Examples
     <tr>
         <td><code>G</code>
         <td>Era designator
         <td><a href="#text">Text</a>
         <td><code>AD</code>
     <tr bgcolor="#eeeeff">
         <td><code>y</code>
         <td>Year
         <td><a href="#year">Year</a>
         <td><code>1996</code>; <code>96</code>
     <tr>
         <td><code>M</code>
         <td>Month in year
         <td><a href="#month">Month</a>
         <td><code>July</code>; <code>Jul</code>; <code>07</code>
     <tr>
         <td><code><b><font color="#FF0000">m</font></b></code>
         <td>Month in year
         <td><a href="#month">Month</a>
         <td><code>July</code>; <code>Jul</code>; <code>07</code>
     <tr bgcolor="#eeeeff">
         <td><code><b><font color="#FF0000">w</font></b></code>
         <td>Weekday
         <td><a href="#text">Text</a>
         <td><code>Sunday; Sun; 1</code>
     <tr>
         <td><code><b><font color="#FF0000">X</font></b></code>
         <td>Week in year
         <td><a href="#number">Number</a>
         <td><code>27</code>
     <tr>
         <td><code>W</code>
         <td>Week in month
         <td><a href="#number">Number</a>
         <td><code>2</code>
     <tr bgcolor="#eeeeff">
         <td><code>D</code>
         <td>Day in year
         <td><a href="#number">Number</a>
         <td><code>189</code>
     <tr>
         <td><code>d</code>
         <td>Day in month
         <td><a href="#number">Number</a>
         <td><code>10</code>
     <tr bgcolor="#eeeeff">
         <td><code>F</code>
         <td>Day of week in month
         <td><a href="#number">Number</a>
         <td><code>2</code>
     <tr>
         <td><code>E</code>
         <td>Day in week
         <td><a href="#text">Text</a>
         <td><code>Tuesday</code>; <code>Tue</code>
     <tr bgcolor="#eeeeff">
         <td><code>a</code>
         <td>Am/pm marker
         <td><a href="#text">Text</a>
         <td><code>PM</code>
     <tr>
         <td><code>H</code>
         <td>Hour in day (0-23)
         <td><a href="#number">Number</a>
         <td><code>0</code>
     <tr bgcolor="#eeeeff">
         <td><code>k</code>
         <td>Hour in day (1-24)
         <td><a href="#number">Number</a>
         <td><code>24</code>
     <tr>
         <td><code>K</code>
         <td>Hour in am/pm (0-11)
         <td><a href="#number">Number</a>
         <td><code>0</code>
     <tr bgcolor="#eeeeff">
         <td><code>h</code>
         <td>Hour in am/pm (1-12) unless AM/PM is not specified then it uses 24 hour time
         <td><a href="#number">Number</a>
         <td><code>12</code>
     <tr>
         <td><code><b><font color="#FF0000">n</font></b></code>
         <td>Minute in hour
         <td><a href="#number">Number</a>
         <td><code>30</code>
     <tr bgcolor="#eeeeff">
         <td><code>s</code>
         <td>Second in minute
         <td><a href="#number">Number</a>
         <td><code>55</code>
     <tr>
         <td><code>S</code>
         <td>Millisecond
         <td><a href="#number">Number</a>
         <td><code>978</code>
     <tr>
         <td><code><b><font color="#FF0000">u</font></b></code>
         <td>Millisecond
         <td><a href="#number">Number</a>
         <td><code>978</code>
     <tr bgcolor="#eeeeff">
         <td><code>z</code>
         <td>Time zone
         <td><a href="#timezone">General time zone</a>
         <td><code>Pacific Standard Time</code>; <code>PST</code>; <code>GMT-08:00</code>
     <tr>
         <td><code>Z</code>
         <td>Time zone
         <td><a href="#rfc822timezone">RFC 822 time zone</a>
         <td><code>-0800</code>
 </table>
 </blockquote>
 Pattern letters are usually repeated, as their number determines the
 exact presentation:
 <ul>
 <li><strong><a name="text">Text:</a></strong>
     For formatting, if the number of pattern letters is 4 or more,
     the full form is used; otherwise a short or abbreviated form
     is used if available.
     For parsing, both forms are accepted, independent of the number
     of pattern letters.
 <li><strong><a name="number">Number:</a></strong>
     For formatting, the number of pattern letters is the minimum
     number of digits, and shorter numbers are zero-padded to this amount.
     For parsing, the number of pattern letters is ignored unless
     it's needed to separate two adjacent fields.
 <li><strong><a name="year">Year:</a></strong>
     For formatting, if the number of pattern letters is 2, the year
     is truncated to 2 digits; otherwise it is interpreted as a
     <a href="#number">number</a>.
     <p>For parsing, if the number of pattern letters is more than 2,
     the year is interpreted literally, regardless of the number of
     digits. So using the pattern "MM/dd/yyyy", "01/11/12" parses to
     Jan 11, 12 A.D.
     <p>For parsing with the abbreviated year pattern ("y" or "yy"),
     <code>SimpleDateFormat</code> must interpret the abbreviated year
     relative to some century.  It does this by adjusting dates to be
     within 80 years before and 20 years after the time the <code>SimpleDateFormat</code>
     instance is created. For example, using a pattern of "MM/dd/yy" and a
     <code>SimpleDateFormat</code> instance created on Jan 1, 1997,  the string
     "01/11/12" would be interpreted as Jan 11, 2012 while the string "05/04/64"
     would be interpreted as May 4, 1964.
     During parsing, only strings consisting of exactly two digits, as defined by
     <A HREF="../../java/lang/Character.html#isDigit(char)"><CODE>Character.isDigit(char)</CODE></A>, will be parsed into the default century.
     Any other numeric string, such as a one digit string, a three or more digit
     string, or a two digit string that isn't all digits (for example, "-1"), is
     interpreted literally.  So "01/02/3" or "01/02/003" are parsed, using the
     same pattern, as Jan 2, 3 AD.  Likewise, "01/02/-3" is parsed as Jan 2, 4 BC.
 <li><strong><a name="month">Month:</a></strong>
     If the number of pattern letters is 3 or more, the month is
     interpreted as <a href="#text">text</a>; otherwise,
     it is interpreted as a <a href="#number">number</a>.
 <li><strong><a name="timezone">General time zone:</a></strong>
     Time zones are interpreted as <a href="#text">text</a> if they have
     names. For time zones representing a GMT offset value, the
     following syntax is used:
     <pre>
     <a name="GMTOffsetTimeZone"><i>GMTOffsetTimeZone:</i></a>
             <code>GMT</code> <i>Sign</i> <i>Hours</i> <code>:</code> <i>Minutes</i>
     <i>Sign:</i> one of
             <code>+ -</code>
     <i>Hours:</i>
             <i>Digit</i>
             <i>Digit</i> <i>Digit</i>
     <i>Minutes:</i>
             <i>Digit</i> <i>Digit</i>
     <i>Digit:</i> one of
             <code>0 1 2 3 4 5 6 7 8 9</code></pre>
     <i>Hours</i> must be between 0 and 23, and <i>Minutes</i> must be between
     00 and 59. The format is locale independent and digits must be taken
     from the Basic Latin block of the Unicode standard.
     <p>For parsing, <a href="#rfc822timezone">RFC 822 time zones</a> are also
     accepted.
 <li><strong><a name="rfc822timezone">RFC 822 time zone:</a></strong>
     For formatting, the RFC 822 4-digit time zone format is used:
     <pre>
     <i>RFC822TimeZone:</i>
             <i>Sign</i> <i>TwoDigitHours</i> <i>Minutes</i>
     <i>TwoDigitHours:</i>
             <i>Digit Digit</i></pre>
     <i>TwoDigitHours</i> must be between 00 and 23. Other definitions
     are as for <a href="#timezone">general time zones</a>.
     <p>For parsing, <a href="#timezone">general time zones</a> are also
     accepted.
 </ul>
 <code>SimpleDateFormat</code> also supports <em>localized date and time
 pattern</em> strings. In these strings, the pattern letters described above
 may be replaced with other, locale dependent, pattern letters.
 <code>SimpleDateFormat</code> does not deal with the localization of text
 other than the pattern letters; that's up to the client of the class.
 <p>

 <h4>Examples</h4>

 The following examples show how date and time patterns are interpreted in
 the U.S. locale. The given date and time are 2001-07-04 12:08:56 local time
 in the U.S. Pacific Time time zone.
 <blockquote>
 <table border=0 cellspacing=3 cellpadding=0 summary="Examples of date and time patterns interpreted in the U.S. locale">
     <tr bgcolor="#ccccff">
         <th align=left>Date and Time Pattern
         <th align=left>Result
     <tr>
         <td><code>"yyyymmdd"</code>
         <td><code>20010704</code>
     <tr bgcolor="#eeeeff">
         <td><code>"www, mmm d, yyyy"</code>
         <td><code>Wed, Jul 4, 2001</code>
     <tr bgcolor="#eeeeff">
         <td><code>"wwww, mmmm d, yyyy"</code>
         <td><code>Wednesday, July 4, 2001</code>
     <tr>
         <td><code>"h:nn a"</code>
         <td><code>12:08 PM</code>
     <tr bgcolor="#eeeeff">
         <td><code>"wwww, mmmm d, yyyy h:nn:ss aa"</code>
         <td><code>Wednesday, July 4, 2001 5:08:00 PM</code>
     <tr bgcolor="#eeeeff">
         <td><code>"wwww, mmmm d, yyyy h:nn:ss"</code>
         <td><code>Wednesday, July 4, 2001 17:08:00 PM</code>
     <tr>
         <td><code>"m/d/yyyy"</code>
         <td><code>7/4/2001</code>
     <tr>
         <td><code>"m/d/yy"</code>
         <td><code>7/4/01</code>
     <tr bgcolor="#eeeeff">
         <td><code>"yymmddhhmmssZ"</code>
         <td><code>010704120856-0700</code>
 </table>
 </blockquote>
     * @param formatString a format string to use for writing <code>Cal</code> to <code>String</code>
     * @return a <code>String</code> of the date representation
     */    
    public String format(String formatString) {
        String temp = this.formatSimpleDate(format2SimpleDateFormat(formatString));
        temp = temp.replaceAll("##WW##", "0" + this.get(Calendar.DAY_OF_WEEK));
        temp = temp.replaceAll("##W##", "" + this.get(Calendar.DAY_OF_WEEK));
        return temp;
    }
    
    /**
     * Get the <code>Cal</code> object as a <code>java.sql.Date</code>
     * @return <code>java.sql.Date</code> representation of the <code>Cal</code>
     */
    public java.sql.Date getSqlDate() {
        java.sql.Date retValue = new java.sql.Date(this.getTimeInMillis());        
        return retValue;
    }
    
    /**
     * Get the <code>Cal</code> object as a <code>String</code>
     * @return <code>String</code> representation of the <code>Cal</code>
     */
    public String getNormalDate() {
        return format("mm/dd/yyyy");
    }
    
    /**
     * Get the <code>Cal</code> object as a <code>java.util.Date</code>
     * @return <code>java.util.Date</code> representation of the <code>Cal</code>
     */
    public java.util.Date getDate() {
    		java.util.Date retValue = new java.util.Date(this.getTimeInMillis());
    		return retValue;
    }
    
    /**
     * Get the <code>Cal</code> object as a <code>java.sql.Time</code>
     * @return <code>java.sql.Time</code> representation of the <code>Cal</code>
     */
    public java.sql.Time getSqlTime() {
        java.sql.Time retValue = new java.sql.Time(this.getTimeInMillis());        
        return retValue;
    }
    
    /**
     * Get the <code>Cal</code> object as a <code>java.sql.Timestamp</code>
     * @return <code>java.sql.Timestamp</code> representation of the <code>Cal</code>
     */
    public java.sql.Timestamp getSqlTimestamp() {
        java.sql.Timestamp retValue = new java.sql.Timestamp(this.getTimeInMillis());        
        return retValue;
    }
    
    /**
     * Sets the <code>Cal's</code> current time based on the number of seconds since epoch
     * should be useful for converint unix times.
     * @param seconds <code>int</code> number of seconds since epoch 
     */
    public void setSeconds(int seconds) {
        this.setTimeInMillis((long)seconds * (long)1000);
    }

    /**
     * Sets the <code>Cal's</code> current time based on the number of seconds since epoch
     * should be useful for converint unix times.
     * @param seconds <code>long</code> number of seconds since epoch 
     */
    public void setSeconds(long seconds) {
        this.setTimeInMillis(seconds * (long)1000);
    }

    /**
     * Sets the <code>Cal's</code> current time based on a <code>java.sql.Date</code>
     * @param date a <code>java.sql.Date</code> to set the time with
     */
    public void setTime(java.sql.Date date) {
        this.setTimeInMillis(date.getTime());
    }

    /**
     * Sets the <code>Cal's</code> current time based on a <code>java.sql.Time</code>
     * @param date a <code>java.sql.Time</code> to set the time with
     */
    public void setTime(java.sql.Time date) {
        this.setTimeInMillis(date.getTime());
    }

    /**
     * Sets the <code>Cal's</code> current time based on a <code>java.sql.Timestamp</code>
     * @param date a <code>java.sql.Timestamp</code> to set the time with
     */
    public void setTime(java.sql.Timestamp date) {
        this.setTimeInMillis(date.getTime());
    }
    
    /**
     * Sets the <code>Cal's</code> current time based on a <code>Calendar</code>
     * @param date a <code>java.util.Calendar</code> to set the time with
     */
    public void setTime(Calendar date) {
        this.setTimeInMillis(date.getTimeInMillis());
    }
    
    /**
     * Sets the <code>Cal's</code> current time based on a <code>Cal</code>
     * @param date a <code>org.mattyo161.commons.cal.Cal</code> to set the time with
    */
    public void setTime(Cal date) {
        this.setTimeInMillis(date.getTimeInMillis());
    }
    
    /**
     * Returns the <code>Cal</code> object as a string using the SimpleDateFormat ("MM/dd/yyyy HH:mm:ss.uuu")
     * @return <code>String</code> representation of the object
     */
    public String toString() {
        return this.formatSimpleDate("MM/dd/yyyy HH:mm:ss.SSS");
    }
    
    /**
     * Sets the Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, and Calendar.MILLISECOND to 0
     */
    public void removeTime() {
        this.set(Calendar.HOUR_OF_DAY,0);
        this.set(Calendar.MINUTE,0);
        this.set(Calendar.SECOND,0);
        this.set(Calendar.MILLISECOND,0);
    }
    
    public static String format2SimpleDateFormat(String formatString) {
         // need to check and make sure that the a is not within single quotes which can be used
         // for automatic text should probably use a regex matcher to loop between single quotes
    		// either that or we don't accept them

    		// convert the string to a SimpleDateFormat
        formatString = formatString.replace('m','M');
        formatString = formatString.replace('n','m');
        formatString = formatString.replaceAll("w{4,}","EEEE");
        formatString = formatString.replaceAll("www","EEE");
        formatString = formatString.replaceAll("ww","'##WW##'");
        formatString = formatString.replaceAll("w","'##W##'");
        formatString = formatString.replace('X','w');
        formatString = formatString.replace('u','S');
        // if the format does not contain "a" and does contain "h" then replace 'h' with 'H'
        if (formatString.indexOf('a') < 0 && formatString.indexOf('h') >= 0) {
            formatString = formatString.replace('h','H');
        }
        return formatString;
    }
    
    /**
     * Parses the specifed date string using the specified SimpleDateFormat and returns
     * a new <code>Cal</code> object. If the date could not be parsed then <code>null</code>
     * is returned
     * @return a <code>Cal</code> object representation of the passed date string; <br/>
     * <code>null</code> if string could not be parsed
     * @param theDateString the date string to parse
     * @param formatString the SimpleDateFormat string to use for parsing
     * @see <a href="#formatSimpleDate(java.lang.String)">formatSimpleDate(String)</a>
     */
    public static Cal parseSimpleDate(String theDateString, String formatString) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatString);
        
        Date tmpDate = sdf.parse(theDateString, new ParsePosition(0));
        if (tmpDate != null) {
	    		// We want to be able to access the parseFormatString so we need to create the Cal and set for formatstring
	    		Cal tmpCal = new Cal(tmpDate);
	    		tmpCal.parseFormatString = formatString;
            return tmpCal;
        }
        return null;
    }
    
    /**
     * Get the difference between two <code>Cal</code> objects in milliseconds, by subtracting the passed
     * <code>Cal</code> from the object <code>Cal</code>
     * @param cal a <code>Cal</code> to compare to the current object to get the difference in milliseconds
     * @return a <code>long</code> representing the difference in milliseconds of the two <code>Cal's</code>
     */
    public long diff(Cal cal) {
        return (this.getTimeInMillis() - cal.getTimeInMillis());
    }
    
    /**
     * Parses the specifed date string using the specified array of SimpleDateFormats in order
     * and returns a new <code>Cal</code> object. If the date could not be parsed then <code>null</code>
     * is returned
     * @return a <code>Cal</code> object representation of the passed date string; <br/>
     * <code>null</code> if string could not be parsed
     * @param theDateString the date string to parse
     * @param formatStrings an array of SimpleDateFormat strings to use for parsing theDateString
     * @see <a href="#formatSimpleDate(java.lang.String)">formatSimpleDate(String)</a>
     */
   public static Cal parseSimpleDate(String theDateString, String[] formatStrings) {
        Date tmpDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setLenient(false);
        int successfulParseNum = -1;
        for (int i = 0; i < formatStrings.length && tmpDate == null; i++) {
            sdf.applyPattern(formatStrings[i]);
            try {
                tmpDate = sdf.parse(theDateString);
                successfulParseNum = i;
            } catch (Exception e) {
                //System.out.println(e + " using \"" + formatStrings[i] + "\"");
                tmpDate = null;
            }
        }
        
       
        if (tmpDate != null) {
            // System.out.println("Successfully parsed \"" + theDateString + "\" using \"" + formatStrings[successfulParseNum] + "\"");

            // This is a little funky we are going to try and account for formats that don't have yyyy
            // by setting the year to the current year, next or previous and determine which is closest to now
            Cal tmpCal = new Cal(tmpDate);
            tmpCal.parseFormatString = formatStrings[successfulParseNum];
            if (formatStrings[successfulParseNum].indexOf("yy") < 0) {
                // the date is missing the year, lets try adding this year
                Cal today = new Cal();
                int currYear = today.get(Calendar.YEAR);
                int theYear = currYear;
                tmpCal.set(Calendar.YEAR, theYear);
                long currDiff = tmpCal.diff(today);
                // Now determine which last year is closer to the current date
                tmpCal.add(Calendar.YEAR, -1);
                if (Math.abs(currDiff) > Math.abs(tmpCal.diff(today))) {
                    theYear = currYear - 1;
                }
                // Now determine which next year is closer to the current date
                tmpCal.add(Calendar.YEAR, 2);
                if (Math.abs(currDiff) > Math.abs(tmpCal.diff(today))) {
                    theYear = currYear + 1;
                }
                tmpCal.set(Calendar.YEAR, theYear);
            }
            return tmpCal;
        }
        return null;
    }
    
    /**
     * Parses the specifed date string using the specified format and returns
     * a new <code>Cal</code> object. If the date could not be parsed then <code>null</code>
     * is returned
     * @return a <code>Cal</code> object representation of the passed date string; <br/>
     * <code>null</code> if string could not be parsed
     * @param theDateString the date string to parse
     * @param formatString the format string to use for parsing
     * @see <a href="#format(java.lang.String)">format(String)</a>
     */
    public static Cal parse(String theDateString, String formatString) {
        SimpleDateFormat sdf = new SimpleDateFormat(format2SimpleDateFormat(formatString));
        
        Date tmpDate = sdf.parse(theDateString, new ParsePosition(0));
        if (tmpDate != null) {
        		// We want to be able to access the parseFormatString so we need to create the Cal and set for formatstring
        		Cal tmpCal = new Cal(tmpDate);
        		tmpCal.parseFormatString = format2SimpleDateFormat(formatString);
            return tmpCal;
        }
        return null;
   }
    
    /**
     * Parses the specifed date string using the specified array of formats in order
     * and returns a new <code>Cal</code> object. If the date could not be parsed then <code>null</code>
     * is returned
     * @return a <code>Cal</code> object representation of the passed date string; <br/>
     * <code>null</code> if string could not be parsed
     * @param theDateString the date string to parse
     * @param formatStrings an array of SimpleDateFormat strings to use for parsing theDateString
     * @see <a href="#format(java.lang.String)">format(String)</a>
     */
    public static Cal parse(String theDateString, String[] formatStrings) {
        for (int i = 0; i < formatStrings.length; i++) {
            formatStrings[i] = format2SimpleDateFormat(formatStrings[i]);
        }
        return parseSimpleDate(theDateString, formatStrings);
   }
    
    /**
     * Parses the specifed date string using the following table of SimpleDateFormats. These are performed 
     * in order if a format could not be mathched then <code>null</code> is returned.<br />
 <table border=0 cellspacing=3 cellpadding=0>
    <tr bgcolor="#ccccff"><th align=left>SimpleDateFormat</th></tr>
    <tr bgcolor="#eeeeff"><td>"MM/dd/yyyy hh:mm:ss.SSS"</td></tr>
    <tr><td>"M/d/yy hh:mm:ss aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"M/d/yy HH:mm:ss"</td></tr>
    <tr><td>"M/d/yy hh:mm aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"M/d/yy HH:mm"</td></tr>
    <tr><td>"M/d/yy"</td></tr>
    <tr bgcolor="#eeeeff"><td>"MMM d, yyyy hh:mm:ss aa"</td></tr>
    <tr><td>"MMM d, yyyy HH:mm:ss"</td></tr>
    <tr bgcolor="#eeeeff"><td>"MMM d, yyyy hh:mm aa"</td></tr>
    <tr><td>"MMM d, yyyy HH:mm"</td></tr>
    <tr bgcolor="#eeeeff"><td>"MMM d, yyyy"</td></tr>
    <tr><td>"yyyyMMddhhmmssSSS"</td></tr>
    <tr bgcolor="#eeeeff"><td>"yyyyMMddhhmmss"</td></tr>
    <tr><td>"yyyyMMddhhmm"</td></tr>
    <tr bgcolor="#eeeeff"><td>"yyyyMMdd"</td></tr>
    <tr><td>"EEE, MMM d, yyyy hh:mm:ss aa"</td></tr>
    <tr><td>"EEE, MMM d, yyyy HH:mm:ss"</td></tr>
    <tr bgcolor="#eeeeff"><td>"EEE, MMM d, yyyy hh:mm aa"</td></tr>
    <tr><td>"EEE, MMM d, yyyy HH:mm"</td></tr>
    <tr bgcolor="#eeeeff"><td>"EEE, MMM d, yyyy"</td></tr>
    <tr><td>"yyyy-MM-dd hh:mm:ss aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"yyyy-MM-dd HH:mm:ss"</td></tr>
    <tr><td>"yyyy-MM-dd"</td></tr>
    <tr bgcolor="#eeeeff"><td>"yyyy.MM.dd hh:mm:ss aa"</td></tr>
    <tr><td>"yyyy.MM.dd HH:mm:ss"</td></tr>
    <tr bgcolor="#eeeeff"><td>"yyyy.MM.dd"</td></tr>
    <tr><td>"M.d.yy hh:mm:ss aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"M.d.yy HH:mm:ss"</td></tr>
    <tr><td>"M.d.yy hh:mm aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"M.d.yy HH:mm"</td></tr>
    <tr><td>"M.d.yy"</td></tr>
    <tr bgcolor="#eeeeff"><td>"M-d-yy hh:mm:ss aa"</td></tr>
    <tr><td>"M-d-yy HH:mm:ss"</td></tr>
    <tr bgcolor="#eeeeff"><td>"M-d-yy hh:mm aa"</td></tr>
    <tr><td>"M-d-yy HH:mm"</td></tr>
    <tr bgcolor="#eeeeff"><td>"M-d-yy"</td></tr>
    <tr><td>"yyyy-M-d h:mm:ss aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"yyyy-M-d h:mm:ss"</td></tr>
    <tr><td>"yyyy.M.d h:mm:ss aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"yyyy.M.d h:mm:ss"</td></tr>
    <tr><td>"EEE MMM d, yyyy hh:mm:ss aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"EEE MMM d, yyyy HH:mm:ss"</td></tr>
    <tr><td>"EEE MMM d, yyyy hh:mm aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"EEE MMM d, yyyy HH:mm"</td></tr>
    <tr><td>"EEE MMM d, yyyy"</td></tr>
    <tr bgcolor="#eeeeff"><td>"EEE MMM d yyyy hh:mm:ss aa"</td></tr>
    <tr><td>"EEE MMM d yyyy HH:mm:ss"</td></tr>
    <tr bgcolor="#eeeeff"><td>"EEE MMM d yyyy hh:mm aa"</td></tr>
    <tr><td>"EEE MMM d yyyy HH:mm"</td></tr>
    <tr bgcolor="#eeeeff"><td>"EEE MMM d yyyy"</td></tr>
    <tr><td>"MMM d hh:mm:ss aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"MMM d HH:mm:ss"</td></tr>
    <tr><td>"MMM d hh:mm aa"</td></tr>
    <tr bgcolor="#eeeeff"><td>"MMM d HH:mm"</td></tr>
    <tr><td>"MMM d"</td></tr>
 </table>
     * @return a <code>Cal</code> object representation of the passed date string; <br/>
     * <code>null</code> if string could not be parsed
     * @see <a href="#formatSimpleDate(java.lang.String)">formatSimpleDate(String)</a>
     */
    public static Cal parse(String theDateString) {
        
        return parseSimpleDate(theDateString, parseFormatStrings);
    }
    
    /**
     * Get the <code>SimpleDate</code> format <code>String</code> of the last successful multi-format parse
     * @return <code>SimpleDate</code> format <code>String</code>
     */
    public String getParseFromatString() {
    		return this.parseFormatString;
    }
}
