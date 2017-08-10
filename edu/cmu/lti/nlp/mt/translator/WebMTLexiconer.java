/*Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.CEncoding;
import edu.cmu.lti.nlp.CLang;

public class WebMTLexiconer extends WebTranslator{
	static final long serialVersionUID=1;
	public WebMTLexiconer(){
		super(
				"http://www.lexiconer.com/"
				,""
				,Pattern.compile(
					"<div id\\=result_box dir\\=ltr>(.+?)</div>"
				));	
		mms.getC(CLang.en_US).put(CLang.zh_CN,"ecdict.php?txtinputenglish=");
		mms.getC(CLang.en_US).put(CLang.zh_TW,"ecdict_big5.php?txtinputenglish=");
		//mms.getC(CLang.en_US).put(CLang.ja_JP,"&langpair=en|ja");

		//mms.getC(CLang.zh_CN).put(CLang.en_US,"&langpair=zh-CN|en");
		//mms.getC(CLang.en_US).put(CLang.zh_TW,"&langpair=zh-TW|en");
		//mms.getC(CLang.en_US).put(CLang.ja_JP,"&langpair=ja|en");
		this.ms_tgtEncoding.put(CLang.zh_TW, CEncoding.BIG5);
		this.ms_tgtEncoding.put(CLang.zh_CN, CEncoding.GBK);
		setTypeCap(_term);
	}	

	private static final Pattern MAIN_PAT=Pattern.compile("\\. (.+?) \\&nbsp\\; \\&nbsp\\; \\[");



	public VectorS getTargets(String page,String srcLang,String trgLang){
		VectorS targets=new VectorS();
		if(!(trgLang.equals(CLang.zh_CN)||trgLang.equals(CLang.zh_TW)))
			return targets;
		if(page.indexOf("No word matching")!=-1)		return targets;

		Matcher m1=MAIN_PAT.matcher(page);
		if(!m1.find())			return targets;
		
		String[] senses=m1.group(1).split("[a-z]+\\.\\s+");
		for(int i=0;i<senses.length;i++){
			String[] words=senses[i].split("[\uff08\uff09\u7684\\s\\p{Punct}]+");
			for(int j=0;j<words.length;j++){
				if(words[j].length()>0){
					targets.add(words[j]);
				}
			}
		}
		return targets;
	}

}
