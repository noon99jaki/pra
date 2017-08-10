package edu.cmu.lti.algorithm.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class TMapID extends TMapXD<Integer> {// implements IUpdateIdx{
	private static final long	serialVersionUID	= 2008042701L;	// YYYYMMDD

	public TMapID newInstance() {
		return new TMapID();
	}

	public void setAll(double d) {
		for (Map.Entry<Integer, Double> e : entrySet())
			e.setValue(d);
	}

	public SetI newSetKey() {
		return new SetI();
	}
	
	public TMapID(Collection<Integer> vd, double value){
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

	public TMapID() {
		//		super(Integer.class, Double.class);
		super(Integer.class);
	}

	public VectorI toVectorKey() {
		return (VectorI) super.toVectorKey();
	}

	public TMapID replaceIdx(VectorI vi) {
		TMapID m = newInstance();
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double x = e.getValue();
			m.put(vi.get(k), x);
		}
		if (m.containsKey(-1)) m.remove(-1);
		return m;
	}

	public TMapID convolve(TMapID m) {
		TMapID rlt = newInstance();
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

	public TMapID subSet(Set<Integer> v) {
		return (TMapID) super.subSet(v);
	}

	public TMapID sub(Collection<Integer> v) {
		return (TMapID) super.sub(v);
	}

	public VectorD subVector(ArrayList<Integer> vi) {
		return (VectorD) super.subVector(vi);
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

	public TMapID multiply(ArrayList<Double> vec) {
		TMapID m2 = newInstance();
		if (vec == null) return m2;

		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double v = e.getValue();
			if (vec.get(k) == 0) continue;
			m2.put(k, v * vec.get(k));
		}
		return m2;
	}

	public TMapID plus(TMapID m) {
		return (TMapID) super.plus(m);
	}

	public TMapID minus(TMapID m) {
		return (TMapID) super.minus(m);
	}

	public TMapID multiply(Double x) {
		return (TMapID) super.multiply(x);
	}

	public TMapID multiplyOn(Double x) {
		return (TMapID) super.multiplyOn(x);
	}

	public TMapID multiplyOn(int x) {
		return multiplyOn((double) x);
	}

	public TMapID devideOn(Double x) {
		return (TMapID) super.devideOn(x);
	}

	public TMapID devide(Double x) {
		return (TMapID) super.devide(x);
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

//	public MapVecDI mvLargestN(int n) {
//		MapVecDI mv = new MapVecDI();
//		for (Map.Entry<Integer, Double> e : entrySet()) {
//			Integer k = e.getKey();
//			Double d = e.getValue();
//			mv.getC(d).add(k);
//			if (mv.size() > n) mv.remove(mv.firstKey());
//		}
//		return mv;
//	}
//
//	//index to the top N largest values
//	public VectorI vLargestN(int n) {
//		return (VectorI) mvLargestN(n).toVectorV();
//	}
//
//	//N-th largest unique value
//	public Double valueULargestN(int n) {
//		MapVecDI mv = mvLargestN(n);
//		return mv.firstKey();
//	}
//
//	//index of the N-th largest value
//	public Integer idxLargestN(int n) {
//		//MapVectorDI mv= mvLargestN(n);
//		VectorI vi = vLargestN(n);
//		return vi.getRight(n - 1);
//	}
//
//	//the N-th largest value
//	public Double valueLargestN(int n) {
//		return get(idxLargestN(n));
//	}

	public void addTo(double[] v, double scale) {
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double d = e.getValue();
			v[k] += d * scale;
		}
		return;
	}

	/**
	 * plus on with id translations 
	 * @param m
	 * @param vidx	dictionary
	 * @param scale
	 * @return
	 */
	public TMapID plusOn(TMapID m, VectorI vidx, double scale) {
		if (m == null) return this;
		for (Map.Entry<Integer, Double> e : m.entrySet()) {
			Integer k = vidx.get(e.getKey());
			double v = e.getValue();
			plusOn(k, v * scale);
		}
		return this;
	}
	
	public TMapID plusOn(TMapID m) {
		return (TMapID) super.plusOn(m);
	}

	public void disterbByID(double scale) {
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double v = e.getValue();
			e.setValue(v + k * scale);
		}
		return;
	}

	public TMapID subPositive() {
		return (TMapID) super.subPositive();
	}

	public TMapID subLargerThan(double th) {
		return (TMapID) super.subLargerThan(th);
	}

	public static TMapID fromFile(String fn) {
		TMapID m = new TMapID();
		m.loadFile(fn);
		return m;
	}

	public static TMapID fromLine(String line) {
		return fromLine(line, "=", " ");
	}

	public static TMapID fromLine(String line, String cSep, String c) {
		TMapID m = new TMapID();
		m.loadLine(line, cSep, c);
		return m;
	}
	
	public VectorI KeyToVecSortByValue(boolean descending) {
		return (VectorI) super.KeyToVecSortByValue(descending);
	}
	
	public TMapID multiply(TMapID vec) {
		return (TMapID) super.multiply(vec);
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
	

	public TMapDVecI mvLargestN(int n) {
		TMapDVecI mv = new TMapDVecI();
		for (Map.Entry<Integer, Double> e : entrySet()) {
			Integer k = e.getKey();
			Double d = e.getValue();
			mv.getC(d).add(k);
			if (mv.size() > n) mv.remove(mv.firstKey());
		}
		return mv;
	}

	//index to the top N largest values
	public VectorI vLargestN(int n) {
		return (VectorI) mvLargestN(n).toVectorV();
	}

	//N-th largest unique value
	public Double valueULargestN(int n) {
		TMapDVecI mv = mvLargestN(n);
		return mv.firstKey();
	}

	//index of the N-th largest value
	public Integer idxLargestN(int n) {
		//MapVectorDI mv= mvLargestN(n);
		VectorI vi = vLargestN(n);
		return vi.getRight(n - 1);
	}

	//the N-th largest value
	public Double valueLargestN(int n) {
		return get(idxLargestN(n));
	}
}