package edu.cmu.lti.nlp.chinese;


import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.nlp.Interfaces.ITagNE;
import edu.cmu.lti.nlp.Interfaces.Tag;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

//edu.cmu.lti.nlp.chinese.Identifinder
public class Identifinder extends SOAPClient implements ITagNE{
  //private static Logger log = Logger.getLogger( IdentifinderSOAPClient.class );
	private static  Identifinder instance =null;
	public static Identifinder getInstance() {
		if (instance==null) 	 instance = new Identifinder();		
		return instance;
	}	

  public Identifinder(){
  	super(Identifinder.class);
  }


  private Pattern pa_NE = Pattern.compile(
  		"<ENAMEX TYPE=\"(.+?)\">(.+?)</ENAMEX>");
  //private TVector<NETag> extractNETag(String idfOut){
  
  public VectorX<Tag> tagNE(String toSegment){
    String idfOut = pushPop(toSegment);
    VectorX<Tag> v_NE = new VectorX<Tag>(Tag.class);
    
    Matcher matcher = pa_NE.matcher(idfOut);
    StringBuffer txtRaw = new StringBuffer();
    int iLast = 0;
    for(;matcher.find(iLast);iLast = matcher.end()){
      int ibm = matcher.start();
      String type = matcher.group(1);
      String txt = matcher.group(2);
      txtRaw.append(idfOut.substring(iLast, ibm));
      int ib = txtRaw.length();
      //Beware that the BBN's NE tags may have space inside of them
      txtRaw.append(txt);
      int ie = txtRaw.length();
      v_NE.add(new Tag(ib, ie, type, txt));      
    }
    txtRaw.append(idfOut.substring(iLast));
  	return v_NE;
  }
  //StringBuffer txtRaw;
  

  
  public void processInput(){
    BufferedReader br = FSystem.getStdIn();
    String line = null;
    while( (line = FFile.readLine(br)) != null){
      System.err.println("input:"+line);
      //String output = client.getIdentifinderOutput(line);
      //System.err.println("server output:"+output);
    }  	
  }

  public static void main(String[] args) {  
    try {
    	Identifinder.getInstance().processInput();
    }
    catch (Exception e) {
      System.err.println(e); 
      e.printStackTrace();
    }
  } 
} 

