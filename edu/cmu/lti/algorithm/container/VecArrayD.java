package edu.cmu.lti.algorithm.container;

public class VecArrayD extends VectorX<double[]> {
	public VecArrayD(){
		super( (new double[0]).getClass());
	}
	public VectorD getRow(int i){
		VectorD vd= new VectorD();
		vd.ensureCapacity(size());
		for (int j=0; j<size();++j){
			double[] v= this.get(j);
			if (v.length<=i)
				vd.add(null);
			else
				vd.add(v[i]);
		}			
		return vd;
	}
}
