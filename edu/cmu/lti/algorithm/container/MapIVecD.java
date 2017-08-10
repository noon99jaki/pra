package edu.cmu.lti.algorithm.container;

import java.util.Map;

public class MapIVecD  extends MapIX<VectorD> { 
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapIVecD newInstance(){
		return new MapIVecD();
	}	

	public VectorD newValue(){		return new VectorD();	}	
	public MapIVecD(){
		super(VectorD.class);
		//super(Double.class);
	}	
	
	
	public VectorD joinValues(){
		VectorD v= new VectorD();
		for (VectorD vv: this.values())
			v.addAll(vv);
		return v;
	}
	
	
	public static MapIVecD  fromFile(String fn){
		MapIVecD mv= new MapIVecD();
		mv.loadFile(fn);
		return mv;

	}
	public VectorD parseValue(String txt){		
		return VectorD.fromLine(txt, " ");
	}
	/*public boolean loadFile(String fn){//, String sep2){
		super.loadFile(fn);//default sep=\t
		return true;
	}*/
}