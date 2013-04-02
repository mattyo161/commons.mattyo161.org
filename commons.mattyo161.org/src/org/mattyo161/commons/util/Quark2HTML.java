package org.mattyo161.commons.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.tidy.Tidy;


public class Quark2HTML {
	private boolean tidy = true;
	private boolean clean = true;
	private String encoding = null;
	private List<String> baseFonts = null;
	private boolean debug = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "" +
			"@1line-center:<B> FIREWOOD KILN DRIED\n" + 
			"<B>Wood Pellets & Compressed Logs\n" +
			"Wolf Hill 978<\\!->281<\\!->4480/978<\\!->356<\\!->6342\n" +
			"@1line-center:<B>DEBRIS REMOVAL\n" +
			"Metal & Appliances FREE\n" +
			"<B>Insured. K.O. Property Mgmt. \n" +
			"@1line-center:<B>FIREWOOD: Seasoned<B> <B>$349/cord<B>\n" +
			"Wood Pellets, Coal, Wood Bricks. We Deliver!\n" +
			"<B>Northeast Nursery  978<\\!->535<\\!->6550<B>\n" +
			"@1line-next:<f\"ZapfDingbats\"><B>H <B> <f\"ZurichClassified\"><B> AMAZING! " +
			"<f\"ZapfDingbats\">H <B> <f\"ZurichClassified\">  New Queen Mattress <\\n>" +
			"Set European  Pillowtop.  Still in plastic. Cost <\\n>" +
			"$1099 Sell $249 Can deliver <B> 603<\\!->305<\\!->9763<B>";
		Quark2HTML converter = new Quark2HTML();
		converter.setEncoding("ISO-8859-1");
		converter.addBaseFont("ZurichClassified");
		converter.addBaseFont("SpartanBook[^\"]+");
		converter.addBaseFont("ATTriumvarite[^\"]+");
		converter.setDebug(true);
		String newStr = converter.clean(str);
		System.out.println(newStr);
//		System.out.println(str);
//		str = replaceQuarkStyle(str, "<B>", "<strong>", "</strong>");
//		str = replaceQuarkStyle(str, "<I>", "<em>", "</em>");
//		str = replaceQuarkStyle(str, "<BI>", "<strong><em>", "</em></strong>");
//		str = replaceQuarkFonts(str, new String[] {"ZurichClassified", "SpartanBook[^\"]+", "ATTriumvarite[^\"]+"});
//
//		str = str.replaceAll("<\\\\!->", "-");
//		str = str.replaceAll("<\\\\n>", " ");
//		str = str.replaceAll("  +", " ");
//		//str = str.replaceAll("&", "&amp;");
//		// str = StringEscapeUtils.escapeHtml(str);
//		
//		// make paragraphs
//		str = str.replaceAll("\\s*@([^:]+):", "</p><p class=\"$1\">");
//		Pattern p = Pattern.compile("^\\s*</p>\\s*");
//		str = p.matcher(str).replaceAll("") + "</p>";
//		str = str.replaceAll("[\\n\\r]+", "<br />");
//
//		System.out.println("\n\n***** Pre Tidy *****\n" + str);
//		
//		Tidy tidy = new Tidy();
//		tidy.setXHTML(true);
//		tidy.setForceOutput(true);
//		tidy.setInputEncoding("ISO-8859-1");
//		tidy.setJoinStyles(true);
//		tidy.setPrintBodyOnly(true);
//		Writer out = new StringWriter();
//		tidy.parse(new StringReader(str), out);
//		str = out.toString();
//		System.out.println("\n\n***** Post Tidy *****\n" + str);

		
		// Fix font end tags, these tags mark the end of a font tag in Classified
//		Pattern p = Pattern.compile("<f\"ZurichClassified\">", Pattern.CASE_INSENSITIVE);
//		str = p.matcher(str).replaceAll("<f\\\\\\$>");
//		p = Pattern.compile("<f\"SpartanBook[^\"]+\">", Pattern.CASE_INSENSITIVE);
//		str = p.matcher(str).replaceAll("<f\\\\\\$>");
//		p = Pattern.compile("<f\"ATTriumvarite[^\"]+\">", Pattern.CASE_INSENSITIVE);
//		str = p.matcher(str).replaceAll("<f\\\\\\$>");

	}
	
	/**
	 * Create object and set parameters for converting Quark content to HTML
	 * @param str
	 * @return
	 */
	public Quark2HTML() {
		baseFonts = new Vector<String>();
	}
	
	/**
	 * Add a font to the list of Fonts to consider being a base font that is it should return to the default style
	 * when this font is encountered.
	 * @param font
	 */
	public void addBaseFont(String font) {
		baseFonts.add(font);
	}
	
	/**
	 * Clean the provided text and return the result.
	 * @param text
	 * @return
	 */
	public String clean(String text) {
		if (debug) {
			System.err.println("\n***** Original text *****\n" + text);
		}
		String str = new String(text);
		str = replaceQuarkStyle(str, "<B>", "<strong>", "</strong>");
		str = replaceQuarkStyle(str, "<I>", "<em>", "</em>");
		str = replaceQuarkStyle(str, "<BI>", "<strong><em>", "</em></strong>");
		str = replaceQuarkFonts(str);

		str = str.replaceAll("<\\\\!->", "-");
		str = str.replaceAll("<\\\\n>", " ");
		str = str.replaceAll("<\\\\h>", "");
		str = str.replaceAll("  +", " ");
		str = str.replaceAll("<v[^>]*>", "").replaceAll("<e[^>]*>", "");
		// may want to support centers and others at some point
		str = str.replaceAll("<\\*[^>]*>", "");
		str = str.replaceAll("^\\s+|\\s+$", "");
		
		// make paragraphs
		str = str.replaceAll("\\s*@([^:]+):", "</p><p class=\"$1\">");
		Pattern p = Pattern.compile("^\\s*</p>\\s*");
		str = p.matcher(str).replaceAll("") + "</p>";
		str = str.replaceAll("[\\n\\r]+", "<br />");
		str = str.replaceAll("<\\\\@>", "@");
		
		if (debug) {
			System.err.println("\n***** Pre Tidy *****\n" + str);
		}
		if (this.tidy) {
			Tidy tidy = new Tidy();
			tidy.setXHTML(true);
			tidy.setForceOutput(true);
			if (this.encoding != null) {
				tidy.setInputEncoding(this.encoding);
			}
			tidy.setJoinStyles(true);
			tidy.setPrintBodyOnly(true);
			Writer out = new StringWriter();
			tidy.parse(new StringReader(str), out);
			str = out.toString();
			if (debug) {
				System.err.println("\n***** Post Tidy *****\n" + str);
			}
		}
		return str;
	}
	
	/**
	 * Search through an XTag string and replace a quark style tag with an appropriate open and close tag.
	 * For example replace a Quark &lt;B&gt; tag with &lt;strong&gt; and &lt;/strong&gt;
	 * @param str
	 * @param quarkStyle to replace ex. &lt;B&gt;
	 * @param openTag to place at beginning of styled text ex. &lt;strong&gt;
	 * @param closeTag to place at end of styled text ex. &lt;/strong&gt;
	 * @return
	 */
	public String replaceQuarkStyle(String str, String quarkStyle, String openTag, String closeTag) {
		Pattern p = Pattern.compile("(" + quarkStyle + "|@[^:@]+:)");
		Matcher m = p.matcher(str);
		int lastpos = 0;
		// keep track of which match you are in
		int count = 0;
		int foundStyles = 0;
		String tmpStr = "";
		
		while (m.find()) {
			String prefix = str.substring(lastpos, m.start());
			String match = str.substring(m.start(), m.end());
			tmpStr += prefix;
			if (match.matches(quarkStyle)) {
				count++;
				if (count % 2 == 1) {
					tmpStr += openTag;
					foundStyles++;
				} else {
					tmpStr += closeTag;
				}
			} else {
				if (count > 0 && count % 2 == 1) {
					tmpStr += closeTag + match;
					count++;
				} else {
					tmpStr += match;
				}
			}
			lastpos = m.end();
		}
		tmpStr += str.substring(lastpos);
		return tmpStr;
	}

	/**
	 * Search through an XTag string and replace any quark font tags with an appropriate open and close tag.
	 * For example replace a Quark &lt;f"ZapfDingbats"&gt; tag with &lt;span class="ZapfDingbats"&gt; and &lt;/span&gt;
	 * @param str
	 * @param baseFonts is a list of fonts that should be considered closed versions of font tags
	 * @return
	 */
	public String replaceQuarkFonts(String str) {
		for (Iterator<String> i = baseFonts.iterator(); i.hasNext(); ) {
			String font = i.next();
			Pattern p = Pattern.compile("<f\"" + font + "\">", Pattern.CASE_INSENSITIVE);
			str = p.matcher(str).replaceAll("<f\\\\\\$>");
		}
		Pattern p = Pattern.compile("(<f\\\"([^\\\"]+)\\\">|<f\\\\\\$>|@[^:@]+:)");
		Matcher m = p.matcher(str);
		int lastpos = 0;
		// keep track of which match you are in
		int count = 0;
		int foundStyles = 0;
		String tmpStr = "";
		
		while (m.find()) {
			String prefix = str.substring(lastpos, m.start());
			String match = str.substring(m.start(), m.end());
			tmpStr += prefix;
			if (match.matches("<f\\\"([^\\\"]+)\\\">")) {
				count++;
				if (count % 2 == 1) {
					tmpStr += "<span class=\"dingbat " + m.group(2).toLowerCase().replaceAll("[^a-z0-9]", "-") + "\">";
					foundStyles++;
				} else {
					tmpStr += "</span>";
				}
			} else {
				if (count > 0 && count % 2 == 1) {
					tmpStr += "</span>";
					if (match.startsWith("@")) {
						tmpStr += match;
					}
					count++;
				} else {
					tmpStr += match;
				}
			}
			lastpos = m.end();
		}
		tmpStr += str.substring(lastpos);
		return tmpStr;
	}

	public boolean isTidy() {
		return tidy;
	}

	public void setTidy(boolean tidy) {
		this.tidy = tidy;
	}

	public boolean isClean() {
		return clean;
	}

	public void setClean(boolean clean) {
		this.clean = clean;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public List<String> getBaseFonts() {
		return baseFonts;
	}

	public void setBaseFonts(List<String> baseFonts) {
		this.baseFonts = baseFonts;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
