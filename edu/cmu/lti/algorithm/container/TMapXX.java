package edu.cmu.lti.algorithm.container;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.cmu.lti.algorithm.Interfaces.CTag;
import edu.cmu.lti.algorithm.Interfaces.ICloneable;
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

public class TMapXX<K, V> extends TreeMap<K, V> implements IPlusObjOn,
		IMinusObjOn, IWrite, IRead, Serializable, Cloneable, ICloneable,
		IGetIntByStr {//, ICopyable
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Class ck = Object.class;
	public Class cv = Object.class;

	public Integer getInt(String name) {
		if (name.equals(CTag.size)) return size();
		return null;
	}

	public V lastValue() {
		return get(this.lastKey());
	}
	public int sum(String name) {
		int n = 0;
		for (V x : this.values())
			n += ((IGetIntByStr) x).getInt(name);
		return n;
	}

	public TMapXX<K, V> copy(TMapXX<K, V> v) {
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

	public TMapXX<K, V> clone() {
		return newInstance().copy(this);
	}

	public TMapXX(Class ck, Class cv) {
		this.ck = ck;
		this.cv = cv;
	}

	public TMapXX(TMapXX<K, V> m) {
		this.ck = m.ck;
		this.cv = m.cv;
		this.putAll(m);
	}

	public TMapXX(Class ck, Class cv, Collection<K> keys, V value) {
		this.ck = ck;
		this.cv = cv;
		for (K key : keys)
			this.put(key, value);
	}

	public TMapXX<K, V> load(TMapXX<K, V> m) {
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

	public TMapXX<K, V> line() {
		return new TMapXX<K, V>(ck, cv);
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
			Object o = cv.newInstance();
			return (V) (o);
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
		return null;
	}


	public TMapXX<K, Integer> newHMapKeyI() {
		return new TMapXX<K, Integer>(ck, Integer.class);
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

	public TMapXX<K, V> subLargerThan(V x) {
		return sub(idxLargerThan(x));
	}

	public TMapXX<K, V> subSmallerThan(V x) {
		return sub(idxSmallerThan(x));
	}

	public TMapXX<K, V> subEqualTo(V x) {
		return sub(idxEqualTo(x));
	}

	public TMapXX<K, V> subNEqualTo(V x) {
		return sub(idxNEqualTo(x));
	}

	public TMapXX<K, V> sub(Collection<K> v) {
		TMapXX<K, V> m = newInstance();
		for (K k : v)
			if (containsKey(k)) m.put(k, get(k));
		return m;
	}

	public void sub(Collection<K> v, TMapXX<K, V> m) {
		for (K k : v)
			if (containsKey(k)) m.put(k, get(k));
	}

	public TMapXX<K, V> removeExcept(Collection<K> v) {
		TMapXX<K, V> m = sub(v);
		clear();
		putAll(m);
		return this;
	}

	public TMapXX<K, V> subSet(Set<K> v) {
		TMapXX<K, V> m = newInstance();
		for (K k : this.keySet())
			if (v.contains(k)) m.put(k, get(k));
		return m;
	}

	public TMapXX<K, V> removeAll(Collection<K> v) {
		for (K k : v)
			remove(k);
		return this;
	}

	public TMapXX<K, V> sub(K[] v) {
		TMapXX<K, V> m = newInstance();
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

	public TMapXX<K, V> filterOn(Set<K> m) {
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

	public TMapXX<K, V> load(ArrayList<K> vk, ArrayList<V> vx, ArrayList<Integer> vi) {
		for (int i : vi)
			put(vk.get(i), vx.get(i));
		return this;
	}

	public TMapXX<K, V> load(ArrayList<K> vk, ArrayList<V> vx) {
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
		for ( HMap.Entry<K, V> e :entrySet() ) {
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
		for (K x : m)
			if (!containsKey(x)) return false;
		return true;
	}

	public boolean containsAny(Collection<K> m) {
		for (K x : m)
			if (containsKey(x)) return true;
		return false;
	}

	public V inner(TMapXX<K, Double> m) {
		//THMapXD<K> m1 = multiply(m);
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

	public V mean() {
		return (V) ((IMultiplyOn) sum()).multiplyOn(1.0 / size());
	}

	//position-wise product 
	//not kronecker product
	public TMapXX<K, V> multiply(TMapXX<K, Double> m) {
		TMapXX<K, V> m2 = newInstance();
		if (m == null) return m2;
		for (Map.Entry<K, V> e : entrySet()) {
			K k = e.getKey();
			V v = e.getValue();
			if (!m.containsKey(k)) continue;
			m2.put(k, (V) ((IMultiply) v).multiply(m.get(k)));
		}
		return m2;
	}

	/*	public THMap<K,V> multiplyOn(HMap<K,Double> m) {//kronecker product
		//Set<Entry<K,V>>

		return this;
	}		*/
	//
	public TMapXX<K, V> addOn(TMapXX<K, V> m) {
		for (Map.Entry<K, V> e : m.entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			this.put(k, x);
		}
		return this;
	}

	public TMapXX<K, V> addOn(K k, V x) {
		this.put(k, x);
		return this;
	}

	/*	public THMap<K,V> addOn(THMap<K,V> m){
	for ( HMap.Entry<K,V> e : m.entrySet() ) {
		K k = e.getKey();
		V x = e.getValue();
		((IAddOn) getC(k)).addOn(x);
	}			
	return this;
	}*/
	public TMapXX<K, V> plusObjOn(Object m) {
		return plusOn((TMapXX<K, V>) m);
	}

	public TMapXX<K, V> minusObjOn(Object m) {
		return minusOn((TMapXX<K, V>) m);
	}

	public TMapXX<K, V> plusOn(TMapXX<K, V> m) {
		for (Map.Entry<K, V> e : m.entrySet())
			plusOn(e.getKey(), e.getValue());
		return this;
	}

	public TMapXX<K, V> plusOn(K k, V x) {
		((IPlusObjOn) getC(k)).plusObjOn(x);
		return this;
	}

	public V plusOnGet(K k, V x) {
		((IPlusObjOn) getC(k)).plusObjOn(x);
		return get(k);
	}

	public TMapXX<K, V> minusOn(TMapXX<K, V> m) {
		for (Map.Entry<K, V> e : m.entrySet()) {
			K k = e.getKey();
			V x = e.getValue();
			((IMinusObjOn) getC(k)).minusObjOn(x);
		}
		return this;
	}

	public boolean save(String fn) {
		return save(fn, "\t", null);
	}

	public boolean save(String fn, String sep) {
		return save(fn, sep, null);
	}

	public boolean save(String fn, String sep, String title) {

		BufferedWriter bw = FFile.newWriter(fn);

		if (title != null) FFile.writeln(bw, title);

		for (Map.Entry<K, V> e : entrySet())
			FFile.write(bw, e.getKey() + sep + e.getValue() + "\n");

		FFile.close(bw);
		return true;
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
				System.err.println("bad THMap line=" + vs.join(sep));//FFile.line_);
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

	public TMapXX<K, V> newInstance() {
		return null;
	}

	public TMapXX<K, V> loadLine(String line, String cPair, String cSep) {
		//THMap<K,V> m = newInstance();		
		for (String item : line.split(cSep)) {
			String v[] = item.split(cPair);
			K k = parseKey(v[0]);
			V x = parseValue(v[1]);
			put(k, x);
		}
		return this;
	}

	public TMapXX<K, V> loadStrings(String[] vs) {
		for (int i = 0; i < vs.length; i += 2)
			put(parseKey(vs[i]), parseValue(vs[i + 1]));
		return this;
	}
}
