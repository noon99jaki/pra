/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.Map;

import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.text.FString;

/**
 * @author nlao
 *This class represents multi-map data structure, 
 *where each key can have more than one value
 */
public class TMapXVecX<K, V>  extends TMapXX<K, VectorX<V> >
	implements	Serializable{// IWrite, IRead , 
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	
	public TMapXVecX(Class ck, Class cv){
		//this.ck = ck; 
		//this.cv = cv;
		super(ck,cv);
	}
	public V get(K k, int i){
		return get(k).get(i);
	}
	public TMapXVecX<K,V> fromMapValueKey(MapXX<V,K>v){
		this.clear();
		for ( Map.Entry<V,K> e : v.entrySet() ) {
			V x = e.getKey();
			K k = e.getValue();
			insert(k,x);
		}
		return this;
	}	
//	public TMapVec<K,V> load(ArrayList<K> vk, ArrayList<V>  vx){
//		if (vk.size()!= vx.size())
//			FSystem.dieUnmatchedVectorSizes();		
//		clear();
//		for (int i=0; i<vk.size();++i)  get(vk.get(i)).add(vx.get(i));
//		return this;
//	}	
	public TMapXVecX(MapXX<K, V> m){
		super(m.ck,  (new VectorX<V>(m.cv)).getClass());
		//this.ck = m.ck; 
		//this.cv = m.cv;
		for ( Map.Entry<K, V> e : m.entrySet() ) {
			K k = e.getKey();
			V x = e.getValue();
			insert(k,x);
		}	
	}
	public void insert(K k, V x){
		VectorX<V> v=null;
		if (! containsKey(k)){
			v =newValue();//newVectorV();//new TVector<V>(cv);
			super.put(k, v);
		}
		else 
			v=this.get(k);
		v.add(x);
	}

	public VectorX<V> newValue(){
		return new VectorX<V>(cv);	
	}
	
	public VectorX<V> toVectorV(){
		VectorX<V> v1 = newValue();
		for ( VectorX<V> e : values() ) 	v1.addAll(e);
		return v1;
	}

	public VectorX<K> toVectorK(){
		VectorX<K> v1= newVectorKey();
		
		for ( Map.Entry<K, VectorX<V> > e : entrySet() ) {
			K k = e.getKey();
			VectorX<V>  v = e.getValue();
			v1.catOn(new VectorX<K>(v.size(), k) );
		}	
		return v1;
	}
	//TMap<K, TVector<V> >
	public  boolean loadColumn(String fn, int icKey, int icValue){
		//TMap<K, TVector<V> >  mv = newInstance();
		BufferedReader br = FFile.newReader(fn);	
		if (br==null) 
			return false;
		String line = null;
		while ((line = FFile.readLine(br)) != null) {
			String vs[] = FString.split(line, "\t");
			K k=parseKey(vs[icKey]);
			V x=parseValueValue(vs[icValue]);
			getC(k).add(x);
		} 
		FFile.close(br);
		return true;
	}
	public V parseValueValue(String s){
		return null;
	}
}