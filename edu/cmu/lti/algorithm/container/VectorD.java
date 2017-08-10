/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.Interfaces.IGetDblByStr;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.util.system.FSystem;

/**
 * @author nlao
 *This class is an extension to Vector&lt;Double&gt;
 */
public class VectorD extends VectorX<Double> implements IGetDblByStr,
		IPlusObjOn, ICloneable {

	public Double getDouble(String name) {
		if (name.equals("sum")) return this.sum();
		if (name.equals("max")) return this.max();
		if (name.equals("min")) return this.min();
		return null;
	}

	public VectorD addAllClone(VectorD vd) {
		this.clear();
		this.ensureCapacity(vd.size());
		for (int i = 0; i < vd.size(); ++i)
			this.add((double) vd.get(i));
		return this;

	}

	public VectorI idxAbsLargerThan(double x) {
		VectorI v = new VectorI();
		for (int i = 0; i < size(); ++i)
			if (Math.abs(get(i)) > x) v.add(i);
		return v;
	}

	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public Double newValue() {//weakness of Java template
		return 0.0;
	}

	public VectorD newInstance() {
		return new VectorD();
	}

	public VectorD plusObjOn(Object m) {
		addAll((VectorD) m);
		return this;
	}

	public VectorD(Double[] v) {
		super(v);
	}

	public VectorD(double[] v) {
		super(Double.class);
		this.ensureCapacity(v.length);
		for (Double x : v)
			add(x);
	}

	public double[] toDoubleArray() {
		double[] v = new double[size()];
		for (int i = 0; i < size(); ++i)
			v[i] = get(i);
		return v;
	}

	public VectorI toVectorI() {
		VectorI v = new VectorI(size());
		for (int i = 0; i < size(); ++i)
			v.set(i, get(i).intValue());
		return v;
	}

	public VectorD(VectorX<Double> v) {
		super(v, Double.class);
	}

	public VectorD(int k) {
		super(k, 0.0);
	}

	public VectorD(int k, Double v) {
		super(k, v);
	}

	public VectorD() {
		super(Double.class);
	}

	public VectorD absOn() {
		for (int i = 0; i < size(); ++i)
			set(i, Math.abs(get(i)));
		return this;
	}

	public VectorD abs() {
		VectorD v = new VectorD(this);
		v.absOn();
		return v;
	}

	public VectorD exp() {
		VectorD v = new VectorD();
		v.ensureCapacity(size());
		for (int i = 0; i < size(); ++i)
			v.add(Math.exp(get(i)));
		return v;
	}

	public VectorD expOn() {
		for (int i = 0; i < size(); ++i)
			set(i, Math.exp(get(i)));
		return this;
	}

	public Double mean() {
		return sum() / size();
	}

	/**variance*/
	public double var() {
		double d = mean();
		double var0 = mod2() / size();
		return var0 - d * d;// /(size()-1);
	}

	/**standard deviation*/
	public double sd() {
		return Math.sqrt(var());
	}

	public Double sum() {
		double s = 0.0;
		for (double d : this)
			s += d;
		return s;
	}

	public double norm2() {
		return Math.sqrt(mod2());
	}

	public double mod2() {
		double m2 = 0.0;
		for (double d : this)
			m2 += d * d;
		return m2;
	}

	public VectorD powOn(double p) {
		for (int i = 0; i < size(); ++i) {
			set(i, Math.pow(get(i), p));
		}
		return this;
	}

	public VectorD sqrOn() {
		for (int i = 0; i < size(); ++i)
			if (get(i) != null) 	set(i, get(i) * get(i));
		return this;
	}

	public VectorD sqr() {
		VectorD vd = new VectorD();
		vd.addAll(this);
		return vd.sqrOn();
	}

	public VectorD sqrRootOn() {
		for (int i = 0; i < size(); ++i)
			set(i, Math.sqrt(get(i)));
		return this;
	}

	public VectorD devideOn(double x) {
		if (x == 1.0) return this;
		for (int i = 0; i < size(); ++i)
			set(i, get(i) / x);
		return this;
	}

	public VectorD multiplyOn(double x) {
		for (int i = 0; i < size(); ++i)
			set(i, get(i) * x);
		return this;
	}

	public VectorD multiplyOn(int ib, int ie, double x) {
		for (int i = ib; i < ie; ++i)
			set(i, get(i) * x);
		return this;
	}

	public double sum(int ib, int ie) {
		double d = 0;
		for (int i = ib; i < ie; ++i)
			d += get(i);
		return d;
	}

	public double sum(int[] ids) {
		double d = 0;
		for (int i : ids)	d += get(i);
		return d;
	}
	
	public VectorD normalizeOn(int ib, int ie) {
		multiplyOn(ib, ie, sum(ib, ie));
		return this;
	}

	public VectorD cumulateOn() {
		for (int i = 1; i < size(); ++i) {
			set(i, get(i) + get(i - 1));
		}
		return this;
	}

	public VectorD cumulate() {
		return ((VectorD) clone()).cumulateOn();
	}

	public VectorD normalizeOn() {
		return devideOn(sum());
	}

	public VectorD normalizeL2On() {
		return devideOn(norm2());
	}

	public VectorD normalize() {
		return ((VectorD) clone()).normalizeOn();
	}

	public VectorD normalize01() {
		VectorD v = new VectorD();
		if (size() == 0) return v;
		double min = this.min();
		double max = this.max();
		double range = max - min;
		for (double x : this) {
			if (range != 0.0) v.add((x - min) / range);
			else v.add(1.0);
		}
		return v;
	}

	public VectorD sub(ArrayList<Integer> vi) {
		return (VectorD) super.sub(vi);
	}

	public VectorD subPositive() {
		VectorD v = new VectorD();
		v.ensureCapacity(size());
		for (double d : this)
			if (d > 0.0) v.add(d);
		return v;
	}

	public VectorD subNegative() {
		VectorD v = new VectorD();
		v.ensureCapacity(size());
		for (double d : this)
			if (d < 0.0) v.add(d);
		return v;
	}

	public VectorD subNonZero() {
		VectorD v = new VectorD();
		v.ensureCapacity(size());
		for (double d : this)
			if (d != 0.0) v.add(d);
		return v;
	}

	public VectorD sub(Set<Integer> vi) {
		return (VectorD) super.sub(vi);
	}

	public double inner(MapID m) {
		double d = 0.0;
		for (Map.Entry<Integer, Double> e : m.entrySet())
			d += this.get(e.getKey()) * e.getValue();
		return d;
	}

	public VectorD multiply(ArrayList<Double> v) {
		VectorD v1 = (VectorD) this.clone();//new VectorD();
		//v1.ensureCapacity(v.size());
		return v1.multiplyOn(v);
	}

	public VectorD multiplyOn(ArrayList<Double> v) {
		FSystem.checkVectorSizes(size(), v.size());

		for (int i = 0; i < size(); ++i)
			set(i, get(i) * v.get(i));
		return this;
	}

	public VectorD multiply(Double x) {
		VectorD v1 = (VectorD) this.clone();
		return v1.multiplyOn(x);
	}

	public VectorD multiply(VectorI v) {
		FSystem.checkVectorSizes(size(), v.size());

		VectorD v1 = new VectorD();
		v1.ensureCapacity(v.size());
		for (int i = 0; i < size(); ++i)
			v1.add(get(i) * v.get(i));
		return v1;
	}

	public VectorD devide(VectorX<Double> v) {
		FSystem.checkVectorSizes(size(), v.size());

		VectorD v1 = new VectorD();
		v1.ensureCapacity(v.size());
		for (int i = 0; i < size(); ++i)
			v1.add(get(i) / v.get(i));
		return v1;
	}

	public static VectorD from(VectorX<Integer> v) {
		VectorD x = new VectorD();
		x.ensureCapacity(v.size());
		for (int i = 0; i < v.size(); ++i)
			x.add((double) v.get(i));
		return x;
	}

	public static VectorD from(String[] v) {
		VectorD x = new VectorD();
		x.load(v);
		return x;
	}
	
	public static VectorD fromLine(String str, String sep) {
		VectorD values = new VectorD();
		values.load(str, sep);
		return values;
	}

	public static VectorD fromLine(String str) {
		return fromLine(str, "\t");
	}

	public VectorD plusOn(ArrayList<Double> v) {

		FSystem.checkVectorSizes(size(), v.size());
		for (int i = 0; i < size(); ++i)
			set(i, get(i) + v.get(i));
		return this;
	}

	public VectorD minusOn(ArrayList<Double> v, double sc) {
		FSystem.checkVectorSizes(size(), v.size());

		for (int i = 0; i < size(); ++i)
			set(i, get(i) - v.get(i) * sc);
		return this;
	}

	public VectorD plus(ArrayList<Double> v, double sc) {
		VectorD vec = new VectorD();
		vec.copy(this);
		return vec.plusOn(v,sc);
	}

	public VectorD plusOn(ArrayList<Double> v, double sc) {
		FSystem.checkVectorSizes(size(), v.size());

		for (int i = 0; i < size(); ++i)
			set(i, get(i) + v.get(i) * sc);
		return this;
	}

	public VectorD plusOnSqr(ArrayList<Double> v, double sc) {
		FSystem.checkVectorSizes(size(), v.size());

		for (int i = 0; i < size(); ++i)
			set(i, get(i) + v.get(i) * v.get(i) * sc);
		return this;
	}

	public VectorD plusOn(VectorI v) {
		FSystem.checkVectorSizes(size(), v.size());

		for (int i = 0; i < size(); ++i)
			set(i, get(i) + v.get(i));
		return this;
	}

	//with automatic extension
	public VectorD plusOnE(VectorD v) {
		this.extend(Math.max(v.size(), size()), 0.0);
		for (int i = 0; i < v.size(); ++i)
			if (v.get(i) != null) set(i, get(i) + v.get(i));
		return this;
	}

	public VectorD plusOnE(double v[]) {
		this.extend(Math.max(v.length, size()), 0.0);
		for (int i = 0; i < v.length; ++i)
			set(i, get(i) + v[i]);
		return this;
	}

	public VectorD minusOn(VectorD v) {
		FSystem.checkVectorSizes(size(), v.size());

		for (int i = 0; i < size(); ++i)
			set(i, get(i) - v.get(i));
		return this;
	}

	public VectorD minus(VectorD v) {
		VectorD v1 = new VectorD(this);
		return v1.minusOn(v);
	}

	public VectorD plusOn(Map<Integer, Double> m) {
		for (Map.Entry<Integer, Double> e : m.entrySet())
			plusOn(e.getKey(), e.getValue());
		return this;
	}

	public VectorD plusOnE(Map<Integer, Double> m) {
		for (Map.Entry<Integer, Double> e : m.entrySet())
			plusOnE(e.getKey(), e.getValue());
		return this;
	}

	public VectorD minusOn(Map<Integer, Double> m) {
		for (Map.Entry<Integer, Double> e : m.entrySet())
			minusOn(e.getKey(), e.getValue());
		return this;
	}

	public VectorD plus1On(VectorX<Integer> vi) {
		plusOn(vi, 1.0);
		return this;
	}

	public VectorD minus1On(VectorX<Integer> vi) {
		minusOn(vi, 1.0);
		return this;
	}

	public VectorD minusOn(VectorX<Integer> vi, VectorX<Double> vd) {
		FSystem.checkVectorSizes(vi.size(), vd.size());

		for (int i = 0; i < vi.size(); ++i)
			minusOn(vi.get(i), vd.get(i));
		return this;
	}

	public VectorD plusOn(VectorX<Integer> vi, VectorX<Double> vd) {
		FSystem.checkVectorSizes(vi.size(), vd.size());

		for (int i = 0; i < vi.size(); ++i)
			plusOn(vi.get(i), vd.get(i));
		return this;
	}

	public VectorD plusOn(VectorX<Integer> vi, double d) {
		for (Integer k : vi)
			plusOn(k, d);
		return this;
	}

	public VectorD minusOn(VectorX<Integer> vi, double d) {
		for (Integer k : vi)
			minusOn(k, d);
		return this;
	}

	public VectorD minusOn(double d) {
		for (int i = 0; i < size(); ++i)
			set(i, get(i) - d);
		return this;
	}

	public VectorD plusOn(double d) {
		for (int i = 0; i < size(); ++i)
			set(i, get(i) + d);
		return this;
	}

	public VectorD plusOn(Integer k, double x) {//TMapXD<K>
		set(k, get(k) + x);
		//return get(k);
		return this;
	}

	public VectorD plusOnE(Integer k, double x) {//TMapXD<K>
		extend(k + 1);
		set(k, get(k) + x);
		//return get(k);
		return this;
	}

	public VectorD minusOn(Integer k, double x) {//TMapXD<K>
		set(k, get(k) - x);
		//return get(k);
		return this;
	}

	public static VectorD I(int p) {
		return new VectorD(p, 1.0);
	}

	//	public TMapIX<Double> newMapIdValue(){
	public MapID newMapIdValue() {
		return new MapID();
	}

	public VectorD negatOn() {
		for (int i = 0; i < size(); ++i)
			set(i, -get(i));
		return this;
	}

	public void addTo(double[] v, double scale) {
		FSystem.checkVectorSizes(size(), v.length);
		for (int i = 0; i < size(); ++i)
			v[i] += get(i) * scale;
		return;
	}

	public double inner(double[] v) {
		FSystem.checkVectorSizes(size(), v.length);
		double d = 0;
		for (int i = 0; i < size(); ++i)
			d += v[i] * get(i);
		return d;
	}

	public double inner(ArrayList<Double> v) {
		FSystem.checkVectorSizes(size(), v.size());
		double d = 0;
		for (int i = 0; i < size(); ++i)
			d += v.get(i) * get(i);
		return d;
	}

	public VectorI signI() {
		VectorI v = new VectorI();
		v.ensureCapacity(size());
		for (Double x : this) {
			if (x > 0.0) v.add(1);
			else if (x < 0.0) v.add(-1);
			else v.add(0);
		}
		return v;
	}

	public MapID toMapNonZero() {
		MapID m = newMapIdValue();
		for (int i = 0; i < size(); ++i)
			if (get(i) != 0.0) m.put(i, get(i));
		return m;
	}

	public String join3f() {
		return join("\t", "%.3f");
	}

	public String join1f() {
		return join("\t", "%.1f");
	}

	public String join(String sSep, String format) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			if (i > 0) sb.append(sSep);
			sb.append(String.format(format, get(i)));
		}
		return (sb.toString());
	}

	public String joinf(String sSep) {//, int n){// formated join
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			if (i > 0) sb.append(sSep);
			sb.append(String.format("%f", get(i)));
		}
		return (sb.toString());
	}

	public static VectorD seq(double min, double max, int nCell) {
		VectorD v = new VectorD();
		v.ensureCapacity(nCell + 1);
		double step = (max - min) / nCell;
		for (int i = 0; i <= nCell; ++i)
			v.add(min + i * step);
		return v;
	}

	public void sortedInsertDesc(double d) {
		this.add(d);
		for (int i = size() - 1; i > 0; --i)
			if (get(i) > get(i - 1)) swap(i, i - 1);
	}

	public void sortedInsertDesc(double d, int maxLen) {
		if (size() < maxLen) {
			sortedInsertDesc(d);
			return;
		}
		if (d <= this.lastElement()) return;
		sortedInsertDesc(d);
		this.trim(1);
	}

	public VectorD findKLargest(int k) {
		VectorD v = new VectorD();
		for (double d : this)
			v.sortedInsertDesc(d, k);
		return v;
	}

	public double findTheKthLargest(int k) {
		return findKLargest(k).lastElement();
	}

	public VectorD addAll(ArrayList<Double> v, double scale) {
		this.ensureCapacity(size() + v.size());
		for (double x : v)
			add(x * scale);
		return this;
	}

	public VectorD addAll(double[] v) {
		this.ensureCapacity(size() + v.length);
		for (double x : v)
			add(x);
		return this;
	}

	public int countNonZero() {
		int n = 0;
		for (double d : this)
			if (d != 0.0) ++n;
		return n;
	}

	public VectorD setAll(double[] v) {
		this.setSize(v.length);
		for (int i = 0; i < v.length; ++i)
			this.set(i, v[i]);
		return this;
	}

	public Double parseLine(String line) {
		return Double.parseDouble(line);
	}

	public double norm1() {
		double s = 0;
		for (Double d : this)
			s += Math.abs(d);
		return s;
	}

	public VectorD inverseOn() {
		for (int i = 0; i < size(); ++i)
			set(i, 1.0 / get(i));
		return this;
	}

	public static VectorD fromFile(String fn) {
		VectorD vd = new VectorD();
		vd.loadFile(fn);
		return vd;
	}

	public static VectorD fromFile(String fn, int iCol) {
		return fromFile(fn, iCol, "\t", false);
	}

	public static VectorD fromFile(String fn, int iCol, String sep,
			boolean bSkipTitle) {
		VectorD vd = new VectorD();
		vd.loadFile(fn, iCol, sep, bSkipTitle);
		return vd;
	}

	public float[] toFloatArray() {
		float v[] = new float[size()];
		for (int i = 0; i < size(); ++i)
			v[i] = new Float(get(i));
		return v;
	}
	
	public TMapXVecI<Double> toMapVectorAbsValueId() {
		TMapXVecI<Double> m = newTMapVectorXI();
		for (int i = 0; i < size(); ++i)
			m.insert(Math.abs(get(i)), i);
		return m;
	}
	
	public VectorI sortAbsId(boolean desc) {
		VectorI vi = (VectorI) toMapVectorAbsValueId().toVectorV();
		if (desc) vi.reverseOn();
		return vi;
	}
}
