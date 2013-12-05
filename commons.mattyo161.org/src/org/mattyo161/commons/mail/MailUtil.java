package org.mattyo161.commons.mail;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.xbill.DNS.Address;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;
      
public class MailUtil {
	
	
	public static boolean sendMail(String toAddress, String fromAddress, String message, String subject) {
		boolean retVal = false;
		try {
			// Clean up the toAddress, if there are multiple addresses then we need to split them into a list
			String[] toAddresses = toAddress.trim().split("[\\s,]+");
			SMTPClient client = new SMTPClient();
			client.connect("localhost");
			message =  "Subject: " + subject + "\r\n" + "From:" + fromAddress + "\r\ncc:\r\nbcc:\r\n\r\n" +
						 message + "\r\n\r\n";
			retVal = client.sendSimpleMessage(fromAddress, toAddresses, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}
	
	/**
	 * Take a Collection of email addresses and return a Collection of 
	 * CheckedMailAddress objects, showing the validity of the email addresses
	 * 
	 * @param addresses a Collection of mail address strings
	 * @return a Collection of CheckedMailAddress objects
	 */
	public static Collection checkAddresses(Collection addresses) {
		Map addressByDomain = getAddressesByDomain(addresses);
		// check for any errors
		Collection returnValue = new Vector();
		
		// If there were errors in getting address by domain then add them to the
		// returnValue now and remove them from the addressByDomain map
		if (addressByDomain.get("errors") != null) {
			returnValue.addAll((Collection) addressByDomain.get("errors"));
			addressByDomain.remove("errors");
		}
		
		for (Iterator i = addressByDomain.keySet().iterator(); i.hasNext();) {
			String currDomain = i.next().toString();
			Collection currDomainValidation = checkAddressesForDomain(currDomain,
					(Collection) addressByDomain.get(currDomain));
			returnValue.addAll(currDomainValidation);
		}
		
		return returnValue;
	}
	
	/**
	 * Take a Collection of email addresses and return a Collection of 
	 * CheckedMailAddress objects, showing the validity of the email addresses 
	 * against the SMTP servers for the given domain
	 * 
	 * @param addresses a Collection of mail address strings
	 * @return a Collection of CheckedMailAddress objects
	 */	
	public static Collection checkAddressesForDomain(String domain, Collection addresses) {
		SMTPClient client = null;
		Collection returnValue = new Vector();
		
		// try and establish a connection with one of the mx records
		Collection mxRecords = getMXRecords(domain);
		
		// some smtp servers will only allow a certain number of addresses to be processed at a given time, 
		// our default grouping will be 10
		// for example rr.com see this address http://security.rr.com/spam.htm#ratelimit
		int currAddressNum = 0;
		// setup the iterator for address out here so it does not get reset inside
		Iterator addressIterator = addresses.iterator();
		for (int groupNum = 0; groupNum <= addresses.size() / 10; groupNum++) {
			for (Iterator i = mxRecords.iterator(); i.hasNext() && (client == null || !client.isConnected());) {
				String currMxRecord = i.next().toString();
				try {
					client = new SMTPClient();
					// set the default time out for a connection to 90 seconds
					client.setDefaultTimeout(90 * 1000);
					System.out.println("Connecting to SMTP server '" + currMxRecord + "'.");
					client.connect(currMxRecord);
					System.out.print(client.getReplyString());
					int reply = client.getReplyCode();
					if (!SMTPReply.isPositiveCompletion(reply)) {
						client.disconnect();
						System.err.println("SMTP server refused connection.");
					}
				} catch (Exception e) {
					if (client.isConnected()) {
						try {
							client.disconnect();
						} catch (Exception e2) {
							// do nothing
						}
					}
					System.err.println(e);
				}
			}
			if (client != null && client.isConnected()) {
				try {
					// lets login now
					if (client.login("localhost")) {
						client.setSender("postmaster@localhost");
						// now lets try and send mail to the given addresses
						for ( ; addressIterator.hasNext(); ) {
							currAddressNum++;
							String currAddress = (String) addressIterator.next();
							System.out.print("Checking address '" + currAddress
									+ "'.");
							try {
								if (client.addRecipient(currAddress)) {
									System.out.println(" (Success)");
									System.out.print("\t" + client.getReplyString());
									returnValue.add(new CheckedMailAddress(currAddress,CheckedMailAddress.VALID,client.getReplyString().trim()));
								} else {
									System.out.println(" (FAILED)");
									System.out.print("\t" + client.getReplyString());
									returnValue.add(new CheckedMailAddress(currAddress,CheckedMailAddress.INVALID,client.getReplyString().trim()));
								}
							} catch (Exception e) {
								System.out.println(" (ERROR)");
								System.out.println("\t" + e.toString());
								returnValue.add(new CheckedMailAddress(currAddress,CheckedMailAddress.ERROR,e.toString()));
							}
							if (currAddressNum % 10 == 0) {
								break;
							}
						}
						System.out.println("Logging Out");
						client.logout();
						System.out.print(client.getReplyString());
					} else {
						client.disconnect();
						System.err.println("SMTP server refused login.");
						returnValue.addAll(errorEmailAddress(addresses,"SMTP server refused login."));
					}
					
					if (client.isConnected()) {
						try {
							client.disconnect();
						} catch (Exception e) {
							// do nothing
						}
					}
				} catch (Exception e) {
					if (client.isConnected()) {
						try {
							client.disconnect();
						} catch (Exception e2) {
							// do nothing
						}
					}
					e.printStackTrace();
					returnValue.addAll(errorEmailAddress(addresses,"SMTP error: " + e.toString()));
				}
			} else {
				if (mxRecords.size() < 1) {
					returnValue.addAll(errorEmailAddress(addresses,"No valid MX records for domain."));
				} else {
					returnValue.addAll(errorEmailAddress(addresses,"Could not connect to any SMTP servers for domain."));
				}
			}
		}
		return returnValue;
	}
	
	private static Collection errorEmailAddress(Collection addresses, String errorMessage) {
		Collection returnValue = new Vector();
		for (Iterator i = addresses.iterator(); i.hasNext(); ) {
			String currAddress = i.next().toString();
			returnValue.add(new CheckedMailAddress(currAddress,CheckedMailAddress.ERROR, errorMessage));
		}
		return returnValue;
	}
	
	/**
	 * Get a list of mx records for the specified domain name
	 * 
	 * @param domain
	 * @return a collection of strings of the smtp mail servers for the given
	 *         domains
	 */
	public static Collection getMXRecords(String domain) {
		return getMXRecords(domain, false);
	}
	
	private static Collection getMXRecords(String domain, boolean noHost) {
		Vector returnValue = new Vector();
		try {
			Record[] records = new Lookup(domain, Type.MX).run();
			if (records != null && records.length > 0) {
				// lets sort the records
				Arrays.sort(records, mxCompare);
				System.out.println("Getting MX records for '" + domain + "'");
				for (int i = 0; i < records.length; i++) {
					MXRecord mx = (MXRecord) records[i];
					System.out.println("Host " + mx.getTarget()
							+ " has preference " + mx.getPriority());
					returnValue.add(mx.getTarget().toString());
				}
			} else if (!noHost) {
				// then lookup to see if the host has an address record if so then add this to the returnValue
				try {
					InetAddress addr = Address.getByName(domain);
					// If I get something in return then it does so add it to the returnValue
					returnValue.add(domain);
				} catch (Exception e) {}
			}
			// if the returnValue is empty then check to see if there are any mx records for a sub domain
			if (returnValue.size() <= 0) {
				String subDomain = getSubDomain(domain);
				if (subDomain != null || !subDomain.equals("")) {
					returnValue.addAll(getMXRecords(subDomain, true));
				}
			}
		} catch (Exception e) {
			System.err.println("ERROR: Unable to retrieve MX Records for domain "
					+ domain);
			e.printStackTrace();
		}
		return returnValue;
	}
	
	private static String getSubDomain(String domain) {
		String[] tokens = domain.split("\\.");
		StringBuffer buff = new StringBuffer();
		// domain has to be at least two tokens if not then return null
		if (tokens.length > 2) {
			for (int i = 1; i < tokens.length; i++) {
				if (i > 1) {
					buff.append(".");
				}
				buff.append(tokens[i]);
			}
		} else {
			return null;
		}
		return buff.toString();
	}
	
	/**
	 * Take a collection of email addresses and turn it into a Map of domains
	 * and collections of address for that domain.
	 * 
	 * @param addresses
	 * @return
	 */
	public static Map getAddressesByDomain(Collection addresses) {
		Map returnValue = new HashMap();
		Collection addressErrors = new Vector();
		for (Iterator i = addresses.iterator(); i.hasNext();) {
			String currAddress = (String) i.next();
			// Make sure this is a valid address
			if (isValidEmailAddress(currAddress)) {
				String currDomain = getEmailAddressDomain(currAddress)
				.toLowerCase();
				if (returnValue.get(currDomain) == null) {
					returnValue.put(currDomain, new Vector());
				}
				Collection currDomainRecs = (Vector) returnValue
				.get(currDomain);
				if (currDomainRecs != null) {
					currDomainRecs.add(currAddress);
				}
			} else {
				addressErrors.add(currAddress);
			}
		}
		if (addressErrors.size() > 0) {
			returnValue.put("errors", addressErrors);
		}
		return returnValue;
	}
	
	/**
	 * Validate the form of an email address.
	 * 
	 * <P>
	 * Return <code>true</code> only if
	 * <ul>
	 * <li> <code>aEmailAddress</code> can successfully construct an
	 * <tt>javax.mail.internet.InternetAddress</tt>
	 * <li> when parsed with a "@" delimiter, <code>aEmailAddress</code>
	 * contains two tokens which satisfy
	 * {@link hirondelle.web4j.util.Util#textHasContent}.
	 * </ul>
	 * 
	 * <P>
	 * The second condition arises since local email addresses, simply of the
	 * form "albert", for example, are valid but almost always undesired.
	 */
	public static boolean isValidEmailAddress(String aEmailAddress) {
		if (aEmailAddress == null)
			return false;
		
		boolean result = true;
		try {
			
			InternetAddress emailAddr = new InternetAddress(aEmailAddress);
			if (!hasNameAndDomain(aEmailAddress)) {
				result = false;
			}
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}
	
	/**
	 * Return true only if aText is not null, and is not empty after trimming.
	 * (Trimming removes both leading/trailing whitespace and ASCII control
	 * characters.)
	 * 
	 * @param aText
	 * @return
	 */
	private static boolean textHasContent(String aText) {
		return aText != null && !aText.trim().equals("");
	}
	
	private static boolean hasNameAndDomain(String aEmailAddress) {
		String[] tokens = aEmailAddress.split("@");
		if (tokens.length == 2 && textHasContent(tokens[0])
				&& textHasContent(tokens[1])) {
			String[] domainTokens = tokens[1].split("\\.");
			if (domainTokens.length >= 2) {
				return true;
			}
		}
		return false;
	}
	
	public static String getEmailAddressDomain(String aEmailAddress) {
		String returnValue = "";
		if (hasNameAndDomain(aEmailAddress)) {
			String[] tokens = aEmailAddress.split("@");
			returnValue = tokens[1];
			// this is no longer valid domain should be anything after the @
//			String[] domainTokens = tokens[1].split("\\.");
//			// the official domain is the last two tokens in the address
//			returnValue = domainTokens[domainTokens.length - 2] + "."
//			+ domainTokens[domainTokens.length - 1];
		}
		return returnValue;
	}
	
	private static Comparator mxCompare = new Comparator() {
	    public int compare(Object o1, Object o2) {
	        MXRecord mx1 = (MXRecord) o1;
	        MXRecord mx2 = (MXRecord) o2;

	        int mx1Temp = mx1.getPriority();
	        int mx2Temp = mx2.getPriority();

	        Integer mx1Factor = new Integer( mx1Temp );
	        Integer mx2Factor = new Integer( mx2Temp );

	        return mx1Factor.compareTo( mx2Factor );
	    }
	};
}
