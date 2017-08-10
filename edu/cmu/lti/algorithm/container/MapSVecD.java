package edu.cmu.lti.algorithm.container;

public class MapSVecD extends MapSX<VectorD> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSVecD newInstance(){
		return new MapSVecD();
	}	
	public VectorD newValue(){		return new VectorD();	}	

	//TVectorMapIX<Double> {
	public MapSVecD(){
		super(VectorD.class);
		//super(Integer.class);
	}	
}