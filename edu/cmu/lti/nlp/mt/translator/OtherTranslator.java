/**
 * 
 */
package edu.cmu.lti.nlp.mt.translator;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.util.text.FString;

/**
 * @author nlao
 *
 */
public abstract class OtherTranslator extends Translator{
	//public String dictDir= Param.proj_path+ "/data/tm/dictionaries/"	;

	public SetS langCap;
	public void setLangCap(String langCap){
		this.langCap=(SetS) FString.tokenize(langCap).toSet();		
	}
	public boolean isCapable(	String srcLang,String trgLang){
		return langCap.contains(srcLang+"-"+trgLang);
	}
	
	public OtherTranslator(String langCap, String typeCap){
		super(typeCap);
		setLangCap(langCap);
	}

	//public abstract VectorS translate(String source,String srcLang,String trgLang);

//	public void init() throws Exception{}	
}
