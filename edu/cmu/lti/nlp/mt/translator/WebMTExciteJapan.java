/*Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Pattern;

import edu.cmu.lti.nlp.CEncoding;
import edu.cmu.lti.nlp.CLang;

public class WebMTExciteJapan extends WebTranslator{
	static final long serialVersionUID=1;
	// http://www.excite.co.jp/world/english/?before=dog&wb_lp=ENJA
	// <textarea cols=36 rows=15 name="after" wrap="virtual" style="width:320px;">犬\n</textarea>
	public WebMTExciteJapan(){
		super(
			"http://www.excite.co.jp/world/english/?"
			,"&c_id=standard&lang=EN&translate=&before="
			,Pattern.compile(
					"<textarea [^>]+? name\\=\"after\" [^>]+?>(.+?)\\s</textarea>"
					,Pattern.DOTALL)		
			);	
		mms.getC(CLang.en_US).put(CLang.ja_JP,"wb_lp=ENJA");
		mms.getC(CLang.ja_JP).put(CLang.en_US,"wb_lp=JAEN");
		//this.tgtEncoding="Shift-JIS";
		this.ms_tgtEncoding.put(CLang.ja_JP, CEncoding.Shift_JIS);
	}	

}
