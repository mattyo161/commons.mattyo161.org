package org.mattyo161.commons.util;

/**
 * A series of simple utilities to manipulate strings.
 * @author mattyo1
 *
 */
public class StringFuncs {

	/**
	 * Replace iso8859 High Ascii characters with their equivalent HTML Entities.
	 * @param text
	 * @return
	 */
	public static String iso8859ToHtml(String text) {
		text = text.replaceAll("\\xa0", "&nbsp;");
		text = text.replaceAll("\\xa1", "&iexcl;");
		text = text.replaceAll("\\xa2", "&cent;");
		text = text.replaceAll("\\xa3", "&pound;");
		text = text.replaceAll("\\xa4", "&curren;");
		text = text.replaceAll("\\xa5", "&yen;");
		text = text.replaceAll("\\xa6", "&brvbar;");
		text = text.replaceAll("\\xa7", "&sect;");
		text = text.replaceAll("\\xa8", "&uml;");
		text = text.replaceAll("\\xa9", "&copy;");
		text = text.replaceAll("\\xaa", "&ordf;");
		text = text.replaceAll("\\xab", "&laquo;");
		text = text.replaceAll("\\xac", "&not;");
		text = text.replaceAll("\\xad", "&shy;");
		text = text.replaceAll("\\xae", "&reg;");
		text = text.replaceAll("\\xaf", "&macr;");
		text = text.replaceAll("\\xb0", "&deg;");
		text = text.replaceAll("\\xb1", "&plusmn;");
		text = text.replaceAll("\\xb2", "&sup2;");
		text = text.replaceAll("\\xb3", "&sup3;");
		text = text.replaceAll("\\xb4", "&acute;");
		text = text.replaceAll("\\xb5", "&micro;");
		text = text.replaceAll("\\xb6", "&para;");
		text = text.replaceAll("\\xb7", "&middot;");
		text = text.replaceAll("\\xb8", "&cedil;");
		text = text.replaceAll("\\xb9", "&sup1;");
		text = text.replaceAll("\\xba", "&ordm;");
		text = text.replaceAll("\\xbb", "&raquo;");
		text = text.replaceAll("\\xbc", "&frac14;");
		text = text.replaceAll("\\xbd", "&frac12;");
		text = text.replaceAll("\\xbe", "&frac34;");
		text = text.replaceAll("\\xbf", "&iquest;");
		text = text.replaceAll("\\xc0", "&Agrave;");
		text = text.replaceAll("\\xc1", "&Aacute;");
		text = text.replaceAll("\\xc2", "&Acirc;");
		text = text.replaceAll("\\xc3", "&Atilde;");
		text = text.replaceAll("\\xc4", "&Auml;");
		text = text.replaceAll("\\xc5", "&Aring;");
		text = text.replaceAll("\\xc6", "&AElig;");
		text = text.replaceAll("\\xc7", "&Ccedil;");
		text = text.replaceAll("\\xc8", "&Egrave;");
		text = text.replaceAll("\\xc9", "&Eacute;");
		text = text.replaceAll("\\xca", "&Ecirc;");
		text = text.replaceAll("\\xcb", "&Euml;");
		text = text.replaceAll("\\xcc", "&Igrave;");
		text = text.replaceAll("\\xcd", "&Iacute;");
		text = text.replaceAll("\\xce", "&Icirc;");
		text = text.replaceAll("\\xcf", "&Iuml;");
		text = text.replaceAll("\\xd0", "&ETH;");
		text = text.replaceAll("\\xd1", "&Ntilde;");
		text = text.replaceAll("\\xd2", "&Ograve;");
		text = text.replaceAll("\\xd3", "&Oacute;");
		text = text.replaceAll("\\xd4", "&Ocirc;");
		text = text.replaceAll("\\xd5", "&Otilde;");
		text = text.replaceAll("\\xd6", "&Ouml;");
		text = text.replaceAll("\\xd7", "&times;");
		text = text.replaceAll("\\xd8", "&Oslash;");
		text = text.replaceAll("\\xd9", "&Ugrave;");
		text = text.replaceAll("\\xda", "&Uacute;");
		text = text.replaceAll("\\xdb", "&Ucirc;");
		text = text.replaceAll("\\xdc", "&Uuml;");
		text = text.replaceAll("\\xdd", "&Yacute;");
		text = text.replaceAll("\\xde", "&THORN;");
		text = text.replaceAll("\\xdf", "&szlig;");
		text = text.replaceAll("\\xe0", "&agrave;");
		text = text.replaceAll("\\xe1", "&aacute;");
		text = text.replaceAll("\\xe2", "&acirc;");
		text = text.replaceAll("\\xe3", "&atilde;");
		text = text.replaceAll("\\xe4", "&auml;");
		text = text.replaceAll("\\xe5", "&aring;");
		text = text.replaceAll("\\xe6", "&aelig;");
		text = text.replaceAll("\\xe7", "&ccedil;");
		text = text.replaceAll("\\xe8", "&egrave;");
		text = text.replaceAll("\\xe9", "&eacute;");
		text = text.replaceAll("\\xea", "&ecirc;");
		text = text.replaceAll("\\xeb", "&euml;");
		text = text.replaceAll("\\xec", "&igrave;");
		text = text.replaceAll("\\xed", "&iacute;");
		text = text.replaceAll("\\xee", "&icirc;");
		text = text.replaceAll("\\xef", "&iuml;");
		text = text.replaceAll("\\xf0", "&eth;");
		text = text.replaceAll("\\xf1", "&ntilde;");
		text = text.replaceAll("\\xf2", "&ograve;");
		text = text.replaceAll("\\xf3", "&oacute;");
		text = text.replaceAll("\\xf4", "&ocirc;");
		text = text.replaceAll("\\xf5", "&otilde;");
		text = text.replaceAll("\\xf6", "&ouml;");
		text = text.replaceAll("\\xf7", "&divide;");
		text = text.replaceAll("\\xf8", "&oslash;");
		text = text.replaceAll("\\xf9", "&ugrave;");
		text = text.replaceAll("\\xfa", "&uacute;");
		text = text.replaceAll("\\xfb", "&ucirc;");
		text = text.replaceAll("\\xfc", "&uuml;");
		text = text.replaceAll("\\xfd", "&yacute;");
		text = text.replaceAll("\\xfe", "&thorn;");
		text = text.replaceAll("\\xff", "&yuml;");
		return text;
	}
	
}
