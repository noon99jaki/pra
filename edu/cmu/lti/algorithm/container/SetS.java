/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.Iterator;

import edu.cmu.lti.util.file.FFile;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.Feature;

/**
 * @author nlao
 *This class is an extension to TSet&lt;String&gt;
 */
public class SetS extends SetX<String>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public SetS newInstance(){
		return new SetS();
	}	
	public VectorS newVector(){
		return new VectorS();
	}
	
	public String newKey(){//needed for primitive classes, silly java
		return null;
	}
	public SetS(){
		super(String.class); 
	}
	public VectorS toVector(){
		return (VectorS) (new VectorS()).load(this);
	}		
	
	public SetS(Iterable<String> v){
		super(v, String.class);
	}
	public SetS(String[] v){
		super(v);
	}
	public SetS(String s){
		super(s.split(" "));
	}
	public SetS addAll (String s){
		addAll(s.split(" "));
		return this;
	}
/*	public SetS from (String[] v){
		return (SetS) super.from(v);
		//return null;
	}	*/
	
//	public MutableInstance toM3rdInstance(){
//		MutableInstance instance = new MutableInstance();
//		for ( String f : this ) 
//			instance.addBinary( new Feature( f ) );
//		return instance;
//	}
	public static SetS fromM3dExample(Example e){
		SetS m = new SetS();
    for(Iterator it = e.binaryFeatureIterator(); it.hasNext();) {
      Feature feat = (Feature)it.next();
      m.add( (new VectorS(feat.getName())).join("."));
    }		
		return m;
	}
	public String match(String txt){
		for (String s: this)
			if (txt.indexOf(s)>=0)
				return s;
		return null;
	}
	public boolean matchAny(String txt){
		return match(txt) !=null;
	}
	public String parseLine(String k){		
		return k;
	}
	
	public static SetS fromFile(String fn){
		return new SetS(FFile.enuLines(fn));
	}

	public static SetS fromFile(String fn, int iCol){
		return new SetS(FFile.enuACol(fn, iCol));
	}
	
	public static VectorS fromLine(String x, String sep) {
		return new VectorS(x.split(sep));
	}	
	

}
