/*****************************************************************************
 * Richard's Automatic Named Entity Translator (RANET)
 * Author: Richard C. Wang
 * E-mail: rcwang#cs,cmu,edu
 * Website: http://www.rcwang.com
 *****************************************************************************/

/*
 * Frank's modification notes:
 * 
 * -Removed Yahoo! from index of search engines
 * 
 */

package edu.cmu.lti.nlp.mt.ranet;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnippetMT {
  public static final String VERSION = "0.94";
  public static final double DF_RATIO_THRESHOLD = 1.7;
  public static final int MAX_FRACTION_DIGITS = 5;
  // added 10/24/2006 exclude words from Frank's dictionary
  // public static Set exclusionWords = Helper.readToSet(new File("./lib/cedict_ts.u8"));
  
  // search engine IDs
  public static final int GOOGLE = 0;
  public static final int ALLTHEWEB = 1;
  public static final int YAHOO = 2;
  
  // each engine name must have an index equivalent to its ID assigned above
  public static final String[] ENGINES = new String[] {
    " Google  ",
    "AlltheWeb",
    //" Yahoo!  ",
  };

  // global configuration parameters
  private int topTransToReturn; // maximum translations to output
  private int numResultsPerPage; // number of search results per page (max 100) to retrieve
  private int numPagesOfResults; // number of search result pages to retrieve
  private int minSecondsToSleep; // minimum number of seconds to tell a thread to sleep when retrieving batch translations
  private int maxSecondsToSleep; // maximum number of seconds to tell a thread to sleep when retrieving batch translations
  private int minRequiredDF; // minimum document frequency of a token for it to become a translation candidate
  private File cacheDir; // directory for storing cache
  private File resultDir; // directory for storing results

  // language configuration parameters
  private int minWordGrams; // minimum number of word grams for a phrase to be a translation candidate
  private int minAlphabets; // only applies to languages with spaces
  private boolean hasSpace; // true if this language uses spaces
  private Pattern tokenizer; // tokenizes words
  private String targetLangID; // ID of the target language
  private List stopWordsList; // list of stop words
  private String[] langCodes; // stores language codes for search engines

  // other variables
  private int totalAdded; // total number of additional snippets
  private int totalSnippets; // total number of snippets
  private int maxTF, maxDF, maxCTF, maxCDF, maxWords; // temporary variables
  private double maxWD; // temporary variable
  private String[] translations; // stores final translations in target language
  private SnippetCollector sc; // stores snippets
  private Helper h;

  // a set of hashtables for storing ngram->statistics mapping
  private Hashtable tfHash; // ngram -> term frequency
  private Hashtable dfHash; // ngram -> document frequency
  private Hashtable wdHash; // ngram -> sum of word distances (only consider snippets containing query)
  private Hashtable ctfHash; // ngram -> word cluster term frequency
  private Hashtable cdfHash; // ngram -> word cluster document frequency
  private Hashtable scoreHash; // ngram -> final scores

  public SnippetMT(File globalConfig, File langConfig) {
    this(Helper.loadPropertiesFile(globalConfig),
         Helper.loadPropertiesFile(langConfig));
  }
  
  public SnippetMT(Properties globalProps, Properties langProps) {
  	
  	Helper.setSuppressOutput(true);
  	
    // initialization
    totalAdded = 0;
    totalSnippets = 0;
    tfHash = new Hashtable();
    dfHash = new Hashtable();
    wdHash = new Hashtable();
    ctfHash = new Hashtable();
    cdfHash = new Hashtable();
    scoreHash = new Hashtable();
    String tmpStr;

    /*** global parameters ***************************************************/
    numResultsPerPage = Integer.parseInt(globalProps.getProperty("numResultsPerPage", "100"));
    numPagesOfResults = Integer.parseInt(globalProps.getProperty("numPagesOfResults", "3"));
    topTransToReturn = Integer.parseInt(globalProps.getProperty("topTransToReturn", "50"));
    minRequiredDF = Integer.parseInt(globalProps.getProperty("minRequiredDF", "2"));
    minSecondsToSleep = Integer.parseInt(globalProps.getProperty("minSecondsToSleep", "10"));
    maxSecondsToSleep = Integer.parseInt(globalProps.getProperty("maxSecondsToSleep", "30"));
    cacheDir = new File(globalProps.getProperty("cacheDir", "./cache"));
    resultDir = new File(globalProps.getProperty("resultDir", "./result"));

    /*** language parameters *************************************************/
    tmpStr = langProps.getProperty("tokenizePattern");
    if (tmpStr == null)
      Helper.die("\"tokenizePattern\" must be defined in [language].conf");
    else tokenizer = Pattern.compile(tmpStr);

    tmpStr = langProps.getProperty("hasSpace");
    if (tmpStr == null)
      Helper.die("\"hasSpace\" must be defined in [language].conf");
    else hasSpace = Boolean.parseBoolean(tmpStr);

    tmpStr = langProps.getProperty("id");
    if (tmpStr == null)
      Helper.die("\"id\" must be defined in [language].conf");
    else targetLangID = tmpStr;

    tmpStr = langProps.getProperty("minAlphabets");
    minAlphabets = (tmpStr == null) ? 0 : Integer.parseInt(tmpStr);

    tmpStr = langProps.getProperty("minWordGrams");
    if (tmpStr == null)
      Helper.die("\"minWordGrams\" must be defined in [language].conf");
    else minWordGrams = Integer.parseInt(tmpStr);

    tmpStr = langProps.getProperty("stopWordFile");
    stopWordsList = (tmpStr == null) ? new ArrayList()
        : Helper.readToList(Helper.getFile(tmpStr));
    
    langCodes = new String[ENGINES.length];
    for (int i = 0; i < langCodes.length; i++)
      langCodes[i] = langProps.getProperty("langCode" + i);
    /** ********************************************************************** */

    // create directory if it does not exist
    Helper.createDir(cacheDir);
    Helper.createDir(resultDir);
    
    // create the helper class
    h = new Helper(numResultsPerPage,
                   numPagesOfResults,
                   minSecondsToSleep,
                   maxSecondsToSleep,
                   hasSpace,
                   targetLangID,
                   cacheDir,
                   resultDir,
                   tokenizer);
  }

  public Helper getHelper() {
    return this.h;
  }

  public Hashtable getScores() {
    return this.scoreHash;
  }

  public SnippetCollector getSnippets() {
    return this.sc;
  }

  public int getTotalAdded() {
    return this.totalAdded;
  }

  public int getTotalSnippets() {
    return this.totalSnippets;
  }

  public String[] getTranslations() {
    return this.translations;
  }

  public String snippetsToXML() {
    return sc.toXML();
  }

  public String statisticsToXML() {
    StringBuffer buf = new StringBuffer();
    buf.append("<Stats>\n");
    buf.append("<Query>" + h.getQuery() + "</Query>\n");
    buf.append("<Num_Snippets>" + sc.snippetSize() + "</Num_Snippets>\n");
    buf.append("<Num_Segments>" + sc.segmentSize() + "</Num_Segments>\n");
    buf.append("<Max_Cluster_TF>" + maxCTF + "</Max_Cluster_TF>\n");
    buf.append("<Max_Cluster_DF>" + maxCDF + "</Max_Cluster_DF>\n");
    buf.append("<Max_TF>" + maxTF + "</Max_TF>\n");
    buf.append("<Max_DF>" + maxDF + "</Max_DF>\n");
    buf.append("<Max_Word_Distance>" + maxWD + "</Max_Word_Distance>\n");
    buf.append("<Max_Word_Gram>" + maxWords + "</Max_Word_Gram>\n");
    buf.append("</Stats>\n");
    return buf.toString();
  }

  public String toXML() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<Results>\n"
        + statisticsToXML() + translationsToXML() + snippetsToXML()
        + "</Results>\n";
  }

  public void toXML(File filename) {
    Helper.out("Writing results to " + filename);
    Helper.writeToFile(filename, toXML());
  }

  public String[] translate(String query, boolean strict) {
    // re-initialization
    tfHash.clear();
    dfHash.clear();
    wdHash.clear();
    ctfHash.clear();
    cdfHash.clear();
    scoreHash.clear();

    // eliminate any surrounding quatation marks
    query = Helper.trimQuotes(query);
    if (Helper.empty(query))
      return new String[0];
    // add quatations around the query if strict is true
    query = strict ? "\"" + query + "\"" : query;
    h.setQuery(query);
    // retrieve the snippets from search engines
    sc = collectSnippets();
    // extract statistical information from the snippets
    getFreqInfo();
    // calculate scores for each translation candidate
    scoreHash = getScoreHash();
    // rank the translations and return the top ones
    List sortedHashValue = Helper.sortByValueThenKeyLength(scoreHash);
    translations = new String[Math.min(sortedHashValue.size(), topTransToReturn)];
    for (int i = 0; i < translations.length; i++) {
      String translation = (String) ((Map.Entry) sortedHashValue.get(i)).getKey();
      translations[i] = translation;
    }
    return translations;
  }

  public String translationsToXML() {
    StringBuffer buf = new StringBuffer();
    NumberFormat f = NumberFormat.getInstance();
    f.setMaximumFractionDigits(MAX_FRACTION_DIGITS);

    buf.append("<Translations>\n");
    for (int i = 0; i < translations.length; i++) {
      String key = translations[i];
      int ctf = ((Integer) ctfHash.get(key)).intValue();
      int cdf = ((Integer) cdfHash.get(key)).intValue();
      int tf = ((Integer) tfHash.get(key)).intValue();
      int df = ((Integer) dfHash.get(key)).intValue();
      String wd = f.format(((Double) wdHash.get(key)).doubleValue());
      String score = f.format(((Double) scoreHash.get(key)).doubleValue());
      buf.append("<Trans Rank=\"" + (i + 1) + "\" CTF=\"" + ctf + "\" CDF=\""
          + cdf + "\" TF=\"" + tf + "\" DF=\"" + df + "\" WD=\"" + wd
          + "\" Score=\"" + score + "\">" + key + "</Trans>\n");
    }
    buf.append("</Translations>\n");
    return buf.toString();
  }

  private SnippetCollector collectSnippets() {
    SnippetCacher cacher = new SnippetCacher(h);
    SnippetCollector sc1 = cacher.getSnippets(tokenizer);
    // run only the engines that had errors previously
    boolean[] engineToRun = cacher.getErrors();

    int numAdded = sc1.snippetSize();
    SnippetReader[] sr = new SnippetReader[ENGINES.length];
    boolean[] errors = new boolean[ENGINES.length];
    // this is a first run if no snippets has been added
    boolean firstRun = (totalAdded == 0);
    for (int i = 0; i < ENGINES.length; i++) {
      if (Helper.empty(langCodes[i]))
        engineToRun[i] = false;
      if (!engineToRun[i])
        continue;
      sr[i] = new SnippetReader(i, langCodes[i], firstRun, h);
      sr[i].start();
    }
    for (int i = 0; i < ENGINES.length; i++) {
      if (!engineToRun[i])
        continue;
      try {
        sr[i].join();
      } catch (Exception e) {
        Helper.err("CollectSnippets Error: " + e);
      }
      errors[i] = sr[i].hasError();
      if (!errors[i]) {
        SnippetCollector sc2 = sr[i].getSnippets();
        sc1.addSnippets(sc2);
      }
    }
    cacher.cache(sc1, errors);
    int snippetSize = sc1.snippetSize();
    numAdded = snippetSize - numAdded;
    totalAdded += numAdded;
    if (numAdded != snippetSize)
      Helper.out("Retrieved an additional " + numAdded + " snippets.");
    Helper.out("Retrieved a total of " + snippetSize + " snippets.");
    totalSnippets += snippetSize;
    return sc1;
  }

  private void getFreqInfo() {
    // must be unique and not be a white space (i.e. \f, \t, \r, \n, :space:)
    byte[] b = { (byte) 255 };
    String keyMarker = new String(b);
    Pattern keyGrabber = Pattern.compile("\\Q" + SnippetReader.KEY_S_MARKER
        + "\\E.*?\\Q" + SnippetReader.KEY_E_MARKER + "\\E");

    // construct a modified snippet list
    String[] segs = new String[sc.segmentSize()];
    for (int i = 0; i < segs.length; i++) {
      String segment = sc.getSegment(i);
      segment = hasSpace ? segment : segment.replaceAll("\\s+", "");
      segs[i] = keyGrabber.matcher(segment).replaceAll(keyMarker).toLowerCase();
    }

    // gather word clusters
    List[] clusters = new ArrayList[sc.segmentSize()];
    for (int i = 0; i < clusters.length; i++) {
      clusters[i] = getWordClusters(segs[i]);
      Helper.sortByStringLengthThenString(clusters[i], false);
      String prev_word = "";
      for (int j = 0; j < clusters[i].size(); j++) {
        String word = (String) clusters[i].get(j);
        Helper.addToHash(ctfHash, word, 1);
        if (!word.equals(prev_word))
          Helper.addToHash(cdfHash, word, 1);
        prev_word = word;
      }
    }

    List allClusters = new ArrayList(ctfHash.keySet());
    Helper.sortByStringLengthThenString(allClusters, false);
    HashSet[] uniqueWords = new HashSet[sc.segmentSize()];
    for (int i = 0; i < uniqueWords.length; i++)
      uniqueWords[i] = new HashSet();

    // count term frequency
    for (int i = 0; i < allClusters.size(); i++) {
      String word = (String) allClusters.get(i);
      int count = 0;
      for (int j = 0; j < clusters.length; j++) {
        int c = count;
        for (int k = 0; k < clusters[j].size(); k++) {
          String cluster = (String) clusters[j].get(k);
          if (word.length() > cluster.length())
            break;
          if (hasSpace)
            count += Helper.count(" " + word + " ", " " + cluster + " ");
          else count += Helper.count(word, cluster);
        }
        // for each snippet, construct a set of word clusters contained within
        // that are derived from all snippets
        if (c != count && !uniqueWords[j].contains(word)) {
          uniqueWords[j].add(word);
          Helper.addToHash(dfHash, word, 1);
        }
      }
      Helper.addToHash(tfHash, word, count);
    }
   
    // remove words (keys) in TF hash that has DF less than minRequiredDF
    List tfList = new ArrayList(tfHash.keySet());
    for (int i = 0; i < tfList.size(); i++) {
      String key = (String) tfList.get(i);
      int df = ((Integer) dfHash.get(key)).intValue();
      if (df < minRequiredDF)
        tfHash.remove(key);
    }
    wordClusterReducer(DF_RATIO_THRESHOLD);

    // count sum of word distances
    for (int i = 0; i < segs.length; i++) {
      if (Helper.empty(segs[i]))
        continue;
      String segment = segs[i];
      if (hasSpace)
        segment = segment.replaceAll("\\b", " ").replaceAll("\\s+", " ");
      
      int[] keyIndices = Helper.allIndicesOf(keyMarker, segment);
      if (keyIndices.length == 0)
        continue;

      for (Iterator j = uniqueWords[i].iterator(); j.hasNext();) {
        String word = (String) j.next();
        if (dfHash.get(word) == null)
          continue;
        
        int[] wordIndices;
        if (hasSpace) {
          String spacedWord = word.replaceAll("\\b", " ").replaceAll("\\s+", " ");
          wordIndices = Helper.allIndicesOf(spacedWord, segment);
        } else {
          wordIndices = Helper.allIndicesOf(word, segment);
        }

        for (int k = 0; k < wordIndices.length; k++) {
          int wi = wordIndices[k];
          int minDist = Integer.MAX_VALUE;

          for (int l = 0; l < keyIndices.length; l++) {
            int ki = keyIndices[l];
            int diff = (ki < wi) ? wi - ki : ki - (wi + word.length()) + 1;
            if (diff < minDist)
              minDist = diff;
          }
          Helper.addToHash(wdHash, word.trim(), (double) 1 / minDist);
        }
      }
    }
  }

  private Hashtable getScoreHash() {
    Hashtable resultHash = new Hashtable();
    if (tfHash.size() == 0 || dfHash.size() == 0 || wdHash.size() == 0)
      return resultHash;

    maxCTF = Helper.getMaxIntegerValue(ctfHash);
    maxCDF = Helper.getMaxIntegerValue(cdfHash);
    maxTF = Helper.getMaxIntegerValue(tfHash);
    maxDF = Helper.getMaxIntegerValue(dfHash);
    maxWD = Helper.getMaxDoubleValue(wdHash);

    // get maximum number of words (grams)
    maxWords = 0;
    for (Iterator i = wdHash.keySet().iterator(); i.hasNext();) {
      String candidate = (String) i.next();
      int numWords = h.countWords(candidate);
      if (numWords > maxWords)
        maxWords = numWords;
    }

    // calculate scores for each translation candidate
    for (Iterator i = wdHash.keySet().iterator(); i.hasNext();) {
      String candidate = (String) i.next();
      double ctf = ((Integer) ctfHash.get(candidate)).doubleValue();
      double cdf = ((Integer) cdfHash.get(candidate)).doubleValue();
      double tf = ((Integer) tfHash.get(candidate)).doubleValue();
      double df = ((Integer) dfHash.get(candidate)).doubleValue();
      double wd = ((Double) wdHash.get(candidate)).doubleValue();
      double numWords = (double) h.countWords(candidate);
      double score = (tf / maxTF) * (df / maxDF) * (ctf / maxCTF)
          * (cdf / maxCDF) * (wd / maxWD) * numWords / maxWords;
      resultHash.put(candidate, new Double(score));
    }
    return resultHash;
  }

  // retrieve word clusters of the snippet
  private List getWordClusters(String in) {
    List list = new ArrayList();

    // remove words using the stop-words list
    for (int i = 0; i < stopWordsList.size(); i++)
      in = in.replaceAll("\\Q" + stopWordsList.get(i) + "\\E", "|");
   
    Matcher m = tokenizer.matcher(in);
    while (m.find()) {
      String toAdd = m.group().toLowerCase().trim();
      if (hasSpace && toAdd.length() < minAlphabets)
        continue;
      if (h.countWords(toAdd) < minWordGrams)
        continue;
      
      // added 10/24/2006 exclude words from Frank's dictionary
      // if (exclusionWords.contains(toAdd)) continue;
      list.add(toAdd);
    }
    return list;
  }

  // sort by descending numbers of words then ascending term frequency
//  private void sortByNumWordsThenTF(List stringList) {
//    Comparator c = new Comparator() {
//      public int compare(Object o1, Object o2) {
//        int numWords1 = h.countWords((String) o1);
//        int numWords2 = h.countWords((String) o2);
//        int result = (new Integer(numWords2)).compareTo(new Integer(numWords1));
//        if (result == 0)
//          return ((Integer) tfHash.get(o1)).compareTo((Integer) tfHash.get(o2));
//        else return result;
//      }
//    };
//    Collections.sort(stringList, c);
//  }

  // sort by descending numbers of words then ascending document frequency
  private void sortByNumWordsThenDF(List stringList) {
    Comparator c = new Comparator() {
      public int compare(Object o1, Object o2) {
        int numWords1 = h.countWords((String) o1);
        int numWords2 = h.countWords((String) o2);
        int result = (new Integer(numWords2)).compareTo(new Integer(numWords1));
        if (result == 0)
          return ((Integer) dfHash.get(o1)).compareTo((Integer) dfHash.get(o2));
        else return result;
      }
    };
    Collections.sort(stringList, c);
  }

  // based on DF
  private void wordClusterReducer(double threshold) {
    List wordList = new ArrayList(dfHash.keySet());
    sortByNumWordsThenDF(wordList);

    for (int i = 0; i < wordList.size(); i++) {
      String word = (String) wordList.get(i);
      Integer w1_count = (Integer) dfHash.get(word);
      if (w1_count == null)
        continue;
      if (w1_count.intValue() < minRequiredDF)
        dfHash.remove(word);
    }
    for (int i = 1; i < wordList.size(); i++) {
      String w1 = (String) wordList.get(i);
      Integer w1_count = (Integer) dfHash.get(w1);
      if (w1_count == null)
        continue;

      for (int j = 0; j < wordList.size(); j++) {
        String w2 = (String) wordList.get(j);
        if (h.countWords(w1) >= h.countWords(w2))
          break;
        if (hasSpace && (" " + w2 + " ").indexOf(" " + w1 + " ") == -1
            || !hasSpace && w2.indexOf(w1) == -1)
          continue;
        Integer w2_count = (Integer) dfHash.get(w2);
        if (w2_count == null)
          continue;
        double dfRatio = w1_count.doubleValue() / w2_count.doubleValue();
        if (dfRatio < threshold) {
          dfHash.remove(w1);
          break;
        }
      }
    }
  }
}
