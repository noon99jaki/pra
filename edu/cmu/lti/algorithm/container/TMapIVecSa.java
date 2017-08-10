package edu.cmu.lti.algorithm.container;

public class TMapIVecSa   extends TMapIVecX<String>{ 

	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapIVecSa newInstance(){
		return new TMapIVecSa();
	}	
	public VectorS newValue(){		return new VectorS();	}	
	//TVectorMapIX<Double> {
	public TMapIVecSa(){
		super(VectorS.class);
		//super(Double.class);
	}	
	public VectorS toVectorV(){
		return (VectorS) super.toVectorV();
	}
	public static TMapIVecSa newColumn(String fn, int icKey, int icValue){
		TMapIVecSa mv= new TMapIVecSa();
		mv.loadColumn(fn, icKey, icValue);
		return mv;
	}

	public String parseValueValue(String s){
		return s;
	}
}