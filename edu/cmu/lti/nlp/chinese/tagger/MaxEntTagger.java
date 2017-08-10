package edu.cmu.lti.nlp.chinese.tagger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.nlp.Interfaces.ITagPOS;
import edu.cmu.lti.nlp.chinese.tagger.MaxEntTaggerService.IServer;
import edu.cmu.lti.nlp.chinese.util.Tree;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
import edu.cmu.lti.util.system.MyProcess;

/**
 * This class read in tree files and output new trees with head information marked
 * in a pretty printing format
 * During this process, FRAG rooted trees are eliminated from corpus
 */

public class MaxEntTagger implements 	ITagPOS , IServer	{
	private static MaxEntTagger instance = null;

	public static MaxEntTagger getInstance() {
		if (instance == null) instance = new MaxEntTagger();
		return instance;
	}

  public  int LANG = Tree.CHINESE;
	public static class Param extends edu.cmu.lti.util.run.Param {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		//public String lang;
		public  String maxent;
		//private  String model;
		public  boolean commandLineMode = false;
	  //default langauge Chinese

	  
		public Param() {
			super(MaxEntTagger.class);
			parse();
		}

		public void parse() {
			maxent = getString("maxent");
			//lang = getString("lang");
		}
	}

	public Param p = new Param();
	
	MyProcess v_proc[]  = new MyProcess[2];
	MyProcess proc1  = new MyProcess();
	MyProcess proc2  = new MyProcess();
	

  private MaxEntTagger(){
  	proc1.start(p.maxent,"-p","-m", p.path_data +"/model1");
  	proc2.start(p.maxent,"-p","-m", p.path_data +"/model2");
  	v_proc[0]=proc1;
  	v_proc[1]=proc2;
  	
  	try{
  		Thread.sleep(1000);
    }catch(Exception ex){
      ex.printStackTrace();
    }
    //proc1.printErr();   proc2.printErr();
  }

  // Private helper functions

  public void destroy(){
  	proc1.destroy();
  	proc2.destroy();
  }
  public void main(String[] args){
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
      p.commandLineMode = true;
      MaxEntTagger tagger =  MaxEntTagger.getInstance();
      tagger.processInput();

    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

  public List<String> twoPassTag(List<String[]> list){
    return twoPassTag(list, LANG);
  }

  /**
   *  Produce POS using the two-pass model
   *  Tag each word based on Maxent classifier decisions
   *  @param list input is a list of String arrays
   *  index 0 is pos (initially set to null), index 1 is word
   */
  public List<String> twoPassTag(List<String[]> list, int lang){
    String line = null;
    List<String> returnPOSList = new ArrayList<String>();
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
          
          v_proc[pass-1].write(str);
 
          line = null;
          String action = null;
          line = v_proc[pass-1].readLine();
          if(line.length() == 0)
            continue;
          action = line;  
          if(pass == 1)
            list.get(i)[0] = action+"";
          if(pass == 2){
            if(p.commandLineMode == true)
              System.out.println(action);
            else  
              returnPOSList.add(action);
          }  
        }
      }
      return returnPOSList;

  } 

  /**
   *  Produce POS using the two-pass model
   *  Tag each word based on Maxent classifier decisions
   *  @param wordList input is a list of words (Strings)
   */
  public List<String> tag(List<String> wordList, int lang){
    List<String[]> emptyPOSInsertedList = new ArrayList<String[]>(wordList.size());
    for(int i=0;i<wordList.size();i++){
      emptyPOSInsertedList.add(new String[]{null,wordList.get(i)});
    }
    return twoPassTag(emptyPOSInsertedList, lang);
  }
  public VectorToken  tagPOSRMI(VectorToken vt )  throws RemoteException{
  	return tagPOS(vt);
  }  
  public VectorToken  tagPOS(VectorToken vt ){
    List<String[]> emptyPOSInsertedList = 
    	new ArrayList<String[]>(vt.size());
    for(int i=0;i<vt.size();i++){
      emptyPOSInsertedList.add(
      		new String[]{null,vt.get(i).getText()});
    }
    List<String> vs= twoPassTag(emptyPOSInsertedList, Tree.CHINESE);
    
    for(int i=0;i<vt.size();i++){
    	Token t = vt.get(i);
   		t.setPOS(vs.get(i));
    } 	
  	return vt;
  } 
  /**
  * Read from STDIN, for Chinese input is assumed to be in UTF8 encoding
  * the format for each input sentence is:
  * <S>
  * Word 1
  * Word 2
  * .
  * .
  * .
  * Word N
  * </S>
  * <S>
  * next sentence ... etc
  * </S>
  */
  private void processInput(){
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
