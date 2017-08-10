package edu.cmu.lti.algorithm.container;

import java.util.Collection;


public class MapDD extends MapXD<Double>{
	
	public MapDD newInstance(){
		return new MapDD();
	}
	public SetD newSetKey(){
		return new SetD();
	}	
	public Double newKey(){//needed for primitive classes, silly java
		return 0.0;
	}	
	public VectorD newVectorKey(){
		return new VectorD();
	}
	public VectorD toVectorKey(){
		return (VectorD) super.toVectorKey();
	}
	
	public MapDD(){
		super(Double.class);
	}	
	public MapDD(Collection<Double> vd, double value){
		super(Double.class, vd, value);
	}
	
	public MapDD normalize() {
		return (MapDD) super.normalize();
	}
	public MapDD normalizeOn() {
		return (MapDD) super.normalizeOn();
	}
	
}
