package edu.cmu.lti.algorithm.container;

import java.util.Collection;
import java.util.Map;

import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;


/**
 * @author nlao
 *This class is an extension to TMap&lt;Integer, Integer&gt;
 *
 */
public class MapII extends MapIX<Integer>{
	//extends TMapXI<Integer> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapII newInstance(){
		return new MapII();
	}
	
	public Integer sum(){
		Integer x=0;
		for ( Integer i : this.values() ) 
			x+=i;		
		return x;
	}
	public MapII(){
		super(Integer.class);
		//super(Integer.class, Integer.class);
	}	
	public Integer newValue(){//weakness of Java template
		return 0;
	}

	public MapII plusOn(Integer k, Integer x){
		if (containsKey(k))
			put(k, get(k)+x);		
		else 
			put(k,x);
		return this;
	}	
	public MapII plusOn(Integer k){
		return plusOn(k,1);
	}	
	public MapII plusOn(Collection<Integer> m){
		for ( Integer k : m ) 
			plusOn(k);
		return this;
	}

	//public Integer newValue(){
	//needed for primitive classes, silly java
		//return 0;	}	
	
	//public SetI newSetValue(){
		//return new SetI();	}
	public MapID toDouble(){
		MapID m = new MapID();
		for (Map.Entry<Integer, Integer>e: this.entrySet())
			m.put(e.getKey(),(double) e.getValue());
		return m;
	}
	public Integer parseValue(String k){
		try{
			return Integer.parseInt(k);
		}
		catch(Exception e){
		}
		return null;
	}
	
	public void  idxSmallerThan(Integer x, VectorX<Integer> v){
		v.clear();	//use outside container to avoid creating object
		//v.removeAllElements();
		v.ensureCapacity(size());
		for ( Map.Entry<Integer, Integer> e : entrySet() ) {
			Integer k = e.getKey();
			if (e.getValue()< x)
				v.add(k);
		}
	}
	

	public static MapII fromFile(String fn){
		MapII m=new MapII();
		m.loadFile(fn);
		return m;
	}
	public static MapII fromLine(String line){
		return fromLine(line, "=", " ");
	}
	public static MapII fromLine(String line, String cSep, String c){
		MapII m=new MapII();
		m.loadLine(line, cSep, c);
		return m;
	}
}
