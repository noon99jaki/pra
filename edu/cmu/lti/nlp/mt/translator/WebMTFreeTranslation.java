/*Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Pattern;

import edu.cmu.lti.nlp.CLang;

public class WebMTFreeTranslation extends WebTranslator{
	static final long serialVersionUID=1;
	//http://ets.freetranslation.com%3Fsequence=core&mode=html&charset=UTF-8&template=results_en-us.htm&language=English/Japanese&srctext=dog

	public WebMTFreeTranslation(){
		super(
				"http://ets"
				,"&sequence=core&mode=html&charset=UTF-8&srctext="
				,Pattern.compile(
					"Machine Translation -+?<br>\\s+(.+?)\\s+</p>"
				));	
		mms.getC(CLang.en_US).put(CLang.zh_CN,"6.freetranslation.com?language=English/SimplifiedChinese");
		mms.getC(CLang.en_US).put(CLang.zh_TW,"6.freetranslation.com?language=English/TraditionalChinese");
		mms.getC(CLang.en_US).put(CLang.ja_JP,".freetranslation.com?language=English/Japanese");
	}	
		//if(trgLang.equals(LangUtil.zh_CN)||trgLang.equals(LangUtil.zh_TW)){
		//	b.append("http://ets6.freetranslation.com?");

}
