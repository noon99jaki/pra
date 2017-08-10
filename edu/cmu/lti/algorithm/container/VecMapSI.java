package edu.cmu.lti.algorithm.container;

public class VecMapSI extends VectorX<MapSI> { 
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public VecMapSI newInstance(){
		return new VecMapSI();
	}	
	//TVectorMapIX<Double> {
	public VecMapSI(){
		super(MapSI.class);
		//super(Double.class);
	}	

}
