/*****************************************************************************
 * Richard's Automatic Named Entity Translator (RANET)
 * Author: Richard C. Wang
 * E-mail: rcwang#cs,cmu,edu
 * Website: http://www.rcwang.com
 *****************************************************************************/
package edu.cmu.lti.nlp.mt.ranet;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ranet {
  private static Helper h;
  private static File locker;
  public static final int EVAL_TOP_N = 5;
  public static final Pattern TRANSLATION_PAT = Pattern.compile(">(.+?)</Trans>");

  public static void main(String[] args) {
    // add a shutdownhook to remove any file lockers when user presses CTRL-C
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        if (locker != null && locker.isFile())
          locker.delete();
      }
    });

    // parse the command line argument
    if (args.length < 4) {
      Helper.err("Incorrect arguments in the command line");
      usage();
      return;
    }

    File globalConfigFile = Helper.getFile(args[0]);
    File langConfigFile = Helper.getFile(args[1]);
    boolean strict = Boolean.parseBoolean(args[2]);

    // the query can also be the name of a file that contains named entities
    // and their translations (separated by comma or tab)
    String query = args[3];
    File queryFile = new File(query);
    HashMap data;
    if (queryFile.isFile()) {
      // data = getGoldTrans(queryFile);
      data = readAnswers(queryFile);
    } else {
      data = new HashMap();
      data.put(query, null);
    }

    int counter = 1;
    double startTime = System.currentTimeMillis();

    SnippetMT smt = new SnippetMT(globalConfigFile, langConfigFile);
    h = smt.getHelper();

    for (Iterator i = data.keySet().iterator(); i.hasNext(); counter++) {
      Helper.out("[" + counter + "/" + data.size() + "] Evaluating...");
      String key = (String) i.next();

      locker = h.getCacheFile(key, ".lock");
      if (locker.isFile()) {
        Helper.err("File locker found: " + locker);
        continue;
      }
      Helper.writeToFile(locker, "");
      smt.translate(key, strict);
      smt.toXML(h.getResultFile(".xml"));
      locker.delete();
    }
    Helper.out("Elapsed time is " + (System.currentTimeMillis() - startTime)
        / 1000 + " seconds.");
    if (data.size() > 1) {
      // if number of words to translate is more than one, then this is a
      // evaluation run
      Helper.out("A total of " + smt.getTotalAdded()
          + " snippets were added into the cache.");
      Helper.out("Average snippets per keyword is "
          + ((float) smt.getTotalSnippets() / data.size()) + " snippets.");
      for (int i = 0; i < EVAL_TOP_N; i++)
        evaluateResults(h.getTargetLangID(), queryFile.getName(), data, true, true, i + 1);
    }
  }

  private static HashMap readAnswers(File answerFile) {
    HashMap data = new HashMap();
    String content = Helper.readFile(answerFile);
    String[] lines = content.split("\n");
    for (int i = 0; i < lines.length; i++) {
      if (Helper.empty(lines[i])) continue;
      // split on '\t' ',' and '|'
      // String element[] = lines[i].split("[\t,\\|]");
      String element[] = lines[i].split("\t");
      String key = Helper.trimQuotes(element[0]);
      if (Helper.empty(key)) continue;
      List answerList = new ArrayList();
      for (int j = 1; j < element.length; j++) {
        String answer = Helper.trimQuotes(element[j]);
        if (Helper.empty(answer)) continue;
        answerList.add(answer);
      }
      data.put(key, answerList.toArray(new String[0]));
    }
    return data;
  }
 
  private static void evaluateResults(String targetLangID,
                                      String evalFilename,
                                      HashMap goldMap,
                                      boolean strict,
                                      boolean answerIsRegExp,
                                      int top) {
    int correct = 0, numGold = 0, numObtained = 0;
    StringBuffer buf = new StringBuffer();

    int counter = 1;
    for (Iterator i = goldMap.keySet().iterator(); i.hasNext(); counter++) {
      String query = (String) i.next();
      String[] answers = (String[]) goldMap.get(query);
      if (answers == null || answers.length == 0)
        continue;
      numGold++;

      query = Helper.trimQuotes(query);
      query = strict ? "\"" + query + "\"" : query;
      File resultFile = h.getResultFile(query, ".xml");
      String content = Helper.readFile(resultFile);
      if (content == null) continue;
      Matcher m = TRANSLATION_PAT.matcher(content);
      List resultList = new ArrayList();
      for (int j = 0; j < top && m.find(); j++)
        resultList.add(m.group(1));
      if (resultList.size() == 0) continue;
      numObtained++;

      boolean isCorrect = false;
      for (int j = 0; j < resultList.size() && !isCorrect; j++) {
        String result = (String) resultList.get(j);
        for (int k = 0; k < answers.length && !isCorrect; k++) {
          if (answerIsRegExp)
            isCorrect = result.matches(answers[k]);
          else isCorrect = result.equals(answers[k]);
          if (isCorrect) {
            buf.append(" ");
            correct++;
          } else buf.append("*");
          buf.append(counter + "\t" + query + "\t" + result + "\t" + answers[k] + "\n");
        }
      }
    }
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(2);
    double p = ((double) correct / numObtained) * 100;
    double r = ((double) correct / numGold) * 100;
    double f1 = 2 * p * r / (p + r);
    StringBuffer resultBuf = new StringBuffer();
    resultBuf.append("\nEvaluation result of top " + top + " translations:\n");
    resultBuf.append("Correct: " + correct + "\tNum Gold: " + numGold
        + "\tNum Obtained: " + numObtained + "\n");
    resultBuf.append("Precision: " + nf.format(p) + "%\tRecall: " + nf.format(r)
        + "%\tF1: " + nf.format(f1) + "%");
    Helper.out(resultBuf.toString());
    File evalFile = new File(evalFilename + ".top" + top + ".eval.txt");
    Helper.out("Writing evaluation logs to: " + evalFile);
    Helper.writeToFile(evalFile, buf.toString() + resultBuf.toString());
  }
  
//  private static void evaluateResults(String targetLangID,
//                                      HashMap goldMap,
//                                      boolean lenient,
//                                      int top) {
//    int correct = 0, numGold = 0, numObtained = 0;
//    Pattern pat = Pattern.compile(">(.+?)</Trans>");
//    StringBuffer buf = new StringBuffer();
//    StringBuffer buf2 = new StringBuffer();
//    int counter = 1;
//
//    for (Iterator i = goldMap.keySet().iterator(); i.hasNext(); counter++) {
//      String key = (String) i.next();
//      String s = (String) goldMap.get(key);
//      if (s == null)
//        continue;
//      String golds[] = s.split("\t");
//      if (golds.length == 0)
//        continue;
//      for (int j = 0; j < golds.length; j++)
//        golds[j] = golds[j].toLowerCase().trim();
//      numGold++;
//
//      // key = key.trim().replaceAll("(^\"|\"$)", "").trim();
//      // key = "\"" + key.trim().replaceAll("(^\"|\"$)", "").trim() + "\"";
//      key = "\"" + Helper.trimQuotes(key) + "\"";
//      File resultFile = new File(h.getResultDir().getPath(), targetLangID + "."
//          + Helper.getQueryID(key) + ".xml");
//      String content = Helper.readFile(resultFile);
//      if (content == null)
//        continue;
//      Matcher m = pat.matcher(content);
//      List answerList = new ArrayList();
//      while (m.find())
//        answerList.add(m.group(1).toLowerCase().trim());
//      if (answerList.size() == 0)
//        continue;
//      numObtained++;
//
//      int prev_correct = correct;
//      for (int j = 0; j < answerList.size() && j < top
//          && prev_correct == correct; j++) {
//        String ans = (String) answerList.get(j);
//        if (Helper.empty(ans))
//          continue;
//        if (j == 0)
//          buf2.append(key.trim().replaceAll("(^\"|\"$)", "").trim() + "," + ans
//              + "\n");
//        for (int k = 0; k < golds.length && prev_correct == correct; k++) {
//          String gold = golds[k];
//          if (Helper.empty(gold))
//            continue;
//          if (!lenient && ans.equals(gold)) {
//            buf.append(" " + counter + "\t" + key + "\t" + ans + "\t" + gold
//                + "\n");
//            correct++;
//          } else if (lenient && h.hasSpace()
//              && (" " + ans + " ").indexOf(" " + gold + " ") != -1) {
//            buf.append(" " + counter + "\t" + key + "\t" + ans + "\t" + gold
//                + "\n");
//            correct++;
//          } else if (lenient && !h.hasSpace() && ans.indexOf(gold) != -1) {
//            buf.append(" " + counter + "\t" + key + "\t" + ans + "\t" + gold
//                + "\n");
//            correct++;
//          } else buf.append("*" + counter + "\t" + key + "\t" + ans + "\t"
//              + gold + "\n");
//        }
//      }
//    }
//    NumberFormat nf = NumberFormat.getInstance();
//    nf.setMaximumFractionDigits(2);
//    double p = ((double) correct / numObtained) * 100;
//    double r = ((double) correct / numGold) * 100;
//    double f1 = 2 * p * r / (p + r);
//    Helper.out("\nEvaluation result of top " + top + " translations "
//        + (lenient ? "with" : "without") + " lenient:");
//    Helper.out("Correct: " + correct + "\tNum Gold: " + numGold
//        + "\tNum Obtained: " + numObtained);
//    Helper.out("Precision: " + nf.format(p) + "%\tRecall: " + nf.format(r)
//        + "%\tF1: " + nf.format(f1) + "%");
//    Helper.writeToFile(new File(top + "." + (lenient ? 1 : 0) + ".result.txt"),
//                  buf.toString());
//    Helper.writeToFile(new File(top + "." + (lenient ? 1 : 0) + ".result2.txt"),
//                  buf2.toString());
//  }

//  private static HashMap getGoldTrans(File goldFile) {
//    HashMap data = new HashMap();
//    String content = Helper.readFile(goldFile);
//    String[] lines = content.split("\n");
//    for (int i = 0; i < lines.length; i++) {
//      if (Helper.empty(lines[i]))
//        continue;
//      // String e[] = (lines[i].indexOf("|") > -1) ? lines[i].split("[\t\\|]") :
//      // lines[i].split("[\t,]");
//      String e[] = lines[i].split("[\t,\\|]");
//      // if (e.length < 2) continue;
//      String key = Helper.trimQuotes(e[0]);
//      if (Helper.empty(key))
//        continue;
//      StringBuffer goldBuf = new StringBuffer();
//      for (int j = 1; j < e.length; j++) {
//        String gold = Helper.trimQuotes(e[j]);
//        if (Helper.empty(gold))
//          continue;
//        goldBuf.append(gold + ((j == e.length - 1) ? "" : "\t"));
//      }
//      data.put(key, goldBuf.toString());
//    }
//    return data;
//  }

  private static void usage() {
    Helper.out("Usage: java Ranet global_config_file target_lang_config_file "
        + "is_strict [ named_entity | dict_file ]");
  }
}
