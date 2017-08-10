package edu.cmu.lti.algorithm.container;

import java.util.Map;

public class MapVecII  extends MapIX<VectorI> { 
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapVecII newInstance(){
		return new MapVecII();
	}	
	//TVectorMapIX<Double> {
	public VectorI newValue(){		return new VectorI();	}	
	public MapVecII(){
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
	
	
	public static MapVecII  fromFile(String fn){
		MapVecII mv= new MapVecII();
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