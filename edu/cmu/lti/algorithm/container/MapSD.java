package edu.cmu.lti.algorithm.container;

import java.util.Set;

import edu.cmu.lti.algorithm.Interfaces.IGetDblByStr;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.minorthird.classify.ClassLabel;


/**
 * @author nlao
 *This class is an extension to TMapXD&lt;String&gt;
 */
public class MapSD extends MapXD<String> implements IGetDblByStr {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSD newInstance(){
		return new MapSD();
	}
	
	public SetS newSetKey(){
		return new SetS();
	}	
	public String newKey(){//needed for primitive classes, silly java
		return null;
	}	
	public VectorS newVectorKey(){
		return new VectorS();
	}
	public MapSD removeZeros(){
		return (MapSD)super.removeZeros();
	}
	
	public MapSD(){
//		super(Integer.class, Double.class);
		super(String.class);
	}	
	public MapSD(MapXX<String, Double> m){
		super(m);
	}
	public MapSD getMDouble(String name){
		return (MapSD) super.getMDouble(name);		
	}
	public MapSD getMDouble(int id){
		return (MapSD) super.getMDouble(id);		
	}
	public Double getDouble(String name){
		Double d = super.getDouble(name);
		if (d!=null) return d;
		return get(name);
	}
	public static MapSD fromM3rdLabel(ClassLabel label){
		MapSD m = new MapSD();
		for (String key: (Set<String>) label.possibleLabels())
			m.put(key, label.getWeight(key));
		return m;
	}
	
	/*public static MapSD parse(String line){
		MapSD m = new MapSD();
		m.loadLine(line, ",", "=");
		return m;//Double.parseDouble(v[1]));
	}*/
	public String parseKey(String v){		
		return v;
	}

	public static MapSD fromFile(String fn){
		MapSD m=new MapSD();
		m.loadFile(fn);
		return m;
	}
	public static MapSD fromFile(String fn, int iC1, int iC2
			, boolean bSkipTitle){//, boolean bSkipZeros){
		MapSD m=new MapSD();
		m.loadFile(fn, iC1, iC2,"\t",bSkipTitle);
	//	if (bSkipZeros);
			
		return m;
	}
	
	public static MapSD fromLine(String line){
		return fromLine(line, "=", " ");
	}
	public static MapSD fromLine(String line, String cSep, String c){
		MapSD m=new MapSD();
		m.loadLine(line, cSep, c);
		return m;
	}
}
