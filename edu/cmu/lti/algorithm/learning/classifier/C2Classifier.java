package edu.cmu.lti.algorithm.learning.classifier;

import edu.cmu.lti.algorithm.learning.data.Instance;



/**
 * expect y={+1,-1}, x=[real]+
 * @author nlao
 *
 */
public abstract class C2Classifier {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

//	public void trainBinarized(DataSet dsTrain, VectorI vp, VectorI vn){}
	public abstract double testBinarized(Instance ins);

	/*
	 * 	public VectorD testBinarized(DataSet ds){
		VectorD v = new VectorD();
		v.ensureCapacity(ds.size());
		for (Interfaces.IInstance ins: ds)
			v.add(testBinarized((Instance)ins));		
		return v;
	}
	 */
}
