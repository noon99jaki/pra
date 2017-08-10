package edu.cmu.lti.algorithm.container;

public class MapIMapIVecI extends MapIX<MapVecII> {
	public MapIMapIVecI(){
		super(MapVecII.class);
	}
	public VectorI getC(int i, int j) {
		return getC(i).getC(j);
	}
}

/*
public class MapMapVectorIII extends TMapMapIIX<VectorI> {
public MapMapVectorIII(){
	super(VectorI.class);
}
}*/