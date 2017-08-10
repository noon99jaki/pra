package edu.cmu.lti.nlp.mt.translator;

import edu.cmu.lti.algorithm.container.VectorS;

public class MTIdentity extends OtherTranslator{
	
	static final long serialVersionUID=1;
	public MTIdentity(){
		super("en_US-zh_TW, en_US-zh_CN, en_US-ja_JP"
					,_all
			); 
	}
	
	public VectorS translate(String source,String srcLang,String trgLang){
		VectorS translations=new VectorS();
		translations.add(source);
		return translations;
	}
	
}
