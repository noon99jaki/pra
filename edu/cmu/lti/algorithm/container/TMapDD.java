package edu.cmu.lti.algorithm.container;

import java.util.Collection;

public class TMapDD  extends TMapXD<Double>{
	
	public TMapDD newInstance(){
		return new TMapDD();
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
	
	public TMapDD(){
		super(Double.class);
	}	
	public TMapDD(Collection<Double> vd, double value){
		super(Double.class, vd, value);
	}
	
	public TMapDD normalize() {
		return (TMapDD) super.normalize();
	}
	public TMapDD normalizeOn() {
		return (TMapDD) super.normalizeOn();
	}
	
}
