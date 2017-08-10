/**
 * 
 */
package edu.cmu.lti.algorithm.container;

/**
 * @author nlao
 *This class is an extension to TMMap&lt;K, Integer&gt;
 */
public class TMapXVecI<K> extends TMapXVecX<K, Integer>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapXVecI(Class ck){
		super(ck, Integer.class);
	}
	public VectorI newVectorV(){
		return new VectorI();
	}
	public VectorI newValue(){
		return new VectorI();
	}
}
