/**
 * 
 */
package edu.cmu.lti.algorithm.container;

/**
 * @author nlao
 *This class is an extension to TMMap&lt;K, Double&gt;
 */
public class TMapXVecD<K> extends TMapXVecX<K, Double>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapXVecD(Class ck){
		super(ck, Double.class);
	}
}
