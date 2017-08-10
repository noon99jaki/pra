/*****************************************************************************
 * Richard's Automatic Named Entity Translator (RANET)
 * Author: Richard C. Wang
 * E-mail: rcwang#cs,cmu,edu
 * Website: http://www.rcwang.com
 *****************************************************************************/
package edu.cmu.lti.nlp.mt.ranet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

public class Helper {

	private static final Pattern HTML_PATTERN_1 = Pattern.compile("<[^<>]+>");
	private static final Pattern HTML_PATTERN_2 = Pattern.compile("(?i)(ht|f)tps?://[\\w\\.\\-\\~\\?\\&\\=\\%]+");
	private static final Pattern HTML_PATTERN_3 = Pattern.compile("nbsp;?");
	private static final Pattern HTML_PATTERN_4 = Pattern.compile("&(amp|gt|lt|quot|apos|middot|#\\d{2,4});");

	private int maxSecondsToSleep;
	private int minSecondsToSleep;
	private int numPagesOfResults;
	private int numResultsPerPage;
	private boolean hasSpace;
	private File resultDir;
	private File cacheDir;
	private String targetLangID;
	private String query;
	private Pattern tokenizer;
	
	private static boolean suppressOutput;

	public static boolean isSuppressOutput(){
		return suppressOutput;
	}

	public static void setSuppressOutput(boolean suppressOutput){
		Helper.suppressOutput=suppressOutput;
	}

	public static void addToHash(Hashtable h, String key, double amount) {
		Double count = (Double) h.get(key);
		double c = (count == null) ? 0 : count.doubleValue();
		h.put(key, new Double(c + amount));
	}

	public static void addToHash(Hashtable h, String key, int amount) {
		Integer count = (Integer) h.get(key);
		int c = (count == null) ? 0 : count.intValue();
		h.put(key, new Integer(c + amount));
	}

	public static int[] allIndicesOf(String needle, String s) {
		List list = new ArrayList();
		for (int index = s.indexOf(needle); index != -1; index = s.indexOf(needle,
				index + needle.length()))
			list.add(new Integer(index));
		int[] result = new int[list.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = ((Integer) list.get(i)).intValue();
		return result;
	}

	public static int count(String word, String s) {
		int counter = 0;
		for (int index = s.indexOf(word); index != -1; index = s.indexOf(word,
				index + word.length()))
			counter++;
		return counter;
	}

	public static void createDir(File dir) {
		if (!dir.isDirectory()) {
			out("Creating directory: " + dir);
			dir.mkdir();
		}
	}

	public static boolean empty(String in) {
		return (in == null || in.trim().length() == 0);
	}

	public static void err(String s) {
		System.err.println("[ERROR] " + s);
	}

	public static void die(String s) {
		System.err.println("[FATAL] " + s);
		System.exit(1);
	}

	public static File getFile(String filename) {
		if (filename == null)
			err("File name is null!");
		File file = new File(filename);
		if (!file.isFile())
			err("Could not find: " + filename);
		return file;
	}

	public static double getMaxDoubleValue(Hashtable table) {
		double maxValue = Double.MIN_VALUE;
		for (Iterator i = table.values().iterator(); i.hasNext();) {
			double value = ((Double) i.next()).doubleValue();
			if (value > maxValue)
				maxValue = value;
		}
		return maxValue;
	}

	public static int getMaxIntegerValue(Hashtable table) {
		int maxValue = Integer.MIN_VALUE;
		for (Iterator i = table.values().iterator(); i.hasNext();) {
			int value = ((Integer) i.next()).intValue();
			if (value > maxValue)
				maxValue = value;
		}
		return maxValue;
	}

	public static int getQueryID(String s) {
		if (empty(s))
			err("Query has not been set!");
		return Math.abs(s.hashCode());
	}

	public static Properties loadPropertiesFile(File propFile) {
		Properties props = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream(propFile);
			props.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			err("Properties file not found: " + e);
		} catch (IOException e) {
			err("Read properties file error: " + e);
		}
		return props;
	}

	public static void out(String s) {
		if(!suppressOutput){
			System.out.println(s);
		}
	}

	public static String readFile(File f) {
		return readFile(f, URLReader.CHARSET, false);
	}

	/*****************************************************************************
	 * Reads in a file, if not found, search on the class path
	 * @param in the input file
	 * @param charset the character set
	 * @param binary is the file binary or text (non-binary)?
	 * @return the content of the file
	 */
	public static String readFile(File in, String charset, boolean binary) {
		if (in == null || charset == null)
			return null;
		InputStream s = null;
		String line;
		StringBuffer buf = new StringBuffer();
		BufferedReader bReader;
		char[] c;
		try {
			if (in.exists()) // if file exist locally
				s = new FileInputStream(in);
			if (s == null) // if file exist somewhere on the class path
				s = ClassLoader.getSystemResourceAsStream(in.getPath());
			if (s == null) { // if file still could not be found
				err("Could not find \"" + in + "\" locally or on classpath");
				return null;
			}
			bReader = new BufferedReader(new InputStreamReader(s, charset));
			if (!binary) {
				while ((line = bReader.readLine()) != null)
					buf.append(line).append("\n");
			} else {
				c = new char[(int) in.length()];
				bReader.read(c);
				buf.append(c);
			}
			bReader.close();
		} catch (Exception e) {
			err("Could not read \"" + in + "\": " + e);
			return null;
		}
		return buf.toString();
	}

	public static List readToList(File f) {
		List list = new ArrayList();
		String str = readFile(f);
		String[] lines = str.split("\n");
		for (int i = 0; i < lines.length; i++) {
			if (!empty(lines[i]))
				list.add(lines[i].trim());
		}
		return list;
	}

	public static Set readToSet(File dictFile) {
		String content = Helper.readFile(dictFile);
		String[] lines = content.split("\n");
		Set set = new HashSet();
		for (int i = 0; i < lines.length; i++) {
			int spaceOffset = lines[i].indexOf(" ");
			String words = lines[i].substring(0, spaceOffset);
			set.add(words);
		}
		return set;
	}

	public static String removeHTML(String in) {
		in = HTML_PATTERN_1.matcher(in).replaceAll("");
		in = HTML_PATTERN_2.matcher(in).replaceAll("");
		in = HTML_PATTERN_3.matcher(in).replaceAll(" ");
		in = HTML_PATTERN_4.matcher(in).replaceAll("");
		return in;
	}

	// sorts string length in decending order but string in ascending order
	public static void sortByStringLengthThenString(List stringList,
			boolean ascend) {
		Collections.sort(stringList, new Comparator() {
			public int compare(Object o1, Object o2) {
				String s1 = (String) o1;
				String s2 = (String) o2;
				int result = new Integer(s2.length()).compareTo(new Integer(s1.length()));
				if (result == 0)
					return ((Comparable) o1).compareTo(o2);
				else return result;
			}
		});
	}

	// sorts in decending order
	public static List sortByValueThenKeyLength(Hashtable hash) {
		ArrayList alist = new ArrayList(hash.entrySet());
		Collections.sort(alist, new Comparator() {
			public int compare(Object o1, Object o2) {
				Comparable c1 = (Comparable) ((Map.Entry) o1).getValue();
				Comparable c2 = (Comparable) ((Map.Entry) o2).getValue();
				int result = c2.compareTo(c1);
				if (result == 0) {
					String s1 = (String) ((Map.Entry) o1).getKey();
					String s2 = (String) ((Map.Entry) o2).getKey();
					return new Integer(s2.length()).compareTo(new Integer(s1.length()));
				} else {
					return result;
				}
			}
		});
		return alist;
	}

	public static String trimQuotes(String s) {
		if (empty(s))
			return null;
		s = s.trim();
		if (s.startsWith("\""))
			s = s.substring(1, s.length());
		if (s.endsWith("\""))
			s = s.substring(0, s.length()-1);
		return s;
		// return s.trim().replaceAll("(^\"|\"$)", "").trim();
	}

	public static void writeToFile(File out, String content) {
		writeToFile(out, content, URLReader.CHARSET, false);
	}

	public static void writeToFile(File out,
			String content,
			String charset,
			boolean append) {
		if (out == null || content == null)
			return;
		try {
			BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(out, append), charset));
			bWriter.write(content);
			bWriter.close();
		} catch (IOException e) {
			err("Writing to " + out + ": " + e);
		}
	}

	// constructor
	public Helper(int numResultsPerPage,
			int numPagesOfResults,
			int minSecondsToSleep,
			int maxSecondsToSleep,
			boolean hasSpace,
			// String charset,
			String targetLangID,
			File cacheDir,
			File resultDir,
			Pattern tokenizer) {

		this.targetLangID = targetLangID;
		this.numResultsPerPage = numResultsPerPage;
		this.numPagesOfResults = numPagesOfResults;
		this.minSecondsToSleep = minSecondsToSleep;
		this.maxSecondsToSleep = maxSecondsToSleep;
		this.hasSpace = hasSpace;
		this.cacheDir = cacheDir;
		this.resultDir = resultDir;
		this.tokenizer = tokenizer;
	}

	public int countWords(String cluster) {
		return hasSpace() ? count(" ", cluster) + 1 : cluster.length();
	}

	public File getCacheDir() {
		return cacheDir;
	}

	public File getCacheFile(String suffix) {
		return getCacheFile(getQuery(), suffix);
	}

	public File getCacheFile(String query, String suffix) {
		return new File(getCacheDir(), getTargetLangID() + "." + getQueryID(query)
				+ suffix);
	}

	public int getMaxSecondsToSleep() {
		return maxSecondsToSleep;
	}

	public int getMinSecondsToSleep() {
		return minSecondsToSleep;
	}

	public int getNumPagesOfResults() {
		return numPagesOfResults;
	}

	public int getNumResultsPerPage() {
		return numResultsPerPage;
	}

	public String getQuery() {
		return query;
	}

	public int getQueryID() {
		return getQueryID(getQuery());
	}

	public File getResultDir() {
		return resultDir;
	}

	public File getResultFile(String suffix) {
		return getResultFile(getQuery(), suffix);
	}

	public File getResultFile(String query, String suffix) {
		return new File(getResultDir(), getTargetLangID() + "." + getQueryID(query)
				+ suffix);
	}

	public String getTargetLangID() {
		return targetLangID;
	}

	public Pattern getTokenizer() {
		return tokenizer;
	}

	public boolean hasSpace() {
		return hasSpace;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
