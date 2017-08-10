package edu.cmu.lti.algorithm.structure;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.container.MapXI;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorX;

public class IndexX<K> implements Serializable {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapXI<K> map_ ;//= new MapSI();
	public VectorX<K> list_;// = new VectorS();

	public IndexX(Class c) {
		 map_ = new MapXI<K>(c);
		list_ = new VectorX<K>(c);
	}
	
	public int size() {
		return list_.size();
	}

	public int get(K key) {
		Integer i = map_.get(key);
		//FSystem.checkTrue(i != null, "item not indexed=" + s);
		if (i == null) {
			System.err.println("item not indexed=" + key);
			// PRA want it to check edge validity
			return -1;
		}
		return i;
	}

	public int tryGet(K key) {
		Integer i = map_.get(key);
		if (i==null) return -1;
		return i;
	}
	
	public K get(int id) {	return list_.get(id); }
	
	public int add(K key) {
		Integer i = map_.get(key);
		if (i != null) return i;

		i = list_.size();
		
  	if (key instanceof ICloneable) key = (K) ((ICloneable)key).clone();

		map_.putOriginal(key, i);
		list_.add(key);
		return i;
	}
	public void getSet(K[] keys, SetI set) {
		set.clear();
		for (K key : keys) 	set.add(get(key));
	}
	public SetI getSet(K[] keys) {
		SetI set = new SetI();
		getSet(keys, set);
		return set;
	}
	
	public void tryGetSet(K[] keys, SetI set) {
		set.clear();
		for (K key : keys) 	{
			int id = this.tryGet(key);
			if (id>=0) set.add(id);
		}
	}
	public SetI tryGetSet(K[] keys) {
		SetI set = new SetI();
		tryGetSet(keys, set);
		return set;
	}
	

	public void addAll(K[] keys) {
		for (K key : keys) add(key);
	}

	public void addAll(Iterable<K>  keys) {
		for (K key : keys) add(key);
	}
	
	public String toString() {
		return list_.toString();
	}

	
	
	public void clear() {
		this.map_.clear();
		this.list_.clear();
	}
	
}
