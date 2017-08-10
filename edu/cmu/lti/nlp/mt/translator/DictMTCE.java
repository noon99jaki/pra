/*
 * Frank Lin
 *
 * IMPORTANT: for now other than english -> simp or trad, 
 * all other ones have been commented out for efficiency reasons
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.nlp.CLang;

public class DictMTCE extends DictSFTranslator{
	
	public DictMTCE(){
		super("cedict_ts.u8","en_US, zh_TW, zh_CN");
		cutOff=20;
	}
	static final long serialVersionUID=1;
		
	
	private final static Pattern LINE_PAT=Pattern.compile("(.+?) (.+?) \\[(.+?)\\] /(.*)/");
	private final static Pattern PAREN_PAT=Pattern.compile("[\\{\\(\\[].*?[\\}\\)\\]]");
	private final static Pattern TO_PAT=Pattern.compile("^to");
	private final static Pattern DET_PAT=Pattern.compile("^((a)|(an)|(the)) ");
	private final static Pattern OMIT_PAT=Pattern.compile("\\.\\.\\.");
	private final static Pattern QUOTE_PAT=Pattern.compile("\"");
	private final static Pattern SPACES_PAT=Pattern.compile("\\s\\s+");
	public boolean parseLine(String line){
		Matcher matcher=LINE_PAT.matcher(line);
		//Find line
		if(!matcher.find()) return false;
		
		ms.put(CLang.zh_TW,matcher.group(1));
		ms.put(CLang.zh_CN,matcher.group(2));
		String[] senses=matcher.group(4).split("/");
		
		//Do some processing with each translation
		for(int i=0;i<senses.length;i++){
			senses[i]=PAREN_PAT.matcher(senses[i]).replaceAll("").trim();
			String[] trans=senses[i].split(",;");
			for(int j=0;j<trans.length;j++){
				String word=trans[j].trim();
				word=TO_PAT.matcher(word).replaceAll("").trim();
				word=DET_PAT.matcher(word).replaceAll("").trim();
				word=OMIT_PAT.matcher(word).replaceAll("").trim();
				word=QUOTE_PAT.matcher(word).replaceAll("").trim();
				word=SPACES_PAT.matcher(word).replaceAll("").trim();
				if(word.length()==0)
					continue;
				ms.put(CLang.en_US,word);
				addItem();
				//dict.getC(ms.get(src)).plusOn(ms.get(trg));
			}
		}
		return true;
	}



}
