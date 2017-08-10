package edu.cmu.lti.algorithm.container;

import java.util.Map;

import edu.cmu.lti.algorithm.Interfaces.IUpdateIdx;

public class MapXI<K> extends MapXX<K, Integer>  implements  IUpdateIdx{	
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	

	public Integer sum(){
		Integer x=0;
		for ( Integer i : this.values() ) 
			x+=i;		
		return x;
	}
	
	public TMapIVecX<K> newMVValueKey(){
		return new TMapIVecX<K>(ck);	
	}
	
	public MapXI<K> replaceIdx(VectorI vi){//<Integer> vi){	
		MapXI<K> m = newInstance();
		for ( Map.Entry<K,Integer> e : entrySet() ) {
			K k = e.getKey();
			Integer v = e.getValue();
			if (vi.get(v)==-1)	continue;
			
			m.put(k,vi.get(v));
		}
		return m;	
	}
	public void updateIdx(VectorI vi){//<Integer> vi){	
		copy(replaceIdx(vi));
	}
	
	public MapXI<K> newInstance(){
		return new MapXI<K>(ck);
	}
	public Integer newValue(){//weakness of Java template
		return 0;
	}
	public VectorI newVectorValue(){
		return new VectorI();
	}	

	public Integer parseValue(String v){		
		return Integer.parseInt(v);
	}
	public SetX<K>  idxSetSmallerThan(int x){
		SetX<K> m = this.newSetKey();
		for ( Map.Entry<K, Integer> e : entrySet() ) {
			K k = e.getKey();
			if (e.getValue()< x)
				m.add(k);
		}
		return m;
	}

	public MapXI(Class ck){
		super(ck, Integer.class);
	}
	public MapXI(MapXX<K, Integer> m){
		super(m);
	}
	public SetI newSetValue(){
		return new SetI();
	}	
	public MapXI<K>  plusOn(MapXI<K> m){
		if (m==null) return this;
		for ( Map.Entry<K, Integer> e : m.entrySet() ) {
			K k = e.getKey();
			Integer v = e.getValue();
			plusOn(k,v);
		}	
		return this;
	}	
	public MapXI<K> plusOn(final K k, Integer x){		
		Integer i = this.get(k);
		if (i != null) 
			put(k, i+x);		
		else 
			put(k,x);
		return this;
	}	
	public MapXI<K> plusOn(final K k){
		return plusOn(k, 1);		
	}		
}
