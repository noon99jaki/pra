package edu.cmu.lti.algorithm.container;

public class MapSVecI extends MapSX<VectorI> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSVecI newInstance(){
		return new MapSVecI();
	}	
	public VectorI newValue(){		return new VectorI();	}	

	//TVectorMapIX<Double> {
	public MapSVecI(){
		super(VectorI.class);
		//super(Integer.class);
	}	
}