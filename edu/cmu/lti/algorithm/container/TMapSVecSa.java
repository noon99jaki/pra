package edu.cmu.lti.algorithm.container;

import edu.cmu.lti.util.file.FFile;


public class TMapSVecSa  extends TMapSVecX<String>{

	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapSVecSa newInstance(){
		return new TMapSVecSa();
	}	
	public VectorS newValue(){		return new VectorS();	}	

	public TMapSVecSa(){
		super(String.class);
	}
	
	public static TMapSVecSa from(
			VecVecS vvs, int iKey,int iValue){
		//this.clear();
		TMapSVecSa mvs = new TMapSVecSa();
		for (VectorS vs: vvs) 
			mvs.getC(vs.get(iKey)).add(vs.get(iValue));		
		return mvs;
	}	
	
	public void parseLine(String line, String c) {
		String v[] = line.split(c);
		VectorS vs= new VectorS();
		vs.ensureCapacity(v.length-1);
		for (int i=1; i<v.length; ++i)
			vs.add(v[i]);
		this.put(v[0],vs);
	}
	public boolean load(String fn, String c){//"\t"
		for (String line: FFile.enuLines(fn))
			if (!line.startsWith("#"))
				parseLine(line,c);	 
		return true;
	}
}