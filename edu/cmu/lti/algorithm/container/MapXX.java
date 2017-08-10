package edu.cmu.lti.algorithm.container;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.cmu.lti.algorithm.Interfaces.CTag;
import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.Interfaces.IGetDblByInt;
import edu.cmu.lti.algorithm.Interfaces.IGetDblByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetIntByStr;
import edu.cmu.lti.algorithm.Interfaces.IMinusObjOn;
import edu.cmu.lti.algorithm.Interfaces.IMultiply;
import edu.cmu.lti.algorithm.Interfaces.IMultiplyOn;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.Interfaces.IRead;
import edu.cmu.lti.algorithm.Interfaces.IWrite;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

/**
 * @author nlao
 *This class is an extension to Map&lt;K, V&gt;
 *
 */
public class MapXX<K, V> extends HashMap<K, V> implements IPlusObjOn,
		IMinusObjOn, IWrite, IRead, Serializable, Cloneable, ICloneable,
		IGetIntByStr {//, ICopyable
	private static final long	serialVersionUID	= 2008042701L;	// YYYYMMDD
	public Class							ck								= Object.class;
	public Class							cv								= Object.class;

	public Integer getInt(String name) {
		if (name.equals(CTag.size)) return size();
		return null;
	}

	public int sum(String name) {
		int n = 0;
		for (V x : this.values())
			n += ((IGetIntByStr) x).getInt(name);
		return n;
	}

	public MapXX<K, V> copy(MapXX<K, V> v) {
		clear();
		try {
			for (Map.Entry<K, V> e : v.entrySet()) {
				K k = e.getKey();
				V x = e.getValue();
				if (x != null) put(k, (V) ((ICloneable) x).clone());
				else put(k, null);
			}
		} catch (Exception e) {
			putAll(v);
		}
		return this;
	}

	public MapXX<K, V> clone() {
		return newInstance().copy(this);
	}

	public MapXX(Class ck, Class cv) {
		this.ck = ck;
		this.cv = cv;
	}

	public MapXX(MapXX<K, V> m) {
		this.ck = m.ck;
		this.cv = m.cv;
		this.putAll(m);
	}

	public MapXX(Class ck, Class cv, Collection<K> keys, V value) {
		this.ck = ck;
		this.cv = cv;
		for (K key: keys) this.put(key, value);
	}
	
	public MapXX<K, V> load(MapXX<K, V> m) {
		clear();
		putAll(m);
		return this;
	}

	public VectorX<K> newVectorKey() {
		return new VectorX<K>(ck);
	}

	public VectorX<V> newVectorValue() {
		return new VectorX<V>(cv);
	}

	public SetX<K> newSetKey() {
		return new SetX<K>(ck);
	}

	public SetX<V> newSetValue() {
		return new SetX<V>(cv);
	}

	public MapXX<K, V> line() {
		return new MapXX<K, V>(ck, cv);
	}

	public K newKey() {//needed for primitive classes, silly java//weakness of Java template
		try {
			Object o = ck.newInstance();
			return (K) (o);
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
		return null;
	}

	public V newValue() {//weakness of Java template
		try {
			return (V) (cv.newInstance());
		} catch (Exception e) {
			if (cv.equals(Integer.class)) return (V) new Integer(0);
			System.out.println("cannot create new instance for " + e.getClass().getName());
			e.printStackTrace();
		}
		return null;
	}

	/*
	public TMap<K, V> updateIdx(VectorI vi){	
		TMap<K, V> m = newInstance();
		return m;
	}*/
	public MapXD<K> newMapXD() {
		return new MapXD<K>(ck);
	}

	//	public TMapXI<K> newMapKeyI(){
	//	return  new TMapXI<K>(ck);
	public MapXX<K, Integer> newMapKeyI() {
		return new MapXX<K, Integer>(ck, Integer.class);
	}

	public MapXD<K> getMDouble(String name) {
		MapXD<K> v = newMapXD();
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			Double y = ((IGetDblByStr) x).getDouble(name);
			if (y == null) continue;
			//if (y==0.0)continue;
			v.put(k, y);
		}
		return v;
	}

	public MapXX<K, Integer> getMInt(String name) {
		MapXX<K, Integer> v = newMapKeyI();
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			Integer y = ((IGetIntByStr) x).getInt(name);
			if (y == null) continue;
			//if (y==0.0)continue;
			v.put(k, y);
		}
		return v;
	}

	public MapXD<K> getMDoubleNoZero(String name) {
		MapXD<K> v = newMapXD();
		//TMapXD<K> v = new TMapXD<K>(ck);
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			Double y = ((IGetDblByStr) x).getDouble(name);
			if (y == null) continue;
			if (y == 0.0) continue;
			v.put(k, y);
		}
		return v;
	}

	public MapXD<K> getMDouble(int id) {
		MapXD<K> v = new MapXD<K>(ck);
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			v.put(k, ((IGetDblByInt) x).getDouble(id));
		}
		return v;
	}

	/*	public TMapXS<K> getMString(int id) {
			VectorS v = new VectorS(size());
			for (int i=0; i<size(); ++i){
				String s = ((IGetStringByInt) get(i)).getString(id);
				v.set(i,s);			
			}
			return v;
		}


		public VectorS getString(String name, ArrayList<Integer> vi) {
			VectorS v = new VectorS(vi.size());
			for (int i = 0; i < vi.size(); ++i) {
				V x = get(vi.get(i));
				String s = ((IGetStringByString) x).getString(name);
				v.set(i, s);			
			}			
			return v;
		}
		*/


	public MapXX<K, V> subTop(int n) {
		VectorX<K> vk = toVectorKey();
		VectorX<V> vv = ValuesToVector();
		return sub(vk.sub(vv.sortId().right(n)));
	}

	public TMapXVecX<K, V> toMMap() {
		return new TMapXVecX<K, V>(this);
	}

	public SetX<K> keysToSet() {
		SetX<K> v = newSetKey();
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			//V x = e.getValue();
			v.add(k);
		}
		return v;
	}

	public SetS toStringPairSet() {
		return toSetSKeyValue("=");
	}

	public SetS toSetSKeyValue(String c) {
		SetS v = new SetS();
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			v.add(k + c + x);
		}
		return v;
	}

	public TMapXVecX<V, K> newMVValueKey() {
		return new TMapXVecX<V, K>(cv, ck);
	}

	public TMapXVecX<V, K> ValueKeyToMapVec() {
		TMapXVecX<V, K> m = newMVValueKey();
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			m.insert(x, k);
		}
		return m;
	}

	public void saveSortedByKey(String fn) {
		TSetX<K> keys = new TSetX(this.keySet(), this.ck);
		BufferedWriter bwF = FFile.newWriter(fn);
		for (K k: keys)	FFile.writeln(bwF, k + "\t" + get(k));
		FFile.close(bwF);
	}
	
	public void saveSortedByValue(String fn) {
		saveSortedByValue(fn, true);
	}

	public void saveSortedByValue(String fn, boolean bDecreasing) {

		TMapXVecX<V, K> value_keys = ValueKeyToMapVec();
		VectorX<K> vKey = value_keys.toVectorV();//.reverseOn();
		VectorX<V> vValue = value_keys.toVectorK();//.reverseOn();

		BufferedWriter bwF = FFile.newWriter(fn);
		if (bDecreasing) for (int i = vKey.size() - 1; i >= 0; --i)
			FFile.write(bwF, vValue.get(i) + "\t" + vKey.get(i) + "\n");
		else for (int i = 0; i < vKey.size(); ++i)
			FFile.write(bwF, vValue.get(i) + "\t" + vKey.get(i) + "\n");
		FFile.close(bwF);
	}

	public SetX<V> toSetValue() {
		SetX<V> v = newSetValue();
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			v.add(x);
		}
		return v;
	}

	public VectorX<K> toVectorKey() {
		VectorX<K> v = newVectorKey();
		v.ensureCapacity(size());
		for (Map.Entry<K, V> e : entrySet())
			v.add(e.getKey());
		return v;
	}

	public VectorX<V> toVectorValue() {
		VectorX<V> v = newVectorValue();
		v.ensureCapacity(size());
		for (Map.Entry<K, V> e : entrySet())
			v.add(e.getValue());
		return v;
	}

	public VectorX<K> KeyToVecSortByValue(boolean descending) {
		VectorX<K> v = toVectorKey().sub(ValuesToVector().sortId());
		if (descending) v.reverseOn();
		return v;
	}

	//more efficient version
	public void KeyToVecSortByValue(VectorX<K> v) {
		v.clear();
		v.ensureCapacity(this.size());
		for (Map.Entry<V, VectorX<K>> e : ValueKeyToMapVec().entrySet())
			for (K k : e.getValue())
				v.add(k);
	}

	public void KeysToVecSortByValueDesc(VectorX<K> v) {
		KeyToVecSortByValue(v);
		v.reverseOn();
	}

	public VectorX<V> subV(ArrayList<K> vi, boolean bIgNull) {
		VectorX<V> v = newVectorValue();
		v.ensureCapacity(vi.size());
		for (K k : vi) {
			V x = getN(k);
			if (bIgNull & x == null) continue;
			v.add(x);
		}
		return v;
	}

	public VectorX<V> subVector(ArrayList<K> vi) {
		return subV(vi, false);
	}

	public VectorX<V> ValuesToVector() {
		VectorX<V> v = newVectorValue();
		v.ensureCapacity(size());
		for (Map.Entry<K, V> e : entrySet()) {
			//K k = e.getKey();
			V x = e.getValue();
			v.add(x);
		}
		return v;
	}

	public K idxMin() {
		V x = null;
		K i = null;
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V v = e.getValue();
			if (x != null) if (((Comparable) x).compareTo(v) <= 0) continue;
			x = v;
			i = k;
		}
		return i;
	}

	public K idxMax() {
		V x = null;
		K i = null;
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V v = e.getValue();
			if (x != null) if (((Comparable) x).compareTo(v) >= 0) continue;
			x = v;
			i = k;
		}
		return i;
	}

	public V min() {
		if (size() == 0) return null;
		return get(idxMin());
	}

	public V max() {
		if (size() == 0) return null;
		return get(idxMax());
	}

	public VectorI compareTo(V x) {
		return ValuesToVector().compareTo(x);
	}

	public VectorX<K> idxLargerThan(V x) {
		return toVectorKey().sub(compareTo(x).idxEqualToInt(1));
	}

	public VectorX<K> idxSmallerThan(V x) {
		return toVectorKey().sub(compareTo(x).idxEqualToInt(-1));
	}

	public SetX<K> idxSetSmallerThan(V x) {
		SetX<K> m = this.newSetKey();
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			if (((Comparable) e.getValue()).compareTo(x) == -1) m.add(k);
		}
		return m;
	}

	public void idxSmallerThan(V x, VectorX<K> v) {
		v.clear(); //use outside container to avoid creating object
		//v.removeAllElements();
		v.ensureCapacity(size());
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			if (((Comparable) e.getValue()).compareTo(x) == -1) v.add(k);
		}
	}

	public int cntSmallerThan(V x) {
		int cnt = 0;
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			if (((Comparable) e.getValue()).compareTo(x) == -1) ++cnt;
		}
		return cnt;
	}

	public VectorX<K> idxNEqualTo(V x) {
		return toVectorKey().sub(compareTo(x).idxNEqualToInt(0));
	}

	public VectorX<K> idxEqualTo(V x) {
		return toVectorKey().sub(compareTo(x).idxEqualToInt(0));
	}

	/*		TVector<K> v=new  TVector<K>(ck);
	v.ensureCapacity(size());
	for (int i=0; i<size(); ++i){
		int c=((Comparable)get(i)).compareTo(x);
		if (c==0) v.add(i);
	}
	return v;*/

	public MapXX<K, V> subLargerThan(V x) {
		return sub(idxLargerThan(x));
	}

	public MapXX<K, V> subSmallerThan(V x) {
		return sub(idxSmallerThan(x));
	}

	public MapXX<K, V> subEqualTo(V x) {
		return sub(idxEqualTo(x));
	}

	public MapXX<K, V> subNEqualTo(V x) {
		return sub(idxNEqualTo(x));
	}

	public MapXX<K, V> sub(Collection<K> v) {
		MapXX<K, V> m = newInstance();
		for (K k : v)
			if (containsKey(k)) m.put(k, get(k));
		return m;
	}

	public void sub(Collection<K> v, MapXX<K, V> m) {
		for (K k : v)
			if (containsKey(k)) m.put(k, get(k));
	}

	public MapXX<K, V> removeExcept(Collection<K> v) {
		if (v!=null) {
			MapXX<K, V> m = sub(v);
			clear();
			putAll(m);
		}
		return this;
	}

	public MapXX<K, V> subSet(Set<K> v) {
		MapXX<K, V> m = newInstance();
		for (K k : this.keySet())
			if (v.contains(k)) m.put(k, get(k));
		return m;
	}

	public MapXX<K, V> removeAll(Collection<K> v) {
		if (v!=null)	for (K k : v) remove(k);
		return this;
	}

	public MapXX<K, V> sub(K[] v) {
		MapXX<K, V> m = newInstance();
		for (K k : v) {
			if (containsKey(k)) m.put(k, getN(k));
		}
		return m;
	}

	public VectorX<V> subV(K[] vi) {
		VectorX<V> v = newVectorValue();
		v.ensureCapacity(vi.length);
		for (K k : vi)
			v.add(getN(k));
		return v;
	}

	public VectorX<V> subVE(K[] vi) {
		VectorX<V> v = newVectorValue();
		v.ensureCapacity(vi.length);
		for (K k : vi) {
			v.add(getN(k));
			if (v.lastElement() == null) System.err.println("unknown key=" + k);
		}
		return v;
	}

	public SetX<V> subS(K[] vi) {
		SetX<V> v = this.newSetValue();
		for (K k : vi)
			if (get(k) != null) v.add(get(k));
		return v;
	}

	public SetX<V> subSE(K[] vi) {
		SetX<V> v = this.newSetValue();
		for (K k : vi)
			if (get(k) != null) v.add(get(k));
		//else		System.err.println("unknown key="+k);
		return v;
	}

	public MapXX<K, V> filterOn(Set<K> m) {
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V v = e.getValue();
			if (!m.contains(k)) this.remove(k);
		}
		return this;
	}

	//get and create if neccesary
	public V getC(K k) {
		if (get(k) != null) return get(k);
		V x = newValue();
		put(k, x);
		return x;
	}

	// accept null as key
	public V getN(K k) {
		if (k == null) return null;
		return get(k);
	}

	/**get and create default value if necessary 
	 * @param k
	 * @return
	 */
	public V getD(K k) {
		if (get(k) != null) return get(k);
		return newValue();
	}

	public V getD(K k, V x) {
		if (get(k) != null) return get(k);
		return x;
	}

	public MapXX<K, V> load(ArrayList<K> vk, ArrayList<V> vx, ArrayList<Integer> vi) {
		for (int i : vi)
			put(vk.get(i), vx.get(i));
		return this;
	}

	public MapXX<K, V> load(ArrayList<K> vk, ArrayList<V> vx) {
		FSystem.checkVectorSizes(vk.size(), vx.size());
		for (int i = 0; i < vk.size(); ++i)
			put(vk.get(i), vx.get(i));
		return this;
	}

	public void to(ArrayList<K> vk, ArrayList<V> vx) {
		vk.clear();
		vx.clear();
		vk.ensureCapacity(size());
		vx.ensureCapacity(size());
		for (Map.Entry<K, V> e : this.entrySet()) {
			vk.add(e.getKey());
			vx.add(e.getValue());
		}
		return;
	}

	public String joinValueKey(String cpair, String c) {
		return join(cpair, c, true);
	}

	public String join() {
		return join("=", ",");
	}

	public String join(String cpair, String c) {
		return join(cpair, c, false);
	}

	public String join(String cpair, String c, boolean bValueKey) {
		StringBuffer sb = new StringBuffer();
		int first = 1;
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V v = e.getValue();
			if (first == 1) first = 0;
			else sb.append(c);
			if (bValueKey) sb.append(FString.format(v) + cpair + FString.format(k));
			else sb.append(FString.format(k) + cpair + FString.format(v));
		}
		return (sb.toString());
	}
	
	public String join(Collection<K> keys, String cpair, String c, boolean bValueKey) {
		StringBuffer sb = new StringBuffer();
		int first = 1;
		for (K k : keys){
			V v = get(k);
			if (first == 1) first = 0;		else sb.append(c);
			
			if (bValueKey) sb.append(FString.format(v) + cpair + FString.format(k));
			else sb.append(FString.format(k) + cpair + FString.format(v));
		}
		return (sb.toString());
	}
	

	public String joinTop(int nTop) {
		return joinTop(nTop, "=", " ");
	}

	public String joinTop(int nTop, String cpair, String c) {
		return joinTop(nTop, cpair, c, false);
	}

	public String joinTop(int nTop, String cpair, String c, boolean bValueKey) {
		StringBuffer sb = new StringBuffer();
		ArrayList<K> vK = this.KeyToVecSortByValue(true).left(nTop);
		int first = 1;
		for (K k : vK) {
			V v = get(k);
			if (first == 1) first = 0;
			else sb.append(c);

			if (bValueKey) sb.append(FString.format(v) + cpair + FString.format(k));
			else sb.append(FString.format(k) + cpair + FString.format(v));
		}
		return (sb.toString());
	}

	//in reversed order
	/*	public String joinR(String cpair, String c) {
			StringBuffer sb = new StringBuffer();
			int first=1;
			for ( Map.Entry<K, V> e :entrySet() ) {
				K k = e.getKey();
				V v = e.getValue();
				if (first==1)	first=0;			
				else	sb.append(c);				
				sb.append(FString.format(k)	+cpair+FString.format(v));			
			}
			return (sb.toString());
		}	*/

	public String toString() {
		return join("=", " ");
	}

	public BufferedWriter write(BufferedWriter bw) {
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V v = e.getValue();
			FFile.write(bw, k + "\t" + v + "\n");
		}
		return bw;
	}

	public BufferedReader read(BufferedReader br) {
		while (true) {
			String line = FFile.readLine(br);
			if (line == null) break;

		}
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V v = e.getValue();
			// writer.write(x);
			//((IRead) x).read(reader);
		}
		return br;
	}

	public boolean containsAll(Collection<K> m) {
		for (K x : m) if (!containsKey(x)) return false;
		return true;
	}

	public boolean containsAny(Collection<K> m) {
		for (K x : m) if (containsKey(x)) return true;
		return false;
	}

	public V inner(Map<K, Double> m) {
		//TMapXD<K> m1 = multiply(m);
		return multiply(m).sum();
	}

	public V inner(ArrayList<K> m) {
		return sub(m).sum();
	}

	public V inner(Set<K> m) {
		return sub(m).sum();
	}

	public V sum() {
		V x = this.newValue();
		for (V v : this.values())
			if (v != null) ((IPlusObjOn) x).plusObjOn(v);
		return x;
	}

	public V sum(Collection<K> keys) {
		V x = this.newValue();
		for (K k: keys) {
			V v = this.get(k);
			if (v != null) 
				((IPlusObjOn) x).plusObjOn(v);
		}
		return x;
	}	
	public V mean() {
		return (V) ((IMultiplyOn) sum()).multiplyOn(1.0 / size());
	}

	//position-wise product 
	//not kronecker product
	public MapXX<K, V> multiply(Map<K, Double> m) {
		MapXX<K, V> m2 = newInstance();
		if (m == null) return m2;
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V v = e.getValue();
			if (!m.containsKey(k)) continue;
			m2.put(k, (V) ((IMultiply) v).multiply(m.get(k)));
		}
		return m2;
	}

	/*	public TMap<K,V> multiplyOn(Map<K,Double> m) {//kronecker product
			//Set<Entry<K,V>>

			return this;
		}		*/
	//
	public MapXX<K, V> addOn(MapXX<K, V> m) {
		for (Map.Entry<K, V> e : m.entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			this.put(k, x);
		}
		return this;
	}

	public MapXX<K, V> addOn(K k, V x) {
		this.put(k, x);
		return this;
	}

	/*	public TMap<K,V> addOn(TMap<K,V> m){
		for ( Map.Entry<K,V> e : m.entrySet() ) {
			K k = e.getKey();
			V x = e.getValue();
			((IAddOn) getC(k)).addOn(x);
		}			
		return this;
	}*/
	public MapXX<K, V> plusObjOn(Object m) {
		return plusOn((MapXX<K, V>) m);
	}

	public MapXX<K, V> minusObjOn(Object m) {
		return minusOn((MapXX<K, V>) m);
	}

	public MapXX<K, V> plusOn(MapXX<K, V> m) {
		for (Map.Entry<K, V> e : m.entrySet())
			plusOn(e.getKey(), e.getValue());
		return this;
	}

	public MapXX<K, V> plusOn(K k, V x) {
		((IPlusObjOn) getC(k)).plusObjOn(x);
		return this;
	}

	public V plusOnGet(K k, V x) {
		((IPlusObjOn) getC(k)).plusObjOn(x);
		return get(k);
	}

	public MapXX<K, V> minusOn(MapXX<K, V> m) {
		for (Map.Entry<K, V> e : m.entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			((IMinusObjOn) getC(k)).minusObjOn(x);
		}
		return this;
	}

	public void save(String fn) {
		save(fn, "\t");
	}

	public void save(String fn, String sep) {
		save(fn, sep, null);
	}
	public void save(String fn, String sep, String title) {
		save(fn, sep, title, false);
	}
	
	public void save(String fn, String sep, String title, boolean value_key) {

		BufferedWriter bw = FFile.newWriter(fn);

		if (title != null) FFile.writeln(bw, title);
		save(sep, value_key, bw);
		FFile.close(bw);
	}
	
	public void save(BufferedWriter bw){
		save("\t", false, bw);
	}
	
	public void save(String sep, boolean value_key, BufferedWriter bw){
		for (Map.Entry<K, V> e : entrySet()) {
			if (value_key)
				FFile.writeln(bw, e.getValue()  + sep + e.getKey());
			else
				FFile.writeln(bw, e.getKey() + sep + e.getValue());
		}
	}

	public K parseKey(String k) {
		return null;
	}

	public V parseValue(String v) {
		return null;
	}

	public boolean loadFile(String fn, int col1, int col2, String sep,
			boolean bSkipTitle) {
		for (VectorS vs : FFile.enuRows(fn, sep, bSkipTitle)) {
			if (vs.size() < 2) {
				System.err.println("bad TMap line=" + vs.join());
				continue;
			}
			put(parseKey(vs.get(col1)), parseValue(vs.get(col2)));
		}
		System.out.println(size() + " items loaded");
		return true;
	}

	public boolean loadFile(String fn, int col1, int col2, String sep) {
		return loadFile(fn, col1, col2, sep, false);
	}

	public boolean loadFile(String fn, boolean bSkipTitle) {
		return loadFile(fn, 0, 1, "\t", bSkipTitle);
	}

	public boolean loadFile(String fn, int col1, int col2) {
		return loadFile(fn, col1, col2, "\t");
	}

	public boolean loadFile(String fn, String sep) {
		return loadFile(fn, 0, 1, sep);
	}

	public boolean loadFile(String fn) {
		return loadFile(fn, "\t");
	}

	public MapXX<K, V> newInstance() {
		return null;
	}

	/*	public K parseKey(String k){
			return null;
		}
		public K parseKey(String k){
			return null;
		}*/

	public MapXX<K, V> loadLine(String line, String cPair, String cSep) {
		//TMap<K,V> m = newInstance();		
		for (String item : line.split(cSep)) {
			String v[] = item.split(cPair);
			K k = parseKey(v[0]);
			V x = parseValue(v[1]);
			put(k, x);
		}
		return this;
	}

	public MapXX<K, V> loadStrings(String[] vs) {
		for (int i = 0; i < vs.length; i += 2)
			put(parseKey(vs[i]), parseValue(vs[i + 1]));
		return this;
	}

	public MapIX<V> newInstanceIX() {
		return new MapIX<V>(this.cv);
	}

	public MapIX<V> replaceIdx(Map<K, Integer> mi) {//<Integer> vi){	
		MapIX<V> m = newInstanceIX();
		for (Map.Entry<K, V> e2 : entrySet()) {
			K k = e2.getKey();
			V x = e2.getValue();
			if (!mi.containsKey(k)) continue;
			//((IUpdateIdx)x).updateIdx(vi); //should recur??			
			m.put(mi.get(k), x);
		}
		return m;
	}

	public MapIX<V> replaceMatchIdx(ArrayList<V> vx) {
		MapIX<V> m = newInstanceIX();
		for (int i = 0; i < vx.size(); ++i) {
			V x = get(vx.get(i));
			if (x == null) continue;
			m.put(i, x);
		}
		return m;
	}
	
  // clone the key if needed
  public V put(K key, V value) {
  	if (key instanceof ICloneable)
  		return super.put((K) ((ICloneable)key).clone(), value);
  	else
  		return super.put(key, value);
  }
  
  // don't clone the key
  public V putOriginal(K key, V value) {
 		return super.put(key, value);
  }
}
