/*****************************************************************************
 * Richard's Automatic Named Entity Translator (RANET)
 * Author: Richard C. Wang
 * E-mail: rcwang#cs,cmu,edu
 * Website: http://www.rcwang.com
 *****************************************************************************/
package edu.cmu.lti.nlp.mt.ranet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnippetCollector {
  private final Pattern CANONICALIZER = Pattern.compile("[\\s\\d\\p{Punct}]+");
  private final Pattern SPACER = Pattern.compile("[\\s&]+");

  private List segments;
  private List ids;
  private List bareSegments;
  private List raws;
  private Pattern tokenizer;

  public SnippetCollector(Pattern tokenizer) {
    this.segments = new ArrayList();
    this.ids = new ArrayList();
    this.bareSegments = new ArrayList();
    this.raws = new ArrayList();
    this.tokenizer = tokenizer;
  }

  public void addSnippet(String snippet) {
    if (Helper.empty(snippet))
      return;
    else raws.add(spaceMerger(snippet));
    String[] segs = snippet.split(SnippetReader.SEGMENT_MARKER);
    if (segs.length == 0)
      return;
    String id = toID(segs[0].trim());
    if (Helper.empty(id))
      return;
    List l = new ArrayList(ids);
    for (int i = 0; i < segs.length; i++) {
      String segment = spaceMerger(segs[i]);
      addSegment(segment, canonicalize(segment), id, l);
    }
  }

  public void addSnippets(SnippetCollector sc) {
    if (sc == null)
      return;
    if (segmentSize() == 0) { // make this faster
      segments = sc.getSegments();
      bareSegments = sc.getBareSegments();
      ids = sc.getIDs();
      raws = sc.getRawSnippets();
    } else {
      raws.addAll(sc.getRawSnippets());
      List l = new ArrayList(ids);
      for (int i = 0; i < sc.segmentSize(); i++)
        addSegment(sc.getSegment(i), sc.getBareSegment(i), sc.getID(i), l);
    }
  }

  public void fromRawXML(File cacheFile) {
    fromRawXML(Helper.readFile(cacheFile));
  }

  public void fromRawXML(String content) {
    if (Helper.empty(content))
      return;
    content = content.replaceAll("</Snippet>", "!/Snippet!");
    content = Helper.removeHTML(content);
    String[] snippets = content.split("!/Snippet!");
    for (int i = 0; i < snippets.length; i++)
      addSnippet(snippets[i]);
  }

  public String getBareSegment(int i) {
    if (i < segmentSize())
      return (String) bareSegments.get(i);
    else return null;
  }

  public List getBareSegments() {
    return bareSegments;
  }

  public String getID(int i) {
    if (i < segmentSize())
      return (String) ids.get(i);
    else return null;
  }

  public List getIDs() {
    return ids;
  }

  public String getRawSnippet(int i) {
    if (i < rawSize())
      return (String) raws.get(i);
    else return null;
  }

  public List getRawSnippets() {
    return raws;
  }

  public String getSegment(int i) {
    if (i < segmentSize())
      return (String) segments.get(i);
    else return null;
  }

  public List getSegments() {
    return segments;
  }

  public int rawSize() {
    return raws.size();
  }

  public int segmentSize() {
    return segments.size();
  }

  public int snippetSize() {
    return (new HashSet(ids)).size();
  }

  public String toRawXML() {
    StringBuffer xml = new StringBuffer();
    xml.append("<Snippets Size=\"" + rawSize() + "\">\n");
    for (int i = 0; i < rawSize(); i++)
      xml.append("<Snippet ID=\"" + (i + 1) + "\">" + getRawSnippet(i)
          + "</Snippet>\n");
    xml.append("</Snippets>\n");
    return xml.toString();
  }

  public String toXML() {
    StringBuffer xml = new StringBuffer();
    xml.append("<Segments SegmentSize=\"" + segmentSize() + "\" SnippetSize=\""
        + snippetSize() + "\">\n");
    for (int i = 0; i < segmentSize(); i++)
      xml.append("<Segment ID=\"" + (i + 1) + "\">" + getSegment(i)
          + "</Segment>\n");
    xml.append("</Segments>\n");
    return xml.toString();
  }

  private void addSegment(String segment,
                          String bareSegment,
                          String id,
                          List copyOfIDs) {
    if (Helper.empty(segment) || Helper.empty(bareSegment) || Helper.empty(id)
        || copyOfIDs.contains(id)
        || segment.indexOf(SnippetReader.KEY_S_MARKER) < 0
        || segment.indexOf(SnippetReader.KEY_E_MARKER) < 0)
      return;
    Matcher m = tokenizer.matcher(segment);
    if (!m.find())
      return;
    boolean inserted = false;
    String oldBareSegment;

    for (int i = 0; i < segmentSize() && !inserted; i++) {
      oldBareSegment = getBareSegment(i);
      // if new is a subset of old, then do nothing
      if (oldBareSegment.length() >= bareSegment.length()
          && oldBareSegment.indexOf(bareSegment) > -1)
        inserted = true;
      // if old is a subset of new
      else if (oldBareSegment.length() < bareSegment.length()
          && bareSegment.indexOf(oldBareSegment) > -1) {
        // replace old with new
        segments.set(i, segment.trim());
        bareSegments.set(i, bareSegment);
        ids.set(i, id);
        inserted = true;
      }
    }
    if (!inserted) {
      segments.add(segment);
      bareSegments.add(bareSegment);
      ids.add(id);
    }
  }

  private String canonicalize(String s) {
    if (Helper.empty(s))
      return null;
    return CANONICALIZER.matcher(s).replaceAll("").toLowerCase();
  }

  private String spaceMerger(String s) {
    if (Helper.empty(s))
      return null;
    return SPACER.matcher(s).replaceAll(" ").trim();
  }

  private String toID(String s) {
    if (Helper.empty(s))
      return null;
    String t = canonicalize(s);
    return Helper.empty(t) ? SPACER.matcher(s).replaceAll("").toLowerCase() : t;
  }
}
