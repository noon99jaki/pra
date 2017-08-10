package edu.cmu.lti.algorithm.learning.classifier;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VecMapID;
import edu.cmu.lti.algorithm.learning.data.DataSet;
import edu.cmu.lti.algorithm.learning.data.Instance;

/**
 * two class linear classifier
 * @author nlao
 *
 */
public class C2Linear extends C2Classifier{
	public VectorD w = new VectorD();
	public double testBinarized(Instance ins){
		return 0.0;//w.inner(ins.m);
	}
	public C2Linear(VectorD vWeight){
		this.w =vWeight;
	}
	/**
	 * transform data X,Y into {x1*y1, x2*y2...}
	 * @param ds
	 * @param vp
	 * @param vn
	 * @return
	 */
	public static VecMapID xyb(DataSet ds, VectorI vp, VectorI vn){
		VecMapID vm = new VecMapID();
		for (int i: vp){
			Instance ins = (Instance) ds.v.get(i);
			//vm.add(ins.m);
		}
		for (int i: vn){
			Instance ins = (Instance) ds.v.get(i);
			//vm.add((MapID) ins.m.multiply(-1.0));
		}				
		return vm;
	}
}
