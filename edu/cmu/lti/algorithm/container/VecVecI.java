/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import edu.cmu.lti.util.system.FSystem;

/**
 * @author nlao
 *
 */
public class VecVecI extends VectorX<VectorI> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public VecVecI newInstance() {
		return new VecVecI();
	}

	//TVectorMapIX<Double> {
	public VecVecI() {
		super(VectorI.class);
		//super(Double.class);
	}

	public VecVecI(int n) {
		super(n, VectorI.class);
	}

	public VectorI parseLine(String line) {
		return VectorI.parse(line, "\t");
	}

	public VecVecI sub(VectorX<Integer> vi) {
		return (VecVecI) super.sub(vi);
	}

	/**
	 * treat the data as 
	 * 	features-->list of instances with feature=T 
	 * @param vWeights of the features
	 * @return
	 */
	public MapID weightedSum(VectorD vWeights) {
		FSystem.checkVectorSizes(size(), vWeights.size());


		// use silliest implementation
		MapID v = new MapID();
		for (int i = 0; i < vWeights.size(); ++i)
			v.plusOn(get(i), vWeights.get(i));
		return v;
	}

	public MapID weightedSum(VectorI vi, VectorD vWeights) {
		FSystem.checkVectorSizes(vi.size(), vWeights.size());

		// use silliest implementation
		MapID v = new MapID();
		for (int i = 0; i < size(); ++i)
			v.plusOn(get(vi.get(i)), vWeights.get(i));
		return v;
	}
}
