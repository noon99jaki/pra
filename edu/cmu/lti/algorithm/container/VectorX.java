package edu.cmu.lti.algorithm.container;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.cmu.lti.algorithm.Interfaces.CTag;
import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.Interfaces.IGetBoolByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetDblByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetIntByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetMapIDByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetObjByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetObjByStrInt;
import edu.cmu.lti.algorithm.Interfaces.IGetStrByInt;
import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.Interfaces.IMultiplyOn;
import edu.cmu.lti.algorithm.Interfaces.IParseXML;
import edu.cmu.lti.algorithm.Interfaces.IPlusObj;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.Interfaces.IRead;
import edu.cmu.lti.algorithm.Interfaces.ISetDblByStr;
import edu.cmu.lti.algorithm.Interfaces.IUpdateIdx;
import edu.cmu.lti.algorithm.Interfaces.IWrite;
import edu.cmu.lti.algorithm.math.rand.FRand;
import edu.cmu.lti.algorithm.sequence.Pipe;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqEnum;
import edu.cmu.lti.algorithm.sequence.SeqS;
import edu.cmu.lti.algorithm.sequence.SeqTransform;
import edu.cmu.lti.algorithm.sequence.SeqS.PipeXS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.html.EColorScheme;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

/**
 * This class is an extension to Vector&lt;V&gt;
 * @author nlao
 * 
 */
public class VectorX<T> extends ArrayList<T> implements IWrite, IRead,
		Serializable, IUpdateIdx, Cloneable, ICloneable, IGetIntByStr {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Class c = Object.class;

	//public TVector() {}

	public VectorX<T> newInstance() {
		return new VectorX<T>(c);
	}

	public VectorX<T> newInstance(int n) {
		return new VectorX<T>(n, c);
	}

	public T newValue() {//weakness of Java template		
		try {
			Object o = c.newInstance();
			return (T) (o);
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
		return null;
	}

	public VectorX<T> plusOn(ArrayList<T> v) {
		if (size() != v.size()) System.err.println("unmatched vector size");

		//Math.min(size(), m.size())
		for (int i = 0; i < v.size(); ++i)
			((IPlusObjOn) getE(i)).plusObjOn(v.get(i));
		//	get(i).plusOn(m.get(i));
		return this;
	}

	public VectorX<T> plus(ArrayList<T> v) {
		if (size() != v.size()) System.err.println("unmatched vector size");

		VectorX<T> v1 = newInstance();
		v1.ensureCapacity(v.size());

		for (int i = 0; i < v.size(); ++i) {
			IPlusObj o = (IPlusObj) getE(i);
			if (o == null) v1.add(null); //Might regret about this
			else v1.add((T) o.plusObj(v.get(i)));
		}
		//	get(i).plusOn(m.get(i));
		return v1;
	}

	public VectorX<T> keepRight(int n) {
		if (n >= this.size()) return this;
		this.removeRange(0, size() - n);
		return this;
	}

	public VectorX<T> keepLeft(int n) {
		if (size() > n) this.trim(size() - n);
		return this;
	}

	public Integer getInt(String name) {
		if (name.equals(CTag.size)) return size();
		return null;
	}

	public T getS(int i) {//smart version of get
		if (i < 0 || i >= size()) return null;
		return get(i);
	}

	public T getN(Integer i) {//smart version of get
		if (i == null) return null;
		return get(i);
	}

	public T getCir(int i) {//get in circle
		return get(i % size());
	}

	public T getC(int i) {//get or create
		if (get(i) != null) return get(i);
		return set(i, newValue());
	}

	public T getD(int i, T x) {//get or default
		return size() > i ? get(i) : x;
	}

	public VectorX<T> initAll() {
		for (int i = 0; i < size(); ++i)
			this.set(i, newValue());
		return this;
	}

	public VectorX<T> initClasses(Class c, String classes) throws Exception {
		return initClasses(c.getPackage().getName() + ".", FString
				.tokenize(classes));
	}

	public VectorX<T> initClasses(String classRoot, String classes)
			throws Exception {
		return initClasses(classRoot, FString.tokenize(classes));
	}

	public VectorX<T> initClasses(String classRoot, VectorS vs) throws Exception {
		for (String cn : vs) {
			T x = (T) Class.forName(classRoot + cn).newInstance();
			add(x);
		}
		return this;
	}

	public VectorX<T> initNull() {
		for (int i = 0; i < size(); ++i) {
			if (get(i) != null) continue;
			set(i, (T) newValue());
		}
		return this;
	}

	//get and extend with default value if not exist
	public T getE(int i) {
		return extend(i + 1).get(i);
	}

	public T getE(int i, T dft) {
		return extend(i + 1, dft).get(i);
	}

	public VectorX<T> setE(int i, T x) {//TVector<V>
		extend(i + 1);
		set(i, x);
		return this;
	}

	public VectorX<T> setE(int i, T x, T dft) {//TVector<V>
		extend(i + 1, dft);
		set(i, x);
		return this;
	}

	public T firstElement() {
		return get(0);
	}
	public T lastElement() {
		return get(this.size()-1);
	}

	public VectorX<T> extend(int n) {
//		return extend(n, null);
		int k = size();
		if (k >= n) return this;

		this.ensureCapacity(n);
		for (int j = k; j < n; ++j)		add(newValue());
		return this;
	}

	public VectorX<T> extend(int n, T x) {//TVector<V>
		int k = size();
		if (k >= n) return this;

		this.ensureCapacity(n);
		for (int j = k; j < n; ++j) add (x);
		return this;
	}

	public VectorX<T> addN(int n, T x) {//TVector<V>
		int k = size();
		this.ensureCapacity(k + n);
		for (int j = k; j < k + n; ++j)
			add(x);
		return this;
	}

	public VectorX(Class c) {
		this.c = c;
	}

	// will pre allocate memory
	public VectorX(Collection<T> v, Class c) {
		if (v.size() == 0) return;
		this.c = c;
		addAll(v);
	}

	// will incrementally allocate memory
	public VectorX(Iterable<T> v, Class c) {
		this.c = c;
		for (T x : v) add(x);
	}

	public VectorX(int k, Class c) {
		this.c = c;
		setSize(k);
	}

	// TODO: remove this function in the end?
	public void setSize(int size) {
		if (size() < size) {
			this.ensureCapacity(size);
			for (int i=this.size(); i<size; ++i)
				this.add(null);
		}
		if (size()> size)
			this.removeRange(size, size());
	}
	
	public VectorX(T[] v) {
		this.c = v.getClass();
		addAll(v);
	}

	public VectorX(T x) {
		this.c = x.getClass();
		add(x);
	}

	public VectorX<T> load(Collection<T> v) {
		clear();
		this.addAll(v);
		return this;
	}

	public VectorX<T> addAll(T[] v) {
		this.ensureCapacity(size() + v.length);
		for (T x : v)
			add(x);
		return this;
	}

	public void load(String[] v) {
		clear();
		ensureCapacity(v.length);
		for (String item : v) {
			if (item == null) continue;
			if (item.length() == 0) continue;
			add(parseLine(item));
		}
	}

	public void load(ArrayList<String> vs) {
		this.setSize(vs.size());
		for (int i = 0; i < vs.size(); ++i) {
			if (vs.get(i) == null) continue;
			if (vs.get(i).length() == 0) continue;
			this.set(i, this.parseLine(vs.get(i)));
		}
	}

	public void load(String line) {
		load(line, "\t");
	}

	public void load(String line, String c) {
		load(line.split(c));
	}

	public VectorX<T> setAll(T[] v) {
		for (int i = 0; i < v.length; ++i)
			this.set(i, v[i]);
		return this;
	}

	public VectorX<T> addOn(T x) {
		add(x);
		return this;
	}

	//copy without duplicate the content
	public VectorX<T> copy(T[] v) {
		clear();
		addAll(v);
		return this;
	}

	public VectorX(int k, T x) {
		this.c = x.getClass();
		addAll(Collections.nCopies(k, x));
	}

	//copy and ducplicate the content
	public VectorX<T> copyShallow(VectorX<T> v) {
		clear();
		addAll(v);
		/*try{
			for (V x: v)
				add((V)  ((ICopyable)x).copy());
		}
		catch(Exception e){
			addAll(v);
		}*/
		return this;
	}

	public VectorX<T> clone() {
		VectorX<T> v = newInstance();
		v.ensureCapacity(size());
		try {
			for (T x : this)
				v.add((T) ((ICloneable) x).clone());
		} catch (Exception e) {
			v.clear();
			v.addAll(this);
		}
		return v;
		//return newInstance().copyShallow(this);
	}

	public void init(int n) {
		setSize(n);
		initAll();
	}

	public SetX<T> toSet() {
		SetX<T> m = newSetValue();
		for (T x : this)
			if (x != null) m.add(x);
		return m;
	}

	public TreeSet<T> toTSet() {
		TreeSet<T> m = new TreeSet<T>();
		for (T x : this)
			if (x != null) m.add(x);
		return m;
	}
	
	public VectorX<T> parseXML(Element e, String tag) {
		clear();
		NodeList v = e.getElementsByTagName(tag);
		for (int i = 0; i < v.getLength(); i++) {
			T x = newValue();
			((IParseXML) x).parseXML((Element) v.item(i));
			add(x);
		}
		return this;
	}

	public int idxMin() {
		T x = null;
		int i = -1;
		for (int j = 0; j < size(); ++j) {
			T y = get(j);
			if (x != null) if (((Comparable) x).compareTo(y) <= 0) continue;
			x = y;
			i = j;
		}
		return i;
	}

	public int idxMax() {
		T x = null;
		int i = -1;
		for (int j = 0; j < size(); ++j) {
			T y = get(j);
			if (x != null) if (((Comparable) x).compareTo(y) >= 0) continue;
			x = y;
			i = j;
		}
		return i;
	}

	public T min() {
		int i = idxMin();
		if (i >= 0) return get(i);
		return null;
	}

	public T max() {
		int i = idxMax();
		if (i >= 0) return get(i);
		return null;
	}

	public T clone(int i) {
		return (T) ((ICloneable) get(i)).clone();
	}

	public VectorX<T> except(int i) {
		return except(i, i + 1);
	}

	public VectorX<T> except(int b, int e) {
		VectorX<T> v = newInstance();
		for (int i = 0; i < size(); ++i)
			if (i < b || i >= e) v.add(get(i));
		return v;
	}

	public VectorX<T> sub(int b) {
		return sub(b, size());
	}

	public VectorX<T> sub(int b, int e) {
		//if (e>size()) e= size();
		//if (b<0 || e<0|| e-b<0)		return null;

		VectorX<T> v = newInstance();
		if (e - b < 0) return v;
		b = Math.max(0, b);
		e = Math.min(size(), e);
		v.ensureCapacity(e - b);
		for (int i = b; i < e && i < size(); ++i)
			//v.set(i-b,get(i));
			v.add(get(i));// TODO: clone or get?
		return v;
	}

	public VectorX<T> truncateOn(int n) {
		if (size() > n) this.setSize(n);
		return this;
	}

	public VectorX<T> left(int n) {
		return sub(0, n);
	}

	public VectorX<T> right(int n) {
		return mid(size() - n);
	}

	public VectorX<T> mid(int b) {
		return sub(b, size());
	}

	public VectorX<T> mid(int b, int n) {
		return sub(b, b + n);
	}

	public T pop() {
		if (size() == 0) return null;
		T x = this.lastElement();
		trim(1);
		return x;
	}

	public T popFrontOn() {
		if (size() == 0) return null;
		T x = this.firstElement();
		for (int i = 0; i < size() - 1; ++i)
			this.set(i, get(i + 1));
		trim(1);
		return x;
	}

	public VectorX<T> pop(int k) {
		VectorX<T> v = right(k);
		trim(k);
		return v;
	}

	public VectorS toVectorS() {
		VectorS vs = new VectorS();//size());
		vs.ensureCapacity(size());
		for (T x : this)
			if (x == null) vs.add(null);
			else vs.add(x.toString());
		return vs;
	}

	public VectorX<T> trim(int k) {

		this.setSize(Math.max(0, size() - k));
		return this;
	}

	public VectorX<T> set(Collection<Integer> vi, T x) {
		for (int i : vi)
			set(i, x);
		return this;
	}

	public VectorX<T> set(ArrayList<Integer> vi, ArrayList<T> vx) {
		FSystem.checkVectorSizes(vi.size(), vx.size());
		for (int i = 0; i < vi.size(); ++i)
			set(vi.get(i), vx.get(i));
		return this;
	}

	public VectorX<T> set(MapXX<Integer, T> m) {
		for (Map.Entry<Integer, T> e : m.entrySet())
			set(e.getKey(), e.getValue());
		return this;
	}

	/*	public TVector<V> set(Collection<Integer> vi, Collection<V> vx) {
			for (int i=0; i<vi.size();++i)
				set(i, vx.(i));		
			return this;
		}	*/

	public VectorX<T> setRange(int ib, int ie, T x) {
		for (int i = ib; i < ie; ++i)
			set(i, x);
		return this;
	}

	public VectorX<T> setAll(T x) {
		for (int i = 0; i < size(); ++i)
			set(i, x);
		return this;
	}

	public VectorX<T> reset(T vx[]) {
		clear();
		addAll(vx);
		return this;
	}

	public VectorX<T> reset(int n, T x) {
		this.setSize(n);
		setAll(x);
		return this;
	}
	
	public VectorX<T> reset(int n) {
		this.setSize(n);
		for (int i = 0; i< this.size(); ++i)
			if (this.get(i) == null) this.set(i, this.newValue());
		return this;
	}

	public SetX<T> newSetValue() {
		return new SetX<T>(c);
	}

	public SetX<T> subSet(Collection<Integer> vi) {
		SetX<T> v = newSetValue();
		for (int i : vi)
			//if (get(i)>=0)
			v.add(get(i));
		return v;
	}

	public SetI idxSet(Set<T> items) {
		SetI ids = new SetI();
		idxSet(items, ids);
		return ids;
	}
	public void idxSet(Set<T> items, SetI ids) {
		for (int i=0; i< this.size(); ++i)
			if (items.contains(get(i))) ids.add(i);
	}
	
	public VectorX<T> sub(Collection<Integer> vi) {
		VectorX<T> v = newInstance();
		if (vi == null) return v;

		v.ensureCapacity(vi.size());

		// TODO: clone or get?
		for (Integer i : vi) {
			if (i >= this.size()) {
				FSystem.die("#column in file=" + this.size() + "<");
			}
			if (i == null) v.add(null);
			else v.add(get(i));
		}
		return v;
	}

	public VectorX<T> intersect(Collection<T> m) {
		VectorX<T> v = newInstance();
		for (T x : this)	if (m.contains(x)) v.add(x);
		return v;
	}

	public VectorX<T> subByMask(ArrayList<Integer> vi) {
		if (vi.size() != size()) System.err.print("unmatched dimension");
		VectorX<T> v = newInstance();
		v.ensureCapacity(vi.size());
		for (int i = 0; i < vi.size(); ++i)
			if (vi.get(i) > 0) v.add(get(i));
		return v;
	}

	public VectorX<T> subRand(double p) {
		VectorX<T> v = newInstance();
		v.ensureCapacity((int) (size() * p) + 10);
		for (T x : this)
			if (FRand.drawBoolean(p)) v.add(x);
		return v;
	}

	public VectorX<T> subRandN(int n) {
		VectorX<T> v = newInstance();
		v.ensureCapacity(n);
		if (n >= size()) {
			v.addAll(this);
			return v;
		}

		VectorI vi = VectorI.seq(size());
		vi.randomize().randomize();
		return this.sub(vi.left(n));
	}

	// round robin
	public VectorX<T> subRR(int nGroup, int iStart) {
		VectorX<T> v = newInstance();
		v.ensureCapacity(size() / nGroup);
		for (int i = iStart; i < size(); i += nGroup)
			v.add(get(i));
		return v;
	}

	//find a subsequence v 
	public int findSeq(ArrayList<T> v) {
		return findSeq(v, 0);
	}

	public VectorI findAny(Set<T> v) {
		VectorI vi = new VectorI();
		for (int i = 0; i < size(); ++i)
			if (v.contains(get(i))) vi.add(i);
		return vi;
	}

	//find a subsequence v starting from ib 
	public int findSeq(ArrayList<T> v, int ib) {
		for (int i = ib; i < size() - v.size(); ++i) {
			int j = 0;
			for (; j < v.size(); ++j) {
				if (!get(i + j).equals(v.get(j))) break;
			}
			if (j != v.size()) continue; //failed matching
			return i;
		}
		return -1;
	}
	
	public int findFrequency(T x) {
		int freq=0;
		for (int i = 0; i < size(); ++i)	if (get(i).equals(x)) ++freq;
		return freq;
	}

	public int findExact(T x) {
		for (int i = 0; i < size(); ++i)	if (get(i).equals(x)) return i;
		return -1;
	}

	public int idxFirst(Set<T> v) {
		for (int i = 0; i < size(); ++i) {
			if (v.contains(get(i))) return i;
		}
		return -1;
	}

	public int idxFirst(T x) {
		for (int i = 0; i < size(); ++i) {
			if (get(i).equals(x)) return i;
		}
		return -1;
	}

	public int idxLast(T x) {
		for (int i = size() - 1; i >= 0; --i) {
			if (get(i).equals(x)) return i;
		}
		return -1;
	}

	//almost the same as addAll(), except returning itself
	public VectorX<T> catOn(Collection<T> v) {
		this.ensureCapacity(size() + v.size());
		this.addAll(v);
		return this;
	}

	public VectorX<T> replace(T x, ArrayList<T> v) {
		int id = findExact(x);
		if (id < 0) return null;
		VectorX<T> v1 = left(id).catOn(v).catOn(mid(id + 1));
		return v1;
	}

	//replace all occurences of sequence with a single element
	public VectorX<T> replace(ArrayList<T> v, T x) {
		int idx = 0;
		for (; true;) {
			//to find
			idx = findSeq(v, idx);
			if (idx < 0) break;
			replace(idx, v.size(), x);
		}
		return null;
	}

	//replace an subsequence with a single element
	public VectorX<T> replace(int ib, int len, T x) {
		return left(ib).pushBackOn(x).catOn(mid(ib + len));
	}

	public VectorX<T> replaceOn(T x, T y) {
		for (int i = 0; i < size(); ++i)
			if (get(i).equals(x)) set(i, y);
		return this;
	}

	public VectorX<T> replace(T x, T y) {
		return ((VectorX<T>) clone()).replaceOn(x, y);
	}

	public BufferedWriter write(BufferedWriter writer) {// throws IOException {
		for (T x : this) {
			//writer.write(x.toString());
			((IWrite) x).write(writer);
		}
		return writer;
	}

	public BufferedReader read(BufferedReader reader) {//throws IOException {
		for (T x : this) {
			//String s; Integer i; i.
			//reader.read(x);
			((IRead) x).read(reader);
		}
		return reader;
	}

	public VectorX<T> reverseOn() { // this.reverse();
		int m = size() / 2;
		for (int i = 0; i < m; ++i) {
			int j = size() - i - 1;
			T x = get(i);
			set(i, get(j));
			set(j, x);
		}
		return this;
	}

	public VectorX<T> reverse() { // this.reverse();
		VectorX<T> v = newInstance();
		v.ensureCapacity(size());
		for (int i = 1; i <= size(); ++i)
			v.add(get(size() - i));
		return v;
	}

	public VecMapID getMapID(String name) {
		VecMapID v = new VecMapID();
		v.ensureCapacity(size());
		for (int i = 0; i < size(); ++i) {
			v.add(((IGetMapIDByStr) get(i)).getMapID(name));
		}
		return v;
	}

	public VectorI getVI(String name) {
		VectorI v = new VectorI(size());
		for (int i = 0; i < size(); ++i) {
			Integer s = ((IGetIntByStr) get(i)).getInt(name);
			v.set(i, s);
		}
		return v;
	}

	public String sumS(String name) {
		return sumS(name, null);
	}

	public String sumS(String name, String c) {
		StringBuffer sb = new StringBuffer();
		for (T x : this) {
			if (sb.length() != 0) if (c != null) sb.append(c);
			sb.append(((IGetStrByStr) x).getString(name));
		}
		return sb.toString();
	}

	public int sumI(String name) {
		int n = 0;
		for (T x : this)
			n += ((IGetIntByStr) x).getInt(name);
		return n;
	}

	public double avgI(String name) {
		return ((double) sumI(name)) / this.size();
	}

	public int sumD(String name) {
		int n = 0;
		for (T x : this)
			n += ((IGetDblByStr) x).getDouble(name);
		return n;
	}

	public VectorX<Object> getVO(String name) {
		VectorX<Object> v = new VectorX<Object>(Object.class);
		v.ensureCapacity(size());
		for (int i = 0; i < size(); ++i) {
			Object x = ((IGetObjByStr) get(i)).getObj(name);
			v.add(x);
		}
		v.c = v.firstElement().getClass();
		return v;
	}

	public VectorX<Object> getVO(String name, int id) {
		VectorX<Object> v = new VectorX<Object>(Object.class);
		v.ensureCapacity(size());
		for (int i = 0; i < size(); ++i) {
			Object x = ((IGetObjByStrInt) get(i)).getObj(name, id);
			v.add(x);
		}
		v.c = v.firstElement().getClass();
		return v;
	}

	public VectorX<Object> getVO(String name, Collection<Integer> vi) {
		VectorX<Object> v = new VectorX<Object>(Object.class);
		v.ensureCapacity(vi.size());
		for (int i : vi) {
			Object x = ((IGetObjByStr) get(i)).getObj(name);
			v.add(x);
		}
		return v;
	}

	public VectorS getVS(String name, Collection<Integer> vi) {
		VectorS v = new VectorS();//vi.size());
		v.ensureCapacity(vi.size());
		for (int i : vi) {
			String s = ((IGetStrByStr) get(i)).getString(name);
			//v.set(i, s);
			v.add(s);
		}
		return v;
	}

	public VectorI getVI(String name, Collection<Integer> vi) {
		VectorI v = new VectorI();//vi.size());
		v.ensureCapacity(vi.size());
		for (int i : vi)
			v.add(((IGetIntByStr) get(i)).getInt(name));
		return v;
	}

	public VectorS getVS(String name, int[] vi) {
		VectorS v = new VectorS();
		v.ensureCapacity(vi.length);
		for (int i = 0; i < vi.length; ++i) {
			if (vi[i] >= 0) v.add(((IGetStrByStr) get(vi[i])).getString(name));
		}
		return v;
	}

	public VectorS getVS() {
		VectorS v = new VectorS(size());
		for (int i = 0; i < size(); ++i) {
			v.set(i, get(i).toString());
		}
		return v;
	}

	public VectorB getVB(String name) {
		VectorB v = new VectorB(size());
		for (int i = 0; i < size(); ++i) {
			Boolean s = ((IGetBoolByStr) get(i)).getBoolean(name);
			v.set(i, s);
		}
		return v;
	}

	public VectorS getVS(String name) {
		VectorS v = new VectorS(size());
		for (int i = 0; i < size(); ++i) {
			String s = ((IGetStrByStr) get(i)).getString(name);
			v.set(i, s);
		}
		return v;
	}

	public SeqS enuString(String name) {//SequenceS
		return new SeqS(this, new PipeXS<T>(name));
	}

	public Seq<T> enu() {
		return new SeqEnum<T>(this.c, this);
	}

	public static class PipeEnu<T> implements Pipe<Integer, T> {
		private VectorX<T> v;

		public PipeEnu(VectorX<T> v) {
			this.v = v;
		}

		public T transform(Integer a) {
			return v.get(a);
		}
	};

	public Seq<T> enu(VectorI vi) {
		return new SeqTransform<T>(this.c, vi, new PipeEnu(this));
	}

	public SeqS enuString(Pipe<T, String> pipe) {
		return new SeqS(this, pipe);
	}

	public TMapXVecI<T> newTMapVectorXI() {
		return new TMapXVecI<T>(c);
	}

	public TMapXVecI<T> toMapVectorValueId() {
		TMapXVecI<T> m = newTMapVectorXI();
		for (int i = 0; i < size(); ++i)
			m.insert(get(i), i);
		return m;
	}

	public TMapSVecX<T> toMapVectorSX(String name) {
		TMapSVecX<T> m = new TMapSVecX<T>(c);
		for (int i = 0; i < size(); ++i) {
			//V x = get(i);
			String s = ((IGetStrByStr) get(i)).getString(name);
			//got problem instantiating a template 
			//m.getC( s).add(get(i));
			if (m.get(s) == null) m.put(s, newInstance());
			m.get(s).add(get(i));
		}
		return m;
	}

	//assume all values are unique
	public MapXI<T> newMapValueId() {
		return new MapXI<T>(c);
	}

	public MapXI<T> toMapValueId() {
		MapXI<T> m = newMapValueId();
		for (int i = 0; i < size(); ++i)
			if (get(i) != null) m.put(get(i), i);
		return m;
	}

	public MapXX<Integer, T> newMapIdValue() {
		return new MapIX<T>(c);
	}

	public MapXX<Integer, T> toMap() {
		MapXX<Integer, T> m = newMapIdValue();
		for (int i = 0; i < size(); ++i)
			m.put(i, get(i));
		return m;
	}

	public MapXX<Integer, T> subMap(ArrayList<Integer> vi) {
		MapXX<Integer, T> m = newMapIdValue();
		for (int i : vi)
			m.put(i, get(i));
		return m;
	}

	//TODO: not efficient
	public VectorI sortId() {
		return sortId(false);
	}
	
	public VectorI sortId(boolean desc) {
		VectorI vi = (VectorI) toMapVectorValueId().toVectorV();
		if (desc) vi.reverseOn();
		return vi;
	}

	//public VectorI  sortId1()	{}

	public VectorX<T> sortByDoubleDesent(String name) {
		VectorD vd = getVD(name);
		return sub(vd.sortId().reverseOn());
	}

	public VectorX<T> sortByDouble(String name) {
		VectorD vd = getVD(name);
		return sub(vd.sortId());
	}

	public VectorX<T> sortByInt(String name) {
		VectorI vd = getVI(name);
		return sub(vd.sortId());
	}

	public VectorX<T> reorder(VectorI vi) {
		VectorX<T> v = newInstance();
		v.ensureCapacity(vi.size());
		for (int i : vi)
			v.add(get(vi.get(i)));
		return v;
	}

	public VectorX<T> shrinkUniqueByString(String name) {
		VectorX<T> v = newInstance();
		v.ensureCapacity(size());
		SetS m = new SetS();

		for (T x : this) {
			String key = ((IGetStrByStr) x).getString(name);
			if (m.contains(key)) continue;
			m.addOn(key);
			v.add(x);
		}
		return v;
	}

	public VectorX<T> shrinkUniqueByStringSorted(String name) {
		MapSX<T> m = new MapSX<T>(c);
		for (T x : this) {
			String key = ((IGetStrByStr) x).getString(name);
			m.addOn(key, x);
		}
		return m.ValuesToVector();
	}

	public VectorX<T> shrinkUniqueByStringCumu(String name) {
		MapSX<T> m = new MapSX<T>(c);
		for (T x : this) {
			String key = ((IGetStrByStr) x).getString(name);
			m.plusOn(key, x);
		}
		return m.ValuesToVector();
	}

	public VectorD getVD(String name) {
		VectorD v = new VectorD(size());
		for (int i = 0; i < size(); ++i) {
			IGetDblByStr o = (IGetDblByStr) get(i);
			if (o != null) v.set(i, o.getDouble(name));
		}
		return v;
	}

	public VectorD getVD(String name, Collection<Integer> vi) {
		VectorD v = new VectorD();
		v.ensureCapacity(vi.size());
		for (int i : vi)
			v.add(((IGetDblByStr) get(i)).getDouble(name));
		return v;
	}

	public MapID getMD(String name, Collection<Integer> vi) {
		MapID v = new MapID();
		for (int i : vi) {
			T x = get(i);
			Double s = ((IGetDblByStr) x).getDouble(name);
			v.put(i, s);
		}
		return v;
	}

	public SetS getMS(String name) {
		SetS v = new SetS();
		for (int i = 0; i < size(); ++i) {
			String s = ((IGetStrByStr) get(i)).getString(name);
			v.add(s);
		}
		return v;
	}

	public VectorX<T> setVD(String name, ArrayList<Double> vd) {
		FSystem.checkVectorSizes(size(), vd.size());
		for (int i = 0; i < size(); ++i)
			((ISetDblByStr) get(i)).setDouble(name, vd.get(i));
		return this;
	}

	public VectorX<T> setVD(String name, ArrayList<Integer> vi, ArrayList<Double> vd) {
		FSystem.checkVectorSizes(vi.size(), vd.size());
		for (int i = 0; i < vi.size(); ++i)
			((ISetDblByStr) get(vi.get(i))).setDouble(name, vd.get(i));
		return this;
	}

	public VectorX<T> setMD(String name, Map<Integer, Double> m) {
		for (Map.Entry<Integer, Double> e : m.entrySet())
			((ISetDblByStr) get(e.getKey())).setDouble(name, e.getValue());
		return this;
	}

	public VectorS getVS(int id) {
		VectorS v = new VectorS(size());
		for (int i = 0; i < size(); ++i) {
			String s = ((IGetStrByInt) get(i)).getString(id);
			v.set(i, s);
		}
		return v;
	}

	public VectorX<T> shrinkUnique() {
		return toSet().toVector();
	}

	//remove all occurence of value x 
	public VectorX<T> removeValue(T x) {
		VectorX<T> v = newInstance();
		for (int i = 0; i < size(); ++i) {
			if (((Comparable) get(i)).compareTo(x) == 0) continue;
			v.add(get(i));
		}
		return v;
	}

	public VectorX<T> shrinkRepeated() {
		VectorX<T> v = newInstance();
		for (int i = 0; i < size(); ++i) {
			if (i == 0) {
				v.add(get(i));
				continue;
			}
			if (((Comparable) get(i)).compareTo(get(i - 1)) == 0) continue;
			v.add(get(i));
		}
		return v;
	}

	public VectorX<T> shrinkRepeatedOn() {
		clear();
		addAll(shrinkRepeated());
		return this;
	}

	public VectorX<T> sort() {
		return this.toSet().toVector();
	}

	public VectorX<T> sortOn() {
		load(sort());
		return this;
	}

	/** 
	 * assuming v is sorted in ascending order
	 * @param x
	 * @return  i if x <= v[i], else v.size()
	 */
	public int findSorted(T x) {
		if (size() == 0) return 0;

		int l = 0; // v[l] is always < x
		int r = size() - 1; // v[r] is always > x
		if (((Comparable) x).compareTo(get(0)) <= 0) return 0;
		if (((Comparable) x).compareTo(get(r)) > 0) return size();

		int m = r / 2;
		for (; r - l > 1;) {
			int c = ((Comparable) x).compareTo(get(m));
			if (c == 0) return m;
			if (c > 0) l = m;
			else r = m;
			m = (l + r) / 2;
		}
		return r;
	}

	/** 
	 * assuming v is sorted in decending order
	 * @param x
	 * @return  i if x <= v[i], else v.size()
	 */
	public int findSortedDesc(T x) {
		if (size() == 0) return 0;

		int l = 0; // v[l] is always < x
		int r = size() - 1; // v[r] is always > x
		if (((Comparable) x).compareTo(get(0)) <= 0) return 0;
		if (((Comparable) x).compareTo(get(r)) > 0) return size();

		int m = r / 2;
		for (; r - l > 1;) {
			int c = ((Comparable) x).compareTo(get(m));
			if (c == 0) return m;
			if (c > 0) l = m;
			else r = m;
			m = (l + r) / 2;
		}
		return r;
	}

	public VectorI compareTo(T x) {
		VectorI v = new VectorI(size());
		for (int i = 0; i < size(); ++i) {
			v.set(i, ((Comparable) get(i)).compareTo(x));
		}
		return v;
	}

	public VectorI idxIn(Set<T> m) {
		VectorI v = new VectorI();
		for (int i = 0; i < size(); ++i) {
			if (m.contains(this.get(i))) v.add(i);
		}
		return v;
	}

	public VectorI idxOut(Set<T> m) {
		VectorI v = new VectorI();
		for (int i = 0; i < size(); ++i) {
			if (!m.contains(this.get(i))) v.add(i);
		}
		return v;
	}

	public VectorI maskIn(Set<T> m) {
		VectorI v = new VectorI();//this.size());//compareTo(x);
		for (T x : this) {
			if (m.contains(x)) v.add(1);
			else v.add(0);
		}
		return v;
	}

	public VectorI maskLargerThan(T x) {
		VectorI v = compareTo(x);
		return v.maskEqualToInt(1);
	}

	public VectorI maskEqualTo(T x) {
		VectorI v = compareTo(x);
		return v.maskEqualToInt(0);
	}

	public VectorI maskSmallerThan(T x) {
		VectorI v = compareTo(x);
		return v.maskEqualToInt(-1);
	}

	public VectorI idxEqualToInt(int x) {
		VectorI v = new VectorI();
		v.ensureCapacity(size());
		for (int i = 0; i < size(); ++i) {
			int c = ((Comparable) get(i)).compareTo(x);
			if (c == 0) v.add(i);
		}
		return v;
	}

	public VectorI idxNEqualToInt(int x) {
		VectorI v = new VectorI();
		v.ensureCapacity(size());
		for (int i = 0; i < size(); ++i) {
			int c = ((Comparable) get(i)).compareTo(x);
			if (c != 0) v.add(i);
		}
		return v;
	}

	/**
	 * transfrom {z,x,x,y,y,y} to {z->{0}, x->{1,2}, y->{3,4,5}
	 * 
	 */
	public MapXX<T, VectorI> idxEqualToX() {
		MapXX<T, VectorI> mv = new MapXX<T, VectorI>(c, VectorI.class);
		for (int i = 0; i < size(); ++i)
			mv.getC(get(i)).add(i);
		return mv;
	}

	public VectorI idxEqualTo(T x) {
		return compareTo(x).idxEqualToInt(0);
	}

	//	public VectorI idxByGet(String name, String value){	
	//		return getVString(name).idxEqualTo(value);	}

	public VectorI idxLargerThan(T x) {
		return compareTo(x).idxEqualToInt(1);
	}

	public VectorI idxSmallerThan(T x) {
		return compareTo(x).idxEqualToInt(-1);
	}

	public VectorI maskEqualToInt(Integer x) {
		VectorI v = new VectorI(size());
		for (int i = 0; i < size(); ++i) {
			if (get(i) == x) v.set(i, 1);
			//else		v.set(i,0);
		}
		return v;
	}

	public String joinIndexed(String cPair, String cInst) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			if (i > 0) sb.append(cInst);
			sb.append(i).append(cPair).append(FString.format(get(i)));
		}
		return (sb.toString());
	}

	public String joinIndexed() {
		/*StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			sb.append(String.format(
					"(%d) %.2f ",i, get(i)));
		}*/
		return joinIndexed("=", " ");
	}

	public String join(String c, EColorScheme cs) {
		int ib = 0;
		int ie = size();
		StringBuffer sb = new StringBuffer();
		for (int i = ib; i < ie; i++) {
			if (i > ib) sb.append(c);
			if (get(i) != null) sb.append(FString.format(get(i)));
		}
		return (sb.toString());
	}

	public String join(String sep, int ib, int ie) {//, boolean bCir
		ie = Math.min(ie, size());
		StringBuffer sb = new StringBuffer();
		for (int i = ib; i < ie; i++) {
			if (i > ib) if (sep != null) sb.append(sep);
			sb.append(FString.format(getCir(i)));
		}
		return (sb.toString());
	}

	public String joinEx(String sep, int iExcept) {//, boolean bCir
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size(); i++) {

			if (i == iExcept) continue;

			if (sep != null) if (sb.length() > 0) sb.append(sep);

			sb.append(FString.format(getCir(i)));
		}
		return (sb.toString());
	}

	public String joinPrefixed(String c, String pf) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			if (i > 0) sb.append(c);
			sb.append(pf);
			sb.append(FString.format(get(i)));
		}
		return (sb.toString());
	}

	public String join(String sep, int ib) {
		return join(sep, ib, size());
	}

	public String join(String sep) {
		return join(sep, 0);
	}

	public String join() {
		return join("\t");
	}

	public String joinIndented(String sIndent) {//int nIndent) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			sb.append(sIndent//FString.repeat(" ",nIndent)
					+ FString.format(get(i)) + "\n");
		}
		return (sb.toString());
	}

	public String toString() {
		return join("\t");	// TODO test this
	}

	public synchronized String toString(String c) {
		return join(c);
	}

	/**
	 * remove the elements e that 
	 * e.getString(key) is contained in ms 
	 * @param key
	 * @param ms
	 */
	public VectorX<T> removeByString(String key, Set<String> ms) {
		VectorX<T> v = newInstance();
		v.ensureCapacity(size());
		for (T x : this) {
			if (ms.contains(((IGetStrByStr) x).getString(key))) continue;
			v.add(x);
		}
		return v;
	}

	public VectorX<T> remove(Set<T> m) {
		return sub(idxOut(m));
	}

	public void removeByStringOn(String key, Set<String> ms) {
		VectorX<T> v = removeByString(key, ms);
		this.clear();
		this.addAll(v);
	}

	public VectorX<T> pushFrontOn(T x) {
		add(null);
		for (int i = size() - 1; i >= 1; --i) {
			set(i, get(i - 1));
		}
		set(0, x);
		return this;
	}

	public VectorX<T> pushBackOn(T x) {
		add(x);
		return this;
	}

	// every inefficient implementation
	public VectorX<T> insertSortedOn(T x) {
		//this.find(x);
		add(x);
		int i = size() - 2;
		for (; i >= 0; --i) {
			if (((Comparable) x).compareTo(get(i)) != -1) break;
			//if (get(i) <= x) break;
			set(i + 1, get(i));
		}
		set(i + 1, x);
		return this;
	}

	/**
	 * insert x to the j-th position
	 * keep at most n elements 
	 */
	public VectorX<T> insertTruncate(T x, int j, int n) {
		if (j > n) {
			System.err.print("insertTruncate() j>n");
			return this; //no need to insert
		}
		
		add(j, x);

		while (size() > n) {
			this.pop();
			//System.err.print("insertTruncate() exceed max length");
		}

		return this;
	}

	public T sum() {
		T x = this.newValue();
		for (T y : this) {
			if (y != null) ((IPlusObjOn) x).plusObjOn(y);
		}
		return x;
	}

	public T mean() {
		//V x
		if (size() == 0) return null;
		if (size() == 1) return get(0);
		//clone(0);//TODO should clone?

		T x = sum();
		return (T) ((IMultiplyOn) x).multiplyOn(1.0 / size());
	}

	/**
	 * find common prefix of two sequences, and return the length
	 * @param v
	 * @return 
	 */
	public int idxCommonPrefix(VectorI v) {
		int i = 0;
		for (; i < size() && i < v.size(); ++i) {
			if (!get(i).equals(v.get(i))) break;
		}
		return i;
	}

	/*	public V commonPrefix(VectorI v){
			return get(idxCommonPrefix(v));
		}*/
	public void updateIdx(VectorI vi) {//<Integer> vi){	
		for (T x : this)
			((IUpdateIdx) x).updateIdx(vi);
	}

	/** from a,a,t,b,b,b, to  {0,1}{2}{3,4,5}*/
	public VecVecI idxBreakByValue() {
		VecVecI vvi = new VecVecI();
		for (int i = 0; i < size(); ++i) {
			if (i == 0) vvi.add(new VectorI());
			//else if (!get(i).equals(get(i-1))) 
			else if (get(i) != (get(i - 1))) //to deal with null value 
			vvi.add(new VectorI());
			vvi.lastElement().add(i);
		}
		return vvi;
	}

	/**
	 * 0 is the right most one
	 * * @param i
	 * @return
	 */
	public T getRight(int i) {
		return get(size() - i - 1);
	}

	public String joinS(ArrayList<String> v, String cpair, String sep) {
		StringBuffer sb = new StringBuffer();
		int first = 1;
		for (int i = 0; i < size(); ++i) {
			if (first == 1) first = 0;
			else sb.append(sep);
			sb.append(FString.format(get(i)) + cpair + v.get(i));
		}
		return (sb.toString());
	}

	public String joinWith(ArrayList<T> v, String cpair, String c) {
		StringBuffer sb = new StringBuffer();
		int first = 1;
		for (int i = 0; i < size(); ++i) {
			if (first == 1) first = 0;
			else sb.append(c);
			sb.append(FString.format(get(i)) + cpair + FString.format(v.get(i)));
		}
		return (sb.toString());
	}

	public static String toString(VectorX<String> vk, VectorX<String> vx) {
		return vk.joinS(vx, "=", "\t");
	}

	public static String joinTabbedStrings(String s1, String s2) {
		return toString(FString.splitVS(s1, "\t"), FString.splitVS(s2, "\t"));
	}

	public T sample() {
		if (size() == 0) return null;
		return get(FRand.drawInt(size()));
	}

	public T sample(int b, int e) {
		if (size() == 0) return null;
		return get(FRand.drawInt(b, e));
	}

	public VectorX<T> LowVarSample(int num_samples) {
	//	if (num_samples>= size()) return this;
		
		VectorX<T> v = this.newInstance();
		v.ensureCapacity(num_samples);
		for (int i: new FRand.LowVarSampleSeq(size(), num_samples))	v.add(get(i));
		return v;
	}
	public VectorX<T> randomize() {
		for (int i = 0; i < size(); ++i)
			swap(i, FRand.rand.nextInt(size()));
		return this;
	}

	/**swap object i1 and i2*/
	public VectorX<T> swap(int i, int j) {
		if (i != j) {
			T x = get(i);
			set(i, get(j));
			set(j, x);
		}
		return this;
	}

	public boolean save(String fn) {
		return FFile.save(this, fn, "\n");
	}

	public void save(BufferedWriter writer) {
		for (T x: this) FFile.writeln(writer, x.toString());
	}
	public boolean save(String fn, String sep) {
		return FFile.save(this, fn, sep);
	}

	public boolean saveWithTitle(String fn, String title) {
		return FFile.save(this, fn, "\n", title);
	}

	public T parseLine(String line) {
		return null;
	}

	public boolean loadFile(String fn) {
		return loadFile(fn, false);
	}

	public boolean loadFile(String fn, boolean skip_title) {
		for (String line : FFile.enuLines(fn, skip_title))
			add(parseLine(line));
		return true;
	}

	public boolean loadFile(String fn, int iCol, String sep, boolean skip_title) {
		for (VectorS vs : FFile.enuRows(fn, sep, skip_title))
			add(parseLine(vs.get(iCol)));
		return true;
	}

	public VectorX<T> loadLine(String x, String sep) {
		this.clear();
		String vs[] = x.split(sep);
		this.ensureCapacity(vs.length);
		for (String s : vs)
			this.add(parseLine(s));
		return this;
	}

	public T[] toArray() {
		//Array.newInstance((c[]).class, size());
		T v[] = (T[]) new Object[size()];
		this.toArray(v);
		return v;
//		return (T[]) Arrays.copyOf(elementData, elementCount);
	}

	public void copy(ArrayList<T> v) {
		clear();
		addAll(v);
	}
	public int getNumNoneEmptyElements() {
		int num = 0;
		for (int size: this.getVI(CTag.size))	if (size>0) ++num;
		return num;
	}
}
