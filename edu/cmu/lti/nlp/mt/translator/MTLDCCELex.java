/*
 * Frank Lin
 *
 * IMPORTANT: for now other than english -> simp or trad, all other ones have been commented out for efficiency reasons
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.nlp.CLang;

public class MTLDCCELex extends DictSFTranslator{
	public MTLDCCELex(){
		super("ldc_cedict.simp.v3","zh_TW, zh_CN");
	}	
	static final long serialVersionUID=1;
	
	private final static Pattern PAREN_PAT=Pattern.compile("[\\{\\(\\[].*?[\\}\\)\\]]");
	private final static Pattern DET_PAT=Pattern.compile("^((a)|(an)|(the)) ");

	Pattern pattern=Pattern.compile("(.+?)\\t/(.+)/");
	
	public boolean parseLine(String line){
		MapSS ms = new MapSS();
		
		Matcher matcher=pattern.matcher(line);
		if(!matcher.find()) return false;

		ms.put(CLang.zh_TW,matcher.group(1));
		ms.put(CLang.zh_CN,matcher.group(1));

		//String trans=matcher.group(1);
		String[] english=matcher.group(2).split("/");
		//add data to hashtables
		for(int i=0;i<english.length;i++){
			String word=english[i].trim().toLowerCase();
			word=PAREN_PAT.matcher(word).replaceAll("");
			word=DET_PAT.matcher(word).replaceAll("");
			if(word.length()==0) continue;
			ms.put(CLang.en_US,word);
			ms.put(CLang.en_US,word);
			dict.add(ms.get(src),ms.get(trg));				
		}
		return true;
	}

}
