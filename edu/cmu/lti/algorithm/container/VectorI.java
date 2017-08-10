/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.text.FString;

/**
 * @author nlao
 *This class is an extension to Vector&lt;Integer&gt;
 */
public class VectorI extends VectorX<Integer> implements Serializable,
	ICloneable, IPlusObjOn, Comparable<VectorI> {
	
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public Integer newValue() {//weakness of Java template
		return 0;
	}

	public VectorI newInstance() {
		return new VectorI();
	}

	public MapIIa newMapValueId() {
		return new MapIIa();
	}

	public SetI newSetValue() {
		return new SetI();
	}

	public VectorI plusObjOn(Object m) {
		addAll((VectorI) m);
		return this;
	}

	public VectorI(Integer[] v) {
		super(v);
	}

	public VectorI(VectorX<Integer> v) {
		super(v, Integer.class);
	}

	public VectorI(int k) {
		super(k, 0);//, Integer.class);
	}

	public VectorI(int k, int v) {
		super(k, v);
	}

	public VectorI() {
		super(Integer.class);
	}

	// assume the current vector contain a sorted index
	//assume @this contains a permuation of 0...size()-1 
	public VectorI reversIdx() {
		return reversIdx(size());
	}

	//transform this=[1,2, 4] to v1=[-1,0,1,-1,2, ...] 
	//with v1.size()=n
	//assuming n>= this.size() 
	public VectorI reversIdx(int n) {
		VectorI vi = new VectorI(n, -1);
		for (int i = 0; i < size(); ++i)
			vi.set(get(i), i);
		return vi;
	}

	public static VectorI seq(int b, int n, int inc) {
		VectorI v = new VectorI(n);
		for (int i = 0; i < n; ++i) {
			//v.add(b);
			v.set(i, b);
			b = b + inc;
		}
		return v;
	}

	public static VectorI seq(int b, int n) {
		return seq(b, n, 1);
	}

	public static VectorI zeros(int n) {
		return new VectorI(n, 0);
	}

	public static VectorI ones(int n) {
		return new VectorI(n, 1);
	}

	public static VectorI seq(int n) {
		return seq(0, n, 1);
	}

	public Integer and() {
		int a = 1;
		for (int i = 0; i < size(); ++i) {
			if (get(i) == null) {
				a = 0;
				break;
			}
			if (get(i) == 0) {
				a = 0;
				break;
			}
		}
		return a;
	}

	//assuming ascending order
	public VectorI intersect(ArrayList<Integer> vi) {
		VectorI v = new VectorI();
		for (int i = 0, j = 0; i < size() && j < vi.size();) {
			if (get(i) == vi.get(j)) {
				v.add(get(i));
				++i;
				++j;
				continue;
			}
			if (this.get(i) > vi.get(j)) ++j;
			else ++i;
		}
		return v;
	}

	public VectorI and(ArrayList<Integer> vi) {
		VectorI v = new VectorI();
		for (int i = 0; i < size() && i < vi.size(); ++i) {
			if (vi.get(i) == 0) v.add(0);
			else v.add(get(i));
		}
		return v;
	}

	public VectorI merge(ArrayList<Integer> vi) {
		VectorI v = new VectorI();
		for (int i = 0, j = 0; i < size() && j < vi.size();) {
			if (this.get(i) == vi.get(j)) {
				v.add(get(i));
				++i;
				++j;
				continue;
			}
			if (get(i) > vi.get(j)) {
				v.add(vi.get(j));
				++j;
			} else {
				v.add(get(i));
				++i;
			}
		}
		return v;
	}

	public VectorI sub(ArrayList<Integer> vi) {
		return (VectorI) super.sub(vi);
	}

	public SetI subSet(Set<Integer> vi) {
		return (SetI) super.subSet(vi);
	}

	public VectorI sub(Set<Integer> vi) {
		return (VectorI) super.sub(vi);
	}

	/*//
	public VectorI toIdx() {
		VectorI v=newInstance();
		
		//v.ensureCapacity(size());
		v.reset(size(),-1);
		for (int i=0; i<size(); ++i){
			if (get(i)>0) 
				v.set(get(i),i);
		}
		return v;
	}		*/
	/**
	 * assume accent order
	 * @param x
	 * @return output
	 */
	/*	public int findSorted(int x){
		return super.findSorted(x);
		int find(Tv u, int l=0, int r=-1)	{
			if (r=-1) r= vector<Tv>::size()-1;
			if ((*this)[l]==u) return l;
			if ((*this)[r]==u) return r;
			int m=r/2;
			for (;r-l>1;)		{
				if ((*this)[m]==u) return m;
				if ((*this)[m]<u) 	l=m; else	r=m; 
				m=(l+r)/2;
			}
			return -1;
		}
	}	*/

	//assume asc sorted vector
	/*	public VectorI insertSortedOn(int x) {
			//this.find(x);
			add(x);
			int i=size()-2;
			for (; i>=0; --i){
				if (get(i) <= x) break;
				set(i+1,get(i));
			}
			set(i+1,x);
			return this;
		}		*/
	public VectorI shrinkRepeated() {
		return new VectorI(super.shrinkRepeated());
	}

	public MapXX<Integer, Object> sub(MapXX<Integer, Object> m) {
		MapXX<Integer, Object> m1 = m.newInstance();
		for (Map.Entry<Integer, Object> e : m.entrySet()) {
			Integer k = e.getKey();
			Object x = e.getValue();
			m1.put(get(k), x);
		}
		return m1;
	}

	public Integer sum() {
		Integer s = 0;
		for (Integer d : this)
			s += d;
		return s;
	}

	public VectorI cumu() {
		VectorI vi = new VectorI();
		vi.ensureCapacity(size());
		int c = 0;
		for (int i : this) {
			c += i;
			vi.add(c);
		}
		return vi;
	}

	public VectorI plusOn(int i, int x) {
		set(i, get(i) + x);
		return this;
	}

	public VectorI plusOnE(int i, int x) {
		extend(i + 1);
		set(i, get(i) + x);
		return this;
	}

	public VectorI plusOn(VectorX<Integer> vi) {
		for (int i = 0; i < size(); ++i)
			set(i, vi.get(i) + get(i));
		return this;
	}

	public VectorI plusAllOn(int x) {
		for (int i = 0; i < size(); ++i)
			set(i, get(i) + x);
		return this;
	}
	public VectorI plusOn(int i) {
		return plusOn(i, 1);
	}
	
	public VectorD toVectorD() {
		VectorD v = new VectorD();
		v.ensureCapacity(size());
		for (int i = 0; i < size(); ++i)
			v.add((double) get(i));
		return v;
	}

	public VectorI multiplyOn(double x) {
		for (int i = 0; i < size(); ++i)
			set(i, (int) (get(i) * x));
		return this;
	}

	public VectorI multiply(double x) {
		VectorI v = new VectorI();
		v.ensureCapacity(size());
		for (int i = 0; i < size(); ++i)
			v.add((int) (get(i) * x));
		return v;
	}

	public static VectorI parse(String x, String sep) {
		return from(FString.toVS(x, sep));
	}

	public static VectorI from(VectorS vs) {
		VectorI vi = new VectorI();
		vi.ensureCapacity(vs.size());
		for (String s : vs)
			vi.add(Integer.parseInt(s));
		return vi;
	}

	public static String join(int[] v, String c) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.length; i++) {
			if (i > 0) sb.append(c);
			sb.append(v[i]);
		}
		return (sb.toString());
	}

	public boolean loadSet(String fn, int estimatedCell) {
		ensureCapacity(estimatedCell);

		//System.out.println("loading dataset from text file"+fn);
		BufferedReader br = FFile.newReader(fn);
		if (br == null) return false;
		String line = null;
		while ((line = FFile.readLine(br)) != null) {
			int i = Integer.parseInt(line);
			this.setE(i, 1);
		}
		return true;
	}

	public VectorI left(int n) {
		return (VectorI) super.left(n);
	}

	public Integer parseLine(String line) {
		return Integer.parseInt(line);
	}

	public SetI toSet() {
		return (SetI) super.toSet();
	}

	public VectorI except(int i) {
		return (VectorI) super.except(i);
	}

	public VectorI sub(int b, int e) {
		return (VectorI) super.sub(b, e);
	}

	public int[] toIntArray() {
		int v[] = new int[size()];
		for (int i = 0; i < size(); ++i)
			v[i] = get(i);
		return v;
	}

	public MapIIa toMapValueId() {
		return (MapIIa) super.toMapValueId();
	}
	public VectorI clone() {
		return new VectorI(this);
	}
//	public int hashCode() {
//		return this.sum();
//	}
//	
//	//Used to compare arrays for content values.
//	public boolean equals(Object obj) {
//		// Always expect arrayWrapper
//		if (obj instanceof VectorI == false) return false;
//		VectorI vec = (VectorI) obj;
//		if (this.size() != vec.size()) return false;
//		return compareTo(vec) ==0;
//	}


	public int compareTo(VectorI c) {
		int len = Math.min(size(), c.size());

		for (int i = 0; i < len; ++i) {
			int cmp = get(i).compareTo(c.get(i));
			if (cmp != 0) return cmp;
		}
		if (size() < c.size()) return -1;
		else if (size() > c.size()) return 1;
		return 0;
	}

	public static void main(String[] args) {
		// test hashing
		MapXI<VectorI> map = new MapXI<VectorI>(VectorI.class);
		VectorI vec = new VectorI();
		
		for (int i = 1; i <= 5; ++i) {
			map.plusOn(vec);//.clone());
			vec.add(i);
		}
		
		for (int i = 1; i <= 3; ++i) {
			vec.pop();
			map.plusOn(vec);
		}
		System.out.println(map.join("=", "\n"));
		return;
	}
	public VectorI sub(int b) {
		return (VectorI) super.sub(b);
	}
}
