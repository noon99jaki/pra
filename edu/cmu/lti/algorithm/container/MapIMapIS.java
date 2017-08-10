/**
 * 
 */
package edu.cmu.lti.algorithm.container;

/**
 * @author nlao
 *
 */
public class MapIMapIS  extends MapIMapIX<String>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapIMapIS newInstance(){
		return new MapIMapIS();
	}	

	public MapIS newValue(){
		return new MapIS();
	}	
	
	public MapIMapIS(){
		super(String.class);
	}	
}