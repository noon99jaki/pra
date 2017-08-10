/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.Map;

import edu.cmu.lti.algorithm.Interfaces.IUpdateIdx;

/**
 * @author nlao
 *
 */
public class MapIX <V>  extends MapXX<Integer, V> implements  IUpdateIdx{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapIX<V> newInstance(){
		return new MapIX<V>(cv);
	}	
	public SetI newSetKey(){
		return new SetI();
	}
	public MapIX(Class c){
		super(Integer.class,c);
	}

	//need to replace both index
	public MapIX<V> replaceIdx(VectorI vi){//<Integer> vi){	
		MapIX<V> m = newInstance();
		for ( Map.Entry<Integer, V> e2 : entrySet() ) {
			Integer k2 = e2.getKey();
			V x = e2.getValue();
			if (vi.get(k2)==-1)	continue;			
			//((IUpdateIdx)x).updateIdx(vi); //should recur??			
			m.put(vi.get(k2), x);
		}
		return m;
	}	
	public MapSX <V> replaceKey(VectorS vs){
		MapSX <V> m = new MapSX <V>(this.cv);
		for (Map.Entry<Integer, V> e: this.entrySet())
			m.put(vs.get(e.getKey()), e.getValue());
		return m;
	}
	public void updateIdx(VectorI vi){//<Integer> vi){	
		copy(replaceIdx(vi));
	}
	public MapID newMapXD(){
		return  new MapID();
	}
	public MapII newMapKeyI(){
		return  new MapII();
	}
	public Integer parseKey(String k){		
		return Integer.parseInt(k);
	}

	//public MapII newMapXI(){		return  new MapII();	}
	

}