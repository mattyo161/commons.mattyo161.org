/*
 * Created on Jul 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.cal;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.mattyo161.commons.cal.Cal;

import junit.framework.TestCase;

/**
 * @author mattyo1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CalTest extends TestCase {
	// changed this to use the same list as Cal, this will ensure that it stays up to date
	String[] formatStrings = Cal.parseFormatStrings;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Class under test for void Cal()
	 */
	public void testCal() {
		
	}
	
	/*
	 * Class under test for void Cal()
	 */
	public void testCalObject() {
		// test by creating valid objects and then turning them into cals and make sure they match
	    Cal tempCal = new Cal();
	    Cal tempCalObj = new Cal();

	    // java.util.Date
	    Date tempDate = new java.util.Date();
	    tempCal = new Cal(tempDate);
	    tempCalObj = new Cal((Object) tempDate);
	    assertEquals(tempCal, tempCalObj);
	    
	    // Object cal will be used to test other functions that don't have simple constructers
	    Cal cal = new Cal();
	    
	    // java.sql.Date
	    java.sql.Date tempDate1 = new java.sql.Date(cal.getTimeInMillis() - ((3600 * 5 + 53) * 1000));
	    tempCal = new Cal(tempDate1);
	    tempCalObj = new Cal((Object) tempDate1);
	    assertEquals(tempCal, tempCalObj);
	    

	    // java.sql.Time
	    java.sql.Time tempDate2 = new java.sql.Time(cal.getTimeInMillis() - ((3600 * 5 + 53) * 1000));
	    tempCal = new Cal(tempDate2);
	    tempCalObj = new Cal((Object) tempDate2);
	    assertEquals(tempCal, tempCalObj);
	    

	    // java.sql.Timestamp
	    java.sql.Timestamp tempDate3 = new java.sql.Timestamp(cal.getTimeInMillis() - ((3600 * 5 + 53) * 1000));
	    tempCal = new Cal(tempDate3);
	    tempCalObj = new Cal((Object) tempDate3);
	    assertEquals(tempCal, tempCalObj);
	    

	    // java.lang.String
	    String tempDate4 = cal.toString();
	    tempCal = new Cal(tempDate4);
	    tempCalObj = new Cal((Object) tempDate4);
	    assertEquals(tempCal, tempCalObj);
	    assertEquals(tempCal.getParseFromatString(), tempCalObj.getParseFromatString());
	    
	    // org.mattyo161.commons.cal.Cal
	    tempCal = new Cal(cal);
	    tempCalObj = new Cal((Object) cal);
	    assertEquals(tempCal, tempCalObj);
	    
	    // Calendar
	    Calendar tempDate5 = new GregorianCalendar();
	    tempCal = new Cal(tempDate5);
	    tempCalObj = new Cal((Object) tempDate5);
	    assertEquals(tempCal, tempCalObj);
	}

	/*
	 * Class under test for void Cal(long)
	 */
	public void testCallong() {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, -5);
		cal.add(Calendar.SECOND, -53);
		Cal cal2 = new Cal(cal.getTimeInMillis());
		assertEquals(cal.getTimeInMillis(),cal2.getTimeInMillis());
	}

	/*
	 * Class under test for void Cal(String)
	 */
	public void testCalString() {
		Cal test = new Cal("3/1/2004");
		assertEquals(test.get(Calendar.YEAR), 2004);
		assertEquals(test.get(Calendar.MONTH), 2);
		assertEquals(test.get(Calendar.DAY_OF_MONTH), 1);
		assertEquals(test.get(Calendar.HOUR_OF_DAY), 0);
		assertEquals(test.get(Calendar.MINUTE), 0);
		assertEquals(test.get(Calendar.SECOND), 0);
		assertEquals(test.get(Calendar.MILLISECOND), 0);
		// added support for getting parseFormat
		assertEquals("M/d/yy", test.getParseFromatString());
		
		test = new Cal("3/1/2004 12:34:05 PM");
		assertEquals(test.get(Calendar.YEAR), 2004);
		assertEquals(test.get(Calendar.MONTH), 2);
		assertEquals(test.get(Calendar.DAY_OF_MONTH), 1);
		assertEquals(test.get(Calendar.HOUR_OF_DAY), 12);
		assertEquals(test.get(Calendar.MINUTE), 34);
		assertEquals(test.get(Calendar.SECOND), 5);
		assertEquals(test.get(Calendar.MILLISECOND), 0);
		// added support for getting parseFormat
		assertEquals("M/d/yy hh:mm:ss aa", test.getParseFromatString());
		
		test = new Cal("Thu Jun 23 07:38:25 2005");
		assertEquals(test.get(Calendar.YEAR), 2005);
		assertEquals(test.get(Calendar.MONTH), 5);
		assertEquals(test.get(Calendar.DAY_OF_MONTH), 23);
		assertEquals(test.get(Calendar.HOUR_OF_DAY), 7);
		assertEquals(test.get(Calendar.MINUTE), 38);
		assertEquals(test.get(Calendar.SECOND), 25);
		assertEquals(test.get(Calendar.MILLISECOND), 0);
		// added support for getting parseFormat
		assertEquals("EEE MMM d HH:mm:ss yyyy", test.getParseFromatString());
		
		
	}

	/*
	 * Class under test for void Cal(String, String)
	 */
	public void testCalStringString() {
		Cal tempVar = new Cal("3/1/2004", "m/d/yyyy");
		assertEquals(2004,	tempVar.get(Calendar.YEAR));
		assertEquals(2, 		tempVar.get(Calendar.MONTH));
		assertEquals(1, 		tempVar.get(Calendar.DAY_OF_MONTH));
		assertEquals(0, 		tempVar.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, 		tempVar.get(Calendar.MINUTE));
		assertEquals(0, 		tempVar.get(Calendar.SECOND));
		assertEquals(0, 		tempVar.get(Calendar.MILLISECOND));
		assertEquals(Cal.format2SimpleDateFormat("m/d/yyyy"), tempVar.getParseFromatString());

		tempVar = Cal.parseSimpleDate("3/1/2004 12:34:05 pm", "M/d/yyyy H:mm:ss a");
		assertEquals(2004,	tempVar.get(Calendar.YEAR));
		assertEquals(2, 		tempVar.get(Calendar.MONTH));
		assertEquals(1, 		tempVar.get(Calendar.DAY_OF_MONTH));
		assertEquals(12, 	tempVar.get(Calendar.HOUR_OF_DAY));
		assertEquals(34, 	tempVar.get(Calendar.MINUTE));
		assertEquals(5, 		tempVar.get(Calendar.SECOND));
		assertEquals(0, 		tempVar.get(Calendar.MILLISECOND));
		assertEquals("M/d/yyyy H:mm:ss a", tempVar.getParseFromatString());
		
		tempVar = Cal.parse("3/1/2004 12:34:05 pm", "m/d/yyyy hh:nn:ss aa");
		assertEquals(2004,	tempVar.get(Calendar.YEAR));
		assertEquals(2, 		tempVar.get(Calendar.MONTH));
		assertEquals(1, 		tempVar.get(Calendar.DAY_OF_MONTH));
		assertEquals(12, 	tempVar.get(Calendar.HOUR_OF_DAY));
		assertEquals(34, 	tempVar.get(Calendar.MINUTE));
		assertEquals(5, 		tempVar.get(Calendar.SECOND));
		assertEquals(0, 		tempVar.get(Calendar.MILLISECOND));
		assertEquals(Cal.format2SimpleDateFormat("m/d/yyyy hh:nn:ss aa"), tempVar.getParseFromatString());

		assertEquals("M/d/yyyy hh:mm:ss aa", Cal.format2SimpleDateFormat("m/d/yyyy hh:nn:ss aa"));
		//tempVar.formatSimpleDate()
		tempVar = null;
		
		tempVar = Cal.parse("3/1/2002 1:34:05 pm", "m/d/yyyy hh:nn:ss aa");
		assertEquals(2002,	tempVar.get(Calendar.YEAR));
		assertEquals(2, 		tempVar.get(Calendar.MONTH));
		assertEquals(1, 		tempVar.get(Calendar.DAY_OF_MONTH));
		assertEquals(13, 	tempVar.get(Calendar.HOUR_OF_DAY));
		assertEquals(34, 	tempVar.get(Calendar.MINUTE));
		assertEquals(5, 		tempVar.get(Calendar.SECOND));
		assertEquals(0, 		tempVar.get(Calendar.MILLISECOND));

		tempVar = new Cal("3/1/2002 1:34:05 pm", "m/d/yyyy hh:nn:ss aa");
		assertEquals(2002,	tempVar.get(Calendar.YEAR));
		assertEquals(2, 		tempVar.get(Calendar.MONTH));
		assertEquals(1, 		tempVar.get(Calendar.DAY_OF_MONTH));
		assertEquals(13, 	tempVar.get(Calendar.HOUR_OF_DAY));
		assertEquals(34, 	tempVar.get(Calendar.MINUTE));
		assertEquals(5, 		tempVar.get(Calendar.SECOND));
		assertEquals(0, 		tempVar.get(Calendar.MILLISECOND));

		tempVar = new Cal("3/1/2002 1:34:05.23 pm", "m/d/yyyy hh:nn:ss.uuu aa");
		assertEquals(2002,	tempVar.get(Calendar.YEAR));
		assertEquals(2, 		tempVar.get(Calendar.MONTH));
		assertEquals(1, 		tempVar.get(Calendar.DAY_OF_MONTH));
		assertEquals(13, 	tempVar.get(Calendar.HOUR_OF_DAY));
		assertEquals(34, 	tempVar.get(Calendar.MINUTE));
		assertEquals(5, 		tempVar.get(Calendar.SECOND));
		assertEquals(23, 		tempVar.get(Calendar.MILLISECOND));

		tempVar = new Cal("3/1/2002 1:34:05.023 pm", "m/d/yyyy hh:nn:ss.uuu aa");
		assertEquals(2002,	tempVar.get(Calendar.YEAR));
		assertEquals(2, 		tempVar.get(Calendar.MONTH));
		assertEquals(1, 		tempVar.get(Calendar.DAY_OF_MONTH));
		assertEquals(13, 	tempVar.get(Calendar.HOUR_OF_DAY));
		assertEquals(34, 	tempVar.get(Calendar.MINUTE));
		assertEquals(5, 		tempVar.get(Calendar.SECOND));
		assertEquals(23, 		tempVar.get(Calendar.MILLISECOND));

		tempVar = new Cal("3/1/2002 1:34:05.230 pm", "m/d/yyyy hh:nn:ss.uuu aa");
		assertEquals(2002,	tempVar.get(Calendar.YEAR));
		assertEquals(2, 		tempVar.get(Calendar.MONTH));
		assertEquals(1, 		tempVar.get(Calendar.DAY_OF_MONTH));
		assertEquals(13, 	tempVar.get(Calendar.HOUR_OF_DAY));
		assertEquals(34, 	tempVar.get(Calendar.MINUTE));
		assertEquals(5, 		tempVar.get(Calendar.SECOND));
		assertEquals(230, 		tempVar.get(Calendar.MILLISECOND));
	}

	/*
	 * Class under test for void Cal(Calendar)
	 */
	public void testCalCalendar() {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, -5);
		cal.add(Calendar.SECOND, -53);
		Cal cal2 = new Cal(cal);
		assertEquals(cal.getTimeInMillis(), cal2.getTimeInMillis());
	}

	/*
	 * Class under test for void Cal(java.util.Date)
	 */
	public void testCalDate() {
		java.util.Date date = new java.util.Date();
		// Make a time adjustment so that I know that when I am creating the time
		//	it is not just using the current date and time
		date.setTime(date.getTime() - ((3600 * 5 + 53) * 1000));
		Cal cal2 = new Cal(date);
		assertEquals(date.getTime(), cal2.getTimeInMillis());
	}

	/*
	 * Class under test for void Cal(java.sql.Date)
	 */
	public void testCalDate1() {
		Calendar cal = new GregorianCalendar();
		// Make a time adjustment so that I know that when I am creating the time
		//	it is not just using the current date and time
		java.sql.Date date = new java.sql.Date(cal.getTimeInMillis() - ((3600 * 5 + 53) * 1000));
		Cal cal2 = new Cal(date);
		assertEquals(date.getTime(), cal2.getTimeInMillis());
	}

	/*
	 * Class under test for void Cal(java.sql.Time)
	 */
	public void testCalTime() {
		Calendar cal = new GregorianCalendar();
		// Make a time adjustment so that I know that when I am creating the time
		//	it is not just using the current date and time
		java.sql.Time time = new java.sql.Time(cal.getTimeInMillis() - ((3600 * 5 + 53) * 1000));
		Cal cal2 = new Cal(time);
		assertEquals(time.getTime(), cal2.getTimeInMillis());
	}

	/*
	 * Class under test for void Cal(java.sql.Timestamp)
	 */
	public void testCalTimestamp() {
		Calendar cal = new GregorianCalendar();
		// Make a time adjustment so that I know that when I am creating the time
		//	it is not just using the current date and time
		java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis() - ((3600 * 5 + 53) * 1000));
		Cal cal2 = new Cal(timestamp);
		assertEquals(timestamp.getTime(), cal2.getTimeInMillis());
	}

	/*
	 * Class under test for void Cal(int, int, int)
	 */
	public void testCalintintint() {
		Cal cal = new Cal(2004,7,4);
		assertEquals(2004, cal.get(Calendar.YEAR));
		assertEquals(7, cal.get(Calendar.MONTH));
		assertEquals(4, cal.get(Calendar.DATE));
		assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, cal.get(Calendar.MINUTE));
		assertEquals(0, cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MILLISECOND));
	}

	/*
	 * Class under test for void Cal(int, int, int, int)
	 */
	public void testCalintintintint() {
		Cal cal = new Cal(2004,7,4,11);
		assertEquals(2004, cal.get(Calendar.YEAR));
		assertEquals(7, cal.get(Calendar.MONTH));
		assertEquals(4, cal.get(Calendar.DATE));
		assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, cal.get(Calendar.MINUTE));
		assertEquals(0, cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MILLISECOND));

		cal = new Cal(2004,7,4,18);
		assertEquals(2004, cal.get(Calendar.YEAR));
		assertEquals(7, cal.get(Calendar.MONTH));
		assertEquals(4, cal.get(Calendar.DATE));
		assertEquals(18, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, cal.get(Calendar.MINUTE));
		assertEquals(0, cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MILLISECOND));
		
		cal = new Cal(2004,7,4,28);
		assertEquals(2004, cal.get(Calendar.YEAR));
		assertEquals(7, cal.get(Calendar.MONTH));
		assertEquals(5, cal.get(Calendar.DATE));
		assertEquals(4, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, cal.get(Calendar.MINUTE));
		assertEquals(0, cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MILLISECOND));
	}

	/*
	 * Class under test for void Cal(int, int, int, int, int)
	 */
	public void testCalintintintintint() {
		Cal cal = new Cal(2004,7,4,11,11);
		assertEquals(2004, cal.get(Calendar.YEAR));
		assertEquals(7, cal.get(Calendar.MONTH));
		assertEquals(4, cal.get(Calendar.DATE));
		assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(11, cal.get(Calendar.MINUTE));
		assertEquals(0, cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MILLISECOND));
	}

	/*
	 * Class under test for void Cal(int, int, int, int, int, int)
	 */
	public void testCalintintintintintint() {
		Cal cal = new Cal(2004,7,4,11,11,11);
		assertEquals(2004, cal.get(Calendar.YEAR));
		assertEquals(7, cal.get(Calendar.MONTH));
		assertEquals(4, cal.get(Calendar.DATE));
		assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(11, cal.get(Calendar.MINUTE));
		assertEquals(11, cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MILLISECOND));
	}

	/*
	 * Class under test for void Cal(int, int, int, int, int, int, int)
	 */
	public void testCalintintintintintintint() {
		Cal cal = new Cal(2004,7,4,11,11,11,11);
		assertEquals(2004, cal.get(Calendar.YEAR));
		assertEquals(7, cal.get(Calendar.MONTH));
		assertEquals(4, cal.get(Calendar.DATE));
		assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(11, cal.get(Calendar.MINUTE));
		assertEquals(11, cal.get(Calendar.SECOND));
		assertEquals(11, cal.get(Calendar.MILLISECOND));
	}

	/*
	 * Class under test for boolean equals(Cal)
	 */
	public void testEqualsCal() {
		Cal cal = new Cal(2004,7,4,11,11,11);
		Cal cal2 = new Cal(2004,7,4,11,11,11);
		assertTrue(cal.equals(cal2));
	}

	/*
	 * Class under test for boolean equals(Object)
	 */
	public void testEqualsObject() {
		Cal cal = new Cal(2004,7,4,11,11,11);
		Object cal2 = new Cal(2004,7,4,11,11,11);
		Calendar cal3 = new GregorianCalendar(2004,7,4,11,11,11);
		Object cal4 = new GregorianCalendar(2004,7,4,11,11,11);
		assertTrue(cal.equals(cal2));
		assertTrue(cal.equals(cal3));
		assertTrue(cal.equals(cal4));
	}

	/*
	 * Class under test for int compareTo(Object)
	 */
	public void testCompareToObject() {
		Cal cal = new Cal();
		Cal calPlus = new Cal(cal);
		calPlus.add(Calendar.MILLISECOND,10);
		Cal calMinus = new Cal(cal);
		calMinus.add(Calendar.MILLISECOND,-10);
		Cal calSame = new Cal(cal);
		
		assertEquals(0, cal.compareTo( calSame));
		assertTrue(cal.compareTo( calPlus.getSqlTimestamp()) < 0);
		assertTrue(cal.compareTo( calMinus.getSqlDate()) > 0);
		
		Calendar cal2Plus = new GregorianCalendar();
		cal2Plus.setTimeInMillis(cal.getTimeInMillis());
		cal2Plus.add(Calendar.MILLISECOND, 10);
		Calendar cal2Minus = new GregorianCalendar();
		cal2Minus.setTimeInMillis(cal.getTimeInMillis());
		cal2Minus.add(Calendar.MILLISECOND, -10);
		
		assertEquals(0, cal.compareTo( calSame));
		assertTrue(cal.compareTo( cal2Plus) < 0);
		assertTrue(cal.compareTo( cal2Minus) > 0);
		
	}

	public void testFormatSimpleDate() {
		Cal cal = new Cal(2004,7,8,18,5,23,30);

		assertEquals("2004.08.08 AD at 18:05:23", 
				cal.formatSimpleDate("yyyy.MM.dd G 'at' HH:mm:ss"));
		assertEquals("Sun, Aug 8, '04", 
				cal.formatSimpleDate("EEE, MMM d, ''yy"));
		assertEquals("6 o'clock PM", 
				cal.formatSimpleDate("h 'o''clock' a"));
		assertEquals("6:05 PM", 
				cal.formatSimpleDate("K:mm a"));
		assertEquals("02004.August.08 AD 06:05 PM", 
				cal.formatSimpleDate("yyyyy.MMMM.dd GGG hh:mm aaa"));
		assertEquals("Sun, 8 Aug 2004 18:05:23", 
				cal.formatSimpleDate("EEE, d MMM yyyy HH:mm:ss"));
		assertEquals("2nd Sunday of August", 
				cal.formatSimpleDate("F'nd' EEEE 'of' MMMM"));
		
		assertEquals("AD",
				cal.formatSimpleDate("G"));
		assertEquals("04 04 04 2004 02004",
				cal.formatSimpleDate("y yy yyy yyyy yyyyy"));
		assertEquals("8 08 Aug August",
				cal.formatSimpleDate("M MM MMM MMMM"));
		assertEquals("33 33 033",
				cal.formatSimpleDate("w ww www"));
		assertEquals("2 02 002",
				cal.formatSimpleDate("W WW WWW"));
		assertEquals("221",
				cal.formatSimpleDate("D"));
		assertEquals("8 08 008",
				cal.formatSimpleDate("d dd ddd"));
		// not really sure what this one is
		assertEquals("2",
				cal.formatSimpleDate("F"));
		cal.add(Calendar.DATE,1);
		assertEquals("2",
				cal.formatSimpleDate("F"));
		cal.add(Calendar.DATE,-1);
		assertEquals("Sun Sun Sun Sunday",
				cal.formatSimpleDate("E EE EEE EEEE"));
		assertEquals("PM",
				cal.formatSimpleDate("a"));
		assertEquals("18 18 018",
				cal.formatSimpleDate("H HH HHH"));
		assertEquals("18 18 018",
				cal.formatSimpleDate("k kk kkk"));
		assertEquals("6 06 006",
				cal.formatSimpleDate("K KK KKK"));
		assertEquals("6 06 006",
				cal.formatSimpleDate("h hh hhh"));
		assertEquals("5 05 005",
				cal.formatSimpleDate("m mm mmm"));
		assertEquals("23 23 023",
				cal.formatSimpleDate("s ss sss"));
		assertEquals("30 30 030",
				cal.formatSimpleDate("S SS SSS"));
	}

	public void testFormat() {
		Cal cal = new Cal(2004,7,8,18,5,23,30);

//		assertEquals("2004.08.08 AD at 18:05:23", 
//				cal.format("yyyy.mm.dd G 'at' hh:nn:ss"));
		assertEquals("2004.08.08 AD 18:05:23", 
				cal.format("yyyy.mm.dd G hh:nn:ss"));
		assertEquals("Sun, Aug 8, '04", 
				cal.format("www, mmm d, ''yy"));
		assertEquals("6 o'clock PM", 
				cal.format("h 'o''clock' a"));
		assertEquals("6:05 PM", 
				cal.format("K:nn a"));
		assertEquals("02004.August.08 AD 06:05 PM", 
				cal.format("yyyyy.mmmm.dd GGG hh:nn aaa"));
		assertEquals("Sun, 8 Aug 2004 18:05:23", 
				cal.format("www, d mmm yyyy hh:nn:ss"));
		
		assertEquals("AD",
				cal.format("G"));
		assertEquals("04 04 04 2004 02004",
				cal.format("y yy yyy yyyy yyyyy"));
		assertEquals("8 08 Aug August",
				cal.format("m mm mmm mmmm"));
		assertEquals("1 01 Sun Sunday",
				cal.format("w ww www wwww"));
		// This means the second week of the month
		assertEquals("2 02 002",
				cal.format("W WW WWW"));
		assertEquals("221",
				cal.format("D"));
		assertEquals("8 08 008",
				cal.format("d dd ddd"));
		// this means it is the second sunday of the month
		assertEquals("2",
				cal.format("F"));
		cal.add(Calendar.DATE,1);
		assertEquals("2",
				cal.format("F"));
		cal.add(Calendar.DATE,-1);
		assertEquals("Sun Sun Sun Sunday",
				cal.format("E EE EEE EEEE"));
		assertEquals("PM",
				cal.format("a"));
		assertEquals("18 18 018",
				cal.format("H HH HHH"));
		assertEquals("18 18 018",
				cal.format("k kk kkk"));
		assertEquals("6 06 006",
				cal.format("K KK KKK"));
		assertEquals("18 18 018",
				cal.format("h hh hhh"));
		assertEquals("6 06 006 PM",
				cal.format("h hh hhh a"));
		assertEquals("5 05 005",
				cal.format("n nn nnn"));
		assertEquals("23 23 023",
				cal.format("s ss sss"));
		assertEquals("30 30 030",
				cal.format("S SS SSS"));
		assertEquals("30 30 030",
				cal.format("u uu uuu"));
	}

	public void testGetSqlDate() {
		Cal cal = new Cal();
		java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());
		assertEquals(sqlDate, cal.getSqlDate());
	}

	public void testGetNormalDate() {
		Cal cal = new Cal(2004, 7, 1);
		assertEquals("08/01/2004", cal.getNormalDate());
	}

	public void testGetDate() {
		Cal cal = new Cal();
		java.util.Date utilDate = new java.util.Date(cal.getTimeInMillis());
		assertEquals(utilDate, cal.getSqlDate());
	}

	public void testGetSqlTime() {
		Cal cal = new Cal();
		java.sql.Time sqlTime = new java.sql.Time(cal.getTimeInMillis());
		assertEquals(sqlTime, cal.getSqlTime());
	}

	public void testGetSqlTimestamp() {
		Cal cal = new Cal();
		java.sql.Timestamp sqlTime = new java.sql.Timestamp(cal.getTimeInMillis());
		assertEquals(sqlTime, cal.getSqlTimestamp());
	}

	/*
	 * Class under test for void setSeconds(int)
	 */
	public void testSetSecondsint() {
		Cal cal = new Cal();
		cal.setSeconds((int) 10000);
		assertEquals(10000 * 1000, cal.getTimeInMillis());
	}

	/*
	 * Class under test for void setSeconds(long)
	 */
	public void testSetSecondslong() {
		Cal cal = new Cal();
		cal.set(Calendar.MILLISECOND, 0);
		Cal cal2 = new Cal(2004,8,1,12,43,56,234);
		cal2.setSeconds(cal.getTimeInMillis()/1000);
		assertEquals(cal, cal2);
	}

	/*
	 * Class under test for void setTime(java.sql.Date)
	 */
	public void testSetTimeDate() {
		java.sql.Date date = new java.sql.Date(new Cal().getTimeInMillis());
		Cal cal = new Cal(2004,7,1,12,54,34,23);
		cal.setTime(date);
		assertEquals(date.getTime(), cal.getTimeInMillis());
		assertEquals(date, cal.getSqlDate());
	}

	/*
	 * Class under test for void setTime(java.sql.Time)
	 */
	public void testSetTimeTime() {
		java.sql.Time date = new java.sql.Time(new Cal().getTimeInMillis());
		Cal cal = new Cal(2004,7,1,12,54,34,23);
		cal.setTime(date);
		assertEquals(date.getTime(), cal.getTimeInMillis());
		assertEquals(date, cal.getSqlTime());
	}

	/*
	 * Class under test for void setTime(java.sql.Timestamp)
	 */
	public void testSetTimeTimestamp() {
		java.sql.Timestamp date = new java.sql.Timestamp(new Cal().getTimeInMillis());
		Cal cal = new Cal(2004,7,1,12,54,34,23);
		cal.setTime(date);
		assertEquals(date.getTime(), cal.getTimeInMillis());
		assertEquals(date, cal.getSqlTimestamp());
	}

	/*
	 * Class under test for void setTime(Calendar)
	 */
	public void testSetTimeCalendar() {
		Calendar date = new GregorianCalendar();
		Cal cal = new Cal(2004,7,1,12,54,34,23);
		cal.setTime(date);
		assertEquals(date.getTimeInMillis(), cal.getTimeInMillis());
		assertEquals(date, (Calendar) cal);
	}

	/*
	 * Class under test for void setTime(Cal)
	 */
	public void testSetTimeCal() {
		Cal date = new Cal();
		Cal cal = new Cal(2004,7,1,12,54,34,23);
		cal.setTime(date);
		assertEquals(date.getTimeInMillis(), cal.getTimeInMillis());
		assertEquals(date, cal);
	}

	/*
	 * Class under test for String toString()
	 */
	public void testToString() {
		Cal cal = new Cal(2004,7,1,13,54,34,23);
		assertEquals("08/01/2004 13:54:34.023", cal.toString());
	}

	public void testRemoveTime() {
		Cal cal = new Cal(2004,7,1,13,54,34,23);
		cal.removeTime();
		assertEquals("08/01/2004 00:00:00.000", cal.toString());
		assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, cal.get(Calendar.MINUTE));
		assertEquals(0, cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MILLISECOND));
	}

	/*
	 * Class under test for Cal parseSimpleDate(String, String)
	 */
	public void testParseSimpleDateStringString() {
        // setup a time for testing
        Cal cal = new Cal();
        System.out.println("\nFormatting and parsing date " + cal + " using parseSimpleDate.");
        for (int i = 0; i < formatStrings.length; i++) {
        		String format = formatStrings[i];
        		String str1 = cal.formatSimpleDate(format);
        		Cal parsedCal = Cal.parseSimpleDate(str1, format);
        		String str2 = parsedCal.formatSimpleDate(format);
        		System.out.println(format + " : " + str1 + " = " + str2);
        		// make sure that the formated dates match
        		assertEquals(str1, str2);
        }
	}

	public void testDiff() {
		Cal cal = new Cal();
		Cal calMinus = new Cal(cal);
		Cal calPlus = new Cal(cal);
		calMinus.add(Calendar.MILLISECOND,-546743);
		calPlus.add(Calendar.MILLISECOND,546743);
		assertEquals(546743, cal.diff(calMinus));
		assertEquals(-546743, cal.diff(calPlus));
	}

	/*
	 * Class under test for Cal parseSimpleDate(String, String[])
	 */
	public void testParseSimpleDateStringStringArray() {
	}
	/*
	 * Class under test for Cal parse(String, String)
	 */
	public void testParseStringString() {
		
	}

	/*
	 * Class under test for Cal parse(String, String[])
	 */
	public void testParseStringStringArray() {
	}

	/*
	 * Class under test for Cal parse(String)
	 */
	public void testParseString() {
        // setup a time for testing
        Cal cal = new Cal();
        System.out.println("\nFormatting and parsing date " + cal + " using parse function.");
        for (int i = 0; i < formatStrings.length; i++) {
        		String format = formatStrings[i];
        		// format the calendar to the current format
        		String str1 = cal.formatSimpleDate(format);
        		// now parse it using the generic parse
        		Cal parsedCal = Cal.parse(str1);
        		// get the format that was used to parse the string
        		String parseFormat = parsedCal.getParseFromatString();
        		// format the newly parsed string
        		String str2 = parsedCal.formatSimpleDate(format);
        		System.out.print(str1 + " = " + str2 + " (" + parseFormat + ")");
        		if (format.equals(parseFormat)) {
        			System.out.print("\n");
        		} else {
        			System.out.println(" was " + format);
        		}
        		// make sure that the formated dates match
        		assertEquals(str1, str2);
        		// also make sure that the Cals match
        		// can't really do this because of issues with information missing from the formated value
        		// assertEquals(cal, parsedCal);
        		// and the format matches as well
        		//assertEquals(format, parseFormat);
        }
	}

}
