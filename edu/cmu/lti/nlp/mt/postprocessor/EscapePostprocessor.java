package edu.cmu.lti.nlp.mt.postprocessor;

import edu.cmu.lti.nlp.mt.util.TextUtil;

public class EscapePostprocessor extends Postprocessor{

	public String process(String text,String srcLang,String trgLang){
		
		String processed=text;
		processed=TextUtil.decodeCommonXmlEscapes(processed);
		processed=TextUtil.decodeUnicodeEscapes(processed);
		return processed;
		
	}

}
