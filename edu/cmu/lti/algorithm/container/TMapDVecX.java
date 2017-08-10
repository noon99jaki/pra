/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.Map;

/**
 * @author nlao
 *
 */
public class TMapDVecX<V> extends TMapXVecX<Double, V>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapDVecX(Class cv){
		super(Double.class, cv);
	}
	public MapDI newMapKeyI(){
		return  new MapDI();
	}
	public VectorD newVectorKey(){
		return new VectorD();
	}
	
	public TMapDVecX<V> negateKey(){
		TMapDVecX<V> m= new TMapDVecX<V>(cv);
		for (Map.Entry<Double, VectorX<V>>e: this.entrySet())
			m.put(-e.getKey(), e.getValue());
		return m;
	}
	public VectorD toVectorK(){
		return (VectorD) super.toVectorK();
	}

}
