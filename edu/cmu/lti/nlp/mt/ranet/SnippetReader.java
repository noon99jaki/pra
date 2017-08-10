/*****************************************************************************
 * Richard's Automatic Named Entity Translator (RANET)
 * Author: Richard C. Wang
 * E-mail: rcwang#cs,cmu,edu
 * Website: http://www.rcwang.com
 *****************************************************************************/
package edu.cmu.lti.nlp.mt.ranet;

import java.net.URLEncoder;

public class SnippetReader extends Thread {
  // delimiter parameters
  protected static final String SEGMENT_MARKER = "@#@";
  protected static final String KEY_S_MARKER = "~#@";
  protected static final String KEY_E_MARKER = "@#~";

  private int minSecondsToSleep;
  private int maxSecondsToSleep;
  private int numResultsPerPage;
  private int numPagesOfResults;
  private int engineID;
  private boolean error;
  private boolean firstRun;
  private String query;
  private String langCode;
  private String engineName;
  private SnippetCollector sc;

  public SnippetReader(int engineID, String langCode, boolean firstRun, Helper h) {
    this.minSecondsToSleep = h.getMinSecondsToSleep();
    this.maxSecondsToSleep = h.getMaxSecondsToSleep();
    this.numResultsPerPage = h.getNumResultsPerPage();
    this.numPagesOfResults = h.getNumPagesOfResults();
    this.query = h.getQuery();
    this.engineID = engineID;
    this.error = false;
    this.firstRun = firstRun;
    this.langCode = langCode;
    this.engineName = SnippetMT.ENGINES[engineID];
    this.sc = new SnippetCollector(h.getTokenizer());
  }

  public SnippetCollector getSnippets() {
    return sc;
  }

  public boolean hasError() {
    return error;
  }

  public void run() {
    boolean hasNextPage = true;
    String url = null;
    String content;
    URLReader[] reader = new URLReader[numPagesOfResults];

    for (int i = 0; i < numPagesOfResults && hasNextPage; i++) {
      if (!firstRun && i == 0)
        randomSleep(i, minSecondsToSleep, maxSecondsToSleep);
      else firstRun = false;
      String id = getThreadID(i);
      
      try {
        switch (engineID) {
          case SnippetMT.GOOGLE:
            url = "http://www.google.com/search?num=" + numResultsPerPage
            + "&hl=en&safe=off&filter=1&lr=" + langCode + "&q="
            + URLEncoder.encode(query, URLReader.CHARSET) + "&start="
            + (i * numResultsPerPage);
            break;
          case SnippetMT.YAHOO:
            url = "http://search.yahoo.com/search?ei=" + URLReader.CHARSET + "&fl=1&n="
            + numResultsPerPage + "&b=" + (i * numResultsPerPage + 1)
            + "&vl=" + langCode + "&p=" + URLEncoder.encode(query, URLReader.CHARSET);
            break;
          case SnippetMT.ALLTHEWEB:
            url = "http://www.alltheweb.com/search?cat=web&l=" + langCode
            + "&hits=" + numResultsPerPage + "&o=" + (i * numResultsPerPage)
            + "&ocjp=1&q=" + URLEncoder.encode(query, URLReader.CHARSET);
            break;
          default:
            Helper.err("Unknown engine ID: " + engineID);
          return;
        }
      } catch (Exception e) {
        Helper.err(id + e);
        error = true;
        break;
      }
      reader[i] = new URLReader(id, url);
      reader[i].start();
      // }
      // for (int i=0; i<max_page; i++) {
      try {
        reader[i].join();
      } catch (Exception e) {
        Helper.err(id + e);
        error = true;
        break;
      }
      content = reader[i].getContent();
      if (content == null) {
        Helper.err(id + "Connection failed! Trying again...");
        i--;
        continue;
      }
      switch (engineID) {
        case SnippetMT.GOOGLE:
          hasNextPage = parseGooglePage(i, content, sc);
          break;
        case SnippetMT.YAHOO:
          hasNextPage = parseYahooPage(i, content, sc);
          break;
        case SnippetMT.ALLTHEWEB:
          hasNextPage = parseAlltheWebPage(i, content, sc);
          break;
        default:
          Helper.err("Unknown engine ID: " + engineID);
        return;
      }
    }
  }

  private String getThreadID(int i) {
    return "[" + (i + 1) + "/" + numPagesOfResults + "][" + engineName + "] "
        + this.getClass().getSimpleName() + ": ";
  }

  private boolean parseAlltheWebPage(int pageNum,
                                     String content,
                                     SnippetCollector sc) {
    String id = getThreadID(pageNum);
    int start = content.indexOf("<!--RS-->");
    int end = content.lastIndexOf("<!--RE-->");
    if (start == -1 || end == -1 || end <= start) {
      Helper.err(id + "Could not parse the returned webpage!");
      return false;
    }
    String[] snippets = content.substring(start, end).split("<!--IS-->");
    int counter = 0;
    String snippet;
    for (int j = 0; j < snippets.length; j++) {
      snippet = snippets[j];
      start = snippet.indexOf("<span class=\"resTitle\">");
      end = snippet.lastIndexOf("</span><br><a href=\"");
      if (start == -1 || end == -1 || end <= start)
        continue;
      snippet = snippet.substring(start, end);
      // alltheweb specifics
      snippet = snippet.replaceFirst("</a></span>&nbsp; \\[.+?\\]<br>", "");
      // mark boundary for title
      snippet = snippet.replaceFirst("<span class=\"resTeaser\">",
                                     SEGMENT_MARKER);
      snippet = snippet.replaceAll("\\.\\.\\.", SEGMENT_MARKER);
      // mark keywords
      snippet = snippet.replaceAll("<b>\\s*([^<>]+)\\s*</b>", KEY_S_MARKER
          + "$1" + KEY_E_MARKER);
      snippet = Helper.removeHTML(snippet);
      snippet = snippet.replaceAll(KEY_E_MARKER + "\\s*" + KEY_S_MARKER, " ");
      if (Helper.empty(snippet))
        continue;
      sc.addSnippet(snippet);
      counter++;
    }
    Helper.out(id + "Retrieved " + counter + " snippets!");
    return content.indexOf(" class=\"rnavLink\">Next</a>&nbsp;&#187;</div>") != -1;
  }

  private boolean parseGooglePage(int pageNum,
                                  String content,
                                  SnippetCollector sc) {
    String id = getThreadID(pageNum);
    int start = content.indexOf("<div>");
    int end = content.lastIndexOf("</div>");
    if (start == -1 || end == -1 || end <= start) {
      Helper.err(id + "Could not parse the returned webpage!");
      return false;
    }
    String[] snippets = content.substring(start, end).split("<p class=g>");
    int counter = 0;
    String snippet;
    for (int j = 0; j < snippets.length; j++) {
      snippet = snippets[j];
      start = 0;
      end = snippet.lastIndexOf("<span class=a>");
      if (start == -1 || end == -1 || end <= start)
        continue;
      snippet = snippet.substring(start, end);
      // google specifics
      // remove e.g. [PDF]
      snippet = snippet.replaceFirst("<span class=w>.+?</span>", "");
      // remove e.g. [ ]
      snippet = snippet.replaceAll("<font size=-1>( - )?\\[.+?\\]</font>", "");
      // remove e.g. View as HTML
      snippet = snippet.replaceFirst("<font color=#6f6f6f>.+?<br>", "");
      // mark boundary for title
      snippet = snippet.replaceFirst("<font size=-1>", SEGMENT_MARKER);
      snippet = snippet.replaceAll("<b>\\.\\.\\.</b>", SEGMENT_MARKER);
      // mark keywords
      snippet = snippet.replaceAll("<font color=CC0033>\\s*([^<>]+)\\s*</font>",
                                   KEY_S_MARKER + "$1" + KEY_E_MARKER);
      snippet = snippet.replaceAll("<b>\\s*([^<>]+)\\s*</b>", KEY_S_MARKER
          + "$1" + KEY_E_MARKER);
      snippet = Helper.removeHTML(snippet);
      snippet = snippet.replaceAll(KEY_E_MARKER + "\\s*" + KEY_S_MARKER, " ");
      snippet = snippet.replaceAll("\\.\\.\\.", SEGMENT_MARKER);
      if (Helper.empty(snippet))
        continue;
      sc.addSnippet(snippet);
      counter++;
    }
    Helper.out(id + "Retrieved " + counter + " snippets!");
    return content.indexOf("&start=" + ((pageNum + 1) * numResultsPerPage)) != -1;
  }

  private boolean parseYahooPage(int pageNum,
                                 String content,
                                 SnippetCollector sc) {
    String id = getThreadID(pageNum);
    int start = content.indexOf("<ol start=");
    int end = content.lastIndexOf("</ol>");
    if (start == -1 || end == -1 || end <= start) {
      Helper.err(id + "Could not parse the returned webpage!");
      return false;
    }
    String[] snippets = content.substring(start, end).split("<li><div>");
    int counter = 0;
    String snippet;
    for (int j = 0; j < snippets.length; j++) {
      snippet = snippets[j];
      start = 0;
      end = snippet.lastIndexOf("</div>");
      if (start == -1 || end == -1 || end <= start)
        continue;
      snippet = snippet.substring(start, end);
      // yahoo specifics
      snippet = snippet.replaceFirst("</a>.+?</div>", "");
      // mark boundary for title
      snippet = snippet.replaceFirst("<div class=yschabstr>", SEGMENT_MARKER);
      snippet = snippet.replaceAll("\\.\\.\\.", SEGMENT_MARKER);
      // mark keywords
      snippet = snippet.replaceAll("<b>\\s*([^<>]+)\\s*</b>", KEY_S_MARKER
          + "$1" + KEY_E_MARKER);
      snippet = Helper.removeHTML(snippet);
      snippet = snippet.replaceAll(KEY_E_MARKER + "\\s*" + KEY_S_MARKER, " ");
      if (Helper.empty(snippet))
        continue;
      sc.addSnippet(snippet);
      counter++;
    }
    Helper.out(id + "Retrieved " + counter + " snippets!");
    return content.indexOf("\">Next</a></b></big>") != -1;
  }

  private void randomSleep(int pageNum, int fromSec, int toSec) {
    String id = getThreadID(pageNum);
    long waitTime = (long) ((Math.random() * Math.abs(toSec - fromSec)) + fromSec) * 1000;
    Helper.out(id + "Sleeping for " + ((double) waitTime / 1000)
        + " seconds...");
    try {
      Thread.sleep(waitTime);
    } catch (InterruptedException e) {
      Helper.err(id + e);
    }
  }
}
