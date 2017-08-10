package edu.cmu.lti.algorithm.container;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import edu.cmu.lti.algorithm.Interfaces.IRead;
import edu.cmu.lti.algorithm.Interfaces.IWrite;
import edu.cmu.lti.util.file.FFile;

public class TSetX <K>  extends  TreeSet<K> implements IWrite, IRead , Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	
	public Class c=Object.class;
	
	public TSetX<K> newInstance(){
		return new TSetX<K>(c);
	}
	public TSetX<K> copy(){
		//return new TMap<K, V>(this);
		TSetX<K> m = newInstance();
		m.addAll(this);
		return m;
	}
	public TSetX<K> removeOn(Collection<K> c){
		removeAll(c);
		return this;
	}
	
	public K newKey(){//needed for primitive classes, silly java//weakness of Java template
		try{
			return (K) c.newInstance();
		}
		catch (Exception e){
			System.out.println(e.getClass().getName());
			e.printStackTrace();			
		}
		return null;
	}
	
	public TSetX(Class c){
		this.c = c; 
	}
	// will incrementally allocate memory
	public TSetX(Iterable<K> v, Class c){
		addIterable(v);
		this.c=c;
	}
	public TSetX<K> addIterable (Iterable<K> v){
		for (K x : v) add(x);		
		return this;
	}
	
	public TSetX(Collection<K> v, Class c){
		this.c = c;	
		addAll(v);
	}
	
	public TSetX<K>  andSet(Set<K> m){
		TSetX<K> m1= newInstance();
		for ( K e : this) if (m.contains(e))	m1.add(e);		
		return m1;
	}

	
	public TSetX(K[] v){
		this.c = v.getClass();
		addAll(v);
	}
	
	public TSetX<K> from(Set<K> m){
		clear();
		addAll(m);
		return this;
	}	
	public TSetX<K>  addOn(Set<K> m){
		for ( K e : m) 	add(e);
		return this;
	}
	// What's this... clip counted version?

	public TSetX<K>  and(ArrayList<K> v){
		TSetX<K> m= newInstance();
		for ( K x : v) {
			if (contains(x))
				m.add(x);
		}
		return m;
	}	
	public TSetX<K>  and(K[] v){
		TSetX<K> m= newInstance();
		for ( K x : v) {
			if (contains(x))
				m.add(x);
		}
		return m;
	}	
	public TSetX<K>  and(Set<K> m){
		TSetX<K> m1= newInstance();
		for ( K e : this) {
			if (m.contains(e))
				m1.add(e);
		}
		return m1;
	}
	
	public TSetX<K> addEnum (Iterable<K> v){
		for (K x : v)
			add(x);		
		return this;
	}
	
	public TSetX<K> addAll (K[] v){
		for (K x : v)
			add(x);		
		return this;
	}
	public TSetX<K> from (K[] v){
		clear();
		addAll(v);
		return this;
	}
	public TSetX<K> from (ArrayList<K> v){
		clear();
		this.addAll(v);
		return this;
	}	
/*	public THSet<K> fromMapKey<V> (TMap<K,V> m){
		for ( Map.Entry<K, V> e : m.entrySet() ) {
			K k = e.getKey();
			V v = e.getValue();
			this.put(k, v);
		}	
		return this;
	}	*/	
	
	public String join(String c) {
		StringBuffer sb = new StringBuffer();
		int first=1;
		for ( K e : this) {
			if (first==1)	first=0;			
			else	sb.append(c);			
			sb.append(e);
		}
		return (sb.toString());
	}	
	public String toString() {
		return join( ", ");
	}
	public BufferedWriter write(BufferedWriter writer){// throws IOException {
		for ( K e : this) {
			// writer.write(x);
			//((IWrite) x).write(writer);
		}
		return writer;
	}
	public BufferedReader read(BufferedReader reader) {//throws IOException {
		for ( K e : this){
			// writer.write(x);
			//((IRead) x).read(reader);
		}
		return reader;
	}
	public VectorX<K> newVector(){
		return new VectorX<K>(c);
	}
	
	public VectorX<K> toVector(){
		VectorX<K> v= newVector();
		v.ensureCapacity(size());
		for ( K e : this){
		//for (int i=0; i< size(); ++i)
			v.add(e);
		}
		return v;
	}

	public boolean in(TSetX<K> m){
		for ( K x : this ) {
			if (!m.contains(x)) return false;
		}
		return true;
	}		
	/*
	public boolean in(TMap<K> m){
		for ( Map.Entry<K, V> e : entrySet() ) {
			K k = e.getKey();
			V v = e.getValue();
			// writer.write(x);
			//((IRead) x).read(reader);
		}
		return reader;
	}		*/
	public TSetX<K> addOn(K x){
		this.add(x);
		return this;
	}
	public boolean save(String fn){
		BufferedWriter writer  = FFile.newWriter(fn);
		for ( K k:this ) 
			FFile.write(writer, k+"\n");		
		FFile.flush(writer);	
		FFile.close(writer);
		return true;
	}	
}

