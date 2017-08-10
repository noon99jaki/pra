package edu.cmu.lti.algorithm.container;

import java.util.Collection;
import java.util.Iterator;

import edu.cmu.lti.util.file.FFile;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.Feature;

public class TSetS extends TSetX<String>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TSetS newInstance(){
		return new TSetS();
	}	
	public VectorS newVector(){
		return new VectorS();
	}
	
	public String newKey(){//needed for primitive classes, silly java
		return null;
	}
	public TSetS(){
		super(String.class); 
	}
	public TSetS(Collection<String> values){
		super(values, String.class); 
	}
	public TSetS(Iterable<String> values){
		super(values, String.class); 
	}
	
	public VectorS toVector(){
		return (VectorS) (new VectorS()).load(this);
	}		
	

	public TSetS(String[] v){
		super(v);
	}
	public TSetS(String s){
		super(s.split(" "));
	}
	public TSetS addAll (String s){
		addAll(s.split(" "));
		return this;
	}
	public static TSetS newFrom(Example e){
		TSetS m = new TSetS();
    for(Iterator it = e.binaryFeatureIterator(); it.hasNext();) {
      Feature feat = (Feature)it.next();
      m.add( (new VectorS(feat.getName())).join("."));
    }		
		return m;
	}
	public void load(String fn){
		this.addEnum(FFile.enuLines(fn));
	}
}

