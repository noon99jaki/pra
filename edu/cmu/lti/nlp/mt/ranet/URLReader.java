/*****************************************************************************
 * Richard's Automatic Named Entity Translator (RANET)
 * Author: Richard C. Wang
 * E-mail: rcwang#cs,cmu,edu
 * Website: http://www.rcwang.com
 *****************************************************************************/
package edu.cmu.lti.nlp.mt.ranet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLReader extends Thread {
  public static final int TIME_OUT_IN_MS = 10000;   // 10 seconds
  public static final String CHARSET = "UTF-8";
  public static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1))";
  
  private String content;
  private String url;
  private String id;
  private int timeout;
  
  public URLReader (String id, String url) {
    this.id = id;
    this.url = url;
  }

  // returns the content given an URL as an input
  public String getURL (String id, String url_str) {
    StringBuffer buf = new StringBuffer();
    Helper.out(id + url_str);

    try{
      URLConnection conn = (new URL(url_str)).openConnection();
      conn.setRequestProperty("Content-Type", "text/html; charset=" + CHARSET);
      conn.setRequestProperty("Accept-Charset", CHARSET);
      conn.setRequestProperty("Accept-Encoding", CHARSET);
      conn.setRequestProperty("Accept", "*/*");
      conn.setRequestProperty("User-Agent", USER_AGENT);
      conn.setConnectTimeout(TIME_OUT_IN_MS);   // comment out if not using Java 1.5
      conn.connect();
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), CHARSET));
      String line;
      while ((line = in.readLine()) != null)
        buf.append(line + "\n");
      in.close();
    } catch (Exception e) {
      Helper.err(id + e);
      return null;
    }
    return buf.toString();
  }

  public void run () {
    this.content = getURL(this.id, this.url);
  }

  public String getContent() {
    return this.content;
  }

  public int getTimeOut () {
    return this.timeout;
  }
}
