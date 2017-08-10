package edu.cmu.lti.algorithm.math.rand;

import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.math.rand.Interfaces.IDrawVecD;
import edu.cmu.lti.algorithm.math.rand.Interfaces.IDrawDouble;

public class MDGaussian implements IDrawVecD{
	public VectorD draw(){
		return null;//mean + var*FRand.rand.nextGaussian();
	}
	
	private double mean, var;	
	public MDGaussian(double mean, double var){
		this.mean = mean;
		this.var = var;
	}

}

