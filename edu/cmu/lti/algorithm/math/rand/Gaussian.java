/**
 * 
 */
package edu.cmu.lti.algorithm.math.rand;

import edu.cmu.lti.algorithm.math.rand.Interfaces.IDrawDouble;

/**
 * @author nlao
 *
 */
public class Gaussian implements IDrawDouble{
	public double draw(){
		return mean + var*FRand.rand.nextGaussian();
	}
	
	private double mean, var;	
	public Gaussian(double mean, double var){
		this.mean = mean;
		this.var = var;
	}

}
