package edu.cmu.lti.algorithm.container;

public class VecSetI extends VectorX<SetI>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public VecSetI newInstance(){
		return new VecSetI();
	}	
	//TVectorMapIX<Double> {
	public VecSetI(){
		super(SetI.class);
		//super(Double.class);
	}	
	public VecSetI(int n){
		super(n,SetI.class);
	}
	public SetI parseLine(String line){
		return SetI.fromString(line,"\t");
	}
	public VecSetI sub(VectorX<Integer> vi){
		return (VecSetI) super.sub(vi);
	}
	
}

