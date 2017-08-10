package edu.cmu.lti.nlp.mt.translator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.run.Param;


/**
 * Japanese Year Transliterator
 * @author Hideki Shima
 * $Id: YearTransliterator.java,v 1.1 2006/10/19 02:02:31 frank Exp $
 */

public class MTJapaneseYear extends OtherTranslator{
	
	static final long serialVersionUID=1;
	public MTJapaneseYear(){
		super("en_US-ja_JP"
					,"TERM.YEAR"
			); 
	}
	public String dictDir= Param.path_proj+ "/data/tm/dictionaries/"	;
		
	private String zero;
	private String one;
	private String two;
	private String three;
	private String four;
	private String five;
	private String six;
	private String seven;
	private String eight;
	private String nine;
	private String nen;
	
	public void initialize(){
		try {
			FileInputStream fis = new FileInputStream(dictDir+"/wide.txt");
			InputStreamReader isr = new InputStreamReader(fis,"utf8");
			BufferedReader reader = new BufferedReader(isr);
			String nextLine;
			while ((nextLine=reader.readLine())!=null) {
				nextLine = nextLine.trim();
				nextLine = nextLine.replaceAll("\\s*=\\s*","=");
		        if (nextLine.length()>0 && !nextLine.startsWith("#")) {
		        	if (nextLine.startsWith("zero4")) zero = nextLine.substring(nextLine.indexOf("=")+1);
		        	if (nextLine.startsWith("one4")) one = nextLine.substring(nextLine.indexOf("=")+1);
		        	if (nextLine.startsWith("two4")) two = nextLine.substring(nextLine.indexOf("=")+1);
		        	if (nextLine.startsWith("three4")) three = nextLine.substring(nextLine.indexOf("=")+1);
		        	if (nextLine.startsWith("four4")) four = nextLine.substring(nextLine.indexOf("=")+1);
		        	if (nextLine.startsWith("five4")) five = nextLine.substring(nextLine.indexOf("=")+1);
		        	if (nextLine.startsWith("six4")) six = nextLine.substring(nextLine.indexOf("=")+1);
		        	if (nextLine.startsWith("seven4")) seven = nextLine.substring(nextLine.indexOf("=")+1);
		        	if (nextLine.startsWith("eight4")) eight = nextLine.substring(nextLine.indexOf("=")+1);
		        	if (nextLine.startsWith("nine4")) nine = nextLine.substring(nextLine.indexOf("=")+1);
		        	if (nextLine.startsWith("nen4")) nen = nextLine.substring(nextLine.indexOf("=")+1);
		        }
			}
	    } catch(Exception e) {
	      e.printStackTrace();
	      System.err.println("Bad Japanese number pattern file!");
	    }
	}
	
	public VectorS translate(String source,String srcLang,String trgLang){

		VectorS result=new VectorS();
		// parse the command line argument
		
		String text = source;
		text = text.replaceFirst("19","");
		
		text = text.replaceAll("0",zero);
		text = text.replaceAll("1",one);
		text = text.replaceAll("2",two);
		text = text.replaceAll("3",three);
		text = text.replaceAll("4",four);
		text = text.replaceAll("5",five);
		text = text.replaceAll("6",six);
		text = text.replaceAll("7",seven);
		text = text.replaceAll("8",eight);
		text = text.replaceAll("9",nine);
		
		text += nen;
		
		result.add(text);
		
		return result;
		
	}
	
}
