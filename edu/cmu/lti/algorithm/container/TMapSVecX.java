/**
 * 
 */
package edu.cmu.lti.algorithm.container;

public class TMapSVecX<V> extends TMapXVecX<String, V>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapSVecX(Class cv){
		super(String.class, (new VectorX<V>(cv)).getClass());
	}
	public MapSI newMapKeyI(){
		return  new MapSI();
	}
	public VectorS newVectorKey(){
		return new VectorS();
	}
}
