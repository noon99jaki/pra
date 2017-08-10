/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.lti.algorithm.Interfaces.CTag;
import edu.cmu.lti.algorithm.Interfaces.IGetIntByStr;
import edu.cmu.lti.algorithm.Interfaces.IRead;
import edu.cmu.lti.algorithm.Interfaces.IWrite;
import edu.cmu.lti.algorithm.math.rand.FRand;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

/**
 * @author nlao
 *This class is an extension to Set&lt;V&gt;
 *
 */
public class SetX<K>  extends  HashSet<K> implements 
	IGetIntByStr, IWrite, IRead , Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Class c=Object.class;
	
	public Integer getInt(String name){
		if (name.equals(CTag.size)) return size();
		return null;
	}
	
	public SetX<K> newInstance(){
		return new SetX<K>(c);
	}
	public SetX<K> copy(){
		//return new TMap<K, V>(this);
		SetX<K> m = newInstance();
		m.addAll(this);
		return m;
	}
	public SetX<K> removeOn(Collection<K> c){
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
	
	public SetX(Class c){
		this.c = c; 
	}
	// will pre allocate memory
	public SetX(Collection<K> v){
		this.c = v.iterator().next().getClass();
		addAll(v);
	}
	// will incrementally allocate memory
	public SetX(Iterable<K> v, Class c){
		addIterable(v);
		this.c=c;
	}
	public SetX(K[] v){
		this.c = v.getClass();
		addAll(v);
	}
	

	public SetX<K>  addOn(Set<K> m){
		for ( K e : m) 	add(e);
		return this;
	}
	// What's this... clip counted version?

	public SetX<K>  and(Collection<K> v){
		SetX<K> m= newInstance();
		for ( K x : v) 
			if (contains(x))
				m.add(x);		
		return m;
	}	
	
	public boolean  containsAny(Collection<K> v){
		for ( K x : v) 
			if (contains(x))
				return true;
		return false;
	}
	
	public SetX<K>  and(K[] v){
		SetX<K> m= newInstance();
		for ( K x : v) {
			if (contains(x))
				m.add(x);
		}
		return m;
	}	
	public SetX<K>  andSet(Set<K> m){
		SetX<K> m1= newInstance();
		for ( K e : this) if (m.contains(e))	m1.add(e);		
		return m1;
	}
	
	public SetX<K> addAll (K[] v){
		for (K x : v)
			add(x);		
		return this;
	}
	public SetX<K> addIterable (Iterable<K> v){
		for (K x : v) add(x);		
		return this;
	}
	public SetX<K> load (K[] v){
		clear();
		return addAll(v);
	}
	public SetX<K> load (Collection<K> v){
		clear();
		addAll(v);
		return this;
	}	
/*	public TSet<K> fromMapKey<V> (TMap<K,V> m){
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
		return join( " ");
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

	public boolean in(SetX<K> m){
		for ( K x : this ) {
			if (!m.contains(x)) return false;
		}
		return true;
	}		


	public SetX<K> addOn(K x){
		this.add(x);
		return this;
	}

	public boolean loadFile(String fn) {
		return loadFile(fn, false);
	}

	public boolean loadFile(String fn, boolean bSkipTitle) {
		for (String line : FFile.enuLines(fn, bSkipTitle))
			add(parseLine(line));
		return true;
	}
	
	public boolean save(String fn){
		return	FFile.save(this,fn,"\n",null);
	}	
	public boolean save(String fn, String sep){
		return	FFile.save(this,fn,sep,null);
	}	
	public boolean saveT(String fn,String title){
		return	FFile.save(this,fn,"\n",title);
	}	
	public K parseLine(String k){		
		return null;
	}
	

	public boolean loadLine(String x, String sep) {
		this.clear();
		for (String s: x.split(sep))
			this.add(parseLine(s));
		return true;
	}

	public K first() {
		if (this.size()==0)	return null;
		return this.iterator().next();
	}
	public K sample() {
		if (size() == 0) return null;
		int i=FRand.drawInt(size());
		int j=0;
		for (K k : this) {
			if (i==j) return k;
			++j;
			if (j==size()) break;
		}
		FSystem.dieShouldNotHappen();
		return null;
	}	
}
