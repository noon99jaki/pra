/*****************************************************************************
 * Richard's Automatic Named Entity Translator (RANET)
 * Author: Richard C. Wang
 * E-mail: rcwang#cs,cmu,edu
 * Website: http://www.rcwang.com
 *****************************************************************************/
package edu.cmu.lti.nlp.mt.ranet;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class SnippetCacher {
  private boolean[] errors;
  private Helper h;

  public SnippetCacher (Helper h) {
    this.h = h;
    this.errors = new boolean[SnippetMT.ENGINES.length];
    Arrays.fill(errors, true);
  }

  public void cache (SnippetCollector sc, boolean[] errors) {
    File cacheFile = h.getCacheFile("." + bitToInt(errors) + ".xml");
    if (!cacheFile.isFile()) {
      Helper.out("Searching for outdated cached file...");
      File oldCache = getCachedFile();
      if (oldCache != null) {
        Helper.out("Deleting outdated cache file: " + oldCache);
        oldCache.delete();
      }
    }
    Helper.out("Caching snippets to " + cacheFile);
    Helper.writeToFile(cacheFile, sc.toRawXML());
  }

  public SnippetCollector getSnippets (Pattern tokenizer) {
    Helper.out("Searching for cached snippets...");
    File cachedFile = getCachedFile();
    setErrors(cachedFile);  // set the error status according to the file name
    SnippetCollector sc = new SnippetCollector(tokenizer);
    sc.fromRawXML(cachedFile);
    return sc;
  }

  public File getCachedFile () {
    File f;
    int n = (int) Math.pow(2, SnippetMT.ENGINES.length);
    for (int i = 0; i < n; i++) {
      f = h.getCacheFile("." + i + ".xml");
      if (f.isFile()) {
        Helper.out("Found cached snippet: " + f);
        return f;
      }
    }
    return null;
  }

  public void setErrors (File cachedFile) {
    if (cachedFile == null)
      return;
    String[] a = cachedFile.getName().split("\\.");
    if (a.length < 4 || !a[2].matches("^\\d+$") || !a[3].equals("xml"))
      return;
    errors = intToBit(Integer.parseInt(a[2]));
  }

  private int bitToInt (boolean[] errors) {
    int result = 0;
    for (int i = 0; i < errors.length; i++)
      if (errors[i])
        result |= (1 << i);
    return result;
  }

  private boolean[] intToBit (int num) {
    boolean[] bool = new boolean[SnippetMT.ENGINES.length];
    for (int i = 0; i < bool.length; i++)
      bool[i] = ((num >> i) & 1) == 1;
    return bool;
  }
 
  public boolean[] getErrors () {
    return errors;
  }
}
