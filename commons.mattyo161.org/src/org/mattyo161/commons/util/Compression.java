/*
 * Created on Feb 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mattyo161.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author dcs
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Compression {

	public static String compressString(String uncompressed) {
		return new String(compressByteArray(uncompressed.getBytes()));
	}
	
	public static byte[] compressByteArray(byte[] uncompressed) {
		 ByteArrayOutputStream os = new ByteArrayOutputStream();
		 try {
		 	GZIPOutputStream gzos = new GZIPOutputStream(os,uncompressed.length);
		 	gzos.write(uncompressed);
		 	gzos.close();
		 	os.close();
		 } catch (IOException e) {
		 	e.printStackTrace();
		 }
		 return os.toByteArray();
	}
	
	public static String uncompressString(String compressed) {
		return new String(uncompressByteArray(compressed.getBytes()));
	}
	public static byte[] uncompressByteArray(byte[] compressed) {
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		try {
			GZIPInputStream gzis = new GZIPInputStream(is,compressed.length);
			byte[] buf = new byte[1024];
			int len;
			while ((len = gzis.read(buf)) > 0) {
				os.write(buf,0,len);
			}
			os.close();
			gzis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return os.toByteArray();	
	}
	
	public static InputStream uncompressStream(InputStream input) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		try {
			GZIPInputStream gzis = new GZIPInputStream(input);
			byte[] buf = new byte[1024];
			int len;
			while ((len = gzis.read(buf)) > 0) {
				os.write(buf,0,len);
			}
			os.close();
			gzis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(os.toByteArray());
	}
}
