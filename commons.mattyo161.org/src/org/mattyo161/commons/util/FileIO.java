/*
 * Created on Feb 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.FileChannel;

/**
 * @author mattyo1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileIO {
	private static final int BLKSIZ = 16384;
	
	public static String readerToString(Reader theReader) throws IOException {
		StringBuffer buff = new StringBuffer();
		char[] b = new char[BLKSIZ];
		int n;
		
		// Read a block if we get some characters then append them
		while ( (n = theReader.read(b)) > 0 ) {
			buff.append(b, 0, n);
		}
		
		// Only build the string once when we return the value
		return buff.toString();
	}
	
	public static String fileToString(File theFile) throws IOException {
		Reader theReader = new FileReader(theFile);
		return readerToString(theReader);
	}
	
	public static String fileToString(String filePath) throws IOException {
		File theFile = new File(filePath);
		Reader theReader = new FileReader(theFile);
		return readerToString(theReader);
	}
	
	public static String fileToString(String filePath, String encoding) throws IOException {
		File theFile = new File(filePath);
		Reader theReader = new InputStreamReader(new FileInputStream(theFile), encoding);
		return readerToString(theReader);
		
	}
	
	public static String inputStreamToString(InputStream is) throws IOException {
		return readerToString(new InputStreamReader(is));
	}
	
	public static void copyFile(File in, File out) throws Exception {
	     FileChannel sourceChannel = new FileInputStream(in).getChannel();
	     FileChannel destinationChannel = new FileOutputStream(out).getChannel();
	     sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
	     // or
	     //  destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	     sourceChannel.close();
	     destinationChannel.close();
	}

}
