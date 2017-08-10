package edu.cmu.lti.algorithm.container;

import java.util.Map;

public class TMapIVecI  extends TMapIX<VectorI> { 
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapIVecI newInstance(){
		return new TMapIVecI();
	}	
	//TVectorMapIX<Double> {
	public VectorI newValue(){		return new VectorI();	}	
	public TMapIVecI(){
		super(VectorI.class);
		//super(Double.class);
	}	
	
	
	public VectorI joinValues(){
		VectorI v= new VectorI();
		for (VectorI vv: this.values())
			v.addAll(vv);
		return v;
	}
	
	public void joinValuesWhereKeySmallerThan(int i, VectorI v){
		v.clear();
		for (Map.Entry<Integer, VectorI> e: entrySet())
			if (e.getKey()<i) 
				v.addAll(e.getValue());
	}
	
	
	public static TMapIVecI  fromFile(String fn){
		TMapIVecI mv= new TMapIVecI();
		mv.loadFile(fn);
		return mv;

	}
	public VectorI parseValue(String txt){		
		return VectorI.parse(txt, " ");
	}
	/*public boolean loadFile(String fn){//, String sep2){
		super.loadFile(fn);//default sep=\t
		return true;
	}*/
}