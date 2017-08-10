/* Frank Lin
 *
 * Generates Chinese numbers
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.CLang;

public class MTChineseYear extends OtherTranslator{
	
	static final long serialVersionUID=1;
	public MTChineseYear(){
		super("en_US-zh_TW, en_US-zh_CN"
					,"UNKNOWN, TERM, TERM.NUMBER, TERM.YEAR"
			); 
	}

	private static final String ROMAN_NUM_CHARS="0123456789";
	private static final String CHINESE_NUM_CHARS="0\u4E00\u4E8C\u4E09\u56DB\u4E94\u516D\u4E03\u516B\u4E5D";
 
	public VectorS translate(String source,String srcLang,String trgLang){
		VectorS targets=new VectorS();
		if(source.matches("\\d+")){
			String target=source;
			for(int i=0;i<ROMAN_NUM_CHARS.length();i++){
				target=target.replace(ROMAN_NUM_CHARS.charAt(i),CHINESE_NUM_CHARS.charAt(i));
			}
			targets.add(target);
			if(source.equals("2000")){
				targets.add("\u4E8C\u5343\u5E74");
				targets.add("\u5169\u5343\u5E74");
			}
		}
		return targets;
	}
  
  public static void main(String[] args)throws Exception{
    
    PrintWriter writer=new PrintWriter(new OutputStreamWriter(System.out,"UTF8"),true);
    BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
    MTChineseYear t=new MTChineseYear();
    for(String nextLine;
    !(nextLine=reader.readLine()).equalsIgnoreCase("quit");  ){
    	writer.println(t.translate(nextLine,CLang.en_US,CLang.zh_CN));
    }
    
  }
  
}
