package edu.cmu.lti.algorithm.container;

import java.util.Map;

public class TMapSX<V> extends TMapXX<String, V>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapSX<V> newInstance(){
		return new TMapSX<V>(cv);
	}
	public String newKey(){//weakness of Java template
		return null;
	}
	public VectorS newVectorKey(){
		return new VectorS();
	}	
	public TMapSX(Class c){
		super(String.class,c);
	}
	public TMapSX<V>  addKeyPrefix(String prefix){
		TMapSX<V> m = newInstance();
		for ( Map.Entry<String, V> e : entrySet() ) {
			String k = e.getKey();
			V v = e.getValue();
			m.put(prefix + k, v);
		}	
		return m;
	}	

	public SetS newSetKey(){
		return new SetS();
	}	
	public String parseKey(String v){		
		return v;
	}

}