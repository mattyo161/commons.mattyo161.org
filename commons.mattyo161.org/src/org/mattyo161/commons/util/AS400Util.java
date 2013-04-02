/*
 * AS400Util.java
 *
 * Created on April 30, 2004, 9:35 AM
 */

package org.mattyo161.commons.util;

import org.mattyo161.commons.cal.*;

import java.sql.*;
import java.util.*;

/**
 *
 * @author  mattyo1
 */
public class AS400Util {
    
    /** Creates a new instance of AS400Util */
    public AS400Util() {
    }
    
    /**
     Take the AS400 cent, year, month, and day columns and convert them into a Cal object
     @param cent century 0-9 0 = 1900, 1 = 2000, 9 used for permanent dates will become 2800, if cent is >= 10 then it is actually
     century so 19 = 1900, 20 = 2000, etc.
     @param year the year from 0 - 99
     @param month the month from 1 - 12
     @param day the day from 1 - 31
     @retrun a Cal object representing the date
     */
    public static Cal Date2Cal(int cent, int year, int month, int day) {
    		if (cent >= 10) {
    			return new Cal((cent * 100) + year, month - 1, day);
    		} else {
    			return new Cal((cent * 100) + 1900 + year, month - 1, day);
    		}
    }

    /**
     Take the AS400 year, month, and day columns and convert them into a Cal object, it will determine
     the appropriate century based on the year, 0 - 50 = 2000 51-99 = 1900
     @param year the year from 0 - 99
     @param month the month from 1 - 12
     @param day the day from 1 - 31
     @retrun a Cal object representing the date
     */
    public static Cal Date2Cal(int year, int month, int day) {
		if (year == 0 && month == 0 && day == 0) {
			return new Cal(1970,1,1);
		}
        if (year <= 50) {
            year += 2000;
        } else {
            year += 1900;
        }
        Cal returnValue = new Cal(year, month - 1, day);
        return returnValue;
    }

    /**
     Take the AS400 date field in the form of an int which is in the form mddyy and convert it to a Cal
     it will determine the century based on the year, 0 - 50 = 2000 51-99 = 1900
     @param date an AS400 date integer
     @retrun a Cal object representing the date
     */
    public static Cal Date2Cal(int date) {
        int year = date % 100;
        int month = date / 10000;
        int day = (date % 10000) / 100;
        if (year <= 50) {
            year += 2000;
        } else {
            year += 1900;
        }
        
        if (date == 0) {
            year = 1970;
            month = 1; 
            day = 1;
        }
        
        Cal returnValue = new Cal(year, month - 1, day);
        return returnValue;
    }

    /**
     Take the AS400 date adn time field in the form of 2 ints which are in the form mddyy and hmmss
     convert them to a Cal it will determine the century based on the year, 0 - 50 = 2000 51-99 = 1900
     @param date an AS400 date integer
     @param time an AS400 time integer
     @retrun a Cal object representing the date
     */
    public static Cal Date2Cal(int date, int time) {
        int year = date % 100;
        int month = date / 10000;
        int day = (date % 10000) / 100;
        if (year <= 50) {
            year += 2000;
        } else {
            year += 1900;
        }
        int hour = time / 10000;
        int minute = (time % 10000) / 100;
        int second = time % 100;

        if (date == 0) {
            year = 1970;
            month = 1; 
            day = 1;
        }
        
        Cal returnValue = new Cal(year, month - 1, day, hour, minute, second);
        
        return returnValue;
    }

    /**
     Take a row from an AS400 ResultSet and try build a date from it given a field key.
     It will attempt to find the appropriate fields by checking for fields like 
     <key>YY, <key>YR, <key>Y,<key>MM, <key>MO, <key>M, <key>CC, <key>DT, it will then
     execute the appropriate Date2Cal function to return the correct Cal
     @param rs an AS400 ResultSet at the row you want to extract the date from
     @param key the key name for the field you want to get the date from
     @retrun a Cal object representing the date
     */
    public static Cal Date2Cal(java.sql.ResultSet rs, String key) throws SQLException {
        int cent = 0;
        int year = 0;
        int month = 0;
        int day = 0;
        int date = 0;
        int format = 0;  // 1 = cent, year, month, day, 2 = year, month, day, 3 = date
        key = key.toLowerCase();
        Cal returnValue = null;
        
        // build a list of columnNames from the result set
        ResultSetMetaData rsmd = rs.getMetaData();
        Set colNames = new TreeSet();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            colNames.add(rsmd.getColumnName(i).toLowerCase());
        }
        
        // now we need to check for our types
        if (colNames.contains(key + "yy")) {
            year = rs.getInt(key + "yy");
            format = 2;
            // check for a century
            if (colNames.contains(key + "cc")) {
                cent = rs.getInt(key + "cc");
                format = 1;
            }
        } else if (colNames.contains(key + "yr")) {
            year = rs.getInt(key + "yr");
            format = 2;
            // check for a century
            if (colNames.contains(key + "cn")) {
                cent = rs.getInt(key + "cn");
                format = 1;
            }
        } else if (colNames.contains(key + "dt")) {
            date = rs.getInt(key + "dt");
            format = 3;
        } else if (colNames.contains(key + "y")) {
            year = rs.getInt(key + "y");
            format = 2;
        }
        
        if (format < 3) {
            if (colNames.contains(key + "mm")) {
                month = rs.getInt(key + "mm");
            } else if (colNames.contains(key + "mo")) {
                month = rs.getInt(key + "mo");
            } else if (colNames.contains(key + "m")) {
                month = rs.getInt(key + "m");
            }
            if (colNames.contains(key + "dd")) {
                day = rs.getInt(key + "dd");
            } else if (colNames.contains(key + "da")) {
                day = rs.getInt(key + "da");
            } else if (colNames.contains(key + "d")) {
                day = rs.getInt(key + "d");
            }
        }
        
        switch (format) {
            case 1:
                returnValue = Date2Cal(cent, year, month, day);
                break;
            case 2:
                returnValue = Date2Cal(year, month, day);
                break;
            case 3:
                returnValue = Date2Cal(date);
                break;
        }
        
        return returnValue;
    }

}
