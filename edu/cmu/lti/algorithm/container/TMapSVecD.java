package edu.cmu.lti.algorithm.container;

public class TMapSVecD  extends TMapSX<VectorD> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapSVecD newInstance(){
		return new TMapSVecD();
	}	
	public VectorD newValue(){		return new VectorD();	}	

	//TVectorMapIX<Double> {
	public TMapSVecD(){
		super(VectorD.class);
		//super(Integer.class);
	}	
}