/*
 * Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Pattern;

import edu.cmu.lti.nlp.CLang;

public class WebMTGoogle extends WebTranslator{
	
	static final long serialVersionUID=1;
	public WebMTGoogle(){
		super(
				"http://translate.google.com/translate_t?"
				,"&ie=UTF8&oe=UTF8&text="
				,Pattern.compile(
					"<div id\\=result_box dir\\=\\\"ltr\\\">(.+?)</div>"
				));	
		mms.getC(CLang.en_US).put(CLang.zh_CN,"langpair=en|zh-CN");
		mms.getC(CLang.en_US).put(CLang.zh_TW,"langpair=en|zh-TW");
		mms.getC(CLang.en_US).put(CLang.ja_JP,"langpair=en|ja");

		mms.getC(CLang.zh_CN).put(CLang.en_US,"langpair=zh-CN|en");
		mms.getC(CLang.zh_TW).put(CLang.en_US,"langpair=zh-TW|en");
		mms.getC(CLang.ja_JP).put(CLang.en_US,"langpair=ja|en");
	}	
}
