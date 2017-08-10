package edu.cmu.lti.algorithm.container;

public class MapIVecS  extends MapIX<VectorS>{ 
	//	extends TMapIX<VectorS> { 

	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapIVecS newInstance(){
		return new MapIVecS();
	}	
	public VectorS newValue(){		return new VectorS();	}	
	//TVectorMapIX<Double> {
	public MapIVecS(){
		super(VectorS.class);
		//super(Double.class);
	}	


	public String parseValueValue(String s){
		return s;
	}
}