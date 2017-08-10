package edu.cmu.lti.algorithm.math;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.IGetDblByStr;
import edu.cmu.lti.algorithm.Interfaces.IMultiplyOn;
import edu.cmu.lti.algorithm.Interfaces.IPlusObj;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.Interfaces.ISetDblByStr;
import edu.cmu.lti.algorithm.container.VectorD;

public class MomentumVec implements Serializable, IGetDblByStr, ISetDblByStr,
		IMultiplyOn, IPlusObjOn, IPlusObj {//, IGetObjByString{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public VectorD variances = new VectorD(); //variance
	public VectorD means = new VectorD(); // mean
	public int num_samples = 0; // number of samples

	public VectorD std_devs = null;

	public void finish() {
		this.meanOn();
		variances.minusOn(means.sqr());
		std_devs = new VectorD();
		for (double var : variances)
			std_devs.add(Momentum.getSD(var, num_samples));
	}

	public void meanOn() {
		multiplyOn(1.0 / num_samples);
	}

	public void addInstance(VectorD vd) {
		num_samples++;
		means.plusOnE(vd);
		variances.plusOnE(vd.sqr());
	}

	public MomentumVec() {
	}

	public MomentumVec(MomentumVec exp) {
		this.copy(exp);
	}

	public MomentumVec clone() {
		MomentumVec s = new MomentumVec();
		s.plusOn(this);
		return s;
	}

	public void copy(MomentumVec x) {
		num_samples = x.num_samples;
		variances = x.variances;
		means = x.means;
	}

	public void clear() {
		num_samples = 0;
		variances.clear();
		means.clear();
	}

	public Double getDouble(String name) {
		//if (name.equals(CTag.g)) return g;//getG();
		System.err.println("unknown variable " + name);
		return null;
	}

	public void setDouble(String name, Double d) {
		//if (name.equals(CTag.we)) we =d;		
		System.err.println("unknown variable " + name);
		return;
	}

	public MomentumVec plusOn(MomentumVec x) {
		means.plusOn(x.means);
		variances.plusOn(x.variances);
		num_samples += x.num_samples;
		return this;
	}

	public MomentumVec plusObjOn(Object x) {
		return plusOn((MomentumVec) x);
	}

	public MomentumVec plusObj(Object x) {
		return (new MomentumVec(this)).plusOn((MomentumVec) x);
	}

	public MomentumVec multiplyOn(Double x) {
		//n*=x;
		variances.multiplyOn(x);
		means.multiplyOn(x);
		return this;
	}


	public static String getTitle() {
		return "n\tMean\tVariance\tSD"; //\te\teY
	}
	/*
	public String print() {
		return String.format("%d\t%.2f\t%.2f\t%.2f"//\t%.2f\t%.2f
				,n,m,V,getSD()	);
	}
	public String toString() {
		return String.format("%d M)%.2f V)%.2f"
				,n,m,V);
	}*/
}
