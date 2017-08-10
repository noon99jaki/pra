/*
 * Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.io.Serializable;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.mt.MTResult;
import edu.cmu.lti.util.text.FString;
//extends MTProcessor 
public abstract class Translator implements Serializable{
	public static final String _all=
		"UNKNOWN, SENTENCE, TERM, TERM.GENERAL, TERM.NUMBER, TERM.YEAR, TERM.ACRONYM TERM.OOV";
	public static final String _all_term=
		"UNKNOWN, TERM, TERM.GENERAL, TERM.NUMBER, TERM.YEAR, TERM.ACRONYM, TERM.OOV";
	public static final String _term=
		"UNKNOWN, TERM, TERM.GENERAL, TERM.ACRONYM, TERM.OOV";

	public int cutOff;
	//public Translator(){}
	//public Translator(ParamMT p){	super(p);	}	
	public SetS typeCap;
	protected Translator(String typeCap){
		setTypeCap(typeCap);
	}
	public void setTypeCap(String typeCap){
		this.typeCap=(SetS) FString.tokenize(typeCap).toSet();		
	}
	public boolean isCapable(
			String srcLang,String trgLang,String type){
		return isCapable(srcLang,trgLang)&&typeCap.contains(type);
	}
	public abstract boolean isCapable(String srcLang,String trgLang);
	public boolean isCapable(MTResult t){			
		return isCapable(t.srcLang, t.trgLang, t.type);
	}	
//	public boolean isCapable(	String srcLang,String trgLang){
//		String langPair=srcLang+"-"+trgLang;
//		return langCap.contains(langPair);	}	
	
	public String toString() {
		return this.getClass().getSimpleName();
	}
	public abstract VectorS translate(
			String source,String srcLang,String trgLang);
	
	public void init(String srcLang,String trgLang){
		
	}

}
