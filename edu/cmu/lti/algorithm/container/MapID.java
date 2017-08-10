/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import edu.cmu.lti.algorithm.math.rand.MultinomI;

/**
 * @author nlao
 *This class is an extension to TMapXD&lt;Integer&gt;
 */
public class MapID extends MapXD<Integer> {// implements IUpdateIdx{
	private static final long	serialVersionUID	= 2008042701L;	// YYYYMMDD

	public MapID newInstance() {
		return new MapID();
	}

	public void setAll(double d) {
		for (Map.Entry<Integer, Double> e : entrySet())
			e.setValue(d);
	}

	public SetI newSetKey() {
		return new SetI();
	}
	public MapID(Collection<Integer> vd, double value){
		super(Integer.class, vd, value);
	}
	// speedtup  version of replaceKey().join();
	public String joinReplaceKey(String cpair, String c, VectorS vKey) {
		StringBuffer sb = new StringBuffer();
		int first = 1;
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double v = e.getValue();
			if (first == 1) first = 0;
			else sb.append(c);
			sb.append(String.format("%s%s%.3f", vKey.get(k), cpair, v));///FString.format(v));			
		}
		return (sb.toString());
	}

	public MapSD replaceKey(VectorS vs) {
		MapSD m = new MapSD();
		for (Map.Entry<Integer, Double> e : entrySet())
			m.put(vs.get(e.getKey()), e.getValue());
		return m;
	}

	public Integer newKey() {//needed for primitive classes, silly java
		return 0;
	}

	public Double newValue() {//weakness of Java template
		return 0.0;
	}

	public VectorI newVectorKey() {
		return new VectorI();
	}

	public TMapDVecI newMVValueKey() {
		return new TMapDVecI();
	}

	public Integer parseKey(String k) {
		return Integer.parseInt(k);
	}

	public MapID() {
		//		super(Integer.class, Double.class);
		super(Integer.class);
	}


	public TMapDVecI toMMapValueKey() {
		TMapDVecI v = new TMapDVecI();
		v.fromMapValueKey(this);
		return v;
		//return new MMapDI(super.toMMapValueKey());
		//return (MMapDI)(super.toMMapValueKey());
	}

	public VectorI toVectorKey() {
		return (VectorI) super.toVectorKey();
	}

	public MapID replaceIdx(VectorI vi) {
		MapID m = newInstance();
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double x = e.getValue();
			m.put(vi.get(k), x);
		}
		if (m.containsKey(-1)) m.remove(-1);
		return m;
	}

	public MapID convolve(MapID m) {
		MapID rlt = newInstance();
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double x = e.getValue();
			for (Map.Entry<Integer, Double> e1 : m.entrySet()) {
				Integer k1 = e1.getKey();
				Double x1 = e1.getValue();
				rlt.plusOn(k1 + k, x * x1);
			}
		}
		return rlt;
	}

	public MapID subSet(Set<Integer> v) {
		return (MapID) super.subSet(v);
	}

	public MapID sub(Collection<Integer> v) {
		return (MapID) super.sub(v);
	}

	public VectorD subVector(ArrayList<Integer> vi) {
		return (VectorD) super.subVector(vi);
	}

	public VecMapID outer(ArrayList<Double> v) {
		VecMapID vm = new VecMapID();
		vm.ensureCapacity(v.size());
		for (Double x : v) {
			if (x == 0.0) vm.add(new MapID());
			else vm.add(this.multiply(x));
		}
		return vm;
	}

	public Double dotProd(ArrayList<Double> m) {
		double d = 0;
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double v = e.getValue();
			d += v * m.get(k);
		}
		return d;
	}

	public Double inner(double[] m) {
		double d = 0;
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double v = e.getValue();
			d += v * m[k];
		}
		return d;
	}

	public MapID multiply(ArrayList<Double> vec) {
		MapID m2 = newInstance();
		if (vec == null) return m2;

		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double v = e.getValue();
			if (vec.get(k) == 0) continue;
			m2.put(k, v * vec.get(k));
		}
		return m2;
	}

	public MapID plus(MapID m) {
		return (MapID) super.plus(m);
	}

	public MapID minus(MapID m) {
		return (MapID) super.minus(m);
	}

	public MapID multiply(Double x) {
		return (MapID) super.multiply(x);
	}

	public MapID multiplyOn(Double x) {
		return (MapID) super.multiplyOn(x);
	}

	public MapID multiplyOn(int x) {
		return multiplyOn((double) x);
	}

	public MapID devideOn(Double x) {
		return (MapID) super.devideOn(x);
	}

	public MapID devide(Double x) {
		return (MapID) super.devide(x);
	}

	public VectorD toVector(int len) {
		//return new VectorD(super.toVector(len));
		VectorD v = new VectorD(len, 0.0);//newVectorValue();		v.reset(len);
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double x = e.getValue();
			v.set(k, x);
		}
		return v;
	}

	public void keepLargest() {
		if (size() <= 1) return;
		int idHigh = idxMax();
		double maxHigh = get(idHigh);
		clear();
		put(idHigh, maxHigh);
	}

	public void keepLargest2() {
		if (size() <= 2) return;
		int id1st = -1;
		int id2nd = -1;
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double x = e.getValue();
			if (id2nd == -1) id2nd = k;
			else {
				if (get(id2nd) >= x) continue;
				id2nd = k;
			}

			if (id1st == -1) {
				id1st = id2nd;
				id2nd = -1;
			} else {
				if (get(id1st) >= get(id2nd)) continue;
				k = id1st;
				id1st = id2nd;
				id2nd = k;
			}
		}

		double d1st = get(id1st);
		double d2nd = get(id2nd);
		clear();
		put(id1st, d1st);
		put(id2nd, d2nd);
	}

	/*	public int sampleKey(){
			MultinomialI rv = new MultinomialI(this);		
			return rv.draw();		
		}*/
	public MultinomI getRV() {
		return new MultinomI(this);
	}

	public void addTo(double[] v, double scale) {
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double d = e.getValue();
			v[k] += d * scale;
		}
		return;
	}

	public MapID plusOn(final MapID m, VectorI vidx, double scale) {
		if (m == null) return this;
		for (Map.Entry<Integer, Double> e : m.entrySet()) {
			Integer k = vidx.get(e.getKey());
			double v = e.getValue();
			plusOn(k, v * scale);
		}
		return this;
	}
	
	public MapID plusOn(final MapID m) {
		return (MapID) super.plusOn(m);
	}

	public void disterbByID(double scale) {
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double v = e.getValue();
			e.setValue(v + k * scale);
		}
		return;
	}

	public TMapDVecI ValueKeyToMapVec() {
		return (TMapDVecI) super.ValueKeyToMapVec();
	}

	public MapID subPositive() {
		return (MapID) super.subPositive();
	}

	public MapID subLargerThan(double th) {
		return (MapID) super.subLargerThan(th);
	}

	public static MapID fromFile(String fn) {
		MapID m = new MapID();
		m.loadFile(fn);
		return m;
	}

	public static MapID fromLine(String line) {
		return fromLine(line, "=", " ");
	}

	public static MapID fromLine(String line, String cSep, String c) {
		MapID m = new MapID();
		m.loadLine(line, cSep, c);
		return m;
	}
	
	public VectorI KeyToVecSortByValue(boolean descending) {
		return (VectorI) super.KeyToVecSortByValue(descending);
	}
	
	public MapID multiply(MapID vec) {
		return (MapID) super.multiply(vec);
	}
	
	// assume that -1 corresponds to the bias value
	public double getBiasedValue(int  key) {
		Double bias = get(-1);
		Double value = get(key);
		if (bias == null){
			if (value == null) return 0.0;
			else return value;
		}
		else {
			if (value == null) return bias;
			return value + bias;
		}
	}
}
