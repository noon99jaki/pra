package edu.cmu.lti.algorithm.math.rand;

import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.math.rand.Interfaces.IDrawInt;
import edu.cmu.lti.util.text.FString;

/**
 * a distribution over natrual numbers 0...|vd|
 * @author nlao
 *
 */
public class MultinomN implements IDrawInt{
	public int draw(){
		return vd.findSorted(FRand.rand.nextDouble()) ;
	}
	
	public VectorD vd;	
	public String toString(){
		return vd.join(" ");
	}
	
	public double getProb(int i){
		if (i==0) return vd.get(i);
		return vd.get(i)-vd.get(i-1);
	}
	//warning: vd will be distoried
	public MultinomN(VectorD vd){ //this.vd = new VectorD(vd);
		this.vd = vd.normalizeOn().cumulateOn();
	}

	public int percentile(double p){
		return vd.findSorted(p);
	}
	
	public static MultinomN fromLine(String line){
		return new MultinomN(FString.splitVS(line," ").toVD());		
	}
}
