/**
 * 
 */
package edu.cmu.lti.util.text;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.VecVecS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;

/**
 * @author nlao String is final, so make a class of
 *         Functions instead of extending String
 */
public class FString {// Input Stream to String
	public static NumberFormat nf4 = NumberFormat.getNumberInstance();
	static {
		nf4.setMaximumFractionDigits(4);
		nf4.setMinimumFractionDigits(4);
	}

	public static VectorS toVS(String x, String sep) {
		return new VectorS(x.split(sep));
	}

	public static String getUpperCaseLetters(String str) {
		return getUpperCaseLetters(str, 0);
	}

	public static String join(String vs[], String c, int ib, int ie) {
		ie = Math.min(ie, vs.length);
		StringBuffer sb = new StringBuffer();
		for (int i = ib; i < ie; i++) {
			if (i > ib) sb.append(c);
			sb.append(vs[i]);
		}
		return (sb.toString());
	}

	public static String join(String vs[], String c, int ib) {
		return join(vs, c, ib, vs.length);
	}

	/** 
	 * @param c seperator
	 * @return joined result
	 */
	public static String join(String vs[], String c) {
		return join(vs, c, 0);
	}

	public static String join(String vs[]) {
		return join(vs, "\t");
	}

	public static String join(String vs[], String c, int vi[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < vi.length; i++) {
			if (i > 0) sb.append(c);
			sb.append(vs[vi[i]]);
		}
		return (sb.toString());
	}

	public static int[] toVInt(String vs[]) {
		int[] vi = new int[vs.length];
		for (int i = 0; i < vs.length; ++i)
			vi[i] = Integer.parseInt(vs[i]);
		return vi;
	}

	/**	/*

	 * 
	 * @param str	="Get Upper Case Letters"
	 * @param nLower	=1
	 * @return ="GeUpCaLe"
	 */
	public static String getUpperCaseLetters(String str, int nLower) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			if (c >= 'A' && c <= 'Z') {
				sb.append(c);

				for (int j = 1; j <= nLower; ++j)
					if (i + j < str.length()) {
						char c1 = str.charAt(i + j);
						if (c1 >= 'a' && c1 <= 'z') sb.append(c1);
						else break;
					}
			}
		}
		return sb.toString();
	}

	/** 
	* @param str	="get Leading Letters"
	* @param nFollower	=0
	* @return ="gLL"
	*/
	public static String getLeadingLetters(String str, int nFollower) {
		StringBuffer sb = new StringBuffer();
		String vs[] = str.split("[^a-zA-Z0-9]");//  \\W
		if (vs.length == 1) return str;
		if (vs.length == 2) ++nFollower;

		for (String s : vs)
			sb.append(s.substring(0, Math.min(s.length(), nFollower + 1)));
		return sb.toString();//.toLowerCase();
	}

	public static String getLeadingLetters(String str) {
		return getLeadingLetters(str, 0);
	}

	public static String capFirstLetter(String word) {
		StringBuffer sb = new StringBuffer(word);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return (sb.toString());
	}

	public static String toTitleCase(String txt) {
		// First thing to do is to create a string buffer as you
		// cannot change a string
		StringBuffer sb = new StringBuffer(txt);
		// Go through the string, every time you come across
		// a new word set the first letter to upper case
		boolean haveSeenSpace = true; // Set it initially to
		// true so that we set
		// the first letter
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == ' ') {
				haveSeenSpace = true;
			} else {
				// Must be a letter so check to see if the last item
				// was a space to set to upper case
				if (haveSeenSpace) {
					sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
					haveSeenSpace = false;
				} else {
					// Must be a letter so push to lower
					sb.setCharAt(i, Character.toLowerCase(sb.charAt(i)));
				}
			}
		}
		return (sb.toString());
	}

	public static VecVecS parseTable(String table, String cRow, String cCol) {
		VecVecS vvs = new VecVecS();
		for (String row : table.split(cRow))
			// VectorS vs = ;
			vvs.add(new VectorS(row.split(cCol)));
		return vvs;
	}
	// http://java.sun.com/j2se/1.5.0/docs/api/java/util/
	// Formatter.html
	public static NumberFormat fraction_formater;
	static {// new DecimalFormat ( "#,##0.0#;(#,##0.0#)" );
		fraction_formater = NumberFormat.getNumberInstance();
		fraction_formater.setMaximumFractionDigits(2);
		fraction_formater.setMinimumFractionDigits(0);
		fraction_formater.setGroupingUsed(false);
	}
	
//	public static NumberFormat int_formater;
//	static {// new DecimalFormat ( "#,##0.0#;(#,##0.0#)" );
//		int_formater = NumberFormat.getNumberInstance();
//		int_formater.setMaximumFractionDigits(2);
//		int_formater.setMinimumFractionDigits(0);
//		int_formater.setGroupingUsed(false);
//	}

	public static String double_format_ ="%.1e";// "%.3f";

	public static String format(Object o) {
		if (o == null) return "";
		if (o.getClass().equals(Double.class)) return String.format(double_format_, o);
		return o.toString();
		//return FString.nf.format(o);
	}

	public static String byteToHex(byte[] vb) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < vb.length; i++) {
			sb.append(Integer.toHexString(0xFF & vb[i]));
		}
		return sb.toString();
	}

	public static String mid(String s, int ib, int n) {
		return s.substring(Math.max(0, ib), Math.min(ib + n, s.length()));
	}

	public static String left(String s, int n) {
		return mid(s, 0, n);
	}

	public static String right(String s, int n) {
		return mid(s, s.length() - n);
	}

	public static String trimRight(String s, int n) {
		return s.substring(0, s.length() - n);
	}

	public static String removeQuotes(String s) {
		if (!s.startsWith("\"")) System.err
				.println("expect string to start with \" :" + s);
		if (!s.endsWith("\"")) System.err.println("expect string to end with \" :"
				+ s);
		return s.substring(1, s.length() - 1);
	}

	public static String mid(String s, int ib) {
		return mid(s, ib, s.length());
	}

	public static String byteToHex1(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) buf
						.append((char) ('0' + halfbyte));
				else buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String inputStream2String(java.io.InputStream is)
			throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		byte[] b = new byte[4096];
		int len = 0;
		while ((len = is.read(b)) > 0) {
			byteStream.write(b, 0, len);
		}
		String outputString = byteStream.toString("UTF-8");
		return outputString;
	}

	public static String[] split(String s) {
		return split(s, "\t");
	}

	public static String[] split(String s, String c) {
		if (s.length() == 0) return new String[0];

		String[] vs = s.split(c);
		int n = FString.count(s, c) + 1;
		if (n == vs.length) return vs;
		String[] v = new String[n];
		int i = 0;
		for (; i < vs.length; ++i)
			v[i] = vs[i];
		for (; i < v.length; ++i)
			v[i] = "";
		return v;
	}

	public static String[] split(String s, char c) {
		if (s.length() == 0) return new String[0];

		VectorI vi = new VectorI();
		vi.add(0);

		for (int i = 0; i < s.length(); ++i)
			if (s.charAt(i) == c) vi.add(i + 1);

		String[] v = new String[vi.size()];
		vi.add(s.length() + 1);

		for (int i = 0; i < v.length; ++i)
			v[i] = s.substring(vi.get(i), vi.get(i + 1) - 1);
		return v;
	}
	public static VectorS splitLine(String s) {
		return splitVS(s, "\t");
	}
	public static VectorS splitVS(String s, String regex) {
		if (s.length() == 0) return new VectorS();

		String[] vs = s.split(regex);
		VectorS v = new VectorS(vs);

		int n = FString.count(s, regex);
		for (int i = vs.length; i < n + 1; ++i) v.add("");
		return v;
	}

	public static VectorS splitAndKeep(String txt, String chars) {
		VectorS vs = new VectorS();
		int b = 0;
		int e = 0;
		for (; e < txt.length(); ++e) {
			if (chars.indexOf(txt.charAt(e)) >= 0) {
				if (e > b) vs.add(txt.substring(b, e));
				vs.add(txt.substring(e, e + 1));
				b = e + 1;
			}
		}
		if (e > b) vs.add(txt.substring(b, e));
		return vs;
	}

	public static int count(String s, char c) {
		if (s == null) return 0;
		int n = 0;
		for (int i = 0; i < s.length(); ++i)
			if (s.charAt(i) == c) ++n;
		return n;
	}

	public static int count(String s, String c) {
		int n = 0;
		for (int k = 0; (k = s.indexOf(c, k) + 1) > 0; ++n);
		return n;
	}

	/*
	 * public static VectorS splitKeep(String s, String c){
	 * VectorS vs = new VectorS(); int j=0; for (int i=0;
	 * i<s.length(); ++i){ s.indexOf(str) } return vs; }
	 */
	public static VectorS tokenize(String s) {
		return splitVS(s, "[ ,;\n]+");
	}

	// public static void convertTo(String s, Integer x){
	// x.parseInt(s); }
	// public static void convertTo(String s, Double x){
	// x.parseDouble(s); }
	// public static void convertTo(String s, String x){ x =s;
	// }
	public static String repeat(String s, Integer n) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < n; i++) {
			sb.append(s);
		}
		return (sb.toString());
	}

	// String to InputStream
	public static InputStream string2InputStream(String s) {
		if (s == null) return null;
		// s = s.trim();
		java.io.InputStream in = null;
		try {
			in = new java.io.ByteArrayInputStream(s.getBytes("UTF-8"));
		} catch (Exception ex) {}
		return in;
	}

	// Takes a string of comma-delimited elements and makes a
	// List
	// out of it, breaking on the commas. For a string that
	// does not
	// contain a comma, the list will be one element long.
	public static List<String> parseList(String s) {
		String[] parts = s.split("\\s*,\\s*");
		return Arrays.asList(parts);
	}

	// If the String is of the form "<X>...</X>", returns
	// "..." with
	// the outermost enclosing tag removed.
	public static String stripOutermostTag(String s) {
		Pattern p = Pattern.compile("^<(\\w+)([^>]*)>(.*)</(\\w+)>$",
				Pattern.DOTALL);
		Matcher m = p.matcher(s);
		if (m.matches()) {
			String t1 = m.group(1);
			String t4 = m.group(4);
			if (t1.equals(t1)) return m.group(3);
			System.err.println("unmatched tag " + t1 + " <> " + t4);
		}
		return null;
	}

	// boolean tryParseInt(String s)
	public static double parseDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.NaN;
	}

	public static int parseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Integer.MAX_VALUE;
	}

	public static BufferedReader toBufferedReader(String s) {
		return new BufferedReader(toInputStreamReader(s));
	}

	public static InputStreamReader toInputStreamReader(String s) {
		return new InputStreamReader(toInputStream(s));
	}

	public static ByteArrayInputStream toInputStream(String s) {
		return new ByteArrayInputStream(s.getBytes());
	}

	public static String findAcronym(String s1) {
		String s = s1.replaceAll("\\.", "");
		final Pattern p = Pattern.compile("([A-Z][A-Z][A-Z][A-Z]*)");
		Matcher m = p.matcher(s);
		if (m.find()) return m.group(1);
		return null;
	}

	public static boolean isSingleWord(String s) {
		return s.indexOf(' ') == -1;
		//return count(s, ' ')==-1;
	}

	public static String convertToUTF8(String s) {
		return convert(s, "UTF8");
	}

	public static String convert(String s, String encoding) {
		try {
			return new String(s.getBytes(encoding), encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String removeNonAsc(String s) {
		return s.replaceAll("[^\\p{ASCII}]", "");
	}

  // get the last section of a string, divided by c
	public static String getLastSection(String str, char c) {
		return str.substring(str.lastIndexOf(c) + 1);
	}
	
  // remove the last section of a string, divided by c
	public static String removeLastSection(String str, char c) {
		return str.substring(0, str.lastIndexOf(c) );
	}

  // get the second last section of a string, divided by c
	public static String getSecondLastSection(String str, String c) {
    return getKLastSection(str, c, 1);
  }
	public static String getKLastSection(String str, String c, int k) {
		VectorS sections = FString.splitVS(str, c);
		return sections.getRight(k);
	}
	
	public static void main(String[] args) {
		System.out.printf("%,d", 12345678);
	}

  public static VectorI findAll(String str, char c) {
  	VectorI positions = new VectorI();
  	for (int i=0; i< str.length(); ++i)
  		if (str.charAt(i) == c)
  			positions.add(i);
    return positions;
  }
 }
