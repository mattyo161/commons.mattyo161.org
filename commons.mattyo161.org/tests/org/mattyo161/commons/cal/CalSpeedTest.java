/*
 * Created on Aug 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.cal;

import java.io.*;
import java.text.DecimalFormat;
import java.util.zip.*;

import junit.framework.TestCase;

/**
 * @author mattyo1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CalSpeedTest extends TestCase {
	/*
	 * Test parseSimpleDateFormat when we specify the format used
	 */
	
	protected static boolean verifyParse = true;
	
	public void testParseSimpleDateFormat() {
		
		String test = "May 30 21:50:51 mattpb /System/Library/CoreServices/Software Update.app/Contents/MacOS/Software Update: ./Applications/Internet Connect.app/Contents/Resources/L2TP.bundle/Contents/Resources/German.lproj";
		Cal cal = Cal.parseSimpleDate(test, "MMM dd HH:mm:ss");
		System.out.println(cal + " using SimpleFormat MMM dd HH:mm:ss");
		assertEquals("May 30 21:50:51", cal.formatSimpleDate("MMM dd HH:mm:ss"));
		
		cal = Cal.parseSimpleDate(test, "MMM dd HH:mm:ss");
		assertEquals("May 30 21:50:51", cal.formatSimpleDate("MMM dd HH:mm:ss"));
		cal = Cal.parse(test);
		System.out.println(cal + " using parse " + cal.getParseFromatString());
		assertEquals("May 30 21:50:51", cal.formatSimpleDate("MMM dd HH:mm:ss"));
		
		test = "127.0.0.1 - - [08/Aug/2004:15:57:33 -0400] \"GET /manuals/oreilly_books/books/corejava2vol1/images/0130471771/graphics/12fig03.gif HTTP/1.1\" 200 9739";
		//test = "08/Aug/2004:15:57:33 -0400] \"GET /manuals/oreilly_books/books/corejava2vol1/images/0130471771/graphics/12fig03.gif HTTP/1.1\" 200 9739";
		String[] dateStr = test.split("[\\[\\]]");
		cal = Cal.parseSimpleDate(dateStr[1], "dd/MMM/yyyy:HH:mm:ss");
		System.out.println(cal + " using parseSimpleDate dd/MMM/yyyy:HH:mm:ss");
		assertEquals("08/Aug/2004:15:57:33", cal.formatSimpleDate("dd/MMM/yyyy:HH:mm:ss"));
	}
	
	public void testParseSimpleWindowServerLog() {
		System.out.println("Parsing tests/windowserver.log.gz using SimpleDateFormat MMM dd HH:mm:ss");
		try {
			//BufferedReader input = new BufferedReader(new FileReader("tests/windowserver.log"));
			BufferedReader input = new BufferedReader(
					new InputStreamReader(
							new GZIPInputStream(
									new FileInputStream("tests/windowserver.log.gz"))));
			try {
				String currLine = input.readLine();
				Cal startTime = new Cal();
				long numLines = 0;
				long errors = 0;
				while (currLine != null) {
					numLines++;
					Cal cal = Cal.parseSimpleDate(currLine, "MMM dd HH:mm:ss");
					if (cal == null) {
						errors++;
						System.out.println("Error parsing: " + currLine);
					} else if (verifyParse && (!currLine.startsWith(cal.formatSimpleDate("MMM dd HH:mm:ss")))) {
						errors++;
						// System.out.println("Parse Error " + dateStr[1] + " != " + cal.format("dd/mmm/yyyy:hh:nn:ss"));
					}
					/*
					if (numLines % 5000 == 0) {
						System.out.println("Processed " + numLines + 
								" lines with " + errors + 
								" errors in " + ((new Cal().diff(startTime)) / 1000.0) + " seconds.");
					}
					*/
					currLine = input.readLine();
				}
				Cal endTime = new Cal();
				long millis = endTime.diff(startTime);
				System.out.println("Processing of " + numLines + 
						" lines with " + errors + 
						" errors took " + (millis / 1000.0) + " seconds.");
				System.out.println("\ta rate of " + 
						new DecimalFormat("#,##0.00").format(numLines / (millis / 1000.0)) + " lines/second");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testParseWindowServerLog() {
		System.out.println("Parsing tests/windowserver.log.gz using parse");
		try {
			//BufferedReader input = new BufferedReader(new FileReader("tests/windowserver.log"));
			BufferedReader input = new BufferedReader(
					new InputStreamReader(
							new GZIPInputStream(
									new FileInputStream("tests/windowserver.log.gz"))));
			try {
				String currLine = input.readLine();
				Cal startTime = new Cal();
				long numLines = 0;
				long errors = 0;
				while (currLine != null) {
					numLines++;
					Cal cal = Cal.parse(currLine);
					if (cal == null) {
						errors++;
						System.out.println("Error parsing: " + currLine);
					} else if (verifyParse && (!currLine.startsWith(cal.formatSimpleDate(cal.getParseFromatString())))) {
						errors++;
						System.out.println("Parse Error " + currLine.substring(0, 15) + " != " + cal.format(cal.getParseFromatString()));
					}
					/*
					if (numLines % 5000 == 0) {
						System.out.println("Processed " + numLines + 
								" lines with " + errors + 
								" errors in " + ((new Cal().diff(startTime)) / 1000.0) + " seconds.");
					}
					*/
					currLine = input.readLine();
				}
				Cal endTime = new Cal();
				long millis = endTime.diff(startTime);
				System.out.println("Processing of " + numLines + 
						" lines with " + errors + 
						" errors took " + (millis / 1000.0) + " seconds.");
				System.out.println("\ta rate of " + 
						new DecimalFormat("#,##0.00").format(numLines / (millis / 1000.0)) + " lines/second");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public void testParse2WindowServerLog() {
		System.out.println("Parsing tests/windowserver.log.gz using parse with format cache");
		try {
			//BufferedReader input = new BufferedReader(new FileReader("tests/windowserver.log"));
			BufferedReader input = new BufferedReader(
					new InputStreamReader(
							new GZIPInputStream(
									new FileInputStream("tests/windowserver.log.gz"))));
			try {
				String currLine = input.readLine();
				Cal startTime = new Cal();
				long numLines = 0;
				long errors = 0;
				String savedFormat = "";
				Cal cal = null;
				while (currLine != null) {
					numLines++;
					if (savedFormat.equals("")) {
						cal = Cal.parse(currLine);
						savedFormat = cal.getParseFromatString();
					} else {
						cal = Cal.parse(currLine,savedFormat);
					}
					if (cal == null) {
						errors++;
						System.out.println("Error parsing: " + currLine);
					} else if (verifyParse && (!currLine.startsWith(cal.formatSimpleDate(cal.getParseFromatString())))) {
						errors++;
						// System.out.println("Parse Error " + dateStr[1] + " != " + cal.format("dd/mmm/yyyy:hh:nn:ss"));
					}
					/*
					if (numLines % 5000 == 0) {
						System.out.println("Processed " + numLines + 
								" lines with " + errors + 
								" errors in " + ((new Cal().diff(startTime)) / 1000.0) + " seconds.");
					}
					*/
					currLine = input.readLine();
				}
				Cal endTime = new Cal();
				long millis = endTime.diff(startTime);
				System.out.println("Processing of " + numLines + 
						" lines with " + errors + 
						" errors took " + (millis / 1000.0) + " seconds.");
				System.out.println("\ta rate of " + 
						new DecimalFormat("#,##0.00").format(numLines / (millis / 1000.0)) + " lines/second");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public void testParseWindowServerLog2() {
		System.out.println("Parsing tests/windowserver.log.gz using parse format mmm dd hh:nn:ss");
		try {
			//BufferedReader input = new BufferedReader(new FileReader("tests/windowserver.log"));
			BufferedReader input = new BufferedReader(
					new InputStreamReader(
							new GZIPInputStream(
									new FileInputStream("tests/windowserver.log.gz"))));
			try {
				String currLine = input.readLine();
				Cal startTime = new Cal();
				long numLines = 0;
				long errors = 0;
				while (currLine != null) {
					numLines++;
					Cal cal = Cal.parse(currLine, "mmm dd hh:nn:ss");
					if (cal == null) {
						errors++;
						System.out.println("Error parsing: " + currLine);
					} else if (verifyParse && (!currLine.startsWith(cal.format("mmm dd hh:nn:ss")))) {
						errors++;
						// System.out.println("Parse Error " + dateStr[1] + " != " + cal.format("dd/mmm/yyyy:hh:nn:ss"));
					}
					/*
					if (numLines % 5000 == 0) {
						System.out.println("Processed " + numLines + 
								" lines with " + errors + 
								" errors in " + ((new Cal().diff(startTime)) / 1000.0) + " seconds.");
					}
					*/
					currLine = input.readLine();
				}
				Cal endTime = new Cal();
				long millis = endTime.diff(startTime);
				System.out.println("Processing of " + numLines + 
						" lines with " + errors + 
						" errors took " + (millis / 1000.0) + " seconds.");
				System.out.println("\ta rate of " + 
						new DecimalFormat("#,##0.00").format(numLines / (millis / 1000.0)) + " lines/second");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testParseAccessLog() {
		System.out.println("Parsing tests/access_log.gz using parse format dd/mmm/yyyy:hh:nn:ss");
		try {
			//BufferedReader input = new BufferedReader(new FileReader("tests/access_log"));
			BufferedReader input = new BufferedReader(
					new InputStreamReader(
							new GZIPInputStream(
									new FileInputStream("tests/access_log.gz"))));
			try {
				String currLine = input.readLine();
				Cal startTime = new Cal();
				long numLines = 0;
				long errors = 0;
				while (currLine != null) {
					numLines++;
					String[] dateStr = currLine.split("[\\[\\]]");
					Cal cal = Cal.parse(dateStr[1], "dd/mmm/yyyy:hh:nn:ss");
					if (cal == null) {
						errors++;
						//	System.out.println("Error parsing: " + currLine);
					} else if (verifyParse && (!dateStr[1].startsWith(cal.format("dd/mmm/yyyy:hh:nn:ss")))) {
						errors++;
						// System.out.println("Parse Error " + dateStr[1] + " != " + cal.format("dd/mmm/yyyy:hh:nn:ss"));
					}
					/*
					if (numLines % 5000 == 0) {
						System.out.println("Processed " + numLines + 
								" lines with " + errors + 
								" errors in " + ((new Cal().diff(startTime)) / 1000.0) + " seconds.");
					}
					*/
					currLine = input.readLine();
				}
				Cal endTime = new Cal();
				long millis = endTime.diff(startTime);
				System.out.println("Processing of " + numLines + 
						" lines with " + errors + 
						" errors took " + (millis / 1000.0) + " seconds.");
				System.out.println("\ta rate of " + 
						new DecimalFormat("#,##0.00").format(numLines / (millis / 1000.0)) + " lines/second");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}



/* Results on iBook G4

With verification turned on:
05/30/1970 21:50:51.000 using SimpleFormat MMM dd HH:mm:ss
05/30/2004 21:50:51.000 using parse MMM d HH:mm:ss
08/08/2004 15:57:33.000 using parseSimpleDate dd/MMM/yyyy:HH:mm:ss
Parsing tests/windowserver.log.gz using SimpleDateFormat MMM dd HH:mm:ss
Processing of 20000 lines with 0 errors took 8.72 seconds.
	a rate of 2,293.58 lines/second
Parsing tests/windowserver.log.gz using parse
Processing of 20000 lines with 36 errors took 78.565 seconds.
	a rate of 254.57 lines/second
Parsing tests/windowserver.log.gz using parse with format cache
Processing of 20000 lines with 1 errors took 7.862 seconds.
	a rate of 2,543.88 lines/second
Parsing tests/windowserver.log.gz using parse format mmm dd hh:nn:ss
Processing of 20000 lines with 0 errors took 10.5 seconds.
	a rate of 1,904.76 lines/second
Parsing tests/access_log.gz using parse format dd/mmm/yyyy:hh:nn:ss
Processing of 48309 lines with 0 errors took 29.746 seconds.
	a rate of 1,624.05 lines/second


With verification off:
05/30/1970 21:50:51.000 using SimpleFormat MMM dd HH:mm:ss
05/30/2004 21:50:51.000 using parse MMM d HH:mm:ss
08/08/2004 15:57:33.000 using parseSimpleDate dd/MMM/yyyy:HH:mm:ss
Parsing tests/windowserver.log.gz using SimpleDateFormat MMM dd HH:mm:ss
Processing of 20000 lines with 0 errors took 5.256 seconds.
	a rate of 3,805.18 lines/second
Parsing tests/windowserver.log.gz using parse
Processing of 20000 lines with 0 errors took 75.518 seconds.
	a rate of 264.84 lines/second
Parsing tests/windowserver.log.gz using parse with format cache
Processing of 20000 lines with 0 errors took 6.208 seconds.
	a rate of 3,221.65 lines/second
Parsing tests/windowserver.log.gz using parse format mmm dd hh:nn:ss
Processing of 20000 lines with 0 errors took 5.907 seconds.
	a rate of 3,385.81 lines/second
Parsing tests/access_log.gz using parse format dd/mmm/yyyy:hh:nn:ss
Processing of 48309 lines with 0 errors took 18.328 seconds.
	a rate of 2,635.80 lines/second

*/
