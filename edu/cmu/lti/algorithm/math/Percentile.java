package edu.cmu.lti.algorithm.math;

/**
 * estimate x% percentile point using perceptron update
 */
public class Percentile{
	public Percentile(double pct){
		this.pct= pct;
	}
	public double ns=0.0;//#samples observed so fa
	public double pct; //expected percentile point 0<pct<1.0
	public double est=1.0;	//the estimation
	public void addSample(double x){
		addSample(x,1.0);
	}
	public double addSample(double x, double weight){
		ns += weight;
		double step =weight/ns;
		if (x> est)
			est += pct*step;
		if (x< est)
			est -= (1-pct)*step;
		return est;
	}
	public double estimate(){
		return est;
	}
	public String toString(){
		return String.format("%.1f/%.1f",est, ns);
	}
}