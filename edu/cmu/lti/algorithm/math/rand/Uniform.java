/**
 * 
 */
package edu.cmu.lti.algorithm.math.rand;

import edu.cmu.lti.algorithm.math.rand.Interfaces.IDrawDouble;

/**
 * @author nlao
 *
 **/
public class Uniform implements IDrawDouble{
	public double draw(){
		return a + b* FRand.rand.nextDouble();
	}
	private double a, b;	
	public Uniform(double a, double b){
		this.a = a;
		this.b = b-a;
	}
}
