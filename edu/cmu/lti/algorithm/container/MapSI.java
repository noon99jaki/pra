/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import edu.cmu.lti.util.file.FFile;


/**
 * @author nlao
 *This class is an extension to TMap&lt;String, Integer&gt;
 */
public class MapSI extends MapXI<String>{//TMap<String, Integer> {//
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSI newInstance(){
		return new MapSI();
	}
	
	public String newKey(){//needed for primitive classes, silly java
		return null;
	}	
	
	public MapSI(){
		super(String.class);
		//super(String.class, Integer.class);
	}	
	public static MapSI fromFile(String fn){
		MapSI m= new MapSI();
		m.loadFile(fn);
		return m;
	}
	public VectorS newVectorKey(){
		return new VectorS();
	}
	//public MapVectorIS newMapVectorValueKey(){
	//	return new MapVectorIS();	}
	
	public TMapIVecSa newMVValueKey(){
		return new TMapIVecSa();	
	}
/*	public Integer sum() {
		Integer sum = 0;
		for ( Map.Entry<String,Integer> e : entrySet() ) {
			//Integer k = e.getKey();
			Integer v = e.getValue();
			sum = sum+v;
		}
		return sum;
	}*/
	public String parseKey(String k){		
		return k;
	}
	
	
	//TODO: rewrite it in enumeration
	public boolean fromFile(String fn, int icKey, int icValue, boolean b2lowerKey){
		for (String line: FFile.enuLines(fn)){
			String vs[] = line.split("\t");
			String key=vs[icKey];
			if (b2lowerKey)
				key = key.toLowerCase();
			put(key,parseValue(vs[icValue]));
		} 
		return true;
	}
	
	public VectorI subV(VectorS vs){
		return (VectorI) super.subVector(vs);
	}
	public VectorI subVIgNull(VectorS vs){
		return (VectorI) super.subV(vs,true);
	}
}

