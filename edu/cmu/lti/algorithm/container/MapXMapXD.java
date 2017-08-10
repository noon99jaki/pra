/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.Map;

import edu.cmu.lti.util.text.FString;

/**
 * @author nlao
 *
 */
public class MapXMapXD<K1,K2> extends MapXX<K1, MapXD<K2>> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Class ck2;
	public MapXMapXD(Class ck1, Class ck2){
		super(ck1, (new MapXD<K2>(ck2)).getClass());
		this.ck2 = ck2;
	}
	public MapXD<K2> newValue(){//weakness of Java template
		return new MapXD<K2>(ck2);
	}

	public Double get(K1 i, K2 j){
		return getC(i).get(j);
	}
	public Double set(K1 i, K2 j, Double x){
		return getC(i).put(j, x);
	}	
	public Double getC(K1 i, K2 j){
		return get(i).getC(j);
	}	
	public void plusOn(K1 i, K2 j, Double x){
		getC(i).plusOn(j,x);
	}		
	public void minusOn(K1 i, K2 j, Double x){
		getC(i).minusOn(j,x);
	}		
	public void plusOn(K1 i, K2 j){
		getC(i).plusOn(j,1.0);
	}		
	public void minusOn(K1 i, K2 j){
		getC(i).minusOn(j,1.0);
	}		
	public void plusOn(VectorX<K1> vk1, K2 k2){
		for (K1 k1: vk1)
			plusOn(k1,k2);
	}	
	public void minusOn(VectorX<K1> vk1, K2 k2){
		for (K1 k1: vk1)
			minusOn(k1,k2);
	}	
	public MapXMapXD<K1,K2> plusOn(K1 i, MapXD<K2> m){
		if (m==null || i==null) return this;
		getC(i).plusOn(m);
		return this;
	}	
	public MapXMapXD<K1,K2> minusOn(K1 i, MapXD<K2> m){
		if (m==null || i==null) return this;
		getC(i).minusOn(m);
		return this;
	}		
	public MapXMapXD<K1,K2> plusOn( MapXD<K1> m, K2 j){
		if (m==null || j==null) return this;
		for ( Map.Entry<K1, Double> e : m.entrySet() ) {
			K1 k = e.getKey();
			Double x = e.getValue();
			plusOn(k,j,x);
		}		
		return this;
	}
	public MapXMapXD<K1,K2> minusOn( MapXD<K1> m, K2 j){
		if (m==null || j==null) return this;
		for ( Map.Entry<K1, Double> e : m.entrySet() ) {
			K1 k = e.getKey();
			Double x = e.getValue();
			minusOn(k,j,x);
		}		
		return this;
	}
	public MapXMapXD<K1,K2>  multiplyOn(double x){
		for ( MapXD<K2> m : this.values() ) 
			m.multiplyOn(x);
		return this;
	}	
	
	public String toString3Col(){
		return toString3Col("\t","\n");
	}
	public String toString3Col(String cpair, String c){
		StringBuffer sb = new StringBuffer();
		int first=1;
		for ( Map.Entry<K1, MapXD<K2>> e : entrySet() ) {
			K1 k1 = e.getKey();
			MapXD<K2> m = e.getValue();
			for(Map.Entry<K2, Double> e1 : m.entrySet()){
				K2 k2 = e1.getKey();
				Double x= e1.getValue();
				if (first==1)	first=0;			
				else	sb.append(c);				
				sb.append(FString.format(k1)
						+cpair+FString.format(k2)
						+cpair+FString.format(x));						
			}	
		}
		return sb.toString();
	}	
}


/*
public TMapXD<K1> shrink(){
	TMapXD<K1> m = new TMapXD<K1>(ck);
	for ( Map.Entry<K1, TMapXD<K2>> e : entrySet() ) {
		K1 k = e.getKey();
		TMapXD<K2> x = e.getValue();
		m.put(k,x.sum());
	}		
	return m;
}
public TMapMapXXD<K1,K2> addOn(TMapMapXXD<K1,K2> mm){
	for ( Map.Entry<K1, TMapXD<K2>> e : mm.entrySet() ) {
		K1 k = e.getKey();
		TMapXD<K2> x = e.getValue();
		getC(k).addOn(x);
	}			
	return this;
}
public TMapMapXXD<K1,K2> plusOn(TMapMapXXD<K1,K2> mm){
	for ( Map.Entry<K1, TMapXD<K2>> e : mm.entrySet() ) {
		K1 k = e.getKey();
		TMapXD<K2> x = e.getValue();
		getC(k).plusOn(x);
	}			
	return this;
}	
public TMapMapXXD<K1,K2> minusOn(TMapMapXXD<K1,K2> mm){
	for ( Map.Entry<K1, TMapXD<K2>> e : mm.entrySet() ) {
		K1 k = e.getKey();
		TMapXD<K2> x = e.getValue();
		getC(k).minusOn(x);
	}			
	return this;
}	*/