package edu.cmu.lti.algorithm.container;

/**
 * @author nlao
 *
 */
public class MapSMapSX<V> extends MapSX<MapSX<V>> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Class cv1;
	public MapSMapSX(Class c){
		super( (new MapSX<V>(c)).getClass());
		this.cv1 = c;
	}
	public MapSX<V>  newValue(){//weakness of Java template
		return new MapSX<V>(cv1);
	}
	public boolean contains(String k1, String k2){
		if (!containsKey(k1)) return false;
		if (!get(k1).containsKey(k2)) return false;
		return true;
	}
	
}

