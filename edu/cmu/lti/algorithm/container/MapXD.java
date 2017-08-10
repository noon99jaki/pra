/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import edu.cmu.lti.algorithm.Interfaces.IGetDblByStr;
import edu.cmu.lti.algorithm.Interfaces.IMinusObjOn;
import edu.cmu.lti.algorithm.Interfaces.IMultiply;
import edu.cmu.lti.algorithm.Interfaces.IMultiplyOn;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

/**
 * @author nlao
 * This class is an extension to TMap&lt;K, Double&gt;
 */
public class MapXD<K> extends MapXX<K, Double> implements IPlusObjOn,
		IMinusObjOn, IGetDblByStr//, IWrite, IRead 
		, Serializable, Cloneable, IMultiply, IMultiplyOn {//, ICopyable
	private static final long	serialVersionUID	= 2008042701L;	// YYYYMMDD

	public MapXD<K> removeZeros() {
		MapXD<K> m = this.newInstance();
		for (Map.Entry<K, Double> e : this.entrySet())
			if (e.getValue() != 0) m.put(e.getKey(), e.getValue());
		return m;
	}

	public Double getDouble(String name) {
		if (name.equals("sum")) return this.sum();
		return null;
	}

	public MapXD<K> newInstance() {
		return new MapXD<K>(ck);
	}

	public MapXD(Class ck, Collection<K> keys, Double value) {
		super(ck, Double.class, keys, value);
	}

	public Double newValue() {//weakness of Java template
		return 0.0;
	}

	public VectorD newVectorValue() {
		return new VectorD();
	}

	public Double parseValue(String v) {
		return Double.parseDouble(v);
	}

	public MapXD(Class ck) {
		super(ck, Double.class);
	}

	public MapXD(MapXX<K, Double> m) {
		super(m);
	}

	public VectorD ValuesToVector() {
		return (VectorD) super.ValuesToVector();
	}

	public Double sum() {
		Double sum = 0.0;
		for (Map.Entry<K, Double> e : entrySet()) {
			// Integer k = e.getKey();
			Double v = e.getValue();
			sum = sum + v;
		}
		return sum;
	}

	public Double mean() {
		return sum() / size();
	}

	public MapXD<K> absOn() {
		for (Map.Entry<K, Double> e : entrySet()) {
			K k = e.getKey();
			Double v = e.getValue();
			put(k, Math.abs(v));
		}
		return this;
	}

	public MapXD<K> abs() {
		MapXD<K> m = newInstance();
		for (Map.Entry<K, Double> e : entrySet()) {
			K k = e.getKey();
			Double v = e.getValue();
			m.put(k, Math.abs(v));
		}
		return m;
	}

	/*
		public Double inner(Map<K, Double> m) {
			//TMapXD<K> m1 = multiply(m);
			return multiply(m).sum();
		}		*/
	public MapXD<K> multiply(Map<K, Double> m) {//kronecker product
		MapXD<K> m2 = newInstance();
		if (m == null) return m2;
		for (Map.Entry<K, Double> e : entrySet()) {
			K k = e.getKey();
			Double v = e.getValue();
			if (!m.containsKey(k)) continue;
			m2.put(k, v * m.get(k));
		}
		return m2;
	}

	public MapXD<K> devide(Map<K, Double> m) {//kronecker product
		MapXD<K> m2 = newInstance();
		if (m == null) return m2;
		for (Map.Entry<K, Double> e : entrySet()) {
			K k = e.getKey();
			Double v = e.getValue();
			if (!m.containsKey(k)) {
				System.err.println("devided by zero");
				continue;
			}
			m2.put(k, v / m.get(k));
		}
		return m2;
	}

	//
	public MapXD<K> min(Map<K, Double> m) {
		MapXD<K> m2 = newInstance();
		if (m == null) return m2;
		for (Map.Entry<K, Double> e : entrySet()) {
			K k = e.getKey();
			Double v = e.getValue();
			if (!m.containsKey(k)) continue;
			m2.put(k, Math.min(v, m.get(k)));
		}
		return m2;
	}

	//Double
	public MapXD<K> plusOn(K k, Double x) {//TMapXD<K>
		if (x!=0.0) {
			Double d = get(k);
			if (d != null) put(k, d + x);
			else put(k, x);
		}
		return this;
	}

	public Double plusOnGet(K k, Double x) {//TMapXD<K>
		Double d = get(k);
		if (d == null) {
			put(k, x);
			return x;
		}
		d += x;
		put(k, d);
		return d;
	}

	public MapXD<K> plusOn(K k) {
		return plusOn(k, 1.0);
	}

	/*public TMapXD<K>  plusOn(Double x){
		for ( Map.Entry<K, Double> e : entrySet() ) 
			e.setValue(e.getValue()+x);		
		return this;
	}	*/
	public MapXD<K> add(K k) {
		put(k, 1.0);
		return this;
	}

	public MapXD<K> minusOn(K k, double x) {
		if (this.containsKey(k)) {
			put(k, get(k) - x);
		} else put(k, -x);
		return this;
	}

	//assume x and get(k) be positive
	//return min(get(k), x);
	//minus x from get(k)
	public double truncateOn(K k, double x) {
		Double d = get(k);
		if (d == null) return 0;
		if (d > x) {
			put(k, d - x);
			return x;
		}
		remove(k);
		if (d <= 0) return 0.0;
		return d;
	}

	public MapXD<K> truncateMaxOn(double max) {
		for (Map.Entry<K, Double> e : entrySet()) {
			double d = e.getValue();
			if (d > max) e.setValue(max);
		}
		return this;
	}

	public MapXD<K> minusOn(double x) {
		for (Map.Entry<K, Double> e : entrySet())
			e.setValue(e.getValue() - x);
		return this;
	}

	public MapXD<K> minus(double x) {
		MapXD<K> m = (MapXD<K>) this.clone();
		return m.minusOn(x);
	}

	public MapXD<K> normalize() {
		MapXD<K> m = newInstance();
		Double sum = sum();
		for (Map.Entry<K, Double> e : entrySet())
			m.put(e.getKey(), e.getValue() / sum);
		return m;
	}

	public MapXD<K> normalizeOn() {
		Double sum = sum();
		if (sum == 0.0) return this;
		return devideOn(sum);
	}

	public MapXD<K> normalizeL1On() {
		Double sum = L1Norm();
		//if (sum==0) return this;
		return devideOn(sum);
	}

	public MapXD<K> normalizeL2On() {
		double d = L2Norm();
		return devideOn(d);
	}

	public double L2Norm() {
		return Math.sqrt(mod2());
	}

	public double L1Norm() {
		double sum = 0;
		for (Map.Entry<K, Double> e : entrySet())
			sum += Math.abs(e.getValue());
		return sum;
	}

	/*	public TMapXD<K> normalize() {
			TMapXD<K> m = newInstance();
			m.from(this);
			//(TMapXD<K>) this.clone();
			return m.normalizeOn();
		}*/
	public MapXD<K> plusOn(MapXD<K> m) {
		if (m == null) return this;
		for (Map.Entry<K, Double> e : m.entrySet()) {
			K k = e.getKey();
			double v = e.getValue();
			plusOn(k, v);
		}
		return this;
	}

	public MapXD<K> plusOn(MapXD<K> m, double scale) {
		if (m == null) return this;
		for (Map.Entry<K, Double> e : m.entrySet()) {
			K k = e.getKey();
			double v = e.getValue();
			plusOn(k, v * scale);
		}
		return this;
	}

	/*public TMapXD<K>  shrinkZeroOn(TMapXD<K> m){
		for ( Map.Entry<K, Double> e : m.entrySet() ) {
			K k = e.getKey();
			double v = e.getValue();
			if (v==0.0) 
				remove(k);
		}	
		return this;
	}*/
	public MapXD<K> shrinkTowardsZero(double L1) {
		if (L1 <= 0) {
			System.err.print("L1 <0");
			return null;
		}
		MapXD<K> m = this.newInstance();
		for (Map.Entry<K, Double> e : entrySet()) {
			K k = e.getKey();
			double v = e.getValue();
			if (v > L1) m.put(k, v - L1);
			else if (v < -L1) m.put(k, v + L1);
		}
		return m;
	}

	public MapXD<K> shrinkTowardsZeroOn(double L1) {
		if (L1 == 0.0) return this;
		MapXD<K> m = shrinkTowardsZero(L1);
		this.clear();
		if (m != null) putAll(m);
		return this;
	}

	public MapXD<K> shrinkTowardsZeroScale(double L1, double scale) {
		MapXD<K> m = this.newInstance();
		if (L1 <= 0) return m;
		for (Map.Entry<K, Double> e : entrySet()) {
			K k = e.getKey();
			double v = e.getValue();
			if (v > L1) m.put(k, (v - L1) * scale);
			else if (v < -L1) m.put(k, (v + L1) * scale);
		}
		return m;
	}

	public MapXD<K> shrinkTowardsZeroScaleOn(double L1, double scale) {
		MapXD<K> m = shrinkTowardsZeroScale(L1, scale);
		this.clear();
		this.putAll(m);
		return this;
	}

	public MapXD<K> plusObjOn(Object m) {
		return plusOn((MapXD<K>) m);
	}

	public MapXD<K> minusObjOn(Object m) {
		return minusOn((MapXD<K>) m);
	}

	public MapXD<K> plusOn(VectorX<K> vk) {
		plusOn(vk, 1.0);
		return this;
	}

	public MapXD<K> plusOn(Collection<K> v) {
		plusOn(v, 1.0);
		return this;
	}

	public MapXD<K> minusOn(VectorX<K> vk) {
		minusOn(vk, 1.0);
		return this;
	}

	/*public TMapXD<K> plusOn( TVector<K> vk, double d){
		for (K k: vk)
			plusOn(k, d);
		return this;
	}	*/
	public MapXD<K> plusOn(Collection<K> v, double d) {
		for (K k : v)
			plusOn(k, d);
		return this;
	}

	/**
	 * plus on a subsequence. useful in efficient manipulation 
	 */
	public MapXD<K> plusOn(ArrayList<K> v, int b, int e, double d) {
		for (int i = b; i < e; ++i)
			plusOn(v.get(i), d);
		return this;
	}
	
	public MapXD<K> plusOn(ArrayList<K> keys, ArrayList<Double> values) {
		FSystem.checkVectorSizes(keys.size(),  values.size());
		for (int i = 0; i < keys.size(); ++i) {
			if (values.get(i) != 0)		plusOn(keys.get(i), values.get(i));
		}
		return this;
	}

	public MapXD<K> set(Collection<K> v, double d) {
		for (K k : v)
			this.put(k, d);
		return this;
	}

	public MapXD<K> minusOn(VectorX<K> vk, double d) {
		for (K k : vk)
			minusOn(k, d);
		return this;
	}

	public MapXD<K> minusOn(MapXD<K> m) {
		if (m == null) return this;
		for (Map.Entry<K, Double> e : m.entrySet()) {
			K k = e.getKey();
			double v = e.getValue();
			minusOn(k, v);
		}
		return this;
	}

	public MapXD<K> plus(MapXD<K> m) {
		MapXD<K> m1 = (MapXD<K>) clone();
		return m1.plusOn(m);
	}

	public MapXD<K> minus(MapXD<K> m) {
		MapXD<K> m1 = (MapXD<K>) clone();
		return m1.minusOn(m);
	}

	public MapXD<K> multiply(Double x) {
		MapXD<K> m = (MapXD<K>) clone();
		return m.multiplyOn(x);
	}

	public MapXD<K> multiplyOn(Double x) {
		for (Map.Entry<K, Double> e : entrySet())
			e.setValue(e.getValue() * x);
		return this;
	}

	public MapXD<K> devide(Double x) {
		MapXD<K> m = (MapXD<K>) clone();
		return m.devideOn(x);
	}

	public MapXD<K> devideOn(Double x) {
		for (Map.Entry<K, Double> e : entrySet())
			e.setValue(e.getValue() / x);
		return this;
	}

	double mod2() {
		double m2 = 0.0;
		for (double d : values())
			m2 += d * d;
		return m2;
	}

	public MapXD<K> from(ArrayList<K> vk) {
		for (int i = 0; i < vk.size(); ++i) {
			plusOn(vk.get(i), 1.0);
		}
		return this;
	}

	public MapXD<K> subTop(double percentile) {
		//TMapXD<K> m = newInstance();
		return (MapXD<K>) sub(idxTopPercent(percentile));
	}

	public MapXD<K> subPositive() {
		return (MapXD<K>) sub(idxLargerThan(0.0));
	}

	public MapXD<K> subNegative() {
		return (MapXD<K>) sub(idxSmallerThan(0.0));
	}

	public VectorD toVectorAbsValue() {
		VectorD v = newVectorValue();
		v.ensureCapacity(size());
		for (Map.Entry<K, Double> e : entrySet()) {
			//K k = e.getKey();			
			v.add(Math.abs(e.getValue()));
		}
		return v;
	}

	public VectorD toVectorValue() {
		return (VectorD) super.toVectorValue();
	}

	//public TVector<K> toVectorKeySortedByValue(){
	//	return toVectorKey().sub(toVectorValue().sortId());	}
	public MapXD<K> topAbsN(int n) {
		if (n >= this.size()) return this;
		VectorI vidx = toVectorAbsValue().sortId();
		VectorX<K> vi = toVectorKey().sub(vidx.reverseOn().left(n));
		return (MapXD<K>) sub(vi);
	}

	public MapXD<K> subAbsLargerThan(Double x) {
		return (MapXD<K>) sub(idxAbsLargerThan(x));
	}

	public VectorX<K> idxAbsLargerThan(Double x) {
		VectorX<K> v = this.newVectorKey();
		for (Map.Entry<K, Double> e : entrySet())
			if (Math.abs(e.getValue()) > x) v.add(e.getKey());
		return v;
	}

	public MapXD<K> negat() {
		MapXD<K> m = this.newInstance();
		for (Map.Entry<K, Double> e : entrySet())
			m.put(e.getKey(), -e.getValue());
		return m;
	}

	//	public TVector<K> idxTopExp(double percentile){		exp	}
	public VectorX<K> idxTopPercent(double percentile) {
		VectorX<K> v = newVectorKey();

		TMapXVecX<Double, K> mv = ValueKeyToMapVec();
		VectorD vx = new VectorD(mv.toVectorK());
		VectorX<K> vk = mv.toVectorV();
		double th = vx.sum() * percentile;
		double cumu = 0;
		for (int i = vk.size() - 1; i >= 0 && cumu < th; --i) {
			cumu += vx.get(i);
			v.add(vk.get(i));
		}
		return v;
	}

	public K idxAbsMax() {
		if (size() == 0) return null;
		double x = -1;
		K i = null;
		for (Map.Entry<K, Double> e : entrySet()) {
			K k = e.getKey();
			double v = Math.abs(e.getValue());
			if (v <= x) continue;
			x = v;
			i = k;
		}
		return i;
	}
	

	public K idxMax() {
		if (size() == 0) return null;
		Double x = null;
		K i = null;
		for (Map.Entry<K, Double> e : entrySet()) {
			K k = e.getKey();
			double v = e.getValue();
			if (x != null)	if (v <= x) continue;
			x = v;
			i = k;
		}
		return i;
	}

	public boolean saveFormat(String format, String fn) {
		//System.out.println("saving dataset to text file "+fn);
		//"%s\t%.3e\n"
		BufferedWriter bw = FFile.newWriter(fn);
		for (Map.Entry<K, Double> e : entrySet()) {
			K k = e.getKey();
			Double v = e.getValue();
			FFile.writeln(bw, String.format(format, k, v));
		}
		FFile.flush(bw);
		FFile.close(bw);
		return true;
	}

	public String toStringE(String fmt) {
		//String oldFmt= FString.fmtDouble;
		return joinF("=", ",", fmt);
	}

	public String joinF(String fmt) {
		return joinF("=", ",", fmt);
	}

	public String joinF(String cpair, String c, String fmt) {
		FString.double_format_ = fmt;
		return super.join(cpair, c);
	}

	/*	public TMapVectorDX<K>  newMapVector(){
			return new TMapVectorDX<K>(ck);
		}	*/
	public TMapDVecX<K> newMVValueKey() {
		return new TMapDVecX<K>(ck);
	}

	public TMapDVecX<K> ValueKeyToMapVec() {
		return (TMapDVecX<K>) super.ValueKeyToMapVec();
	}

	public MapXD<K> maxOn(MapXD<K> m) {
		for (Map.Entry<K, Double> e : m.entrySet()) {
			Double d = this.get(e.getKey());
			if (d != null) if (d >= e.getValue()) continue;
			this.put(e.getKey(), e.getValue());
		}

		return this;
	}

	public Double sum(Collection<K> keys) {
		Double x = 0.0;
		for (K k: keys) {
			Double v = get(k);
			if (v != null)  x += v;
		}
		return x;
	}
	
	public Double sum(K[] keys) {
		Double x = 0.0;
		for (K k: keys) {
			Double v = get(k);
			if (v != null)  x += v;
		}
		return x;
	}
	
	public Double average(Collection<K> keys) {
		return sum(keys)/keys.size();
	}
	public Double average() {
		return sum()/size();
	}
	
	public MapXD<K> multiply(MapXD<K> vec) {
		if (this.size() > vec.size()) return vec.multiply(this);
		
		MapXD result = newInstance();
		if (size()==0) return result;

		for (Map.Entry<K, Double> e : entrySet()) {
			K key = e.getKey();
			Double value1 = e.getValue();
			Double value2 = vec.get(key);
			if (value2 == null) continue;
			result.put(key, value2 * value1);
		}
		return result;
	}
	public VectorI KeyToVecSortByAbsValue(boolean descending) {
		return (VectorI) toVectorKey().sub(ValuesToVector().sortAbsId(descending));
	}
}
