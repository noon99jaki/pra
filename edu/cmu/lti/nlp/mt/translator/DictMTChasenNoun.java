/*
 * Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictMTChasenNoun extends DictSFTranslator{
	public DictMTChasenNoun(){
		super("chasen-noun-dict.txt", "en_US, ja_JP");
		cutOff=1;
	}	
	static final long serialVersionUID=1;
		
	Pattern pattern=Pattern.compile("(.+?)\\,(.+?)\\,(.+?)\\,(.+?)\\,(.+?)\\,(.+?)\\,(.+?)\\,(.+?)\\,(.+?)\\,(.+?)");
	public boolean parseLine(String line ){
		Matcher matcher=pattern.matcher(line);
		if(!matcher.matches()) return false;
		String w1= matcher.group(1);
		String w2= matcher.group(10);
		//Double d= Double.parseDouble(matcher.group(2));
		dict.add(w1,w2);
		//dict.getC(w1).plusOn(w2,d);
		return true;
	}
}
