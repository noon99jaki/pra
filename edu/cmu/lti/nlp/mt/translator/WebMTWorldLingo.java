/*Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Pattern;

import edu.cmu.lti.nlp.CLang;

public class WebMTWorldLingo extends WebTranslator{
	static final long serialVersionUID=1;
	// http://www.worldlingo.com/wl/translate/ja/microsoft/computer_translation.html?wl_text=dog&wl_gloss=1&wl_srclang=EN&wl_trglang=ja
	// <textarea name="wl_result" rows="7" wrap="VIRTUAL" cols="25">犬</textarea></span>

	public WebMTWorldLingo(){
		super(
				"http://www.worldlingo.com/wl/translate/ja/microsoft/computer_translation.html?"
				,"&wl_gloss=1&wl_text="
				,Pattern.compile(
						"<textarea name\\=\"wl_result\"[^>]+?>(.+?)</textarea>"
				));	
		mms.getC(CLang.en_US).put(CLang.zh_CN,"wl_srclang=EN&wl_trglang=zh_cn");
		mms.getC(CLang.en_US).put(CLang.zh_TW,"wl_srclang=EN&wl_trglang=zh_tw");
		mms.getC(CLang.en_US).put(CLang.ja_JP,"wl_srclang=EN&wl_trglang=ja");

		//mms.getC(CLang.zh_CN).put(CLang.en_US,"&langpair=zh-CN|en");
		//mms.getC(CLang.en_US).put(CLang.zh_TW,"&langpair=zh-TW|en");
		//mms.getC(CLang.en_US).put(CLang.ja_JP,"&langpair=ja|en");
		//this.ms_tgtEncoding.put(CLang.zh_TW, CEncoding.BIG5);
	}	
}
