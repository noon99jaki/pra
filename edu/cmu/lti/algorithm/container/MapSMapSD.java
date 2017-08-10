/**
 * 
 */
package edu.cmu.lti.algorithm.container;


/**
 * @author nlao
 */


public class MapSMapSD extends MapSX< MapSD>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSMapSD newInstance(){
		return new MapSMapSD();
	}	

	public MapSD newValue(){
		return new MapSD();
	}	
	public MapSMapSD(){
		super(MapSD.class);
	}	
	public MapSMapSD plusOn(String k1, String k2, double x){
		getC(k1).plusOn(k2, x);
		return this;
	}
}
