package edu.cmu.lti.algorithm.container;

public class MapDI extends MapXI<Double>{//TMap<String, Integer> {//
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapDI newInstance(){
		return new MapDI();
	}
	
	public Double newKey(){//needed for primitive classes, silly java
		return null;
	}	
	
	public MapDI(){
		super(Double.class);
	}	
	public VectorD newVectorKey(){
		return new VectorD();
	}
	//public MapVectorIS newMapVectorValueKey(){
	//	return new MapVectorIS();	}
	
	//public MapVectorID newMapVectorValueKey(){
	//	return new MapVectorID();	
	//}

}

