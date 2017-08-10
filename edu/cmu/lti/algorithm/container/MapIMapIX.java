package edu.cmu.lti.algorithm.container;

import java.util.Map;

import edu.cmu.lti.algorithm.Interfaces.IUpdateIdx;

public class MapIMapIX<V>  extends MapIX<MapIX<V> > 
	implements IUpdateIdx{//,IGetDoubleByString{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Class cv1;
	public MapIMapIX<V> newInstance(){
		return new MapIMapIX<V>(cv1);
	}
	public MapIMapIX(Class c){
		super((new MapIX<V>(c)).getClass());
		this.cv1 = c;
	}
	public MapIX<V>  newValue(){//weakness of Java template
		return new MapIX<V>(cv1);
	}	
	public MapIMapIX<V> transpose(){
		MapIMapIX<V> mm=newInstance();
		for ( Map.Entry<Integer, MapIX<V>> e1 : entrySet() ) {
			Integer k1 = e1.getKey();
			MapIX<V>  m = e1.getValue();
			//if (k1==-1) continue;
			
			for ( Map.Entry<Integer, V> e2 : m.entrySet() ) {
				Integer k2 = e2.getKey();
				V x = e2.getValue();
				//if (k2==-1)	continue;
				//mm.put(k2,k1, x);
				mm.getC(k2).put(k1, x);
			}
		}
		return mm;
	}
	public boolean contains(Integer k1, Integer k2){
		if (!containsKey(k1)) return false;
		if (!get(k1).containsKey(k2)) return false;
		return true;
	}	
	public MapIMapIX<V> put(Integer k1, Integer k2, V x){
		getC(k1).put(k2,x);		
		return this;
	}
	public MapIMapIDa getMMDouble(String name) {
		MapIMapIDa mm = new MapIMapIDa();
		for (int id: this.keySet())
			mm.put(id, this.getMDouble(name));
		return mm;
	}
	public VectorX<V> getV() {
		VectorX<V>  v = new VectorX<V> (this.cv1);
		for (int id: this.keySet())
			v.addAll(this.get(id).values());
		return v;
	}
	public V get(int i, int j) {
		MapIX<V> m = get(i);
		if (m==null)
			return null;
		return m.get(j);
	}
	public boolean containsKey(int i,int j){
		return get(i,j)!=null;		
	}
	public V getC(int i, int j) {
		return getC(i).getC(j);
	}

	public void updateIdx(VectorI vi){//<Integer> vi){	
		super.updateIdx(vi);
		for (MapIX<V> m: this.values()){
			m.updateIdx(vi);
		}
	}

}