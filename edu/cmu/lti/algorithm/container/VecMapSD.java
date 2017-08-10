package edu.cmu.lti.algorithm.container;

public class VecMapSD  extends VectorX<MapSD> { 
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public VecMapSD newInstance(){
		return new VecMapSD();
	}	
	//TVectorMapIX<Double> {
	public VecMapSD(){
		super(MapSD.class);
		//super(Double.class);
	}	

}