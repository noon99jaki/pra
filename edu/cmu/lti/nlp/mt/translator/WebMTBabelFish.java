/*Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;
import java.util.regex.Pattern;

import edu.cmu.lti.nlp.CLang;

public class WebMTBabelFish extends WebTranslator{	
	static final long serialVersionUID=1;
	public WebMTBabelFish(){
		super("http://babelfish.altavista.com/babelfish/tr?"
			,"&c_id=standard&lang=EN&translate=&urltext="
			,Pattern.compile("<div style\\=padding\\:10px\\;>(.+?)</div>")
			);	
		mms.getC(CLang.en_US).put(CLang.zh_CN,"lp=en_zh");
		mms.getC(CLang.en_US).put(CLang.zh_TW,"lp=en_zt");
		mms.getC(CLang.en_US).put(CLang.ja_JP,"lp=en_ja");

		mms.getC(CLang.zh_CN).put(CLang.en_US,"lp=zh_en");
		mms.getC(CLang.zh_TW).put(CLang.en_US,"lp=zt_en");
		mms.getC(CLang.ja_JP).put(CLang.en_US,"lp=ja_en");
	}	

}
