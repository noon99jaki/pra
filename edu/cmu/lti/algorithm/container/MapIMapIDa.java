/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.Map;


/**
 * @author nlao
 *
 */
public class MapIMapIDa extends MapXMapXD<Integer, Integer>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapIMapIDa newInstance(){
		return new MapIMapIDa();
	}	
	public Integer newKey(){//needed for primitive classes, silly java
		return 0;
	}	
	public MapID newValue(){
		return new MapID();
	}	
	
	public MapIMapIDa(){
		super(Integer.class, Integer.class);
	}	
	//public void plusOn(int i, int j, Double x){		
	//getC(i).plusOn(j,x);	}		

	public MapIMapIDa plusOn(int i, MapXD<Integer> m){
		getC(i).plusOn(m);
		return this;
	}	
	public MapIMapIDa minusOn(int i, MapXD<Integer> m){
		getC(i).minusOn(m);
		return this;
	}		
	public MapIMapIDa plusOn( MapXD<Integer> m, int j){
		if (m==null) return this;
		for ( Map.Entry<Integer, Double> e : m.entrySet() ) {
			Integer k = e.getKey();
			Double x = e.getValue();
			getC(k).plusOn(j,x);
		}		
		return this;
	}			
	public MapIMapIDa minusOn( MapXD<Integer> m, int j){
		if (m==null) return this;
		for ( Map.Entry<Integer, Double> e : m.entrySet() ) {
			Integer k = e.getKey();
			Double x = e.getValue();
			getC(k).minusOn(j,x);
		}		
		return this;
	}			
}
