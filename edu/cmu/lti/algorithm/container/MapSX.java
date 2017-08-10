/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.Map;

/**
 * @author nlao
 *
 */
public class MapSX<V>  extends MapXX<String, V>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSX<V> newInstance(){
		return new MapSX<V>(cv);
	}
	public String newKey(){//weakness of Java template
		return null;
	}
	public VectorS newVectorKey(){
		return new VectorS();
	}	
	public MapSX(Class c){
		super(String.class,c);
	}
	public MapSX<V>  addKeyPrefix(String prefix){
		MapSX<V> m = newInstance();
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
	public MapSD newMapXD(){
		return  new MapSD();
	}
	public MapSI newMapKeyI(){		
		return  new MapSI();	
	}
	public String parseKey(String v){		
		return v;
	}

}