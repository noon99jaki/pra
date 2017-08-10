/**
 * 
 */
package edu.cmu.lti.util.text;

import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.algorithm.container.VectorX;

/**
 * @author nlao
 *
 */
public class VectorPatternRule extends VectorX<PatternRule>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public void add( String type, String pattern, double score ) {
		add(new PatternRule( type,  pattern,  score));
	}
	
	public VectorPatternRule(){
		super(PatternRule.class);
	}
	
	public MapSD match( String txt ) {
		MapSD m = new MapSD();
		for ( PatternRule r: this ){
			if ( r.match( txt ) ){
				m.plusOn(r.type,r.score);
		   	//System.out.println(r.toString() +" matched in: "+txt);
			}
		}
		return m;
	}
	
	public Result match( String txt, int matchGroupIndex ) {
		MapSD m = new MapSD();
		Result result = null;
		for ( PatternRule r: this ){
			String matched = r.match( txt, matchGroupIndex );
			if ( matched != null ){
				m.plusOn(r.type,r.score);
				result = new Result( m, matched );
				break;
			}
		}
		return result;
	}
	
	public class Result {
		public MapSD m;
		public String matched;
		public Result( MapSD m, String matched ) {
			this.m = m;
			this.matched = matched;
		}
	}
	
}
