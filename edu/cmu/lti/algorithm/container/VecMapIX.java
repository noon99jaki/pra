/**
 * 
 */
package edu.cmu.lti.algorithm.container;


/**
 * @author nlao
 *
 */
public class VecMapIX<V> extends VectorX<MapIX<V>> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public VecMapIX<V> newInstance(){
		return new VecMapIX<V>(c1_);
	}	
	public Class c1_=Object.class;
	public VecMapIX(Class c1){
		super((new MapIX<V>(c1)).getClass());
		this.c1_=c1;
	}
	public MapIX<V> newValue() {
		return new MapIX<V>(c1_);
	}

	public V get(int i, int j){
		return getE(i).get(j);
	}

	public V getC(int i, int j){
		return get(i).getC(j);
	}	

}
