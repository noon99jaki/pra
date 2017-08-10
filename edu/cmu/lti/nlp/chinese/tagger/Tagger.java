package edu.cmu.lti.nlp.chinese.tagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.cmu.lti.nlp.chinese.util.Tree;

/**
 * This class read in tree files and output new trees with head information marked
 * in a pretty printing format
 * During this process, FRAG rooted trees are eliminated from corpus
 */

public class Tagger{
  //private static Logger log = Logger.getLogger( Tagger.class );

  //two external Maxent proceses
  private static ProcessBuilder classifierPB;
  private static Process classifier[] = new Process[2];

  private static BufferedWriter cInput[] = new BufferedWriter[2];
  private static BufferedReader cOutput[] = new BufferedReader[2];

  private static boolean commandLineMode = false;
  private static boolean initialized = false;

  private static String maxentPath;
  private static String[] modelFile = new String[2];

  //default langauge Chinese
  private static int LANG = Tree.CHINESE;

  private static void initialize(){
    if(initialized)
      return;
    loadProperties();  
    try{
      for(int i=0;i<2;i++){
		    classifierPB = new ProcessBuilder(maxentPath,"-p","-m",modelFile[i]);
		    classifier[i] = classifierPB.start();
		    cOutput[i] = new BufferedReader(new InputStreamReader(classifier[i].getInputStream()));
		    cInput[i] = new BufferedWriter(new OutputStreamWriter(classifier[i].getOutputStream(), "UTF8"));
        classifier[i].getErrorStream().close();
      }
      Thread.sleep(3000);
    }catch(Exception ex){
      System.err.println("Error in static initialization of Tagger");
      ex.printStackTrace();
      System.exit(-1);
    }
    initialized = true;
  }

  // Private helper functions
  private static void loadProperties() {
    Properties properties = new Properties();
    try { 
      File userProperties = new File( System.getProperty("javelin.home")+ "/conf", Tagger.class.getName() + ".properties");
      if(!userProperties.exists())
        throw new IOException("Missing properties file for "+ Tagger.class.getName());
      properties.load(new FileInputStream( userProperties));

    //create two external processes and open pipes for communicating with them
      maxentPath= ((String)properties.get("maxent")).trim();
      modelFile[0]= ((String)properties.get("tagger_pass1_model")).trim();
      modelFile[1] = ((String)properties.get("tagger_pass2_model")).trim();

      maxentPath = new File(maxentPath).getAbsolutePath();
      modelFile[0] = new File(modelFile[0]).getAbsolutePath();
      modelFile[1] = new File(modelFile[1]).getAbsolutePath();

    } catch ( Exception e ) {
        System.err.println( "Caught exception while loading properties: "
          + e.getMessage() );
        e.printStackTrace();
    }
  }

  public static void main(String[] args){
    if(args.length > 1){
      System.err.println("Usage: java Tagger [LANG(E|C)]");
      System.exit(-1);
    }
    try{
      //Language options, E for parsing English, C for parsing Chinese
      if(args.length > 0){
        if(args[0].equals("E"))
          LANG = Tree.ENGLISH;
        else if(args[0].equals("C"))
          LANG = Tree.CHINESE;
        else{
          System.err.println("Usage: language options: E|C");
          System.err.println("Usage: language options: E|C");
          System.exit(-1);
        }
      }
      commandLineMode = true;
      initialize();
      //read from STDIN and parse sentences that start with <S> on a line, and ends with </S>
      processInput();

      //clean up the two external processes
      for(int i=0;i<2;i++){
        cInput[i].close();
        cOutput[i].close();
        classifier[i].destroy();
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

  public static List<String> twoPassTag(List<String[]> list){
    return twoPassTag(list, LANG);
  }

  /**
   *  Produce POS using the two-pass model
   *  Tag each word based on Maxent classifier decisions
   *  @param list input is a list of String arrays
   *  index 0 is pos (initially set to null), index 1 is word
   */
  public static List<String> twoPassTag(List<String[]> list, int lang){
    if(initialized == false)
      initialize();
    String line = null;
    List<String> returnPOSList = new ArrayList<String>();
    try{
      for(int pass=1;pass<=2;pass++){
        for(int i=0;i<list.size();i++){
          String word = list.get(i)[1];
          String feature = FeatureExtractor.extractFeature(list,i,pass,lang);
          
          StringBuffer sb = new StringBuffer();
          sb.append("? ");
          sb.append(feature);
          sb.append("\n");
          String str = sb.toString();
          //System.out.println("writing to classifier");
          cInput[pass-1].write(str,0,str.length());
          cInput[pass-1].flush();

          line = null;
          String action = null;
          line = cOutput[pass-1].readLine();
          line = line.trim();
          if(line.length() == 0)
            continue;
          action = line;  
          if(pass == 1)
            list.get(i)[0] = action+"";
          if(pass == 2){
            if(commandLineMode == true)
              System.out.println(action);
            else  
              returnPOSList.add(action);
          }  
        }
      }
      return returnPOSList;
    }catch(Exception ex){
      ex.printStackTrace();
      return null;
    }
  } 

  /**
   *  Produce POS using the two-pass model
   *  Tag each word based on Maxent classifier decisions
   *  @param wordList input is a list of words (Strings)
   */
  public static List<String> tag(List<String> wordList, int lang){
    List<String[]> emptyPOSInsertedList = new ArrayList<String[]>(wordList.size());
    for(int i=0;i<wordList.size();i++){
      emptyPOSInsertedList.add(new String[]{null,wordList.get(i)});
    }
    return twoPassTag(emptyPOSInsertedList, lang);
  }
  
  /**
  * Read from STDIN, for Chinese input is assumed to be in UTF8 encoding
  * the format for each input sentence is:
  * &lt;S&gt;
  * Word 1
  * Word 2
  * .
  * .
  * .
  * Word N
  * &lt;/S&gt;
  * &lt;S&gt;
  * next sentence ... etc
  * &lt;/S&gt;
  */
  private static void processInput(){
    try{
      BufferedReader br = null;
      String line = null;

      if(LANG  == Tree.CHINESE)
        br = new BufferedReader(new InputStreamReader(System.in, "UTF8"));
      else
        br = new BufferedReader(new InputStreamReader(System.in));
        
      while( (line = br.readLine()) != null){
        if(line.startsWith("<S")){
          System.out.println(line);
          List<String[]> list = new ArrayList<String[]>();
          while(true){
            line = br.readLine();
            if(line == null){
              System.err.println("Error in processFile method: Ill-formated input tree file, <S> tag not closed by </S>");
              //System.exit(1);
            }
            if(line.equals("</S>")){
              break;
            }  
            if(line.trim().equals("("))
              line = "（";
            else if(line.trim().equals(")"))
              line = "）";
            list.add(new String[]{null,line.trim()});
          }
          if(list.size() > 0){
            twoPassTag(list, LANG); 
          }
          System.out.println("</S>");
        }
      }  
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

}
