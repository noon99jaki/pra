package edu.cmu.lti.algorithm.math.rand;

import edu.cmu.lti.algorithm.container.MapDD;
import edu.cmu.lti.algorithm.container.VectorD;

public class MultinomD  extends MultinomN{// implements IDrawRandV{
	public Double drawD(){
		return vk.get(draw());
	}
	
	public VectorD vk;	
	
	public MultinomD(MapDD mDist){
		this( mDist.toVectorKey(),  mDist.ValuesToVector());	
	}
	
	public MultinomD(VectorD vk	, VectorD vd	){
		super(vd);
		this.vk= vk;
	}	
	
	public Double percentileD(double p){
		return vk.get(super.percentile(p));
	}
}
