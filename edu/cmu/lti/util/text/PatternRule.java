/**
 * 
 */
package edu.cmu.lti.util.text;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nlao
 *
 */
public  class PatternRule{
	public String type;
	public Pattern p;
	public double score;
	public PatternRule(String type, String pattern, double score){
		this.type = type;
		p= Pattern.compile(pattern);
		this.score = score;
	}
	public boolean match(String txt){
    	Matcher m = p.matcher( txt );
    	return m.find();
	}
	public String match(String txt, int matchIndex){
		String result = null;
    	Matcher m = p.matcher( txt );
    	if ( m.find() ) {
    		result = m.group(matchIndex);
    	}
    	return result;
	}
	public String toString(){
		return type+" "+score+" "+p.toString();
	}
}