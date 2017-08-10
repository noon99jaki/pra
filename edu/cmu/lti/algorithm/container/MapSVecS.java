package edu.cmu.lti.algorithm.container;

import edu.cmu.lti.util.text.FString;

public class MapSVecS extends MapSX<VectorS>{
	//TMapSX<VectorS> { 
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSVecS newInstance(){
		return new MapSVecS();
	}	
	public VectorS newValue(){		return new VectorS();	}	
	//TVectorMapIX<Double> {
	public MapSVecS(){
		super(VectorS.class);
		//super(VectorS.class);
	}	
	public VectorS parseValue(String v){		
		return FString.splitVS(v, " ");
	}
}
