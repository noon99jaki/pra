/*Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Pattern;

import edu.cmu.lti.nlp.CEncoding;
import edu.cmu.lti.nlp.CLang;

public class WebMTFreshEye extends WebTranslator{	
	static final long serialVersionUID=1;
	// http://mt.fresheye.com/ft_result.cgi?e=EJ&gen_text=dog
	// <TD bgcolor="#FFFFFF">　犬</TD>

	public WebMTFreshEye(){
		super(
				"http://mt.fresheye.com/ft_result.cgi?"
				,"&gen_text="
				,Pattern.compile(
						"<TD bgcolor\\=\"#FFFFFF\">(.+?)</TD>"
				));	
		mms.getC(CLang.en_US).put(CLang.ja_JP,"e=EJ");
		mms.getC(CLang.ja_JP).put(CLang.en_US,"e=JE");
		//this.tgtEncoding = "Shift-JIS";
		this.ms_tgtEncoding.put(CLang.ja_JP, CEncoding.Shift_JIS);
	}
}
