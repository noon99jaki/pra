/*Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Pattern;

import edu.cmu.lti.nlp.CLang;

public class WebMTAmikai extends WebTranslator{
	static final long serialVersionUID=1;
	public WebMTAmikai(){
		super("http://standard.beta.amikai.com/amitext/indexUTF8.jsp?"
				,"&c_id=standard&lang=EN&translate=&sourceText="
				,Pattern.compile("<textarea name=\"translatedText\".+?>(.+?)\\n</textarea>")
				);	
		MAX_SOURCE_CHAR=100;
		mms.getC(CLang.en_US).put(CLang.zh_CN,"langpair=EN,ZH_CN");
		mms.getC(CLang.en_US).put(CLang.zh_TW,"langpair=EN,ZH_TW");
		mms.getC(CLang.en_US).put(CLang.ja_JP,"langpair=EN,JA");

		mms.getC(CLang.zh_CN).put(CLang.en_US,"langpair=ZH_CN,EN");
		mms.getC(CLang.zh_TW).put(CLang.en_US,"langpair=ZH_TW,EN");
		mms.getC(CLang.ja_JP).put(CLang.en_US,"langpair=JA,EN");
	}	

}
