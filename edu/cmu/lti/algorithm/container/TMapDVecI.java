/**
 * 
 */
package edu.cmu.lti.algorithm.container;


public class TMapDVecI	extends TMapDVecX<Integer> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapDVecI(){
		super(Integer.class);
	}
	public VectorI newValue(){		
		return new VectorI();	
	}	
	//public TMapVectorXI<Double> newTMapVectorXI(){
		//return  new MapVectorDI();}
	public VectorI toVectorV(){
		return (VectorI) super.toVectorV();
	}

}	

